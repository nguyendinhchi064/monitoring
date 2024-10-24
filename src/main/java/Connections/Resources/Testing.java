package Connections.Resources;

import Connections.Model.ConnectionCredentials;
import Connections.Model.JobInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.quartz.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Path("/db_transfer")
@RolesAllowed("User")
public class Testing {

    @Inject
    JsonWebToken jwt;

    @Inject
    Scheduler quartzScheduler;

    @Inject
    JobInfoRepository jobInfoRepository;

    @Inject
    ObjectMapper objectMapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferData(ConnectionCredentials userInput) {
        String username = jwt.getClaim("username");
        String userId = jwt.getClaim("sub");
        String jobId = UUID.randomUUID().toString();

        try {
            System.out.println("MySQL User: " + userInput.getMysqlUser());

            String mysqlUrl = String.format("jdbc:mysql://%s:%s/%s", userInput.getMysqlHost(), userInput.getMysqlPort(), userInput.getMysqlDb());

            JobInfo jobInfo = new JobInfo(jobId, "IN_PROGRESS", mysqlUrl, userInput.getMysqlDb(), userInput.getTableName(), userInput.getMongoCollection(), username, userId);
            jobInfoRepository.saveJob(jobInfo);

            // Serialize the ConnectionCredentials object to JSON
            String userInputJson = objectMapper.writeValueAsString(userInput);
            System.out.println("Serialized ConnectionCredentials: " + userInputJson);

            // Build the Quartz job
            JobDetail jobDetail = JobBuilder.newJob(DataTransferJob.class)
                    .withIdentity(jobId)
                    .usingJobData("userInput", userInputJson)
                    .usingJobData("username", username)
                    .usingJobData("userId", userId)
                    .build();

            // Trigger the job to start immediately
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobId)
                    .startNow()
                    .build();

            // Schedule the job
            quartzScheduler.scheduleJob(jobDetail, trigger);

            // Return the JobID to the client
            return Response.ok("{\"JobID\":\"" + jobId + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (SchedulerException | JsonProcessingException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error scheduling the data transfer job: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobStatuses() {
        String username = jwt.getClaim("username");

        List<JobInfo> jobs = jobInfoRepository.findJobsByUsername(username);

        // Return the list of jobs or an empty list if none are found
        if (jobs == null || jobs.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No jobs found for user: " + username)
                    .build();
        }

        return Response.ok(jobs)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/jobStatusCounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobStatusCounts() {
        String username = jwt.getClaim("username");

        // Retrieve jobs for the user from the repository
        List<JobInfo> jobs = jobInfoRepository.findJobsByUsername(username);

        // Count the jobs based on their status
        long completedCount = jobs.stream().filter(job -> "COMPLETED".equals(job.getStatus())).count();
        long failedCount = jobs.stream().filter(job -> "FAILED".equals(job.getStatus())).count();

        return Response.ok(new JobStatusCounts(completedCount, failedCount))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/resume")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeDataTransfer(@QueryParam("jobId") String jobId) {
        try {
            System.out.println("Received request to resume job with ID: " + jobId);
            JobInfo jobInfo = jobInfoRepository.findJobById(jobId);
            if (jobInfo == null) {
                System.out.println("Job with ID " + jobId + " not found.");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Job with ID " + jobId + " not found.")
                        .build();
            }

            System.out.println("Job found: " + jobInfo);
            if (!"IN_PROGRESS".equals(jobInfo.getStatus())) {
                System.out.println("Job with ID " + jobId + " is not in progress and cannot be resumed. Current status: " + jobInfo.getStatus());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Job with ID " + jobId + " is not in progress and cannot be resumed.")
                        .build();
            }

            // Recreate ConnectionCredentials from job information
            System.out.println("Recreating ConnectionCredentials for job with ID: " + jobId);
            ConnectionCredentials userInput = new ConnectionCredentials();
            userInput.setMysqlHost(jobInfo.getMysqlUrl());
            userInput.setMongoCollection(jobInfo.getMongoCollection());

            // Serialize the ConnectionCredentials object to JSON
            String userInputJson = objectMapper.writeValueAsString(userInput);
            System.out.println("Serialized ConnectionCredentials: " + userInputJson);

            // Build the Quartz job
            System.out.println("Building Quartz job for job ID: " + jobId);
            JobDetail jobDetail = JobBuilder.newJob(DataTransferJob.class)
                    .withIdentity(jobId)
                    .usingJobData("userInput", userInputJson)
                    .usingJobData("username", jobInfo.getUsername())
                    .build();

            // Trigger the job to start immediately
            System.out.println("Creating trigger for job ID: " + jobId);
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobId)
                    .startNow()
                    .build();

            // Schedule the job
            System.out.println("Scheduling job with ID: " + jobId);
            quartzScheduler.scheduleJob(jobDetail, trigger);

            System.out.println("Job with ID " + jobId + " resumed successfully.");
            // Update job info description
            jobInfo.setDescription("Resume success");
            jobInfoRepository.updateJob(jobInfo);
            return Response.ok("Job resumed successfully.")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (SchedulerException | JsonProcessingException e) {
            System.err.println("Error resuming the data transfer job: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error resuming the data transfer job: " + e.getMessage())
                    .build();
        }
    }

    public static class JobStatusCounts {
        public long completed;
        public long failed;

        public JobStatusCounts(long completed, long failed) {
            this.completed = completed;
            this.failed = failed;
        }
    }
}

//{
//  "mysqlHost": "localhost",
//  "mysqlPort": "3306",
//  "mysqlDb": "UserInfo",
//  "mysqlUser": "user",
//  "mysqlPassword": "user",
//  "tableName": "user_db",
//  "mongoConnectionString": "mongodb://root:root@localhost:27018/?authSource=admin",
//  "mongoDatabase": "User",
//  "mongoCollection": "Testing5"
//}
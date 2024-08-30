package Connections.Resources;

import Connections.Model.ConnectionCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.quartz.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/db_transfer")
@RolesAllowed("User")
public class Testing {

    @Inject
    JsonWebToken jwt;

    @Inject
    JobStatusListener jobStatusListener;

    @Inject
    Scheduler quartzScheduler;

    @Inject
    ObjectMapper objectMapper;  // Inject Jackson ObjectMapper

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferData(ConnectionCredentials userInput) throws SchedulerException, JsonProcessingException {
        String username = jwt.getClaim("username");
        String userId = jwt.getClaim("sub");
        String jobId = UUID.randomUUID().toString();

        System.out.println("MySQL User: " + userInput.getMysqlUser());

        String mysqlUrl = String.format("jdbc:mysql://%s:%s/%s", userInput.getMysqlHost(), userInput.getMysqlPort(), userInput.getMysqlDb());
        jobStatusListener.jobToBeExecuted(jobId, username, userId, mysqlUrl, userInput.getMysqlDb(), userInput.getTableName(), userInput.getMongoCollection());

        // Serialize the ConnectionCredentials object to JSON
        String userInputJson = objectMapper.writeValueAsString(userInput);
        System.out.println("Serialized ConnectionCredentials: " + userInputJson);

        JobDetail jobDetail = JobBuilder.newJob(DataTransferJob.class)
                .withIdentity(jobId)
                .usingJobData("userInput", userInputJson)
                .usingJobData("username", username)
                .usingJobData("userId", userId)
                .build();

        // Trigger the job to start now
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobId)
                .startNow()
                .build();

        // Schedule the job
        quartzScheduler.scheduleJob(jobDetail, trigger);

        return Response.ok("{\"JobID\":\"" + jobId + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JobStatusListener.JobInfo> getJobStatuses() {
        String username = jwt.getClaim("username");
        List<JobStatusListener.JobInfo> jobs = jobStatusListener.getJobsForUser(username);
        if (jobs == null) {
            jobs = new ArrayList<>(); // Return an empty list if no jobs are found
        }
        return jobs; // Directly return the list
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
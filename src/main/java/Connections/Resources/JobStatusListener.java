package Connections.Resources;

import Connections.Model.JobInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class JobStatusListener {

    @Inject
    JobInfoRepository jobInfoRepository;

    // This method is called when the job is about to start ( but failed ?)
//    @Transactional
//    public void jobToBeExecuted(String jobId, String username, String userId, String mysqlUrl, String mysqlDatabase, String mysqlTable, String mongoCollection) {
//        // Create and persist new JobInfo into the database
//        JobInfo jobInfo = new JobInfo(jobId, "IN_PROGRESS", mysqlUrl, mysqlDatabase, mysqlTable, mongoCollection, username, userId);
//        jobInfoRepository.saveJob(jobInfo);
//        System.out.println("Job " + jobInfo.getJobId() + " started for user " + username);
//    }

    @Transactional
    public void updateJobStatus(String jobId, String status, String description) {
        JobInfo job = jobInfoRepository.findJobById(jobId);  // Fetch the job by ID from the database
        if (job != null) {
            job.setStatus(status);
            if ("FAILED".equals(status)) {
                job.setDescription(description); // Set failure reason in the description
            }
            jobInfoRepository.updateJob(job);
            System.out.println("Job " + jobId + " updated to " + status);
        } else {
            System.out.println("Job with ID " + jobId + " not found.");
        }
    }

    public void jobWasExecuted(String jobId, String username, Exception jobException) {
        String status = (jobException == null) ? "COMPLETED" : "FAILED";
        String description = (jobException != null) ? jobException.getMessage() : null;
        updateJobStatus(jobId, status, description);
        System.out.println("Job " + jobId + " completed for user " + username + " with status " + status);
    }
}

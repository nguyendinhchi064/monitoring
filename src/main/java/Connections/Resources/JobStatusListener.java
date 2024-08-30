package Connections.Resources;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class JobStatusListener {

    private final ConcurrentHashMap<String, List<JobInfo>> userJobsMap = new ConcurrentHashMap<>();

    public void jobToBeExecuted(String jobId, String username, String userId, String mysqlUrl, String mysqlDatabase, String mysqlTable, String mongoCollection) {
        JobInfo jobInfo = new JobInfo(jobId, "IN_PROGRESS", mysqlUrl, mysqlDatabase, mysqlTable, mongoCollection, username, userId);
        userJobsMap.computeIfAbsent(username, k -> new ArrayList<>()).add(jobInfo);
        System.out.println("Job " + jobId + " started for user " + username);
    }

    public void updateJobStatus(String jobId, String username, String status) {
        List<JobInfo> jobs = userJobsMap.get(username);
        if (jobs != null) {
            for (JobInfo job : jobs) {
                if (job.getJobId().equals(jobId)) {
                    job.setStatus(status);
                    System.out.println("Job " + jobId + " for user " + username + " updated to " + status + ".");
                }
            }
        }
    }

    public void jobWasExecuted(String jobId, String username, Exception jobException) {
        updateJobStatus(jobId, username, jobException == null ? "COMPLETED" : "FAILED");
    }

    public List<JobInfo> getJobsForUser(String username) {
        return userJobsMap.getOrDefault(username, new ArrayList<>());
    }

    public static class JobInfo {
        private String jobId;
        private String status;
        private String mysqlUrl;
        private String mysqlDatabase;
        private String mysqlTable;
        private String mongoCollection;
        private String username;
        private String userId;

        public JobInfo(String jobId, String status, String mysqlUrl, String mysqlDatabase, String mysqlTable, String mongoCollection, String username, String userId) {
            this.jobId = jobId;
            this.status = status;
            this.mysqlUrl = mysqlUrl;
            this.mysqlDatabase = mysqlDatabase;
            this.mysqlTable = mysqlTable;
            this.mongoCollection = mongoCollection;
            this.username = username;
            this.userId = userId;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMysqlUrl() {
            return mysqlUrl;
        }

        public void setMysqlUrl(String mysqlUrl) {
            this.mysqlUrl = mysqlUrl;
        }

        public String getMysqlDatabase() {
            return mysqlDatabase;
        }

        public void setMysqlDatabase(String mysqlDatabase) {
            this.mysqlDatabase = mysqlDatabase;
        }

        public String getMysqlTable() {
            return mysqlTable;
        }

        public void setMysqlTable(String mysqlTable) {
            this.mysqlTable = mysqlTable;
        }

        public String getMongoCollection() {
            return mongoCollection;
        }

        public void setMongoCollection(String mongoCollection) {
            this.mongoCollection = mongoCollection;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}

package Connections.Model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "job_info")
public class JobInfo {

    @Id
    @Column(name = "jobId", nullable = false)
    private String jobId;

    private String status;
    private String mysqlUrl;
    private String mysqlDatabase;
    private String mysqlTable;
    private String mongoCollection;
    private String username;
    private String userId;
    private String description;

    // Getters and Setters
    public JobInfo() {
        this.jobId = UUID.randomUUID().toString(); // Generate UUID if not set
    }

    // Constructor to set all fields including jobId
    public JobInfo(String jobId, String status, String mysqlUrl, String mysqlDatabase, String mysqlTable, String mongoCollection, String username, String userId) {
        this.jobId = jobId != null ? jobId : UUID.randomUUID().toString(); // Generate UUID if jobId is not provided
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

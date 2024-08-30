package Connections.Resources;

import Connections.Model.ConnectionCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataTransferJob implements Job {

    @Inject
    JobStatusListener jobStatusListener;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobId = context.getJobDetail().getKey().getName();
        String userInputJson = context.getMergedJobDataMap().getString("userInput");
        String username = context.getMergedJobDataMap().getString("username");
        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        try {
            // Deserialize the JSON string back into a ConnectionCredentials object
            ConnectionCredentials userInput = objectMapper.readValue(userInputJson, ConnectionCredentials.class);
            System.out.println("Deserialized MySQL User: " + userInput.getMysqlUser());

            performDataTransfer(userInput, username, jobId);

            // Update job status to COMPLETED
            jobStatusListener.jobWasExecuted(jobId, username, null);
        } catch (Exception e) {
            // Update job status to FAILED in case of an exception
            jobStatusListener.jobWasExecuted(jobId, username, e);
            throw new JobExecutionException("Failed to process data transfer job", e);
        }
    }
    private void performDataTransfer(ConnectionCredentials userInput, String username, String jobId) throws Exception {
        // Include the MySQL user and password in the connection string
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s",
                userInput.getMysqlHost(),
                userInput.getMysqlPort(),
                userInput.getMysqlDb(),
                userInput.getMysqlUser(),
                userInput.getMysqlPassword());

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + userInput.getTableName());

            List<Document> documents = new ArrayList<>();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            try (var mongoClient = MongoClients.create(userInput.getMongoConnectionString())) {
                MongoDatabase mongoDatabase = mongoClient.getDatabase(username);
                MongoCollection<Document> collection = mongoDatabase.getCollection(userInput.getMongoCollection());

                while (rs.next()) {
                    Document document = new Document();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rsMetaData.getColumnName(i);
                        Object columnValue = rs.getObject(i);
                        document.append(columnName, columnValue);
                    }
                    documents.add(document);
                    collection.insertOne(document);
                }
            }
        }
    }
}

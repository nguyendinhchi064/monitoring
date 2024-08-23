package Connections.Resources;

import Connections.Model.ConnectionCredentials;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.bson.Document;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("/db_transfer")
@RolesAllowed("User")
public class Testing {

    @Context
    SecurityContext securityContext;

    @Inject
    JsonWebToken jwt;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferData(ConnectionCredentials userInput) {
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", userInput.getMysqlHost(), userInput.getMysqlPort(), userInput.getMysqlDb());

        // Extract the username from the JWT token
        String username = jwt.getClaim("username");

        try (Connection connection = DriverManager.getConnection(jdbcUrl, userInput.getMysqlUser(), userInput.getMysqlPassword())) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + userInput.getTableName());

            List<Document> documents = new ArrayList<>();

            // Get metadata to dynamically retrieve column names
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            try (var mongoClient = MongoClients.create(userInput.getMongoConnectionString())) {
                MongoDatabase mongoDatabase = mongoClient.getDatabase(username); // Use username as the database name
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

            return Response.ok(documents).build(); // Returning the list of documents
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred: " + e.getMessage())
                    .build();
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

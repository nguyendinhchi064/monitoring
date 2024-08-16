package Connections.Resources;

import Connections.Model.ConnectionCredentials;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;

import java.sql.*;

/*todo: Now we connect success, can't we just set up for user just credentials for mysql. About the mongo, user just have to select the collection */
@Path("/db_transfer")
public class Testing {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferData(ConnectionCredentials userInput) {
        //User use the ConnectionCredentials to provide parameters to create a JDBC URL like jdbc:mariadb://localhost(mysqlhost):3306(Port)/UserInfo(DatabaseName)
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", userInput.getMysqlHost(), userInput.getMysqlPort(), userInput.getMysqlDb());

        //Try connection here and success
        try (Connection connection = DriverManager.getConnection(jdbcUrl, userInput.getMysqlUser(), userInput.getMysqlPassword())) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + userInput.getTableName());
            //Connecting to mongo and select the collection
            try (var mongoClient = MongoClients.create(userInput.getMongoConnectionString())) {
                MongoDatabase mongoDatabase = mongoClient.getDatabase(userInput.getMongoDatabase());
                MongoCollection<Document> collection = mongoDatabase.getCollection(userInput.getMongoCollection());

                while (rs.next()) {
                    Document document = new Document();

                    // Replace with actual column names from your MySQL table
                    //This id have a problem now
                    document.append("id", rs.getInt("id"));
                    document.append("name", rs.getString("name"));
                    document.append("value", rs.getString("value"));

                    collection.insertOne(document);
                }
            }
            return Response.ok("Data transferred successfully!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error occurred: " + e.getMessage())
                    .build();
        }
    }
}

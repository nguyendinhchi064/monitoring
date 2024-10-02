package MongoTable.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Path;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/log")
@RolesAllowed("User")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogResource {

    @Inject
    MongoDBService mongoDBService;

    @Inject
    JsonWebToken jwt;

    private String getDatabaseName() {
        // Extract the username from the JWT claim
        return jwt.getClaim("username");
    }

    @POST
    @Path("/create")
    public Response createCollection(Map<String, String> jsonPayload) {
        String collectionName = jsonPayload.get("collectionName");
        mongoDBService.createCollection(getDatabaseName(), collectionName);
        return Response.ok("Collection created successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


    @GET
    @Path("/collections")
    public Response listCollections() {
        List<String> collections = mongoDBService.listCollections(getDatabaseName());
        return Response.ok(collections)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/{collectionName}/data")
    public Response saveData(@PathParam("collectionName") String collectionName,
                             List<Document> data) {
        data.forEach(doc -> mongoDBService.saveData(getDatabaseName(), collectionName, doc));
        return Response.ok("Data saved successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/{collectionName}/data")
    public Response getData(@PathParam("collectionName") String collectionName) {
        List<Document> data = mongoDBService.getData(getDatabaseName(), collectionName);
        return Response.ok(data)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/collectionLogs")
    public Response getCollectionLogs() {
        List<Document> logs = mongoDBService.getData(getDatabaseName(), "collectionLogs");
        return Response.ok(logs)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/collectionStats")
    public Response getCollectionDocumentCounts() {
        List<Document> collectionStats = mongoDBService.getCollectionDocumentCounts(getDatabaseName());
        return Response.ok(collectionStats).type(MediaType.APPLICATION_JSON).build();
    }


    // Fetch Latest Data from `collectionLogs`
    @GET
    @Path("/collectionLogs/latest")
    public Response getLatestCollectionLogs() {
        Document latestLog = mongoDBService.getLatestDocument(getDatabaseName(), "collectionLogs");
        if (latestLog == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No logs found").build();
        }
        return Response.ok(latestLog)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Fetch Latest Date and Timestamp in User Specified Collection
    @GET
    @Path("/{collectionName}/latest-timestamp")
    public Response getLatestDateAndTimestampInCollection(@PathParam("collectionName") String collectionName) {
        Document latestDocument = mongoDBService.getLatestDocument(getDatabaseName(), collectionName);
        if (latestDocument == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data found in the collection").build();
        }

        // Extract date and timestamp fields if they exist
        Object latestDate = latestDocument.get("date");
        Object latestTimestamp = latestDocument.get("timestamp");

        // If both date and timestamp are null, extract timestamp from _id
        if (latestDate == null && latestTimestamp == null && latestDocument.containsKey("_id")) {
            ObjectId id = latestDocument.getObjectId("_id");  // Extract _id as ObjectId
            latestTimestamp = id.getDate();  // Get the timestamp from the ObjectId
        }

        Document response = new Document()
                .append("latestDate", latestDate)
                .append("latestTimestamp", latestTimestamp);

        return Response.ok(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }



    @GET
    @Path("/user/latest")
    public Response getLatestUserDatabaseEntry() {
        List<String> collections = mongoDBService.listCollections(getDatabaseName());
        Document latestEntry = null;
        String latestCollection = null;

        for (String collection : collections) {
            Document latestDocInCollection = mongoDBService.getLatestDocument(getDatabaseName(), collection);

            if (latestDocInCollection != null) {
                // Get date and timestamp fields
                Date latestDate = latestDocInCollection.getDate("date");
                if (latestDate == null) {
                    // If date is null, try using the timestamp field
                    Object timestamp = latestDocInCollection.get("timestamp");
                    if (timestamp != null && timestamp instanceof Date) {
                        latestDate = (Date) timestamp;
                    }
                }

                if (latestDate == null && latestDocInCollection.containsKey("_id")) {
                    // If both date and timestamp are null, extract date from _id
                    latestDate = ((ObjectId) latestDocInCollection.get("_id")).getDate();
                }

                if (latestEntry == null || (latestDate != null && latestEntry.getDate("date") != null &&
                        latestDate.after(latestEntry.getDate("date")))) {
                    latestEntry = latestDocInCollection;
                    latestCollection = collection;
                }
            }
        }

        if (latestEntry == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data found in user's database").build();
        }

        return Response.ok(latestEntry.append("collection", latestCollection))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }




//    @DELETE
//    @Path("/{collectionName}/data")
//    public Response deleteData(@PathParam("collectionName") String collectionName,
//                               List<Document> queries) {
//        queries.forEach(query -> mongoDBService.deleteData(getDatabaseName(), collectionName, query));
//        return Response.ok("Data deleted successfully")
//                .type(MediaType.APPLICATION_JSON)
//                .build();
//    }
}

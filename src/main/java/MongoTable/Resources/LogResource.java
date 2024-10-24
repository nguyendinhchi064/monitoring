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
        return Response.ok(new Document("createdAt", latestLog.get("createdAt")))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/{collectionName}/latest")
    public Response getEarliestCreatedAtInCollection(@PathParam("collectionName") String collectionName) {
        // Get the earliest document in the specified collection
        Document earliestDocInCollection = mongoDBService.getLatestDocumentFromId(getDatabaseName(), collectionName);

        if (earliestDocInCollection == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data found in the collection").build();
        }

        // Extract the timestamp from the `_id` field
        ObjectId objectId = earliestDocInCollection.getObjectId("_id");
        if (objectId == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No '_id' field found in the earliest document").build();
        }

        // Get the date from the ObjectId
        Date createdAt = objectId.getDate();

        // Return only the `createdAt` field
        return Response.ok(new Document("createdAt", createdAt))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    @GET
    @Path("/user/latest")
    public Response getLatestUserDatabaseEntry() {
        List<String> collections = mongoDBService.listCollections(getDatabaseName());
        Date latestDate = null;

        for (String collection : collections) {
            // Get the latest document in the current collection
            Document latestDocInCollection = mongoDBService.getLatestDocumentFromId(getDatabaseName(), collection);

            if (latestDocInCollection != null) {
                // Extract the timestamp from the `_id` field
                ObjectId objectId = latestDocInCollection.getObjectId("_id");
                if (objectId != null) {
                    Date createdAt = objectId.getDate();

                    // Update the latestDate if this createdAt is more recent
                    if (latestDate == null || createdAt.after(latestDate)) {
                        latestDate = createdAt;
                    }
                }
            }
        }

        if (latestDate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No data found in user's database").build();
        }

        // Return only the latest timestamp across all collections
        return Response.ok(new Document("latestTime", latestDate))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


}

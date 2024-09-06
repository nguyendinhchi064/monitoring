package MongoTable.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Path;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.eclipse.microprofile.jwt.JsonWebToken;

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


//    @PUT
//    @Path("/{collectionName}/data")
//    public Response updateData(@PathParam("collectionName") String collectionName,
//                               List<Document> updates) {
//        for (Document updateData : updates) {
//            Document query = new Document("_id", updateData.get("_id"));
//            Document update = new Document();
//            updateData.keySet().forEach(key -> {
//                if (!"_id".equals(key)) {
//                    update.append(key, updateData.get(key));
//                }
//            });
//            mongoDBService.updateData(getDatabaseName(), collectionName, query, update);
//        }
//        return Response.ok("Data updated successfully")
//                .type(MediaType.APPLICATION_JSON)
//                .build();
//    }
//
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

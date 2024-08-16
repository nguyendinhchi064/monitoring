package MongoTable.Resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Path;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;

import java.util.List;

@Path("/log")
@RolesAllowed("User")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogResource {

    @Inject
    MongoDBService mongoDBService;

    @POST
    @Path("/{collectionName}")
    public Response createCollection(@PathParam("collectionName") String collectionName) {
        mongoDBService.createCollection(collectionName);
        return Response.ok("Collection created successfully").build();
    }
    @GET
    @Path("/collections")
    public Response listCollections() {
        List<String> collections = mongoDBService.listCollections();
        return Response.ok(collections)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/{collectionName}/data")
    public Response saveData(@PathParam("collectionName") String collectionName,
                             List<Document> data) {
        data.forEach(doc -> mongoDBService.saveData(collectionName, doc));
        return Response.ok("Data saved successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/{collectionName}/data")
    public Response getData(@PathParam("collectionName") String collectionName) {
        List<Document> data = mongoDBService.getData(collectionName);
        return Response.ok(data)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/{collectionName}/data")
    public Response updateData(@PathParam("collectionName") String collectionName,
                               List<Document> updates) {
        for (Document updateData : updates) {
            Document query = new Document("_id", updateData.get("_id"));
            Document update = new Document();
            updateData.keySet().forEach(key -> {
                if (!"_id".equals(key)) {
                    update.append(key, updateData.get(key));
                }
            });
            mongoDBService.updateData(collectionName, query, update);
        }
        return Response.ok("Data updated successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("/{collectionName}/data")
    public Response deleteData(@PathParam("collectionName") String collectionName,
                               List<Document> queries) {
        queries.forEach(query -> mongoDBService.deleteData(collectionName, query));
        return Response.ok("Data deleted successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

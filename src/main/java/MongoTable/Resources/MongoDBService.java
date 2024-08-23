package MongoTable.Resources;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MongoDBService {

    @Inject
    MongoClient mongoClient;

    public List<String> listCollections(String dbName) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        List<String> collections = new ArrayList<>();
        database.listCollectionNames().forEach(collections::add);
        return collections;
    }

    public void createCollection(String dbName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        database.createCollection(collectionName);
    }

    public void dropCollection(String dbName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        database.getCollection(collectionName).drop();
    }

    public void saveData(String dbName, String collectionName, Document data) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        collection.insertOne(data);
    }

    public List<Document> getData(String dbName, String collectionName) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        List<Document> documents = collection.find().into(new ArrayList<>());

        // Remove The Id
        for (Document doc : documents) {
            doc.remove("_id");
        }

        return documents;
    }

    public void updateData(String dbName, String collectionName, Document query, Document update) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        collection.updateOne(query, new Document("$set", update));
    }

    public void deleteData(String dbName, String collectionName, Document query) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        collection.deleteOne(query);
    }

    private MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        return database.getCollection(collectionName);
    }
}

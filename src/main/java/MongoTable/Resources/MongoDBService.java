package MongoTable.Resources;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
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

        // Log the collection creation time
        MongoCollection<Document> logCollection = database.getCollection("collectionLogs");
        Document logEntry = new Document("collectionName", collectionName)
                .append("createdAt", new Date());
        logCollection.insertOne(logEntry);
    }


    public void saveData(String dbName, String collectionName, Document data) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        collection.insertOne(data);
    }

    public List<Document> getData(String dbName, String collectionName) {
        MongoCollection<Document> collection = getCollection(dbName, collectionName);
        List<Document> documents = collection.find().into(new ArrayList<>());


        return documents;
    }


    private MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        return database.getCollection(collectionName);
    }
    public Document getLatestDocument(String databaseName, String collectionName) {
        MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
        return collection.find().sort(Sorts.descending("createdAt")).first(); // Sort by "createdAt" field to get the latest document
    }
    public Document getLatestDocumentFromId(String databaseName, String collectionName) {
        MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
        return collection.find().sort(Sorts.descending("_id")).first();  // Sort by "_id" to get the earliest document
    }
    public List<Document> getCollectionDocumentCounts(String dbName) {
        MongoDatabase database = mongoClient.getDatabase(dbName);
        List<Document> collectionStats = new ArrayList<>();

        for (String collectionName : database.listCollectionNames()) {
            if (!"collectionLogs".equals(collectionName)) {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                long count = collection.countDocuments();

                Document collectionStat = new Document("collectionName", collectionName)
                        .append("documentCount", count);
                collectionStats.add(collectionStat);
            }
        }
        return collectionStats;
    }
}

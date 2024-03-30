import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoService {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    public MongoService() {
        mongoClient = MongoClients.create("mongodb://pedago.univ-avignon.fr:27017");
        database = mongoClient.getDatabase("cal_aw");
        userCollection = database.getCollection("user");
    }
    public Document connect(String username, String password) {
        Document query = new Document("id", username).append("mdp", password);
        Document userDocument = userCollection.find(query).first();
        closeMongoClient();
        return userDocument;
    }

    public void closeMongoClient() {
        mongoClient.close();
    }
}

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.LoggerFactory;

public class MongoService {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    public MongoService() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);

        mongoClient = MongoClients.create("mongodb://pedago.univ-avignon.fr:27017");
        database = mongoClient.getDatabase("cal_aw");
        userCollection = database.getCollection("user");
    }

    public Document getUser(String username, String password) {
        Document query = new Document("id", username).append("mdp", password);
        Document userDocument = userCollection.find(query).first();
        return userDocument;
    }

    public void updateMode(String username, String mode) {
        Document query = new Document("id", username);
        Document update = new Document("$set", new Document("modeFavori", mode));
        userCollection.updateOne(query, update);
    }

    public void addReservation(String username, Event event) {
        Document query = new Document("id", username);
        Document reservation = new Document();
        reservation.put("startDate", event.getStartDate());
        reservation.put("endDate", event.getEndDate());
        reservation.put("location", event.getLocation());
        Document update = new Document("$push", new Document("reservations", reservation));
        userCollection.updateOne(query, update);
    }
    public void addPersonalEvent(String username, Event event, String color) {
        Document query = new Document("id", username);
        Document newEvent = new Document();
        newEvent.put("startDate", event.getStartDate());
        newEvent.put("endDate", event.getEndDate());
        newEvent.put("location", event.getLocation());
        newEvent.put("subject", event.getSubject());
        newEvent.put("type", event.getType());
        newEvent.put("color", color);
        Document update = new Document("$push", new Document("eventPerso", newEvent));
        userCollection.updateOne(query, update);
    }
    
    public void closeMongoClient() {
        mongoClient.close();
    }

}
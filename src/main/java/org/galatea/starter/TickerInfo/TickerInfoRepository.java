package org.galatea.starter.TickerInfo;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.lang.Object;
import org.bson.Document;


public class TickerInfoRepository {

    public static void main(String [] args){
        System.out.println("https://spring.io/guides/gs/accessing-data-mongodb/");

        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://ReneBorr:<GalaPassword>@tickerinfo-glh7c.mongodb.net/test?retryWrites=true&w=majority");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("AlphaVantage");



        MongoCollection col = database.getCollection("Tickers");
        Document doc = new Document();
        doc.append("Name", "Rene");
        col.insertOne(doc);






    }
}

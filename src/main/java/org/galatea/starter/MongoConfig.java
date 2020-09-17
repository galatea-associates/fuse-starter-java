package org.galatea.starter;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Collection;
import java.util.Collections;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.galatea.starter.domain.rpsy")
public class MongoConfig extends AbstractMongoClientConfiguration {

  @Override
  protected String getDatabaseName() {
    return "stock_price_data";
  }

  @Override
  public MongoClient mongoClient() {
    boolean useAtlas = true; //placeholder
    MongoClientFact
    String uri;
    if (useAtlas) {
      String password = "merrylittle";
      uri = "mongodb+srv://Lord:" + password
          + "@fusecluster.kvz3u.gcp.mongodb.net/stock_price_data?w=majority";
    } else {
      uri = "mongodb://localhost:27017/stock_price_data";
    }

    ConnectionString connectionString = new ConnectionString(uri);
    MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .retryWrites(true)
        .build();
    return MongoClients.create(mongoClientSettings);
  }

  @Override
  public Collection getMappingBasePackages() {
    return Collections.singleton("org.galatea.starter");
  }
}
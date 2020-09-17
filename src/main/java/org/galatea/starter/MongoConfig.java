package org.galatea.starter;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.galatea.starter.domain.repository.StockRepository;
import org.galatea.starter.domain.repository.StockRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.galatea.starter.domain.repository")
public class MongoConfig  extends AbstractMongoClientConfiguration {
  @Value("${spring.data.mongodb.uri}")
  String mongoUri;

  @Override
  public MongoClient mongoClient() {
    ConnectionString connectionString = new ConnectionString(
        String.format(mongoUri, MyProps.mongoPass));
    MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .build();
    return MongoClients.create(mongoClientSettings);
  }

  @Override
  protected String getDatabaseName() {
    return "FuseCluster";
  }

  @Bean
  public StockRepository stockRepository() {
    return new StockRepositoryImpl();
  }
}
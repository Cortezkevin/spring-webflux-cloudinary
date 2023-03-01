package com.kevin.cloudinary.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories("com.kevin.cloudinary.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "cloudinary";
    }

    @Bean
    public MongoClient mongoClient(){

        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/cloudinary");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString( connectionString )
                .build();

        return MongoClients.create( mongoClientSettings  );
    }
}

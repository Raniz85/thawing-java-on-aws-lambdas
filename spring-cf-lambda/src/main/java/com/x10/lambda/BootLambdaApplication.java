package com.x10.lambda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootApplication
public class BootLambdaApplication {

    @Bean
    public DynamoDbClient dynamo() {
        return DynamoDbClient.create();
    }

    public static void main(String[] args) {
        SpringApplication.run(BootLambdaApplication.class, args);
    }
}

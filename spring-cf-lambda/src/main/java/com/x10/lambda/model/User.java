package com.x10.lambda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class User {

    @JsonIgnore
    private final DynamoDbClient dynamo;

    @JsonIgnore
    private final String tableName;

    private final UUID id;

    private final String name;

    public User(DynamoDbClient dynamo, String tableName, String name) {
        this.dynamo = dynamo;
        this.tableName = tableName;
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public void save() {
        dynamo.putItem(builder -> builder.item(toAttributevalueMap())
                .tableName(tableName));
    }

    private Map<String, AttributeValue> toAttributevalueMap() {
        return ImmutableMap.of(
                "name", AttributeValue.builder().s(name).build(),
                "id", AttributeValue.builder().s(id.toString()).build()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class CreateUser {

        private String name;

        public String getName() {
            return name;
        }

        public User toUser(DynamoDbClient dynamo, String tableName) {
            return new User(dynamo, tableName, name);
        }
    }
}

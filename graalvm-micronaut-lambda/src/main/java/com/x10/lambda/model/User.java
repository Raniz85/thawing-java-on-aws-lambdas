package com.x10.lambda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;

@Introspected
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

    @Introspected
    public static class CreateUser {

        @NonNull
        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User toUser(DynamoDbClient dynamo, String tableName) {
            return new User(dynamo, tableName, name);
        }
    }
}

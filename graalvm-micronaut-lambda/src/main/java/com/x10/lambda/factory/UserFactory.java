package com.x10.lambda.factory;


import com.x10.lambda.model.User;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Singleton
public class UserFactory {

    private final DynamoDbClient dynamo;

    private final String tableName;

    @Inject
    public UserFactory(DynamoDbClient dynamo, @Property(name = "user.table-name") String tableName) {
        this.dynamo = dynamo;
        this.tableName = tableName;
    }

    public User createUser(final User.CreateUser createUser) {
        return createUser.toUser(dynamo, tableName);
    }
}

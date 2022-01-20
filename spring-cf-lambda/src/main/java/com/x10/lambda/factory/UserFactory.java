package com.x10.lambda.factory;


import com.x10.lambda.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
public class UserFactory {

    private final DynamoDbClient dynamo;

    private final String tableName;

    public UserFactory(DynamoDbClient dynamo, @Value("${user.table-name}") String tableName) {
        this.dynamo = dynamo;
        this.tableName = tableName;
    }

    public User createUser(final User.CreateUser createUser) {
        return createUser.toUser(dynamo, tableName);
    }
}

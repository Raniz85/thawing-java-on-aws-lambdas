package com.x10.lambda.model

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DynamoDbRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse
import spock.lang.Specification

import java.util.function.Consumer

class UserSpec extends Specification {

    DynamoDbClient dynamo = Mock()

    def user(String name) {
        new User(dynamo, "testTable", name)
    }

    def "That user is correctly saved"() {
        given: "A user"
        def user = user("John Doe")
        def userId = user.id

        when: "The user is saved"
        user.save()

        then: "The correct call is sent to DynamoDB"
        1 * dynamo.putItem(_ as Consumer<PutItemRequest.Builder>) >> { Consumer<DynamoDbRequest.Builder> callback ->
            def builder = PutItemRequest.builder()
            callback.accept(builder)
            PutItemRequest request = builder.build()
            assert request.tableName() == "testTable"
            assert request.item().get("name").s() == "John Doe"
            assert request.item().get("id").s() == userId.toString()

            return PutItemResponse.builder()
                    .build()
        }
    }
}

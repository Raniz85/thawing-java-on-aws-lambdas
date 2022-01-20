package com.x10.lambda

import com.x10.lambda.model.User
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import spock.lang.Specification

import javax.validation.ConstraintViolationException
import java.util.function.Consumer

// Starting the embedded application won't work since the test
// properties doesn't propagate down to the embedded application
// We construct the handler ourselves instead
@MicronautTest(startApplication = false)
@Property(name = "user.table-name", value = "test")
class UserRequestHandlerSpec extends Specification {

    @Inject
    private ApplicationContext context

    private DynamoDbClient dynamo = Mock()

    @MockBean(DynamoDbClient)
    DynamoDbClient dynamo() {
        dynamo
    }

    private UserRequestHandler requestHandler;

    def setup() {
        requestHandler = new UserRequestHandler(context)
    }

    def "Invalid names"() {
        given: "A user to create"
        def user = new User.CreateUser(
                name: name
        )

        when: "The user is handled"
        requestHandler.execute(user)

        then: "The correct action is taken"
        thrown(ConstraintViolationException)

        where:
        name << ["", null]
    }

    def "Valid names"() {
        given: "A user to create"
        def user = new User.CreateUser(
                name: name
        )

        when: "The user is handled"
        requestHandler.execute(user)

        then: "The user is saved"
        1 * dynamo.putItem(_ as Consumer)

        where:
        name << ["a", "1", "John Doe", "🙂"]
    }
}

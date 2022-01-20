package com.x10.lambda;

import com.x10.lambda.factory.UserFactory;
import com.x10.lambda.model.User;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Optional;

@Introspected
public class UserRequestHandler extends MicronautRequestHandler<User.CreateUser, User> {

    private final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);

    public UserRequestHandler() {
    }

    public UserRequestHandler(ApplicationContext context) {
        super(context);
    }

    @Override
    public User execute(User.CreateUser input) {
        long start = System.nanoTime();
        final Validator validator = getApplicationContext().getBean(Validator.class);
        Optional.of(validator.validate(input))
                .filter(c -> !c.isEmpty())
                .ifPresent(c -> {
                    throw new ConstraintViolationException(c);
                });
        final UserFactory userFactory = getApplicationContext().getBean(UserFactory.class);
        long userGet = System.nanoTime();
        logger.debug(String.format("Getting user factory took %.3f ms%n", (userGet - start) / 1e6));
        final User user = userFactory.createUser(input);
        long userCreate = System.nanoTime();
        logger.debug(String.format("Creating user took %.3f ms%n", (userCreate - userGet) / 1e6));
        user.save();
        long userSave = System.nanoTime();
        logger.debug(String.format("Saving user took %.3f ms%n", (userSave - userCreate) / 1e6));
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memory.getHeapMemoryUsage();
        logger.debug("Heap memory; max: {}, used: {}, commited: {}", heap.getMax(), heap.getUsed(), heap.getCommitted());
        MemoryUsage nonHeap = memory.getNonHeapMemoryUsage();
        logger.debug("Non-heap memory; max: {}, used: {}, commited: {}", nonHeap.getMax(), nonHeap.getUsed(), nonHeap.getCommitted());
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        logger.debug("Active threads: {}, daemon: {}, total started: {}", threads.getThreadCount(), threads.getDaemonThreadCount(), threads.getTotalStartedThreadCount());
        return user;
    }
}

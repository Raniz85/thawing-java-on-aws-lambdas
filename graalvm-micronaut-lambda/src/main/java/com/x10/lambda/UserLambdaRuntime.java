package com.x10.lambda;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.x10.lambda.model.User;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime;
import io.micronaut.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserLambdaRuntime extends AbstractMicronautLambdaRuntime<User.CreateUser, User, User.CreateUser, User> {

    private static final Logger logger = LoggerFactory.getLogger(UserLambdaRuntime.class);

    public static void main(String[] args) {
        try {
            new UserLambdaRuntime().run(args);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    protected RequestHandler<User.CreateUser, User> createRequestHandler(String... args) {
        return new UserRequestHandler();
    }

    @Override
    protected void log(LogLevel level, String msg) {
        switch (level) {
            case TRACE:
                logger.trace(msg);
                break;
            case DEBUG:
                logger.debug(msg);
                break;
            case ALL:
            case NOT_SPECIFIED:
            case INFO:
                logger.info(msg);
                break;
            case WARN:
                logger.warn(msg);
                break;
            case ERROR:
                logger.error(msg);
                break;
        }
    }

    @Override
    protected void logn(LogLevel logLevel, String msg) {
        log(logLevel, msg);
    }

    @Override
    protected void logn(LogLevel level, Object... messageParts) {
        log(level, Stream.of(messageParts)
                .map(String::valueOf)
                .collect(Collectors.joining()));
    }

    @Override
    protected boolean shouldLog(LogLevel level) {
        switch (level) {
            case ALL:
                return true;
            case TRACE:
                return logger.isTraceEnabled();
            case DEBUG:
                return logger.isDebugEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case WARN:
                return logger.isWarnEnabled();
            case ERROR:
                return logger.isErrorEnabled();
            default:
            case OFF:
            case NOT_SPECIFIED:
                return false;
        }
    }

    @Override
    protected LogLevel getLogLevel() {
        if (logger.isTraceEnabled()) {
            return LogLevel.TRACE;
        }
        if (logger.isDebugEnabled()) {
            return LogLevel.DEBUG;
        }
        if (logger.isInfoEnabled()) {
            return LogLevel.INFO;
        }
        if (logger.isWarnEnabled()) {
            return LogLevel.WARN;
        }
        if (logger.isErrorEnabled()) {
            return LogLevel.ERROR;
        }
        return LogLevel.OFF;
    }
}

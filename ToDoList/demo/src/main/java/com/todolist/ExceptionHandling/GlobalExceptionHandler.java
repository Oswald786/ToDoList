package com.todolist.ExceptionHandling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
public class GlobalExceptionHandler implements ExceptionHandler<RuntimeException, HttpResponse<?>> {


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @Override
    public HttpResponse<?> handle(HttpRequest request, RuntimeException exception) {
        ExceptionMessageModel exceptionInfo = new ExceptionMessageModel();
        exceptionInfo.setMessage(exception.getMessage());
        exceptionInfo.setError(exception.getClass().getSimpleName());
        exceptionInfo.setTimestamp(String.valueOf(System.currentTimeMillis()));
        exceptionInfo.setPath(request.getPath());
        log.error("Global exception: {}", exceptionInfo.toString());
        return HttpResponse.serverError(exceptionInfo);
    }

    @PostConstruct
    public void init() {
        log.info("GlobalExceptionHandler INITIALIZED");
    }
}

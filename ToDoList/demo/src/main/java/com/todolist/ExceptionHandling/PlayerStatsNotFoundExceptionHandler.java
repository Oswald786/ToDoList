package com.todolist.ExceptionHandling;

import com.todolist.exceptions.PlayerStatsNotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
public class PlayerStatsNotFoundExceptionHandler implements ExceptionHandler<PlayerStatsNotFoundException, HttpResponse<ExceptionMessageModel>> {
    @Override
    public HttpResponse<ExceptionMessageModel> handle(HttpRequest request, PlayerStatsNotFoundException exception) {
        ExceptionMessageModel exceptionInfo = new ExceptionMessageModel();
        exceptionInfo.setMessage(exception.getMessage());
        exceptionInfo.setError(exception.getClass().getSimpleName());
        exceptionInfo.setTimestamp(String.valueOf(System.currentTimeMillis()));
        exceptionInfo.setPath(request.getPath());
        return HttpResponse.notFound(exceptionInfo);
    }
}

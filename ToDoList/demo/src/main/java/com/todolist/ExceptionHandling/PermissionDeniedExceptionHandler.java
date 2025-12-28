package com.todolist.ExceptionHandling;

import com.todolist.exceptions.PermissionDeniedException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
public class PermissionDeniedExceptionHandler implements ExceptionHandler<PermissionDeniedException, HttpResponse<ExceptionMessageModel>> {
    @Override
    public HttpResponse<ExceptionMessageModel> handle(HttpRequest request, PermissionDeniedException exception) {
        ExceptionMessageModel exceptionInfo = new ExceptionMessageModel();
        exceptionInfo.setMessage(exception.getMessage());
        exceptionInfo.setError(exception.getClass().getSimpleName());
        exceptionInfo.setTimestamp(String.valueOf(System.currentTimeMillis()));
        exceptionInfo.setPath(request.getPath());
        return HttpResponse.badRequest(exceptionInfo);
    }
}

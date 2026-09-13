package org.example.dogs.adapter.in.rest;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.example.dogs.application.exception.DogNotFoundException;

@Singleton
public class DogNotFoundExceptionHandler
        implements ExceptionHandler<DogNotFoundException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(
            HttpRequest request,
            DogNotFoundException exception) {

        return HttpResponse.notFound();
    }
}
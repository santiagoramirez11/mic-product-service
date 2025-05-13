package com.banreservas.product.exception.handler;

import com.banreservas.openapi.models.ErrorResponseDto;
import com.banreservas.openapi.models.LoginCredentialErrorResponseDto;
import com.banreservas.product.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleOnError(final ProductNotFoundException exception) {
        log.debug(exception.getMessage());
        final ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), "product-not-found", exception.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<LoginCredentialErrorResponseDto>> handleOnError(final BadCredentialsException exception) {
        log.debug(exception.getMessage());
        final LoginCredentialErrorResponseDto errorResponse = new LoginCredentialErrorResponseDto(exception.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleOnError(final DuplicateKeyException exception) {
        log.debug(exception.getMessage());
        final ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.CONFLICT.value(), "entity-already-exists", "A resource with the same key already exists. ");
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleOnError(final IllegalArgumentException exception) {
        log.debug(exception.getMessage());
        final ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), "IllegalArgumentException", "The provided argument is invalid. Please check your input and try again. " + exception.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleOnError(final Exception exception) {
        log.error(exception.getMessage());
        final ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE.value(), "service-unavailable", "An unexpected error occurred. Please try again later or contact support.");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse));
    }
}

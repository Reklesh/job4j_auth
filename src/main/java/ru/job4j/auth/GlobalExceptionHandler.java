package ru.job4j.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<?> handle(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        return e.getFieldErrors().stream()
                .map(f -> Map.of(
                        f.getField(),
                        String.format("%s. Actual value: %s", f.getDefaultMessage(), f.getRejectedValue())
                ))
                .toList();
    }
}

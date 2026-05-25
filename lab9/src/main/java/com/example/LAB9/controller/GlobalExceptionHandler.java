package com.example.LAB9.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Object handleBadRequest(Exception ex, Model model, HttpServletRequest request) {
        HttpStatus status = ex instanceof IllegalArgumentException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        String message = extractMessage(ex);

        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(status).body(buildBody(status, message, request.getRequestURI()));
        }

        model.addAttribute("message", message);
        return "error-page";
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, Model model, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI()));
        }

        model.addAttribute("message", ex.getMessage());
        return "error-page";
    }

    private Map<String, Object> buildBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    private String extractMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException validationException
                && validationException.getBindingResult().hasFieldErrors()) {
            return validationException.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        }
        return ex.getMessage();
    }
}

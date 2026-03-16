package com.bank.creditmonitor.config;

import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex,
            @RequestHeader(value = "Accept", defaultValue = "") String accept, Model model) {
        if (accept.contains("application/json")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", ex.getMessage(),
                            "timestamp", LocalDateTime.now().toString(),
                            "status", 404));
        }
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex,
            @RequestHeader(value = "Accept", defaultValue = "") String accept, Model model) {
        if (accept.contains("application/json")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Internal server error",
                            "detail", ex.getMessage(),
                            "timestamp", LocalDateTime.now().toString(),
                            "status", 500));
        }
        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
        return "error";
    }
}

package com.fresh.auth.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(RuntimeException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", e.getMessage());
        result.put("data", null);
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", message);
        result.put("data", null);
        return result;
    }

    @ExceptionHandler(BindException.class)
    public Map<String, Object> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", message);
        result.put("data", null);
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "系统异常");
        result.put("data", null);
        return result;
    }
}

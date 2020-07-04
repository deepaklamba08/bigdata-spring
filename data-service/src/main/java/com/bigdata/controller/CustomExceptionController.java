package com.bigdata.controller;

import com.bigdata.model.RequestResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class CustomExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RequestResult<Map<String, Object>>> objectNotFoundExceptionHandler(Exception ex, WebRequest request) {
        ex.printStackTrace();
        RequestResult<Map<String, Object>> errors = new RequestResult<>(204, "Request is invalid.");
        return new ResponseEntity<>(errors, HttpStatus.NO_CONTENT);
    }

}

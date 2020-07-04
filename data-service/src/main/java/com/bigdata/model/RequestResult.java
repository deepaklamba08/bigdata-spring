package com.bigdata.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class RequestResult<T> {

    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public RequestResult() {
    }

    public RequestResult(int status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public RequestResult(int status, String message) {
        this.status = status;
        this.message = message;
    }


    public RequestResult(int status, T result) {
        this.status = status;
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

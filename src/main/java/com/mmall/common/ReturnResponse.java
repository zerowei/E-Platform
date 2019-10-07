package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReturnResponse<T> implements Serializable {
    private int status;
    private T data;
    private String message;

    private ReturnResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    private ReturnResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ReturnResponse(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    private ReturnResponse(int status) {
        this.status = status;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return status == StatusCode.SUCCESS.getCode();
    }

    public static <T> ReturnResponse<T> ReturnSuccess() {
        return new ReturnResponse<>(StatusCode.SUCCESS.getCode());
    }

    public static <T> ReturnResponse<T> ReturnSuccessByMessage(String message) {
        return new ReturnResponse<>(StatusCode.SUCCESS.getCode(), message);
    }

    public static <T> ReturnResponse<T> ReturnSuccessByData(T data) {
        return new ReturnResponse<>(StatusCode.SUCCESS.getCode(), data);
    }

    public static <T> ReturnResponse<T> ReturnSuccess(String message, T data) {
        return new ReturnResponse<>(StatusCode.SUCCESS.getCode(), data, message);
    }

    public static <T> ReturnResponse<T> ReturnError() {
        return new ReturnResponse<>(StatusCode.ERROR.getCode());
    }

    public static <T> ReturnResponse<T> ReturnErrorByMessage(String message) {
        return new ReturnResponse<>(StatusCode.ERROR.getCode(), message);
    }

    public static <T> ReturnResponse<T> ReturnError(int status, String message) {
        return new ReturnResponse<>(status, message);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

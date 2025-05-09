package com.example.taskapi2.model;

import lombok.Data;

@Data
public class ApiResponse<T> {

    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    private String status;
    private String message;
    private T data;
    private long timestamp;

}

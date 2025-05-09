package com.example.taskapi2.service;

import com.example.taskapi2.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    public <T> ResponseEntity<ApiResponse<T>> successResponse(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>("OK", message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<Void>> successResponse(String message) {
        ApiResponse<Void> response = new ApiResponse<>("OK", message, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public <T> ResponseEntity<ApiResponse<T>> createdResponse(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>("CREATED", message, data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse<Void>> createdResponse(String message) {
        ApiResponse<Void> response = new ApiResponse<>("CREATED", message, null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse<Void>> errorResponse(String message) {
        ApiResponse<Void> response = new ApiResponse<>("error", message, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    public <T> ResponseEntity<ApiResponse<T>> errorResponse(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>("error", message, data);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

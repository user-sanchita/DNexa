package com.example.ECommerce.Platform.Exception;

public class FollowedByAnotherStatusException extends RuntimeException {
    public FollowedByAnotherStatusException(String message) {
        super(message);
    }
}

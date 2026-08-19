package com.example.ECommerce.Platform.Exception;

public class EmailSendException extends RuntimeException {
  public EmailSendException(String message) {
    super(message);
  }
}

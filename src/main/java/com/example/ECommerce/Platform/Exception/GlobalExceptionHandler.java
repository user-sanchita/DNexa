package com.example.ECommerce.Platform.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String,String>errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(),error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
    // 2. Custom Exception (Login fail)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(401).body(error);
    }

    // 3. Generic Exception (fallback)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex){
//        Map<String, String> error = new HashMap<>();
//        error.put("error", "Something went wrong");
//        return ResponseEntity.status(500).body(error);
//    }
    //    4. Email Already Exists
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleEmailExists(EmailAlreadyExistsException ex){
        Map<String,String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }


    // 5. Already Done
    @ExceptionHandler(AlreadyDoneException.class)
    public ResponseEntity<String> handleUserNotactivated(AlreadyDoneException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    // 6.User's Status not active
    @ExceptionHandler(UserStatusNotActivatedException.class)
    public ResponseEntity<String> handleUserStatus(UserStatusNotActivatedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }
    // 7.Same Password
    @ExceptionHandler(SameException.class)
    public ResponseEntity<String> handleSamePassword(SameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 8. User Not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // 9. Invalid Input
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInput(InvalidInputException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    //Invalid one time token
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidToken(InvalidTokenException ex){
        return ResponseEntity.status(400).body(ex.getMessage());
    }
    //Email send exception
    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<String> handleEmailError(EmailSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
    // Category is Inactive
    @ExceptionHandler(NotActivatedException.class)
    public ResponseEntity<String> handleNotActivationError(NotActivatedException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
    //SellPrice LesserThan RealPrice
    @ExceptionHandler(SellPriceLesserThanRealPrice.class)
    public ResponseEntity<String> handleSellPriceAndRealPrice(SellPriceLesserThanRealPrice ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
    //product not available
    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<String> handleUnavailableProduct(ProductNotAvailableException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(FollowedByAnotherStatusException.class)
    public ResponseEntity<String> handlePreviousStatus(FollowedByAnotherStatusException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedUsage(UnAuthorizedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

}
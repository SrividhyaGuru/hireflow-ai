package com.hireflow.auth.exception;

import com.hireflow.auth.dto.ErrorResponse;
import com.hireflow.auth.exception.business.InvalidRoleException;
import com.hireflow.auth.exception.business.UserAlreadyExistsByEmailException;
import com.hireflow.auth.exception.business.UserNameTakenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsByEmailException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistByEmail(UserAlreadyExistsByEmailException userAlreadyExistsByEmailException) {
        ErrorResponse errorResponse = new ErrorResponse(List.of(userAlreadyExistsByEmailException.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNameTakenException.class)
    public ResponseEntity<ErrorResponse> handleUsernameTakenException(UserNameTakenException userNameTakenException) {
        ErrorResponse errorResponse = new ErrorResponse(List.of(userNameTakenException.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoleException(InvalidRoleException invalidRoleException) {
        ErrorResponse errorResponse = new ErrorResponse(List.of(invalidRoleException.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleRequestValidationException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<String> requestValidationErrors = methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();
        ErrorResponse errorResponse = new ErrorResponse(requestValidationErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

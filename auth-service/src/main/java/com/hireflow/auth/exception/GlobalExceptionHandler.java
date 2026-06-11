package com.hireflow.auth.exception;

import com.hireflow.auth.exception.businessexception.InvalidRoleException;
import com.hireflow.auth.exception.businessexception.UserAlreadyExistsByEmailException;
import com.hireflow.auth.exception.businessexception.UserNameTakenException;
import com.hireflow.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsByEmailException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistByEmail(UserAlreadyExistsByEmailException userAlreadyExistsByEmailException) {
        ErrorResponse errorResponse = new ErrorResponse(userAlreadyExistsByEmailException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNameTakenException.class)
    public ResponseEntity<ErrorResponse> handleUsernameTakenException(UserNameTakenException userNameTakenException) {
        ErrorResponse errorResponse = new ErrorResponse(userNameTakenException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoleException(InvalidRoleException invalidRoleException) {
        ErrorResponse errorResponse = new ErrorResponse(invalidRoleException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

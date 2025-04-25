package com.repsy;

import com.repsy.exceptions.FileValidationException;
import com.repsy.exceptions.InvalidMetadataException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidMetadataException.class})
    public ResponseEntity<String> handleInvalidMetadata(InvalidMetadataException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({FileValidationException.class})
    public ResponseEntity<String> handleInvalidMetadata(FileValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // Log exception here (log.error etc.)
        return ResponseEntity.internalServerError()
                .body("An unexpected error occurred: " + ex.getMessage());
    }

}

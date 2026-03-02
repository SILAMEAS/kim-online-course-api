package com.sila.config.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExceptionGlobal {

    /**
     * ✅ Handle ALL database constraint violations (GENERIC)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";

        String message = "Database constraint violation";

        // ✅ MySQL duplicate entry
        if (rootMsg.contains("Duplicate entry")) {

            String field = extractFieldFromConstraint(rootMsg);

            if (field != null) {
                message = field + " already exists";
            } else {
                message = "Duplicate value already exists";
            }
        }

        return new ResponseEntity<>(
                MessageResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(message)
                        .build(),
                HttpStatus.CONFLICT
        );
    }

    /**
     * ✅ Extract field name from constraint name
     * Works ONLY if you name constraints properly
     * Example: uk_user_email → email
     */
    private String extractFieldFromConstraint(String message) {
        try {
            // Find: for key 'constraint_name'
            int keyIndex = message.indexOf("for key '");
            if (keyIndex == -1) return null;

            String constraint = message.substring(keyIndex + 9, message.length() - 1);

            // Example:
            // uk_user_email → email
            // uk_course_title → title

            if (constraint.contains("_")) {
                String[] parts = constraint.split("_");
                return parts[parts.length - 1];
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Handle FileValidationException
     */
    @ExceptionHandler(value = FileValidationException.class)
    public ResponseEntity<MessageResponse> handleFileValidationException(FileValidationException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message(ex.getMessage()).status(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<MessageResponse> handAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message(ex.getMessage()).status(HttpStatus.FORBIDDEN.value()).build(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handle ServerErrorException
     */
    @ExceptionHandler(value = ServerErrorException.class)
    public ResponseEntity<MessageResponse> handleNotFound(ServerErrorException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message(ex.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle NotFoundException
     */
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(NotFoundException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message(ex.getMessage()).status(HttpStatus.NOT_FOUND.value()).build(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle BadRequestException
     */
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<MessageResponse> handleBadRequest(BadRequestException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message(ex.getMessage()).status(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
    }


    /**
     * HttpMediaTypeNotSupportedException
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<MessageResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message("Content-Type not supported: " + ex.getContentType()).status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).build(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(MessageResponse.builder().message(ex.getMessage()).status(HttpStatus.PAYLOAD_TOO_LARGE.value()).build(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Handle MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MessageResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s.",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");

        return new ResponseEntity<>(
                MessageResponse.builder().message(message).status(HttpStatus.BAD_REQUEST.value()).build(),
                HttpStatus.BAD_REQUEST
        );

    }

    /**
     * Handle MethodArgumentNotValidException
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationException(MethodArgumentNotValidException exception) {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setStatus(HttpStatus.BAD_REQUEST.value());

        StringBuilder str = new StringBuilder();
        var fieldErrors = exception.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            str.append(fieldErrors.get(0).getField()).append(": ").append(fieldErrors.get(0).getDefaultMessage());
        } else {
            str.append("Validation failed.");
        }
        return new ResponseEntity<>(MessageResponse.builder().message(str.toString()).status(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
    }


}

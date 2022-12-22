package com.example.files.controller.exception;

import com.example.files.controller.exception.object.DeleteObjectException;
import com.example.files.controller.exception.object.DownloadObjectException;
import com.example.files.controller.exception.object.GetObjectException;
import com.example.files.controller.exception.object.UploadObjectException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResponseExceptionHandler {

    @ExceptionHandler(DownloadObjectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDownloadException(DownloadObjectException ex) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(UploadObjectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUploadException(UploadObjectException ex) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(DeleteObjectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUploadException(DeleteObjectException ex) {
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(GetObjectException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleUploadException(GetObjectException ex) {
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }
}

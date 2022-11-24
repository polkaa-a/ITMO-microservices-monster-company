package monsters.controller.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class ResponseExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(NotFoundException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<Violation> violations = ex.getConstraintViolations()
                .stream()
                .map(violation ->
                        new Violation(violation.getPropertyPath().toString(), violation.getMessage())
                )
                .toList();

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                "invalid data",
                request.getDescription(false),
                violations
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError ->
                        new Violation(fieldError.getField(), fieldError.getDefaultMessage())
                )
                .toList();

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                "invalid data",
                request.getDescription(false),
                violations
        );
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleEntityExistsException(EntityExistsException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorMessage> handleFeignException(FeignException ex, WebRequest request) {
        return switch (ex.status()) {
            case 404 -> new ResponseEntity<>(new ErrorMessage(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage(),
                    request.getDescription(false),
                    null), HttpStatus.NOT_FOUND);
            case 503 -> new ResponseEntity<>(new ErrorMessage(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    ex.getMessage(),
                    request.getDescription(false),
                    null),
                    HttpStatus.SERVICE_UNAVAILABLE);
            default -> new ResponseEntity<>(new ErrorMessage(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getMessage(),
                    request.getDescription(false),
                    null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}

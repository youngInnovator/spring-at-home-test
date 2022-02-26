package com.rize.test.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.rize.test.exception.ArtistNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RizeGlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);

    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        if(ex.getMostSpecificCause() instanceof InvalidFormatException
                && ex.getMessage().contains("Cannot deserialize value of type `java.util.Date` from String")){
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", status.value());

            List<String> errors = new ArrayList<>();
            errors.add(String.format("Cannot deserialize value of Date from String: %s. Please provide the value in yyyy-mm-dd format", getErrorValue(ex)));

            body.put("errors", errors);

            return new ResponseEntity<>(body, headers, status);
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @ResponseBody
    @ExceptionHandler(ArtistNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String artistNotFoundHandler(ArtistNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();

        List<String> errors = new ArrayList<>();
        errors.add(getErrorValue(ex));

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private String getErrorValue(HttpMessageNotReadableException ex){
        String message = ex.getMessage();
        return (message != null ? message.substring(message.indexOf("String \"") + 8, message.indexOf("\":")) : null);
    }

    private String getErrorValue(ConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains(":")){
            return message.substring(message.indexOf(":") + 2);
        }
        return message;
    }

}

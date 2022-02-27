package com.rize.test.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.rize.test.exception.ArtistNotFoundException;
import com.rize.test.model.Category;
import com.rize.test.model.ErrorDto;
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
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@ControllerAdvice
public class RizeGlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ErrorDto(errors), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        ErrorDto errorDto = null;
        Throwable cause = ex.getMostSpecificCause();
        if(cause instanceof InvalidFormatException
                || cause instanceof IllegalArgumentException
                && ex.getMessage() != null){
            errorDto = handleError(ex.getMessage());
        }

        if(errorDto != null){
            return new ResponseEntity<>(errorDto, headers, status);
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @ResponseBody
    @ExceptionHandler(ArtistNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> artistNotFoundHandler(ArtistNotFoundException ex) {
        return new ResponseEntity<>(new ErrorDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        return new ResponseEntity<>(new ErrorDto(getErrorValue(ex)), HttpStatus.BAD_REQUEST);
    }

    private ErrorDto handleError(@NotNull String message){
        List<String> errors = new ArrayList<>();
        if(message.contains("Cannot deserialize value of type `java.util.Date` from String")){
            errors.add(format("Invalid Date value: %s. Please provide the value in yyyy-mm-dd format",
                    message.substring(message.indexOf("String \"") + 8, message.indexOf("\":"))));
        }

        if(message.contains("JSON parse error: No enum constant com.rize.test.model.Category")){
            errors.add(format("Category value %s is incorrect. It must be in %s",
                    message.substring(message.indexOf("Category.") + 9, message.indexOf(";")),
                    Arrays.toString(Category.values())));
        }

        if(!errors.isEmpty()){
            return new ErrorDto(errors);
        }

        return null;
    }

    private String getErrorValue(@NotNull ConstraintViolationException ex){
        String message = ex.getMessage();
        if(message != null && message.contains(":")){
            return message.substring(message.indexOf(":") + 2);
        }
        return message;
    }
}

package com.rize.test.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.rize.test.exception.ArtistNotFoundException;
import com.rize.test.model.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RizeGlobalResponseEntityExceptionHandlerTest {

    private MethodArgumentNotValidException methodArgumentNotValidException;

    private BindingResult bindingResult;

    private List<FieldError> errors;
    private RizeGlobalResponseEntityExceptionHandler rizeGlobalResponseEntityExceptionHandler;

    @Mock
    private ArtistNotFoundException artistNotFoundException;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @BeforeEach
    public void setup() {
        bindingResult = mock(BindingResult.class);
        methodArgumentNotValidException = new MethodArgumentNotValidException(null, bindingResult);
        errors = new ArrayList<>();
        rizeGlobalResponseEntityExceptionHandler = new RizeGlobalResponseEntityExceptionHandler();
        errors.add(new FieldError("test", "test", "test cannot be null"));
        errors.add(new FieldError("test", "test2", "test2 cannot be null"));
    }

    @Test
    void testHandleMethodArgumentNotValid() {
        // Given
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // When
        ResponseEntity<Object> response = rizeGlobalResponseEntityExceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException,
                HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, null);

        //Then
        List<String> errors = ((ErrorDto) response.getBody()).getErrors();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, errors.size());
        assertEquals("test cannot be null", errors.get(0));
        assertEquals("test2 cannot be null", errors.get(1));
    }

    @ParameterizedTest
    @MethodSource("handleHttpMessageNotReadableValidValues")
    void testHandleHttpMessageNotReadableValidValues(String message, Exception exception, String expected) {
        // Given
        when(httpMessageNotReadableException.getMostSpecificCause()).thenReturn(exception);
        when(httpMessageNotReadableException.getMessage()).thenReturn(message);

        // When
        ResponseEntity<Object> response = rizeGlobalResponseEntityExceptionHandler.handleHttpMessageNotReadable(httpMessageNotReadableException,
                HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, null);

        //Then
        List<String> errors = ((ErrorDto) response.getBody()).getErrors();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, errors.size());
        assertThat(errors.get(0)).contains(expected);
    }

    private static Stream<Arguments> handleHttpMessageNotReadableValidValues() {
        return Stream.of(
                arguments("Cannot deserialize value of type `java.util.Date` from String \"2022-18-12\":",
                        new InvalidFormatException((JsonParser) null, null, null, null),
                        "Invalid Date value: 2022-18-12. Please provide the value in yyyy-mm-dd format"),
                arguments("JSON parse error: No enum constant com.rize.test.model.Category.ACTR;",
                        new IllegalArgumentException(),
                        "Category value ACTR is incorrect. It must be in")
        );
    }

    @ParameterizedTest
    @MethodSource("handleHttpMessageNotReadableInValidValues")
    void testHandleHttpMessageNotReadableInValidValues(String message, Exception exception) {
        // Given
        when(httpMessageNotReadableException.getMostSpecificCause()).thenReturn(exception);
        when(httpMessageNotReadableException.getMessage()).thenReturn(message);

        // When
        ResponseEntity<Object> response = rizeGlobalResponseEntityExceptionHandler.handleHttpMessageNotReadable(httpMessageNotReadableException,
                HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, null);

        //Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private static Stream<Arguments> handleHttpMessageNotReadableInValidValues() {
        return Stream.of(
                arguments("Cannot deserialize value of type `java.util.Date` from String \"2022-18-12\":",
                        new ConstraintViolationException(null)),
                arguments("JSON parse error: No enum constant com.rize.test.model.Category.ACTR;",
                        new ConstraintViolationException(null)),
                arguments("Cannot deserialize value",
                        new InvalidFormatException((JsonParser) null, null, null, null)),
                arguments("JSON parse error",
                        new IllegalArgumentException())
        );
    }

    @Test
    void testArtistNotFoundHandler() {
        // Given
        when(artistNotFoundException.getMessage()).thenReturn("Artist not found");

        // When
        ResponseEntity<Object> response = rizeGlobalResponseEntityExceptionHandler.artistNotFoundHandler(artistNotFoundException);

        //Then
        List<String> errors = ((ErrorDto) response.getBody()).getErrors();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, errors.size());
        assertEquals("Artist not found", errors.get(0));
    }

    @ParameterizedTest
    @CsvSource({"Invalid: Must provide correct date, Must provide correct date", "Must provide correct date, Must provide correct date"})
    void testConstraintViolationExceptionHandler(String message, String expectedValue) {
        // Given
        when(constraintViolationException.getMessage()).thenReturn(message);

        // When
        ResponseEntity<Object> response = rizeGlobalResponseEntityExceptionHandler.constraintViolationExceptionHandler(constraintViolationException);

        //Then
        List<String> errors = ((ErrorDto) response.getBody()).getErrors();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, errors.size());
        assertEquals(expectedValue, errors.get(0));
    }
}
package com.agileengine.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultExceptionHandlerTest {

    private DefaultExceptionHandler exceptionHandler = new DefaultExceptionHandler();
    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void itShouldReturnNotFoundResponseWhenHandlingResourceNotFoundException() {
        // given
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // when
        ResponseEntity<ApiError> response = exceptionHandler.handleException(exception, request);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().message());
    }

    @Test
    public void itShouldReturnBadRequestResponseWhenHandlingRequestValidationException() {
        // given
        RequestValidationException exception = new RequestValidationException("Validation failed");

        // when
        ResponseEntity<ApiError> response = exceptionHandler.handleException(exception, request);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().message());
    }

    @Test
    public void itShouldReturnInternalServerErrorResponseWhenHandlingInconsistentDataException() {
        // given
        InconsistentDataException exception = new InconsistentDataException("Data inconsistency");

        // when
        ResponseEntity<ApiError> response = exceptionHandler.handleException(exception, request);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Data inconsistency", response.getBody().message());
    }
}


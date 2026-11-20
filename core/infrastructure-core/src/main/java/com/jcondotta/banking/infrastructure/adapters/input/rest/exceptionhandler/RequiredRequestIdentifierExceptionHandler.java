package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.ApiProblem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequiredRequestIdentifierExceptionHandler {

    private static final String TITLE_VALIDATION_FAILED = "Request validation failed";

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ProblemDetail> handleMissingHeader(
        MissingRequestHeaderException ex, HttpServletRequest request) {
        var detail = "Required request header " + ex.getHeaderName() + " is missing.";
        return badRequest(detail, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleInvalidIdentifier(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return badRequest("Request parameter or header must contain a valid UUID.", request);
    }

    private static ResponseEntity<ProblemDetail> badRequest(String detail, HttpServletRequest request) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setType(ApiProblem.VALIDATION_ERRORS);
        problemDetail.setTitle(TITLE_VALIDATION_FAILED);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.of(problemDetail).build();
    }
}

package com.dbs.web.crawler.handler;

import com.dbs.web.crawler.vo.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Handler/Advice for any exception related to controller arguments (Ex: Missing Servlet Request
 * Parameter/Bean Validation/Conversion Failures etc)
 */
@RestControllerAdvice
@Order(1)
@Slf4j
public class CommonExceptionHandler {

  /**
   * Handler method for controller argument validation errors.
   *
   * <p>Argument validation failure handling can be applied to either: (1) Deserialized HTTP Request
   * Body Model (2) Converted Query Parameter Wrapper Model (For DTO models serving as a wrapper
   * class around query parameters)
   *
   * @param exception {@link MethodArgumentNotValidException}
   * @return The converted {@link ErrorResponse} object containing the formatted error message based
   *     on the method argument not valid failure.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationFailureException(MethodArgumentNotValidException exception) {

    List<String> fieldErrors =
        exception.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    new StringBuilder(fieldError.getField())
                        .append(" : ")
                        .append(fieldError.getDefaultMessage())
                        .toString())
            .collect(Collectors.toList());
    Collections.sort(fieldErrors);
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.toString())
        .messages(fieldErrors)
        .build();
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMissingServletRequestParameterException(
          MissingServletRequestParameterException exception) {
    return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.toString())
            .messages(
                    Collections.singletonList(
                            "Required parameter [ " + exception.getParameterName() + " ] is missing."))
            .build();
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(
          ResponseStatusException exception) {
    ErrorResponse error =
            ErrorResponse.builder()
                    .status(exception.getStatus().toString())
                    .messages(Arrays.asList(exception.getReason()))
                    .build();
    return ResponseEntity.status(exception.getStatus()).body(error);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGenericException(Exception exception) {
    log.error("500 INTERNAL_SERVER_ERROR : [ " + exception.getMessage() + " ]", exception);
    return ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
            .messages(singletonList(exception.getMessage()))
            .build();
  }
}

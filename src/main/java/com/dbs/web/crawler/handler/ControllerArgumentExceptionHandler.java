package com.dbs.web.crawler.handler;

import com.dbs.web.crawler.vo.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler/Advice for any exception related to controller arguments (Ex: Missing Servlet Request
 * Parameter/Bean Validation/Conversion Failures etc)
 */
@RestControllerAdvice
@Order(1)
public class ControllerArgumentExceptionHandler {

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
}

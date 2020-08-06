package com.thoughtworks.rslist.component;


import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.exception.InvalidRequestParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidParameterException;

/**
 * @author gaarahan
 */
@ControllerAdvice
public class RsExceptionHandler {

  @ExceptionHandler({
      InvalidParameterException.class,
      InvalidIndexException.class,
      InvalidRequestParameterException.class
  })
  public ResponseEntity<Error> handleException (Exception e) {
    Error error = new Error(e.getMessage());
    return ResponseEntity.badRequest().body(error);
  }
}

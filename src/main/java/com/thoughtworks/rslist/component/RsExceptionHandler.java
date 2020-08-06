package com.thoughtworks.rslist.component;

import com.thoughtworks.rslist.domain.Error;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.exception.InvalidRequestParameterException;
import com.thoughtworks.rslist.exception.InvalidUserIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author gaarahan
 */
@ControllerAdvice
public class RsExceptionHandler {

  @ExceptionHandler({
      InvalidIndexException.class,
      InvalidRequestParameterException.class,
      InvalidUserIdException.class
  })
  public ResponseEntity<Error> handleException (Exception e) {
    Error error = new Error(e.getMessage());
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class
  })
  public ResponseEntity<Error> handleMethodArgsHandler (MethodArgumentNotValidException e) {
    Error error;
    if (e.getBindingResult().getTarget() instanceof User) {
      error = new Error("invalid user");
    } else {
      error = new Error("invalid param");
    }
    return ResponseEntity.badRequest().body(error);
  }
}

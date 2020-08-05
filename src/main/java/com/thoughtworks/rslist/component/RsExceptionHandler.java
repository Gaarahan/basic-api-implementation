package com.thoughtworks.rslist.component;


import com.thoughtworks.rslist.exception.InvalidIndexException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author gaarahan
 */
@ControllerAdvice
public class RsExceptionHandler {

  @ExceptionHandler({Exception.class, InvalidIndexException.class})
  public String handleException (Exception e) {
    if (e instanceof InvalidIndexException) {
      return "invalid param";
    }
    return e.getMessage();
  }
}

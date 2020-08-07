package com.thoughtworks.rslist.exception;

/**
 * @author gaarahan
 */
public class InvalidRequestParameterException extends RuntimeException{

  private final String errMes;

  public InvalidRequestParameterException() {
    this.errMes = "invalid request param";
  }

  @Override
  public String getMessage() {
    return this.errMes;
  }
}

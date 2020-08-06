package com.thoughtworks.rslist.exception;

/**
 * @author gaarahan
 */
public class InvalidIndexException extends RuntimeException{

  private final String errMes;

  public InvalidIndexException() {
    this.errMes = "invalid index";
  }

  @Override
  public String getMessage() {
    return this.errMes;
  }
}

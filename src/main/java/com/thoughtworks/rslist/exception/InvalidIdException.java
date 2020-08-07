package com.thoughtworks.rslist.exception;

public class InvalidIdException extends RuntimeException {
  private final String errMes;

  public InvalidIdException() {
    this.errMes = "invalid id";
  }

  @Override
  public String getMessage() {
    return this.errMes;
  }
}

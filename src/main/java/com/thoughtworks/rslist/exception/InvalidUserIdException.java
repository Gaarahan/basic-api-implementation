package com.thoughtworks.rslist.exception;

public class InvalidUserIdException extends RuntimeException {
  private final String errMes;

  public InvalidUserIdException() {
    this.errMes = "invalid user id";
  }

  @Override
  public String getMessage() {
    return this.errMes;
  }
}

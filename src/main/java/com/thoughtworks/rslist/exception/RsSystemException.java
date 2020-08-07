package com.thoughtworks.rslist.exception;

public class RsSystemException extends RuntimeException {
  private final String errMsg;

  public RsSystemException(String message, String errMsg) {
    super(message);
    this.errMsg = errMsg;
  }

  public String getErrMsg() {
    return errMsg;
  }
}

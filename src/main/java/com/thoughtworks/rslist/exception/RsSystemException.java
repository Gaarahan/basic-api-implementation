package com.thoughtworks.rslist.exception;

public class RsSystemException extends RuntimeException {
  private final String errMsg;

  public RsSystemException(String errMsg) {
    this.errMsg = errMsg;
  }

  public String getErrMsg() {
    return errMsg;
  }
}

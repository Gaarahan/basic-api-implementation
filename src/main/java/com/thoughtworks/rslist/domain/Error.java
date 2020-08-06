package com.thoughtworks.rslist.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaarahan
 */

@Data
public class Error {
  private String error;

  public Error(String message) {
    this.error = message;
  }
}

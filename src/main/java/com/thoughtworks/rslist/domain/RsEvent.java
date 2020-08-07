package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsEvent {

  @NotNull
  private String eventName;

  @NotNull
  private String keyWord;

  @NotNull
  private int userId;
}

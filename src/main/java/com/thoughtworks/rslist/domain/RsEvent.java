package com.thoughtworks.rslist.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RsEvent {
  @NotNull
  private String eventName;
  @NotNull
  private String keyWord;
  @NotNull
  private int userId;

  private int voteNum = 0;

  public RsEvent(@NotNull String eventName, @NotNull String keyWord, @NotNull int userId) {
    this.eventName = eventName;
    this.keyWord = keyWord;
    this.userId = userId;
  }
}

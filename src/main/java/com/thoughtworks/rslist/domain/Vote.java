package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
  @NotNull
  private int rsEventId;
  @NotNull
  private int userId;
  @NotNull
  private int castVoteNum;
  @NotNull
  private Timestamp voteTime;
}

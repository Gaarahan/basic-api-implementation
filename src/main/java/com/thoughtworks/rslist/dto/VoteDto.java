package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author gaarahan
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="vote")
public class VoteDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private int castVoteNum;
  private Timestamp voteTime;

  @ManyToOne
  private UserDto userDto;

  @ManyToOne
  private RsEventDto rsEventDto;
}

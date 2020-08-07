package com.thoughtworks.rslist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * @author gaarahan
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="rs_event")
public class RsEventDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String eventName;
  private String keyWord;
  private int voteNum = 0;

  @ManyToOne
  private UserDto userDto;

  @OneToMany(mappedBy = "rsEventDto")
  private List<VoteDto> voteDtos;
}

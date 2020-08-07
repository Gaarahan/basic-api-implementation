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
@Table(name = "user")
public class UserDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;
  private int age;
  private String gender;
  private String email;
  private String phone;
  private int voteNum = 10;

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "userDto")
  private List<RsEventDto> rsEventDtos;
}

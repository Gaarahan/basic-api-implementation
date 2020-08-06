package com.thoughtworks.rslist.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author gaarahan
 */

@Entity
@Data
@Table(name = "user")
public class UserDto {
  @Id
  @GeneratedValue
  private int id;
  private String name;
  private int age;
  private String gender;
  private String email;
  private String phone;
  private int voteNum = 10;
}

package com.thoughtworks.rslist.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * @author gaarahan
 */

@Entity
@Data
@Table(name="rs_event")
public class RsEventDto {
  @Id
  @GeneratedValue
  private int id;
  private String eventName;
  private String keyWord;

  private int userId;
}

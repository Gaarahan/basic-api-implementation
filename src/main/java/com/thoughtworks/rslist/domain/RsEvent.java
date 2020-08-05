package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class RsEvent {

  @NotNull
  private String eventName;

  @NotNull
  private String key;

  @Valid
  @NotNull
  private User user;

  public RsEvent() {
    this.eventName = "";
    this.key = "";
    this.user = null;
  }

  public RsEvent(String eventName, String key, User user) {
    this.eventName = eventName;
    this.key = key;
    this.user = user;
  }

  public String getEventName() {
    return eventName;
  }

  public String getKey() {
    return key;
  }

  @JsonIgnore
  public User getUser() {
    return user;
  }

  @JsonProperty
  public void setUser(User user) {
    this.user = user;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RsEvent {

  private String eventName;
  private String key;
  private User user;

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

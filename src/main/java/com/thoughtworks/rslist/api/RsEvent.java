package com.thoughtworks.rslist.api;

public class RsEvent {

  private String eventName;
  private String key;

  public RsEvent(String eventName, String key) {
    this.eventName = eventName;
    this.key = key;
  }

  public String getEventName() {
    return eventName;
  }

  public String getKey() {
    return key;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public void setKey(String key) {
    this.key = key;
  }
}

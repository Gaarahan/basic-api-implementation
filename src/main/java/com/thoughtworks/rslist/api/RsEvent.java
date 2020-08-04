package com.thoughtworks.rslist.api;

public class RsEvent {

  private final String eventName;
  private final String key;

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
}

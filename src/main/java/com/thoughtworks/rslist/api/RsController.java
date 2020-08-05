package com.thoughtworks.rslist.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RsController {
  private final List<RsEvent> rsList;
  private User user;

  public RsController() {
    this.rsList = new ArrayList<>();
    this.user = new User("han", 21, "male", "gaarahan@foxmail.com", "12455556666");
    this.rsList.add(new RsEvent("rs1", "key", this.user));
    this.rsList.add(new RsEvent("rs2", "key", this.user));
    this.rsList.add(new RsEvent("rs3", "key", this.user));
  }

  @GetMapping("rs/{index}")
  public RsEvent getRsEventOfIndex (@PathVariable int index) {
    return this.rsList.get(index - 1);
  }

  @GetMapping("rs/list")
  public List<RsEvent> getSplitOrAllRsList (@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
    if (start != null && end != null) {
      return this.rsList.subList(start - 1, end);
    }
    return this.rsList;
  }

  @PostMapping("rs/add")
  public ResponseEntity<Object> addNewRsEvent (@RequestBody RsEvent event) {
    this.rsList.add(event);
    return ResponseEntity.created(URI.create("")).build();
  }

  @PatchMapping("rs/update")
  public void updateRsEvent (@RequestParam Integer index,
                             @RequestParam(required = false) String eventName,
                             @RequestParam(required = false) String key) {

    RsEvent targetRsEvent = this.rsList.get(index - 1);
    if (eventName != null) {
      targetRsEvent.setEventName(eventName);
    }

    if (key!= null) {
      targetRsEvent.setKey(key);
    }
  }

  @DeleteMapping("/rs/{index}")
  public void deleteRsEvent(@PathVariable int index) {
    this.rsList.remove(index - 1);
  }
}

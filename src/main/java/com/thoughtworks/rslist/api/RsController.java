package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.exception.InvalidRequestParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaarahan
 */
@RestController
public class RsController {
  private final List<RsEvent> rsList;

  public RsController() {
    this.rsList = new ArrayList<>();
    User user = new User("han", 21, "male", "gaarahan@foxmail.com", "12455556666");
    this.rsList.add(new RsEvent("rs1", "key", user));
    this.rsList.add(new RsEvent("rs2", "key", user));
    this.rsList.add(new RsEvent("rs3", "key", user));
  }

  @GetMapping("rs/{index}")
  public ResponseEntity<RsEvent> getRsEventOfIndex (@PathVariable int index) {
    if (index < 1 || index > this.rsList.size()) {
      throw new InvalidIndexException();
    }
    return ResponseEntity.ok(this.rsList.get(index - 1));
  }

  @GetMapping("rs/list")
  public ResponseEntity<List<RsEvent>> getSplitOrAllRsList (
      @RequestParam(required = false) Integer start,
      @RequestParam(required = false) Integer end
  ) {
    List<RsEvent> res;
    if (start == null && end == null) {
      res = this.rsList;
    }
    else if (start == null || end == null) {
      throw new InvalidRequestParameterException();
    }
    else {
      if (start > end || start < 1 || end > this.rsList.size()) {
        throw new InvalidRequestParameterException();
      }

      res = this.rsList.subList(start - 1, end);
    }

    return ResponseEntity.ok(res);
  }

  @PostMapping("rs/add")
  public ResponseEntity<Object> addNewRsEvent (@RequestBody @Valid RsEvent event) {
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

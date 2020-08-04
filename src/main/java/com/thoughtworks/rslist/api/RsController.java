package com.thoughtworks.rslist.api;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RsController {
  private final List<RsEvent> rsList;

  public RsController() {
    this.rsList = new ArrayList<>();
    this.rsList.add(new RsEvent("rs1", "key"));
    this.rsList.add(new RsEvent("rs2", "key"));
    this.rsList.add(new RsEvent("rs3", "key"));
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

  @PostMapping("/rs/add")
  public void addNewRsEvent (@RequestBody RsEvent event) {
    this.rsList.add(event);
  }
}

package com.thoughtworks.rslist.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RsController {
  private List<RsEvent> rsList;

  public RsController() {
    this.rsList = new ArrayList<>();
    this.rsList.add(new RsEvent("rs1", "key"));
    this.rsList.add(new RsEvent("rs2", "key"));
    this.rsList.add(new RsEvent("rs3", "key"));
  }

  @GetMapping("rs/list")
  public List<RsEvent> getSplitOrAllRsList (@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
    if (start != null && end != null) {
      return this.rsList.subList(start - 1, end);
    }
    return this.rsList;
  }
}

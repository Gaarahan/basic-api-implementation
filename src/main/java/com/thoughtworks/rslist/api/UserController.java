package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaarahan
 */
@RestController
public class UserController {
  private final List<User> userList;

  public UserController() {
    this.userList = new ArrayList<>();
  }

  @PostMapping("/users")
  public void register(@RequestBody @Valid User user) {
    this.userList.add(user);
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getList() {
    return ResponseEntity.ok(this.userList);
  }
}

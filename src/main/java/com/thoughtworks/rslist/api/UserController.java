package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author gaarahan
 */
@RestController
public class UserController {
  private final UserRepository userRepository;
  ModelMapper mapper = new ModelMapper();

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping("/users")
  public void register(@RequestBody @Valid User user) {
    UserDto newUser = mapper.map(user, UserDto.class);
    this.userRepository.save(newUser);
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserDto>> getList() {
    return ResponseEntity.ok(this.userRepository.findAll());
  }
}

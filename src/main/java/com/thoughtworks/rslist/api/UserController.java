package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

  @GetMapping("/users/{id}")
  public ResponseEntity<Optional<UserDto>> getUserById(@PathVariable Integer id) {
    if (id == null || id < 0) {
      throw new InvalidIndexException();
    }
    return ResponseEntity.ok(this.userRepository.findById(id));
  }

  @Transactional
  @GetMapping("/users/delete/{id}")
  public void deleteUserById(@PathVariable Integer id) {
    if (id == null || id < 0) {
      throw new InvalidIndexException();
    }
    this.userRepository.deleteById(id);
  }
}

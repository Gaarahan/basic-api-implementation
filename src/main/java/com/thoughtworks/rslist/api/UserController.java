package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.exception.InvalidUserIdException;
import com.thoughtworks.rslist.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gaarahan
 */
@RestController
public class UserController {
  private final UserRepository userRepository;
  ModelMapper modelMapper = new ModelMapper();

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping("/users")
  public void register(@RequestBody @Valid User user) {
    UserDto newUser = modelMapper.map(user, UserDto.class);
    this.userRepository.save(newUser);
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getList() {
    List<User> allUser = this.userRepository.findAll().stream()
        .map(userDto -> modelMapper.map(userDto, User.class))
        .collect(Collectors.toList());
    return ResponseEntity.ok(allUser);
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Integer id) {
    if (id == null || id < 0) {
      throw new InvalidUserIdException();
    }
    Optional<UserDto> userDto = this.userRepository.findById(id);
    if (!userDto.isPresent()) {
      throw new InvalidUserIdException();
    }
    return ResponseEntity.ok(modelMapper.map(userDto.get(), User.class));
  }

  @Transactional
  @DeleteMapping("/users/{id}")
  public void deleteUserById(@PathVariable Integer id) {
    if (id == null || id < 0) {
      throw new InvalidIndexException();
    }

    Optional<UserDto> userDto = this.userRepository.findById(id);
    if (!userDto.isPresent()) {
      throw new InvalidUserIdException();
    }

    this.userRepository.deleteById(id);
  }
}

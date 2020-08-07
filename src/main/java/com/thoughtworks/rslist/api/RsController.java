package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.exception.InvalidIdException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gaarahan
 */
@RestController
public class RsController {
  ModelMapper modelMapper = new ModelMapper();
  UserRepository userRepository;
  RsEventRepository rsEventRepository;

  public RsController(RsEventRepository rsEventRepository, UserRepository userRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("rs/list")
  public ResponseEntity<List<RsEvent>> getSplitOrAllRsList () {
    List<RsEvent> collect = this.rsEventRepository.findAll().stream().map(rsEventDto -> this.modelMapper.map(rsEventDto, RsEvent.class)).collect(Collectors.toList());
    return ResponseEntity.ok(collect);
  }

  @PostMapping("rs/add")
  public ResponseEntity<Object> addNewRsEvent (@RequestBody @Valid RsEvent event) {
    Optional<UserDto> userDto = this.userRepository.findById(event.getUserId());
    if (!userDto.isPresent()) {
      throw new InvalidIdException();
    }

    RsEventDto newRs = this.modelMapper.map(event, RsEventDto.class);
    newRs.setUserDto(userDto.get());
    this.rsEventRepository.save(newRs);
    return ResponseEntity.created(URI.create("")).build();
  }

  @PatchMapping("rs/update")
  public void updateRsEvent (@RequestParam Integer id,
                             @RequestParam(required = false) String eventName,
                             @RequestParam(required = false) String keyWord) {

    Optional<RsEventDto> targetRsEventDtoCon = this.rsEventRepository.findById(id);
    if (!targetRsEventDtoCon.isPresent()) {
      throw new InvalidIdException();
    }
    RsEventDto rsEventDto = targetRsEventDtoCon.get();
    if (eventName != null) {
      rsEventDto.setEventName(eventName);
    }

    if (keyWord!= null) {
      rsEventDto.setKeyWord(keyWord);
    }
    this.rsEventRepository.save(rsEventDto);
  }

  @Transactional
  @DeleteMapping("/rs/{id}")
  public void deleteRsEventById(@PathVariable int id) {
    if (this.rsEventRepository.findById(id).isPresent()) {
      this.rsEventRepository.deleteById(id);
    } else {
      throw new InvalidIdException();
    }
  }
}

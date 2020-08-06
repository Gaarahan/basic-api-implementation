package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.exception.InvalidIndexException;
import com.thoughtworks.rslist.exception.InvalidRequestParameterException;
import com.thoughtworks.rslist.exception.InvalidUserIdException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaarahan
 */
@RestController
public class RsController {
  ModelMapper mapper = new ModelMapper();
  UserRepository userRepository;
  RsEventRepository rsEventRepository;

  public RsController(RsEventRepository rsEventRepository, UserRepository userRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("rs/list")
  public ResponseEntity<List<RsEventDto>> getSplitOrAllRsList () {
    return ResponseEntity.ok(this.rsEventRepository.findAll());
  }

  @PostMapping("rs/add")
  public ResponseEntity<Object> addNewRsEvent (@RequestBody @Valid RsEvent event) {
    if (!this.userRepository.findById(event.getUserId()).isPresent()) {
      throw new InvalidUserIdException();
    }
    this.rsEventRepository.save(this.mapper.map(event, RsEventDto.class));
    return ResponseEntity.created(URI.create("")).build();
  }

//  @PatchMapping("rs/update")
//  public void updateRsEvent (@RequestParam Integer index,
//                             @RequestParam(required = false) String eventName,
//                             @RequestParam(required = false) String key) {
//
//    RsEvent targetRsEvent = this.rsList.get(index - 1);
//    if (eventName != null) {
//      targetRsEvent.setEventName(eventName);
//    }
//
//    if (key!= null) {
//      targetRsEvent.setKey(key);
//    }
//  }

  @Transactional
  @DeleteMapping("/rs/{id}")
  public void deleteRsEventById(@PathVariable int id) {
    this.rsEventRepository.deleteById(id);
  }
}

package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.exception.InvalidIdException;
import com.thoughtworks.rslist.exception.RsSystemException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class VoteController {
  RsEventRepository rsEventRepository;
  UserRepository userRepository;
  VoteRepository voteRepository;

  ModelMapper modelMapper = new ModelMapper();

  public VoteController(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
  }

  @PostMapping("/rs/vote/{rsEventId}")
  ResponseEntity<Object> voteToEvent(@PathVariable int rsEventId, @RequestBody @Valid Vote vote) {
    Optional<RsEventDto> rsEventDtoCon = this.rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDtoCon = this.userRepository.findById(vote.getUserId());
    if (!rsEventDtoCon.isPresent() || !userDtoCon.isPresent()) {
      throw new InvalidIdException();
    }


    UserDto userDto = userDtoCon.get();
    if (userDto.getRestVote() < vote.getCastVoteNum()) {
      throw new RuntimeException("rest vote not enough");
    }

    userDto.setRestVote(userDto.getRestVote() - vote.getCastVoteNum());
    this.userRepository.save(userDto);

    RsEventDto rsEventDto = rsEventDtoCon.get();
    rsEventDto.setVoteNum(rsEventDto.getVoteNum() + vote.getCastVoteNum());
    this.rsEventRepository.save(rsEventDtoCon.get());

    VoteDto voteDto = modelMapper.map(vote, VoteDto.class);
    voteDto.setUserDto(userDto);
    voteDto.setRsEventDto(rsEventDto);
    this.voteRepository.save(voteDto);

    return ResponseEntity.created(URI.create("")).build();
  }

  @GetMapping("/rs/vote")
  public List<Vote> getVoteList(@RequestParam String startTime, @RequestParam String endTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime start = LocalDateTime.parse(startTime, formatter);
    LocalDateTime end = LocalDateTime.parse(endTime, formatter);

    if (start.isAfter(end)) {
      throw new RsSystemException("wrong time interval");
    }

    return this.voteRepository.findAllByVoteTimeBetween(Timestamp.valueOf(start), Timestamp.valueOf(endTime))
        .stream().map(voteDto -> modelMapper.map(voteDto, Vote.class))
        .collect(Collectors.toList());
  }
}

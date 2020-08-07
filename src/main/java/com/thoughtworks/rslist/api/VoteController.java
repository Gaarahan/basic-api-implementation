package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.exception.InvalidIdException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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

}

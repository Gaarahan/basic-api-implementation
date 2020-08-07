package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.VoteDto;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends CrudRepository<VoteDto, Integer> {
  List<VoteDto> findAllByVoteTimeBetween(Timestamp start, Timestamp end);
}

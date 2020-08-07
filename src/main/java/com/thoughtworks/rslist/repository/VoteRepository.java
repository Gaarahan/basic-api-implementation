package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.VoteDto;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<VoteDto, Integer> {
}

package com.pc.votingapp.dao.repositories;

import com.pc.votingapp.dao.entities.Subject;
import com.pc.votingapp.dao.entities.User;
import com.pc.votingapp.dao.entities.Vote;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VoteRepository extends CrudRepository<Vote, Long> {

    Optional<Vote> findByUserAndSubject(User user, Subject subject);
}

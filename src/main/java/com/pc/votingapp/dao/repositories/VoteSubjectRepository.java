package com.pc.votingapp.dao.repositories;

import com.pc.votingapp.dao.entities.VoteSubject;
import org.springframework.data.repository.CrudRepository;

public interface VoteSubjectRepository extends CrudRepository<VoteSubject, Long> {
}

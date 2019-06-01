package com.pc.votingapp.dao.repositories;

import com.pc.votingapp.dao.entities.Subject;
import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
}

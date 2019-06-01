package com.pc.votingapp.dao.repositories;

import com.pc.votingapp.dao.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Search for user based on its login value
     * @param login
     * @return
     */
    Optional<User> findByLogin(String login);
}

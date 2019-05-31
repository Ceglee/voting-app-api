package com.pc.votingapp.dao.repositories;

import com.pc.votingapp.dao.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Search for user based on its login value
     * @param login
     * @return
     */
    User findByLogin(String login);
}

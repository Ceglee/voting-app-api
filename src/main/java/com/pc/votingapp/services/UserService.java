package com.pc.votingapp.services;

import com.pc.votingapp.api.resources.UserResource;
import com.pc.votingapp.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByLogin(username);
        return new User(user.getLogin(), user.getPassword(), Collections.emptyList());
    }

    @Transactional
    public String createUser(UserResource resource) {
        var entity = toEntity(resource);
        repository.save(entity);
        return entity.getLogin();
    }

    private com.pc.votingapp.dao.entities.User toEntity(UserResource resource) {
        var entity = new com.pc.votingapp.dao.entities.User();
        entity.setFirstName(resource.getFirstName());
        entity.setLastName(resource.getLastName());
        entity.setLogin(resource.getLogin());
        entity.setPassword(encoder.encode(resource.getPassword()));
        return entity;
    }
}

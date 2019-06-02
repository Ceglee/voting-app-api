package com.pc.votingapp.api.controllers;

import com.pc.votingapp.api.resources.UserResource;
import com.pc.votingapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserResource userResource,
                                                UriComponentsBuilder builder) {
        Long userId = service.createUser(userResource);
        return ResponseEntity.created(
                builder.path("/api/user/")
                        .path(String.valueOf(userId))
                        .build()
                        .toUri()
        ).build();
    }
}

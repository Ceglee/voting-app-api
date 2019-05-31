package com.pc.votingapp.api.controllers;

import com.pc.votingapp.api.resources.VoteSubjectResource;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("/api")
public class VoteController {

    @PostMapping("/subjects")
    public ResponseEntity<Object> createVoteSubject(@RequestBody @Valid VoteSubjectResource voteSubjectResource, HttpRequest request) {
        return ResponseEntity.created(request.getURI().resolve("123")).build();
    }
}

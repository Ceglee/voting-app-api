package com.pc.votingapp.api.controllers;

import com.pc.votingapp.api.resources.SubjectResource;
import com.pc.votingapp.api.resources.VoteResource;
import com.pc.votingapp.api.resources.VotingResource;
import com.pc.votingapp.services.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.util.List;

@RestController
public class VotingController {

    @Autowired
    private VotingService service;

    @GetMapping("/api/subject")
    public List<SubjectResource> getSubjects() {
        return service.getAllSubjects();
    }

    @PostMapping("/api/subject")
    public ResponseEntity<Object> createSubject(@RequestBody @Valid SubjectResource subjectResource,
                                                Principal principal,
                                                UriComponentsBuilder builder) {
        Long subjectId = service.createSubject(subjectResource, principal.getName());
        return ResponseEntity.created(
                builder.path("/api/subject/")
                        .path(String.valueOf(subjectId))
                        .build()
                        .toUri()
        ).build();
    }

    @GetMapping("/api/subject/{subjectId}/voting")
    public VotingResource getVoting(@PathVariable @Pattern(regexp = "^\\d*$") Long subjectId,
                                    Principal principal) {
        return service.getVotingForSubject(principal.getName(), subjectId);
    }

    @PostMapping("/api/subject/{subjectId}/vote")
    public ResponseEntity<Object> createVote(@RequestBody @Valid VoteResource subjectResource,
                                             @PathVariable @Pattern(regexp = "^\\d*$") Long subjectId,
                                             Principal principal,
                                             UriComponentsBuilder builder) {
        Long voteId = service.voteForSubject(subjectResource, principal.getName(), subjectId);
        return ResponseEntity.created(
                builder.path("/api/subject/")
                        .path(String.valueOf(subjectId))
                        .path("/vote/")
                        .path(String.valueOf(voteId))
                        .build()
                        .toUri()
        ).build();
    }

    @PutMapping("/api/subject/{subjectId}/vote")
    public VoteResource updateVote(@RequestBody @Valid VoteResource subjectResource,
                                   @PathVariable @Pattern(regexp = "^\\d*$") Long subjectId,
                                   Principal principal) {
        return service.updateVote(subjectResource, principal.getName(), subjectId);
    }

}

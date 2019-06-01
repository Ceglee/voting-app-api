package com.pc.votingapp.api.controllers;

import com.pc.votingapp.api.resources.SubjectResource;
import com.pc.votingapp.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class SubjectController {

    @Autowired
    private SubjectService service;

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
                builder.path("/api/subjects")
                        .path(String.valueOf(subjectId))
                        .build()
                        .toUri()
        ).build();
    }


}

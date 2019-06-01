package com.pc.votingapp.services;

import com.pc.votingapp.api.resources.SubjectResource;
import com.pc.votingapp.dao.entities.Subject;
import com.pc.votingapp.dao.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository repository;

    @Transactional
    public List<SubjectResource> getAllSubjects() {
        var results = repository.findAll().spliterator();
        return StreamSupport
                .stream(results, false)
                .map(this::toResource)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createSubject(SubjectResource resource) {
        var entity = toEntity(resource);
        repository.save(entity);
        return entity.getId();
    }

    private SubjectResource toResource(Subject entity) {
        var resource = new SubjectResource();
        resource.setTitle(entity.getTitle());
        resource.setDescription(entity.getDescription());
        resource.setVotingStart(entity.getVotingStart());
        resource.setVotingEnd(entity.getVotingEnd());
        return resource;
    }

    private Subject toEntity(SubjectResource resource) {
        var entity = new Subject();
        // TODO add owner id
        entity.setTitle(resource.getTitle());
        entity.setDescription(resource.getDescription());
        entity.setVotingStart(resource.getVotingStart());
        entity.setVotingEnd(resource.getVotingEnd());
        return entity;
    }
}

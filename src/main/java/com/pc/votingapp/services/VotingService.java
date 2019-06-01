package com.pc.votingapp.services;

import com.pc.votingapp.api.resources.SubjectResource;
import com.pc.votingapp.api.resources.VoteResource;
import com.pc.votingapp.api.resources.VotingResource;
import com.pc.votingapp.dao.entities.Subject;
import com.pc.votingapp.dao.entities.User;
import com.pc.votingapp.dao.entities.Vote;
import com.pc.votingapp.dao.repositories.SubjectRepository;
import com.pc.votingapp.dao.repositories.UserRepository;
import com.pc.votingapp.dao.repositories.VoteRepository;
import com.pc.votingapp.exceptions.AlreadyVotedException;
import com.pc.votingapp.exceptions.NotVotedYetException;
import com.pc.votingapp.exceptions.SubjectDoesNotExist;
import com.pc.votingapp.exceptions.UserNotFoundException;
import com.pc.votingapp.exceptions.VoteLockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class VotingService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Transactional
    public List<SubjectResource> getAllSubjects() {
        var subjects = subjectRepository.findAll().spliterator();
        return StreamSupport
                .stream(subjects, false)
                .map(this::toSubjectResource)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createSubject(SubjectResource resource, String username) {
        var user = userRepository.findByLogin(username).orElseThrow(UserNotFoundException::new);
        var subject = toSubjectEntity(resource);
        subject.setOwner(user);
        subjectRepository.save(subject);
        return subject.getId();
    }

    @Transactional
    public Long voteForSubject(VoteResource resource, String username, Long subjectId) {
        var user = userRepository.findByLogin(username).orElseThrow(UserNotFoundException::new);
        var subject = subjectRepository.findById(subjectId).orElseThrow(SubjectDoesNotExist::new);
        var vote = voteRepository.findByUserAndSubject(user, subject).orElse(null);

        if (vote != null) {
            throw new AlreadyVotedException();
        }

        vote = toVoteEntity(resource, user, subject);
        voteRepository.save(vote);
        return vote.getId();
    }

    @Transactional
    public VoteResource updateVote(VoteResource resource, String username, Long subjectId) {
        var user = userRepository.findByLogin(username).orElseThrow(UserNotFoundException::new);
        var subject = subjectRepository.findById(subjectId).orElseThrow(SubjectDoesNotExist::new);
        var vote = voteRepository.findByUserAndSubject(user, subject).orElseThrow(NotVotedYetException::new);

        if (vote.getLocked()) {
            throw new VoteLockedException();
        }

        vote.setInFavor(resource.getInFavor());
        vote.setLocked(true);
        return resource;
    }

    @Transactional
    public VotingResource getVotingForSubject(String username, Long subjectId) {
        var user = userRepository.findByLogin(username).orElseThrow(UserNotFoundException::new);
        var subject = subjectRepository.findById(subjectId).orElseThrow(SubjectDoesNotExist::new);
        var vote = voteRepository.findByUserAndSubject(user, subject).orElse(null);

        return toVotingResource(subject, vote);
    }

    private SubjectResource toSubjectResource(Subject entity) {
        var resource = new SubjectResource();
        resource.setId(entity.getId());
        resource.setTitle(entity.getTitle());
        resource.setDescription(entity.getDescription());
        resource.setVotingStart(entity.getVotingStart());
        resource.setVotingEnd(entity.getVotingEnd());
        return resource;
    }

    private Subject toSubjectEntity(SubjectResource resource) {
        var entity = new Subject();
        entity.setTitle(resource.getTitle());
        entity.setDescription(resource.getDescription());
        entity.setVotingStart(resource.getVotingStart());
        entity.setVotingEnd(resource.getVotingEnd());
        return entity;
    }

    private Vote toVoteEntity(VoteResource resource, User user, Subject subject) {
        var entity = new Vote();
        entity.setInFavor(resource.getInFavor());
        entity.setLocked(false);
        entity.setUser(user);
        entity.setSubject(subject);
        return entity;
    }

    private VotingResource toVotingResource(Subject subjectEntity, Vote userVoteEntity) {
        var resource = new VotingResource();
        var inFavor = 0L;
        var against = 0L;
        for (Vote voteEntity : subjectEntity.getVotes()) {
            if (voteEntity.getInFavor()) {
                inFavor++;
            } else {
                against++;
            }
        }

        resource.setInFavor(inFavor);
        resource.setAgainst(against);

        if (userVoteEntity != null) {
            resource.setYourVoteId(userVoteEntity.getId());
            resource.setLocked(userVoteEntity.getLocked());
        }

        return resource;
    }
}

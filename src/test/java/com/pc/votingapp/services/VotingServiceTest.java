package com.pc.votingapp.services;

import com.pc.votingapp.api.resources.SubjectResource;
import com.pc.votingapp.api.resources.VoteResource;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VotingServiceTest {

    private static final Long SUBJECT_ID = 123L;
    private static final Long VOTE_ID = 1234L;
    private static final String SUBJECT_TITLE = "title";
    private static final String USER_NAME = "username";

    @MockBean
    private SubjectRepository subjectRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private VoteRepository voteRepository;

    @Autowired
    private VotingService service;

    @Test
    public void getAllSubjectsRepositoryReturnsEmpty() {
        when(subjectRepository.findAll()).thenReturn(Collections.emptyList());
        var result = service.getAllSubjects();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void getAllSubjectsRepositoryReturnsList() {
        var subject = new Subject();
        subject.setTitle(SUBJECT_TITLE);
        when(subjectRepository.findAll()).thenReturn(List.of(subject, subject));
        var result = service.getAllSubjects();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(SUBJECT_TITLE, result.get(0).getTitle());
        assertEquals(SUBJECT_TITLE, result.get(1).getTitle());
    }

    @Test(expected = UserNotFoundException.class)
    public void createSubjectUserNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.empty());
        service.createSubject(new SubjectResource(), USER_NAME);
    }

    @Test
    public void createSubjectUserExists() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        doAnswer(invocation -> {
            Subject subject = invocation.getArgument(0);
            subject.setId(SUBJECT_ID);
            return null;
        }).when(subjectRepository).save(any());
        var result = service.createSubject(new SubjectResource(), USER_NAME);
        assertEquals(SUBJECT_ID, result);
    }

    @Test(expected = UserNotFoundException.class)
    public void voteForSubjectUserNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.empty());
        service.voteForSubject(new VoteResource(), USER_NAME, SUBJECT_ID);
    }

    @Test(expected = SubjectDoesNotExist.class)
    public void voteForSubjectSubjectNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());
        service.voteForSubject(new VoteResource(), USER_NAME, SUBJECT_ID);
    }

    @Test(expected = AlreadyVotedException.class)
    public void voteForSubjectAlreadyVoted() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(new Subject()));
        when(voteRepository.findByUserAndSubject(any(), any())).thenReturn(Optional.of(new Vote()));
        service.voteForSubject(new VoteResource(), USER_NAME, SUBJECT_ID);
    }

    @Test
    public void voteForSubjectVoteDoesNotExists() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(new Subject()));
        when(voteRepository.findByUserAndSubject(any(), any())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Vote vote = invocation.getArgument(0);
            vote.setId(VOTE_ID);
            return null;
        }).when(voteRepository).save(any());
        var result = service.voteForSubject(new VoteResource(), USER_NAME, SUBJECT_ID);
        assertEquals(VOTE_ID, result);
    }

    @Test(expected = UserNotFoundException.class)
    public void updateVoteUserNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.empty());
        service.updateVote(new VoteResource(), USER_NAME, SUBJECT_ID);
    }

    @Test(expected = SubjectDoesNotExist.class)
    public void updateVoteSubjectNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());
        service.updateVote(new VoteResource(), USER_NAME, SUBJECT_ID);
    }

    @Test(expected = NotVotedYetException.class)
    public void updateVoteAlreadyVoted() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(new Subject()));
        when(voteRepository.findByUserAndSubject(any(), any())).thenReturn(Optional.empty());
        service.updateVote(new VoteResource(), USER_NAME, SUBJECT_ID);
    }

    @Test(expected = VoteLockedException.class)
    public void updateVoteVoteIsLocked() {
        var vote = new Vote();
        vote.setLocked(true);
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(new Subject()));
        when(voteRepository.findByUserAndSubject(any(), any())).thenReturn(Optional.of(vote));
        var result = service.updateVote(new VoteResource(), USER_NAME, SUBJECT_ID);
        assertNotNull(result);
        assertFalse(result.getInFavor());
    }

    @Test
    public void updateVoteVoteIsInProperState() {
        var vote = new Vote();
        vote.setLocked(false);

        var voteResource = new VoteResource();
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(new Subject()));
        when(voteRepository.findByUserAndSubject(any(), any())).thenReturn(Optional.of(vote));
        var result = service.updateVote(voteResource, USER_NAME, SUBJECT_ID);
        assertNotNull(result);
        assertEquals(voteResource, result);
    }


    @Test(expected = UserNotFoundException.class)
    public void getVotingForSubjectUserNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.empty());
        service.getVotingForSubject(USER_NAME, SUBJECT_ID);
    }

    @Test(expected = SubjectDoesNotExist.class)
    public void getVotingForSubjectSubjectNotFound() {
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());
        service.getVotingForSubject(USER_NAME, SUBJECT_ID);
    }

    @Test
    public void getVotingForSubjectAllInProperState() {
        var subject = new Subject();
        subject.setVotes(Collections.emptySet());
        when(userRepository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        var result = service.getVotingForSubject(USER_NAME, SUBJECT_ID);
        assertNotNull(result);
    }
}


package com.pc.votingapp.api.controllers;

import com.pc.votingapp.api.resources.SubjectResource;
import com.pc.votingapp.api.resources.VoteResource;
import com.pc.votingapp.api.resources.VotingResource;
import com.pc.votingapp.exceptions.AlreadyVotedException;
import com.pc.votingapp.exceptions.SubjectDoesNotExist;
import com.pc.votingapp.exceptions.UserNotFoundException;
import com.pc.votingapp.exceptions.VoteLockedException;
import com.pc.votingapp.services.VotingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(VotingController.class)
public class VotingControllerTest {

    private static final String VALID_SUBJECT_REQUEST_BODY = "{\n"+
            "  \"title\": \"test\",\n"+
            "  \"description\": \"123\",\n"+
            "  \"votingStart\": \"2000-01-01\",\n"+
            "  \"votingEnd\": \"2000-01-16\"\n"+
            "}\n";
    private static final String INVALID_SUBJECT_REQUEST_BODY = "{\"title\": \"test\"}";
    private static final String VALID_VOTING_REQUEST_BODY = "{\"inFavor\": false}";
    private static final String INVALID_VOTING_REQUEST_BODY = "{}";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VotingService votingService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getSubjectsUserNotAuthenticated() throws Exception {
        mvc.perform(get("/api/subject")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username="test_user")
    public void getSubjectsUserAuthenticatedServiceReturnEmptyCollection() throws Exception {
        when(votingService.getAllSubjects()).thenReturn(Collections.emptyList());
        mvc.perform(get("/api/subject")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    @WithMockUser(username="test_user")
    public void getSubjectsUserAuthenticatedServiceReturnResults() throws Exception {
        var now = new Date();
        var resource = new SubjectResource();
        resource.setId(123L);
        resource.setTitle("title");
        resource.setDescription("desc");
        resource.setVotingStart(now);
        resource.setVotingEnd(now);

        when(votingService.getAllSubjects()).thenReturn(List.of(resource, resource));
        mvc.perform(get("/api/subject")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[1].id").value(123))
                .andExpect(jsonPath("$[1].title").value("title"));
    }

    @Test
    public void createSubjectUserNotAuthenticated() throws Exception {
        mvc.perform(post("/api/subject")
                .content(VALID_SUBJECT_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="test_user")
    public void createSubjectUserAuthenticatedInvalidRequest() throws Exception {
        var exception = mvc.perform(post("/api/subject")
                .content(INVALID_SUBJECT_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(MethodArgumentNotValidException.class));
    }

    @Test
    @WithMockUser(username="test_user")
    public void createSubjectUserAuthenticatedValidRequestServiceThrowsException() throws Exception {
        when(votingService.createSubject(any(), anyString())).thenThrow(new UserNotFoundException());
        var exception = mvc.perform(post("/api/subject")
                .content(VALID_SUBJECT_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(UserNotFoundException.class));
    }

    @Test
    @WithMockUser(username="test_user")
    public void createSubjectUserAuthenticatedValidRequestServiceReturns() throws Exception {
        when(votingService.createSubject(any(), anyString())).thenReturn(123L);
        var response = mvc.perform(post("/api/subject")
                .content(VALID_SUBJECT_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var location = response.getHeader("Location");
        assertNotNull(location);
        assertEquals("http://localhost/api/subject/123", location);
    }

    @Test
    public void getVotingUserNotAuthenticated() throws Exception {
        mvc.perform(get("/api/subject/123/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="test_user")
    public void getVotingUserAuthenticatedServiceThrowsException() throws Exception {
        when(votingService.getVotingForSubject(anyString(), anyLong())).thenThrow(new SubjectDoesNotExist());
        var exception = mvc.perform(get("/api/subject/123/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(SubjectDoesNotExist.class));
    }
    @Test
    @WithMockUser(username="test_user")
    public void getVotingUserAuthenticatedServiceReturns() throws Exception {
        var votingResource = new VotingResource();
        votingResource.setInFavor(1L);
        votingResource.setAgainst(2L);
        votingResource.setLocked(false);
        votingResource.setVoted(true);


        when(votingService.getVotingForSubject(anyString(), anyLong())).thenReturn(votingResource);
        mvc.perform(get("/api/subject/123/voting")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.inFavor").value(1L))
                .andExpect(jsonPath("$.against").value(2L))
                .andExpect(jsonPath("$.locked").value(false))
                .andExpect(jsonPath("$.voted").value(true));
    }

    @Test
    public void createVoteUserNotAuthenticated() throws Exception {
        mvc.perform(post("/api/subject/123/vote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="test_user")
    public void createVoteUserAuthenticatedInvalidRequest() throws Exception {
        var exception = mvc.perform(post("/api/subject/123/vote")
                .content(INVALID_VOTING_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(MethodArgumentNotValidException.class));
    }

    @Test
    @WithMockUser(username="test_user")
    public void createVoteUserAuthenticatedValidRequestServiceThrowsException() throws Exception {
        when(votingService.voteForSubject(any(), anyString(), anyLong())).thenThrow(new AlreadyVotedException());
        var exception = mvc.perform(post("/api/subject/123/vote")
                .content(VALID_VOTING_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(AlreadyVotedException.class));
    }

    @Test
    @WithMockUser(username="test_user")
    public void createVoteUserAuthenticatedValidRequestServiceReturns() throws Exception {
        when(votingService.voteForSubject(any(), anyString(), anyLong())).thenReturn(123L);
        var response = mvc.perform(post("/api/subject/123/vote")
                .content(VALID_VOTING_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var location = response.getHeader("Location");
        assertNotNull(location);
        assertEquals("http://localhost/api/subject/123/vote/123", location);
    }

    @Test
    public void updateVoteUserNotAuthenticated() throws Exception {
        mvc.perform(put("/api/subject/123/vote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="test_user")
    public void updateVoteUserAuthenticatedInvalidRequest() throws Exception {
        var exception = mvc.perform(put("/api/subject/123/vote")
                .content(INVALID_VOTING_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(MethodArgumentNotValidException.class));
    }

    @Test
    @WithMockUser(username="test_user")
    public void updateVoteUserAuthenticatedValidRequestServiceThrowsException() throws Exception {
        when(votingService.updateVote(any(), anyString(), anyLong())).thenThrow(new VoteLockedException());
        var exception = mvc.perform(put("/api/subject/123/vote")
                .content(VALID_VOTING_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(VoteLockedException.class));
    }

    @Test
    @WithMockUser(username="test_user")
    public void updateVoteUserAuthenticatedValidRequestServiceReturns() throws Exception {
        var resource = new VoteResource();
        resource.setInFavor(true);
        when(votingService.updateVote(any(), anyString(), anyLong())).thenReturn(resource);
        mvc.perform(put("/api/subject/123/vote")
                .content(VALID_VOTING_REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.inFavor").value(true));
    }
}

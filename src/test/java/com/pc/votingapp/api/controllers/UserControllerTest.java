package com.pc.votingapp.api.controllers;

import com.pc.votingapp.exceptions.UserAlreadyExistsException;
import com.pc.votingapp.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final String INVALID_REQUEST = "{\"login\":\"test\"}";
    private static final String VALID_REQUEST = "{\"login\":\"test\"," +
            "\"password\":\"123\"," +
            "\"firstName\":\"firstName\"," +
            "\"lastName\":\"lastName\"}";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    @Test
    public void createUserUsingInvalidDataInRequestBody() throws Exception {
        var exception = mvc.perform(post("/user")
                .content(INVALID_REQUEST)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(MethodArgumentNotValidException.class));
    }

    @Test
    public void createUserServiceThrowsException() throws Exception {
        when(service.createUser(any())).thenThrow(UserAlreadyExistsException.class);
        var exception = mvc.perform(post("/user")
                .content(VALID_REQUEST)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn().getResolvedException();
        assertThat(exception, instanceOf(UserAlreadyExistsException.class));
    }

    @Test
    public void createUserAllIsOk() throws Exception {
        when(service.createUser(any())).thenReturn(1L);
        var response = mvc.perform(post("/user")
                .content(VALID_REQUEST)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var location = response.getHeader("Location");
        assertNotNull(location);
        assertEquals("http://localhost/api/user/1", location);
    }
}

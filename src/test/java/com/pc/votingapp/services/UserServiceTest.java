package com.pc.votingapp.services;

import com.pc.votingapp.api.resources.UserResource;
import com.pc.votingapp.dao.entities.User;
import com.pc.votingapp.dao.repositories.UserRepository;
import com.pc.votingapp.exceptions.UserAlreadyExistsException;
import com.pc.votingapp.exceptions.UserNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    private static final String USER_NAME = "test_user";
    private static final String PASSWORD = "123";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";

    @MockBean
    private UserRepository repository;

    @MockBean
    private PasswordEncoder encoder;

    @Autowired
    private UserService service;

    @Test(expected = UserNotFoundException.class)
    public void loadUserByUsernameUserNotFound() {
        var username = "test_user";
        when(repository.findByLogin(username)).thenReturn(Optional.empty());
        service.loadUserByUsername(username);
    }

    @Test
    public void loadUserByUsernameUserFound() {
        var user = new User();
        user.setLogin(USER_NAME);
        user.setPassword(PASSWORD);

        when(repository.findByLogin(USER_NAME)).thenReturn(Optional.of(user));
        var result = service.loadUserByUsername(USER_NAME);
        assertEquals(USER_NAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void createUserUserAlreadyExists() {
        var userResource = new UserResource();
        userResource.setLogin(USER_NAME);
        userResource.setPassword(PASSWORD);
        userResource.setFirstName(FIRST_NAME);
        userResource.setLastName(LAST_NAME);

        when(repository.findByLogin(USER_NAME)).thenReturn(Optional.of(new User()));
        service.createUser(userResource);
    }

    @Test
    public void createUserUserDoesNotAlreadyExists() {
        var userId = 123L;
        var encodedPass = "!@#$";
        var userResource = new UserResource();
        userResource.setLogin(USER_NAME);
        userResource.setPassword(PASSWORD);
        userResource.setFirstName(FIRST_NAME);
        userResource.setLastName(LAST_NAME);

        var userEntity = new User();
        userEntity.setLogin(USER_NAME);

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(encodedPass, user.getPassword());
            user.setId(userId);
            return null;
        }).when(repository).save(any());

        when(repository.findByLogin(USER_NAME)).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn(encodedPass);

        service.createUser(userResource);

        assertEquals(123L, service.createUser(userResource).longValue());
    }
}

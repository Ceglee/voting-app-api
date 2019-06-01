package com.pc.votingapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="user name already exists")
public class UserAlreadyExistsException extends RuntimeException {
}

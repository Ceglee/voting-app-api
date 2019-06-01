package com.pc.votingapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="cannot update vote which has been created yet")
public class NotVotedYetException extends RuntimeException {
}

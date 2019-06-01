package com.pc.votingapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="cannot create vote which has been already created")
public class AlreadyVotedException extends RuntimeException {
}

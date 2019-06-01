package com.pc.votingapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="subject as not found for given id")
public class SubjectDoesNotExist extends RuntimeException {
}

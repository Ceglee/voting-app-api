package com.pc.votingapp.api.resources;

import com.pc.votingapp.api.validators.VotingPeriod;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@VotingPeriod(
        votingStart = "votingStart",
        votingEnd =  "votingEnd",
        message = "invalid votingStart or votingEnd parameters"
)
public class VoteSubjectResource {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @FutureOrPresent
    private String votingStart;

    @NotNull
    @FutureOrPresent
    private String votingEnd;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVotingStart() {
        return votingStart;
    }

    public void setVotingStart(String votingStart) {
        this.votingStart = votingStart;
    }

    public String getVotingEnd() {
        return votingEnd;
    }

    public void setVotingEnd(String votingEnd) {
        this.votingEnd = votingEnd;
    }
}

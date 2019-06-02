package com.pc.votingapp.api.resources;

import com.pc.votingapp.api.validators.VotingPeriod;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@VotingPeriod(
        votingStart = "votingStart",
        votingEnd =  "votingEnd",
        message = "Invalid votingStart or votingEnd parameters"
)
public class SubjectResource {

    @Null
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Date votingStart;

    @NotNull
    private Date votingEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getVotingStart() {
        return votingStart;
    }

    public void setVotingStart(Date votingStart) {
        this.votingStart = votingStart;
    }

    public Date getVotingEnd() {
        return votingEnd;
    }

    public void setVotingEnd(Date votingEnd) {
        this.votingEnd = votingEnd;
    }

}

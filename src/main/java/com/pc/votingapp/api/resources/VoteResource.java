package com.pc.votingapp.api.resources;

import javax.validation.constraints.NotNull;

public class VoteResource {

    @NotNull
    private Boolean inFavor;

    public Boolean getInFavor() {
        return inFavor;
    }

    public void setInFavor(Boolean inFavor) {
        this.inFavor = inFavor;
    }
}

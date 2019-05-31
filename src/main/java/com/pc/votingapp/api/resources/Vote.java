package com.pc.votingapp.api.resources;

import javax.validation.constraints.NotNull;

public class Vote {

    @NotNull
    private Long subjectId;

    @NotNull
    private Boolean inFavor;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Boolean getInFavor() {
        return inFavor;
    }

    public void setInFavor(Boolean inFavor) {
        this.inFavor = inFavor;
    }
}

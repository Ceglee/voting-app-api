package com.pc.votingapp.api.resources;

public class VotingResource {

    private Long inFavor;
    private Long against;
    private Long yourVoteId;
    private Boolean locked;

    public Long getInFavor() {
        return inFavor;
    }

    public void setInFavor(Long inFavor) {
        this.inFavor = inFavor;
    }

    public Long getAgainst() {
        return against;
    }

    public void setAgainst(Long against) {
        this.against = against;
    }

    public Long getYourVoteId() {
        return yourVoteId;
    }

    public void setYourVoteId(Long yourVoteId) {
        this.yourVoteId = yourVoteId;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}

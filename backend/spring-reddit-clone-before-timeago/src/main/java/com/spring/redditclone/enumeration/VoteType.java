package com.spring.redditclone.enumeration;

import com.spring.redditclone.exception.SpringRedditException;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private int direction;

    VoteType(int direction) {
    }

    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found by direction " + direction));
    }

    public Integer getDirection() {
        return direction;
    }
}

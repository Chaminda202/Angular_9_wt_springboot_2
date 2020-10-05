package com.spring.redditclone.repository;

import com.spring.redditclone.model.entity.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
}

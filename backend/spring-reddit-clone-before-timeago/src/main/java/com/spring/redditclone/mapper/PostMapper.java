package com.spring.redditclone.mapper;

import com.spring.redditclone.model.PostRequest;
import com.spring.redditclone.model.PostResponse;
import com.spring.redditclone.model.entity.Post;
import com.spring.redditclone.model.entity.Subreddit;
import com.spring.redditclone.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mappings({
            @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "description", source = "postRequest.description"),
            @Mapping(target = "subreddit", source = "subreddit"),
            @Mapping(target = "user", source = "user")
    })
   Post mapToPostEntity(PostRequest postRequest, Subreddit subreddit, User user);

    @Mappings({
            @Mapping(target = "id", source = "postId"),
            @Mapping(target = "userName", source = "user.username"),
            @Mapping(target = "subredditName", source = "subreddit.name")
    })
    PostResponse mapToPostResponseDto(Post post);
}

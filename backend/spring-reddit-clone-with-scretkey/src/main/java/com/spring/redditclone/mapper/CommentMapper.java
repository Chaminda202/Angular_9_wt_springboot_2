package com.spring.redditclone.mapper;

import com.spring.redditclone.model.CommentDto;
import com.spring.redditclone.model.entity.Comment;
import com.spring.redditclone.model.entity.Post;
import com.spring.redditclone.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "post", source = "post"),
            @Mapping(target = "user", source = "user")
    })
    Comment mapToCommentEntity(CommentDto commentDto, Post post, User user);

    @Mappings({
            @Mapping(target = "postId", source = "id"),
            @Mapping(target = "username", source = "user.username")
    })
    CommentDto mapToCommentDto(Comment comment);
}

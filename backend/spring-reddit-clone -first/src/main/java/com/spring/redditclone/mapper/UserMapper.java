package com.spring.redditclone.mapper;

import com.spring.redditclone.model.RegisterRequest;
import com.spring.redditclone.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    @Mapping(target = "created", expression = "java(java.time.Instant.now())")
    User mapToEntity(RegisterRequest RegisterRequest);
}

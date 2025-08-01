package org.Akorad.dto.user;

import org.Akorad.dto.security.RegisterRequest;
import org.Akorad.dto.response.UserResponse;
import org.Akorad.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);
}

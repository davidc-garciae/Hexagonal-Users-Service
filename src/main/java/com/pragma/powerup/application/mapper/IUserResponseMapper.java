package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.domain.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserResponseMapper {

  @Mapping(
      target = "role",
      expression = "java(user.getRole() != null ? user.getRole().name() : null)")
  UserResponseDto toResponse(UserModel user);
}

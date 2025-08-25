package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserEntityMapper {

  @Mapping(
      target = "role",
      expression = "java(user.getRole() != null ? user.getRole().name() : null)")
  UserEntity toEntity(UserModel user);

  @Mapping(
      target = "role",
      expression =
          "java(entity.getRole() != null ? com.pragma.powerup.domain.model.RoleEnum.valueOf(entity.getRole()) : null)")
  UserModel toDomain(UserEntity entity);
}

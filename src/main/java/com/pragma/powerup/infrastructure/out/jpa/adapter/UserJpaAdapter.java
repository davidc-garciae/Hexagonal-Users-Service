package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

  private final IUserRepository userRepository;
  private final IUserEntityMapper userEntityMapper;

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public boolean existsByDocument(String document) {
    return userRepository.existsByDocument(document);
  }

  @Override
  public UserModel save(UserModel user) {
    UserEntity entity = userEntityMapper.toEntity(user);
    UserEntity saved = userRepository.save(entity);
    return userEntityMapper.toDomain(saved);
  }

  @Override
  public UserModel findByEmail(String email) {
    return userRepository.findByEmail(email).map(userEntityMapper::toDomain).orElse(null);
  }
}

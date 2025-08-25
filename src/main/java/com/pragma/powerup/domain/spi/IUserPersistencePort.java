package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserPersistencePort {
  boolean existsByEmail(String email);

  boolean existsByDocument(String document);

  UserModel save(UserModel user);

  UserModel findByEmail(String email);
}

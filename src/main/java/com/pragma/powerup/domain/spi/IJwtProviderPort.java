package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface IJwtProviderPort {
  String generateToken(UserModel user);

  long getExpirationMs();
}

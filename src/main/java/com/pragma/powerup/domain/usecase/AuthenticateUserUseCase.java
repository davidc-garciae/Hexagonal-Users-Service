package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

public class AuthenticateUserUseCase implements IAuthServicePort {

  private final IUserPersistencePort userPersistencePort;
  private final IPasswordEncoderPort passwordEncoderPort;

  public AuthenticateUserUseCase(
      IUserPersistencePort userPersistencePort, IPasswordEncoderPort passwordEncoderPort) {
    this.userPersistencePort = userPersistencePort;
    this.passwordEncoderPort = passwordEncoderPort;
  }

  @Override
  public UserModel authenticate(String email, String password) {
    if (email == null || password == null) {
      throw new DomainException("Email and password are required");
    }

    UserModel user = userPersistencePort.findByEmail(email);
    if (user == null) {
      throw new DomainException("Invalid credentials");
    }

    if (!Boolean.TRUE.equals(user.getActive())) {
      throw new DomainException("User is not active");
    }

    if (!passwordEncoderPort.matches(password, user.getPassword())) {
      throw new DomainException("Invalid credentials");
    }

    return user;
  }
}

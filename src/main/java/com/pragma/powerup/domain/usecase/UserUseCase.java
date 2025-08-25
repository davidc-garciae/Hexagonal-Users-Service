package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

public class UserUseCase implements IUserServicePort {

  private final CreateOwnerUseCase createOwnerUseCase;

  public UserUseCase(
      IUserPersistencePort userPersistencePort,
      IPasswordEncoderPort passwordEncoderPort,
      IDateProviderPort dateProviderPort) {
    this.createOwnerUseCase =
        new CreateOwnerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
  }

  @Override
  public UserModel createOwner(UserModel user) {
    return createOwnerUseCase.createOwner(user);
  }
}

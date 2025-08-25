package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.time.LocalDate;
import java.time.Period;

public class CreateOwnerUseCase {

  private final IUserPersistencePort userPersistencePort;
  private final IPasswordEncoderPort passwordEncoderPort;
  private final IDateProviderPort dateProviderPort;

  public CreateOwnerUseCase(
      IUserPersistencePort userPersistencePort,
      IPasswordEncoderPort passwordEncoderPort,
      IDateProviderPort dateProviderPort) {
    this.userPersistencePort = userPersistencePort;
    this.passwordEncoderPort = passwordEncoderPort;
    this.dateProviderPort = dateProviderPort;
  }

  public UserModel createOwner(UserModel request) {
    validateFields(request);

    if (Boolean.TRUE.equals(userPersistencePort.existsByEmail(request.getEmail()))) {
      throw new DomainException("Email already registered");
    }
    if (Boolean.TRUE.equals(userPersistencePort.existsByDocument(request.getDocument()))) {
      throw new DomainException("Document already registered");
    }

    String encoded = passwordEncoderPort.encode(request.getPassword());
    request.setPassword(encoded);
    request.setRole(RoleEnum.OWNER);
    request.setActive(true);
    return userPersistencePort.save(request);
  }

  private void validateFields(UserModel u) {
    if (u.getEmail() == null || !u.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new DomainException("Invalid email");
    }
    if (u.getPhone() == null
        || u.getPhone().length() > 13
        || !u.getPhone().matches("^\\+?[0-9]{1,13}$")) {
      throw new DomainException("Invalid phone number");
    }
    if (u.getDocument() == null || !u.getDocument().matches("^[0-9]+$")) {
      throw new DomainException("Invalid document");
    }
    LocalDate today = dateProviderPort.today();
    if (u.getBirthDate() == null || Period.between(u.getBirthDate(), today).getYears() < 18) {
      throw new DomainException("User must be of legal age");
    }
  }
}

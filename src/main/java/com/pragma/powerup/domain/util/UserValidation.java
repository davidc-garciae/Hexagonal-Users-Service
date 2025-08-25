package com.pragma.powerup.domain.util;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import java.time.LocalDate;
import java.time.Period;

public final class UserValidation {

  private UserValidation() {}

  public static void validateCommonFields(UserModel user, IDateProviderPort dateProviderPort) {
    if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new DomainException("Invalid email");
    }
    if (user.getPhone() == null
        || user.getPhone().length() > 13
        || !user.getPhone().matches("^\\+?[0-9]{1,13}$")) {
      throw new DomainException("Invalid phone number");
    }
    if (user.getDocument() == null || !user.getDocument().matches("^[0-9]+$")) {
      throw new DomainException("Invalid document");
    }
    LocalDate today = dateProviderPort.today();
    if (user.getBirthDate() == null || Period.between(user.getBirthDate(), today).getYears() < 18) {
      throw new DomainException("User must be of legal age");
    }
  }
}

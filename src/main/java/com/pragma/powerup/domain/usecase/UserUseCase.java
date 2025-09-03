package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

public class UserUseCase implements IUserServicePort {

  private final CreateOwnerUseCase createOwnerUseCase;
  private final CreateEmployeeUseCase createEmployeeUseCase;
  private final CreateCustomerUseCase createCustomerUseCase;
  private final IUserPersistencePort userPersistencePort;

  public UserUseCase(
      IUserPersistencePort userPersistencePort,
      IPasswordEncoderPort passwordEncoderPort,
      IDateProviderPort dateProviderPort) {
    this.userPersistencePort = userPersistencePort;
    this.createOwnerUseCase =
        new CreateOwnerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
    this.createEmployeeUseCase =
        new CreateEmployeeUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
    this.createCustomerUseCase =
        new CreateCustomerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
  }

  @Override
  public UserModel createOwner(UserModel user) {
    return createOwnerUseCase.createOwner(user);
  }

  @Override
  public UserModel createEmployee(UserModel user) {
    return createEmployeeUseCase.createEmployee(user);
  }

  @Override
  public UserModel createCustomer(UserModel user) {
    return createCustomerUseCase.createCustomer(user);
  }

  @Override
  public UserModel getUserById(Long id) {
    return userPersistencePort.findById(id);
  }

  @Override
  public boolean isEmployeeOfRestaurant(Long userId, Long restaurantId) {
    UserModel user = userPersistencePort.findById(userId);
    if (user == null) {
      return false;
    }

    // Un usuario es empleado de un restaurante si:
    // 1. Tiene el rol EMPLOYEE
    // 2. Su restaurantId coincide con el restaurantId solicitado
    return user.getRole() == com.pragma.powerup.domain.model.RoleEnum.EMPLOYEE
        && restaurantId != null
        && restaurantId.equals(user.getRestaurantId());
  }
}

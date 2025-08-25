package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserServicePort {
  UserModel createOwner(UserModel user);

  UserModel createEmployee(UserModel user);

  UserModel createCustomer(UserModel user);
}

package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;

public interface IAuthServicePort {

  UserModel authenticate(String email, String password);
}

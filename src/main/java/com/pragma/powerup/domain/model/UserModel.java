package com.pragma.powerup.domain.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModel {
  private Long id;
  private String firstName;
  private String lastName;
  private String document; // only digits
  private String phone; // max 13, may include +
  private LocalDate birthDate; // must be adult
  private String email;
  private String password; // encrypted
  private RoleEnum role;
  private Boolean active = Boolean.TRUE;
  private Long restaurantId; // only for employee
}

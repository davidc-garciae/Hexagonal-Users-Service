package com.pragma.powerup.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmployeeRequestDto {
  @NotBlank private String firstName;
  @NotBlank private String lastName;

  @NotBlank
  @Pattern(regexp = "^[0-9]+$", message = "Document must be numeric")
  private String document;

  @NotBlank
  @Size(max = 13)
  @Pattern(regexp = "^\\+?[0-9]{1,13}$", message = "Invalid phone")
  private String phone;

  @NotNull private LocalDate birthDate;
  @NotBlank @Email private String email;
  @NotBlank private String password;

  @NotNull private Long restaurantId;
}

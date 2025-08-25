package com.pragma.powerup.domain.spi;

import java.time.LocalDate;

public interface IDateProviderPort {
  LocalDate today();
}

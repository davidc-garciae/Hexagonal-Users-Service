package com.pragma.powerup.domain.spi;

public interface IPasswordEncoderPort {
  String encode(String raw);

  boolean matches(String raw, String encoded);
}

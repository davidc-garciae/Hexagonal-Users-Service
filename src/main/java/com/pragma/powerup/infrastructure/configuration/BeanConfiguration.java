package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.AuthenticateUserUseCase;
import com.pragma.powerup.domain.usecase.UserUseCase;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfiguration {

  @Bean
  public IUserPersistencePort userPersistencePort(
      IUserRepository userRepository, IUserEntityMapper userEntityMapper) {
    return new UserJpaAdapter(userRepository, userEntityMapper);
  }

  @Bean
  public IPasswordEncoderPort passwordEncoderPort() {
    return new IPasswordEncoderPort() {
      private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

      @Override
      public String encode(String raw) {
        return delegate.encode(raw);
      }

      @Override
      public boolean matches(String raw, String encoded) {
        return delegate.matches(raw, encoded);
      }
    };
  }

  @Bean
  public IDateProviderPort dateProviderPort() {
    return java.time.LocalDate::now;
  }

  @Bean
  public IUserServicePort usuarioServicePort(
      IUserPersistencePort userPersistencePort,
      IPasswordEncoderPort passwordEncoderPort,
      IDateProviderPort dateProviderPort) {
    return new UserUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
  }

  @Bean
  public IAuthServicePort authServicePort(
      IUserPersistencePort userPersistencePort, IPasswordEncoderPort passwordEncoderPort) {
    return new AuthenticateUserUseCase(userPersistencePort, passwordEncoderPort);
  }
}

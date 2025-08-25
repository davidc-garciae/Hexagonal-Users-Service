package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.util.UserValidation;

public class CreateEmployeeUseCase {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IDateProviderPort dateProviderPort;

    public CreateEmployeeUseCase(
            IUserPersistencePort userPersistencePort,
            IPasswordEncoderPort passwordEncoderPort,
            IDateProviderPort dateProviderPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.dateProviderPort = dateProviderPort;
    }

    public UserModel createEmployee(UserModel request) {
        UserValidation.validateCommonFields(request, dateProviderPort);
        validateFields(request);

        if (userPersistencePort.existsByEmail(request.getEmail())) {
            throw new DomainException("Email already registered");
        }
        if (userPersistencePort.existsByDocument(request.getDocument())) {
            throw new DomainException("Document already registered");
        }

        String encoded = passwordEncoderPort.encode(request.getPassword());
        request.setPassword(encoded);
        request.setRole(RoleEnum.EMPLOYEE);
        request.setActive(true);
        return userPersistencePort.save(request);
    }

    private void validateFields(UserModel u) {
        if (u.getRestaurantId() == null) {
            throw new DomainException("Restaurant is required for employee");
        }
    }
}

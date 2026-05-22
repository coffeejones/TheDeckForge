package org.example.thedeckforge.service;

import org.example.thedeckforge.validation.ValidationStrategy;
import org.example.thedeckforge.validation.ValidationType;
import org.example.thedeckforge.entity.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ValidationService {
    private final Map<String, ValidationStrategy> validatorMap;

    public ValidationService(Map<String, ValidationStrategy> validatorMap) {
        this.validatorMap = validatorMap;
    }

    public void validate(ValidationType type, Object object) throws ValidationException {
        ValidationStrategy validator = validatorMap.get(type.name());
        if (validator == null) {
            throw new ValidationException("No validate found for type" + type);
        }
        validator.validate(object);
    }
}

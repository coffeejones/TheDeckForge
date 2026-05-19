package org.example.thedeckforge.validation;

import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.Roles;
import org.example.thedeckforge.validation.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class UserValidation implements ValidationStrategy{
    @Override
    public ValidationType getValidationType() {
        return ValidationType.USER;
    }

    @Override
    public void validate(Object object) {
        if(!(object instanceof User user)){
            throw new ValidationException("Invalid Validation Type");
        }
        if(!(user.getAuthority().getRole() == Roles.MEMBER || user.getAuthority().getRole() == Roles.ORGANIZER || user.getAuthority().getRole() == Roles.ADMIN)){
            throw new ValidationException("Invalid User");
        }
    }
}

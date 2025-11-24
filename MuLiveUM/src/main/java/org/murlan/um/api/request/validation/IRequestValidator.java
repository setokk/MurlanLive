package org.murlan.um.api.request.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class IRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return IRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        IRequest request = (IRequest) target;
        try {
            request.validate();
        } catch (IllegalArgumentException ex) {
            errors.reject("invalid.request", ex.getMessage());
        }
    }
}

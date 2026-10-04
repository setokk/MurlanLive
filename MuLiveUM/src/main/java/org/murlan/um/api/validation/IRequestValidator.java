package org.murlan.um.api.validation;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.murlan.um.error.BusinessLogicException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class IRequestValidator implements org.springframework.validation.Validator {

    private final jakarta.validation.Validator beanValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return IRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        IRequest request = (IRequest) target;

        // Jakarta Bean Validation
        Set<ConstraintViolation<Object>> violations = beanValidator.validate(target);

        for (ConstraintViolation<Object> violation : violations) {
            errors.rejectValue(
                    violation.getPropertyPath().toString(),
                    "invalid.request",
                    violation.getMessage()
            );
        }

        if (!violations.isEmpty()) {
            return;
        }

        // MuLive custom validation
        try {
            request.validate();
        } catch (BusinessLogicException ex) {
            for (String errorMessage : ex.getErrorMessages()) {
                errors.reject(
                        "invalid.request",
                        errorMessage
                );
            }
        }
    }
}

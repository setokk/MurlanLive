package org.murlan.um.config;

import org.murlan.um.api.validation.IRequestValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ValidationConfig implements WebMvcConfigurer {
    private final IRequestValidator requestValidator;

    public ValidationConfig(IRequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    @Override
    public Validator getValidator() {
        return requestValidator;
    }
}

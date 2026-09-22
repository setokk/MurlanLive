package org.murlan.um.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailTemplateService {
    public static final String FORGOT_PASSWORD = "forgot-password";

    private final Map<String, String> templates = new HashMap<>();

    @PostConstruct
    private void loadTemplates() throws IOException {
        templates.put(FORGOT_PASSWORD, load("email/forgot-password.html"));
    }

    private String load(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

    public String render(String template, Map<String, String> variables) {
        String html = templates.get(template);
        if (html == null) {
            throw new IllegalArgumentException("Unknown email template: " + template);
        }

        for (var entry : variables.entrySet()) {
            html = html.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
            );
        }

        return html;
    }
}

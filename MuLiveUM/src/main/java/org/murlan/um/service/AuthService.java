package org.murlan.um.service;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.error.BusinessLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public PlayerDto getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof PlayerDto))
            throw new BusinessLogicException(HttpStatus.FORBIDDEN, "No JWT player found");
        return (PlayerDto) authentication.getPrincipal();
    }
}

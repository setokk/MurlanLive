package org.murlan.um.service.mapper;

import org.murlan.um.api.request.LoginPlayerRequest;
import org.murlan.um.api.request.RegisterPlayerRequest;
import org.murlan.um.service.param.player.LoginPlayerParam;
import org.murlan.um.service.param.player.RegisterPlayerParam;
import org.springframework.stereotype.Component;

@Component
public final class PlayerMapper {
    public LoginPlayerParam toParam(LoginPlayerRequest request) {
        return new LoginPlayerParam(request.getUsername(), request.getPassword());
    }

    public RegisterPlayerParam toParam(RegisterPlayerRequest request) {
        return new RegisterPlayerParam(request.getUsername(), request.getPassword());
    }
}

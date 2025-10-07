package org.murlan.live.protocol.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;

@Getter
@Setter
@AllArgsConstructor
public class GenericErrorResp implements Resp {
    private String errorMessage;

    @Override
    public ResponseStatus getResponseStatus() {
        return ResponseStatus.ERROR;
    }

    @Override
    public String convertToString(ProtocolConfig config) {
        return String.join(config.getProtocol_delimiter(), "-1", errorMessage);
    }
}

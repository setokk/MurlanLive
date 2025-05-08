package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.message.Req;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
public class Parser {
    private final ProtocolConfig config;

    public Req parse(ClientEvent clientEvent, String message) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String[] messageParts = message.split(config.getProtocol_delimiter());
        Constructor<?> constructor = clientEvent.getRequest().getClass().getDeclaredConstructor(String[].class);
        Req request = (Req) constructor.newInstance(messageParts);
        request.setJWT(messageParts[0]);
        return request;
    }
}

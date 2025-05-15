package org.murlan.live.protocol;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.config.ConfigProvider;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.game.deck.Card;
import org.murlan.live.protocol.message.PlayHandReq;
import org.murlan.live.protocol.message.Req;

public class ParserTest {
    private ProtocolConfig config;
    private Parser parser;

    @Before
    public void setUp() {
        config = ConfigProvider.getProtocolConfig();
        parser = new Parser(config);
    }

    @Test
    public void testParser() {
        String playHandMessage =
                ClientEvent.PLAY_HAND.ordinal() + config.getProtocol_delimiter() +
                        "j2LVOAdsaclbmcvb"      + config.getProtocol_delimiter() +
                        Card.ACE_OF_CLUBS.ordinal();

        Req request = parser.parse(playHandMessage);
        Assert.assertTrue(request instanceof PlayHandReq);
        PlayHandReq playHandRequest = (PlayHandReq) request;
        Assert.assertEquals(Card.ACE_OF_CLUBS, playHandRequest.getCardCombination().getCards().getFirst());
    }
}

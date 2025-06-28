package org.murlan.live.protocol;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.config.ConfigProvider;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.logic.MovePipeline;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.Req;

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

    @Test
    public void testParserMovePipeline() {
        String playHandMessage =
                ClientEvent.PLAY_HAND.ordinal() + config.getProtocol_delimiter() +
                        "j2LVOAdsaclbmcvb"      + config.getProtocol_delimiter() +
                        Card.KING_OF_HEARTS.ordinal() + config.getProtocol_card_delimiter() +
                        Card.KING_OF_DIAMONDS.ordinal();

        Req request = parser.parse(playHandMessage);
        Assert.assertTrue(request instanceof PlayHandReq);

        PlayHandReq playHandRequest = (PlayHandReq) request;
        boolean isMoveValid = MovePipeline.validateMove(playHandRequest.getCardCombination());
        Assert.assertTrue(isMoveValid);
        Assert.assertEquals(CardCombinationType.DOUBLE_CARDS, playHandRequest.getCardCombination().getType());
    }
}

package org.murlan.live.protocol;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.logic.MovePipeline;
import org.murlan.live.protocol.api.ChatReq;
import org.murlan.live.protocol.api.ChatResp;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.util.Parser;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    private static ProtocolConfig config;
    private static Parser parser;

    @BeforeAll
    public static void setUp() {
        config = ConfigProvider.getProtocolConfig();
        parser = new Parser(config);
    }

    @Test
    public void testParser_PlayHand() throws InvalidDataException {
        String playHandMessage = ClientEvent.PLAY_HAND.id() + config.getProtocol_delimiter() + Card.ACE_OF_CLUBS.ordinal();

        Req request = parser.parse(playHandMessage);
        assertInstanceOf(PlayHandReq.class, request);
        assertEquals(Card.ACE_OF_CLUBS, ((PlayHandReq) request).getCardCombination().getCards().getFirst());
    }

    @ParameterizedTest
    @MethodSource("escapeCharacterMessages")
    public void testParser_EscapeCharacters(String originalMessage) throws InvalidDataException {
        String chatMessage = ClientEvent.CHAT.id()
                + config.getProtocol_delimiter()
                + new ChatResp(null).escape(originalMessage, config);

        Req request = parser.parse(chatMessage);
        assertInstanceOf(ChatReq.class, request);
        assertEquals(originalMessage, ((ChatReq) request).getMessage());
    }

    private static Stream<String> escapeCharacterMessages() {
        return Stream.of(
                """
                Hello players! How are you? You should get 5 backwards slashes here: \\\\\\\\\\! While also getting a dollar $!
                """,
                """
                Somehow I ended up in this situation man, \\
                """,
                "I want to put 2 dollars side by side! $$, I should be able to do this."
        );
        }

    @Test
    public void testParserMovePipeline() throws InvalidDataException {
        String playHandMessage = ClientEvent.PLAY_HAND.id() + config.getProtocol_delimiter() +
                Card.KING_OF_HEARTS.ordinal() + config.getProtocol_list_delimiter() +
                Card.KING_OF_DIAMONDS.ordinal();

        Req request = parser.parse(playHandMessage);
        assertInstanceOf(PlayHandReq.class, request);

        PlayHandReq playHandRequest = (PlayHandReq) request;
        boolean isMoveValid = MovePipeline.validate(playHandRequest.getCardCombination());
        assertTrue(isMoveValid);
        assertEquals(CardCombinationType.DOUBLE_CARDS, playHandRequest.getCardCombination().getType());
    }
}

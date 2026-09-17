package org.murlan.live.game.logic.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Rank;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.GiveCardResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.api.InformPlayHandResp;
import org.murlan.live.protocol.api.PassResp;
import org.murlan.live.protocol.api.PlayHandResp;
import org.murlan.live.protocol.dto.Player;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class OnTurnTimeout implements Consumer<GameState> {
    private final Room room;
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;

    @Override
    public void accept(GameState gameState) {
        synchronized (room) {
            List<PlayerSession> playersInRoom = roomHandler.getPlayersInRoom(room.getId());
            if (gameState.getState() == GameState.State.PLAYING) {
                handlePlayingState(gameState, playersInRoom);
            } else if (gameState.getState() == GameState.State.GIVING_CARDS) {
                handleGivingCardsState(gameState, playersInRoom);
            }
        }
    }

    private void handlePlayingState(GameState gameState, List<PlayerSession> playersInRoom) {
        try {
            Player currTurnPlayer = gameState.getCurrTurnPlayer();
            PlayerSession currTurnPlayerSession = findPlayerSession(currTurnPlayer, playersInRoom);

            if (gameState.isFirstMove() && gameState.isShouldCurrTurnPlayerUseThreeOfSpades()) {
                CardCombination playedCardCombination = new CardCombination(Card.THREE_OF_SPADES);
                gameState.playHand(currTurnPlayer, playedCardCombination);

                endpointHelper.send(new PlayHandResp(ResponseStatus.OK, playedCardCombination), currTurnPlayerSession);
                endpointHelper.informPlayers(new InformPlayHandResp(
                        ResponseStatus.OK,
                        currTurnPlayer.getId(),
                        playedCardCombination
                ), currTurnPlayerSession, playersInRoom);
            } else {
                gameState.pass(currTurnPlayer, true);

                boolean canCurrPlayerPlayAnyHand = gameState.getPassCounter().getCounter() == 0;
                endpointHelper.send(new PassResp(ResponseStatus.OK), currTurnPlayerSession);
                endpointHelper.informPlayers(new InformPassResp(
                        ResponseStatus.OK,
                        currTurnPlayer.getId(),
                        canCurrPlayerPlayAnyHand
                ), null, roomHandler.getPlayersInRoom(room.getId()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGivingCardsState(GameState gameState, List<PlayerSession> playersInRoom) {
        InformGiveCardResp informGiveCardResp = new InformGiveCardResp(ResponseStatus.OK,
                -1, -1, null,
                room.getActiveGameState().haveBothPlayersGivenCards()
        );

        if (!gameState.getGivenCards().contains(gameState.getPrevLoser())) {
            Card highestCard = gameState.getPrevLoser().getHand().getCards()
                    .stream()
                    .max(new Card.CardComparator())
                    .orElseThrow(() -> new IllegalStateException("No max card found"));

            gameState.giveCard(highestCard, gameState.getPrevLoser(), gameState.getPrevWinner());

            updateAndInformPlayersForGiveCard(informGiveCardResp,
                    playersInRoom,
                    gameState.getPrevLoser(), gameState.getPrevWinner(),
                    highestCard,
                    gameState.haveBothPlayersGivenCards());
        }

        if (!gameState.getGivenCards().contains(gameState.getPrevWinner())) {
            Card randomCardLessOrEqualThan10 = gameState.getPrevWinner().getHand().getCards()
                    .stream()
                    .filter(c -> c.hasSmallerOrEqualRank(Rank.TEN))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            cards -> {
                                if (cards.isEmpty()) {
                                    throw new IllegalStateException("No card with rank <= 10 found");
                                }
                                return cards.get(ThreadLocalRandom.current().nextInt(cards.size()));
                            }
                    ));

            gameState.giveCard(randomCardLessOrEqualThan10, gameState.getPrevWinner(), gameState.getPrevLoser());

            updateAndInformPlayersForGiveCard(informGiveCardResp,
                    playersInRoom,
                    gameState.getPrevWinner(), gameState.getPrevLoser(),
                    randomCardLessOrEqualThan10,
                    gameState.haveBothPlayersGivenCards());
        }
    }

    private void updateAndInformPlayersForGiveCard(InformGiveCardResp informGiveCardResp,
                                                   List<PlayerSession> playersInRoom,
                                                   Player originPlayer, Player targetPlayer,
                                                   Card card, boolean haveBothPlayersGivenCards) {
        informGiveCardResp.setOriginPlayerId(originPlayer.getId());
        informGiveCardResp.setTargetPlayerId(targetPlayer.getId());
        informGiveCardResp.setCard(card);
        informGiveCardResp.haveBothPlayersGivenCards(haveBothPlayersGivenCards);

        PlayerSession originPlayerSession = findPlayerSession(originPlayer, playersInRoom);
        try {
            endpointHelper.send(new GiveCardResp(ResponseStatus.OK, haveBothPlayersGivenCards, card), originPlayerSession);
            endpointHelper.informPlayers(informGiveCardResp, originPlayerSession, playersInRoom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PlayerSession findPlayerSession(Player player, List<PlayerSession> playersInRoom) {
        return playersInRoom.stream()
                .filter(ps -> ps.getPlayer().equals(player))
                .findAny()
                .orElseThrow();
    }
}

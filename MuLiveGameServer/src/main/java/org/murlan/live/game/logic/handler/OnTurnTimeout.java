package org.murlan.live.game.logic.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.Rank;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
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
            if (gameState.getState() == GameState.State.PLAYING) {
                try {
                    Player currTurnPlayer = gameState.getCurrTurnPlayer();
                    gameState.pass(currTurnPlayer);

                    boolean canCurrPlayerPlayAnyHand = gameState.getPassCounter().getCounter() == 0;
                    endpointHelper.informPlayers(new InformPassResp(
                            ResponseStatus.OK,
                            currTurnPlayer.getId(),
                            canCurrPlayerPlayAnyHand
                    ), null, roomHandler.getPlayersInRoom(room.getId()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (gameState.getState() == GameState.State.GIVING_CARDS) {
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
                            roomHandler.getPlayersInRoom(room.getId()),
                            gameState.getPrevLoser(), gameState.getPrevWinner(),
                            highestCard);
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
                            roomHandler.getPlayersInRoom(room.getId()),
                            gameState.getPrevWinner(), gameState.getPrevLoser(),
                            randomCardLessOrEqualThan10);
                }
            }
        }
    }

    private void updateAndInformPlayersForGiveCard(InformGiveCardResp informGiveCardResp,
                                                   List<PlayerSession> playersInRoom,
                                                   Player originPlayer, Player targetPlayer,
                                                   Card card) {
        informGiveCardResp.setOriginPlayerId(originPlayer.getId());
        informGiveCardResp.setTargetPlayerId(targetPlayer.getId());
        informGiveCardResp.setCard(card);

        PlayerSession originPlayerSession = playersInRoom.stream()
                .filter(ps -> ps.getPlayer().equals(originPlayer))
                .findAny()
                .orElseThrow();
        try {
            endpointHelper.informPlayers(informGiveCardResp, originPlayerSession, playersInRoom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

# [GameServer]: WebSocket Protocol Contract Documentation
This is the documentation for the MurlanLive Game Server. In the following sections, we will explain how to properly use the Game Server API Contract and what messages and events the clients/consumers should expect for achieving proper and correct communication.

### Contents
- [**Symbol Table**](#symbol-table)
- [**Connection and Authentication**](#connection-and-authentication)
- [**Client Events**](#client-events)

### Symbol Table
Symbol | Description
--- | ---
$ | Indicates the delimiter (see *protocol_delimiter* in [protocol_config.yml](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/resources/org/murlan/live/protocol-config.yml)).
_ | Indicates the list delimiter (see *protocol_list_delimiter* in [protocol_config.yml](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/resources/org/murlan/live/protocol-config.yml)).
card_combination | A combination of card ordinals (see [Card.java](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/game/deck/Card.java) and [CardCombination.java](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/game/deck/CardCombination.java)).
response_status | Indicates if a client event message was successful/valid (200=OK, 999=ERROR).

## Connection and Authentication
Before a client can send/receive any events, they should first connect and authenticate themselves. The authentication is performed through the JWT mechanism (JSON Web Tokens) via REST calls to the UMS (User Management Service). The client can then use that JWT token in a query parameter when initially connecting to the game server like so:

```wss://<gameserver_url>:<gameserver_port>?jwt=<jwt_token>```

If the authentication is successful, the connection will proceed normally. If the JWT is invalid or non-existent, the game server will send an error message and the connection will automatically close.

## Client Events
**Client events** are events that the client *(in our case Godot UI)* sends to the game server in order to receive/send information useful about the game (find available rooms, join a room, play their hand etc.). Every client **must** recognize the responses to their request by a **Client Event ID**, since it is based on WebSockets (event-driven architecture).

**Note:** client events **should not** be confused with normal HTTP requests. A client **must not** wait for a response in the sense of a HTTP call. The responses are **asynchronous**, and that's why the **Client Event ID** is always sent back to the clients along with the actual response.

### Client Event Messages Table
ID | Action | Body | Message Examples
--- | --- | --- | ---
C0 | GAME_STATE | N/A | "C0"
C1 | PLAY_HAND |<card_combination> | "C1$5_7", "C1$0_4_8_12_16", "C1$52"
C2 | PASS | N/A | "C2"
C3 | SURRENDER | N/A | "C3"
C4 | AVAILABLE_ROOMS | N/A | "C4"
C5 | JOIN_ROOM | <room_id>$<passcode> | "C5$3eyu548-32423h-aflcr$apassword5"
C6 | CREATE_ROOM | <room_name>$<is_public>$<passcode> | "C6$Room 1$true$doesntMatter", "C6$MyRoom$false$passcodeMATTERSHere"

*For more information, see [ClientEvent.java](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/protocol/ClientEvent.java).*

### Client Event Responses Table
ID | Action | Body | Response Examples
--- | --- | --- | ---
C0 | GAME_STATE | <response_status>$<[GameStateDto(JSON)](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/protocol/dto/GameStateDto.java)> | "C0$200${"status":0,"currTurnPlayer":null,"players":[],"currCardCombination":"2_3","deck":"52_53_1_5_6"}"
C1 | PLAY_HAND |<response_status> | "C1$200", "C1$999"
C2 | PASS | <response_status> | "C2$200", "C2$999"
C3 | SURRENDER | <response_status> | "C3$200", "C3$999"
C4 | AVAILABLE_ROOMS | <response_status>$Array::<[RoomDto(JSON)](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/protocol/dto/RoomDto.java)>" | "C4$200$[{"id":"fadwpf-wu8j-57cx","name":"Room 5","numPlayers":3}]"
C5 | JOIN_ROOM | <response_status> | "C5$200", "C5$999"
C6 | CREATE_ROOM | <response_status>$[RoomDto(JSON)](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/protocol/dto/RoomDto.java) | "C6$200${"id":"t3mwknt3i-3tw3tj-3twt","name":"Room 1","numPlayers":1}"

*For more information, see the relevant [Resp.java](https://github.com/setokk/MurlanLive/blob/main/MuLiveGameServer/src/main/java/org/murlan/live/protocol/api/Resp.java) classes.*
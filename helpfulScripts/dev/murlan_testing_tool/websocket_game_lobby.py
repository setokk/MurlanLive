import websocket

WS_URL = "ws://localhost:45600/game-lobby"


def connect_user(user: dict):
    username = user["user"]["username"]
    jwt = user["jwt"]

    ws = websocket.create_connection(
        f"{WS_URL}?jwt={jwt}"
    )

    print(f"[{username}] WebSocket connected")

    return ws

def check_game_start(user_table: list, timeout: float = 1.0) -> None:
    print("\nChecking for INFORM_GAME_START responses...")

    for user in user_table:
        username = user["user"]["username"]
        ws = user["ws"]

        old_timeout = ws.gettimeout()
        ws.settimeout(timeout)

        game_started = False

        try:
            while True:
                response = ws.recv()

                if not response:
                    break

                print(f"[{username}] Received: {response}")

                if response.startswith("S3"):
                    print(f"[{username}] GAME START EVENT RECEIVED")
                    game_started = True
                    break

        except websocket.WebSocketTimeoutException:
            pass

        except websocket.WebSocketConnectionClosedException:
            print(f"[{username}] WebSocket connection closed")

        finally:
            ws.settimeout(old_timeout)

        if not game_started:
            print(f"[{username}] DID NOT RECEIVE GAME START EVENT")

    print("Finished checking game start events.\n")
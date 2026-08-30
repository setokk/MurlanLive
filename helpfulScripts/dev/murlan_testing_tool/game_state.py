import websocket

def get_game_state(ws):
    ws.send(f"C0")

    #C0$status_response$json
    ws.settimeout(1)

    try:
        response = ws.recv()

        if response:
            print(f"Game State: {response}")
        else:
            print("No game state response")

    except websocket.WebSocketTimeoutException:
        print("No game state response")


import websocket

def declare_ready_to_play(ws):
    ws.send(f"C8")

    #C8$status_response
    ws.settimeout(1)
    try:
        while True:
            response = ws.recv()

            if not response:
                break

            if response.startswith("C8"):
                print(response)
                break

    except websocket.WebSocketTimeoutException:
        pass
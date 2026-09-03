import websocket

def play_hand(ws, cards):
    hand = "_".join(map(str, cards))
    print(hand)
    ws.send(f"C1${hand}")
    response = ws.recv()
    print(f"Play hand response: {response}")

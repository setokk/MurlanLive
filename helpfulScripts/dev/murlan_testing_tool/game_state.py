import websocket
import json

def get_game_state(ws):
    ws.send(f"C0")

    #C0$status_response$json
    ws.settimeout(1)

    try:
        response = ws.recv()

        if response:
            print(f"Game State: {response}")
            return response
        else:
            print("No game state response")

    except websocket.WebSocketTimeoutException:
        print("No game state response")

def get_current_card_combination(ws):
    response = get_game_state(ws)
    if response:
        json_data = response.split("$", 2)[2]
        game_state = json.loads(json_data)
        current_combo = game_state["currCardCombination"].split("_")
        if current_combo:
            print("Current card combination: ", current_combo)
        else:
            print("No current card combination, table is empty")


def get_hand(ws):
    response = get_game_state(ws)
    if response:
        json_data = response.split("$", 2)[2]
        game_state = json.loads(json_data)
        hand = game_state["hand"].split("_")
        if hand:
            print("Your hand is: ", hand)
            return hand
        else:
            print("You don't have cards")
            return []

def get_current_player(ws):
    response = get_game_state(ws)
    if response:
        json_data = response.split("$", 2)[2]
        game_state = json.loads(json_data)
        current_player = game_state["currTurnPlayer"]
        if current_player:
            print("Current player is: ", current_player["username"])
            return current_player
        else:
            print("Error: No current player")
            return {}

def choose_cards(user):
    if user["user"]["username"] == get_current_player(user["ws"])["username"]:
        hand = get_hand(user["ws"])

        if not hand:
            return []

        print("\nChoose cards to play:")
        for i, card in enumerate(hand, start=1):
            print(f"{i}. {card}")

        print("0. Cancel")

        while True:
            choice = input("\nCards to play (e.g. 1 3 5): ").strip()

            if choice == "0":
                return []

            try:
                indexes = [int(x) - 1 for x in choice.split()]

                # Check that all selected positions are valid
                if all(0 <= index < len(hand) for index in indexes):
                    selected_cards = [hand[index] for index in indexes]
                    return selected_cards

            except ValueError:
                pass

            print("Invalid card choice.")
    else:
        print("Not your turn to play")

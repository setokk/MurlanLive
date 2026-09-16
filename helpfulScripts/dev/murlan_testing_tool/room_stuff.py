import json
import websocket

def join_room(room, ws):
    ws.send(f"C4${room["id"]}")

    #C4$status_response
    client_response = ws.recv()

    print(f"Join {room["name"]} Client Response: {client_response}")

def leave_room(ws):
    ws.send(f"C7")

    #C7$status_response
    response = ws.recv()

    print(f"Leave Room Client Response: {response}")


def create_room(user , ws):
    ws.send(f"C5${user['user']['username']}'s Room$true$21")

    response = ws.recv()

    #C5$status_response#json
    parts = response.split("$")
    if parts[1] != "200":
        print(f"[{user['user']['username']}] Create Room Response: {response}")
    else:
        print("Created a room")


def get_available_rooms(ws):
    ws.send("C3")

    # C3$200$TableWithRooms
    try: 
        response = ws.recv() 
    except websocket.WebSocketException as e: 
        print(f"WebSocket error while getting rooms: {e}") 
        return []
    parts = response.split("$")
    
    if parts[1] != "200":
        print(f"Available Rooms Response: {response}")

    rooms = json.loads(parts[2])

    return rooms


def choose_room(rooms):
    print("\nChoose room:")

    for i, room in enumerate(rooms, start=1):
        print(f"{i}. {room["name"]}")

    print("0. Back")

    while True:
        choice = input("\nChoice: ").strip()

        if choice == "0":
            break

        try:
            index = int(choice) - 1

            if 0 <= index < len(rooms):
                return rooms[index]

        except ValueError:
            pass

        print("Invalid room choice.")
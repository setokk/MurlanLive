import json

def get_available_rooms(ws):
    ws.send("C3")

    # C3$200$TableWithRooms
    response = ws.recv()
    parts = response.split("$")
    
    if parts[1] != "200":
        print(f"Available Rooms Response: {response}")

    rooms = json.loads(parts[2])

    return rooms
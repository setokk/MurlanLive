import json
import websocket

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
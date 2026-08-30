def join_room(room, ws):
    ws.send(f"C4${room["id"]}")

    #C4$status_response
    client_response = ws.recv()
    parts = client_response.split("$")

    if parts[1] != "200":
        print(f"Join {room["name"]} Client Response: {client_response}")
    else:
        #S5$status_response$json
        server_response = ws.recv()
        parts = server_response.split("$")
        
        if parts[1] != "200":
            print(f"Join {room["name"]} Server Response: {server_response}")
        else:
            print(f"Joined {room["name"]}")

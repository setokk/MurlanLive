def create_room(user , ws):
    ws.send(f"C5${user['user']['username']}'s Room$true$21")

    response = ws.recv()

    #C5$status_response#json
    parts = response.split("$")
    if parts[1] != "200":
        print(f"[{user['user']['username']}] Create Room Response: {response}")
    else:
        print("Created a room")
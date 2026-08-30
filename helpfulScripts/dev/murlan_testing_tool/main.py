from users import USERS
from websocket_game_lobby import connect_user, check_game_start
from login import login_user
from register import register_user
from available_rooms import get_available_rooms
from game_state import get_game_state
from create_room import create_room
from join_room import join_room


def choose_user(user_table):
    print("\nChoose user:")

    for i, user_data in enumerate(user_table, start=1):
        print(f"{i}. {user_data['user']['username']}")

    print("0. Exit")

    while True:
        choice = input("\nChoice: ").strip()

        try:
            index = int(choice) - 1

            if 0 <= index < len(user_table):
                return user_table[index]

        except ValueError:
            pass

        if choice == "0":
            return

        print("Invalid choice.")

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

def main():
    rooms = []
    user_table = []

    for user in USERS:
        try:
            user_table.append({"jwt": login_user(user), "user": user})
        except:
            user_table.append({"jwt": register_user(user), "user": user})

    for user in user_table:
        user["ws"] = connect_user(user)


    user = choose_user(user_table)
    if not user:
        print("Exiting...")
    else:
        while True:
            print(f"\nCurrent User: {user["user"]["username"]}")
            print("1. Create room")
            print("2. Join room")
            print("3. Check available rooms")
            print("4. Pick another user")
            print("5. Check game state")
            print("6. Check for game start event")
            print("0. Exit")

            choice = input("\nChoice: ").strip()

            if not user_table:
                    print("\nIssue with logging/registering the users")
                    break

            if choice == "1":
                create_room(user, user["ws"])
                continue

            if choice == "2":
                rooms = get_available_rooms(user["ws"])
                if rooms:
                    room = choose_room(rooms)
                    if room:
                        join_room(room, user["ws"])
                    else:
                        continue
                else:
                    print("No rooms available")
                    continue

            if choice == "3":
                display_players = []
                rooms = get_available_rooms(user["ws"])
                if rooms:
                    for room in rooms:
                        for player in room["players"]:
                            display_players.append(player["username"])
                        print(f"{room["name"]} : {display_players}")
                        display_players = []
                    continue
                else:
                    print("No rooms available")
                    continue

            if choice == "4":
                user = choose_user(user_table)

            if choice == "5":
                get_game_state(user["ws"])

            if choice == "6":
                check_game_start(user_table)

            elif choice == "0":
                print("Exiting...")
                break

            else:
                print("Invalid choice.")


if __name__ == "__main__":
    main()
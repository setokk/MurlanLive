from users import USERS
from websocket_stuff import connect_user, check_game_start, clear_pending_messages, disconnect_all_users
from login_register import login_user, register_user
from room_stuff import join_room, leave_room, create_room, get_available_rooms, choose_room
from play_pass_hand import play_hand, pass_hand
from ready import declare_ready_to_play
import game_state


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
            disconnect_all_users(user_table)
            return

        print("Invalid choice.")

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
            print("7. Play hand")
            print("8. Pass hand")
            print("9. Ready to play")
            print("10. Leave room")
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
                        continue
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
                        try:
                            for player in room["players"]:
                                display_players.append(player["username"])
                            print(f"{room["name"]} : {display_players}")
                            display_players = []
                        except:
                            pass
                    continue
                else:
                    print("No rooms available")
                    continue

            if choice == "4":
                user = choose_user(user_table)
                if user:
                    clear_pending_messages(user["ws"])

            if choice == "5":
                game_state.get_game_state(user["ws"])
                continue

            if choice == "6":
                check_game_start(user_table)
                continue

            if choice == "7":
                cards = game_state.choose_cards(user)
                if cards:
                    play_hand(user["ws"], cards)
                continue

            if choice == "8":
                pass_hand(user["ws"])
                continue

            if choice == "9":
                declare_ready_to_play(user["ws"])
                continue

            if choice == "10":
                leave_room(user["ws"])
                continue
            
            elif choice == "0":
                disconnect_all_users(user_table)
                print("Exiting...")
                break

            else:
                print("Invalid choice.")


if __name__ == "__main__":
    main()
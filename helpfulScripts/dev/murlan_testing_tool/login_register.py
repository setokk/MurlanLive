import requests

LOGIN_URL = "http://localhost:8080/api/players/login"


def login_user(user):
    user2 = user
    user2 =user2.update({"username_or_email": user["username"]})
    user2 = user2.pop("username")
    response = requests.post(
        LOGIN_URL,
        json=user2
    )

    response.raise_for_status()

    jwt = response.text.strip()

    print(f"[{user["username"]}] logged in successfully")

    return jwt


REGISTER_URL = "http://localhost:8080/api/players/register"


def register_user(user):
    user.update({"email" : f"{user["username"]}@email.com"})
    response = requests.post(
        REGISTER_URL,
        json=user
    )

    response.raise_for_status()

    jwt = response.text.strip()

    print(f"[{user["username"]}] registered successfully")

    return jwt
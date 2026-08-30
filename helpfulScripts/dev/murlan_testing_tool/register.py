import requests

REGISTER_URL = "http://localhost:8080/api/players/register"


def register_user(user):
    response = requests.post(
        REGISTER_URL,
        json=user
    )

    response.raise_for_status()

    jwt = response.text.strip()

    print(f"[{user["username"]}] registered successfully")

    return jwt
import requests

LOGIN_URL = "http://localhost:8080/api/players/login"


def login_user(user):
    response = requests.post(
        LOGIN_URL,
        json=user
    )

    response.raise_for_status()

    jwt = response.text.strip()

    print(f"[{user["username"]}] logged in successfully")

    return jwt
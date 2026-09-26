extends Control

func _ready() -> void:
	PlayerRESTClient.validate_jwt_completed.connect(_on_validate_jwt_completed)
	if PlayerSession.load_session():
		PlayerRESTClient.validate_jwt(PlayerSession.jwt)

func _on_validate_jwt_completed(res: ApiResponse) -> void:
	if res.success:
		WebSocketClient.connect_to_ws(PlayerSession.jwt)
		await WebSocketClient.connection_established
		
		SceneManager.show_lobby()

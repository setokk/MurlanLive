extends Node

var jwt: String
var player: Player
var key: String = "U-W+]=Wiz?C?[ybo[IB$79mxVL6dHn6W"
const SESSION_PATH: String = "user://session.dat"

var muted_players: Dictionary = {}
var blocked_players: Dictionary = {}

func set_session(jwt: String) -> bool:
	self.jwt = jwt
	self.player = JwtUtils.decode_jwt(self.jwt)
	if self.player.is_invalid():
		self.jwt = ""
		self.player = null
		return false
	return true

func save_session() -> bool:
	if self.jwt.is_empty():
		return false

	var file: FileAccess = FileAccess.open_encrypted_with_pass(SESSION_PATH, FileAccess.WRITE, key)
	if file == null:
		push_error("Failed to open session file for writing: %s" % FileAccess.get_open_error())
		return false

	file.store_string(self.jwt)
	file.close()
	return true

func load_session() -> bool:
	if not FileAccess.file_exists(SESSION_PATH):
		return false

	var file: FileAccess = FileAccess.open_encrypted_with_pass(SESSION_PATH, FileAccess.READ, key)
	if file == null:
		push_error("Failed to open session file: %s" % FileAccess.get_open_error())
		return false

	var stored_jwt: String = file.get_as_text()
	file.close()

	if stored_jwt.is_empty():
		return false

	if not set_session(stored_jwt):
		clear_session() # expired/invalid token, don't leave a stale file around
		return false

	return true

func clear_session() -> void:
	self.jwt = ""
	self.player = null
	WebSocketClient.disconnect_from_ws()
	if FileAccess.file_exists(SESSION_PATH):
		DirAccess.remove_absolute(ProjectSettings.globalize_path(SESSION_PATH))

func init_blocked_players(players: Array[Dictionary]) -> void:
	for player in players:
		add_blocked(player)

func is_muted(id: int) -> bool:
	return muted_players.has(id)

func is_blocked(id: int) -> bool:
	return blocked_players.has(id)

func add_muted(player: Dictionary) -> void:
	muted_players[int(player["id"])] = player

func remove_muted(id: int) -> void:
	muted_players.erase(id)

func add_blocked(player: Dictionary) -> void:
	blocked_players[int(player["id"])] = player

func remove_blocked(id: int) -> void:
	blocked_players.erase(id)

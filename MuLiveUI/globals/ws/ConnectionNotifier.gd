extends Node
## Global, scene-independent listener for WebSocketClient connectivity events.

const CLOSE_CODE_LABELS: Dictionary[int, String] = {
	-1: "Connection lost",
	1000: "Disconnected",
	1001: "Server is going away",
	1006: "Connection lost",
	1011: "Server error",
	1012: "Server is restarting",
	1013: "Server is overloaded, try again shortly",
}

const MULIVE_CLOSE_REASONS: Dictionary[String, Dictionary] = {
	"Forbidden": {
		"title": "Access Denied",
		"message": "You don't have permission to connect.",
	},
	"Invalid JWT": {
		"title": "Session Expired",
		"message": "Your session is no longer valid. Please log in again.",
	},
	"JWT session already exists": {
		"title": "Already Connected",
		"message": "This account is already connected in another session.",
	},
	"Request body error": {
		"title": "Connection Error",
		"message": "The client sent a malformed request to the server.",
	},
	"No active session found": {
		"title": "Session Not Found",
		"message": "No active session was found for this connection. Please log in again.",
	},
	"No query params provided": {
		"title": "Connection Error",
		"message": "The connection request was missing required parameters.",
	},
	"Incompatible protocol version provided. Please update client or change gameserver host": {
		"title": "Update Required",
		"message": "Your client's protocol version is incompatible with the server. Please update the client or change the game server host.",
	},
}

func _ready() -> void:
	WebSocketClient.connection_closed.connect(_on_connection_closed)

func _on_connection_closed(code: int, reason: String, clean: bool) -> void:
	if MULIVE_CLOSE_REASONS.has(reason):
		var info: Dictionary = MULIVE_CLOSE_REASONS[reason]
		PopupFactory.error(info["message"], info["title"])
		return

	var label: String = CLOSE_CODE_LABELS.get(code, "Disconnected")
	var message: String = "%s (code %d)" % [label, code]
	if not reason.is_empty():
		message += ": %s" % reason

	if clean and code == 1000:
		PopupFactory.info(message, "Disconnected")
	else:
		PopupFactory.warning(message, "Connection Lost")

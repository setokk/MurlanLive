extends Node
## Global, scene-independent listener for WebSocketClient connectivity events.
##
## The dictionaries below hold *translation keys* (see translations.csv), not
## display text. The only exception are the keys of MULIVE_CLOSE_REASONS: those
## are the exact reason strings sent by the server and must stay as they are.

const CLOSE_CODE_LABELS: Dictionary[int, String] = {
	-1: "CLOSE_LABEL_CONNECTION_LOST",
	1000: "CLOSE_LABEL_DISCONNECTED",
	1001: "CLOSE_LABEL_SERVER_GOING_AWAY",
	1006: "CLOSE_LABEL_CONNECTION_LOST",
	1011: "CLOSE_LABEL_SERVER_ERROR",
	1012: "CLOSE_LABEL_SERVER_RESTARTING",
	1013: "CLOSE_LABEL_SERVER_OVERLOADED",
}

const MULIVE_CLOSE_REASONS: Dictionary[String, Dictionary] = {
	"Forbidden": {
		"title": "CLOSE_FORBIDDEN_TITLE",
		"message": "CLOSE_FORBIDDEN_MESSAGE",
	},
	"Invalid JWT": {
		"title": "CLOSE_INVALID_JWT_TITLE",
		"message": "CLOSE_INVALID_JWT_MESSAGE",
	},
	"JWT session already exists": {
		"title": "CLOSE_JWT_EXISTS_TITLE",
		"message": "CLOSE_JWT_EXISTS_MESSAGE",
	},
	"Request body error": {
		"title": "CONNECTION_ERROR_TITLE",
		"message": "CLOSE_BAD_BODY_MESSAGE",
	},
	"No active session found": {
		"title": "CLOSE_NO_SESSION_TITLE",
		"message": "CLOSE_NO_SESSION_MESSAGE",
	},
	"No query params provided": {
		"title": "CONNECTION_ERROR_TITLE",
		"message": "CLOSE_NO_QUERY_PARAMS_MESSAGE",
	},
	"Incompatible protocol version provided. Please update client or change gameserver host": {
		"title": "CLOSE_INCOMPATIBLE_PROTOCOL_TITLE",
		"message": "CLOSE_INCOMPATIBLE_PROTOCOL_MESSAGE",
	},
}

func _ready() -> void:
	WebSocketClient.connection_closed.connect(_on_connection_closed)

func _on_connection_closed(code: int, reason: String, clean: bool) -> void:
	if MULIVE_CLOSE_REASONS.has(reason):
		var info: Dictionary = MULIVE_CLOSE_REASONS[reason]
		PopupFactory.error(tr(info["message"]), tr(info["title"]))
		return

	var label: String = tr(CLOSE_CODE_LABELS.get(code, "CLOSE_LABEL_DISCONNECTED"))
	var message: String = tr("CONNECTION_CLOSED_WITH_CODE") % [label, code]
	if not reason.is_empty():
		message += ": %s" % reason

	if clean and code == 1000:
		PopupFactory.info(message, tr("CONNECTION_DISCONNECTED_TITLE"))
	else:
		PopupFactory.warning(message, tr("CONNECTION_LOST_TITLE"))

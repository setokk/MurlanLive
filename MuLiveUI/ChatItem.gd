extends Control

class_name ChatItem

var username: String
var message: String

@onready var username_label: Label = $HBoxContainer/Username
@onready var message_label: Label = $HBoxContainer/Message

func _ready() -> void:
	username_label.text = username + ":"
	message_label.text = message

func set_username(username: String) -> void:
	self.username = username
	
func set_message(message: String) -> void:
	self.message = message

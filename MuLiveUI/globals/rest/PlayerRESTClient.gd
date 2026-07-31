extends Node

signal login_completed(success: bool, jwt: String)
signal register_completed(success: bool, jwt: String)

var config: ProtocolConfig = ProtocolConfigProvider.get_config()
var login_http := HTTPRequest.new()
var register_http := HTTPRequest.new()

func _ready():
	add_child(login_http)
	add_child(register_http)
	
	login_http.request_completed.connect(_on_login_completed)
	register_http.request_completed.connect(_on_register_completed)

func login(username: String, password: String) -> void:
	var body: String = JSON.stringify({
		"username": username,
		"password": password
	})
	
	login_http.request(
		config.protocol_um_server_host + "/api/players/login",
		["Content-Type: application/json"],
		HTTPClient.METHOD_POST, 
		body
	)
	
func register(username: String, password: String) -> void:
	var body: String = JSON.stringify({
		"username": username,
		"password": password
	})
	
	register_http.request(
		config.protocol_um_server_host + "/api/players/register",
		["Content-Type: application/json"],
		HTTPClient.METHOD_POST, 
		body
	)

func _on_login_completed(result, response_code, headers, body):
	var jwt: String = body.get_string_from_utf8()
	
	if response_code == 200:
		login_completed.emit(true, jwt)
	else:
		push_error("Error while trying to login, response_code: %d, body: %s" % [response_code, body])
		login_completed.emit(false, "")

func _on_register_completed(result, response_code, headers, body):
	var jwt: String = body.get_string_from_utf8()
	
	if response_code == 200:
		register_completed.emit(true, jwt)
	else:
		push_error("Error while trying to register, response_code: %d, body: %s" % [response_code, body])
		register_completed.emit(false, "")

extends Node

signal login_completed(res: ApiResponse, jwt: String)
signal register_completed(res: ApiResponse, jwt: String)
signal forgot_password_completed(res: ApiResponse)
signal validate_jwt_completed(res: ApiResponse)

var config: ProtocolConfig = ProtocolConfigProvider.get_config()
var login_http := HTTPRequest.new()
var register_http := HTTPRequest.new()
var forgot_password_http := HTTPRequest.new()
var validate_jwt_http := HTTPRequest.new()

func _ready():
	add_child(login_http)
	add_child(register_http)
	add_child(forgot_password_http)
	add_child(validate_jwt_http)
	
	login_http.request_completed.connect(_on_login_completed)
	register_http.request_completed.connect(_on_register_completed)
	forgot_password_http.request_completed.connect(_on_forgot_password_completed)
	validate_jwt_http.request_completed.connect(_on_validate_jwt_completed)

func login(usernameOrEmail: String, password: String) -> void:
	var body: String = JSON.stringify({
		"usernameOrEmail": usernameOrEmail,
		"password": password
	})
	
	login_http.request(
		config.protocol_um_server_host + "/api/players/login",
		["Content-Type: application/json"],
		HTTPClient.METHOD_POST, 
		body
	)
	
func register(username: String, email: String, password: String) -> void:
	var register_request: Dictionary = {
		"username": username,
		"password": password
	}
	
	if not email.is_empty():
		register_request["email"] = email
		
	var body: String = JSON.stringify(register_request)
	
	register_http.request(
		config.protocol_um_server_host + "/api/players/register",
		["Content-Type: application/json"],
		HTTPClient.METHOD_POST, 
		body
	)
	
func forgot_password(usernameOrEmail: String) -> void:
	var body: String = JSON.stringify({
		"usernameOrEmail": usernameOrEmail
	})

	forgot_password_http.request(
		config.protocol_um_server_host + "/api/players/forgot-password",
		["Content-Type: application/json"],
		HTTPClient.METHOD_POST,
		body
	)

func validate_jwt(jwt: String) -> void:
	validate_jwt_http.request(
		config.protocol_um_server_host + "/api/players/validate-jwt",
		[
			"Content-Type: application/json",
			"Authorization: Bearer " + jwt
		],
		HTTPClient.METHOD_GET
	)

func _on_login_completed(result, response_code, headers, body):
	var jwt: String = body.get_string_from_utf8()
	var res := ApiResponse.from_http(response_code, body)
	login_completed.emit(res, jwt)

func _on_register_completed(result, response_code, headers, body):
	var jwt: String = body.get_string_from_utf8()
	var res := ApiResponse.from_http(response_code, body)
	register_completed.emit(res, jwt)
	
func _on_forgot_password_completed(result, response_code, headers, body):
	var res := ApiResponse.from_http(response_code, body)
	forgot_password_completed.emit(res)

func _on_validate_jwt_completed(result, response_code, headers, body):
	var res := ApiResponse.from_http(response_code, body)
	validate_jwt_completed.emit(res)

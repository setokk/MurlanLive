extends TextureButton

var refresh_tween: Tween
var original_rotation: float


func _ready() -> void:
	await get_tree().process_frame

	pivot_offset = size / 2.0
	original_rotation = rotation


func start_refresh_animation() -> void:
	if refresh_tween and refresh_tween.is_running():
		return

	refresh_tween = create_tween()
	refresh_tween.set_loops()

	refresh_tween.tween_property(
		self,
		"rotation",
		rotation + TAU,
		0.8
	)


func stop_refresh_animation() -> void:
	if refresh_tween and refresh_tween.is_running():
		refresh_tween.kill()

	var tween := create_tween()
	tween.tween_property(
		self,
		"rotation",
		original_rotation,
		0.2
	).set_trans(Tween.TRANS_SINE).set_ease(Tween.EASE_OUT)

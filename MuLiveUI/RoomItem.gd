extends Control

class_name RoomItem


@onready var join_button: Button = $Panel/Panel/Panel/JoinButton

@onready var seats: Array[Control] = [
	$Panel/Panel/RoomAvailabilityContainer/AspectRatioContainer/PanelContainer/Seat,
	$Panel/Panel/RoomAvailabilityContainer/AspectRatioContainer2/PanelContainer/Seat,
	$Panel/Panel/RoomAvailabilityContainer/AspectRatioContainer3/PanelContainer/Seat,
	$Panel/Panel/RoomAvailabilityContainer/AspectRatioContainer4/PanelContainer/Seat
]


const USER_ICON: Texture2D = preload("res://assets/images/user-icon.png")
const EMPTY_SEAT_ICON: Texture2D = preload("res://assets/images/seat-icon-greyscale-no-bg.png")


var usernames: Array[String] = [
	"Alex",
	"Maria",
	"John",
	"Bob",
	"Nick",
	"Anna",
	"Chris",
	"George"
]


func _ready() -> void:
	randomize_room()


func randomize_room() -> void:

	# At least one player must be present.
	var player_count: int = randi_range(1, 4)

	var occupied_seats: Array[int] = []

	# First seat is always occupied.
	occupied_seats.append(0)

	# Randomly choose the remaining occupied seats.
	var seat_index: int = 1
	while seat_index < player_count:
		if not occupied_seats.has(seat_index):
			occupied_seats.append(seat_index)
		seat_index = seat_index + 1
		
	# Update every seat.
	for i in range(seats.size()):

		var is_occupied: bool = occupied_seats.has(i)

		update_seat(
			seats[i],
			is_occupied,
			i
		)
	
	update_join_button()


func update_seat(
	seat: Control,
	is_occupied: bool,
	seat_index: int
) -> void:

	var username_label: Label = (
		seat.get_node("UsernameLabel")
	)

	var icon: TextureButton = (
		seat.get_node("SeatOrUserIcon")
	)

	if is_occupied:

		icon.texture_normal = USER_ICON

		username_label.text = (
			usernames[randi_range(0, usernames.size() - 1)]
		)

		username_label.visible = true

	else:

		icon.texture_normal = EMPTY_SEAT_ICON
		icon.modulate = Color()
		username_label.text = ""
		username_label.visible = false


func get_player_count() -> int:

	var count: int = 0

	for seat in seats:

		var username_label: Label = (
			seat.get_node("UsernameLabel")
		)

		if username_label.visible:
			count += 1

	return count


func is_full() -> bool:

	return get_player_count() >= seats.size()



func update_join_button() -> void:

	join_button.disabled = is_full()

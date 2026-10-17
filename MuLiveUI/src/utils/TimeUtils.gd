class_name TimeUtils
extends RefCounted

static func format_seconds(total_seconds: int) -> String:
	var minutes: int = total_seconds / 60
	var seconds: int = total_seconds % 60
	return "%d:%02d" % [minutes, seconds]

static func format_date_time(date_time: String) -> String:
	var date_parts: PackedStringArray = date_time.substr(0, 10).split("-")
	var time_parts: String = date_time.substr(11, 5)

	var formatted_date: String = "%s-%s-%s" % [
		date_parts[2], date_parts[1], date_parts[0]
	]

	var formatted_time: String = time_parts
	return "%s - %s" % [
		formatted_date, formatted_time
	]

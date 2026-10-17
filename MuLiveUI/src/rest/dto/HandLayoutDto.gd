class_name HandLayoutDto
extends RefCounted

enum CardCombinationPlacementType {
	DEFAULT,
	SINGLE_CARD,
	DOUBLE_CARDS,
	TRIPLE_CARDS,
	BOMB,
	KOLOR,
	BOMB_KOLOR,
}

enum SortMode {
	ASC,
	DESC,
	RANDOM,
}

var name: String = ""
var is_active: bool = false
var card_combination_placements: Array[CardCombinationPlacementDto] = []

static func from_dict(data: Dictionary) -> HandLayoutDto:
	var dto := HandLayoutDto.new()
	dto.name = data.get("name", "")
	dto.is_active = data.get("isActive", false)

	var placements: Array = data.get("cardCombinationPlacements", [])
	for placement_data in placements:
		dto.card_combination_placements.append(
			CardCombinationPlacementDto.from_dict(placement_data)
		)

	return dto

func to_dict() -> Dictionary:
	var placements_array := []
	for placement in card_combination_placements:
		placements_array.append(placement.to_dict())

	return {
		"name": name,
		"isActive": is_active,
		"cardCombinationPlacements": placements_array,
	}

static func array_from_json(parsed_json) -> Array[HandLayoutDto]:
	var result: Array[HandLayoutDto] = []
	if parsed_json == null:
		return result
	for item in (parsed_json as Array):
		result.append(HandLayoutDto.from_dict(item))
	return result

static func array_to_dicts(layouts: Array[HandLayoutDto]) -> Array:
	var result := []
	for layout in layouts:
		result.append(layout.to_dict())
	return result

class CardCombinationPlacementDto:
	extends RefCounted

	var card_combination_placement_type: HandLayoutDto.CardCombinationPlacementType = \
		HandLayoutDto.CardCombinationPlacementType.DEFAULT
	var sort_mode: HandLayoutDto.SortMode = HandLayoutDto.SortMode.ASC

	static func from_dict(data: Dictionary) -> CardCombinationPlacementDto:
		var dto := CardCombinationPlacementDto.new()
		dto.card_combination_placement_type = _enum_from_string(
			HandLayoutDto.CardCombinationPlacementType,
			data.get("cardCombinationPlacementType", "DEFAULT")
		)
		dto.sort_mode = _enum_from_string(
			HandLayoutDto.SortMode,
			data.get("sortMode", "ASC")
		)
		return dto

	func to_dict() -> Dictionary:
		return {
			"cardCombinationPlacementType": HandLayoutDto.CardCombinationPlacementType.keys()[card_combination_placement_type],
			"sortMode": HandLayoutDto.SortMode.keys()[sort_mode],
		}

	static func _enum_from_string(enum_type: Dictionary, value: String) -> int:
		if enum_type.has(value):
			return enum_type[value]
		push_warning("Unknown enum value '%s' for %s, defaulting to 0" % [value, enum_type])
		return 0

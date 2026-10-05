class_name ProfileIconUtils
extends RefCounted

static func load_image(id: int) -> Texture2D:
	var dir := DirAccess.open(GameConstants.ICONS_DIRECTORY)
	if dir == null:
		return null

	dir.list_dir_begin()
	var file_name := dir.get_next()

	while file_name != "":
		if not dir.current_is_dir() and file_name.get_basename() == str(id):
			dir.list_dir_end()

			var image := Image.load_from_file(GameConstants.ICONS_DIRECTORY + file_name)
			if image == null or image.is_empty():
				return null

			return ImageTexture.create_from_image(image)

		file_name = dir.get_next()

	dir.list_dir_end()
	return null

#pragma once

#include <vector>
#include <glm\glm.hpp>
#include <glad\glad.h>

#include "game_object.h"
#include "sprite_renderer.h"
#include "resource_manager.h"

class GameLevel
{
public:
	std::vector<GameObject> Bricks;
	GameLevel(){}
	void Load(const GLchar *file, GLuint levelWidth, GLuint levelHeight);
	void Draw(SpriteRenderer &renderer);
	GLboolean IsCompleted();
private:
	void init(std::vector<std::vector<GLuint>> tileData, GLuint levelWidth, GLuint levelHeight);
};

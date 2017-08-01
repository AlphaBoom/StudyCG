#ifndef GAME_H
#define GAME_H

#include <glad\glad.h>
#include <vector>
#include "game_level.h"

enum GameState {
	GAME_ACTIVE,
	GAME_MENU,
	GAME_WIN
};


// Initial size of the player paddle
const glm::vec2 PLAYER_SIZE(100, 20);
// Initial velocity of the player paddle
const GLfloat PLAYER_VELOCITY(500.0f);


class Game
{
public:
	GameState State;
	GLboolean Keys[1024];
	GLuint Width, Height;

	std::vector<GameLevel> Levels;
	GLuint Level;

	Game(GLuint width, GLuint height);
	~Game();
	void Init();
	void ProcessInput(GLfloat dt);
	void Update(GLfloat dt);
	void Render();
};
#endif
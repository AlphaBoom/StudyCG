#ifndef GAME_H
#define GAME_H

#include <glad\glad.h>
#include <vector>
#include "game_level.h"
#include "power_up.h"

enum GameState {
	GAME_ACTIVE,
	GAME_MENU,
	GAME_WIN
};

enum Direction {
	UP,
	RIGHT,
	DOWN,
	LEFT
};

typedef std::tuple<GLboolean, Direction, glm::vec2> Collision;

// Initial size of the player paddle
const glm::vec2 PLAYER_SIZE(100, 20);
// Initial velocity of the player paddle
const GLfloat PLAYER_VELOCITY(500.0f);
const glm::vec2 INITIAL_BALL_VELOCITY(100.0f, -350.0f);
const GLfloat BALL_RADIUS = 12.5f;


class Game
{
public:
	GameState State;
	GLboolean Keys[1024];
	GLuint Width, Height;

	std::vector<GameLevel> Levels;
	std::vector<PowerUp> PowerUps;
	GLuint Level;

	Game(GLuint width, GLuint height);
	~Game();
	void Init();
	void ProcessInput(GLfloat dt);
	void Update(GLfloat dt);
	void Render();
	void DoCollisions();
	void SpawnPowerUps(GameObject &blocks);
	void UpdatePowerUps(GLfloat dt);

	void ResetLevel();
	void ResetPlayer();
};
#endif
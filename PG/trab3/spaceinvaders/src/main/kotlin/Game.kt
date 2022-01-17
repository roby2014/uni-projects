import pt.isel.canvas.KeyEvent

/**
 * Represents all the game information.
 * @property area Game area.
 * @property aliens Aliens information.
 * @property ship Spaceship object.
 * @property points Player points.
 * @property gameStatus Current game status. [GameStatus].
 */
data class Game(
    val area: Area,
    val aliens: Aliens,
    val ship: Spaceship,
    val points: Int = 0,
    val gameStatus: GameStatus
)


/**
 * The main function of our game. **This handles the important logic.**
 * It checks if there are possible collisions and then applies movement to the shots.
 * It also checks if there are no more aliens which means we won.
 * @return Updated game object with the new properties.
 */
fun Game.runGameLogic() =
    when {
        gameStatus == GameStatus.RUNNING && aliens.invaders.isNotEmpty() -> checkCollisions().moveShots()
        aliens.invaders.isEmpty() -> copy(gameStatus = GameStatus.WIN)
        else -> this
    }

/**
 * Checks for keyboard input and applies logic. There is only 1 case:
 * If space bar is pressed, it should try to spawn a new spaceship shot
 * @return Updated game object with the new spaceship shot or the previous game object with no modifications.
 */
fun Game.handleKeyboardInput(it: KeyEvent) =
    when (it.text) {
        "Space" -> newShipShot()
        else -> this
    }

/**
 * Returns the current game object with vertical movement applied to the shots.
 * Before applying the movement, it will clean/remove the shots that are invalid (not on the screen anymore).
 * Why do we need to "clean" them? Because when shots go below/above the canvas height, it DOES NOT mean they
 * are deleted from memory, they are just not displayed anymore.
 */
fun Game.moveShots() = copy(
    aliens = aliens.cleanShots(this),
    ship = ship.moveShipShot(this)
)

/**
 * Checks if there are any collisions. There are 4 cases:
 * Case 1: Alien shot collided with our spaceship, game should end.
 * Case 2: Alien shot collided with our spaceship shot, the alien shot should die/be deleted.
 * Case 3: Alien collided with the spaceship shot.
 * Case 4: Alien collided with spaceship, game over.
 * Case 5: No collision, just return the same object.
 * @return Updated game property if any collision occurred.
 */
fun Game.checkCollisions(): Game {
    aliens.shots.forEachIndexed { i, alienShot ->
        if (alienShot.hitBox.checkCollision(ship.hitBox)) { // Case 1
            return copy(gameStatus = GameStatus.LOSE)
        }
        if (ship.shot != null && alienShot.hitBox.checkCollision(ship.shot.hitBox)) { //  Case 2
            return copy(aliens = aliens.onAlienShotDestroy(i), points = points + 1)
        }
    }

    aliens.invaders.forEachIndexed { i, a ->
        if (ship.shot != null && a.hitBox.checkCollision(ship.shot.hitBox)) { // Case 3
            return copy(
                aliens = aliens.onAlienDestroy(i),
                ship = ship.resetShot(),
                points = points + a.type.points
            )
        }
        if (a.hitBox.checkCollision(ship.hitBox)) { // Case 4
            return copy(gameStatus = GameStatus.LOSE)
        }
    }

    return this // case 5
}

/**
 * Moves the spaceship to a new x-axis position given by [x] (if valid).
 * @return New game object with the new spaceship position.
 */
fun Game.moveSpaceship(x: Int) = when {
    ship.canSpaceshipMoveTo(x) -> copy(
        ship = Spaceship(
            Point((x - SHIP_WIDTH / 2), SHIP_MOVING_LANE_Y),
            shot = ship.shot,
            hitBox = ship.hitBox.copy(x1 = x - SHIP_WIDTH / 2, x2 = x + SHIP_WIDTH / 2)
        )
    )
    else -> this
}

/**
 * Adds a new spaceship shot to the current game if condition is met.
 * The condition is: No past shots from the spaceship on the screen.
 * In case creation conditions are met, it adds the shot to the current shot list.
 * @return New game object with the new shot added.
 */
fun Game.newShipShot() = when {
    ship.canSpaceshipShoot() -> copy(ship = ship.addShipShot())
    else -> this
}

/**
 * Update current aliens by changing their animation and moving them horizontally
 * In case we hit the edge, reverse direction and move down
 * @return New game object with the movement applied.
 */
fun Game.moveAliens(): Game {
    if (gameStatus != GameStatus.RUNNING)
        return this

    val updatedAliens = Aliens(
        aliens.reverseStatus().map { it.moveHorizontally(aliens.direction, it.pos) },
        aliens.shots,
        aliens.direction
    )

    if (updatedAliens.edgeHit(game = this)) {
        return copy(
            aliens = Aliens(
                updatedAliens.invaders.map { it.moveVertically(aliens.direction, it.pos) },
                updatedAliens.shots,
                updatedAliens.direction.reverseDirection()
            )
        )
    }

    return copy(aliens = updatedAliens.newShot(this))
}
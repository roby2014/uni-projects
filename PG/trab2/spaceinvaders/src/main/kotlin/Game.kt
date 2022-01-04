import pt.isel.canvas.KeyEvent

/**
 * Represents all the game information.
 * @property area Game area.
 * @property alienShots Alien shots list.
 * @property ship Spaceship object.
 * @property over Decides if game should be over or not.
 */
data class Game(val area: Area, val alienShots: List<Shot>, val ship: Spaceship, val over: Boolean)

/**
 * The main function of our game. This handles all the important logic.
 * It checks if there are possible collisions and then applies movement to the shots.
 * @return Updated game object with the new properties.
 */
fun Game.runGameLogic() = this.checkCollisions().moveShots()

/**
 * Checks if space bar is pressed, so we can try to spawn a new spaceship shot
 * @return Updated game object with the new spaceship shot or the previous game object with no modifications.
 */
fun Game.handleKeyboardInput(it: KeyEvent): Game {
    return if (it.text == "Space") newShipShot()
    else this
}

/**
 * Returns the current game object with vertical movement applied to the shots.
 * Before applying the movement, it will clean/remove the shots that are invalid (not on the screen anymore).
 * Why do we need to "clean" them? Because when shots go below/above the canvas height, it DOES NOT mean they
 * are deleted from memory, they are just not displayed anymore.
 */
fun Game.moveShots() = copy(
    alienShots = alienShots.filter { it.validShot(this) }.map { it.moveShot(this) },
    ship = ship.moveShipShot(this)
)

/**
 * Checks if any alien shot collided with something. If this happens, there are 3 cases to handle with:
 * Case 1: Collided with our spaceship, game should end.
 * Case 2: Collided with our spaceship shot, the alien shot should die/be deleted.
 * Case 3: No collision, just return the same object.
 * @return Updated game property if any collision happened.
 */
fun Game.checkCollisions(): Game {
    alienShots.forEachIndexed { i, alienShot ->
        if (alienShot.hitBox.checkCollision(ship.hitBox)) {
            return copy(over = true)
        }
        if (ship.shot != null) {
            if (alienShot.hitBox.checkCollision(ship.shot.hitBox)) {
                return copy(alienShots = alienShots.filterIndexed { index, _ -> index != i })
            }
        }
    }
    return this
}

/**
 * Moves the spaceship to a new x-axis position given by [x] (if valid).
 * @return New game object with the new spaceship position.
 */
fun Game.moveSpaceship(x: Int) = when {
    ship.canSpaceshipMoveTo(x) -> copy(
        ship = Spaceship(
            (x - SHIP_WIDTH / 2),
            shot = ship.shot,
            hitBox = ship.hitBox.copy(x1 = x - SHIP_WIDTH / 2, x2 = x + SHIP_WIDTH / 2)
        )
    )
    else -> this
}

/**
 * Creates a new alien shot if condition is met. The condition is:
 * Apply 50% of possibility first.W
 * In case creation conditions are met, it adds the shot to the current shot list.
 * @return New game object with the new shot added.
 */
fun Game.newAlienShot() = when {
    (0..1).random() == 1 -> copy(alienShots = alienShots + addAlienShot(this))
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

const val FONT_SIZE = 25
const val FONT_BORDER = 15

// Default spaceship properties
const val SHIP_WIDTH = 50
const val SHIP_HEIGHT = 30
const val SHIP_MOVING_LANE_Y = 450 - FONT_BORDER * 2
const val SHOOTER_HEIGHT = 15

/**
 * Represents the spaceship.
 * @property pos Coordinates of spaceship.
 * @property hitBox Hit box of spaceship.
 * @property shot Spaceship shot.
 */
data class Spaceship(
    val pos: Point,
    val hitBox: HitBox,
    val shot: Shot? = null,
)

/**
 * Constructs and returns a new spaceship centered on the canvas width ([x]) with a hit box.
 */
fun spaceship(x: Int) = Spaceship(
    pos = Point(x=x, y=SHIP_MOVING_LANE_Y),
    hitBox = hitBoxSpaceship(x)
)

/**
 * Adds a spaceship shot.
 * @return New spaceship object with a new shot.
 */
fun Spaceship.addShipShot(): Spaceship {
    val shotSpawnArea = pos.x + SHIP_WIDTH / 2
    val shotHitBox = HitBox(shotSpawnArea, shotSpawnArea + SHOT_WIDTH, pos.y, pos.y - SHOT_HEIGHT)
    return copy(shot = Shot(shotSpawnArea, pos.y, false, -SHOT_SPEED, shotHitBox))
}

/**
 * Removes/destroys our spaceship shot. We use this when colliding with an alien.
 */
fun Spaceship.resetShot() = copy(shot=null)

/**
 * Moves a spaceship shot ONLY IF there is one.
 * @return New spaceship object with movement applied to the shot.
 */
fun Spaceship.moveShipShot(g: Game) = when {
    shot.validShot(g) -> copy(shot = shot?.moveShot(g))
    else -> copy(shot = null) // delete shot
}

/**
 * Returns true if our spaceship can shoot. Why do we need this?
 * Because our spaceship can only shoot if there are no past shots on the screen.
 */
fun Spaceship.canSpaceshipShoot() = this.shot == null

/**
 * Returns true if spaceship can move to the current mouse position given by [x].
 * The mouse position AND THE SPACESHIP need to be inside the canvas width.
 * This is used to check if we can move the spaceship horizontally, so we can see the whole ship.
 */
fun Spaceship.canSpaceshipMoveTo(x: Int) = (x + SHIP_WIDTH / 2 < CANVAS_WIDTH && x - SHIP_WIDTH / 2 > 0)
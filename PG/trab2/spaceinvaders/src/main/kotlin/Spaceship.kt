import pt.isel.canvas.GREEN
import pt.isel.canvas.YELLOW

// Default spaceship properties
const val SHIP_WIDTH = 50
const val SHIP_HEIGHT = 10
const val SHIP_COLOR = GREEN
const val SHIP_MOVING_LANE_Y = 450

// Default spaceship's shooter properties
const val SHOOTER_WIDTH = 4
const val SHOOTER_HEIGHT = 5
const val SHOOTER_COLOR = YELLOW

/**
 * Represents the spaceship.
 * @property x X-axis of Spaceship.
 * @property y Y-axis of Spaceship. Default value: [SHIP_MOVING_LANE_Y]
 * @property hitBox Hit box of spaceship.
 * @property shot Spaceship shot.
 */
data class Spaceship(
    val x: Int,
    val y: Int = SHIP_MOVING_LANE_Y,
    val hitBox: HitBox,
    val shot: Shot? = null,
)

/**
 * Constructs and returns a new spaceship centered on the canvas width ([x]).
 */
fun spaceship(x: Int) = Spaceship(
    x = x,
    hitBox = HitBox(x , x  + SHIP_WIDTH, SHIP_MOVING_LANE_Y, SHIP_MOVING_LANE_Y + SHIP_HEIGHT)
)

/**
 * Adds a spaceship shot.
 * @return New spaceship object with a new shot.
 */
fun Spaceship.addShipShot(): Spaceship {
    val shotSpawnArea = x + SHIP_WIDTH / 2
    val shotHitBox = HitBox(shotSpawnArea, shotSpawnArea + SHOT_WIDTH, y, y - SHOT_HEIGHT)
    return copy(shot = Shot(shotSpawnArea, y, false, -SHOT_SPEED, shotHitBox))
}

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
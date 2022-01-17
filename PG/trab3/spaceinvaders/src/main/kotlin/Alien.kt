// Horizontal & Vertical alien speed
const val ALIEN_MOVING_SPEED_X = 4
const val ALIEN_MOVING_SPEED_Y = 20

// Used for "cutting" the image into pieces, so we can draw each individual alien.
// This **DOES NOT** refer to cells/aliens size. It refers to the image invaders.png
const val ALIEN_WIDTH = 112
const val ALIEN_HEIGHT = 80

// Player points gain when killing an alien
const val POINTS_SQUID_KILL = 30
const val POINTS_CRAB_KILL = 20
const val POINTS_OCTOPUS_KILL = 10

/**
 * Represents an alien.
 * @property pos Coordinates of our alien.
 * @property type Alien type ([AlienType]).
 * @property status Alien status ([AlienStatus]).
 *
 */
data class Alien(val pos: Point, val type: AlienType, val status: AlienStatus, val hitBox: HitBox)

/**
 * Type of Alien, where [start] is where the image starts horizontally (y-axis) and [points]
 *  is the amount of points that player wins if it kills the alien.
 */
enum class AlienType(val start: Int, val points: Int) {
    SQUID(start = 0, points = POINTS_SQUID_KILL), // yellow (row 0)
    CRAB(start = ALIEN_HEIGHT, points = POINTS_CRAB_KILL), // blue (row 1)
    OCTOPUS(start = ALIEN_HEIGHT * 2, points = POINTS_OCTOPUS_KILL), // green (row 2)
}

/**
 * Type of Alien, where [start] is where the image starts vertically (x-axis)
 */
enum class AlienStatus(val start: Int) {
    STANDING(0),
    MOVING(ALIEN_WIDTH)
}

/**
 * Move each alien horizontally (x-axis) depending on its direction([d]) and current position ([p]).
 */
fun Alien.moveHorizontally(d: Direction, p: Point): Alien {
    val right = (d == Direction.RIGHT)
    return copy(
        pos = Point(
            x = p.x + if (right) ALIEN_MOVING_SPEED_X else -ALIEN_MOVING_SPEED_X,
            y = p.y
        ),
        hitBox = hitBoxAlien(pos, right, type)
    )
}

/**
 * Move each alien vertically (y-axis) depending on its direction([d]) and current position ([p]).
 */
fun Alien.moveVertically(d: Direction, p: Point): Alien {
    val newPos = Point(x = p.x, y = p.y + ALIEN_MOVING_SPEED_Y)
    return copy(pos = newPos, hitBox = hitBoxAlien(newPos, right = (d == Direction.RIGHT), type = type))
}

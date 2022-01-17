// "Small" utilities

/**
 * Represents an area information.
 * @property w Game width.
 * @property h Game height.
 */
data class Area(val w: Int, val h: Int)

/**
 * Represents a direction, either LEFT or RIGHT.
 */
enum class Direction {
    LEFT, RIGHT
}

/**
 * Returns the opposite direction of receiver's ([this]) direction.
 */
fun Direction.reverseDirection() = if (this == Direction.RIGHT) Direction.LEFT else Direction.RIGHT

/**
 * Represents the game status, either WIN, LOSE or still RUNNING.
 */
enum class GameStatus {
    WIN, LOSE, RUNNING
}

/**
 * Represents a point position.
 * @property x X-axis coordinate.
 * @property y Y-axis coordinate.
 */
data class Point(val x: Int, val y: Int)
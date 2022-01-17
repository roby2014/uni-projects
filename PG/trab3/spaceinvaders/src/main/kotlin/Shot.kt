import pt.isel.canvas.RED
import pt.isel.canvas.WHITE

// Default shot properties
const val SHOT_WIDTH = 4
const val SHOT_HEIGHT = 7
const val SHOT_SPEED = 4 // spaceship shot, alien shots speed will be randomized

/**
 * Represents shot information.
 * @property x Where the shot comes from (x-axis).
 * @property y Current y-axis position of shot.
 * @property fromAlien Defines if shot comes from an alien or from the spaceship.
 * @property speed Shot speed (and direction).
 * @property color Shot color.
 * @property hitBox Hit box of shot.
 */
data class Shot(
    val x: Int,
    val y: Int,
    val fromAlien: Boolean,
    val speed: Int,
    val hitBox: HitBox,
    val color: Int = if (fromAlien) RED else WHITE,
)

/**
 * Returns a new shot with vertical movement applied.
 */
fun Shot.moveShot(g: Game) = copy(y = y + speed, hitBox = hitBox.copy(y1 = y + speed, y2 = y + speed + SHOT_HEIGHT))

/**
 * Returns a new alien shot.
 * It will spawn from a random alien position, it moves in a random speed, and it will have a hit box.
 */
fun spawnAlienShot(g: Game): Shot {
    val randomAlien = g.aliens.invaders.random()
    val spawn = Point(randomAlien.pos.x + CELL_WIDTH / 2, randomAlien.pos.y) // Shot spawn point
    return Shot(
        x = spawn.x,
        y = spawn.y,
        fromAlien = true,
        speed = (1..SHOT_SPEED).random(),
        hitBox = HitBox(x1=spawn.x, x2=spawn.x + SHOT_WIDTH, y1=1, y2=1 + SHOT_HEIGHT)
    )
}

/**
 * Returns true if the shot exists on the screen.
 */
fun Shot?.validShot(g: Game) = this != null && (y in 0..CANVAS_HEIGHT) && (x in 0..CANVAS_WIDTH)

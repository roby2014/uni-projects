// Grid dimensions
const val MAX_COLUMNS = 11
const val MAX_ROWS = 5
const val MAX_CELLS = MAX_COLUMNS * MAX_ROWS
const val CELL_WIDTH = 56
const val CELL_HEIGHT = 50

// Initial top border aliens
const val INITIAL_TOP_BORDER = 20

/**
 * Represents the Aliens.
 * @property invaders List of aliens on the screen.
 * @property shots Alien shots.
 * @property direction Current alien moving direction.
 */
data class Aliens(val invaders: List<Alien>, val shots: List<Shot>, val direction: Direction)

/**
 * Spawns initial aliens.
 */
fun spawnAliens() = Aliens(
    invaders = List(MAX_CELLS) { index ->
        val alienCords = getAlienCordsByIndex(index)
        Alien(alienCords, getTypeByIndex(index), AlienStatus.STANDING, hitBoxAlien(alienCords))
    },
    shots = emptyList(),
    direction = Direction.RIGHT
)

/**
 * Returns the [AlienType] depending on its initial index ([alienIndex]).
 * First 2 rows are for [AlienType.SQUID], 3rd and 4th for [AlienType.CRAB] and last one for [AlienType.OCTOPUS]
 */
private fun getTypeByIndex(alienIndex: Int) =
    when (alienIndex) {
        in (0..21) -> AlienType.SQUID
        in (22..43) -> AlienType.CRAB
        in (44..55) -> AlienType.OCTOPUS
        else -> AlienType.SQUID // in case it fails, return this by default
    }

/**
 * This function returns what coordinates the alien will have depending on its index ([alienIndex]).
 * The row (y) will depend on its index. The first 10 are at row 1, 11 to 21 are at row 2, etc...
 * The column (x) will depend on its index and the row*11. Row*11 because we have 11 columns each row.
 */
private fun getAlienCordsByIndex(alienIndex: Int): Point {
    val y = when (alienIndex) { // row
        in (0..10) -> 0
        in (11..21) -> 1
        in (22..32) -> 2
        in (33..43) -> 3
        in (44..55) -> 4
        else -> 0
    }
    val x = alienIndex - (y * 11) // column
    return Point(x=x * CELL_WIDTH, y=y * CELL_HEIGHT + INITIAL_TOP_BORDER)
}

/**
 * Cleans the current alien shot list.
 */
fun Aliens.cleanShots(g: Game) = copy(shots = shots.filter { it.validShot(g) }.map { it.moveShot(g) })

/**
 * Creates a new alien shot.
 */
fun Aliens.newShot(g: Game) = copy(shots = shots + spawnAlienShot(g))

/**
 * Returns true if any alien is hitting our screen edge.
 * Why need this to invert aliens direction and move them down.
 */
fun Aliens.edgeHit(game: Game): Boolean {
    val max = invaders.maxOf { it.pos.x }
    val min = invaders.minOf { it.pos.x }
    return min < 0 || max >= game.area.w - CELL_WIDTH
}

/**
 * Removes an alien shot by its index. [i]
 */
fun Aliens.onAlienShotDestroy(i: Int) = copy(shots = shots.filterIndexed { index, _ -> index != i })

/**
 * Removes an alien by its index. [i]
 */
fun Aliens.onAlienDestroy(i: Int) = copy(invaders = invaders.filterIndexed { index, _ -> index != i })

/**
 * Reverts all aliens status ([GameStatus])
 * @return Alien list with the new status updated.
 */
fun Aliens.reverseStatus(): List<Alien> {
    // Inline function
    fun Alien.reverseStatus(s: AlienStatus) =
        copy(status = if (s == AlienStatus.STANDING) AlienStatus.MOVING else AlienStatus.STANDING)

    return invaders.map { it.reverseStatus(it.status) }
}

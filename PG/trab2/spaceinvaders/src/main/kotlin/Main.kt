import pt.isel.canvas.*

// Default canvas settings
const val CANVAS_WIDTH = 700
const val CANVAS_HEIGHT = 500
const val CANVAS_BG_COLOR = BLACK

// Frame rate related
const val FRAME_RATE = 70
const val FRAMES_PER_SECOND = 1000 / FRAME_RATE

// Interval to create new alien shots
const val ALIEN_SHOT_INTERVAL = 250 // ms

/**
 * Entry point of application.
 * The game has a spaceship which the player can move it horizontally with the mouse.
 * Every 250 ms, there is 50% chance of spawning a new alien shot (coming from the top).
 * The player can shoot (with the mouse or space bar) if there are no past bullets on the screen.
 * The main goal is to dodge the alien's shots and to destroy them.
 * If the alien shot hits the player, game ends.
 */
fun main() {
    onStart {
        val cv: Canvas = Canvas(CANVAS_WIDTH, CANVAS_HEIGHT, CANVAS_BG_COLOR)
        var game: Game = Game(Area(cv.width, cv.height), emptyList(), spaceship(cv.width / 2), false)

        cv.onMouseMove { it ->
            game = game.moveSpaceship(it.x)
        }
        cv.onMouseDown {
            game = game.newShipShot()
        }
        cv.onKeyPressed {
            game = game.handleKeyboardInput(it)
        }
        cv.onTimeProgress(ALIEN_SHOT_INTERVAL) {
            game = game.newAlienShot()
        }
        cv.onTimeProgress(FRAMES_PER_SECOND) {
            game = game.runGameLogic()
            cv.drawGame(game)
        }
    }
    onFinish {}
}
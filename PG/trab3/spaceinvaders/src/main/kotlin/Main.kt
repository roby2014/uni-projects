import pt.isel.canvas.*

// Default canvas settings
const val CANVAS_WIDTH = 700
const val CANVAS_HEIGHT = 500
const val CANVAS_BG_COLOR = BLACK

// Frame rate related
const val FRAME_RATE = 70
const val FRAMES_PER_SECOND = 1000 / FRAME_RATE

// Interval to move aliens (in ms)
const val ALIEN_MOVE_INTERVAL = 500

/**
 * Entry point of application.
 * The game has a spaceship which the player can move it horizontally with the mouse a
 *  and a group of aliens that move towards the spaceship while shooting.
 * Every time the aliens move, new alien shot will be spawned (coming from a random alien).
 * The player can shoot (with the mouse or space bar) if there are no past bullets on the screen.
 * The main goal is to dodge the alien's shots and to destroy them and the aliens itself to win points.
 * If an alien shot/alien hits the player, game ends, if player destroys every alien, player wins.
 */
fun main() {
    onStart {
        val cv = Canvas(CANVAS_WIDTH, CANVAS_HEIGHT, CANVAS_BG_COLOR)
        var game = Game(
            area = Area(cv.width, cv.height),
            aliens = spawnAliens(),
            ship = spaceship(x = cv.width / 2),
            gameStatus = GameStatus.RUNNING
        )

        cv.onTimeProgress(FRAMES_PER_SECOND) {
            game = game.runGameLogic()
            cv.drawGame(game)
        }
        cv.onTimeProgress(ALIEN_MOVE_INTERVAL) {
            game = game.moveAliens()
        }
        cv.onMouseMove {
            game = game.moveSpaceship(it.x)
        }
        cv.onMouseDown {
            game = game.newShipShot()
        }
        cv.onKeyPressed {
            game = game.handleKeyboardInput(it)
        }
    }
    onFinish {}
}

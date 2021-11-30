import pt.isel.canvas.*
import kotlin.math.*

/**
 * Global values
 */

const val CANVAS_HEIGHT = 500
const val CANVAS_WIDTH = 600

const val INITIAL_RADIUS = 100
const val ANDROID_COLOR = 0xA4C639

const val CURSOR_DELTA = 4
const val ZOOM_DELTA = 1

// Used for drawing the "head", which will be a semi-circle in this case
const val HEAD_START_ANG = 0
const val HEAD_END_ANGLE = 180

// We call these "delta" because the original size will depend on these multiplied by the head's radius
const val ANTENNA_ANGLE_DELTA = 60.0
const val ANTENNA_SIZE_DELTA = 0.3
const val ANTENNA_DIAMETER_DELTA = 0.08

const val EYE_X_DELTA = 0.44
const val EYE_Y_DELTA = 0.4
const val EYE_RADIUS_DELTA = 0.1

// Our Android Head Class
data class Head(val x: Int, val y: Int, val radius: Int)

fun main() {

    // Function called when window is created
    onStart {
        // Create Canvas and our mutable Head object
        val cv = Canvas(CANVAS_WIDTH, CANVAS_HEIGHT, WHITE)
        var head = Head(cv.width / 2, cv.height / 2, INITIAL_RADIUS) // canvas center

        // Draw initial head on the canvas
        head.draw(cv)

        // Handle mouse input
        cv.onMouseDown { click ->
            head = Head(click.x, click.y, head.radius)
            head.draw(cv) // redraw with the new properties
        }

        // Handle keyboard input
        cv.onKeyPressed { key ->
            head = when (key.text) {
                // '+' for zoom IN; '-' for zoom OUT
                "Plus", "Minus" -> head.zoom(key.text)

                // Arrow keys to move the head on the canvas
                "Right", "Left", "Up", "Down" -> head.move(key.text)

                // in case input is not what we expected, don't change anything (by returning the same object)
                else -> head
            }
            head.draw(cv) // redraw with the new properties
        }
    }

    // Called when execution ends
    onFinish {
    }
}


/**
 * Function to draw our head.
 *
 * @receiver Head object, properties will be used for drawing
 * @param cv Canvas object
 */
fun Head.draw(cv: Canvas) {
    // clean canvas
    cv.erase()

    // draw head
    cv.drawArc(x, y, radius, HEAD_START_ANG, HEAD_END_ANGLE, ANDROID_COLOR, 0)

    // draw eyes
    drawEyes(cv)

    // draw antennas
    drawAntennas(cv)
}


/**
 * Function to draw our head's eyes.
 *
 * @receiver Head object, properties will be used for drawing
 * @param cv Canvas object
 */
fun Head.drawEyes(cv: Canvas) {
    val eyeRadius = (EYE_RADIUS_DELTA * radius).roundToInt()

    // get both eyes X cords
    val xEyeOffset = (EYE_X_DELTA * radius).roundToInt()
    val eyesX = Pair(x - xEyeOffset, x + xEyeOffset)

    // get eyes Y cords
    val yEyePos = y - (EYE_Y_DELTA * radius).roundToInt()

    // draw eyes
    cv.drawCircle(eyesX.first, yEyePos, eyeRadius, WHITE, 0)
    cv.drawCircle(eyesX.second, yEyePos, eyeRadius, WHITE, 0)
}


/**
 * Function to draw our head's antennas.
 *
 * It uses some basic trigonometric math to be drawn on the right place.
 *
 * @receiver Head object, properties will be used for drawing
 * @param cv Canvas object
 */
fun Head.drawAntennas(cv: Canvas) {
    // Get antenna's angle, diameter and radius
    val antennaAngle = (ANTENNA_ANGLE_DELTA).toRadians()
    val antennaDiameter = (ANTENNA_DIAMETER_DELTA * radius).roundToInt()
    val antennaRadius = antennaDiameter / 2

    // get antenna Y cord
    val antennaBeginningY = sin(antennaAngle) * radius
    val antennaEndingY = sin(antennaAngle) * (ANTENNA_SIZE_DELTA * radius)
    val toY = y - (antennaBeginningY + antennaEndingY).roundToInt()

    // get both antennas X cords
    val antennaBeginningX = cos(antennaAngle) * radius
    val antennaEndingX = cos(antennaAngle) * (ANTENNA_SIZE_DELTA * radius)
    val antennaX = (antennaBeginningX + antennaEndingX).roundToInt()
    val toX = Pair(x - antennaX, x + antennaX) // X differs for the two antennas, Y does not

    // draw antennas line, in opposite X direction
    cv.drawLine(x, y - antennaDiameter, toX.first, toY, ANDROID_COLOR, antennaDiameter)
    cv.drawLine(x, y - antennaDiameter, toX.second, toY, ANDROID_COLOR, antennaDiameter)

    //
    val antennaFixX = antennaDiameter / 3

    // draw antenna tip
    cv.drawCircle(toX.first - antennaFixX, toY - antennaRadius, antennaRadius, ANDROID_COLOR, 0)
    cv.drawCircle(toX.second + antennaFixX, toY - antennaRadius, antennaRadius, ANDROID_COLOR, 0)
}


/**
 * Function to zoom in or out, if input is '+' or '-' respectively.
 *
 * NOTE: Only zoom OUT if antenna pixel is greater than 1 px
 *
 * @receiver Head object
 * @param keyText Key text identifier
 * @return Head object with the new proprieties
 *
 */
fun Head.zoom(keyText: String): Head {
    return when (keyText) {
        "Plus" -> Head(x, y, radius + ZOOM_DELTA)
        "Minus" -> Head(x, y, radius + (if (ANTENNA_DIAMETER_DELTA * radius > 1) -ZOOM_DELTA else 0))
        else -> this
    }
}


/**
 * Function to move the head on the canvas X and Y cords, depending on our input.
 *
 * NOTE: The head should not 'disappear' from the screen, so we have to check if cords are still valid before adding delta
 *
 * @receiver Head object
 * @param keyText Key text identifier
 * @return Head object with the new proprieties
 */
fun Head.move(keyText: String): Head {
    return when (keyText) {
        "Right" -> Head(x + (if (x > CANVAS_WIDTH) 0 else CURSOR_DELTA), y, radius)
        "Left" -> Head(x + (if (x < 0) 0 else -CURSOR_DELTA), y, radius)
        "Up" -> Head(x, y + (if (y < 0) 0 else -CURSOR_DELTA), radius)
        "Down" -> Head(x, y + (if (y > CANVAS_HEIGHT) 0 else CURSOR_DELTA), radius)
        else -> this
    }
}


/**
 * Function to convert degrees to radians. Useful for trigonometric functions.
 *
 * @receiver Degrees value to convert
 * @return Angle in radians
 */
fun Double.toRadians() = this * PI / 180.0

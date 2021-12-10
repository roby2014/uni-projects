import pt.isel.canvas.CYAN
import pt.isel.canvas.Canvas

/**
 * Represents a hit box of something.
 * What is a hit box? A hit box is an invisible shape around some object used for real-time collision detection.
 * Properties explanation: https://prnt.sc/226f46q
 * @property x1 X-axis starting point
 * @property x2 X-axis ending point
 * @property y1 Y-axis starting point
 * @property y2 Y-axis ending point
 */
data class HitBox(val x1: Int, val x2: Int, val y1: Int, val y2: Int)

/**
 * Returns true if [this] is colliding with [h].
 * This might help understand the function better: https://prnt.sc/223qw39
 */
fun HitBox.checkCollision(h: HitBox) = x1 < h.x2 && x2 > h.x1 && y1 < h.y2 && y2 > h.y1

/**
 * Draws the hit box of something. **This function is used for debugging purposes!**
 * @receiver Current hit box properties.
 * @param cv Canvas object.
 */
fun HitBox.drawHitBox(cv: Canvas) {
    cv.drawLine(x1, y1, x2, y1, CYAN, 1)
    cv.drawLine(x1, y2, x2, y2, CYAN, 1)
    cv.drawLine(x1, y1, x1, y2, CYAN, 1)
    cv.drawLine(x2, y1, x2, y2, CYAN, 1)
}

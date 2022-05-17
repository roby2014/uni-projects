import pt.isel.canvas.Canvas
import pt.isel.canvas.RED

/**
 * Draws everything about the game, if game is not over yet.
 * This will be used for refreshing the visual game information.
 * @receiver Canvas object.
 * @param g Actual game object.
 */
fun Canvas.drawGame(g: Game) {
    if (!g.over) {
        erase()
        drawSpaceship(g.ship)
        drawShots(g.alienShots, g.ship.shot)
        //drawHitBoxes(g.alienShots, g.ship) // for debugging, uncomment this line for visible hit boxes
    } else {
        drawText(g.area.w / 2 - 100, g.area.h - 8, "Game over", RED, 33)
    }
}

/**
 * Draws the spaceship and its shooter on the canvas.
 * @receiver Canvas object.
 * @param s Spaceship object.
 */
fun Canvas.drawSpaceship(s: Spaceship) {
    drawRect(s.x, s.y, SHIP_WIDTH, SHIP_HEIGHT, SHIP_COLOR, 0)
    drawRect(s.x + SHIP_WIDTH / 2, s.y - SHOOTER_HEIGHT, SHOOTER_WIDTH, SHOOTER_HEIGHT, SHOOTER_COLOR, 0)
}

/**
 * Draws on canvas the current list of shots and spaceship shot (IF IT EXISTS).
 * @receiver Current canvas object.
 * @param ls List of current shots.
 * @param ss Spaceship shot.
 */
fun Canvas.drawShots(ls: List<Shot>, ss: Shot?) {
    ls.forEach { drawRect(it.x, it.y, SHOT_WIDTH, SHOT_HEIGHT, it.color, 0) }
    if (ss != null)
        drawRect(ss.x, ss.y, SHOT_WIDTH, SHOT_HEIGHT, ss.color, 0)
}

/**
 * Draws the hit boxes of all objects. **This function is used for debugging purposes!**
 * @receiver Current canvas object.
 * @param ls List of current alien shots.
 * @param s Spaceship object.
 */
fun Canvas.drawHitBoxes(ls: List<Shot>, s: Spaceship) {
    ls.forEach { it.hitBox.drawHitBox(this) } // alien shots hit boxes
    s.hitBox.drawHitBox(this) // spaceship hit box
    s.shot?.hitBox?.drawHitBox(this) // spaceship shot hit box
}

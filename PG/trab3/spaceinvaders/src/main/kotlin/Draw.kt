import pt.isel.canvas.*

/**
 * Draws everything about the game, if game is not over yet.
 * This will be used for updating the visual game information.
 * @receiver Canvas object.
 * @param g Actual game object.
 */
fun Canvas.drawGame(g: Game) {
    when (g.gameStatus) {
        GameStatus.LOSE -> drawBottomCenteredText(text="Game Over", g, color=RED)
        GameStatus.WIN -> drawBottomCenteredText(text="You Win", g, color=GREEN)
        else -> {
            erase()
            drawAliens(g.aliens)
            drawSpaceship(g.ship)
            drawShots(g.aliens.shots, g.ship.shot)
            drawText(FONT_BORDER, height - FONT_BORDER, "${g.points}", WHITE, FONT_SIZE)
            //drawGrid() // for debugging initial grid
            //drawHitBoxes(g) // for debugging visible hit boxes
        }
    }
}

/**
 * Draws the spaceship.
 * @receiver Canvas object.
 * @param s Spaceship object.
 */
fun Canvas.drawSpaceship(s: Spaceship) {
    drawImage("spaceship", s.pos.x, s.pos.y, SHIP_WIDTH, SHIP_HEIGHT)
}

/**
 * Draws on canvas the current list of alien shots and spaceship shot (if it exists).
 * @receiver Current canvas object.
 * @param ls List of current alien shots.
 * @param ss Spaceship shot.
 */
fun Canvas.drawShots(ls: List<Shot>, ss: Shot?) {
    ls.forEach { drawRect(it.x, it.y, SHOT_WIDTH, SHOT_HEIGHT, it.color, 0) }
    if (ss != null)
        drawRect(ss.x, ss.y, SHOT_WIDTH, SHOT_HEIGHT, ss.color, 0)
}

/**
 * Draws on canvas the current list of aliens.
 * @receiver Current canvas object.
 * @param al Alien list.
 */
fun Canvas.drawAliens(al: Aliens) {
    for (a in al.invaders) {
        drawImage(
            "invaders|${a.status.start},${a.type.start},$ALIEN_WIDTH,$ALIEN_HEIGHT",
            a.pos.x,
            a.pos.y,
            CELL_WIDTH,
            CELL_HEIGHT
        )
    }
}

/**
 * Draws the hit box of something.
 * **This function is used for debugging purposes!**
 * @receiver Canvas object.
 * @param h Hit box that will be drawn.
 */
fun Canvas.drawHitBox(h: HitBox?) {
    if (h != null) {
        drawLine(h.x1, h.y1, h.x2, h.y1, CYAN, 1)
        drawLine(h.x1, h.y2, h.x2, h.y2, CYAN, 1)
        drawLine(h.x1, h.y1, h.x1, h.y2, CYAN, 1)
        drawLine(h.x2, h.y1, h.x2, h.y2, CYAN, 1)
    }
}

/**
 * Draws all the object hit boxes from the current game.
 * **This function is used for debugging purposes!**
 * @receiver Current canvas object.
 * @param g Current game object.
 */
fun Canvas.drawHitBoxes(g: Game) {
    g.aliens.shots.forEach { drawHitBox(it.hitBox) } // alien shots hit boxes
    drawHitBox(g.ship.hitBox) // spaceship hit box
    drawHitBox(g.ship.shot?.hitBox) // spaceship shot hit box
    g.aliens.invaders.forEach { drawHitBox(it.hitBox) } // aliens hit box
}

/**
 * Draws a simple 11*5 grid.
 * **This function is used for debugging purposes!**
 * @receiver Current canvas object.
 */
fun Canvas.drawGrid() {
    (1..MAX_COLUMNS).forEach {
        drawLine(it * CELL_WIDTH, 1, it * CELL_WIDTH, height, WHITE, 1)
    }

    (1..MAX_ROWS).forEach {
        drawLine(1, it * CELL_HEIGHT, width, it * CELL_HEIGHT, WHITE, 1)
    }
}

/**
 * Draws text centered on the bottom.
 * This is used for "Game Over" and "You Win"
 * @receiver Current canvas object.
 * @param text String to display.
 * @param g Current game object.
 */
fun Canvas.drawBottomCenteredText(text: String, g: Game, color: Int) {
    drawText(x=g.area.w / 2 - 50, y=g.area.h - FONT_BORDER, text, color, FONT_SIZE)
}
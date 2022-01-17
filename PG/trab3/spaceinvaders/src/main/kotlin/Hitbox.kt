/**
 * Represents a hit box of something.
 * A hit box is an invisible shape around some object used for real-time collision detection.
 * @property x1 X-axis starting point
 * @property x2 X-axis ending point
 * @property y1 Y-axis starting point
 * @property y2 Y-axis ending point
 */
data class HitBox(val x1: Int, val x2: Int, val y1: Int, val y2: Int)

/**
 * Returns a hitBox object for the spaceship where [x] is spaceship's current x coordinate.
 */
fun hitBoxSpaceship(x: Int) =
    HitBox(x1 = x, x2 = x + SHIP_WIDTH, y1 = SHIP_MOVING_LANE_Y + SHOOTER_HEIGHT, y2 = SHIP_MOVING_LANE_Y + SHIP_HEIGHT)

/**
 * Returns a hit box object for an alien. Each alien will have a different hit box because it has different size.
 * We apply padding to alien hit boxes because they have different size.
 * This function tries to set the "best"/most accurate hit box for the alien, depending on its movement.
 * @param cords Alien coordinates.
 * @param right Is alien going right? [Direction]
 * @param type Alien type. [AlienType]
 * @return Returns an alien hit box.
 */
fun hitBoxAlien(cords: Point, right: Boolean = true, type: AlienType = AlienType.SQUID): HitBox {
    val (xx1,xx2) = when (type) { // hit box padding depending on alien type (size)
        AlienType.SQUID -> 10 to 0
        AlienType.CRAB, AlienType.OCTOPUS -> 5 to 5 // Crab and Octopus are the same size.
    }

    return HitBox(
        x1 = cords.x + if (right) xx1 else -xx2,
        x2 = cords.x + CELL_WIDTH + if (right) xx2 else -xx1,
        y1 = cords.y,
        y2 = cords.y + CELL_HEIGHT,
    )
}

/**
 * Returns true if receiver ([this]) is colliding with [h].
 * This might help understand the function better: https://prnt.sc/223qw39
 */
fun HitBox.checkCollision(h: HitBox) = x1 < h.x2 && x2 > h.x1 && y1 < h.y2 && y2 > h.y1

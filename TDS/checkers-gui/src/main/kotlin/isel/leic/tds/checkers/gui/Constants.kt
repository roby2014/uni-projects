package isel.leic.tds.checkers.gui

import isel.leic.tds.checkers.model.BOARD_DIM
import java.awt.GraphicsEnvironment
import java.awt.Rectangle

// Database credentials
const val CONNECTION_STRING = "mongodb://Grupo04:MgUoyafupMf0yhht" +
        "@ac-twljvbj-shard-00-00.hmkwhou.mongodb.net:27017," +
        "ac-twljvbj-shard-00-01.hmkwhou.mongodb.net:27017," +
        "ac-twljvbj-shard-00-02.hmkwhou.mongodb.net:27017/?" +
        "ssl=true&replicaSet=atlas-2je3qn-shard-0&authSource=admin&retryWrites=true&w=majority"
const val DATABASE_NAME = "LEIC32D-Grupo04"
const val COLLECTION_NAME = "Checkers"

// Default refresh targets time (3000ms = 3s)
const val AUTO_REFRESH_TARGETS_TIME = 3000L

// Dimensions that depend on the player's screen.
// This allows to visualize everything even on smaller monitors.
// It uses some magic numbers that were gotten via testing on different monitors.
val SCREEN_SIZE: Rectangle = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
val MAGIC_DIVISOR = if (SCREEN_SIZE.width > 1400) { 3 } else { 4 }
val WND_WIDTH = (SCREEN_SIZE.width / MAGIC_DIVISOR) + 120 // magic
val CELL_SIZE = WND_WIDTH / (BOARD_DIM + 1)
val WND_HEIGHT = WND_WIDTH + (CELL_SIZE * 2) // for bottom "status" bar
val TEXT_FONT_SIZE = CELL_SIZE / 40
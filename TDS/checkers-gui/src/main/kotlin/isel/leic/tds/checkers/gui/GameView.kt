package isel.leic.tds.checkers.gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import isel.leic.tds.checkers.model.*
import kotlin.math.abs

/**
 * Draws the board.
 * @param onCellClick called when user clicks a cell
 */
@Composable
fun BoardView(
    fromSquare: Square?,
    board: Board?,
    targetOption: Boolean,
    player: Player?,
    onCellClick: (Square) -> Unit
) {
    Column {

        Row {
            Column {
                // first, draw a,b,c,d,e,.... columns
                Row {
                    CellInfo(" ") // the first one is empty!
                    repeat(BOARD_DIM) { idx ->
                        CellInfo(idx.indexToColumn().symbol.toString())
                    }
                }

                repeat(BOARD_DIM) { rowIdx ->
                    Row {
                        // first cell of each row is the row index (8,7,6,5...)
                        CellInfo(rowIdx.indexToRow().number.toString())

                        // draw the cells
                        repeat(BOARD_DIM) { col ->
                            val move = board?.pieces?.find { it.pos == Square(rowIdx, col) }
                            val symbol = when (move?.isQueen) {
                                true -> move.player.queenSymbol
                                false -> move.player.symbol
                                null -> ' '
                            }
                            val isBlack = move?.pos?.black ?: Square(rowIdx, col).black
                            val fromPiece = board?.pieces?.find { it.pos == fromSquare }
                            val isValidToPos =
                                move == null && targetOption
                                        && squareValidToPos(fromPiece, Square(rowIdx, col), board?.pieces)
                            Cell(symbol.toString(), Square(rowIdx, col), isBlack, isValidToPos, onCellClick)
                        }
                    }
                }
            }
        }
        if (player != null) {
            Row {
                when (board) {
                    is BoardRun -> showYourPlayerAndCurrentPlayer(player, board.currentPlayer)

                    is BoardStarting -> showYourPlayerAndCurrentPlayer(player, board.currentPlayer)

                    else -> {
                    }
                }
            }

        }
    }

}

/**
 * Displays a bottom element ("status" bar) with player's color and current turn player
 * **/
@Composable
fun showYourPlayerAndCurrentPlayer(player: Player, currentPlayer: Player) {
    Text("You are: ${player.symbol}", Modifier.size(CELL_SIZE.dp * 3), fontSize = TEXT_FONT_SIZE.em)
    symbolPiece(player.symbol, true, CELL_SIZE.dp)
    Text("Current turn: ${currentPlayer.symbol}", Modifier.size(CELL_SIZE.dp * 3), fontSize = TEXT_FONT_SIZE.em)
    symbolPiece(currentPlayer.symbol, true, CELL_SIZE.dp)
}

/**
 * Verifies if the square is a valid target.
 * If returns true, that square will turn green by Cell to indicate to user that it is a valid target.
 */
fun squareValidToPos(fromPiece: Piece?, toSquare: Square, pieces: List<Piece>?): Boolean {
    if (fromPiece == null || pieces == null) return false
    /*
        Verifies if there's any capture.
    */
    val captureToPosList = getAllPlayerPossibleCaptures(fromPiece.player, pieces)
    return if (captureToPosList.isEmpty()) {
        /*
            If there is none, normal movement rules.
        */
        val hDistance = toSquare.column.index - fromPiece.pos.column.index
        val vDistance = toSquare.row.index - fromPiece.pos.row.index
        if (!fromPiece.isQueen) {
            if (abs(vDistance) != 1 || abs(hDistance) != 1) return false
            return when (fromPiece.player) {
                Player.WHITE -> abs(hDistance) == 1 && vDistance == -1
                Player.BLACK -> abs(hDistance) == 1 && vDistance == 1
            }
        }
        abs(hDistance) == abs(vDistance)
    }
    /*
        If there is any, verifies if toSquare belongs in the list of possible positions in captures made
        by fromPiece.
     */
    else captureToPosList
        .filter { capture -> capture.captor == fromPiece }
        .map { capture -> capture.pos }
        .contains(toSquare)
}

/**
 * Draws a board cell square.
 * @param symbol symbol to be displayed (w/b)
 * @param position the cell's position
 * @param isBlack true if the cell square background is black
 * @param onCellClick called when cell is pressed
 */
@Composable
fun Cell(symbol: String, position: Square, isBlack: Boolean, isValidToPos: Boolean, onCellClick: (Square) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(CELL_SIZE.dp)
            .border(1.dp, Color.Gray)
            .background(if (isValidToPos) Color.Green else if (isBlack) Color.Black else Color.White)
            .clickable(onClick = { onCellClick(position) })
    ) {
        symbolPiece(symbol, isBlack, CELL_SIZE.dp)
    }
}

/**
 * In case [symbol] is not a valid piece symbol, it returns a "Text" element with the symbol,
 * otherwise, returns the piece image, depending on [imageSize]
 */
@Composable
fun symbolPiece(symbol: String, isBlack: Boolean, imageSize: Dp) {
    val symbolImages = hashMapOf(
        "b" to "piece_b.png",
        "w" to "piece_w.png",
        "B" to "piece_bk.png",
        "W" to "piece_wk.png"
    )

    return when (val img = symbolImages[symbol]) {
        null -> Text(symbol, fontSize = 3.em, color = if (isBlack) Color.White else Color.Black)
        else -> Image(
            painter = painterResource(img),
            contentDescription = img,
            modifier = Modifier.size(imageSize)
        )
    }
}

/**
 * Draws a board info cell. (abcdef, 8765432..)
 */
@Composable
fun CellInfo(symbol: String) {
    when {
        // first top left cell ("empty")
        symbol == " " -> {
            Column(
                modifier = Modifier
                    .height((CELL_SIZE / 2).dp)
                    .width((CELL_SIZE / 2).dp)
                    .background(Color.White)
            )
            {
                Text(
                    symbol,
                    fontSize = 1.em,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                )
            }
        }

        // letter (top letters)
        symbol.toIntOrNull() == null -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .height((CELL_SIZE / 2).dp)
                    .width(CELL_SIZE.dp)
                    .background(Color.White)
            ) {
                Text(symbol, fontSize = 1.em, fontStyle = FontStyle.Italic, color = Color.Black)
            }
        }

        // left numbers / row indices
        else -> {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width((CELL_SIZE / 2).dp)
                    .height(CELL_SIZE.dp)
                    .background(Color.White)
                    .padding(5.dp)
            ) {
                Text(symbol, fontSize = 1.em, fontStyle = FontStyle.Italic, color = Color.Black)
            }
        }
    }
}

/**
 * Top menu bar.
 */
@Composable
fun FrameWindowScope.CheckersTopMenuBar(
    onGameStart: (String) -> Unit,
    onGameLoad: (String) -> Unit,
    onGameRefresh: () -> Unit,
    onGameExit: () -> Unit,
    onOptionsTargets: () -> Unit,
    onAutoRefreshTargets: () -> Unit,
    onSetRefreshTime: (Int) -> Unit,
    options: Pair<Boolean, Boolean> // first is show targets, second is auto refresh
) {
    // some dialogs that will be used
    val (newGameDialog, setNewGameDialog) = remember { mutableStateOf(false) }
    if (newGameDialog) {
        DialogInput(
            onSelect = { name ->
                onGameStart(name)
                setNewGameDialog(false)
            },
            onCancel = { setNewGameDialog(false) },
            title = "New game",
            textBoxText = "Game name",
            selectText = "Start game",
            cancelText = "Cancel"
        )
    }
    val (loadGameDialog, setLoadGameDialog) = remember { mutableStateOf(false) }
    if (loadGameDialog) {
        DialogInput(
            onSelect = { name ->
                onGameLoad(name)
                setLoadGameDialog(false)
            },
            onCancel = { setLoadGameDialog(false) },
            title = "Connect to existing game",
            textBoxText = "Game name",
            selectText = "Connect",
            cancelText = "Cancel"
        )
    }

    val (refreshTimeDialog, setRefreshTimeDialog) = remember { mutableStateOf(false) }
    if (refreshTimeDialog) {
        DialogInput(
            onSelect = { time ->
                onSetRefreshTime(time.toInt() * 1000)
                setRefreshTimeDialog(false)
            },
            onCancel = { setLoadGameDialog(false) },
            title = "Set custom refresh time",
            textBoxText = "Time (in seconds)",
            selectText = "Set",
            cancelText = "Cancel"
        )
    }

    // menu bar with all the options
    MenuBar {
        Menu("Game", mnemonic = 'G') {
            Item("Start", onClick = { setNewGameDialog(true) })
            Item("Connect", onClick = { setLoadGameDialog(true) })
            Item("Refresh", onClick = onGameRefresh)
            Item("Exit", onClick = onGameExit)
        }
        Menu("Options", mnemonic = 'O') {
            Item("${if (options.first) "Hide" else "Show"} Targets", onClick = onOptionsTargets)
            Item("${if (options.second) "Disable" else "Enable"} Auto-Refresh", onClick = onAutoRefreshTargets)
            Item("Set Refresh Time", onClick = { setRefreshTimeDialog(true) })
        }
    }
}

/**
 * Dialog input box generic function.
 * @param onSelect Function that will run after text is "selected".
 * @param onCancel Function that runs after "canceling".
 * @param title Title of dialog input box.
 * @param textBoxText Text that shows lightly above the input text box.
 * @param selectText Text for the "select" button.
 * @param cancelText Text for the "cancel" button.
 */
@Composable
fun DialogInput(
    onSelect: (String) -> Unit,
    onCancel: () -> Unit,
    title: String,
    textBoxText: String,
    selectText: String,
    cancelText: String
) = Dialog(
    onCloseRequest = onCancel,
    title = title,
    state = DialogState(height = Dp.Unspecified, width = 350.dp)
) {
    val (value, setValue) = remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value,
            onValueChange = { setValue(it) },
            label = { Text(textBoxText) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (value.isNotBlank()) onSelect(value)
                }
            ) {
                Text(selectText)
            }
            Button(onClick = onCancel) {
                Text(cancelText)
            }
        }
    }
}

/**
 * Dialog box that displays a message.
 * @param msg Message to be placed in message box.
 * @param onClose Handler that is called when user presses the 'x' button.
 */
@Composable
fun DialogMessage(msg: String, onClose: () -> Unit) {
    if (msg != "")
        Dialog(
            onCloseRequest = onClose,
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                width = 200.dp,
                height = 200.dp
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                    Text(msg, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
}
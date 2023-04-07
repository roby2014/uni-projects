package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.model.Player.BLACK
import isel.leic.tds.checkers.model.Player.WHITE
import kotlin.test.*

class BoardTest {

    @Test
    fun `boardwinner play should throw because game ended`() {
        val board = BoardWinner(emptyList(), WHITE)
        val dummySquare = "3c".toSquareOrNull()!!
        assertEquals(
            "The player WHITE already won this game",
            assertFailsWith<IllegalStateException> { board.play(dummySquare, dummySquare, WHITE) }.message
        )
    }

    @Test
    fun `boarddraw play should throw because game ended`() {
        val board = BoardDraw(emptyList())
        val dummySquare = "3c".toSquareOrNull()

        assertNotNull(dummySquare)
        assertEquals(
            "This game has already finished with a draw",
            assertFailsWith<IllegalStateException> {
                board.play(dummySquare, dummySquare, BLACK)
            }.message
        )
    }

    @Test
    fun `boardstarting creating boardrun after 2 plays`() {
        val board = BoardStarting(currentPlayer = WHITE)
        val boardFirstPlay = board.play("3a".toSquareOrNull()!!, "4b".toSquareOrNull()!!, WHITE)
        assertTrue(boardFirstPlay is BoardStarting)

        val boardSecondPlay = boardFirstPlay.play("6b".toSquareOrNull()!!, "5a".toSquareOrNull()!!, BLACK)
        assertTrue(boardSecondPlay is BoardRun)
    }

    @Test
    fun `boardrun invalid player trying to make a move`() {
        val board = BoardStarting(currentPlayer = BLACK)
        val dummySquare = "3c".toSquareOrNull()
        assertNotNull(dummySquare)

        val ex = assertFailsWith<IllegalArgumentException> {
            board.play(dummySquare, dummySquare, WHITE)
        }
        assertEquals("You cannot play twice", ex.message)
    }

    @Test
    fun `boardrun play to a occupied square`() {
        val board = BoardStarting(currentPlayer = WHITE)
        val dummySquare = "3c".toSquareOrNull()
        assertNotNull(dummySquare)

        val ex = assertFailsWith<IllegalArgumentException> {
            board.play(dummySquare, dummySquare, WHITE)
        }
        assertEquals("Square '3c' occupied", ex.message)
    }

    @Test
    fun `boardrun non-queen white piece going down should throw exception`() {
        val whitePiece = Piece(WHITE, "3c".toSquareOrNull()!!)
        val board = BoardRun(listOf(whitePiece), WHITE)
        val ex = assertFailsWith<IllegalArgumentException> {
            board.play("3c".toSquareOrNull()!!, "2b".toSquareOrNull()!!, WHITE)
        }
        assertEquals("You can't play backwards with a non Queen piece!", ex.message)
    }

    @Test
    fun `boardrun non-queen black piece going up should throw exception`() {
        val blackPiece = Piece(BLACK, "7c".toSquareOrNull()!!)
        val board = BoardRun(listOf(blackPiece), BLACK)
        val ex = assertFailsWith<IllegalArgumentException> {
            board.play("7c".toSquareOrNull()!!, "8b".toSquareOrNull()!!, BLACK)
        }
        assertEquals("You can't play backwards with a non Queen piece!", ex.message)
    }

    @Test
    fun `boardrun moving a piece that doesn't exist throws exception`() {
        val whitePiece = Piece(WHITE, "3c".toSquareOrNull()!!)
        val board = BoardRun(listOf(whitePiece), WHITE)
        val ex = assertFailsWith<IllegalArgumentException> {
            board.play("4b".toSquareOrNull()!!, "5a".toSquareOrNull()!!, WHITE)
        }
        assertEquals("No such piece in that square exists", ex.message)
    }

    @Test
    fun `boardrun current player trying to move other player's pieces throws exception`() {
        val blackPiece = Piece(BLACK, "7c".toSquareOrNull()!!)
        val board = BoardRun(listOf(blackPiece), WHITE)
        val ex = assertFailsWith<IllegalArgumentException> {
            board.play("7c".toSquareOrNull()!!, "6b".toSquareOrNull()!!, WHITE)
        }
        assertEquals("You cannot move the other player's pieces", ex.message)
    }

    @Test
    fun `testing a normal move`() {
        val whitePiece = Piece(WHITE, "3a".toSquareOrNull()!!)
        val blackPiece = Piece(BLACK, "6b".toSquareOrNull()!!)
        val board = BoardRun(listOf(whitePiece, blackPiece), WHITE)
        val boardNew = board.play("3a".toSquareOrNull()!!, "4b".toSquareOrNull()!!, WHITE)
        val newWhitePiece = boardNew.pieces.first { it.player == WHITE }
        assertEquals("4b".toSquareOrNull()!!, newWhitePiece.pos)
    }

    @Test
    fun `boardrun moving a piece two rows forward without capture throws exception`() {
        /**
         * 7
         * 6        b
         * 5
         * 4
         * 3    w
         * 2
         * 1
         *      a   b   c   d
         */

        val whitePiece = Piece(WHITE, "3a".toSquareOrNull()!!)
        val blackPiece = Piece(BLACK, "6b".toSquareOrNull()!!)
        val ex1 = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun((0..1).map { if (it == 0) whitePiece else blackPiece }, WHITE)
            board.play("3a".toSquareOrNull()!!, "5c".toSquareOrNull()!!, WHITE)
        }
        assertEquals("You can only move one row forward if there is no capture", ex1.message)

        val ex2 = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun((0..1).map { if (it == 0) whitePiece else blackPiece }, BLACK)
            board.play("6b".toSquareOrNull()!!, "4d".toSquareOrNull()!!, BLACK)
        }
        assertEquals("You can only move one row forward if there is no capture", ex2.message)
    }

    @Test
    fun `boardrun not moving a piece diagonally will throw exception`() {
        /**
         * 7
         * 6        b
         * 5
         * 4
         * 3    w
         * 2
         * 1
         *      a   b   c   d   e
         */

        val whitePiece = Piece(WHITE, "3a".toSquareOrNull()!!)
        val blackPiece = Piece(BLACK, "6b".toSquareOrNull()!!)
        val ex1 = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun((0..1).map { if (it == 0) whitePiece else blackPiece }, WHITE)
            board.play("3a".toSquareOrNull()!!, "4d".toSquareOrNull()!!, WHITE)
        }
        assertEquals("You can only move in diagonals", ex1.message)

        val ex2 = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun((0..1).map { if (it == 0) whitePiece else blackPiece }, BLACK)
            board.play("6b".toSquareOrNull()!!, "5e".toSquareOrNull()!!, BLACK)
        }
        assertEquals("You can only move in diagonals", ex2.message)
        val ex3 = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun((0..1).map { if (it == 0) whitePiece else blackPiece }, WHITE)
            board.play("3a".toSquareOrNull()!!, "5a".toSquareOrNull()!!, WHITE)
        }
        assertEquals("You can only move in diagonals", ex3.message)

        val ex4 = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun((0..1).map { if (it == 0) whitePiece else blackPiece }, BLACK)
            board.play("6b".toSquareOrNull()!!, "4b".toSquareOrNull()!!, BLACK)
        }
        assertEquals("You can only move in diagonals", ex4.message)
    }

    @Test
    fun `boardrun usual exception`() {
        /**
         * 8
         * 7
         * 6
         * 5    b   w
         * 4
         * 3    w
         * 2
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(WHITE, "3a".toSquareOrNull()!!),
            Piece(WHITE, "5c".toSquareOrNull()!!),
            Piece(BLACK, "5a".toSquareOrNull()!!),
        )
        val board = BoardRun(piecesList, BLACK)
            .play("5a".toSquareOrNull()!!, "4b".toSquareOrNull()!!, BLACK)
    }

    @Test
    fun `boardrun check when piece is converted to queen`() {
        /**
         * 8
         * 7            w
         * 6
         * 5
         * 4
         * 3        b
         * 2
         * 1
         *      a   b   c   d
         */

        val whitePiece = Piece(WHITE, "7c".toSquareOrNull()!!)
        val blackPiece = Piece(BLACK, "2b".toSquareOrNull()!!)
        val board = BoardRun(listOf(whitePiece, blackPiece), WHITE)

        val newBoard = board.play("7c".toSquareOrNull()!!, "8b".toSquareOrNull()!!, WHITE)
            .play("2b".toSquareOrNull()!!, "1c".toSquareOrNull()!!, BLACK)

        val whiteQueenPiece = newBoard.pieces.first { it.player == WHITE }
        val blackQueenPiece = newBoard.pieces.first { it.player == BLACK }
        assertTrue(whiteQueenPiece.isQueen && blackQueenPiece.isQueen)
    }

    @Test
    fun `boardrun forced capture test`() {
        /**
         * 3            b
         * 2        w
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(WHITE, "2b".toSquareOrNull()!!),
            Piece(BLACK, "3c".toSquareOrNull()!!),
        )
        val ex = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun(piecesList, WHITE)
            board.play("2b".toSquareOrNull()!!, "3a".toSquareOrNull()!!, WHITE)
        }
        assertEquals("There is a mandatory capture in ${piecesList[1].pos}", ex.message)
    }

    @Test
    fun `boardrun cant force capture because destination position occupied`() {
        /**
         * 6
         * 5            b
         * 4        w
         * 3    w
         * 2
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(BLACK, "5c".toSquareOrNull()!!),
            Piece(WHITE, "4b".toSquareOrNull()!!),
            Piece(WHITE, "3a".toSquareOrNull()!!)
        )

        val exception = assertFailsWith<IllegalArgumentException> {
            val board = BoardRun(piecesList, BLACK)
            board.play("5c".toSquareOrNull()!!, "3a".toSquareOrNull()!!, BLACK)
        }
        assertEquals("Square '${piecesList[2].pos}' occupied", exception.message)
    }

    @Test
    fun `boardrun cant force capture because destination position occupied 2`() {
        /**
         * 6
         * 5            b
         * 4        w
         * 3    w
         * 2
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(BLACK, "5c".toSquareOrNull()!!),
            Piece(WHITE, "4b".toSquareOrNull()!!),
            Piece(WHITE, "3a".toSquareOrNull()!!)
        )
        val board = BoardRun(piecesList, BLACK)
            .play("5c".toSquareOrNull()!!, "4d".toSquareOrNull()!!, BLACK)
        assertNotNull(board)
        assertEquals(3, board.pieces.size)
    }

    @Test
    fun `boardrun white is forced to capture 5c and does not do it`() {
        /**
         * 6
         * 5            b
         * 4        w
         * 3            w
         * 2
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(WHITE, "4b".toSquareOrNull()!!),
            Piece(WHITE, "3c".toSquareOrNull()!!),
            Piece(BLACK, "5c".toSquareOrNull()!!)
        )

        val exception = assertFailsWith<IllegalArgumentException> {
            BoardRun(piecesList, WHITE).play("3c".toSquareOrNull()!!, "4d".toSquareOrNull()!!, WHITE)
        }
        assertEquals("There is a mandatory capture in ${piecesList[2].pos}", exception.message)
    }

    @Test
    fun `boardrun white piece going backwards to capture`() {
        /**
         * 5
         * 4        w
         * 3            b
         * 2
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(WHITE, "4b".toSquareOrNull()!!),
            Piece(BLACK, "3c".toSquareOrNull()!!),
        )
        val board = BoardRun(piecesList, WHITE).play("4b".toSquareOrNull()!!, "2d".toSquareOrNull()!!, WHITE)
        assertEquals(board.pieces.size, 1)
    }

    @Test
    fun `boardrun white is forced to capture 5c and does it`() {
        /**
         * 6
         * 5            b
         * 4        w
         * 3            w
         * 2
         * 1
         *      a   b   c   d
         */

        val piecesList = listOf(
            Piece(WHITE, "4b".toSquareOrNull()!!),
            Piece(WHITE, "3c".toSquareOrNull()!!),
            Piece(BLACK, "5c".toSquareOrNull()!!)
        )

        val board = BoardRun(piecesList, WHITE)
        assertEquals(3, board.pieces.size)

        val from = piecesList[0].pos
        val to = "6d".toSquareOrNull()!!
        val newBoard = board.play(from, to, WHITE)

        assertEquals(2, newBoard.pieces.size)
        assert(newBoard.pieces.contains(Piece(WHITE, to)))
        assert(newBoard.pieces.contains(piecesList[1]))
    }

    @Test
    fun `boardrun capture test - player cannot play again since there isn't another capture`() {
        /**
         * 5                b
         * 4
         * 3            b
         * 2        w
         * 1
         *      a   b   c   d
         */

        val pieces = listOf(
            Piece(WHITE, "2b".toSquareOrNull()!!),
            Piece(BLACK, "3c".toSquareOrNull()!!),
            Piece(BLACK, "5d".toSquareOrNull()!!)
        )

        val board = BoardRun(pieces, WHITE).play("2b".toSquareOrNull()!!, "4d".toSquareOrNull()!!, WHITE)

        assertTrue(board is BoardRun)
        assertEquals(2, board.pieces.size)
        assertEquals(BLACK, board.currentPlayer)
    }


    @Test
    fun `boardrun capture test - player can play again since there is another capture`() {
        /**
         * 6
         * 5            b
         * 4
         * 3            b
         * 2        w
         * 1
         *      a   b   c   d
         */

        val pieces = listOf(
            Piece(WHITE, "2b".toSquareOrNull()!!),
            Piece(BLACK, "3c".toSquareOrNull()!!),
            Piece(BLACK, "5c".toSquareOrNull()!!)
        )

        val board = BoardRun(pieces, WHITE).play("2b".toSquareOrNull()!!, "4d".toSquareOrNull()!!, WHITE)

        assertTrue(board is BoardRun)
        assertEquals(2, board.pieces.size)
        assertEquals(WHITE, board.currentPlayer)
    }

    @Test
    fun `boardrun winning state`() {
        /**
         * 4
         * 3            b
         * 2        w
         * 1
         *      a   b   c   d
         */
        val pieces = listOf(
            Piece(WHITE, "2b".toSquareOrNull()!!),
            Piece(BLACK, "3c".toSquareOrNull()!!)
        )
        val board = BoardRun(pieces, WHITE).play("2b".toSquareOrNull()!!, "4d".toSquareOrNull()!!, WHITE)
        assertTrue(board is BoardWinner)
        assertEquals(WHITE, board.winner)
    }

    @Test
    fun `boardrun queen capture moving different rows and wins`() {
        /**
         * 4
         * 3            w
         * 2
         * 1    B
         *      a   b   c   d
         */
        val pieces = listOf(
            Piece(BLACK, "1a".toSquareOrNull()!!, isQueen = true),
            Piece(WHITE, "3c".toSquareOrNull()!!)
        )
        val board = BoardRun(pieces, BLACK).play("1a".toSquareOrNull()!!, "4d".toSquareOrNull()!!, BLACK)
        assertTrue(board is BoardWinner)
        assertEquals(BLACK, board.winner)
    }

    @Test
    fun `boardrun queen capture moving different rows only 1 offset and game continues`() {
        /**
         * 4    w
         * 3            w
         * 2
         * 1    B
         *      a   b   c   d
         */
        val pieces = listOf(
            Piece(BLACK, "1a".toSquareOrNull()!!, isQueen = true),
            Piece(WHITE, "3c".toSquareOrNull()!!),
            Piece(WHITE, "4a".toSquareOrNull()!!)
        )
        val board = BoardRun(pieces, BLACK)
            .play("1a".toSquareOrNull()!!, "4d".toSquareOrNull()!!, BLACK)
        assertTrue(board is BoardRun)
        assert(board.pieces.size == 2)
        assert(board.pieces.first { it.isQueen }.pos.toString() == "4d")
    }

    @Test
    fun `boardrun queen capture moving different rows and game continues`() {
        /**
         * 6
         * 5
         * 4    w
         * 3            w
         * 2
         * 1    B
         *      a   b   c   d   e   f
         */
        val pieces = listOf(
            Piece(BLACK, "1a".toSquareOrNull()!!, isQueen = true),
            Piece(WHITE, "3c".toSquareOrNull()!!),
            Piece(WHITE, "4a".toSquareOrNull()!!)
        )
        val board = BoardRun(pieces, BLACK).play("1a".toSquareOrNull()!!, "5e".toSquareOrNull()!!, BLACK)
        assertTrue(board is BoardRun)
        assert(board.pieces.size == 2)
        assert(board.pieces.first { it.isQueen }.pos.toString() == "5e")
    }

    @Test
    fun `boardrun queen capture even when blocked`() {
        /**
         * 6
         * 5
         * 4                W
         * 3            b
         * 2        b
         * 1
         *      a   b   c   d   e   f
         */
        val pieces = listOf(
            Piece(BLACK, "2b".toSquareOrNull()!!),
            Piece(BLACK, "3c".toSquareOrNull()!!),
            Piece(WHITE, "4d".toSquareOrNull()!!, isQueen = true)
        )
        assertFailsWith<IllegalArgumentException> {
            BoardRun(pieces, WHITE)
                .play("4d".toSquareOrNull()!!, "1a".toSquareOrNull()!!, WHITE)
        }
    }

    @Test
    fun `boardrun queen capture even when blocked 2`() {
        /**
         * 6                         W
         * 5                     b
         * 4                b
         * 3
         * 2        b
         * 1
         *      a   b   c   d   e   f
         */
        val pieces = listOf(
            Piece(BLACK, "2b".toSquareOrNull()!!),
            Piece(BLACK, "4d".toSquareOrNull()!!),
            Piece(BLACK, "5e".toSquareOrNull()!!),
            Piece(WHITE, "6f".toSquareOrNull()!!, isQueen = true)
        )
        assertFailsWith<IllegalArgumentException> {
            BoardRun(pieces, WHITE)
                .play("6f".toSquareOrNull()!!, "1a".toSquareOrNull()!!, WHITE)
        }
    }

    @Test
    fun `boardrun unordinary queen exception`() {
        /**
         * 6
         * 5
         * 4                    w
         * 3
         * 2        B
         * 1
         *      a   b   c   d   e   f
         */
        val pieces = listOf(
            Piece(BLACK, "2b".toSquareOrNull()!!, isQueen = true),
            Piece(WHITE, "4e".toSquareOrNull()!!)
        )
        BoardRun(pieces, BLACK)
            .play("2b".toSquareOrNull()!!, "3c".toSquareOrNull()!!, BLACK)
    }

    @Test
    fun `boardrun ends in a draw once it reaches stalemate`() {
        val pieces = listOf(
            Piece(WHITE, "1a".toSquareOrNull()!!),
            Piece(BLACK, "7a".toSquareOrNull()!!)
        )
        val board = BoardRun(pieces, WHITE, 14)
            .play("1a".toSquareOrNull()!!, "2b".toSquareOrNull()!!, WHITE)
        assertTrue(board is BoardDraw)
    }

    @Test
    fun `boardrun plays 2 times because can capture`() {
        /**
         * 6
         * 5            b
         * 4        b
         * 3                    b
         * 2                        w
         * 1
         *      a   b   c   d   e   f
         */
        val pieces = listOf(
            Piece(WHITE, "2f".toSquareOrNull()!!),
            Piece(BLACK, "3e".toSquareOrNull()!!),
            Piece(BLACK, "4b".toSquareOrNull()!!),
            Piece(BLACK, "5c".toSquareOrNull()!!)
        )
        val board = BoardRun(pieces, WHITE, 10)
            .play("2f".toSquareOrNull()!!, "4d".toSquareOrNull()!!, WHITE)
            .play("4d".toSquareOrNull()!!, "6b".toSquareOrNull()!!, WHITE)
    assert(board.pieces.size == 2)

    }


}



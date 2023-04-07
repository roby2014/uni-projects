package isel.leic.tds.checkers.model

enum class Player(val symbol: String, val queenSymbol: String) {
    WHITE("w", "W"),
    BLACK("b", "B");

    fun nextPlayer() = if(this == WHITE) BLACK else WHITE

    /**
     * Turns the contents of the instance of Player and turns it into a string.
     * @return String of the contents of the player.
     */
    fun serialize() = if(this == WHITE) "WHITE" else "BLACK"

    companion object {
        /**
         * Processes the string and converts it into a player.
         * @param input String with contents of the plauer.
         * @return The player indicated in the string.
         * @throws IllegalStateException If no valid Player is read.
         */
        fun deserialize(input: String) = when(input){
            "WHITE" -> WHITE
            "BLACK" -> BLACK
            else -> throw IllegalArgumentException("That is not a valid player!")
        }
    }
}

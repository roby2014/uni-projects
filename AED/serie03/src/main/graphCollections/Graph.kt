package series.serie3.graphCollections

interface Graph<I, D> {
    interface Vertex<I, D> {
        val id: I
        val data: D
        fun setData(newData: D): D
        fun getAdjacencies(): MutableSet<Edge<I>?>
    }

    interface Edge<I> {
        val id: I
        val adjacent: I
    }

    val size: Int
    fun addVertex(id: I, d: D): D?
    fun addEdge(id: I, idAdj: I): I?
    fun getVertex(id: I): Vertex<I, D>?
    fun getEdge(id: I, idAdj: I): Edge<I>?
    operator fun iterator(): Iterator<Vertex<I, D>>
    fun edgesIterator(): Iterator<Edge<I>>
}
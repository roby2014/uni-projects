package series.serie3

import series.serie3.graphCollections.Graph

/**
 * GraphStructure
 *
 * Represents a Graph with vertices and edges.
 * @param I the type of vertices and edges ID
 * @param D the type of data that each vertex holds
 */
class GraphStructure<I, D> : Graph<I, D> {

    /**
     * Stores how many vertices the graph has.
     */
    override var size: Int = 0

    /**
     * Stores the graph vertices.
     */
    private val vertices = mutableListOf<VertexStructure<I, D>>()

    /**
     * VertexStructure
     * @param id Vertex id.
     * @param data Vertex data.
     */
    inner class VertexStructure<I, D>(override val id: I, override var data: D) : Graph.Vertex<I, D> {
        /**
         * Stores the edges of each vertex.
         */
        private val edges = mutableSetOf<Graph.Edge<I>?>()

        /**
         * Sets [data] to [newData].
         */
        override fun setData(newData: D): D {
            data = newData
            return data
        }

        /**
         * Returns adjacency list (all linked edges) from [this] Vertex
         */
        override fun getAdjacencies() = edges
    }

    /**
     * EdgeStructure
     * @param id Vertex source id.
     * @param adjacent Vertex destination id.
     */
    inner class EdgeStructure<I>(override val id: I, override val adjacent: I) : Graph.Edge<I>

    /**
     * Adds a new vertex (node) to the graph.
     * @param id Vertex's id.
     * @param d Vertex's data.
     * @return null if vertex already exists (not adding the vertex)
     *  OR vertex's data on success.
     */
    override fun addVertex(id: I, d: D): D? {
        if (getVertex(id) != null) {
            return null
        }

        vertices.add(VertexStructure(id, d))
        size++
        return d
    }

    /**
     * Adds a new edge (link) to the graph.
     * @param id Vertex source id.
     * @param idAdj Vertex destination id.
     * @return null if source does not exist (not adding any edge)
     *  OR vertex destination id on success.
     */
    override fun addEdge(id: I, idAdj: I): I? {
        val v = getVertex(id) ?: return null
        v.getAdjacencies().add(EdgeStructure(id, idAdj))
        return idAdj
    }

    /**
     * Gets a vertex by its [id] and returns it if it exists, otherwise null.
     */
    override fun getVertex(id: I) = vertices.find { it.id == id }

    /**
     * Gets an edge by its source and destination id and returns it if it exists, otherwise null.
     */
    override fun getEdge(id: I, idAdj: I): Graph.Edge<I>? {
        val v = getVertex(id) ?: return null
        return v.getAdjacencies().find { it?.adjacent == idAdj }
    }

    /**
     * Returns all edges from all vertices in a random order.
     */
    fun getAllEdges() = vertices.flatMap { v -> v.getAdjacencies() }.toMutableSet()

    /**
     * Graph iterator for iterating vertices.
     */
    private inner class VertexIterator : Iterator<Graph.Vertex<I, D>> {
        private val it = vertices.iterator()
        override fun hasNext() = it.hasNext()
        override fun next() = it.next()
    }

    /**
     * Graph iterator for iterating edges.
     */
    private inner class EdgeIterator : Iterator<Graph.Edge<I>> {
        private var currEdges = mutableSetOf<Graph.Edge<I>?>()
        private var idx = 0
        override fun hasNext(): Boolean {
            currEdges = getAllEdges()
            return idx < currEdges.size
        }
        override fun next() = currEdges.elementAt(idx++)!!
    }

    /**
     * Returns a graph iterator for iterating vertices.
     */
    override operator fun iterator(): Iterator<Graph.Vertex<I, D>> = VertexIterator()

    /**
     * Returns a graph iterator for iterating edges.
     */
    override fun edgesIterator(): Iterator<Graph.Edge<I>> = EdgeIterator()
}

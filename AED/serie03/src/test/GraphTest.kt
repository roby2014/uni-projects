package series.serie3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import series.serie3.graphCollections.Graph

class GraphTest {

    @Test
    fun graph_empty() {
        val graph = GraphStructure<Int, String>()
        assertEquals(0, graph.size)
    }

    @Test
    fun graph_singleton() {
        val graph = GraphStructure<Int, String>()
        assertEquals("V1", graph.addVertex(1, "V1"))
        assertNull(graph.addVertex(1, "V1"))
        assertEquals(1, graph.size)
        assertEquals("V1", graph.getVertex(1)!!.data)
        assertEquals(2, graph.addEdge(1, 2))
        assertEquals(3, graph.addEdge(1, 3))
        assertTrue(graph.getEdge(1, 2) != null)
        assertNotNull(graph.getEdge(1, 3))
        assertNull(graph.addEdge(0, 4))
        assertNull(graph.getEdge(0, 4))
        assertNull(graph.getEdge(1, 9))
    }

    @Test
    fun graph_equalElements() {
        val graph = GraphStructure<Int, String>()
        val setE: MutableSet<Graph.Edge<Int>?> = mutableSetOf()
        for (id in 1..4)
            assertEquals("V$id", graph.addVertex(id, "V$id"))
        assertEquals(4, graph.size)
        for (id in 1..4) for (id2 in 1..4)
            if (id2 != id) assertEquals(id2, graph.addEdge(id, id2))
        for (id in 1..4) {
            val set2 = mutableSetOf(1, 2, 3, 4)
            set2.remove(id)
            for (adj in set2)
                assertNotNull(graph.getEdge(id, adj))
        }
    }

    @Test
    fun graph_someElements() {
        val graph = GraphStructure<Int, String>()
        for (id in 0..99)
            assertEquals("V$id", graph.addVertex(id, "V$id"))
        assertEquals(100, graph.size)
        for (id in 0..99) for (id2 in 0..99)
            if (id2 != id) assertEquals(id2, graph.addEdge(id, id2))
        for (id in 0..99) for (id2 in 0..99)
            if (id2 != id) assertEquals(id2, graph.addEdge(id, id2))
        val setV: MutableSet<String> = mutableSetOf()
        for (id in 0..99) {
            val vertex = graph.getVertex(id)
            if (vertex != null) setV.add(vertex.data)
        }
        val set: MutableSet<String> = mutableSetOf()
        for (id in 0..99)
            set.add("V$id")
        assertEquals(setV, set)
    }

    @Test
    fun graph_testIterators() {
        val graph = GraphStructure<Int, String>()
        val setV: MutableSet<String> = mutableSetOf()
        val setE1: MutableSet<Graph.Edge<Int>?> = mutableSetOf()
        val setE2: MutableSet<Graph.Edge<Int>?> = mutableSetOf()
        for (id in 1..4)
            assertEquals("V$id", graph.addVertex(id, "V$id"))
        for (v in graph)
            setV.add(v.data)
        assertEquals(setV, mutableSetOf("V1", "V2", "V3", "V4"))
        for (id in 1..4) for (id2 in 1..4)
            if (id2 != id) assertEquals(id2, graph.addEdge(id, id2))
        for (e in graph.edgesIterator())
            setE1.add(e)
        for (v in graph) {
            val edges = v.getAdjacencies()
            for (e in edges) setE2.add(e)
        }
        assertEquals(setE1, setE2)
    }
}
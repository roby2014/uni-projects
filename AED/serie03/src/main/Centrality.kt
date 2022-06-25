package series.serie3

import series.serie3.graphCollections.Graph
import java.io.File

/**
 * Returns degree from [node] aKa the vertex.
 * Since [getAdjacencies] only returns the edges that start on the node, we also need
 *  to sum the edges that end up there.
 */
fun <I, D> GraphStructure<I, D>.getDegree(node: Graph.Vertex<I, D>) =
    node.getAdjacencies().size + this.getAllEdges().count { it?.adjacent == node.id }

/**
 * Global variable to use along the commands, which stores our graph.
 */
val graph = GraphStructure<String, String>()

fun main(args: Array<String>) {
    if (args.isEmpty())
        throw Exception("File argument null")

    // read file lines and add the nodes to our graph structure
    File(args[0])
        .readLines().map { it.replace(" ", "") } // remove whitespaces
        .forEach {
            val values = it.split("->")
            val (from, to) = values[0] to values[1]
            graph.addVertex(from, from)
            graph.addVertex(to, to) // also add "to/end" node in case it does not already exist
            graph.addEdge(from, to)
        }

    do {
        println(); print("command: ")
        val fullCommand = readln().split(" ")
        when (fullCommand[0]) {
            "degree" -> cmdDegree(fullCommand.elementAtOrNull(1))
            "smallestDistance" -> cmdSmallestDistance(
                fullCommand.elementAtOrNull(1),
                fullCommand.elementAtOrNull(2)
            )
            "closeness" -> cmdCloseness(fullCommand.elementAtOrNull(1))
            "degrees" -> cmdDegrees()
            "exit" -> break
            else -> println("Command \"${fullCommand[0]}\" not found!")
        }
    } while (true)
}

/**
 * Returns the degree of the vertex identified by [vertexId].
 */
fun cmdDegree(vertexId: String?) {
    if (vertexId == null) {
        println("Usage: degree vertexId")
        return
    }

    val v = graph.getVertex(vertexId)
    if (v == null) {
        println("Vertex with vertexId $vertexId not found")
        return
    }

    println("degree($vertexId) = ${graph.getDegree(v)}")
}

/**
 * Returns the value of the smallest distance between
 *  the pair of identical vertices identified by [vertexId1] and [vertexId2].
 */
fun cmdSmallestDistance(vertexId1: String?, vertexId2: String?) {
    if (vertexId1 == null || vertexId2 == null) {
        println("Usage: smallestDistance vertexId1 vertexId2")
        return
    }

    TODO()
}

/**
 * Returns the value of the closeness metric for the vertex identified by [vertexId].
 * If the [vertexId] argument is null, returns the value of the closeness metric for all vertices.
 */
fun cmdCloseness(vertexId: String?) {
    if (vertexId == null) {
        println("Usage: closeness vertexId")
        return
    }


    val v = graph.getVertex(vertexId)
    if (v == null) {
        println("Vertex with vertexId $vertexId not found")
        return
    }

    TODO()
}

/**
 * Returns the degree of all vertices.
 */
fun cmdDegrees() {
    for (v in graph)
        println("degree(${v.id}) = ${graph.getDegree(v)}")
}
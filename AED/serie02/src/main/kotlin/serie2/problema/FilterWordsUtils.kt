package serie2.problema

import java.io.File

// Some utilities for FilterWords problem (v1 & v2)

/**
 * Filters [this] string, returning true if the word does not contain any "non letter" character
 * Use this to only filter valid words from the file
 */
fun String.filterInvalidWords() =
    this.length > 1 && !this.contains(".") && !this.contains(",") &&
            !this.contains("?") && !this.contains("!") && !this.contains(":")

/**
 * Returns list of strings from file [filename]
 */
fun readFileWords(filename: String) =
    File(filename)
        .readText(Charsets.UTF_8)
        .trim()
        .split(" ")
        .filter { it.filterInvalidWords() }
        .map { it.replace('\n', ' ').replace('\r', ' ') }

/**
 * MutableSet interface (for v2 problem)
 */
interface MutableSet<E> {
    interface MutableEntry<E> {
        val element: E
    }

    val size: Int
    fun contains(element: E): Boolean
    fun add(element: E): Boolean
    operator fun iterator(): Iterator<MutableEntry<E>>
}

/**
 * MutableHashSet implementation
 * Used to store words from file for v2 problem
 */
class MutableHashSet<E> : MutableSet<E>, Iterable<MutableSet.MutableEntry<E>> {
    private class Node<E>(e: E) : MutableSet.MutableEntry<E> {
        override var element: E = e
        var next: Node<E>? = null
        var previous: Node<E>? = null
    }

    private var table: Array<Node<E>?>
    private var dimTable = 0 // M dimensão da tabela
    override var size = 0    // N chaves

    constructor() {
        table = arrayOfNulls<Node<Any>?>(11) as Array<Node<E>?>
        dimTable = table.size
    }

    private fun hash(e: E): Int {
        var idx = e.hashCode() % dimTable
        if (idx < 0) idx += dimTable
        return idx
    }

    override fun contains(e: E): Boolean {
        val idx = hash(e)
        var curr = table[idx]
        while (curr != null) {
            if (e != null && e == curr.element)
                return true
            curr = curr.next
        }
        return false
    }

    override fun add(e: E): Boolean {
        if (contains(e))
            return false
        val idx = hash(e)
        if (size.toDouble() / dimTable >= 0.75)
            resize()
        val node = Node(e)
        node.next = table[idx]
        table[idx]?.let { t -> t.previous = node }
        table[idx] = node
        size++
        return true
    }

    private fun resize() {
        //Aumentar a dimensão da tabela e recalcular a posição dos elementos na nova tabela.
        dimTable *= 2
        val newTable = arrayOfNulls<Node<Any>?>(dimTable) as Array<Node<E>?>
        for (i in table.indices) {
            var curr = table[i]
            while (curr != null) {
                table[i] = table[i]?.next
                val newIdx = hash(curr.element)
                curr.next = newTable[newIdx]
                newTable[newIdx]?.let { nt -> nt.previous = curr }
                curr.previous = null
                newTable[newIdx] = curr
                curr = table[i]
            }
        }
        table = newTable
    }

    private inner class MyIterator : Iterator<MutableSet.MutableEntry<E>> {
        var currIdx = -1
        var currNode: Node<E>? = null
        var list: Node<E>? = null

        override fun hasNext(): Boolean {
            if (currNode != null) return true
            while (currIdx < table.size) {
                if (list == null) {
                    currIdx++
                    if (currIdx < table.size) list = table[currIdx]
                } else {
                    currNode = list
                    list?.let { l -> list = l.next }
                    return true
                }
            }
            return false
        }

        override fun next(): MutableSet.MutableEntry<E> {
            if (!hasNext()) throw NoSuchElementException()
            val aux = currNode
            currNode = null
            return aux!!
        }
    }

    override fun iterator(): Iterator<MutableSet.MutableEntry<E>> = MyIterator()
}
package serie2.mapCollections

class HashMap<K, V> : MutableMap<K, V> {
    private data class Node<K, V>(
        override val key: K,
        override var value: V,
        var next: Node<K, V>?,
        var previous: Node<K, V>?
    ) :
        MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            value = newValue
            return value
        }
    }

    private var table: Array<Node<K, V>?>
    override var size = 0
    private var dimTable = 0

    constructor(dim: Int) {
        table = arrayOfNulls<Node<K, V>?>(dim)
        dimTable = dim
    }

    constructor() {
        table = arrayOfNulls<Node<K, V>?>(10)
        dimTable = 10
    }

    override fun get(key: K): V? {
        val pos = index(key)
        val node = search(key, pos)
        return node?.value
    }

    override fun put(key: K, value: V): V? {
        val pos = index(key)
        val node = search(key, pos)
        if (node == null) {
            // nao existe, criamos
            if ((size.toDouble() / dimTable.toDouble()) > 0.75)
                resize()

            val new = Node(key, value, null, null)
            new.next = table[pos]
            if (table[pos] != null)
                table[pos]?.let { it.previous = new }
            table[pos] = new
            size++
        } else {
            // ja existe, damos overwrite
            table[pos] = Node(key, value, node.next, node.previous)
        }

        return node?.value
    }

    override fun remove(key: K): V? {
        val idx = index(key)
        val e = search(key, idx)
        var curr = table[idx]
        while (curr != null) {
            if (e != null && e.value == curr.value) {
                val node = curr
                if (node.previous != null)
                    node.previous?.let { np -> np.next = node.next }
                else
                    table[idx] = node.next
                node.next?.let { nn -> nn.previous = node.previous }
                size--
                return node.previous?.value
            } else curr = curr.next
        }
        return null
    }

    private fun index(e: K): Int {
        val pos = e.hashCode() % dimTable
        return if (pos < 0) pos + dimTable else pos
    }

    private fun search(key: K, idx: Int): Node<K, V>? {
        var current: Node<K, V>? = table[idx]
        while (current != null) {
            if (current.key == key) return current
            current = current.next
        }
        return current
    }

    private fun resize() {
        dimTable *= 2
        val newTable = arrayOfNulls<Node<K, V>>(dimTable)
        for (i in table.indices) {
            var current = table[i]
            while (current != null) {
                table.let { it[i] = table.let { it[i]?.let { it.next } } }//passa o elemnto para o proximo da direita
                val newPos = index(current.key)
                current.next = newTable[newPos]
                if (newTable[newPos] != null) {
                    newTable[newPos]?.let { it.previous = current }//diz que o previous foca na poosição antiga
                }
                newTable[newPos] = current
                current = table.let { it[i] }
            }
        }
        table = newTable
    }

    private inner class MyIterator : Iterator<MutableMap.MutableEntry<K, V>> {
        var currIdx = -1
        var currNode: Node<K, V>? = null
        var list: Node<K, V>? = null

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

        override fun next(): MutableMap.MutableEntry<K, V> {
            if (!hasNext()) throw NoSuchElementException()
            val aux = currNode
            currNode = null
            return aux!!
        }
    }

    override fun iterator(): Iterator<MutableMap.MutableEntry<K, V>> = MyIterator()
}
package serie2

class IntArrayList {
    var l: ArrayList<Int>

    // inicializa o tipo de dados (lista de k elementos)
    constructor(k: Int) {
        l = ArrayList(k)
    }

    // adiciona o inteiro x à lista
    fun append(x: Int): Boolean {
        val lastSize = l.size
        l += x
        return lastSize + 1 == l.size
    }

    // permite obter o i-ésimom elemento desta lista
    fun get(i: Int): Int? = when {
        (i > l.size - 1) -> null
        (l[i] in l) -> l[i]
        else -> null
    }

    // adiciona a todos os inteiros presentes nesta lista o inteiro x
    fun addToAll(x: Int) {
        l = l.map { it + x } as ArrayList<Int>
    }
}


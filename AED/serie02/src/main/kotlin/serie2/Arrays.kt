package serie2

fun median(v: IntArray, l: Int, r: Int): Int {
    return if (v.size % 2 == 0) {
        (randomizedSelect(v, 0, v.size - 1, (v.size / 2)) + v[v.size / 2]) / 2
    } else {
        randomizedSelect(v, 0, v.size - 1, (v.size + 1) / 2)
    }
}


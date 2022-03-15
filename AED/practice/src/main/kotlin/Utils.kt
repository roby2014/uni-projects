// Some utilities.

/** Swaps 2 values in an IntArray by their indices ([i],[j]). */
fun IntArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}
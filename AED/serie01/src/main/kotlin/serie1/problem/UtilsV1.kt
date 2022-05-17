package serie1.problem

import java.io.BufferedReader

fun Array<BufferedReader>.myForEachIndexed(action: (index: Int, BufferedReader) -> Unit): Unit {
    var index = 0
    for (item in this) action(index++, item)
}

/**
 * Returns the index of/and the minimum string from [arr].
 */
fun getMinStr(arr: Array<String>): Pair<Int, String> {
    var (idx, min) = Pair(0, "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz")
    for (i in arr.indices) {
        if (arr[i] != "" && arr[i] < min) {
            min = arr[i]
            idx = i
        }
    }
    return idx to min
}

/**
 * Returns true if [this] array already contains [s].
 */
fun Array<String>.alreadyContains(s: String): Boolean {
    var count = 0
    for (str in this) {
        if (str == s) {
            count++
        }
    }
    return count > 0
}

/**
 * Sets all elements from [this] array to [v].
 */
fun BooleanArray.setAll(v: Boolean) {
    for (i in 0 until this.size) {
        this[i] = v
    }
}

/**
 * Returns true if all boolean elements from [this] array are [v].
 */
fun BooleanArray.isAll(v: Boolean): Boolean {
    var count = 0
    for (b in this) {
        if (b == v) {
            count++
        }
    }
    return count == this.size-1
}
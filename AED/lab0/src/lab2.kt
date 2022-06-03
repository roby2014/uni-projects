// ISEL
// LAB 2 AED
// Pedro Malafaia & Roberto Petrisoru - LEIC 21D

val RANGE = (-Int.MAX_VALUE..Int.MAX_VALUE)
val ARRAY_SIZES = listOf(10000, 40000, 90000, 120000)
const val TIMES = 11 // times to run each function

fun main() {
    println("Benchmark of the maximum sub array algorithm (quadratic solution)")

    val allowDuplicates = true
    println("Allowing duplicate values on the array? " + if (allowDuplicates) "YES" else "NO")

    // benchmark the different sizes
    for (size in ARRAY_SIZES) {
        val arr = createFilledIntArray(size, allowDuplicates)
        val avg = benchmarkFn(TIMES) {
            arr.maximalSubArrayQ(0, arr.size-1)
        }
        println("Array size: $size | Times run: $TIMES | Average time: $avg ms")
    }
}

/**
 * Fills an IntArray either with duplicate values or not.
 */
fun IntArray.fillArray(allowDuplicates: Boolean) {
    for (i in 0 until this.size) {
        var n = RANGE.random()
        if (!allowDuplicates) {
            while (this.contains(n)) {
                n = RANGE.random()
            }
        }
        this[i] = n
    }
}

/**
 * Creates an IntArray of size [size] and filled with random values.
 * @param size Array size
 * @param allowDuplicates Allows duplicate values on the array
 * @return New IntArray created
 */
fun createFilledIntArray(size: Int, allowDuplicates: Boolean): IntArray {
    val arr = IntArray(size)
    arr.fillArray(allowDuplicates)
    return arr
}

/**
 * Quadratic solution for the maximum sub array problem.
 * The array is passed as [this] but an interval can be specified with [start] and [end].
 * @return Pair of the maximum sub array and its sum
 */
fun IntArray.maximalSubArrayQ(start: Int, end: Int): Pair<IntArray, Int> {
    var (sum, left, right) = Triple(0, 0, 0)
    for (i in start..end) {
        var currentSum = 0
        for (j in i..end) {
            currentSum += this[j]
            if (currentSum > sum) {
                sum = currentSum
                left = i
                right = j
            }
        }
    }
    return Pair(this.copyOfRange(left, right + 1), sum)
}
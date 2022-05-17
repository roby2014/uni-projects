import kotlin.test.assertEquals
import kotlin.system.measureTimeMillis

/**
 * Returns the average time took to run the function (specified by [block]) x [times].
 */
fun benchmarkFn(times: Int, block: () -> Unit): Int {
    var time = 0
    repeat(times) {
        val ms = measureTimeMillis {
            block()
        }
        time += ms.toInt()
    }
    return time / times
}

/**
 * Debugs a function benchmark in a pretty way I would say :)
 */
fun debugBenchmark(fnName: String, size: Int, times: Int, avg: Int) {
    println("@ %-24s @\tArray size: %-8d @\tTimes run: %-3d @\tAverage time: %d ms\t".format(fnName, size, times, avg))
}

/**
 * Loops many arrays with different [sizes] and with different [range]s.
 * Arrays are filled with random values and can be used along with their size on [block] as a lambda param.
 * i.e : loopRandomArrays(listOf(100, 200, 300), 0..100) { size, arr -> ... }
 */
fun loopRandomIntArrays(sizes: List<Int>, range: IntRange, block: (Int, IntArray) -> Unit) {
    for (size in sizes) {
        val arr = createFilledIntArray(size, true, range)
        block(size, arr)
    }
}

/** Asserts that the [expected] IntArray is equal to the [actual] IntArray, with an optional [message]. */
fun assertArrayEquals(expected: IntArray, actual: IntArray, message: String? = "") {
    assertEquals(expected.toList(), actual.toList(), message)
}

/**
 * Fills an IntArray either with duplicate values or not contained in [range].
 */
fun IntArray.fillArray(allowDuplicates: Boolean, range: IntRange) {
    for (i in 0 until this.size) {
        var n = range.random()
        if (!allowDuplicates) {
            while (this.contains(n)) {
                n = range.random()
            }
        }
        this[i] = n
    }
}

/**
 * Creates an IntArray of size [size] and filled with random values.
 * @param size Array size
 * @param allowDuplicates Allows duplicate values on the array
 * @param range Values range
 * @return New IntArray created
 */
fun createFilledIntArray(size: Int, allowDuplicates: Boolean, range: IntRange): IntArray {
    val arr = IntArray(size)
    arr.fillArray(allowDuplicates, range)
    return arr
}
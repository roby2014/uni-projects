package serie2

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
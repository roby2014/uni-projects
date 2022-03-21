import other.maximalSubArrayQ
import other.maximalSubArraySW
import kotlin.test.Test
import kotlin.test.assertEquals

private val ARRAY_SIZES = listOf(5000, 10000, 40000, 90000 /*, 120000, ... */) // array sizes
private const val TIMES = 6 // times to run each function to get avg time
private val MAX_RANGE = -10000..10000 // array values range

class OtherTest {
    @Test
    fun testSubArrayQuadratic() {
        val arr = intArrayOf(1, -2, 10, 3, -5, 2, 0)
        val sum = arr.maximalSubArrayQ(0, arr.size - 1)
        assertEquals(13, sum)
    }

    @Test
    fun benchmarkSubArrayQuadratic() {
        loopRandomIntArrays(ARRAY_SIZES, MAX_RANGE) { size, arr ->
            val avg = benchmarkFn(TIMES) {
                arr.maximalSubArrayQ(0, arr.size - 1)
            }
            debugBenchmark("SubArray Quadratic", size, TIMES, avg)
        }
    }

    @Test
    fun testSubArrayLinear() {
        val arr = intArrayOf(1, -2, 10, 3, -5, 2, 0)
        val sum = arr.maximalSubArraySW(0, arr.size - 1, 2)
        assertEquals(13, sum)
    }

    @Test
    fun benchmarkSubArrayLinear() {
        loopRandomIntArrays(ARRAY_SIZES, MAX_RANGE) { size, arr ->
            // this is so fast that we need to manually increase size to make it a bit slower
            val realSize = size * 15
            val avg = benchmarkFn(TIMES) {
                arr.maximalSubArraySW(0, arr.size - 1, 3)
            }
            debugBenchmark("SubArray Linear", realSize, TIMES, avg)
        }
    }
}
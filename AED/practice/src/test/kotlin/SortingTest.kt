import sorting.insertionSort
import sorting.selectionSort
import kotlin.test.Test

private val ARRAY_SIZES = listOf(5000, 10000, 40000, 90000 /*, 120000, ... */) // array sizes
private const val TIMES = 6 // times to run each function to get avg time
private val MAX_RANGE = Int.MIN_VALUE..Int.MAX_VALUE // array values range

class SortingTest {
    @Test
    fun testSelectionSort() {
        val arr = intArrayOf(4, 3, 2, 8, 1, 20, 5, 16, -1, 6)
        arr.selectionSort()
        assertArrayEquals(intArrayOf(-1, 1, 2, 3, 4, 5, 6, 8, 16, 20), arr)
    }

    @Test
    fun benchmarkSelectionSort() {
        loopRandomIntArrays(ARRAY_SIZES, MAX_RANGE) { size, arr ->
            val avg = benchmarkFn(TIMES) {
                arr.selectionSort()
            }
            debugBenchmark("Selection Sort", size, TIMES, avg)
        }
    }

    @Test
    fun testInsertionSort() {
        val arr = intArrayOf(4, 3, 2, 8, 1, 20, 5, 16, -1, 6)
        arr.insertionSort()
        assertArrayEquals(intArrayOf(-1, 1, 2, 3, 4, 5, 6, 8, 16, 20), arr)
    }

    @Test
    fun benchmarkInsertionSort() {
        loopRandomIntArrays(ARRAY_SIZES, MAX_RANGE) { size, arr ->
            val avg = benchmarkFn(TIMES) {
                arr.insertionSort()
            }
            debugBenchmark("Insertion Sort", size, TIMES, avg)
        }
    }
}

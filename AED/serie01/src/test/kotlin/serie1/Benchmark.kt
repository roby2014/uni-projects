package serie1

import org.junit.jupiter.api.Test
import benchmark.utils.*
import serie1.problem.wordFilterV1
import serie1.problem.wordFilterV2

class Benchmark {
    private val times = 25
    private val maxRange = Int.MIN_VALUE..Int.MAX_VALUE
    private val arraySizes = listOf(5000, 10000, 50000, 100000, 500000, 1000000)

    // 1
    @Test
    fun countEquals_Benchmark() {
        loopRandomPointArray(arraySizes, 0..10) { size, arr, arr1 ->
            val avg = benchmarkFn(times) {
                countEquals(arr, arr1) { a: Point, b: Point ->
                    if (a.x != b.x) a.x - b.x
                    else a.y - b.y
                }
            }
            debugBenchmark("countEquals", size, times, avg)
        }
    }

    // 2
    @Test
    fun countIncreasingSubArrays_Benchmark() {
        loopRandomIntArrays(arraySizes, maxRange) { size, arr ->
            val avg = benchmarkFn(times) {
                countIncreasingSubArrays(arr)
            }
            debugBenchmark("countIncreasingSubArrays", size, times, avg)
        }
    }

    // 3
    @Test
    fun upperBound_Benchmark() {
        loopRandomIntArrays(arraySizes, maxRange) { size, arr ->
            val avg = benchmarkFn(times) {
               val uB = upperBound(arr,0,arr.size-1,arr.last())
            }
            debugBenchmark("upperBounds", size, times, avg)
        }
    }

    // 4
    @Test
    fun mostLonely_Benchmark() {
        loopRandomIntArrays(arraySizes, maxRange) { size, arr ->
            val avg = benchmarkFn(times) {
                mostLonely(arr)
            }
            debugBenchmark("mostLonely", size, times, avg)
        }
    }

    // Problem 3
    private val okArgs = arrayOf("anabela", "joaquim", "output.txt", "f1.txt", "f2.txt", "f3.txt")
    private val fasterArgs = arrayOf("joana", "joaquim", "output.txt", "f1.txt", "f2.txt", "f3.txt")

    // problem v1
    @Test
    fun benchmark2_wordFilterV1() {
        var avg = benchmarkFn(times) { wordFilterV1(okArgs) }
        println("wordFiltering V1 (${okArgs[0]}..${okArgs[1]}) took ~ $avg ms")
        avg = benchmarkFn(times) { wordFilterV1(fasterArgs) }
        println("wordFiltering V1 (${fasterArgs[0]}..${fasterArgs[1]}) took ~ $avg ms")
    }

    // problem v2
    @Test
    fun benchmark1_wordFilterV2() {
        var avg = benchmarkFn(times) { wordFilterV2(okArgs) }
        println("wordFiltering V2 (${okArgs[0]}..${okArgs[1]}) took ~ $avg ms")
        avg = benchmarkFn(times) { wordFilterV2(fasterArgs) }
        println("wordFiltering V2 (${fasterArgs[0]}..${fasterArgs[1]}) took ~ $avg ms")
    }

}
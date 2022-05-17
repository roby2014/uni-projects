package benchmark.utils.serie1

import benchmark.utils.benchmarkFn
import serie1.problem.wordFilterV1
import serie1.problem.wordFilterV2
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class WordFilterTest {
    @Test
    fun noArgs() {
        assertFailsWith<Exception>(
            message = "ERROR: Please specify all arguments: <word1> <word2> <output-file> <source-files ...>",
            block = { wordFilterV1(arrayOf("")) }
        )
        assertFailsWith<Exception>(
            message = "ERROR: Please specify all arguments: <word1> <word2> <output-file> <source-files ...>",
            block = { wordFilterV2(arrayOf("")) }
        )
    }

    @Test
    fun invalidArgs() {
        val invalidArgs = arrayOf("joao", "ana", "output.txt", "f1.txt")
        assertFailsWith<Exception>(
            message = "ERROR: <word1> must be lexicographically inferior to <word2>",
            block = { wordFilterV1(invalidArgs) }
        )
        assertFailsWith<Exception>(
            message = "ERROR: <word1> must be lexicographically inferior to <word2>",
            block = { wordFilterV2(invalidArgs) }
        )
    }

    // Avaliação experimental

    private val times = 10 // function benchmark count (times that will run)
    val r1 = Pair("ana", "joaquim")
    val r2 = Pair("ana", "pedro")

    @Test
    fun avaliacaoExperimental_wordFilterV1() {
        // ana joaquim (faster)
        var args = arrayOf(r1.first, r1.second, "output.txt", "f1.txt")
        var avg = benchmarkFn(times) { wordFilterV1(args) }
        println("wordFiltering V1 (1 file) (${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r1.first, r1.second, "output.txt", "f1.txt", "f2.txt")
        avg = benchmarkFn(times) { wordFilterV1(args) }
        println("wordFiltering V1 (2 files)(${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r1.first, r1.second, "output.txt", "f1.txt", "f2.txt", "f3.txt")
        avg = benchmarkFn(times) { wordFilterV1(args) }
        println("wordFiltering V1 (3 files)(${args[0]}..${args[1]}) took ~ $avg ms")

        // ana..pedro
        args = arrayOf(r2.first, r2.second, "output.txt", "f1.txt")
        avg = benchmarkFn(times) { wordFilterV1(args) }
        println("wordFiltering V1 (1 file)(${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r2.first, r2.second, "output.txt", "f1.txt", "f2.txt")
        avg = benchmarkFn(times) { wordFilterV1(args) }
        println("wordFiltering V1 (2 files)(${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r2.first, r2.second, "output.txt", "f1.txt", "f2.txt", "f3.txt")
        avg = benchmarkFn(times) { wordFilterV1(args) }
        println("wordFiltering V1 (3 files)(${args[0]}..${args[1]}) took ~ $avg ms")
    }

    @Test
    fun avaliacaoExperimental_wordFilterV2() {
        // ana joaquim (faster)
        var args = arrayOf(r1.first, r1.second, "output.txt", "f1.txt")
        var avg = benchmarkFn(times) { wordFilterV2(args) }
        println("wordFiltering V2 (1 file) (${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r1.first, r1.second, "output.txt", "f1.txt", "f2.txt")
        avg = benchmarkFn(times) { wordFilterV2(args) }
        println("wordFiltering V2 (2 files)(${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r1.first, r1.second, "output.txt", "f1.txt", "f2.txt", "f3.txt")
        avg = benchmarkFn(times) { wordFilterV2(args) }
        println("wordFiltering V2 (3 files)(${args[0]}..${args[1]}) took ~ $avg ms")

        // ana..pedro
        args = arrayOf(r2.first, r2.second, "output.txt", "f1.txt")
        avg = benchmarkFn(times) { wordFilterV2(args) }
        println("wordFiltering V2 (1 file)(${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r2.first, r2.second, "output.txt", "f1.txt", "f2.txt")
        avg = benchmarkFn(times) { wordFilterV2(args) }
        println("wordFiltering V2 (2 files)(${args[0]}..${args[1]}) took ~ $avg ms")

        args = arrayOf(r2.first, r2.second, "output.txt", "f1.txt", "f2.txt", "f3.txt")
        avg = benchmarkFn(times) { wordFilterV2(args) }
        println("wordFiltering V2 (3 files)(${args[0]}..${args[1]}) took ~ $avg ms")
    }
}
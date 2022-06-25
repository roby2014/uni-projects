import org.junit.jupiter.api.Test
import series.serie3.kSmallest
import kotlin.test.assertEquals

class KSmallestTest {

    val cmp={ i1: Int, i2: Int -> i1 - i2 }

    @Test
    fun kSmallest_empty_trees() {
        val tree = emptyBST()
        assertEquals(0, kSmallest(tree, 0))
        assertEquals(0, kSmallest(tree, 1))
    }
    @Test
    fun kSmallest_singleNodeBST() {
        val tree = singleNodeBST(1)
        assertEquals(1, kSmallest(tree, 1))
        assertEquals(0, kSmallest(tree, 0))
        assertEquals(0, kSmallest(tree, 2))
    }

    @Test
    fun kSmallest_randomBST() {
        val tree = populatedBST(intArrayOf(6,4,1,8,5,9,7,2,3,10))
        for (i in 1..10)
            assertEquals(i, kSmallest(tree, i))
    }

    @Test
    fun kSmallest_leftBST() {
        val tree = populatedBST(intArrayOf(10,9,8,7,6,5,4,3,2,1))
        for (i in 1..10)
            assertEquals(i, kSmallest(tree, i))
    }

    @Test
    fun kSmallest_rightBST() {
        val tree = populatedBST(intArrayOf(1,2,3,4,5,6,7,8,9,10))
        for (i in 1..10)
            assertEquals(i, kSmallest(tree, i))
    }
}
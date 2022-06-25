import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import series.serie3.isBalanced

class IsBalancedTest {

    val  CMP_NATURAL_ORDER = { i1: Int, i2: Int -> i1.compareTo(i2) }

    @Test
    fun isBalanced_empty_trees() {
        val tree = emptyBST()
        assertEquals(-1,height(tree))
        assertTrue(isBalanced(tree))
    }

    @Test
    fun isBalanced_singleNodeBST() {
        var tree = singleNodeBST(10)
        assertTrue(isBalanced(tree))
        tree = add(tree, 2, CMP_NATURAL_ORDER)
        assertEquals(1, height(tree))
        assertTrue(isBalanced(tree))
        tree = add(tree, 1, CMP_NATURAL_ORDER)
        assertEquals(2, height(tree))
        assertFalse(isBalanced(tree))
        tree = add(tree, 20, CMP_NATURAL_ORDER)
        assertEquals(2, height(tree))
        assertTrue(isBalanced(tree))
    }
    @Test
    fun isBalanced_completeBST() {
        val tree = populatedBST(intArrayOf(10,5,12,4,6,16,11))
        assertEquals(2, height(tree))
        assertTrue(isBalanced(tree))
    }

    @Test
    fun isBalanced_noncompleteBSTs() {
        var tree = populatedBST(intArrayOf(30,10,5,12,4,3,0,11,40,50))
        assertEquals(5, height(tree))
        assertFalse(isBalanced(tree))
        tree = populatedBST(intArrayOf(30,10,5,12,4,11,40,50))
        assertEquals(3, height(tree))
        assertTrue(isBalanced(tree))
    }

}
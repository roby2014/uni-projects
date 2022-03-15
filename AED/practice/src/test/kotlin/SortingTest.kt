import kotlin.test.Test

class SortingTest {
    @Test
    fun testSelectionSort() {
        var arr = intArrayOf(4, 3, 2, 8, 1)
        arr.selectionSort()
        assertArrayEquals(intArrayOf(1, 2, 3, 4, 8), arr)

        arr = intArrayOf(20, 5, 16, -1, 6)
        arr.selectionSort()
        assertArrayEquals(intArrayOf(-1, 5, 6, 16, 20), arr)
    }

    @Test
    fun testInsertionSort() {
        var arr = intArrayOf(4, 3, 2, 8, 1)
        arr.insertionSort()
        assertArrayEquals(intArrayOf(1, 2, 3, 4, 8), arr)

        arr = intArrayOf(20, 5, 16, -1, 6)
        arr.insertionSort()
        assertArrayEquals(intArrayOf(-1, 5, 6, 16, 20), arr)
    }
}
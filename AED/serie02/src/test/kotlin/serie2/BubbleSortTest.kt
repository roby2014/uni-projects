package serie2

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class BubbleSortTest {
    @Test
    fun bubblesort_empty_list() {
        val list: Node<Int> = emptyListWithSentinel()
        bubbleSort(list, Comparator.naturalOrder<Int>())
        assertTrue(isSorted(list, Comparator.naturalOrder<Int>()))
    }

    @Test
    fun bubblesort_with_oneElement_list() {
        val list: Node<Int> = getRandomListSentinel(1)
        bubbleSort(list, Comparator.naturalOrder<Int>())
        assertTrue(isSorted(list, Comparator.naturalOrder<Int>()))
    }

    @Test
    fun bubblesort_with_SomeElements1_list() {
        val array = ArrayList<Int>()
        array.add(4)
        array.add(3)
        array.add(2)
        array.add(1)
        val list: Node<Int> = getListWithSentinel(array)
        bubbleSort(list, Comparator.naturalOrder<Int>())
        assertTrue(isSorted(list, Comparator.naturalOrder<Int>()))
    }

    @Test
    fun bubblesort_with_increasingElements() {
        val list: Node<Int> = getListWithSentinel(0, 20, 1)
        bubbleSort(list, Comparator.reverseOrder<Int>())
        assertTrue(isSorted(list, Comparator.reverseOrder<Int>()))
    }

    @Test
    fun bubblesort_with_SomeElements2_list() {
        val array = ArrayList<Int>()
        array.add(22)
        array.add(-30)
        array.add(-30)
        array.add(32)
        array.add(-38)
        array.add(-34)
        array.add(-36)
        val list: Node<Int> = getListWithSentinel(array)
        bubbleSort(list, Comparator.naturalOrder<Int>())
        assertTrue(isSorted(list, Comparator.naturalOrder<Int>()))
    }

    @Test
    fun bubblesort_with_someElements3_list() {
        val array = ArrayList<Int>()
        array.add(27)
        array.add(-12)
        array.add(33)
        array.add(27)
        array.add(26)
        array.add(-33)
        array.add(14)
        array.add(26)
        array.add(8)
        array.add(9)
        array.add(-19)
        val list: Node<Int> = getListWithSentinel(array)
        bubbleSort(list, Comparator.naturalOrder<Int>())
        assertTrue(isSorted(list, Comparator.naturalOrder<Int>()))
    }

    @Test
    fun bubblesort_with_randomElements_list() {
        val list: Node<Int> = getRandomListSentinel(11)
        bubbleSort(list, Comparator.naturalOrder<Int>())
        assertTrue(isSorted(list, Comparator.naturalOrder<Int>()))
    }
}




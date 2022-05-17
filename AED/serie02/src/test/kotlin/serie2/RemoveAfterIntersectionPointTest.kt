package serie2

import org.junit.jupiter.api.Test


class RemoveAfterIntersectionPointTest {

    private val emptyList: Node<String> = emptyListWithSentinel()
    private val singletonListWithA: Node<String> = makeList("A")
    private val singletonListWithB: Node<String> = makeList("b")
    private val singletonListWithC: Node<String> = makeList("c")
    
    @Test
    fun remove_after_intersectionPoint_empty_lists() {
        removeAfterIntersectionPoint( emptyList,  emptyList, naturalOrder())
        assertListEqualsWithSentinel( emptyList, emptyListWithSentinel(), naturalOrder())
        removeAfterIntersectionPoint( emptyList, singletonListWithA, naturalOrder())
        assertListEqualsWithSentinel( emptyList, emptyListWithSentinel(), naturalOrder())
        removeAfterIntersectionPoint(singletonListWithA,  emptyList, naturalOrder())
        assertListEqualsWithSentinel(singletonListWithA, makeList("A"), naturalOrder())
    }


    @Test
    fun remove_after_intersectionPoint_one_element_lists() {
        val l1: Node<String> = makeList("A")
        removeAfterIntersectionPoint(l1, makeList("a")) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
        assertListEqualsWithSentinel(emptyList, l1) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
        val list: Node<String> = makeList("a")
        removeAfterIntersectionPoint(list, singletonListWithA, naturalOrder())
        assertListEqualsWithSentinel(singletonListWithA, list) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
    }

    @Test
    fun remove_after_intersectionPoint_one_element_match() {
        val l1: Node<String> = makeList("b", "a")
        val l2 = singletonListWithA
        removeAfterIntersectionPoint(l1, l2) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
        assertListEqualsWithSentinel(singletonListWithB, l1, naturalOrder())
    }

    @Test
    fun remove_after_intersectionPoint_two_element_match() {
        val l1: Node<String> = makeList("c", "b", "a")
        val l2: Node<String> = makeList("f", "b", "a")
        removeAfterIntersectionPoint(l1, l2) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
        assertListEqualsWithSentinel(singletonListWithC, l1) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
    }

    @Test
    fun remove_after_intersectionPoint_not_match() {
        val l = makeList("b", "a")
        val equalToL = makeList("b", "a")
        val reverseL= makeList("a", "b")
        val greaterL = makeList("b", "a", "c")
        removeAfterIntersectionPoint(l, reverseL, naturalOrder())
        assertListEqualsWithSentinel(equalToL, l, naturalOrder())
        removeAfterIntersectionPoint(l, greaterL, naturalOrder())
        assertListEqualsWithSentinel(equalToL, l, naturalOrder())
        removeAfterIntersectionPoint(l, singletonListWithA, naturalOrder())
        assertListEqualsWithSentinel(equalToL, l, naturalOrder())
    }

    @Test
    fun remove_after_intersectionPoint_all_match() {
        val fiveElements: Node<String> = makeList("a", "b", "c", "d", "e")
        removeAfterIntersectionPoint(
            fiveElements,
            makeList("a", "b", "c", "d", "e")
        ) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
        assertListEqualsWithSentinel(emptyList, fiveElements) { obj: String, str: String ->
            obj.compareTo(str, ignoreCase = true)
        }
    }

    @Test
    fun remove_after_intersectionPoint_some_match() {
        val n1: Node<Int> = makeList(3, 5, 2, 7, 4)
        val n2: Node<Int> = makeList(9, 3, 10, 8, 2, 7, 4)
        val expected: Node<Int> = makeList(3, 5)
        removeAfterIntersectionPoint(n1, n2, naturalOrder())
        assertListEqualsWithSentinel(expected, n1, naturalOrder())
    }
}

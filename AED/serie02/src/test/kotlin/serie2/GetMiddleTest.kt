package serie2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test



class GetMiddleTest {
    @Test
    fun middle_empty_list() {
        assertEquals(null, getMiddle<Int>(null))
    }


    @Test
    fun middle_singleton_list() {
        val list= getRandomListWithoutSentinel(1)
        assertEquals(list?.value, getMiddle(list)?.value)
    }

    @Test
    fun middle_list_with_odd_dimension() {
        val list= getListWithoutSentinel(1, 20, 1)
        assertEquals(10, getMiddle(list)?.value)
    }

    @Test
    fun middle_list_with_even_dimension() {
        val list= getListWithoutSentinel(0, 20, 1)
        assertEquals(10, getMiddle(list)?.value)
    }
}


















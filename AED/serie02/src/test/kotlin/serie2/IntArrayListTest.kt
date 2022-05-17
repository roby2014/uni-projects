package serie2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class IntArrayListTest {
    @Test
    fun intArrayList_empty() {
        val ar=IntArrayList(10)
        assertEquals(null,ar.get(0))
    }

    @Test
    fun intArrayList_singleton() {
        val ar=IntArrayList(10)
        ar.append(10)
        assertEquals(10,ar.get(0))
        ar.addToAll(3)
        assertEquals(13,ar.get(0))
    }

    @Test
    fun intArrayList_someElements() {
        val ar=IntArrayList(10)
        ar.append(10)
        assertEquals(10,ar.get(0))
        ar.addToAll(3)
        ar.append(5)
        assertEquals(5,ar.get(1))
        ar.append(10)
        assertEquals(10,ar.get(2))
        ar.addToAll(4)
        assertEquals(14,ar.get(2))
    }

}
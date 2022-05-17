package serie2

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class MedianTest {
    @Test
    fun median_onArrayWith1Element(){
      val array = intArrayOf(1)
      assertEquals(1, median(array, 0, 0))
    }

    @Test
    fun median_onArrayWith2Elements(){
       val array = intArrayOf(1, 3)
       assertEquals(2, median(array, 0, 1))
    }

    @Test
    fun median_onArrayWithImparNumberOfElements(){
       val array = intArrayOf(20, 2, 10, 9, 7, 6, 5, 4, 30, 50, 60, 1, 0)
       assertEquals(7, median(array, 0, array.size - 1))
        }

    @Test
    fun median_onArrayWithParNumberOfElements(){
        val array = intArrayOf(20, 2, 10, 9, 7, 6, 5, 4, 30, 50, 60, 1, 0, 70)
        assertEquals(8, median(array, 0, array.size - 1))
    }
}



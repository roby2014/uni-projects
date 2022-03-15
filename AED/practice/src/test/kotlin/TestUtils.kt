import kotlin.test.assertEquals

/** Asserts that the [expected] IntArray is equal to the [actual] IntArray, with an optional [message]. */
fun assertArrayEquals(expected: IntArray, actual: IntArray, msg: String? = "") {
    assertEquals(expected.toList(), actual.toList(), msg)
}
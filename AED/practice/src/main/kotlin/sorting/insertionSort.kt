package sorting

import swap

// Insertion sort is a sorting algorithm that places an unsorted
// element at its suitable place in each iteration.

/**
 * Insertion Sort Algorithm of an IntArray.
 *
 * Time Complexity: O(n^2)
 * @receiver Array to sort
 */
fun IntArray.insertionSort() {
    for (i in 1 until this.size) {
        var j = i
        while (j > 0 && this[j - 1] > this[j]) {
            this.swap(j - 1, j)
            j -= 1
        }
    }
}
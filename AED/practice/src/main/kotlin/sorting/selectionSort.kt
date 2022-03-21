package sorting

import swap

// The selection sort algorithm sorts an array by repeatedly finding
// the minimum element from unsorted part and putting it at the beginning.

/**
 * Insertion Sort Algorithm of an IntArray.
 *
 * Time Complexity: O(n^2)
 * @receiver Array to sort
 */
fun IntArray.selectionSort() {
    for (i in 0 until this.size) {
        var min = i
        for (j in i until this.size) {
            if (this[j] < this[min]) {
                min = j
            }
        }
        this.swap(i, min)
    }
}


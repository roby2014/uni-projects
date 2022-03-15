// Some sorting algorithms.

/**
 * The selection sort algorithm sorts an array by repeatedly finding
 * the minimum element (considering ascending order) from unsorted part
 * and putting it at the beginning.
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

/**
 * Insertion sort is a sorting algorithm that places an unsorted
 * element at its suitable place in each iteration.
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
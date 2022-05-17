package serie2

/**
 * Swaps [v] ([i]) with [v] ([j])
 */
fun swap(v: IntArray, i: Int, j: Int) {
    val temp = v[i]
    v[i] = v[j]
    v[j] = temp
}

/**
 * Sorts the array [arr] by using quick sorting randomized algorithm
 */
fun randomizedQuickSort(arr: IntArray, left: Int, right: Int) {
    if (left < right) {
        val q = randomizedPartition(arr, left, right)
        randomizedQuickSort(arr, left, q - 1)
        randomizedQuickSort(arr, q + 1, right)
    }
}

/**
 * Divides the [arr] into 3 parts:
 *  - Elements less than the pivot element
 *  - Pivot element (central element)
 *  - Elements greater than the pivot element
 *  @return Index of pivot
 */
fun partition(arr: IntArray, left: Int, right: Int): Int {
    val pivot = arr[right]
    var i = left - 1
    for (j in left until right) {
        if (arr[j] <= pivot) {
            i++
            swap(arr, i, j)
        }
    }
    swap(arr, ++i, right)
    return i
}

/**
 * Partitions the [arr] into two (possibly empty) sub arrays, by choosing a random pivot (i)
 * All elements before A(i) are < i, while all elements >= A(i) are >= i
 * @return Index of pivot
 */
fun randomizedPartition(arr: IntArray, left: Int, right: Int): Int {
    swap(arr, right, (left..right).random())
    return partition(arr, left, right)
}

/**
 * Returns the [i]th smallest element of the array [arr].
 * If i = n/2, it will return the median.
 */
fun randomizedSelect(arr: IntArray, left: Int, right: Int, i: Int): Int {
    if (left == right)
        return arr[left]

    val q = randomizedPartition(arr, left, right)
    val k = q - left + 1

    return if (i == k)
        arr[q]
    else if (i < k)
        randomizedSelect(arr, left, q - 1, i)
    else
        randomizedSelect(arr, q + 1, right, i - k)
}

/**
 * Returns min and max value of [a] using only 3*n/2 comparisons
 */
fun getMinMax(a: IntArray): Pair<Int, Int> {
    var (min, max) = Int.MAX_VALUE to Int.MIN_VALUE

    var i = 0
    while (i < a.size - 1) {
        val (x, y) = a[i] to a[i + 1]
        if (x > y) {
            if (x > max) max = x
            if (y < min) max = y
        } else {
            if (y > max) max = y
            if (x < min) min = x
        }
        i += 2
    }

    if (a.size % 2 == 1) {
        val last = a[a.size - 1]
        if (last > max) max = last
        if (last < min) min = last
    }

    return min to max
}

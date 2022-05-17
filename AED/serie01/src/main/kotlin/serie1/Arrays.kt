package serie1

import mergeSort
import java.lang.Integer.min
import kotlin.math.abs


data class Point(var x: Int, var y: Int)

/**
 * Returns last index of the array that is lower or equal to the element we are searching
 */
fun upperBound(a: IntArray, l: Int, r: Int, element: Int): Int {
    var lx = l
    var rx = r
    if ( lx > rx || a[lx] > element ) return -1
    while (lx <= rx){
        val mid = (lx + rx)/2
        if ( a[mid] <= element ) lx = mid + 1
        else rx = mid - 1
    }
    return rx
}

/**
 * Returns the total count of increasing sub arrays in an IntArray ([arr]).
 */
fun countIncreasingSubArrays(arr: IntArray): Int {
    var (count, len) = Pair(0, 1)


    for (i in 1 until arr.size) {
        if (arr[i] > arr[i - 1]) {
            count += len++
        } else {
            len = 1
        }
    }

    return count
}

/**
 * Returns the number of pooints that are simultaneously in both arrays
 */
fun countEquals(points1: Array<Point>, points2: Array<Point>, cmp: (p1: Point, p2: Point) -> Int): Int {
    if (points1.isEmpty() || points2.isEmpty()) return 0

    var i = 0
    var j = 0
    var count = 0

    while ( i!= points1.size  && j!= points2.size ){
        when{
            cmp(points1[i], points2[j]) < 0 -> i++
            cmp(points1[i], points2[j]) > 0 -> j++
            cmp(points1[i], points2[j]) == 0 -> {
                count += 1
                i++
                j++
            }
        }
    }

    return count
}

/**
 * Returns the most lonely number in an IntArray ([arr]).
 *
 * The most lonely number in an array is the number that has the greatest distance k,
 *  k being the smallest absolute value of the differences between him and each value of the array.
 */
fun mostLonely(a: IntArray): Int {
    mergeSort(a, a.size)

    if (a.size == 1)
        return a[0]

    // save one iteration
    var (lonely, maxK) = Pair(a[0], abs(a[0] - a[1]))

    // loop array and get current a[i]'s value of k
    for (i in 1 until a.size) {
        val k = if (i == a.size - 1) abs(a[i] - a[i - 1])
                else min(abs(a[i] - a[i - 1]), abs(a[i] - a[i + 1]))

        if (k >= maxK) {
            val draw = k == maxK
            maxK = if (draw) min(k, maxK) else k
            lonely = if (draw) min(lonely, a[i]) else a[i]
        }

    }

    return lonely
}

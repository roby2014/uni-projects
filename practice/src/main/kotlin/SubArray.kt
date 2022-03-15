// The maximum sum sub array problem is the task of finding a contiguous sub array
// with the largest sum, within a given one-dimensional array A[1...n] of numbers.

/**
 * Quadratic solution for the maximum sub array problem.
 * @return Pair of the maximum sub array and its sum.
 */
fun IntArray.maximalSubArrayQ(start: Int, end: Int): Pair<IntArray, Int> {
    var (sum, left, right) = Triple(0, 0, 0)
    for (i in start..end) {
        var currentSum = 0
        for (j in i..end) {
            currentSum += this[j]
            if (currentSum > sum) {
                sum = currentSum
                left = i
                right = j
            }
        }
    }
    return Pair(this.copyOfRange(left, right + 1), sum)
}

/**
 * Linear solution for the maximum sub array problem
 * (Uses the sliding window technique)
 * @return Pair of the maximum sub array and its sum.
 */
fun IntArray.maximalSubArraySW(start: Int, end: Int): Pair<IntArray, Int> {
    // TODO
    return Pair(this.copyOfRange(1, 1), 1)
}
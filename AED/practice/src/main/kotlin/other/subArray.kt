package other

// The maximum sum sub array problem is the task of finding a contiguous sub array
// with the largest sum, within a given one-dimensional array A[1...n] of numbers.

/**
 * Quadratic solution for the maximum sub array problem.
 * @return Sum of maximum sub array
 */
fun IntArray.maximalSubArrayQ(start: Int, end: Int): Int {
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

    return sum
}

/**
 * Linear solution for the maximum sub array of size K problem
 * (Uses the sliding window technique)
 * @return Sum of maximum sub array of size [k]
 */
fun IntArray.maximalSubArraySW(start: Int, end: Int, k: Int): Int {
    var currentSum = 0

    // get first window
    for (i in start until k) {
        currentSum += this[i]
    }
    var bestSum = currentSum

    // slide the window to the right
    for (i in k..end) {
        currentSum += this[i] - this[i - k];
        if (currentSum > bestSum) {
            bestSum = currentSum
        }
    }

    return bestSum
}
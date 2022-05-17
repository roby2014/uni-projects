fun merge(a: IntArray, l: IntArray, r: IntArray, left: Int, right: Int) {
    var i = 0
    var j = 0
    var k = 0
    while (i < left && j < right) {
        if (l[i] <= r[j]) {
            a[k++] = l[i++]
        } else {
            a[k++] = r[j++]
        }
    }
    while (i < left) {
        a[k++] = l[i++]
    }
    while (j < right) {
        a[k++] = r[j++]
    }
}

fun mergeSort(a: IntArray, n: Int) {
    if (n < 2) {
        return
    }
    val mid = n / 2
    val l = IntArray(mid)
    val r = IntArray(n - mid)
    for (i in 0 until mid) {
        l[i] = a[i]
    }
    for (i in mid until n) {
        r[i - mid] = a[i]
    }
    mergeSort(l, mid)
    mergeSort(r, n - mid)
    merge(a, l, r, mid, n - mid)
}

fun merge(a: MutableList<String>, l: MutableList<String>, r: MutableList<String>, left: Int, right: Int) {
    var i = 0
    var j = 0
    var k = 0
    while (i < left && j < right) {
        if (l[i] <= r[j]) {
            a[k++] = l[i++]
        } else {
            a[k++] = r[j++]
        }
    }
    while (i < left) {
        a[k++] = l[i++]
    }
    while (j < right) {
        a[k++] = r[j++]
    }
}

fun mergeSort(a: MutableList<String>, n: Int) {
    if (n < 2) {
        return
    }
    val mid = n / 2
    val l = MutableList<String>(mid) { "" }
    val r = MutableList<String>(n - mid) { "" }
    for (i in 0 until mid) {
        l[i] = a[i]
    }
    for (i in mid until n) {
        r[i - mid] = a[i]
    }
    mergeSort(l, mid)
    mergeSort(r, n - mid)
    merge(a, l, r, mid, n - mid)
}

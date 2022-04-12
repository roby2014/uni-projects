package serie1.problem

fun getMinStr(list: MutableList<String>): Pair<Int, String> {
    var (idx, min) = Pair(0, "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz")
    for (i in list.indices) {
        if (list[i] != "" && list[i] < min) {
            min = list[i]
            idx = i
        }
    }
    return idx to min
}
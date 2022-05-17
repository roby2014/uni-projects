package serie2.mapCollections

fun <K, V, R> HashMap<K, V>.map(transform:(MutableMap.MutableEntry <K,V>)->R): List<R> {
    val arr = ArrayList<R>()
    for (item in this)
        arr.add(transform(item))
    return arr
}

fun <K, V, R> HashMap<K, V>.filter(predicate:(MutableMap.MutableEntry<K,V>)->Boolean): List<MutableMap.MutableEntry<K,V>> {
    val arr = ArrayList<MutableMap.MutableEntry<K,V>>()
    for (item in this) if (predicate(item)) arr.add(item)
    return arr
}

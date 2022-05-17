package serie2.mapCollections

interface MutableMap<K,V> {
    interface MutableEntry<K, V>{
        val key: K
        val value:V
        fun setValue(newValue: V): V
    }
    val size: Int
    fun put(key: K, value: V): V?
    fun remove(key: K): V?
    operator fun get(key: K): V?
    operator fun iterator(): Iterator<MutableEntry<K, V>>
}

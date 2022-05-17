package serie2


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

/*
* Double Linked Lists Without Sentinel, Non_circular
* */
fun <E> emptyListWithoutSentinel(): Node<E>? {
    return null
}

fun getListWithoutSentinel(begin: Int, end: Int, step: Int): Node<Int>? {
    var begin = begin
    if (end < begin) return null
    val list = Node<Int>()
    var cur: Node<Int>? = list
    if(cur!=null) cur.value = begin
    begin += step
    while (begin < end) {
        val next = Node<Int>()
        if(cur!=null) cur.next = next
        next.previous = cur
        next.value = begin
        if(cur!=null) cur = cur.next
        begin += step
    }
    return list
}

fun getRandomListWithoutSentinel(dimension: Int): Node<Int>?{
    val r = Random()
    var list: Node<Int>? = emptyListWithoutSentinel ()
    for (i in 0 until dimension) {
        val novo = newNode(r.nextInt() % 40)
        novo.next = list
        if (list != null) list.previous = novo
        list = novo
    }
    return list
}

/*
  * For circular double linked lists with sentinel
  */
fun <E> emptyListWithSentinel(): Node<E> {
    val empty = Node<E>()
    empty.previous = empty
    empty.next = empty.previous
    return empty
}


fun getListWithSentinel(begin: Int, end: Int, step: Int): Node<Int> {
    val array = ArrayList<Int>()
    var i = begin
    while (i <= end) {
        array.add(i)
        i += step
    }
    return makeList(array)
}


fun <E> makeList(array: ArrayList<E>): Node<E> {
    val list: Node<E> = emptyListWithSentinel()
    for (v in array) {
        val new = newNode(v, list.previous, list)
        list.previous?.let { lp ->
            lp.next = new
        }
        list.previous = new
    }
    return list
}
fun <E> makeList(vararg array: E): Node<E> {
    val list: Node<E> = emptyListWithSentinel()
    for (v in array) {
        val new = newNode(v, list.previous, list)
        list.previous?.let { lp ->
            lp.next = new
        }
        list.previous = new
    }
    return list
}

fun <E> assertListEqualsWithSentinel(expected: Node<E>, result: Node<E>, cmp: Comparator<E>) {
    var listExpected = expected.next
    var listResult = result.next
    while (listExpected != expected && listResult != result) {
        listExpected?.let { le ->
            listResult?.let { lr -> assertEquals(0, cmp.compare(le.value, lr.value))
                listExpected = le.next
                listResult = lr.next
            }
        }
    }
    assertTrue(listExpected == expected)
    assertTrue(listResult == result)
}

fun <E> isSorted(list: Node<E>, cmp: Comparator<E>): Boolean {
    var curr = list.next
    if (curr == list || curr == list.previous) return true
    while ( curr?.let{ cr->cr.next} != list) {
        if ( cmp.compare(curr?.let{ cr-> cr.value}, curr?.let{it-> it.next?.let{it1->it1.value}}) > 0) return false
        curr = curr?.next
    }

    return true
}


fun getListWithSentinel(array: ArrayList<Int>): Node<Int>{
    var list= emptyListWithSentinel<Int>()
    if (array.size == 0) return list
    for (i in 0 until array.size) {
        val current = Node<Int>()
        current.value=array[i]
        current.next=list
        current.previous=list.previous
        list.previous?.next=current
        list.previous=current

    }
    return list
}


fun getRandomListSentinel(dimension: Int): Node<Int>{

    val r = Random()
    val list = emptyListWithSentinel<Int>()
    for (i in 0 until dimension) {
        val novo = newNode<Int>(r.nextInt() % 40)
        novo.next = list //novo.next = list.next
        novo.previous = list.previous //novo.previous = list
        list.previous?.let { lp ->
            lp.next = novo
        }
        list.previous = novo //list.next = novo
    }
    return list
}

/*
 *
 * Generic Methods
 */
fun <E> newNode(v: E): Node<E> {
    val result = Node<E>()
    result.value = v
    return result
}

fun <E> newNode(v: E, p: Node<E>?, n: Node<E>?): Node<E> {
    val result = newNode(v)
    result.previous = p
    result.next = n
    return result
}

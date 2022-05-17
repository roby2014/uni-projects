package serie2

fun <E> removeAfterIntersectionPoint(list1: Node<E>, list2: Node<E>, cmp: Comparator<E>) {
    list1.printTillNull()
    println()
    list2.printTillNull()

    var curr = list1.next
    var curr2 = list2.next

    while (true) {
        val (a, b) = curr?.value to curr2?.value
        val (c, d) = curr?.next!!.value to curr2?.next!!.value

        if (a == null || b == null || c == null || d == null)
            break

        println("a=$a  b=$b  c=$c  d=$d")
        if (cmp.compare(a, b) == 0 && cmp.compare(c, d) == 0) {
            // intersection here
            curr.deleteTillNull()
            break
        }

        curr = curr.next!!
        curr2 = curr2.next!!
    }

    println("AFTER INTERSECTION")

    list1.printTillNull()
    println()
    list2.printTillNull()
}

fun <E> getMiddle(list: Node<E>?): Node<E>? {
    var curr = list

    // count how many nodes
    var count = 0
    while (curr != null) {
        curr = curr.next
        count++
    }

    // loop and return the middle node
    curr = list // point to head again
    for (i in 0 until count) {
        if (i == count / 2) {
            return curr
        } else if (curr == null) {
            return null
        }
        curr = curr.next
    }

    return null
}

fun <E> bubbleSort(list: Node<E>, cmp: Comparator<E>) {
    // point at current node
    var curr = list.next

    // will point to the last bubble node
    var lastNode: Node<E>? = null

    // iterate while we can still swap aux with aux.next
    var swapping: Boolean
    do {
        swapping = false
        curr = list.next
        if (curr?.next == null || curr.value == null)
            break

        // iterate while aux.value is greater than aux.next.value
        while (curr != null && curr.next != lastNode && curr.next!!.value != null) {
            if (cmp.compare(curr.value, curr.next?.value) > 0) {
                curr.swapCurrNext()
                swapping = true
            }
            curr = curr.next
        }

        lastNode = curr
    } while (swapping)

    // for debugging purposes:
    // list.printTillNull() // should print ordered list!
}




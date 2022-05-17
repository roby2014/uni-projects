package serie2

class Node<E> {
    var previous: Node<E>? = null
    var next: Node<E>? = null
    var value: E? = null

    constructor() {}
    constructor(e: E) {
        value = e
    }

    /**
     * Returns a string with [this]'s node info
     */
    fun info()= "$this (value: ${this.value}) (prev: ${this.previous}) (next: ${this.next})"

    /**
     * Prints [this] double linked list until value is null
     * README: **Do not use this on circular double linked lists!**
     */
    fun printTillNull() {
        var curr = this.next
        while (curr?.value != null) {
            println(curr.info())
            curr = curr.next
        }
    }

    /**
     * Removes all nodes till it hits null, starting at head ([this])
     */
    fun deleteTillNull() {
        this.value = null
        var curr = this.next
        while (curr?.value != null) {
            curr.value = null
            curr = curr.next
        }
    }

    /**
     * Swaps current node's value ([this]) with next node's value
     */
    fun swapCurrNext() {
        val tmp = this.value
        this.value = this.next!!.value
        this.next!!.value = tmp
    }
}

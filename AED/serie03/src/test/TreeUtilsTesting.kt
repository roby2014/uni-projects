import series.serie3.Node
import kotlin.math.max

/**Generic BST Functions**/
fun <E> height(root: Node<E>?): Int {
    return if (root == null)  -1
    else 1 + max(height(root.left),height(root.right))
}

fun <E> search(root: Node<E>?, key: E, cmp: Comparator<E>): Node<E>? {
    if (root == null || cmp.compare(key, root.item) == 0) return root
    else if (cmp.compare(key, root.item) < 0) return search(root.left, key, cmp)
    else return search(root.right, key, cmp)
}

fun <E> add(root: Node<E>?, e: E, cmp: Comparator<E>): Node<E> {
    var root = root
    if (root == null) root = Node(e,null,null)
    else if (cmp.compare(e, root.item) < 0) root.left = add(root.left, e, cmp)
    else root.right = add(root.right, e, cmp)
    return root!!
}


//prefix path
fun <E> preorder(root: Node<E>?) {
    if (root != null) {
        print("$root.item ")
        preorder(root.left)
        preorder(root.right)
    }
}
//infix path
fun <E> inorder(root: Node<E>?) {
    if (root != null) {
        inorder(root.left)
        print("$root.item")
        inorder(root.right)
    }
}
//suffix path
fun <E> postorder(root: Node<E>?) {
    if (root != null) {
        postorder(root.left)
        postorder(root.right)
        print("$root.item")
    }
}

/*
   Create BSTs using Ints
    */
val cmp={ i1: Int, i2: Int -> i1 - i2 }

fun emptyBST(): Node<Int>?=null

fun singleNodeBST(i:Int): Node<Int>?=add(null,i,cmp)

fun populatedBST(array:IntArray): Node<Int>? {
    var tree: Node<Int>? = null
    for (i in array) tree = add(tree, i, cmp)
    return tree
}

fun leftChildsBST(): Node<Int>? {
    val nodes = intArrayOf(5, 4, 3, 2, 1)
    var tree: Node<Int>? = null
    for (i in nodes.indices) tree = add(tree, nodes[i], cmp)
    return tree
}

fun rightChildsBST(): Node<Int>? {
    val nodes = intArrayOf(1, 2, 3, 4, 5)
    var tree: Node<Int>? = null
    for (i in nodes.indices) tree = add(tree, nodes[i], cmp)
    return tree
}

fun populatedBST(): Node<Int>? {
    val nodes = intArrayOf(10, 4, 1, 8, 5, 6, 7, 0, 2, 3, 9, 12, 11)
    var tree: Node<Int>? = null
    for (i in nodes.indices) tree = add(tree, nodes[i], cmp)
    return tree
}

fun subPopulatedBST(): Node<Int>? {
    val nodes = intArrayOf(10, 4, 1, 8, 12, 11)
    var tree: Node<Int>? = null
    for (i in nodes.indices) tree = add(tree, nodes[i], cmp)
    return tree
}

fun completeBST(): Node<Int>? {
    val nodes = intArrayOf(8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7, 9, 11, 13, 15)
    var tree: Node<Int>? = null
    for (i in nodes.indices) tree = add(tree, nodes[i], cmp)
    return tree
}


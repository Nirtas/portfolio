package entropy

import kotlin.math.log2

fun calculateEntropy(array: Array<Array<Int>>): Double {
    var sum: Double = 0.0
    var px: Double = 0.0
    val n: Int = array.count() * array[0].count()
    var nx: Int = 0
    for (x in 0..255) {
        nx = 0
        for (i in array.indices) {
            nx += array[i].count { it == x }
        }
        if (nx == 0) continue
        px = nx.toDouble() / n
        sum += px * log2(px)
    }
    return -sum
}
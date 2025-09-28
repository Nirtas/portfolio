package util

object CommonUtils {
    fun pow(number: Double, degree: Int): Double {
        var result: Double = 1.0
        if (degree >= 0) {
            var i: Int = 1
            while (i <= degree) {
                result *= number
                i++
            }
        } else {
            var i: Int = 0
            while (i > degree) {
                result /= number
                i--
            }
        }
        return result
    }

    fun pow(number: Int, degree: Int): Double {
        return pow(number.toDouble(), degree)
    }

    inline fun <reified T> excludeRowsAndCols(array: Array<Array<T>>, blockSize: Int): Array<Array<T>> {
        val newArray: Array<Array<T>> = array.copyOf().map { it.copyOf() }.toTypedArray()
        for (i in newArray.indices) {
            for (j in newArray[i].indices) {
                if (i % blockSize != 0 || j % blockSize != 0) {
                    newArray[i][j] = newArray[i / blockSize * blockSize][j / blockSize * blockSize]
                }
            }
        }
        return newArray
    }

    infix fun <T, U, S, V, W> ((T, U, S, V) -> W).callWith(arguments: Pair<Triple<T, U, S>, V>): W =
        this(arguments.first.first, arguments.first.second, arguments.first.third, arguments.second)
}

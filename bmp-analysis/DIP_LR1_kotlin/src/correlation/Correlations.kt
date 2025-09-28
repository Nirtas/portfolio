package correlation

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun calculateMathExpectation(
    array: Array<Array<Double>>,
    height: Int,
    width: Int
): Double {
    var m: Double = 1.0 / (height * width)
    var sum: Double = 0.0
    for (i in array.indices) {
        for (j in array[i].indices) {
            sum += array[i][j]
        }
    }
    m *= sum
    return m
}

fun calculateVariance(
    array: Array<Array<Double>>,
    mathExpectation: Double,
    height: Int,
    width: Int
): Double {
    var variance: Double = 1.0 / (height * width - 1)
    var sum: Double = 0.0
    for (i in array.indices) {
        for (j in array[i].indices) {
            sum += (array[i][j] - mathExpectation).pow(2)
        }
    }
    variance *= sum
    return sqrt(variance)
}

fun calculateCorrelationCoefficient(
    arrayA: Array<Array<Int>>,
    arrayB: Array<Array<Int>>,
    x: Long = 0,
    y: Int = 0
): Double {
    if (arrayA.size != arrayB.size || arrayA[0].size != arrayB[0].size) {
        throw Exception("Different size")
    }

    val height: Int = arrayA.size
    val width: Int = arrayA[0].size

    val absX: Int = abs(x.toInt())
    val absY: Int = abs(y)
    val selectionHeight: Int = height - absY
    val selectionWidth: Int = width - absX

    val newArrayA: Array<Array<Double>>
    val newArrayB: Array<Array<Double>>

    if (y >= 0) {
        if (x >= 0) {
            newArrayA =
                arrayA.take(selectionHeight).map { it.take(selectionWidth).map { it.toDouble() }.toTypedArray() }
                    .toTypedArray()
            newArrayB = arrayB.takeLast(selectionHeight)
                .map { it.takeLast(selectionWidth).map { it.toDouble() }.toTypedArray() }.toTypedArray()
        } else {
            newArrayA = arrayA.take(selectionHeight)
                .map { it.takeLast(selectionWidth).map { it.toDouble() }.toTypedArray() }.toTypedArray()
            newArrayB = arrayB.takeLast(selectionHeight)
                .map { it.take(selectionWidth).map { it.toDouble() }.toTypedArray() }.toTypedArray()
        }
    } else {
        if (x >= 0) {
            newArrayA = arrayA.takeLast(selectionHeight)
                .map { it.take(selectionWidth).map { it.toDouble() }.toTypedArray() }.toTypedArray()
            newArrayB = arrayB.take(selectionHeight)
                .map { it.takeLast(selectionWidth).map { it.toDouble() }.toTypedArray() }.toTypedArray()
        } else {
            newArrayA = arrayA.takeLast(selectionHeight)
                .map { it.takeLast(selectionWidth).map { it.toDouble() }.toTypedArray() }.toTypedArray()
            newArrayB =
                arrayB.take(selectionHeight).map { it.take(selectionWidth).map { it.toDouble() }.toTypedArray() }
                    .toTypedArray()
        }
    }

    val mathExpectationA: Double = calculateMathExpectation(newArrayA, height, width)
    val mathExpectationB: Double = calculateMathExpectation(newArrayB, height, width)

    val varianceA: Double = calculateVariance(newArrayA, mathExpectationA, height, width)
    val varianceB: Double = calculateVariance(newArrayB, mathExpectationB, height, width)

    val array: Array<Array<Double>> =
        Array(selectionHeight) { heightIndex ->
            Array(selectionWidth) { widthIndex ->
                (newArrayA[heightIndex][widthIndex] - mathExpectationA) *
                        (newArrayB[heightIndex][widthIndex] - mathExpectationB)
            }
        }

    return calculateMathExpectation(array, height, width) / (varianceA * varianceB)
}
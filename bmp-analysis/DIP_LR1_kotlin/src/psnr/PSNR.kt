package psnr

import util.CommonUtils.pow
import kotlin.math.log10

fun calculatePSNR(arrayA: Array<Array<Int>>, arrayB: Array<Array<Int>>): Double {
    val height1: Int = arrayA.size
    val width1: Int = arrayA[0].size
    val height2: Int = arrayB.size
    val width2: Int = arrayB[0].size

    if (height1 != height2 || width1 != width2) {
        throw Exception("Different size")
    }

    val numerator: Double = width1 * height1 * pow(pow(2, 8) - 1, 2)
    var denominator: Double = 0.0
    for (i in 0..<height1) {
        for (j in 0..<width1) {
            denominator += pow(arrayA[i][j] - arrayB[i][j], 2)
        }
    }
    return 10 * log10(numerator / denominator)
}
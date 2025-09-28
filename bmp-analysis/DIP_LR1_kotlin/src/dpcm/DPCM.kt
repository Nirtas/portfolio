package dpcm

import kotlin.math.roundToInt

fun formDifArray(array: Array<Array<Int>>, rule: EDPCMRule): Array<Array<Int>> {
    val newArray: Array<Array<Int>> = Array(array.size) { Array(array[it].size) { 0 } }

    when (rule) {
        EDPCMRule.FIRST -> {
            for (i in 1..array.lastIndex) {
                for (j in 1..array[i].lastIndex) {
                    newArray[i][j] = array[i][j] - array[i][j - 1]
                }
            }
        }

        EDPCMRule.SECOND -> {
            for (i in 1..array.lastIndex) {
                for (j in 1..array[i].lastIndex) {
                    newArray[i][j] = array[i][j] - array[i - 1][j]
                }
            }
        }

        EDPCMRule.THIRD -> {
            for (i in 1..array.lastIndex) {
                for (j in 1..array[i].lastIndex) {
                    newArray[i][j] = array[i][j] - array[i - 1][j - 1]
                }
            }
        }

        EDPCMRule.FOURTH -> {
            for (i in 1..array.lastIndex) {
                for (j in 1..array[i].lastIndex) {
                    newArray[i][j] =
                        array[i][j] - ((array[i][j - 1] + array[i - 1][j] + array[i - 1][j - 1]) / 3.0).roundToInt()
                }
            }
        }
    }

    return newArray
}
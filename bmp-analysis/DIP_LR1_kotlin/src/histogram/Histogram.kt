package histogram

fun frequencies(array: Array<Array<Int>>): List<ComponentFrequency> {
    val frequencies: MutableList<ComponentFrequency> = mutableListOf()
    var index: Int = 0
    for (i in array.indices) {
        for (j in array[i].indices) {
            index = frequencies.indexOfFirst { it.x == array[i][j] }
            if (index >= 0) {
                frequencies[index].count++
            } else {
                frequencies.add(ComponentFrequency(array[i][j], 1))
            }
        }
    }
    return frequencies
}
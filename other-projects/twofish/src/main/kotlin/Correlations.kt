import kotlin.math.pow
import kotlin.math.sqrt

fun calculateCorrelationCoefficient(
    inputArray: Array<UByte>,
    outputArray: Array<UByte>,
    imageHeight: Int,
    imageWidth: Int
) {
    val inputArrayB: Array<UByte> = Array(imageHeight * imageWidth) { 0u }
    val inputArrayG: Array<UByte> = Array(imageHeight * imageWidth) { 0u }
    val inputArrayR: Array<UByte> = Array(imageHeight * imageWidth) { 0u }
    var m: Int = 0
    for (i in 0..<(imageHeight * imageWidth) step 3) {
        inputArrayB[m] = inputArray[i]
        inputArrayG[m] = inputArray[i + 1]
        inputArrayR[m++] = inputArray[i + 2]
    }
    val outputArrayB: Array<UByte> = Array(imageHeight * imageWidth) { 0u }
    val outputArrayG: Array<UByte> = Array(imageHeight * imageWidth) { 0u }
    val outputArrayR: Array<UByte> = Array(imageHeight * imageWidth) { 0u }
    m = 0
    for (i in 0..<(imageHeight * imageWidth) step 3) {
        outputArrayB[m] = outputArray[i]
        outputArrayG[m] = outputArray[i + 1]
        outputArrayR[m++] = outputArray[i + 2]
    }

    println("rBB = ${corr(inputArrayB, outputArrayB, imageHeight, imageWidth)}")
    println("rBG = ${corr(inputArrayB, outputArrayG, imageHeight, imageWidth)}")
    println("rBR = ${corr(inputArrayB, outputArrayR, imageHeight, imageWidth)}")
    println("rGB = ${corr(inputArrayG, outputArrayB, imageHeight, imageWidth)}")
    println("rGG = ${corr(inputArrayG, outputArrayG, imageHeight, imageWidth)}")
    println("rGR = ${corr(inputArrayG, outputArrayR, imageHeight, imageWidth)}")
    println("rRB = ${corr(inputArrayR, outputArrayB, imageHeight, imageWidth)}")
    println("rRG = ${corr(inputArrayR, outputArrayG, imageHeight, imageWidth)}")
    println("rRR = ${corr(inputArrayR, outputArrayR, imageHeight, imageWidth)}")
}

fun corr(inputArray: Array<UByte>, outputArray: Array<UByte>, height: Int, width: Int): Double {
    var mInput: Double = 1.0 / (height * width)
    var sumInput: Int = 0
    var mOutput: Double = 1.0 / (height * width)
    var sumOutput: Int = 0
    for (i in 0..<height) {
        for (j in 0..<width) {
            sumInput += inputArray[height * i + j].toInt()
            sumOutput += outputArray[height * i + j].toInt()
        }
    }
    mInput *= sumInput
    mOutput *= sumOutput

    var numerator: Double = 1.0 / (height * width)
    var sumNumerator: Double = 0.0
    for (i in 0..<height) {
        for (j in 0..<width) {
            sumNumerator += (inputArray[height * i + j].toDouble() - mInput) * (outputArray[height * i + j].toDouble() - mOutput)
        }
    }
    numerator *= sumNumerator

    var figInput: Double = 1.0 / (height * width - 1)
    var sumFigInput: Double = 0.0
    for (i in 0..<height) {
        for (j in 0..<width) {
            sumFigInput += (inputArray[height * i + j].toDouble() - mInput).pow(2)
        }
    }
    figInput *= sumFigInput
    figInput = sqrt(figInput)

    var figOutput: Double = 1.0 / (height * width - 1)
    var sumFigOutput: Double = 0.0
    for (i in 0..<height) {
        for (j in 0..<width) {
            sumFigOutput += (outputArray[height * i + j].toDouble() - mOutput).pow(2)
        }
    }
    figOutput *= sumFigOutput
    figOutput = sqrt(figOutput)
    return numerator / (figInput * figOutput)
}
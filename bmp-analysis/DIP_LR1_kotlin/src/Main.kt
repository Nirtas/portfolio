/*
    Доп 1а. Сформировать зеркальное изображение (отразить относительно вертикали).
*/

import bitmap.*
import correlation.Correlation
import correlation.calculateCorrelationCoefficient
import dpcm.EDPCMRule
import dpcm.formDifArray
import entropy.calculateEntropy
import histogram.frequencies
import psnr.calculatePSNR
import type.DWord
import type.UnsignedByte
import type.Word
import util.BitmapUtils.changePixelFormat
import util.FileUtils.CORRELATION_DIRECTORY_PATH
import util.FileUtils.HISTOGRAM_DIRECTORY_PATH
import util.FileUtils.ONE_COLOR_DIRECTORY_PATH
import util.FileUtils.REVERSED_DIRECTORY_PATH
import util.FileUtils.saveBitmapToFile
import util.FileUtils.writeCSV
import java.io.File

fun main() {
    println()
    val fileName: String = "house_2"
    val byteArray: ByteArray = File("$fileName.bmp").readBytes()

    //region Проверка Bitmap файла
    if (readWord(byteArray, 0).value.toInt() !in listOf(0x4d42, 0x4349, 0x5450)) {
        throw Exception("This is not a BMP file")
    }
    //endregion

    //region Формирование bitmapRGB
    val bitmapRGB: Bitmap = readBitmapRGB(byteArray)
    //endregion

    //region Преобразование из RGB в YCbCr
    val bitmapYCbCr: Bitmap = bitmapRGB.changePixelFormat(EPixelFormat.YCbCr)
    //endregion

    //region Сохранить bitmap как файл
    saveBitmapToFile(bitmapRGB, "${fileName}_RGB.bmp")
    saveBitmapToFile(bitmapYCbCr, "${fileName}_YCbCr.bmp")
    //endregion

    //region Коэффициент корреляции
    //Коэффициент rAB
    println("Коэффициенты корреляции RGB")
    val onlyR: Array<Array<Int>> = bitmapRGB.getPixelsArrayOnlyComponent1()
    val onlyG: Array<Array<Int>> = bitmapRGB.getPixelsArrayOnlyComponent2()
    val onlyB: Array<Array<Int>> = bitmapRGB.getPixelsArrayOnlyComponent3()
    //println("rRR = ${calculateCorrelationCoefficient(onlyR, onlyR)}")
    //println("rGG = ${calculateCorrelationCoefficient(onlyG, onlyG)}")
    //println("rBB = ${calculateCorrelationCoefficient(onlyB, onlyB)}")
    println("rRG = ${calculateCorrelationCoefficient(onlyR, onlyG)}")
    println("rRB = ${calculateCorrelationCoefficient(onlyR, onlyB)}")
    println("rBG = ${calculateCorrelationCoefficient(onlyG, onlyB)}")

    println()

    val yArray: Array<Int> = arrayOf(-10, -5, 0, 5, 10)
    val corrList11: MutableList<Correlation> = mutableListOf()
    val corrList22: MutableList<Correlation> = mutableListOf()
    val corrList33: MutableList<Correlation> = mutableListOf()

    //Коэффициент rAA(x, y)
    for (y in yArray) {
        for (x in -bitmapRGB.infoHeader.biWidth.value / 4..<bitmapRGB.infoHeader.biWidth.value / 4 step 2) {
            corrList11.add(Correlation(x, calculateCorrelationCoefficient(onlyR, onlyR, x, y)))
            corrList22.add(Correlation(x, calculateCorrelationCoefficient(onlyG, onlyG, x, y)))
            corrList33.add(Correlation(x, calculateCorrelationCoefficient(onlyB, onlyB, x, y)))
        }

        writeCSV("$CORRELATION_DIRECTORY_PATH/y = ${y}/correlationsRR.csv", corrList11)
        writeCSV("$CORRELATION_DIRECTORY_PATH/y = ${y}/correlationsGG.csv", corrList22)
        writeCSV("$CORRELATION_DIRECTORY_PATH/y = ${y}/correlationsBB.csv", corrList33)

        corrList11.clear()
        corrList22.clear()
        corrList33.clear()
    }

    //Коэффициент rAB
    println("Коэффициенты корреляции YCbCr")
    val onlyY: Array<Array<Int>> = bitmapYCbCr.getPixelsArrayOnlyComponent1()
    val onlyCb: Array<Array<Int>> = bitmapYCbCr.getPixelsArrayOnlyComponent2()
    val onlyCr: Array<Array<Int>> = bitmapYCbCr.getPixelsArrayOnlyComponent3()
    //println("rYY = ${calculateCorrelationCoefficient(onlyY, onlyY)}")
    //println("rCbCb = ${calculateCorrelationCoefficient(onlyCb, onlyCb)}")
    //println("rCrCr = ${calculateCorrelationCoefficient(onlyCr, onlyCr)}")
    println("rYCb = ${calculateCorrelationCoefficient(onlyY, onlyCb)}")
    println("rYCr = ${calculateCorrelationCoefficient(onlyY, onlyCr)}")
    println("rCbCr = ${calculateCorrelationCoefficient(onlyCb, onlyCr)}")

    println()

    //Коэффициент rAA(x, y)
    for (y in yArray) {
        for (x in -bitmapYCbCr.infoHeader.biWidth.value / 4..<bitmapYCbCr.infoHeader.biWidth.value / 4 step 2) {
            corrList11.add(
                Correlation(
                    x,
                    calculateCorrelationCoefficient(onlyY, onlyY, x, y)
                )
            )
            corrList22.add(
                Correlation(
                    x,
                    calculateCorrelationCoefficient(onlyCb, onlyCb, x, y)
                )
            )
            corrList33.add(
                Correlation(
                    x,
                    calculateCorrelationCoefficient(onlyCr, onlyCr, x, y)
                )
            )
        }

        writeCSV("$CORRELATION_DIRECTORY_PATH/y = ${y}/correlationsYY.csv", corrList11)
        writeCSV("$CORRELATION_DIRECTORY_PATH/y = ${y}/correlationsCbCb.csv", corrList22)
        writeCSV("$CORRELATION_DIRECTORY_PATH/y = ${y}/correlationsCrCr.csv", corrList33)

        corrList11.clear()
        corrList22.clear()
        corrList33.clear()
    }
    //endregion

    //region Доп
    val bitmapRGBReversed: Bitmap = bitmapRGB.copy(
        fileHeader = bitmapRGB.fileHeader.copy(),
        infoHeader = bitmapRGB.infoHeader.copy(),
        pixels = bitmapRGB.pixels.map { it.map { it.getPixel() }.reversed().toTypedArray() }.toTypedArray()
    )
    saveBitmapToFile(bitmapRGBReversed, "$REVERSED_DIRECTORY_PATH/${fileName}_reversed.bmp")
    //endregion

    //region Картинки с одним цветом
    val bitmapOnlyR: Bitmap = bitmapRGB.getBitmapWithComponent1()
    saveBitmapToFile(bitmapOnlyR, "$ONE_COLOR_DIRECTORY_PATH/${fileName}_onlyR.bmp")

    val bitmapOnlyG: Bitmap = bitmapRGB.getBitmapWithComponent2()
    saveBitmapToFile(bitmapOnlyG, "$ONE_COLOR_DIRECTORY_PATH/${fileName}_onlyG.bmp")

    val bitmapOnlyB: Bitmap = bitmapRGB.getBitmapWithComponent3()
    saveBitmapToFile(bitmapOnlyB, "$ONE_COLOR_DIRECTORY_PATH/${fileName}_onlyB.bmp")

    val bitmapOnlyY: Bitmap = bitmapYCbCr.getBitmapWithComponent1()
    saveBitmapToFile(bitmapOnlyY, "$ONE_COLOR_DIRECTORY_PATH/${fileName}_onlyY.bmp")

    val bitmapOnlyCb: Bitmap = bitmapYCbCr.getBitmapWithComponent2()
    saveBitmapToFile(bitmapOnlyCb, "$ONE_COLOR_DIRECTORY_PATH/${fileName}_onlyCb.bmp")

    val bitmapOnlyCr: Bitmap = bitmapYCbCr.getBitmapWithComponent3()
    saveBitmapToFile(bitmapOnlyCr, "$ONE_COLOR_DIRECTORY_PATH/${fileName}_onlyCr.bmp")
    //endregion

    //region PSNR RGB
    println("PSNR RGB")
    val bitmapRGBRestored: Bitmap = bitmapYCbCr.changePixelFormat(EPixelFormat.RGB)
    val onlyRRestored: Array<Array<Int>> = bitmapRGBRestored.getPixelsArrayOnlyComponent1()
    val onlyGRestored: Array<Array<Int>> = bitmapRGBRestored.getPixelsArrayOnlyComponent2()
    val onlyBRestored: Array<Array<Int>> = bitmapRGBRestored.getPixelsArrayOnlyComponent3()
    println("PSNR R = ${calculatePSNR(onlyR, onlyRRestored)}")
    println("PSNR G = ${calculatePSNR(onlyG, onlyGRestored)}")
    println("PSNR B = ${calculatePSNR(onlyB, onlyBRestored)}")

    println()
    //endregion

    //region Децимация компонент Cb и Cr в blockSize раз по ширине и в blockSize раз по высоте
    val blockSize: Int = 4

    //region Исключение строк и столбцов
    println("Исключение строк и столбцов, размер уменьшен в ${blockSize * blockSize} раз")
    val bitmapYCbCrExcluded: Bitmap = bitmapYCbCr.excludeRowsAndCols(blockSize)

    //Перевод в RGB
    val bitmapRGBAfterExcluding: Bitmap = bitmapYCbCrExcluded.changePixelFormat(EPixelFormat.RGB)

    //region PSNR после децимации Cb и Cr
    val onlyCbExcluded: Array<Array<Int>> = bitmapYCbCrExcluded.getPixelsArrayOnlyComponent2()
    val onlyCrExcluded: Array<Array<Int>> = bitmapYCbCrExcluded.getPixelsArrayOnlyComponent3()
    println("PSNR Cb = ${calculatePSNR(onlyCb, onlyCbExcluded)}")
    println("PSNR Cr = ${calculatePSNR(onlyCr, onlyCrExcluded)}")

    val onlyRAfterExcluding: Array<Array<Int>> = bitmapRGBAfterExcluding.getPixelsArrayOnlyComponent1()
    val onlyGAfterExcluding: Array<Array<Int>> = bitmapRGBAfterExcluding.getPixelsArrayOnlyComponent2()
    val onlyBAfterExcluding: Array<Array<Int>> = bitmapRGBAfterExcluding.getPixelsArrayOnlyComponent3()
    println("PSNR R = ${calculatePSNR(onlyR, onlyRAfterExcluding)}")
    println("PSNR G = ${calculatePSNR(onlyG, onlyGAfterExcluding)}")
    println("PSNR B = ${calculatePSNR(onlyB, onlyBAfterExcluding)}")
    //endregion

    //endregion

    println()

    //region Среднее арифметическое blockSize * blockSize смежных элементов
    println("Среднее арифметическое ${blockSize * blockSize} смежных элементов")
    val bitmapYCbCrDecimated: Bitmap = bitmapYCbCr.decimatePixels(blockSize)

    //Перевод в RGB
    val bitmapRGBAfterDecimation: Bitmap = bitmapYCbCrDecimated.changePixelFormat(EPixelFormat.RGB)

    //region PSNR после децимации Cb и Cr
    val onlyCbDecimated: Array<Array<Int>> = bitmapYCbCrDecimated.getPixelsArrayOnlyComponent2()
    val onlyCrDecimated: Array<Array<Int>> = bitmapYCbCrDecimated.getPixelsArrayOnlyComponent3()
    println("PSNR Cb = ${calculatePSNR(onlyCb, onlyCbDecimated)}")
    println("PSNR Cr = ${calculatePSNR(onlyCr, onlyCrDecimated)}")

    val onlyRAfterDecimation: Array<Array<Int>> = bitmapRGBAfterDecimation.getPixelsArrayOnlyComponent1()
    val onlyGAfterDecimation: Array<Array<Int>> = bitmapRGBAfterDecimation.getPixelsArrayOnlyComponent2()
    val onlyBAfterDecimation: Array<Array<Int>> = bitmapRGBAfterDecimation.getPixelsArrayOnlyComponent3()
    println("PSNR R = ${calculatePSNR(onlyR, onlyRAfterDecimation)}")
    println("PSNR G = ${calculatePSNR(onlyG, onlyGAfterDecimation)}")
    println("PSNR B = ${calculatePSNR(onlyB, onlyBAfterDecimation)}")
    //endregion

    //endregion

    println()
    //endregion

    //region Сохранение прореженных изображений в файл
    saveBitmapToFile(bitmapRGBAfterExcluding, "${fileName}_RGB_excluded_$blockSize.bmp")
    saveBitmapToFile(bitmapRGBAfterDecimation, "${fileName}_RGB_decimated_$blockSize.bmp")
    //endregion

    //region Гистограммы
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/R/original.csv", frequencies(onlyR))
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/G/original.csv", frequencies(onlyG))
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/B/original.csv", frequencies(onlyB))

    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Y/original.csv", frequencies(onlyY))
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cb/original.csv", frequencies(onlyCb))
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cr/original.csv", frequencies(onlyCr))
    //endregion

    //region Энтропия
    println("Энтропия")
    println("entropy R: ${calculateEntropy(bitmapRGB.getPixelsArrayOnlyComponent1())}")
    println("entropy G: ${calculateEntropy(bitmapRGB.getPixelsArrayOnlyComponent2())}")
    println("entropy B: ${calculateEntropy(bitmapRGB.getPixelsArrayOnlyComponent3())}")

    println("entropy Y: ${calculateEntropy(bitmapYCbCr.getPixelsArrayOnlyComponent1())}")
    println("entropy Cb: ${calculateEntropy(bitmapYCbCr.getPixelsArrayOnlyComponent2())}")
    println("entropy Cr: ${calculateEntropy(bitmapYCbCr.getPixelsArrayOnlyComponent3())}")
    println()
    //endregion

    //region Анализ эффективности разностного кодирования
    val D1R: Array<Array<Int>> = formDifArray(onlyR, EDPCMRule.FIRST)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/R/DPCM 1 rule.csv", frequencies(D1R))

    val D1G: Array<Array<Int>> = formDifArray(onlyG, EDPCMRule.FIRST)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/G/DPCM 1 rule.csv", frequencies(D1G))

    val D1B: Array<Array<Int>> = formDifArray(onlyB, EDPCMRule.FIRST)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/B/DPCM 1 rule.csv", frequencies(D1B))

    val D1Y: Array<Array<Int>> = formDifArray(onlyY, EDPCMRule.FIRST)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Y/DPCM 1 rule.csv", frequencies(D1Y))

    val D1Cb: Array<Array<Int>> = formDifArray(onlyCb, EDPCMRule.FIRST)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cb/DPCM 1 rule.csv", frequencies(D1Cb))

    val D1Cr: Array<Array<Int>> = formDifArray(onlyCr, EDPCMRule.FIRST)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cr/DPCM 1 rule.csv", frequencies(D1Cr))

    val D2R: Array<Array<Int>> = formDifArray(onlyR, EDPCMRule.SECOND)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/R/DPCM 2 rule.csv", frequencies(D2R))

    val D2G: Array<Array<Int>> = formDifArray(onlyG, EDPCMRule.SECOND)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/G/DPCM 2 rule.csv", frequencies(D2G))

    val D2B: Array<Array<Int>> = formDifArray(onlyB, EDPCMRule.SECOND)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/B/DPCM 2 rule.csv", frequencies(D2B))

    val D2Y: Array<Array<Int>> = formDifArray(onlyY, EDPCMRule.SECOND)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Y/DPCM 2 rule.csv", frequencies(D2Y))

    val D2Cb: Array<Array<Int>> = formDifArray(onlyCb, EDPCMRule.SECOND)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cb/DPCM 2 rule.csv", frequencies(D2Cb))

    val D2Cr: Array<Array<Int>> = formDifArray(onlyCr, EDPCMRule.SECOND)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cr/DPCM 2 rule.csv", frequencies(D2Cr))

    val D3R: Array<Array<Int>> = formDifArray(onlyR, EDPCMRule.THIRD)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/R/DPCM 3 rule.csv", frequencies(D3R))

    val D3G: Array<Array<Int>> = formDifArray(onlyG, EDPCMRule.THIRD)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/G/DPCM 3 rule.csv", frequencies(D3G))

    val D3B: Array<Array<Int>> = formDifArray(onlyB, EDPCMRule.THIRD)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/B/DPCM 3 rule.csv", frequencies(D3B))

    val D3Y: Array<Array<Int>> = formDifArray(onlyY, EDPCMRule.THIRD)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Y/DPCM 3 rule.csv", frequencies(D3Y))

    val D3Cb: Array<Array<Int>> = formDifArray(onlyCb, EDPCMRule.THIRD)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cb/DPCM 3 rule.csv", frequencies(D3Cb))

    val D3Cr: Array<Array<Int>> = formDifArray(onlyCr, EDPCMRule.THIRD)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cr/DPCM 3 rule.csv", frequencies(D3Cr))

    val D4R: Array<Array<Int>> = formDifArray(onlyR, EDPCMRule.FOURTH)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/R/DPCM 4 rule.csv", frequencies(D4R))

    val D4G: Array<Array<Int>> = formDifArray(onlyG, EDPCMRule.FOURTH)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/G/DPCM 4 rule.csv", frequencies(D4G))

    val D4B: Array<Array<Int>> = formDifArray(onlyB, EDPCMRule.FOURTH)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/B/DPCM 4 rule.csv", frequencies(D4B))

    val D4Y: Array<Array<Int>> = formDifArray(onlyY, EDPCMRule.FOURTH)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Y/DPCM 4 rule.csv", frequencies(D4Y))

    val D4Cb: Array<Array<Int>> = formDifArray(onlyCb, EDPCMRule.FOURTH)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cb/DPCM 4 rule.csv", frequencies(D4Cb))

    val D4Cr: Array<Array<Int>> = formDifArray(onlyCr, EDPCMRule.FOURTH)
    writeCSV("$HISTOGRAM_DIRECTORY_PATH/Cr/DPCM 4 rule.csv", frequencies(D4Cr))
    //endregion

    //region Энтропия DPCM
    println("Энтропия DPCM")
    println("Правило 1")
    println("entropy R: ${calculateEntropy(D1R)}")
    println("entropy G: ${calculateEntropy(D1G)}")
    println("entropy B: ${calculateEntropy(D1B)}")
    println("entropy Y: ${calculateEntropy(D1Y)}")
    println("entropy Cb: ${calculateEntropy(D1Cb)}")
    println("entropy Cr: ${calculateEntropy(D1Cr)}")
    println()

    println("Правило 2")
    println("entropy R: ${calculateEntropy(D2R)}")
    println("entropy G: ${calculateEntropy(D2G)}")
    println("entropy B: ${calculateEntropy(D2B)}")
    println("entropy Y: ${calculateEntropy(D2Y)}")
    println("entropy Cb: ${calculateEntropy(D2Cb)}")
    println("entropy Cr: ${calculateEntropy(D2Cr)}")
    println()

    println("Правило 3")
    println("entropy R: ${calculateEntropy(D3R)}")
    println("entropy G: ${calculateEntropy(D3G)}")
    println("entropy B: ${calculateEntropy(D3B)}")
    println("entropy Y: ${calculateEntropy(D3Y)}")
    println("entropy Cb: ${calculateEntropy(D3Cb)}")
    println("entropy Cr: ${calculateEntropy(D3Cr)}")
    println()

    println("Правило 4")
    println("entropy R: ${calculateEntropy(D4R)}")
    println("entropy G: ${calculateEntropy(D4G)}")
    println("entropy B: ${calculateEntropy(D4B)}")
    println("entropy Y: ${calculateEntropy(D4Y)}")
    println("entropy Cb: ${calculateEntropy(D4Cb)}")
    println("entropy Cr: ${calculateEntropy(D4Cr)}")
    println()
    //endregion
}

fun readWord(byteArray: ByteArray, startPosition: Int): Word {
    val value: Int = ((byteArray[startPosition].toInt() shl 8) or (byteArray[startPosition + 1].toInt() and 0xFF))
    return Word(value).reversed()
}

fun readDWord(byteArray: ByteArray, startPosition: Int): DWord {
    val value: Long = ((byteArray[startPosition].toInt() shl 24) or (byteArray[startPosition + 1].toInt() shl 16) or
            (byteArray[startPosition + 2].toInt() shl 8) or (byteArray[startPosition + 3].toInt())).toLong()
    return DWord(value).reversed()
}

fun readUnsignedByte(byteArray: ByteArray, startPosition: Int): UnsignedByte = UnsignedByte(byteArray[startPosition])

fun readBitmapRGB(byteArray: ByteArray): Bitmap {
    val bitmapFileHeader: BitmapFileHeader = BitmapFileHeader(
        bfType = readWord(byteArray, 0),
        bfSize = readDWord(byteArray, 2),
        bfReserved1 = readWord(byteArray, 6),
        bfReserved2 = readWord(byteArray, 8),
        bfOffBits = readDWord(byteArray, 10)
    )
    val bitmapInfoHeader: BitmapInfoHeader = BitmapInfoHeader(
        biSize = readDWord(byteArray, 14),
        biWidth = readDWord(byteArray, 18),
        biHeight = readDWord(byteArray, 22),
        biPlanes = readWord(byteArray, 26),
        biBitCount = readWord(byteArray, 28),
        biCompression = readDWord(byteArray, 30),
        biSizeImage = readDWord(byteArray, 34),
        biXPelsPerMeter = readDWord(byteArray, 38),
        biYPelsPerMeter = readDWord(byteArray, 42),
        biClrUsed = readDWord(byteArray, 46),
        biClrImportant = readDWord(byteArray, 50)
    )
    val pixelsStartPosition: Int = (bitmapInfoHeader.biSize + 14).value.toInt()
    val pixels: Array<Array<Pixel>> = Array(bitmapInfoHeader.biHeight.value.toInt()) { heightIndex ->
        Array(bitmapInfoHeader.biWidth.value.toInt()) { widthIndex ->
            RGB(
                readUnsignedByte(
                    byteArray,
                    pixelsStartPosition + heightIndex * bitmapInfoHeader.biWidth.value.toInt() * 3 + widthIndex * 3 + 2
                ),
                readUnsignedByte(
                    byteArray,
                    pixelsStartPosition + heightIndex * bitmapInfoHeader.biWidth.value.toInt() * 3 + widthIndex * 3 + 1
                ),
                readUnsignedByte(
                    byteArray,
                    pixelsStartPosition + heightIndex * bitmapInfoHeader.biWidth.value.toInt() * 3 + widthIndex * 3
                )
            )
        }
    }
    return Bitmap(bitmapFileHeader, bitmapInfoHeader, pixels)
}

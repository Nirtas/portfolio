package util

import bitmap.Bitmap
import bitmap.EPixelFormat
import bitmap.Pixel
import bitmap.YCbCr
import type.UnsignedByte
import util.PixelUtils.asRGB
import util.PixelUtils.asYCbCr

object BitmapUtils {
    fun Bitmap.changePixelFormat(newFormat: EPixelFormat): Bitmap {
        return Bitmap(
            fileHeader = this.fileHeader.copy(),
            infoHeader = this.infoHeader.copy(),
            pixels = this.pixels.map {
                it.map { pixel ->
                    when (newFormat) {
                        EPixelFormat.RGB -> {
                            pixel.asRGB()
                        }

                        EPixelFormat.YCbCr -> {
                            pixel.asYCbCr()
                        }
                    }
                }.toTypedArray()
            }.toTypedArray()
        )
    }

    fun restoreYCbCrArray(
        y: Array<Array<UnsignedByte>>,
        cb: Array<Array<UnsignedByte>>,
        cr: Array<Array<UnsignedByte>>,
        blockSize: Int
    ): Array<Array<Pixel>> {
        if (y.size % blockSize != 0 || y[0].size % blockSize != 0) {
            throw Exception("The array size must be a multiple of $blockSize")
        }
        if (cb.size != cr.size || cb[0].size != cr[0].size || y.size != blockSize * cb.size || y[0].size != blockSize * cb[0].size) {
            throw Exception("Different size")
        }
        val array: Array<Array<Pixel>> =
            Array(y.size) { Array(y[it].size) { YCbCr() } }
        for (i in y.indices) {
            for (j in y[i].indices) {
                array[i][j] = YCbCr(y[i][j], cb[i / blockSize][j / blockSize], cr[i / blockSize][j / blockSize])
            }
        }
        return array
    }

    fun decimateYCbCrArray(
        array: Array<Array<Pixel>>,
        blockSize: Int
    ): Triple<Array<Array<UnsignedByte>>, Array<Array<UnsignedByte>>, Array<Array<UnsignedByte>>> {
        if (array.size % blockSize != 0 || array[0].size % blockSize != 0) {
            throw Exception("The array size must be a multiple of $blockSize")
        }
        val newCb: Array<Array<UnsignedByte>> =
            Array(array.size / blockSize) { Array(array[it].size / blockSize) { UnsignedByte(0) } }
        val newCr: Array<Array<UnsignedByte>> =
            Array(array.size / blockSize) { Array(array[it].size / blockSize) { UnsignedByte(0) } }
        var averageCbValue: Long = 0
        var averageCrValue: Long = 0
        for ((heightIndex, i) in (array.indices step blockSize).withIndex()) {
            for ((widthIndex, j) in (array[i].indices step blockSize).withIndex()) {
                averageCbValue = 0
                averageCrValue = 0
                for (l in i..<i + blockSize) {
                    for (k in j..<j + blockSize) {
                        averageCbValue += array[l][k].getComponent2().value
                        averageCrValue += array[l][k].getComponent3().value
                    }
                }
                averageCbValue /= blockSize * blockSize
                averageCrValue /= blockSize * blockSize
                newCb[heightIndex][widthIndex] = UnsignedByte(averageCbValue)
                newCr[heightIndex][widthIndex] = UnsignedByte(averageCrValue)
            }
        }
        return Triple(array.map { it.map { it.getComponent1() }.toTypedArray() }.toTypedArray(), newCb, newCr)
    }
}
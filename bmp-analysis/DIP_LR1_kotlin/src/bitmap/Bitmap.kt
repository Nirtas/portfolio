package bitmap

import type.UnsignedByte
import util.BitmapUtils
import util.CommonUtils
import util.CommonUtils.callWith

data class Bitmap(
    val fileHeader: BitmapFileHeader,
    val infoHeader: BitmapInfoHeader,
    val pixels: Array<Array<Pixel>>,
) {
    override fun toString(): String {
        return "Bitmap:\n$fileHeader\n$infoHeader\n${pixels.contentDeepToString()}"
    }

    fun getPixelsArrayOnlyComponent1(): Array<Array<Int>> {
        return this.pixels.map { it.map { it.getComponent1().value.toInt() }.toTypedArray() }.toTypedArray()
    }

    fun getPixelsArrayOnlyComponent2(): Array<Array<Int>> {
        return this.pixels.map { it.map { it.getComponent2().value.toInt() }.toTypedArray() }.toTypedArray()
    }

    fun getPixelsArrayOnlyComponent3(): Array<Array<Int>> {
        return this.pixels.map { it.map { it.getComponent3().value.toInt() }.toTypedArray() }.toTypedArray()
    }

    fun excludeRowsAndCols(blockSize: Int): Bitmap = copy(
        fileHeader = fileHeader.copy(),
        infoHeader = infoHeader.copy(),
        pixels = CommonUtils.excludeRowsAndCols(pixels, blockSize)
    )

    fun decimatePixels(blockSize: Int): Bitmap = copy(
        fileHeader = fileHeader.copy(),
        infoHeader = infoHeader.copy(),
        pixels = BitmapUtils::restoreYCbCrArray callWith Pair(
            BitmapUtils.decimateYCbCrArray(pixels, blockSize),
            blockSize
        )
    )

    fun toByteArray(): ByteArray {
        var byteArray: ByteArray = byteArrayOf(
            *fileHeader.toByteArray(),
            *infoHeader.toByteArray()
        )
        val pixels: Array<ByteArray> = pixels.flatten().map { it.toByteArray() }.toTypedArray()
        for (array in pixels) {
            byteArray += byteArrayOf(*array)
        }
        return byteArray
    }

    fun getBitmapWithComponent1(): Bitmap {
        val bitmapOnlyComponent1: Bitmap = copy(
            fileHeader = fileHeader.copy(),
            infoHeader = infoHeader.copy(),
            pixels = pixels.map { it.map { it.getPixel() }.toTypedArray() }.toTypedArray()
        )
        when (pixels[0][0].getPixelFormat()) {
            EPixelFormat.RGB -> {
                for (i in bitmapOnlyComponent1.pixels.indices) {
                    for (j in bitmapOnlyComponent1.pixels[i].indices) {
                        bitmapOnlyComponent1.pixels[i][j].setComponent2(UnsignedByte(0))
                        bitmapOnlyComponent1.pixels[i][j].setComponent3(UnsignedByte(0))
                    }
                }
            }

            EPixelFormat.YCbCr -> {
                for (i in bitmapOnlyComponent1.pixels.indices) {
                    for (j in bitmapOnlyComponent1.pixels[i].indices) {
                        bitmapOnlyComponent1.pixels[i][j].setComponent2(pixels[i][j].getComponent1())
                        bitmapOnlyComponent1.pixels[i][j].setComponent3(pixels[i][j].getComponent1())
                    }
                }
            }
        }
        return bitmapOnlyComponent1
    }

    fun getBitmapWithComponent2(): Bitmap {
        val bitmapOnlyComponent2: Bitmap = copy(
            fileHeader = fileHeader.copy(),
            infoHeader = infoHeader.copy(),
            pixels = pixels.map { it.map { it.getPixel() }.toTypedArray() }.toTypedArray()
        )
        when (pixels[0][0].getPixelFormat()) {
            EPixelFormat.RGB -> {
                for (i in bitmapOnlyComponent2.pixels.indices) {
                    for (j in bitmapOnlyComponent2.pixels[i].indices) {
                        bitmapOnlyComponent2.pixels[i][j].setComponent1(UnsignedByte(0))
                        bitmapOnlyComponent2.pixels[i][j].setComponent3(UnsignedByte(0))
                    }
                }
            }

            EPixelFormat.YCbCr -> {
                for (i in bitmapOnlyComponent2.pixels.indices) {
                    for (j in bitmapOnlyComponent2.pixels[i].indices) {
                        bitmapOnlyComponent2.pixels[i][j].setComponent1(pixels[i][j].getComponent2())
                        bitmapOnlyComponent2.pixels[i][j].setComponent3(pixels[i][j].getComponent2())
                    }
                }
            }
        }
        return bitmapOnlyComponent2
    }

    fun getBitmapWithComponent3(): Bitmap {
        val bitmapOnlyComponent3: Bitmap = copy(
            fileHeader = fileHeader.copy(),
            infoHeader = infoHeader.copy(),
            pixels = pixels.map { it.map { it.getPixel() }.toTypedArray() }.toTypedArray()
        )
        when (pixels[0][0].getPixelFormat()) {
            EPixelFormat.RGB -> {
                for (i in bitmapOnlyComponent3.pixels.indices) {
                    for (j in bitmapOnlyComponent3.pixels[i].indices) {
                        bitmapOnlyComponent3.pixels[i][j].setComponent1(UnsignedByte(0))
                        bitmapOnlyComponent3.pixels[i][j].setComponent2(UnsignedByte(0))
                    }
                }
            }

            EPixelFormat.YCbCr -> {
                for (i in bitmapOnlyComponent3.pixels.indices) {
                    for (j in bitmapOnlyComponent3.pixels[i].indices) {
                        bitmapOnlyComponent3.pixels[i][j].setComponent1(pixels[i][j].getComponent3())
                        bitmapOnlyComponent3.pixels[i][j].setComponent2(pixels[i][j].getComponent3())
                    }
                }
            }
        }
        return bitmapOnlyComponent3
    }
}



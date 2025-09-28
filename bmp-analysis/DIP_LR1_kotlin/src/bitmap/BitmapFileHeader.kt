package bitmap

import type.DWord
import type.UnsignedByte
import type.Word

data class BitmapFileHeader(
    val bfType: Word,       //Описывает тип файла, всегда ASCII-код двух символов 'B' и 'M'
    val bfSize: DWord,      //Размер файла в байтах
    val bfReserved1: Word,  //Зарезервировано, должно быть 0
    val bfReserved2: Word,  //Зарезервировано, должно быть 0
    val bfOffBits: DWord    //Смещение в байтах от начала заголовка до массива пикселей
) {
    override fun toString(): String {
        return "bitmap.BitmapFileHeader: " +
                "bfType = ${bfType.toString(16)}, " +
                "bfSize = ${bfSize.toString(16)}, " +
                "bfReserved1 = ${bfReserved1.toString(16)}, " +
                "bfReserved2 = ${bfReserved2.toString(16)}, " +
                "bfOffBits = ${bfOffBits.toString(16)}"
    }

    fun toByteArray(): ByteArray {
        val byteArray: ByteArray = byteArrayOf(
            *bfType.reversed().toUnsignedByte(),
            *bfSize.reversed().toUnsignedByte(),
            *bfReserved1.reversed().toUnsignedByte(),
            *bfReserved2.reversed().toUnsignedByte(),
            *bfOffBits.reversed().toUnsignedByte()
        )
        return byteArray
    }

    private fun byteArrayOf(vararg elements: UnsignedByte): ByteArray {
        val byteArray: ByteArray = ByteArray(elements.size)
        for (i in elements.indices) {
            byteArray[i] = elements[i].value.toByte()
        }
        return byteArray
    }
}
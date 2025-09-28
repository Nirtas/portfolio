package bitmap

import type.DWord
import type.UnsignedByte
import type.Word

data class BitmapInfoHeader(
    val biSize: DWord,          //Размер этого класса в байтах
    val biWidth: DWord,         //Ширина изображения в пикселях
    val biHeight: DWord,        //Высота изображения в пикселях. Если положительна, то строки изображения
                                //хранятся снизу вверх. Отрицательна - сверху вниз
    val biPlanes: Word,         //Количество плоскостей изображения всегда должно быть 1
    val biBitCount: Word,       //Количество бит на пиксель. Для RGB24 равно 24
    val biCompression: DWord,   //Тип сжатия, для RGB24 равно BI_RGB (нулевое значение)
    val biSizeImage: DWord,     //Размер изображения в байтах. Необязательное поле, может быть равно нулю
    val biXPelsPerMeter: DWord, //Горизонтальное разрешение в пикселях на метр. Используется для указания
                                //физического размера изображения. Не является обязательным
    val biYPelsPerMeter: DWord, //Вертикальное разрешение в пикселях на метр. Используется для указания
                                //физического размера изображения. Не является обязательным
    val biClrUsed: DWord,       //Размер палитры (равно 0 для RGB24)
    val biClrImportant: DWord   //Число значимых элементов палитры. Равно 0 для RGB24
) {
    override fun toString(): String {
        return "bitmap.BitmapInfoHeader: " +
                "biSize = ${biSize.toString(16)}, " +
                "biWidth = ${biWidth.toString(16)}, " +
                "biHeight = ${biHeight.toString(16)}, " +
                "biPlanes = ${biPlanes.toString(16)}, " +
                "biBitCount = ${biBitCount.toString(16)}, " +
                "biCompression = ${biCompression.toString(16)}, " +
                "biSizeImage = ${biSizeImage.toString(16)}, " +
                "biXPelsPerMeter = ${biXPelsPerMeter.toString(16)}, " +
                "biYPelsPerMeter = ${biYPelsPerMeter.toString(16)}, " +
                "biClrUsed = ${biClrUsed.toString(16)}, " +
                "biClrImportant = ${biClrImportant.toString(16)}"
    }

    fun toByteArray(): ByteArray {
        val byteArray: ByteArray = byteArrayOf(
            *biSize.reversed().toUnsignedByte(),
            *biWidth.reversed().toUnsignedByte(),
            *biHeight.reversed().toUnsignedByte(),
            *biPlanes.reversed().toUnsignedByte(),
            *biBitCount.reversed().toUnsignedByte(),
            *biCompression.reversed().toUnsignedByte(),
            *biSizeImage.reversed().toUnsignedByte(),
            *biXPelsPerMeter.reversed().toUnsignedByte(),
            *biYPelsPerMeter.reversed().toUnsignedByte(),
            *biClrUsed.reversed().toUnsignedByte(),
            *biClrImportant.reversed().toUnsignedByte()
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
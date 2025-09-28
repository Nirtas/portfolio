package bitmap

import type.UnsignedByte

open class YCbCr: Pixel {
    var Y: UnsignedByte
    var Cb: UnsignedByte
    var Cr: UnsignedByte

    override fun getPixelFormat(): EPixelFormat = EPixelFormat.YCbCr

    override fun getPixel(): Pixel = YCbCr(Y, Cb, Cr)

    override fun getComponent1(): UnsignedByte = Y
    override fun getComponent2(): UnsignedByte = Cb
    override fun getComponent3(): UnsignedByte = Cr

    override fun setComponent1(value: UnsignedByte) { Y = value }
    override fun setComponent2(value: UnsignedByte) { Cb = value }
    override fun setComponent3(value: UnsignedByte) { Cr = value }

    constructor() {
        this.Y = UnsignedByte(0)
        this.Cb = UnsignedByte(0)
        this.Cr = UnsignedByte(0)
    }

    constructor(_Y: UnsignedByte, _Cb: UnsignedByte, _Cr: UnsignedByte) {
        this.Y = _Y
        this.Cb = _Cb
        this.Cr = _Cr
    }

    override fun toString(): String {
        return "(${Y.toString(16)}, ${Cb.toString(16)}, ${Cr.toString(16)})"
    }

    override fun toByteArray(): ByteArray {
        val byteArray: ByteArray = byteArrayOf(Cr, Cb, Y)
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
package bitmap

import type.UnsignedByte

open class RGB: Pixel {
    var R: UnsignedByte
    var G: UnsignedByte
    var B: UnsignedByte

    override fun getPixelFormat(): EPixelFormat = EPixelFormat.RGB

    override fun getPixel(): Pixel = RGB(R, G, B)

    override fun getComponent1(): UnsignedByte = R
    override fun getComponent2(): UnsignedByte = G
    override fun getComponent3(): UnsignedByte = B

    override fun setComponent1(value: UnsignedByte) { R = value }
    override fun setComponent2(value: UnsignedByte) { G = value }
    override fun setComponent3(value: UnsignedByte) { B = value }

    constructor() {
        this.R = UnsignedByte(0)
        this.G = UnsignedByte(0)
        this.B = UnsignedByte(0)
    }

    constructor(_R: UnsignedByte, _G: UnsignedByte, _B: UnsignedByte) {
        this.R = _R
        this.G = _G
        this.B = _B
    }

    override fun toString(): String {
        return "(${R.toString(16)}, ${G.toString(16)}, ${B.toString(16)})"
    }

    override fun toByteArray(): ByteArray {
        val byteArray: ByteArray = byteArrayOf(B, G, R)
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

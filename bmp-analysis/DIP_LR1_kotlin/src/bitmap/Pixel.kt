package bitmap

import type.UnsignedByte

abstract class Pixel {
    abstract fun getPixel(): Pixel
    abstract fun getComponent1(): UnsignedByte
    abstract fun getComponent2(): UnsignedByte
    abstract fun getComponent3(): UnsignedByte
    abstract fun toByteArray(): ByteArray
    abstract fun setComponent1(value: UnsignedByte)
    abstract fun setComponent2(value: UnsignedByte)
    abstract fun setComponent3(value: UnsignedByte)
    abstract fun getPixelFormat(): EPixelFormat
}

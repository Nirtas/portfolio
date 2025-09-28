package util

import bitmap.EPixelFormat
import bitmap.Pixel
import bitmap.RGB
import bitmap.YCbCr
import type.UnsignedByte

object PixelUtils {
    fun Pixel.asRGB(): RGB {
        return when (this.getPixelFormat()) {
            EPixelFormat.RGB -> this as RGB
            EPixelFormat.YCbCr -> (this as YCbCr).toRGB()
        }
    }

    fun Pixel.asYCbCr(): YCbCr {
        return when (this.getPixelFormat()) {
            EPixelFormat.RGB -> (this as RGB).toYCbCr()
            EPixelFormat.YCbCr -> this as YCbCr
        }
    }

    private fun RGB.toYCbCr(): YCbCr {
        val Y: Double = 0.299 * R.value + 0.587 * G.value + 0.114 * B.value
        val Cb: Double = 0.5643 * (B.value - Y) + 128
        val Cr: Double = 0.7132 * (R.value - Y) + 128
        return YCbCr(UnsignedByte(Y.toLong()), UnsignedByte(Cb.toLong()), UnsignedByte(Cr.toLong()))
    }

    private fun YCbCr.toRGB(): RGB {
        val R: Double = saturation(Y.value + 1.402 * (Cr.value - 128))
        val G: Double = saturation(Y.value - 0.714 * (Cr.value - 128) - 0.334 * (Cb.value - 128))
        val B: Double = saturation(Y.value + 1.772 * (Cb.value - 128))
        return RGB(UnsignedByte(R.toLong()), UnsignedByte(G.toLong()), UnsignedByte(B.toLong()))
    }

    private fun saturation(x: Double): Double {
        if (x < 0.0) return 0.0
        if (x > 255.0) return 255.0
        return x
    }
}
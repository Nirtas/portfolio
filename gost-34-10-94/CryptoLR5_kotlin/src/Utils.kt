object Utils {
    fun mod(number: Int, module: Int): Int {
        var result: Int = number
        if (module < 1) result += module
        while (result < 0) result += module
        while (result >= module) result -= module
        return result
    }

    fun rotateLeft32bit(number: Long, shift: Int): Long {
        if (number == 0L) return 0
        if (mod(shift, 32) == 0) return number
        return ((number shl shift) or (number shr (32 - shift)))
    }
}
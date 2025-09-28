object Utils {
    fun mod(number: Int, module: Int): Int {
        var result = number
        if (module < 1) return number
        while (result < 0) result += module
        while (result >= module) result -= module
        return result
    }

    fun mod(number: UInt, module: Int): UInt {
        var result = number
        if (module < 1) return number
        while (result < 0u) result += module.toUInt()
        while (result >= module.toUInt()) result -= module.toUInt()
        return result
    }

    // (GCD, x, y)
    fun extendedGcd(number: Int, module: Int): Triple<Int, Int, Int> {
        if (module == 0) return Triple(number, 1, 0)
        var oldX: Int = 1
        var x: Int = 0
        var oldY: Int = 0
        var y: Int = 1
        var oldR: Int = number
        var r: Int = module
        var q: Int = 0
        var temp: Int = 0

        while (r != 0) {
            q = oldR / r

            temp = r
            r = oldR - r * q
            oldR = temp

            temp = x
            x = oldX - x * q
            oldX = temp

            temp = y
            y = oldY - y * q
            oldY = temp
        }

        return Triple(oldR, mod(oldX, module), mod(oldY, module))
    }

    fun divideNumbers(divisibleNumber: Int, divisorNumber: Int, module: Int = 0): Int {
        if (divisorNumber == 0) throw Exception("Division by 0 is not allowed.")

        var result: Int = 0

        if (module != 0) {
            result = mod(divisibleNumber * extendedGcd(divisorNumber, module).second, module)
        } else {
            result = divisibleNumber / divisorNumber
        }

        return result
    }

    fun xor(firstNumber: UInt, secondNumber: UInt): UInt {
        val firstNumberString: String = firstNumber.toString(2).reversed()
        val secondNumberString: String = secondNumber.toString(2).reversed()
        var result: String = ""
        var i: Int = 0
        while (i < firstNumberString.count()) {
            if (i >= secondNumberString.count()) {
                result += firstNumberString[i]
                i++
                continue
            }
            val sum = firstNumberString[i].digitToInt() + secondNumberString[i].digitToInt()
            if (sum == 0 || sum == 2) {
                result += "0"
            } else {
                result += "1"
            }
            i++
        }
        while (i < secondNumberString.count()) {
            result += secondNumberString[i]
            i++
        }
        return result.reversed().toUInt(2)
    }

    fun xor(firstNumber: UByte, secondNumber: UByte): UByte {
        val firstNumberString: String = firstNumber.toString(2).reversed()
        val secondNumberString: String = secondNumber.toString(2).reversed()
        var result: String = ""
        var i: Int = 0
        while (i < firstNumberString.count()) {
            if (i >= secondNumberString.count()) {
                result += firstNumberString[i]
                i++
                continue
            }
            val sum = firstNumberString[i].digitToInt() + secondNumberString[i].digitToInt()
            if (sum == 0 || sum == 2) {
                result += "0"
            } else {
                result += "1"
            }
            i++
        }
        while (i < secondNumberString.count()) {
            result += secondNumberString[i]
            i++
        }
        return result.reversed().toUByte(2)
    }

    fun UInt.rotateRight4bit(number: Int): UInt {
        require(this in (0u..15u))
        if (this == 0u) return 0u
        val shift: Int = mod(number, 4)
        if (shift == 0) return this
        return ((this shr shift) or (this shl (4 - shift))) and 0x0Fu
    }
}
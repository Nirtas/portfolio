package type

//Всегда хранит 4 байта
class DWord : Type {
    override var value: Long
        private set

    constructor() {
        value = 0L
    }

    constructor(_value: Byte) {
        value = _value.toLong() and 0xFFFFFFFF
    }

    constructor(_value: Int) {
        value = _value.toLong() and 0xFFFFFFFF
    }

    constructor(_value: Long) {
        value = _value and 0xFFFFFFFF
    }

    fun toWord(): Array<Word> {
        val array: Array<Word> = arrayOf(Word(value shr 8), Word(value))
        return array
    }

    fun toUnsignedByte(): Array<UnsignedByte> {
        val array: Array<UnsignedByte> = arrayOf(
            UnsignedByte(value shr 24),
            UnsignedByte(value shr 16),
            UnsignedByte(value shr 8),
            UnsignedByte(value)
        )
        return array
    }

    fun reversed(): DWord {
        return DWord(
            ((value and 0xFF) shl 24) or (((value shr 8) and 0xFF) shl 16) or
                    (((value shr 16) and 0xFF) shl 8) or ((value shr 24) and 0xFF)
        )
    }

    operator fun plus(other: DWord): DWord {
        return DWord(value + other.value)
    }

    operator fun plus(other: Word): DWord {
        return DWord(value + other.value)
    }

    operator fun plus(other: UnsignedByte): DWord {
        return DWord(value + other.value)
    }

    operator fun plus(other: Byte): DWord {
        return DWord(value + other)
    }

    operator fun plus(other: Int): DWord {
        return DWord(value + other)
    }

    operator fun plus(other: Long): DWord {
        return DWord(value + other)
    }

    operator fun minus(other: DWord): DWord {
        return DWord(value - other.value)
    }

    operator fun minus(other: Word): DWord {
        return DWord(value - other.value)
    }

    operator fun minus(other: UnsignedByte): DWord {
        return DWord(value - other.value)
    }

    operator fun minus(other: Byte): DWord {
        return DWord(value - other)
    }

    operator fun minus(other: Int): DWord {
        return DWord(value - other)
    }

    operator fun minus(other: Long): DWord {
        return DWord(value - other)
    }

    operator fun times(other: DWord): DWord {
        return DWord(value * other.value)
    }

    operator fun times(other: Word): DWord {
        return DWord(value * other.value)
    }

    operator fun times(other: UnsignedByte): DWord {
        return DWord(value * other.value)
    }

    operator fun times(other: Byte): DWord {
        return DWord(value * other)
    }

    operator fun times(other: Int): DWord {
        return DWord(value * other)
    }

    operator fun times(other: Long): DWord {
        return DWord(value * other)
    }

    operator fun div(other: DWord): DWord {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(value / other.value)
    }

    operator fun div(other: Word): DWord {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(value / other.value)
    }

    operator fun div(other: UnsignedByte): DWord {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(value / other.value)
    }

    operator fun div(other: Byte): DWord {
        if (other.toInt() == 0) throw Exception("Division by 0 is not allowed")
        return DWord(value / other)
    }

    operator fun div(other: Int): DWord {
        if (other == 0) throw Exception("Division by 0 is not allowed")
        return DWord(value / other)
    }

    operator fun div(other: Long): DWord {
        if (other == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(value / other)
    }
}
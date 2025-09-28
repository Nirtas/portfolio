package type

//Всегда хранит 2 байта
class Word: Type {
    override var value: Long
        private set

    constructor() {
        value = 0
    }

    constructor(_value: Byte) {
        value = (_value.toInt() and 0xFFFF).toLong()
    }

    constructor(_value: Int) {
        value = (_value and 0xFFFF).toLong()
    }

    constructor(_value: Long) {
        value = _value and 0xFFFF
    }

    fun toDWord(): DWord {
        return DWord(value)
    }

    fun toUnsignedByte(): Array<UnsignedByte> {
        val array: Array<UnsignedByte> = arrayOf(UnsignedByte(value shr 8), UnsignedByte(value))
        return array
    }

    fun reversed(): Word {
        return Word((value shl 8) or (value shr 8))
    }

    operator fun plus(other: DWord): DWord {
        return DWord(value + other.value)
    }

    operator fun plus(other: Word): Word {
        return Word(value + other.value)
    }

    operator fun plus(other: UnsignedByte): Word {
        return Word(value + other.value)
    }

    operator fun plus(other: Byte): Word {
        return Word(value + other)
    }

    operator fun plus(other: Int): Word {
        return Word(value + other)
    }

    operator fun plus(other: Long): DWord {
        return DWord(value + other)
    }

    operator fun minus(other: DWord): DWord {
        return DWord(value - other.value)
    }

    operator fun minus(other: Word): Word {
        return Word(value - other.value)
    }

    operator fun minus(other: UnsignedByte): Word {
        return Word(value - other.value)
    }

    operator fun minus(other: Byte): Word {
        return Word(value - other)
    }

    operator fun minus(other: Int): Word {
        return Word(value - other)
    }

    operator fun minus(other: Long): DWord {
        return DWord(value - other)
    }

    operator fun times(other: DWord): DWord {
        return DWord(value * other.value)
    }

    operator fun times(other: Word): Word {
        return Word(value * other.value)
    }

    operator fun times(other: UnsignedByte): Word {
        return Word(value * other.value)
    }

    operator fun times(other: Byte): Word {
        return Word(value * other)
    }

    operator fun times(other: Int): Word {
        return Word(value * other)
    }

    operator fun times(other: Long): DWord {
        return DWord(value * other)
    }

    operator fun div(other: DWord): DWord {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(value / other.value)
    }

    operator fun div(other: Word): Word {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return Word(value / other.value)
    }

    operator fun div(other: UnsignedByte): Word {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return Word(value / other.value)
    }

    operator fun div(other: Byte): Word {
        if (other.toInt() == 0) throw Exception("Division by 0 is not allowed")
        return Word(value / other)
    }

    operator fun div(other: Int): Word {
        if (other == 0) throw Exception("Division by 0 is not allowed")
        return Word(value / other)
    }

    operator fun div(other: Long): DWord {
        if (other == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(value / other)
    }
}

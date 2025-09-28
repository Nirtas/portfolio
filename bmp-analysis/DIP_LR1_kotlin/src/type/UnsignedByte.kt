package type

//Всегда хранит 1 байт
class UnsignedByte: Type {
    override var value: Long
        private set

    constructor() {
        this.value = 0
    }

    constructor(_value: Byte) {
        this.value = (_value.toInt() and 0xFF).toLong()
    }

    constructor(_value: Int) {
        this.value = (_value and 0xFF).toLong()
    }

    constructor(_value: Long) {
        this.value = (_value.toInt() and 0xFF).toLong()
    }

    fun toWord(): Word {
        return Word(value)
    }

    fun toDWord(): DWord {
        return DWord(value)
    }

    operator fun plus(other: DWord): DWord {
        return DWord(this.value + other.value)
    }

    operator fun plus(other: Word): DWord {
        return DWord(this.value + other.value)
    }

    operator fun plus(other: UnsignedByte): UnsignedByte {
        return UnsignedByte(this.value + other.value)
    }

    operator fun plus(other: Byte): UnsignedByte {
        return UnsignedByte(this.value + other)
    }

    operator fun plus(other: Int): Word {
        return Word(this.value + other)
    }

    operator fun plus(other: Long): DWord {
        return DWord(this.value + other)
    }

    operator fun minus(other: DWord): DWord {
        return DWord(this.value - other.value)
    }

    operator fun minus(other: Word): DWord {
        return DWord(this.value - other.value)
    }

    operator fun minus(other: UnsignedByte): UnsignedByte {
        return UnsignedByte(this.value - other.value)
    }

    operator fun minus(other: Byte): UnsignedByte {
        return UnsignedByte(this.value - other)
    }

    operator fun minus(other: Int): Word {
        return Word(this.value - other)
    }

    operator fun minus(other: Long): DWord {
        return DWord(this.value - other)
    }

    operator fun times(other: DWord): DWord {
        return DWord(this.value * other.value)
    }

    operator fun times(other: Word): DWord {
        return DWord(this.value * other.value)
    }

    operator fun times(other: UnsignedByte): UnsignedByte {
        return UnsignedByte(this.value * other.value)
    }

    operator fun times(other: Byte): UnsignedByte {
        return UnsignedByte(this.value * other)
    }

    operator fun times(other: Int): Word {
        return Word(this.value * other)
    }

    operator fun times(other: Long): DWord {
        return DWord(this.value * other)
    }

    operator fun div(other: DWord): DWord {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(this.value / other.value)
    }

    operator fun div(other: Word): DWord {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(this.value / other.value)
    }

    operator fun div(other: UnsignedByte): UnsignedByte {
        if (other.value == 0L) throw Exception("Division by 0 is not allowed")
        return UnsignedByte(this.value / other.value)
    }

    operator fun div(other: Byte): UnsignedByte {
        if (other.toInt() == 0) throw Exception("Division by 0 is not allowed")
        return UnsignedByte(this.value / other)
    }

    operator fun div(other: Int): Word {
        if (other == 0) throw Exception("Division by 0 is not allowed")
        return Word(this.value / other)
    }

    operator fun div(other: Long): DWord {
        if (other == 0L) throw Exception("Division by 0 is not allowed")
        return DWord(this.value / other)
    }
}
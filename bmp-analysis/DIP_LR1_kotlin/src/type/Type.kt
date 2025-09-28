package type

abstract class Type {
    abstract val value: Long

    override fun equals(other: Any?): Boolean {
        if (other is Type) return this.value == other.value
        if (other is Byte) return this.value == other.toLong()
        if (other is Int) return this.value == other.toLong()
        if (other is Long) return this.value == other
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString().uppercase()
    }

    fun toString(radix: Int): String {
        return value.toString(radix).uppercase()
    }
}
class LinearCongruentialGenerator(var b: Long, var seed: Long, var c: Long, var m: Long) {

    fun getRandValue(): Long {
        val new: Long = (b * seed + c).mod(m)
        seed = new
        return new
    }

    fun getRandValues(n: Int): Array<Long> {
        return Array(n) { getRandValue() }
    }
}
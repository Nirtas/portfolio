import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.floor

fun procedureC(p: BigInteger, q: BigInteger): BigInteger {
    var f: BigInteger = BigInteger.ZERO
    var d: BigInteger = BigInteger.ONE
    do {
        f = calculateF(d, p, q)
        d++
    } while (f == BigInteger.ONE)
    return f
}

fun calculateF(d: BigInteger, p: BigInteger, q: BigInteger): BigInteger {
    if (q != BigInteger.ZERO) {
        return d.modPow((p - BigInteger.ONE) / q, p)
    } else {
        throw Exception("q must not be equal to 0")
    }
}

fun procedureB(b: Long, x0: Long, c: Long, modulePow: Int): Pair<BigInteger, BigInteger> {
    val (_, q, y0) = procedureA(b, x0, c, modulePow, true)
    val (Q, _, y1) = procedureA(b, y0, c, modulePow)

    val bitLength: Int = 1024
    val one: Long = 1
    val module: Long = one shl modulePow

    var p: BigInteger = BigInteger.ZERO

    val generatorLC: LinearCongruentialGenerator = LinearCongruentialGenerator(b, y1, c, module)

    var y: MutableList<Long> = mutableListOf(y1)
    val yCount: Int = if (modulePow == 16) 64 else 32

    step3@ while (true) {
        y = mutableListOf(y[0], *generatorLC.getRandValues(yCount))

        var Y: BigInteger = BigInteger.ZERO
        for (i in 0..<yCount) {
            Y += BigInteger.valueOf(y[i]).multiply(BigInteger.TWO.pow(modulePow * i))
        }

        y[0] = y[yCount]

        val firstPartOfN: BigDecimal =
            BigDecimal(BigInteger.TWO.pow(bitLength - 1))
                .divide(BigDecimal(q * Q), 0, RoundingMode.CEILING)

        val secondPartOfN: BigDecimal =
            BigDecimal(BigInteger.TWO.pow(bitLength - 1).multiply(Y))
                .divide(BigDecimal(q * Q * BigInteger.TWO.pow(bitLength)), 0, RoundingMode.FLOOR)

        var N: BigInteger = (firstPartOfN + secondPartOfN).toBigInteger()

        if (N.mod(BigInteger.TWO) == BigInteger.ONE) {
            N += BigInteger.ONE
        }

        var k: BigInteger = BigInteger.ZERO

        step8@ while (true) {

            p = q * Q * (N + k) + BigInteger.ONE

            if (p > BigInteger.TWO.pow(bitLength)) {
                continue@step3
            }

            if (BigInteger.TWO.modPow(q * Q * (N + k), p) == BigInteger.ONE &&
                BigInteger.TWO.modPow(q * (N + k), p) != BigInteger.ONE
            ) {
                break@step3
            } else {
                k += BigInteger.TWO
            }
        }
    }

    return Pair(p, q)
}

fun procedureA(
    b: Long,
    x0: Long,
    c: Long,
    modulePow: Int,
    flagReturnFirstY: Boolean = false
): Triple<BigInteger, BigInteger, Long> {
    val bitLength: Int = 512
    val one: Long = 1
    val module: Long = one shl modulePow

    val t: MutableList<Int> = mutableListOf()
    t.add(bitLength)

    var index: Int = 0

    while (t[index] > modulePow) {
        t.add(floor(t[index] / 2.0).toInt())
        index++
    }

    val p: Array<BigInteger> = Array(t.size) { BigInteger.ZERO }
    p[index] = findSmallestPrimeNumber(t[index])

    var m: Int = index - 1

    var rm: Int = 0

    var y: MutableList<Long> = mutableListOf(x0)
    val generatorLC: LinearCongruentialGenerator = LinearCongruentialGenerator(b, y[0], c, module)
    var y0: Long = 0

    do {
        rm = ceil(t[m].toDouble() / modulePow).toInt()
        step6@ while (true) {
            y = mutableListOf(y[0], *generatorLC.getRandValues(rm))

            var Ym: BigInteger = BigInteger.ZERO
            for (i in 0..<rm) {
                Ym += BigInteger.valueOf(y[i]).multiply(BigInteger.TWO.pow(modulePow * i))
            }

            y0 = y[0]
            y[0] = y[rm]

            val firstPartOfN: BigDecimal =
                BigDecimal(BigInteger.TWO.pow(t[m] - 1))
                    .divide(BigDecimal(p[m + 1]), 0, RoundingMode.CEILING)

            val secondPartOfN: BigDecimal =
                BigDecimal(BigInteger.TWO.pow(t[m] - 1).multiply(Ym))
                    .divide(BigDecimal(p[m + 1].multiply(BigInteger.TWO.pow(modulePow * rm))), 0, RoundingMode.FLOOR)

            var N: BigInteger = (firstPartOfN + secondPartOfN).toBigInteger()

            if (N.mod(BigInteger.TWO) == BigInteger.ONE) {
                N += BigInteger.ONE
            }

            var k: BigInteger = BigInteger.ZERO

            step11@ while (true) {

                p[m] = p[m + 1] * (N + k) + BigInteger.ONE

                if (p[m] > BigInteger.TWO.pow(t[m])) {
                    continue@step6
                }

                if (BigInteger.TWO.modPow(p[m + 1].multiply(N.add(k)), p[m]) == BigInteger.ONE &&
                    BigInteger.TWO.modPow(N.add(k), p[m]) != BigInteger.ONE
                ) {
                    m--
                    break@step6
                } else {
                    k += BigInteger.TWO
                }
            }
        }
    } while (m >= 0)

    return if (flagReturnFirstY) {
        Triple(p[0], p[1], y0)
    } else {
        Triple(p[0], p[1], y[0])
    }
}

fun findSmallestPrimeNumber(bitLength: Int): BigInteger {
    var string: String = "1"
    for (i in 1..<bitLength) {
        string += "0"
    }
    return BigInteger(string, 2).nextProbablePrime()
}
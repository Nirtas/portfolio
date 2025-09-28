/*
    Вариант 19. Реализовать алгоритм электронной цифровой подписи ГОСТ Р 34.10-94.
    При постановке подписи использовать самостоятельно реализованную хеш-функцию MD4.
    Алгоритмически важные элементы, такие как тесты простоты, расширенный алгоритм Евклида, быстрое возведение в
    степень, нахождение первообразного корня, нахождение мультипликативного обратного по модулю и т.д должны быть
    реализованы самостоятельно.
    В системе должна быть реализована возможность выбора длины ключа шифрования.
    Ключи в системе должны генерироваться автоматически: случайным образом или по паролю.
    Отчет должен содержать описание системы с указанием особенностей реализации, примеры работы программы.
    В работе можно использовать готовые реализации библиотеки больших чисел (числа больше, чем long int).
*/

import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextInt

var p: BigInteger = BigInteger.ZERO
var q: BigInteger = BigInteger.ZERO
var a: BigInteger = BigInteger.ZERO
var y: BigInteger = BigInteger.ZERO

fun main() {
    generateKeys(512, 32)
    println("p: ${p.toString(16)}")
    println("q: ${q.toString(16)}")
    println("a: ${a.toString(16)}")

    println()

    val x: BigInteger = BigInteger("123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0", 16)
    println("x: ${x.toString(16)}")

    y = calculatePublicKey(x)
    println("y: ${y.toString(16)}")

    println()

    val message: String = "Hello!"
    println("message: $message")

    val hash: BigInteger = BigInteger(MD4.hash(message).joinToString("") { it.toString(16) }, 16)
    println("hash: ${hash.toString(16)}")

    println()

    val sign: Array<BigInteger> = sign(x, hash)
    //sign[0] = BigInteger("1", 16)
    //sign[1] = BigInteger("1", 16)
    println("sign: " + sign.map { it.toString(16) })

    println()

    println(verify(message, sign))
}

fun generateKeys(pBitLength: Int, modulePow: Int) {
    println("KEYS GENERATING")

    var x0: Long = 0
    var c: Long = 0

    if (modulePow == 16) {
        val pow2_16: Long = 65536
        x0 = (1L..<pow2_16).random()
        c = (1L..<pow2_16).random()
        while (c % 2 == 0L) c = (1L..<pow2_16).random()
        if (pBitLength == 512) {
            val (p1, q1, _) = procedureA(b = 19381, x0 = x0, c = c, modulePow = 16)
            p = p1
            q = q1
        } else if (pBitLength == 1024) {
            val (p1, q1) = procedureB(b = 19381, x0 = x0, c = c, modulePow = 16)
            p = p1
            q = q1
        }
    } else if (modulePow == 32) {
        val pow2_32: Long = 4294967296
        x0 = (1L..<pow2_32).random()
        c = (1L..<pow2_32).random()
        while (c % 2 == 0L) c = (1L..<pow2_32).random()
        if (pBitLength == 512) {
            val (p1, q1, _) = procedureA(b = 97781173, x0 = x0, c = c, modulePow = 32)
            p = p1
            q = q1
        } else if (pBitLength == 1024) {
            val (p1, q1) = procedureB(b = 97781173, x0 = x0, c = c, modulePow = 32)
            p = p1
            q = q1
        }
    }

    println("x0: ${x0.toString(16)}")
    println("c: ${c.toString(16)}")

    a = procedureC(p, q)
}

fun sign(x: BigInteger, hash: BigInteger): Array<BigInteger> {
    println("SIGNING")
    var h: BigInteger = hash
    if (h.mod(q) == BigInteger.ZERO) {
        h = BigInteger.ONE
    }

    var k: BigInteger = BigInteger.ZERO
    var r1: BigInteger = BigInteger.ZERO
    var r2: BigInteger = BigInteger.ZERO
    var s: BigInteger = BigInteger.ZERO

    do {
        do {
            k = randBigInteger(q)
            r1 = a.modPow(k, p)
            r2 = r1.mod(q)
            println("k: ${k.toString(16)}")
        } while (r2 == BigInteger.ZERO)
        s = (x * r2 + k * h).mod(q)
    } while (s == BigInteger.ZERO)

    return arrayOf(r2, s)
}

fun randBigInteger(maxValue: BigInteger): BigInteger {
    val bitLength: Int = Random.nextInt(1..maxValue.bitLength())
    var string: String = ""
    var bigInteger: BigInteger = BigInteger.ZERO
    do {
        string = ""
        for (i in 0..<bitLength) {
            string += (0..1).random()
        }
        bigInteger = BigInteger(string, 2)
    } while (bigInteger >= q)
    return bigInteger
}

fun calculatePublicKey(x: BigInteger): BigInteger {
    return a.modPow(x, p)
}

fun verify(message: String, sign: Array<BigInteger>): Boolean {
    println("VERIFICATION")
    if (sign[0] !in BigInteger.ONE..<q || sign[1] !in BigInteger.ONE..<q) {
        println("Invalid size")
        return false
    }

    var hash: BigInteger = BigInteger(MD4.hash(message).joinToString("") { it.toString(16) }, 16)
    if (hash.mod(q) == BigInteger.ZERO) {
        hash = BigInteger.ONE
    }

    val v: BigInteger = hash.modPow(q - BigInteger.TWO, q)
    val z1: BigInteger = (sign[1] * v).mod(q)
    val z2: BigInteger = ((q - sign[0]) * v).mod(q)
    val u: BigInteger = (a.modPow(z1, p) * y.modPow(z2, p)).mod(p).mod(q)

    println("v: ${v.toString(16)}")
    println("z1: ${z1.toString(16)}")
    println("z2: ${z2.toString(16)}")
    println("u: ${u.toString(16)}")

    return sign[0] == u
}

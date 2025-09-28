import Utils.divideNumbers
import Utils.mod

class Polynomial(polynomial_param: List<PolynomialMember>) {
    var polynomial: MutableList<PolynomialMember> =
        polynomial_param.distinctBy { it.degree }.filter { it.coefficient != 0 }.toMutableList()

    fun findPolynomialMember(degree: Int): Pair<PolynomialMember, Int>? {
        val foundPolynomialMember: PolynomialMember = polynomial.find { it.degree == degree } ?: return null
        val index: Int = polynomial.indexOf(foundPolynomialMember)
        return Pair(foundPolynomialMember, index)
    }

    fun removePolynomialMember(degree: Int): Boolean = polynomial.removeIf { it.degree == degree }
    fun addPolynomialMember(polynomialMember: PolynomialMember): Boolean = polynomial.add(polynomialMember)
    override fun toString(): String {
        if (polynomial.isEmpty()) {
            return "The polynomial does not exist"
        }
        polynomial.sortByDescending { it.degree }
        var string: String = ""
        for (i in polynomial.indices) {
            if (polynomial[i].coefficient > 0 && i != 0) string += "+"
            string += polynomial[i].toString()
        }
        return string
    }

    fun convertModulo(module: Int) {
        if (module != 0) {
            val temp: Polynomial = Polynomial(this.polynomial)
            temp.polynomial.forEach { polynomialMember ->
                polynomialMember.convertCoefficientModulo(module)
            }
            polynomial = temp.polynomial.filter { it.coefficient != 0 }.toMutableList()
        }
    }

    operator fun minus(secondPolynomial: Polynomial): Polynomial {
        val result: Polynomial = Polynomial(emptyList())
        val tempPolynomial: Polynomial = Polynomial(secondPolynomial.polynomial)
        polynomial.forEach { polynomialMember ->
            val tempPolynomialMember: PolynomialMember? =
                tempPolynomial.findPolynomialMember(polynomialMember.degree)?.first
            if (tempPolynomialMember != null) {
                if ((polynomialMember - tempPolynomialMember).coefficient != 0) {
                    result.addPolynomialMember(polynomialMember - tempPolynomialMember)
                } else {
                    tempPolynomial.removePolynomialMember(tempPolynomialMember.degree)
                }
            } else {
                result.addPolynomialMember(polynomialMember)
            }
        }
        tempPolynomial.polynomial.forEach { tempPolynomialMember ->
            if (result.findPolynomialMember(tempPolynomialMember.degree) == null) {
                result.addPolynomialMember(
                    PolynomialMember(
                        -tempPolynomialMember.coefficient,
                        tempPolynomialMember.degree
                    )
                )
            }
        }
        return result
    }

    operator fun times(secondPolynomial: Polynomial): Polynomial {
        val result: Polynomial = Polynomial(emptyList())
        secondPolynomial.polynomial.forEach { secondPolynomialMember ->
            polynomial.forEach secondForEach@{ polynomialMember ->
                val newPolynomialMember: PolynomialMember = secondPolynomialMember * polynomialMember
                if (newPolynomialMember.coefficient == 0) {
                    return@secondForEach
                }
                val foundPolynomialMember: Pair<PolynomialMember, Int>? =
                    result.findPolynomialMember(newPolynomialMember.degree)
                if (foundPolynomialMember != null) {
                    result.polynomial[foundPolynomialMember.second] += newPolynomialMember
                } else {
                    result.addPolynomialMember(newPolynomialMember)
                }
            }
        }
        return result
    }

    operator fun times(multiplierPolynomialMember: PolynomialMember): Polynomial {
        val result: Polynomial = Polynomial(emptyList())
        polynomial.forEach { polynomialMember ->
            val newPolynomialMember: PolynomialMember = polynomialMember * multiplierPolynomialMember
            if (newPolynomialMember.coefficient == 0) {
                return@forEach
            }
            val foundPolynomialMember: Pair<PolynomialMember, Int>? =
                result.findPolynomialMember(newPolynomialMember.degree)
            if (foundPolynomialMember != null) {
                result.polynomial[foundPolynomialMember.second] += newPolynomialMember
            } else {
                result.addPolynomialMember(newPolynomialMember)
            }
        }
        return result
    }
}

data class PolynomialMember(var coefficient: Int, val degree: Int) {
    fun convertCoefficientModulo(module: Int) {
        if (module != 0) {
            coefficient = mod(coefficient, module)
        }
    }

    override fun toString(): String {
        val displayX: Boolean = degree > 0
        val displayCoefficient: Boolean = coefficient !in arrayOf(-1, 1)
        val displayDegree: Boolean = degree > 1
        var string: String = ""

        if (displayX) {
            if (displayCoefficient) {
                if (displayDegree) {
                    string += "${coefficient}x^${degree}"
                } else {
                    string += "${coefficient}x"
                }
            } else {
                if (displayDegree) {
                    if (coefficient > 0) {
                        string += "x^$degree"
                    } else {
                        string += "-x^$degree"
                    }
                } else {
                    if (coefficient > 0) {
                        string += "x"
                    } else {
                        string += "-x"
                    }
                }
            }
        } else {
            string += coefficient
        }

        return string
    }

    operator fun times(polynomialMember: PolynomialMember): PolynomialMember {
        return PolynomialMember(
            coefficient * polynomialMember.coefficient,
            degree + polynomialMember.degree
        )
    }

    operator fun minus(polynomialMember: PolynomialMember): PolynomialMember {
        if (degree == polynomialMember.degree) {
            return PolynomialMember(coefficient - polynomialMember.coefficient, degree)
        } else {
            throw Exception("The degrees don't match.")
        }
    }

    operator fun plus(polynomialMember: PolynomialMember): PolynomialMember {
        if (degree == polynomialMember.degree) {
            return PolynomialMember(coefficient + polynomialMember.coefficient, degree)
        } else {
            throw Exception("The degrees don't match.")
        }
    }
}

fun dividePolynomials(
    divisiblePolynomial: Polynomial,
    divisorPolynomial: Polynomial,
    module: Int = 0
): Pair<Polynomial, Polynomial> {
    if (divisiblePolynomial.polynomial.isEmpty()) {
        throw Exception("The first polynomial (p1(x)) does not exist.")
    }
    if (divisorPolynomial.polynomial.isEmpty()) {
        throw Exception("The second polynomial (p2(x)) does not exist.")
    }

    val quotient: Polynomial = Polynomial(emptyList())
    var remainder: Polynomial = Polynomial(divisiblePolynomial.polynomial)
    remainder.convertModulo(module)
    val tempDivisorPolynomial: Polynomial = Polynomial(divisorPolynomial.polynomial)
    tempDivisorPolynomial.convertModulo(module)
    var temp: Polynomial = Polynomial(emptyList())
    var newQuotientPolynomialMember: PolynomialMember = PolynomialMember(0, 0)
    var maxRemainderPolynomialMember: PolynomialMember? = remainder.polynomial.maxByOrNull { it.degree }
    var maxDivisorPolynomialMember: PolynomialMember? = tempDivisorPolynomial.polynomial.maxByOrNull { it.degree }

    while (
        (maxRemainderPolynomialMember != null && maxDivisorPolynomialMember != null) &&
        (maxRemainderPolynomialMember.degree >= maxDivisorPolynomialMember.degree)
    ) {
        newQuotientPolynomialMember = PolynomialMember(
            divideNumbers(maxRemainderPolynomialMember.coefficient, maxDivisorPolynomialMember.coefficient, module),
            maxRemainderPolynomialMember.degree - maxDivisorPolynomialMember.degree
        )
        temp = tempDivisorPolynomial * newQuotientPolynomialMember
        temp.convertModulo(module)
        remainder -= temp
        remainder.convertModulo(module)
        quotient.addPolynomialMember(newQuotientPolynomialMember)
        maxRemainderPolynomialMember = remainder.polynomial.maxByOrNull { it.degree }
        maxDivisorPolynomialMember = tempDivisorPolynomial.polynomial.maxByOrNull { it.degree }
    }

    return Pair(quotient, remainder)
}

fun Int.toPolynomial(): Polynomial {
    val reversedBinaryString: String = this.toString(2).reversed()
    val list: MutableList<PolynomialMember> = mutableListOf()
    for (i in reversedBinaryString.indices) {
        list.add(PolynomialMember(reversedBinaryString[i].digitToInt(), i))
    }
    return Polynomial(list)
}

fun Polynomial.toInt(): Int {
    val tempPolynomial: Polynomial = Polynomial(this.polynomial)
    tempPolynomial.convertModulo(2)
    var number: Int = 0
    tempPolynomial.polynomial.forEach {
        number += 1 shl it.degree
    }
    return number
}
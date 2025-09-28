import Utils.mod
import Utils.rotateRight4bit
import Utils.xor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Twofish {
    val RS: Array<Array<UByte>> = arrayOf(
        arrayOf(0x01u, 0xA4u, 0x55u, 0x87u, 0x5Au, 0x58u, 0xDBu, 0x9Eu),
        arrayOf(0xA4u, 0x56u, 0x82u, 0xF3u, 0x1Eu, 0xC6u, 0x68u, 0xE5u),
        arrayOf(0x02u, 0xA1u, 0xFCu, 0xC1u, 0x47u, 0xAEu, 0x3Du, 0x19u),
        arrayOf(0xA4u, 0x55u, 0x87u, 0x5Au, 0x58u, 0xDBu, 0x9Eu, 0x03u)
    )

    val MDS: Array<Array<UByte>> = arrayOf(
        arrayOf(0x01u, 0xEFu, 0x5Bu, 0x5Bu),
        arrayOf(0x5Bu, 0xEFu, 0xEFu, 0x01u),
        arrayOf(0xEFu, 0x5Bu, 0x01u, 0xEFu),
        arrayOf(0xEFu, 0x01u, 0xEFu, 0x5Bu)
    )

    val q0_permutation_table: Array<Array<UByte>> = arrayOf(
        arrayOf(
            0x08u,
            0x01u,
            0x07u,
            0x0Du,
            0x06u,
            0x0Fu,
            0x03u,
            0x02u,
            0x00u,
            0x0Bu,
            0x05u,
            0x09u,
            0x0Eu,
            0x0Cu,
            0x0Au,
            0x04u
        ),
        arrayOf(
            0x0Eu,
            0x0Cu,
            0x0Bu,
            0x08u,
            0x01u,
            0x02u,
            0x03u,
            0x05u,
            0x0Fu,
            0x04u,
            0x0Au,
            0x06u,
            0x07u,
            0x00u,
            0x09u,
            0x0Du
        ),
        arrayOf(
            0x0Bu,
            0x0Au,
            0x05u,
            0x0Eu,
            0x06u,
            0x0Du,
            0x09u,
            0x00u,
            0x0Cu,
            0x08u,
            0x0Fu,
            0x03u,
            0x02u,
            0x04u,
            0x07u,
            0x01u
        ),
        arrayOf(
            0x0Du,
            0x07u,
            0x0Fu,
            0x04u,
            0x01u,
            0x02u,
            0x06u,
            0x0Eu,
            0x09u,
            0x0Bu,
            0x03u,
            0x00u,
            0x08u,
            0x05u,
            0x0Cu,
            0x0Au
        )
    )

    val q1_permutation_table: Array<Array<UByte>> = arrayOf(
        arrayOf(
            0x02u,
            0x08u,
            0x0Bu,
            0x0Du,
            0x0Fu,
            0x07u,
            0x06u,
            0x0Eu,
            0x03u,
            0x01u,
            0x09u,
            0x04u,
            0x00u,
            0x0Au,
            0x0Cu,
            0x05u
        ),
        arrayOf(
            0x01u,
            0x0Eu,
            0x02u,
            0x0Bu,
            0x04u,
            0x0Cu,
            0x03u,
            0x07u,
            0x06u,
            0x0Du,
            0x0Au,
            0x05u,
            0x0Fu,
            0x09u,
            0x00u,
            0x08u
        ),
        arrayOf(
            0x04u,
            0x0Cu,
            0x07u,
            0x05u,
            0x01u,
            0x06u,
            0x09u,
            0x0Au,
            0x00u,
            0x0Eu,
            0x0Du,
            0x08u,
            0x02u,
            0x0Bu,
            0x03u,
            0x0Fu
        ),
        arrayOf(
            0x0Bu,
            0x09u,
            0x05u,
            0x01u,
            0x0Cu,
            0x03u,
            0x0Du,
            0x0Eu,
            0x06u,
            0x04u,
            0x07u,
            0x0Fu,
            0x02u,
            0x00u,
            0x08u,
            0x0Au
        )
    )

    // x^8 + x^6 + x^3 + x^2 + 1
    const val W_PRIMITIVE_POLYNOMIAL: Int = 0x14D

    val multiplicativeGroupByW: Array<UByte> = createMultiplicativeGroup(W_PRIMITIVE_POLYNOMIAL.toPolynomial(), 256)

    // x^8 + x^6 + x^5 + x^3 + 1
    const val M_PRIMITIVE_POLYNOMIAL: Int = 0x169

    val multiplicativeGroupByM: Array<UByte> = createMultiplicativeGroup(M_PRIMITIVE_POLYNOMIAL.toPolynomial(), 256)

    var S: Array<UInt> = emptyArray()

    var K: Array<UInt> = emptyArray()

    fun createMultiplicativeGroup(genericPolynomial: Polynomial, module: Int): Array<UByte> {
        val table: Array<UByte> = Array(module) { 0u }
        table[0] = 1u
        for (i in 1..<module) {
            table[i] = dividePolynomials(
                (table[i - 1] * 2u).toInt().toPolynomial(),
                genericPolynomial,
                2
            ).second.toInt().toUByte()
        }
        return table
    }

    enum class ChainingMode {
        ECB, CBC, CFB, OFB
    }

    fun encryptECB(originalMessageBlocks: Array<Array<UByte>>): Array<Array<UByte>> {
        val encryptedBlocks: Array<Array<UByte>> = Array(originalMessageBlocks.count()) { Array(16) { 0u } }
        for (i in originalMessageBlocks.indices) {
            encryptedBlocks[i] = encryptBlock(originalMessageBlocks[i])
        }
        return encryptedBlocks
    }

    fun encryptCBC(originalMessageBlocks: Array<Array<UByte>>, initialVector: Array<UByte>): Array<Array<UByte>> {
        var vector: Array<UByte> = initialVector
        val encryptedBlocks: Array<Array<UByte>> = Array(originalMessageBlocks.count()) { Array(16) { 0u } }
        for (i in originalMessageBlocks.indices) {
            encryptedBlocks[i] = originalMessageBlocks[i]
            for (j in encryptedBlocks[i].indices) {
                encryptedBlocks[i][j] = xor(encryptedBlocks[i][j], vector[j])
            }
            encryptedBlocks[i] = encryptBlock(originalMessageBlocks[i])
            vector = encryptedBlocks[i].copyOf()
        }
        return encryptedBlocks
    }

    fun encryptCFB(originalMessageBlocks: Array<Array<UByte>>, initialVector: Array<UByte>): Array<Array<UByte>> {
        var vector: Array<UByte> = initialVector
        val encryptedBlocks: Array<Array<UByte>> = Array(originalMessageBlocks.count()) { Array(16) { 0u } }
        for (i in originalMessageBlocks.indices) {
            encryptedBlocks[i] = encryptBlock(vector)
            for (j in encryptedBlocks[i].indices) {
                encryptedBlocks[i][j] = xor(encryptedBlocks[i][j], originalMessageBlocks[i][j])
            }
            vector = encryptedBlocks[i].copyOf()
        }
        return encryptedBlocks
    }

    fun encryptOFB(originalMessageBlocks: Array<Array<UByte>>, initialVector: Array<UByte>): Array<Array<UByte>> {
        var vector: Array<UByte> = initialVector
        val encryptedBlocks: Array<Array<UByte>> = Array(originalMessageBlocks.count()) { Array(16) { 0u } }
        for (i in originalMessageBlocks.indices) {
            encryptedBlocks[i] = encryptBlock(vector)
            vector = encryptedBlocks[i].copyOf()
            for (j in encryptedBlocks[i].indices) {
                encryptedBlocks[i][j] = xor(encryptedBlocks[i][j], originalMessageBlocks[i][j])
            }
        }
        return encryptedBlocks
    }

    fun decryptECB(encryptedBlocks: Array<Array<UByte>>): Array<Array<UByte>> {
        val decryptedBlocks: Array<Array<UByte>> = Array(encryptedBlocks.count()) { Array(16) { 0u } }
        for (i in encryptedBlocks.indices) {
            decryptedBlocks[i] = decryptBlock(encryptedBlocks[i])
        }
        return decryptedBlocks
    }

    fun decryptCBC(encryptedBlocks: Array<Array<UByte>>, initialVector: Array<UByte>): Array<Array<UByte>> {
        var vector: Array<UByte> = initialVector
        val decryptedBlocks: Array<Array<UByte>> = Array(encryptedBlocks.count()) { Array(16) { 0u } }
        for (i in encryptedBlocks.indices) {
            decryptedBlocks[i] = decryptBlock(encryptedBlocks[i])
            for (j in decryptedBlocks[i].indices) {
                decryptedBlocks[i][j] = xor(decryptedBlocks[i][j], vector[j])
            }
            vector = encryptedBlocks[i].copyOf()
        }
        return decryptedBlocks
    }

    fun decryptCFB(encryptedBlocks: Array<Array<UByte>>, initialVector: Array<UByte>): Array<Array<UByte>> {
        var vector: Array<UByte> = initialVector
        val decryptedBlocks: Array<Array<UByte>> = Array(encryptedBlocks.count()) { Array(16) { 0u } }
        for (i in encryptedBlocks.indices) {
            decryptedBlocks[i] = encryptBlock(vector)
            for (j in decryptedBlocks[i].indices) {
                decryptedBlocks[i][j] = xor(decryptedBlocks[i][j], encryptedBlocks[i][j])
            }
            vector = encryptedBlocks[i].copyOf()
        }
        return decryptedBlocks
    }

    fun decryptOFB(encryptedBlocks: Array<Array<UByte>>, initialVector: Array<UByte>): Array<Array<UByte>> {
        var vector: Array<UByte> = initialVector
        val decryptedBlocks: Array<Array<UByte>> = Array(encryptedBlocks.count()) { Array(16) { 0u } }
        for (i in encryptedBlocks.indices) {
            decryptedBlocks[i] = encryptBlock(vector)
            vector = decryptedBlocks[i].copyOf()
            for (j in decryptedBlocks[i].indices) {
                decryptedBlocks[i][j] = xor(decryptedBlocks[i][j], encryptedBlocks[i][j])
            }
        }
        return decryptedBlocks
    }

    fun divideArrayIntoBlocks(array: Array<UByte>, blockLength: Int): Array<Array<UByte>> {
        require(array.count() % blockLength == 0)
        val blocks: Array<Array<UByte>> = Array(array.count() / blockLength) { Array(blockLength) { 0u } }
        runBlocking {
            for (i in blocks.indices) {
                launch {
                    for (j in 0..<blockLength) {
                        launch {
                            blocks[i][j] = array[blockLength * i + j]
                        }
                    }
                }
            }
        }
        return blocks
    }

    fun generateSubKeys(userKey: Array<UByte>): Array<UInt> {
        val k: Int = userKey.count() / 8

        val M: Array<UInt> = Array(2 * k) { 0u }
        for (i in 0..<2 * k) {
            for (j in 3 downTo 0) {
                M[i] = (M[i] shl 8) + userKey[4 * i + j]
            }
        }

        val Me: Array<UInt> = Array(k) { 0u }
        val Mo: Array<UInt> = Array(k) { 0u }

        var l: Int = 0
        var n: Int = 0
        for (i in M.indices) {
            if (i % 2 == 0) {
                Me[l++] = M[i]
            } else {
                Mo[n++] = M[i]
            }
        }

        val m2: Array<Array<UByte>> = Array(k) { Array(8) { 0u } }
        for (i in 0..<k) {
            for (j in 0..<8) {
                m2[i][j] = userKey[8 * i + j]
            }
        }
        val s: Array<Array<UByte>> = Array(k) { Array(4) { 0u } }
        for (i in m2.indices) {
            s[i] = multiplyMatrixByVector(RS, m2[i], multiplicativeGroupByW)
        }
        val Si: Array<UInt> = Array(k) { 0u }
        for (i in 0..<k) {
            for (j in 3 downTo 0) {
                Si[i] = (Si[i] shl 8) + s[i][j]
            }
        }
        S = Si.reversedArray()

        val A: Array<UInt> = Array(20) { 0u }
        val B: Array<UInt> = Array(20) { 0u }
        val K2: Array<UInt> = Array(40) { 0u }
        for (i in 0..19) {
            A[i] = h(
                (((2 * i) shl 24) + ((2 * i) shl 16) + ((2 * i) shl 8) + 2 * i).toUInt(),
                Me
            )
            B[i] = h(
                (((2 * i + 1) shl 24) + ((2 * i + 1) shl 16) + ((2 * i + 1) shl 8) + (2 * i + 1)).toUInt(),
                Mo
            ).rotateLeft(8)
            K2[2 * i] = ((A[i].toULong() + B[i].toULong()) and 0xFFFFFFFFu).toUInt()
            K2[2 * i + 1] = ((A[i].toULong() + 2u * B[i].toULong()) and 0xFFFFFFFFu).toUInt().rotateLeft(9)
        }
        return K2
    }

    fun multiplyMatrixByVector(
        leftMatrix: Array<Array<UByte>>,
        vector: Array<UByte>,
        multiplicativeGroup: Array<UByte>
    ): Array<UByte> {
        if (leftMatrix[0].count() != vector.count()) {
            throw Exception("The number of rows of the left matrix must be equal to the number of columns of the vector.")
        }

        val resultMatrix: Array<UByte> = Array(leftMatrix.count()) { 0u }

        for (i in leftMatrix.indices) {
            for (j in vector.indices) {
                var sum: UByte = 0u
                for (k in leftMatrix[i].indices) {
                    val firstNumberIndex: Int = multiplicativeGroup.indexOf(leftMatrix[i][k])
                    val secondNumberIndex: Int = multiplicativeGroup.indexOf(vector[k])
                    if (firstNumberIndex != -1 && secondNumberIndex != -1) {
                        sum = xor(
                            sum,
                            multiplicativeGroup[mod(
                                firstNumberIndex + secondNumberIndex,
                                multiplicativeGroup.count() - 1
                            )]
                        )
                    }
                }
                resultMatrix[i] = sum
            }
        }

        return resultMatrix
    }

    fun h(X: UInt, L: Array<UInt>): UInt {
        val l: Array<UInt> = Array(L.count()) { 0u }
        var x: UInt = 0u
        for (j in 0..3) {
            for (i in 0..<L.count()) {
                l[i] = (l[i] shl 8) + ((L[i] shr (8 * j)) and 0xFFu)
            }
            x = (x shl 8) + ((X shr (8 * j)) and 0xFFu)
        }
        val y: Array<UInt> = Array(L.count() + 1) { 0u }
        val y2: Array<UByte> = Array(4) { 0u }
        for (j in 3 downTo 0) {
            y[L.count()] = (y[L.count()] shl 8) + ((x shr (8 * j)) and 0xFFu)
        }
        if (L.count() == 4) {
            y[3] = xor(qPermutation((y[4] shr 24) and 0xFFu, 1), (l[3] shr 24) and 0xFFu)
            y[3] = (y[3] shl 8) + xor(qPermutation((y[4] shr 16) and 0xFFu, 0), (l[3] shr 16) and 0xFFu)
            y[3] = (y[3] shl 8) + xor(qPermutation((y[4] shr 8) and 0xFFu, 0), (l[3] shr 8) and 0xFFu)
            y[3] = (y[3] shl 8) + xor(qPermutation(y[4] and 0xFFu, 1), l[3] and 0xFFu)
        }
        if (L.count() >= 3) {
            y[2] = xor(qPermutation((y[3] shr 24) and 0xFFu, 1), (l[2] shr 24) and 0xFFu)
            y[2] = (y[2] shl 8) + xor(qPermutation((y[3] shr 16) and 0xFFu, 1), (l[2] shr 16) and 0xFFu)
            y[2] = (y[2] shl 8) + xor(qPermutation((y[3] shr 8) and 0xFFu, 0), (l[2] shr 8) and 0xFFu)
            y[2] = (y[2] shl 8) + xor(qPermutation(y[3] and 0xFFu, 0), l[2] and 0xFFu)
        }
        y2[0] = qPermutation(
            xor(
                qPermutation(xor(qPermutation((y[2] shr 24) and 0xFFu, 0), (l[1] shr 24) and 0xFFu), 0),
                (l[0] shr 24) and 0xFFu
            ), 1
        ).toUByte()
        y2[1] = qPermutation(
            xor(
                qPermutation(
                    xor(
                        qPermutation((y[2] shr 16) and 0xFFu, 1),
                        (l[1] shr 16) and 0xFFu
                    ), 0
                ), (l[0] shr 16) and 0xFFu
            ), 0
        ).toUByte()
        y2[2] = qPermutation(
            xor(
                qPermutation(
                    xor(qPermutation((y[2] shr 8) and 0xFFu, 0), (l[1] shr 8) and 0xFFu),
                    1
                ), (l[0] shr 8) and 0xFFu
            ), 1
        ).toUByte()
        y2[3] = qPermutation(
            xor(
                qPermutation(xor(qPermutation(y[2] and 0xFFu, 1), l[1] and 0xFFu), 1),
                l[0] and 0xFFu
            ), 0
        ).toUByte()
        val z: Array<UByte> = multiplyMatrixByVector(MDS, y2, multiplicativeGroupByM)
        var Z: UInt = 0u
        for (i in 3 downTo 0) {
            Z = (Z shl 8) + z[i].toUInt()
        }
        return Z
    }

    fun qPermutation(x: UInt, q: Int): UInt {
        require(x in (0u..255u))
        require(q == 0 || q == 1)
        val a: Array<UInt> = Array(5) { 0u }
        val b: Array<UInt> = Array(5) { 0u }
        a[0] = x shr 4
        b[0] = mod(x, 16)
        a[1] = xor(a[0], b[0])
        b[1] = xor(xor(a[0], b[0].rotateRight4bit(1)), mod(8u * a[0], 16))
        if (q == 0) {
            a[2] = q0_permutation_table[0][a[1].toInt()].toUInt()
            b[2] = q0_permutation_table[1][b[1].toInt()].toUInt()
        } else {
            a[2] = q1_permutation_table[0][a[1].toInt()].toUInt()
            b[2] = q1_permutation_table[1][b[1].toInt()].toUInt()
        }
        a[3] = xor(a[2], b[2])
        b[3] = xor(xor(a[2], b[2].rotateRight4bit(1)), mod(8u * a[2], 16))
        if (q == 0) {
            a[4] = q0_permutation_table[2][a[3].toInt()].toUInt()
            b[4] = q0_permutation_table[3][b[3].toInt()].toUInt()
        } else {
            a[4] = q1_permutation_table[2][a[3].toInt()].toUInt()
            b[4] = q1_permutation_table[3][b[3].toInt()].toUInt()
        }
        return 16u * b[4] + a[4]
    }

    fun F(R0: UInt, R1: UInt, r: Int): Array<UInt> {
        val T: Array<UInt> = Array(2) { 0u }
        T[0] = h(R0, S)
        T[1] = h(R1.rotateLeft(8), S)
        val F: Array<UInt> = Array(2) { 0u }
        F[0] = ((T[0] + T[1] + K[2 * r + 8]) and 0xFFFFFFFFu)
        F[1] = ((T[0] + 2u * T[1] + K[2 * r + 9]) and 0xFFFFFFFFu)
        return F
    }

    fun encryptBlock(array: Array<UByte>): Array<UByte> {
        require(array.count() == 16)
        val P: Array<UInt> = Array(4) { 0u }
        for (i in 0..3) {
            for (j in 3 downTo 0) {
                P[i] = (P[i] shl 8) + array[4 * i + j]
            }
        }
        val R: Array<Array<UInt>> = Array(17) { Array(4) { 0u } }
        for (i in 0..3) {
            R[0][i] = xor(P[i], K[i])
        }
        val F: Array<Array<UInt>> = Array(16) { Array(2) { 0u } }
        for (r in 0..15) {
            F[r] = F(R[r][0], R[r][1], r)
            R[r + 1][0] = xor(R[r][2], F[r][0]).rotateRight(1)
            R[r + 1][1] = xor(R[r][3].rotateLeft(1), F[r][1])
            R[r + 1][2] = R[r][0]
            R[r + 1][3] = R[r][1]
        }
        val C: Array<UInt> = Array(4) { 0u }
        for (i in 0..3) {
            C[i] = xor(R[16][mod(i + 2, 4)], K[i + 4])
        }
        val c: Array<UByte> = Array(16) { 0u }
        for (i in 0..15) {
            c[i] = ((C[i / 4] shr (8 * mod(i, 4))) and 0xFFu).toUByte()
        }
        return c
    }

    fun decryptBlock(array: Array<UByte>): Array<UByte> {
        require(array.count() == 16)
        val P: Array<UInt> = Array(4) { 0u }
        for (i in 0..3) {
            for (j in 3 downTo 0) {
                P[i] = (P[i] shl 8) + array[4 * i + j]
            }
        }
        val R: Array<Array<UInt>> = Array(17) { Array(4) { 0u } }
        for (i in 0..3) {
            R[16][i] = xor(P[i], K[i + 4])
        }
        val F: Array<Array<UInt>> = Array(16) { Array(2) { 0u } }
        for (r in 15 downTo 0) {
            F[r] = F(R[r + 1][0], R[r + 1][1], r)
            R[r][0] = xor(R[r + 1][2].rotateLeft(1), F[r][0])
            R[r][1] = xor(R[r + 1][3], F[r][1]).rotateRight(1)
            R[r][2] = R[r + 1][0]
            R[r][3] = R[r + 1][1]
        }
        val C: Array<UInt> = Array(4) { 0u }
        for (i in 0..3) {
            C[i] = xor(R[0][mod(i + 2, 4)], K[i])
        }
        val c: Array<UByte> = Array(16) { 0u }
        for (i in 0..15) {
            c[i] = ((C[i / 4] shr (8 * mod(i, 4))) and 0xFFu).toUByte()
        }
        return c
    }
}
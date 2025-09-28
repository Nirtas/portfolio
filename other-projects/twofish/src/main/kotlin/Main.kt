/*
    Вариант 10. Реализовать алгоритм шифрования Twofish.
*/

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.DecimalFormat
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText

enum class InputLocation(val locationName: String) {
    TEXT_FROM_CONSOLE("TEXT FROM CONSOLE"), PICTURE("PICTURE"), TEXT_FROM_FILE("TEXT FROM FILE"), FILE("FILE")
}

enum class InputType {
    STRING, BYTE_ARRAY
}

enum class OutputLocation(val locationName: String) {
    CONSOLE("CONSOLE"), PICTURE("NEW PICTURE"), TEXT_FILE("NEW TEXT FILE"), FILE("NEW FILE")
}

enum class OperationType {
    ENCRYPTION, DECRYPTION
}

var pictureHeader: Array<UByte> = emptyArray()
var inputArray: Array<UByte> = emptyArray()
var outputArray: Array<UByte> = emptyArray()

fun main() {
    /*val image: ByteArray = Files.readAllBytes(Paths.get("screen4.bmp"))
    for (i in (50000..50030)) {
        image[i] = 0xFF.toByte()
    }
    Files.write(Paths.get("screen5.bmp"), image)*/

    val originalMessageBlocks: Array<Array<UByte>> = inputMessage()

    var userKey: Array<UByte> = emptyArray()
    var isIncorrectInput: Boolean = false
    do {
        print("Enter 1 - generate a key, 2 - enter manually: ")
        when (readln()) {
            "1" -> {
                userKey = generateUserKey()
                println("generated user key: " + userKey.map { it.toString(16).uppercase() })
                isIncorrectInput = false
            }

            "2" -> {
                userKey = inputKey()
                isIncorrectInput = false
            }

            else -> {
                println("Incorrect input. Try again.")
                isIncorrectInput = true
            }
        }
    } while (isIncorrectInput)

    Twofish.K = Twofish.generateSubKeys(userKey)

    val mode: Twofish.ChainingMode = inputChainingMode()

    var initialVector: Array<UByte> = emptyArray()
    if (mode in arrayOf(Twofish.ChainingMode.CBC, Twofish.ChainingMode.CFB, Twofish.ChainingMode.OFB)) {
        isIncorrectInput = false
        do {
            print("Enter 1 - generate an initial vector, 2 - enter manually: ")
            when (readln()) {
                "1" -> {
                    initialVector = generateInitialVector()
                    println("generated initial vector: " + initialVector.map { it.toString(16).uppercase() })
                    isIncorrectInput = false
                }

                "2" -> {
                    initialVector = inputInitialVector()
                    isIncorrectInput = false
                }

                else -> {
                    println("Incorrect input. Try again.")
                    isIncorrectInput = true
                }
            }
        } while (isIncorrectInput)
    }

    val operationType: OperationType = inputOperationType()

    var resultBlocks: Array<Array<UByte>> = Array(originalMessageBlocks.count()) { Array(16) { 0u } }

    when (mode) {
        Twofish.ChainingMode.ECB -> {
            when (operationType) {
                OperationType.ENCRYPTION -> {
                    resultBlocks = Twofish.encryptECB(originalMessageBlocks)
                }

                OperationType.DECRYPTION -> {
                    resultBlocks = Twofish.decryptECB(originalMessageBlocks)
                }
            }
        }

        Twofish.ChainingMode.CBC -> {
            when (operationType) {
                OperationType.ENCRYPTION -> {
                    resultBlocks = Twofish.encryptCBC(originalMessageBlocks, initialVector)
                }

                OperationType.DECRYPTION -> {
                    resultBlocks = Twofish.decryptCBC(originalMessageBlocks, initialVector)
                }
            }
        }

        Twofish.ChainingMode.CFB -> {
            when (operationType) {
                OperationType.ENCRYPTION -> {
                    resultBlocks = Twofish.encryptCFB(originalMessageBlocks, initialVector)
                }

                OperationType.DECRYPTION -> {
                    resultBlocks = Twofish.decryptCFB(originalMessageBlocks, initialVector)
                }
            }
        }

        Twofish.ChainingMode.OFB -> {
            when (operationType) {
                OperationType.ENCRYPTION -> {
                    resultBlocks = Twofish.encryptOFB(originalMessageBlocks, initialVector)
                }

                OperationType.DECRYPTION -> {
                    resultBlocks = Twofish.decryptOFB(originalMessageBlocks, initialVector)
                }
            }
        }
    }

    outputArray = resultBlocks.flatten().toTypedArray()
    calculateCorrelationCoefficient(inputArray, outputArray, 260, 260)
    resultBlocks.forEachIndexed { index, block ->
        val (zerosCount: Int, onesCount: Int) = countZerosAndOnes(block)
        val zerosPercentage: Double = zerosCount.toDouble() / (zerosCount + onesCount) * 100
        val onesPercentage: Double = 100 - zerosPercentage
        println(
            "Block #$index: 0 = $zerosCount (${DecimalFormat("#.##").format(zerosPercentage)}%), 1 = $onesCount (${
                DecimalFormat(
                    "#.##"
                ).format(onesPercentage)
            }%)."
        )
    }

    var allowedLocations: List<OutputLocation> = OutputLocation.entries
    if (pictureHeader.isEmpty()) {
        allowedLocations = allowedLocations.minus(OutputLocation.PICTURE)
    }

    val outputLocation: OutputLocation = inputOutputLocation(allowedLocations)

    when (outputLocation) {
        OutputLocation.CONSOLE -> {
            val inputType: InputType = inputInputType()
            when (inputType) {
                InputType.STRING -> {
                    var result: String = resultBlocks.joinToString("") {
                        it.map { it.toInt().toChar() }.joinToString("")
                    }
                    if (operationType == OperationType.DECRYPTION) {
                        val zeroSymbolsCount: Int = countZeroSymbols(result)
                        result = result.dropLast(zeroSymbolsCount)
                    }
                    println("result: " + result)
                }

                InputType.BYTE_ARRAY -> {
                    var result: Array<UByte> = emptyArray()
                    runBlocking {
                        for (i in resultBlocks.indices) {
                            launch {
                                result += resultBlocks[i]
                            }
                        }
                    }
                    if (operationType == OperationType.DECRYPTION) {
                        val zeroSymbolsCount: Int = countZeroSymbols(result)
                        result = result.dropLast(zeroSymbolsCount).toTypedArray()
                    }
                    println("result: " + result.map { it.toString(16).uppercase() })
                }
            }
        }

        OutputLocation.PICTURE -> {
            var result = pictureHeader
            runBlocking {
                launch {
                    result += resultBlocks.flatten()
                }
            }
            if (operationType == OperationType.DECRYPTION) {
                val zeroSymbolsCount: Int = countZeroSymbols(result)
                result = result.dropLast(zeroSymbolsCount).toTypedArray()
            }

            var newImageDirectoryPath: Path
            do {
                print("Enter the correct directory where the new image will be located: ")
                newImageDirectoryPath = Paths.get(readln())
            } while (!newImageDirectoryPath.exists())

            var newImageName: String = ""
            do {
                print("Enter the correct name of new image file WITHOUT extension: ")
                newImageName = readln()
            } while (newImageName.isEmpty())

            Files.createFile(Path.of(newImageDirectoryPath.toString(), "${newImageName}.bmp"))
                .writeBytes(result.map { it.toByte() }.toByteArray())
        }

        OutputLocation.TEXT_FILE -> {
            val inputType: InputType = inputInputType()
            when (inputType) {
                InputType.STRING -> {
                    var result: String = resultBlocks.joinToString("") {
                        it.map { it.toInt().toChar() }.joinToString("")
                    }
                    if (operationType == OperationType.DECRYPTION) {
                        val zeroSymbolsCount: Int = countZeroSymbols(result)
                        result = result.dropLast(zeroSymbolsCount)
                    }

                    var newTextFileDirectoryPath: Path
                    do {
                        print("Enter the correct directory where the new text file will be located: ")
                        newTextFileDirectoryPath = Paths.get(readln())
                    } while (!newTextFileDirectoryPath.exists())

                    var newTextFileName: String = ""
                    do {
                        print("Enter the correct name of new text file WITHOUT extension: ")
                        newTextFileName = readln()
                    } while (newTextFileName.isEmpty())

                    Files.createFile(Path.of(newTextFileDirectoryPath.toString(), "${newTextFileName}.txt"))
                        .writeText(result)
                }

                InputType.BYTE_ARRAY -> {
                    var result: Array<UByte> = emptyArray()
                    for (resultBlock in resultBlocks) {
                        result += resultBlock
                    }
                    if (operationType == OperationType.DECRYPTION) {
                        val zeroSymbolsCount: Int = countZeroSymbols(result)
                        result = result.dropLast(zeroSymbolsCount).toTypedArray()
                    }

                    var newTextFileDirectoryPath: Path
                    do {
                        print("Enter the correct directory where the new text file will be located: ")
                        newTextFileDirectoryPath = Paths.get(readln())
                    } while (!newTextFileDirectoryPath.exists())

                    var newTextFileName: String = ""
                    do {
                        print("Enter the correct name of new text file WITHOUT extension: ")
                        newTextFileName = readln()
                    } while (newTextFileName.isEmpty())

                    Files.createFile(Path.of(newTextFileDirectoryPath.toString(), "${newTextFileName}.txt"))
                        .writeText(result.map { it.toString(16).uppercase() }.toString())
                }
            }
        }

        OutputLocation.FILE -> {
            var result: Array<UByte> = emptyArray()
            for (resultBlock in resultBlocks) {
                result += resultBlock
            }
            if (operationType == OperationType.DECRYPTION) {
                val zeroSymbolsCount: Int = countZeroSymbols(result)
                result = result.dropLast(zeroSymbolsCount).toTypedArray()
            }

            var newFileDirectoryPath: Path
            do {
                print("Enter the correct directory where the new file will be located: ")
                newFileDirectoryPath = Paths.get(readln())
            } while (!newFileDirectoryPath.exists())

            var newFileName: String = ""
            do {
                print("Enter the correct name of new file WITH extension: ")
                newFileName = readln()
            } while (newFileName.isEmpty())

            Files.createFile(Path.of(newFileDirectoryPath.toString(), newFileName))
                .writeBytes(result.map { it.toByte() }.toByteArray())
        }
    }
}

fun countZerosAndOnes(block: Array<UByte>): Pair<Int, Int> {
    val blockString: String = block.joinToString("") { it.toString(2) }
    val zerosCount: Int = blockString.count { it == '0' }
    val onesCount: Int = blockString.count { it == '1' }
    return Pair(zerosCount, onesCount)
}

fun generateUserKey(): Array<UByte> {
    val userKey: Array<UByte> = Array((0..31).random()) { 0u }
    for (i in userKey.indices) {
        userKey[i] = (UByte.MIN_VALUE..UByte.MAX_VALUE).random().toUByte()
    }
    return expandUserKey(userKey)
}

fun generateInitialVector(): Array<UByte> {
    val initialVector: Array<UByte> = Array(16) { 0u }
    for (i in initialVector.indices) {
        initialVector[i] = (UByte.MIN_VALUE..UByte.MAX_VALUE).random().toUByte()
    }
    return initialVector
}

fun countZeroSymbols(array: Array<UByte>): Int {
    var zeroSymbolsCount: Int = 0
    for (i in array.indices.reversed()) {
        if (array[i] == 0u.toUByte()) {
            zeroSymbolsCount++
        } else {
            break
        }
    }
    return zeroSymbolsCount
}

fun countZeroSymbols(text: String): Int {
    var zeroSymbolsCount: Int = 0
    for (i in text.indices.reversed()) {
        if (text[i] == 0.toChar()) {
            zeroSymbolsCount++
        } else {
            break
        }
    }
    return zeroSymbolsCount
}

fun textToUByteArray(text: String): Array<UByte> {
    val startPos: Int = text.indexOfFirst { it == '[' }
    val endPos: Int = text.indexOfFirst { it == ']' }
    if (startPos == -1 || endPos == -1) {
        throw Exception("Byte array not found.")
    }
    return text.filterIndexed { index, c -> index in (startPos + 1)..<endPos }.split(", ")
        .map { it.toInt(16).toUByte() }.toTypedArray()
}

fun inputMessage(): Array<Array<UByte>> {
    println("\nINPUT MESSAGE")
    val inputLocation: InputLocation = inputInputLocation(InputLocation.entries)
    var originalMessage: String = ""
    var originalMessageArray: Array<UByte> = emptyArray()
    var originalMessageBlocks: Array<Array<UByte>> = emptyArray()
    when (inputLocation) {
        InputLocation.TEXT_FROM_CONSOLE -> {
            val inputType: InputType = inputInputType()
            when (inputType) {
                InputType.STRING -> {
                    println("Enter text. Example: Encrypt this string!")
                    originalMessageArray = readln().map { it.code.toUByte() }.toTypedArray()
                }

                InputType.BYTE_ARRAY -> {
                    println("Enter text. Example: [FF, A0, A, 0, BB]")
                    originalMessageArray = textToUByteArray(readln())
                }
            }
        }

        InputLocation.PICTURE -> {
            var imagePath: Path
            do {
                print("Enter the correct path to the existing file with the extension .bmp: ")
                imagePath = Paths.get(readln())
            } while (!imagePath.exists() || imagePath.extension != "bmp")
            val imageContent: Array<UByte> = Files.readAllBytes(imagePath).map { it.toUByte() }.toTypedArray()
            pictureHeader = imageContent.take(54).toTypedArray()
            originalMessageArray = imageContent.takeLast(imageContent.count() - 54).toTypedArray()
            inputArray = originalMessageArray
        }

        InputLocation.TEXT_FROM_FILE -> {
            var textFilePath: Path
            do {
                print("Enter the correct path to the existing text file: ")
                textFilePath = Paths.get(readln())
            } while (!textFilePath.exists())
            originalMessage = Files.readAllLines(textFilePath).joinToString(" ")
            val inputType: InputType = inputInputType()
            when (inputType) {
                InputType.STRING -> {
                    originalMessageArray = originalMessage.map { it.code.toUByte() }.toTypedArray()
                }

                InputType.BYTE_ARRAY -> {
                    originalMessageArray = textToUByteArray(originalMessage)
                }
            }
        }

        InputLocation.FILE -> {
            var filePath: Path
            do {
                print("Enter the correct path to the existing file: ")
                filePath = Paths.get(readln())
            } while (!filePath.exists())

            originalMessageArray = Files.readAllBytes(filePath).map { it.toUByte() }.toTypedArray()
        }
    }

    if (originalMessageArray.count() % 16 != 0 || originalMessageArray.isEmpty()) {
        originalMessageArray = expandArray(originalMessageArray, 16 * (originalMessageArray.count() / 16 + 1))
    }
    originalMessageBlocks = Twofish.divideArrayIntoBlocks(originalMessageArray, 16)

    return originalMessageBlocks
}

fun inputKey(): Array<UByte> {
    println("\nINPUT KEY")
    val allowedLocations: List<InputLocation> = listOf(InputLocation.TEXT_FROM_CONSOLE, InputLocation.TEXT_FROM_FILE)
    val inputLocation: InputLocation = inputInputLocation(allowedLocations)
    var userKey: Array<UByte> = emptyArray()
    when (inputLocation) {
        InputLocation.TEXT_FROM_CONSOLE -> {
            val inputType: InputType = inputInputType()
            when (inputType) {
                InputType.STRING -> {
                    println("Enter key. Example: Key")
                    userKey = readln().map { it.code.toUByte() }.toTypedArray()
                }

                InputType.BYTE_ARRAY -> {
                    println("Enter key. Example: [FF, A0, A, 0, BB]")
                    userKey = textToUByteArray(readln())
                }
            }
        }

        InputLocation.TEXT_FROM_FILE -> {
            var textFilePath: Path
            do {
                print("Enter the correct path to the existing text file: ")
                textFilePath = Paths.get(readln())
            } while (!textFilePath.exists())
            val inputType: InputType = inputInputType()
            when (inputType) {
                InputType.STRING -> {
                    userKey =
                        Files.readAllLines(textFilePath).joinToString(" ").map { it.code.toUByte() }.toTypedArray()
                }

                InputType.BYTE_ARRAY -> {
                    userKey = textToUByteArray(Files.readAllLines(textFilePath).joinToString(" "))
                }
            }
        }

        else -> {}
    }
    return expandUserKey(userKey)
}

fun expandUserKey(userKey: Array<UByte>): Array<UByte> {
    var newUserKey: Array<UByte> = userKey.copyOf()
    when (newUserKey.count()) {
        in (0..15) -> newUserKey = expandArray(newUserKey, 16)
        in (17..23) -> newUserKey = expandArray(newUserKey, 24)
        in (25..31) -> newUserKey = expandArray(newUserKey, 32)
        in (33..Int.MAX_VALUE) -> throw Exception("The key length must be 16, 24 or 32 characters.")
    }
    return newUserKey
}

fun inputInputLocation(allowedLocations: List<InputLocation>): InputLocation {
    var inputLocation: InputLocation = InputLocation.TEXT_FROM_CONSOLE
    var isIncorrectInput: Boolean = false
    do {
        println("Select the type of location that you want to work with.")
        print("Enter ")
        for (i in allowedLocations.indices) {
            print("${i + 1} - ${allowedLocations[i].locationName}")
            if (i != allowedLocations.lastIndex) print(", ")
        }
        print(": ")
        val input: String = readln()
        for (i in allowedLocations.indices) {
            if (input == (i + 1).toString()) {
                inputLocation = allowedLocations[i]
                isIncorrectInput = false
                break
            }
            if (i == allowedLocations.lastIndex) {
                println("Incorrect input. Try again.")
                isIncorrectInput = true
            }
        }
    } while (isIncorrectInput)
    return inputLocation
}

fun inputInputType(): InputType {
    var inputType: InputType = InputType.STRING
    var isIncorrectInput: Boolean = false
    do {
        println("Enter the type.")
        print("Enter 1 - TEXT, 2 - BYTE ARRAY: ")
        when (readln()) {
            "1" -> {
                inputType = InputType.STRING
                isIncorrectInput = false
            }

            "2" -> {
                inputType = InputType.BYTE_ARRAY
                isIncorrectInput = false
            }

            else -> {
                println("Incorrect input. Try again.")
                isIncorrectInput = true
            }
        }
    } while (isIncorrectInput)
    return inputType
}

fun inputChainingMode(): Twofish.ChainingMode {
    var mode: Twofish.ChainingMode = Twofish.ChainingMode.ECB
    var isIncorrectInput: Boolean = false
    do {
        println("Select chaining mode.")
        print("Enter 1 - ECB, 2 - CBC, 3 - CFB, 4 - OFB: ")
        when (readln()) {
            "1" -> {
                mode = Twofish.ChainingMode.ECB
                isIncorrectInput = false
            }

            "2" -> {
                mode = Twofish.ChainingMode.CBC
                isIncorrectInput = false
            }

            "3" -> {
                mode = Twofish.ChainingMode.CFB
                isIncorrectInput = false
            }

            "4" -> {
                mode = Twofish.ChainingMode.OFB
                isIncorrectInput = false
            }

            else -> {
                println("Incorrect input. Try again.")
                isIncorrectInput = true
            }
        }
    } while (isIncorrectInput)
    return mode
}

fun inputInitialVector(): Array<UByte> {
    println("\nINPUT INITIAL VECTOR")
    val allowedLocations: List<InputLocation> = listOf(InputLocation.TEXT_FROM_CONSOLE, InputLocation.TEXT_FROM_FILE)
    val inputLocation: InputLocation = inputInputLocation(allowedLocations)
    var initialVector: Array<UByte> = Array(16) { 0u }
    do {
        println("The initialization vector must be 16 characters long.")
        println("Example: !INITIAL VECTOR! for input type: TEXT.")
        println("Example: [00, 01, 02, 03, 04, 05, 06, 07, 08, 09, AA, BB, CC, DD, EE, FF] for input type: BYTE ARRAY.")
        when (inputLocation) {
            InputLocation.TEXT_FROM_CONSOLE -> {
                val inputType: InputType = inputInputType()
                when (inputType) {
                    InputType.STRING -> {
                        println("Enter initial vector: ")
                        initialVector = readln().map { it.code.toUByte() }.toTypedArray()
                    }

                    InputType.BYTE_ARRAY -> {
                        println("Enter initial vector: ")
                        initialVector = textToUByteArray(readln())
                    }
                }
            }

            InputLocation.TEXT_FROM_FILE -> {
                var textFilePath: Path
                do {
                    print("Enter the correct path to the existing text file: ")
                    textFilePath = Paths.get(readln())
                } while (!textFilePath.exists())
                val inputType: InputType = inputInputType()
                when (inputType) {
                    InputType.STRING -> {
                        initialVector =
                            Files.readAllLines(textFilePath).joinToString(" ").map { it.code.toUByte() }.toTypedArray()
                    }

                    InputType.BYTE_ARRAY -> {
                        initialVector = textToUByteArray(Files.readAllLines(textFilePath).joinToString(" "))
                    }
                }
            }

            else -> {}
        }
    } while (initialVector.count() != 16)

    return initialVector
}

fun inputOperationType(): OperationType {
    var operationType: OperationType = OperationType.ENCRYPTION
    var isIncorrectInput = false
    do {
        println("What to do with the input data?")
        print("Enter 1 - ENCRYPTION, 2 - DECRYPTION: ")
        when (readln()) {
            "1" -> {
                operationType = OperationType.ENCRYPTION
                isIncorrectInput = false
            }

            "2" -> {
                operationType = OperationType.DECRYPTION
                isIncorrectInput = false
            }

            else -> {
                println("Incorrect input. Try again.")
                isIncorrectInput = true
            }
        }
    } while (isIncorrectInput)
    return operationType
}

fun inputOutputLocation(allowedLocations: List<OutputLocation>): OutputLocation {
    var outputLocation: OutputLocation = OutputLocation.CONSOLE
    var isIncorrectInput: Boolean = false
    do {
        println("Enter the type of location where you want to output the result of the program.")
        print("Enter ")
        for (i in allowedLocations.indices) {
            print("${i + 1} - ${allowedLocations[i].locationName}")
            if (i != allowedLocations.lastIndex) print(", ")
        }
        print(": ")
        val input: String = readln()
        for (i in allowedLocations.indices) {
            if (input == (i + 1).toString()) {
                outputLocation = allowedLocations[i]
                isIncorrectInput = false
                break
            }
            if (i == allowedLocations.lastIndex) {
                println("Incorrect input. Try again.")
                isIncorrectInput = true
            }
        }
    } while (isIncorrectInput)
    return outputLocation
}

fun expandArray(array: Array<UByte>, length: Int): Array<UByte> {
    var expandedArray: Array<UByte> = array
    for (i in 1..(length - array.count())) {
        expandedArray += 0u
    }
    return expandedArray
}

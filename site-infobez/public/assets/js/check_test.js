const KeyFormation = function (phrase, key) {

    let fullKey = ''
    let isAnotherSymbol = false
    let countAnotherSymbols = 0

    for (let i = 0; i < phrase.length; i++) {

        if ((phrase[i][0].charCodeAt(0) < 1040 || phrase[i][0].charCodeAt(0) > 1071) && (phrase[i][0].charCodeAt(0) < 48 || phrase[i][0].charCodeAt(0) > 57)) {

            isAnotherSymbol = true
        }

        if (!isAnotherSymbol) {
            fullKey += key[(i - countAnotherSymbols) % key.length]
        } else {
            fullKey += " ";
            countAnotherSymbols++;
        }

        isAnotherSymbol = false
    }

    return fullKey
}

const CaesarCipher = function (phrase, rot, cryptMethod) {

    let ans = ''

    for (let i = 0, len = phrase.length; i < len; i++) {

        if (phrase[i].charCodeAt(0) < 1040 || phrase[i].charCodeAt(0) > 1071) {

            ans += phrase[i]
            continue
        }

        if (Number(cryptMethod) === Number(1)) {
            ans += String.fromCharCode(((phrase[i].charCodeAt(0) - 1040 + Number(rot)) % 32) + 1040)
        } else if (Number(cryptMethod) === Number(2)) {
            ans += String.fromCharCode(((phrase[i].charCodeAt(0) - 1040 - Number(rot) + 32) % 32) + 1040)
        }

    }

    return ans
}

const VigenereCipher = function (phrase, key, cryptMethod) {

    let fullKey = KeyFormation(phrase, key)

    let ans = ''

    for (let i = 0, len = phrase.length; i < len; i++) {

        if (phrase[i].charCodeAt(0) < 1040 || phrase[i].charCodeAt(0) > 1071) {

            ans += phrase[i]
            continue
        }

        if (Number(cryptMethod) === Number(1)) {
            ans += String.fromCharCode(((phrase[i].charCodeAt(0) + fullKey[i].charCodeAt(0) + 1) % 32) + 1040)
        } else if (Number(cryptMethod) === Number(2)) {
            ans += String.fromCharCode(((phrase[i].charCodeAt(0) - fullKey[i].charCodeAt(0) + 31) % 32) + 1040)
        }

    }

    return ans
}

const PlayfairCipher = function (phrase, key, cryptMethod) {

    let playfairArr = new Array(4).fill(undefined).map(() => new Array(8).fill(undefined));

    let numKey = 0

    phrase = phrase.replace(/[^А-Яа-я]/g, "")

    for (let i = 0, playfairArrRows = playfairArr.length; i < playfairArrRows; i++) {
        for (let j = 0, playfairArrCols = playfairArr[0].length; j < playfairArrCols; j++) {

            if (key[numKey] !== undefined) {

                if (!(playfairArr.some(arr => arr.includes(key[numKey])))) {

                    playfairArr[i][j] = key[numKey]
                } else {
                    j--
                }

                numKey++

                continue
            }

            for (let k = 0; k < 32; k++) {

                if (!(playfairArr.some(arr => arr.includes(String.fromCharCode(1040 + k))))) {

                    playfairArr[i][j] = String.fromCharCode(1040 + k)

                    break
                }
            }
        }
    }

    let bigramsArr = []

    for (let i = 0, lenPhrase = phrase.length; i < lenPhrase; i += 2) {

        if (i === lenPhrase - 1) {

            bigramsArr.push([phrase[i], 'Ъ'])
            break
        }

        if (phrase[i] !== phrase[i + 1]) {

            bigramsArr.push([phrase[i], phrase[i + 1]])
        } else {

            bigramsArr.push([phrase[i], 'Ъ'])
            i--
        }
    }

    let firstBigramElementPos = [-1, -1]
    let secondBigramElementPos = [-1, -1]

    let correctAnswerArr = []

    for (let i = 0, bigramsArrRows = bigramsArr.length; i < bigramsArrRows; i++) {

        for (let j = 0, playfairArrRows = playfairArr.length; j < playfairArrRows; j++) {

            if (firstBigramElementPos[1] === -1) {

                firstBigramElementPos[0] = j
                firstBigramElementPos[1] = playfairArr[j].indexOf(bigramsArr[i][0])
            }

            if (secondBigramElementPos[1] === -1) {

                secondBigramElementPos[0] = j
                secondBigramElementPos[1] = playfairArr[j].indexOf(bigramsArr[i][1])
            }


            if (firstBigramElementPos[1] !== -1 && secondBigramElementPos[1] !== -1) {

                if (firstBigramElementPos[0] === secondBigramElementPos[0]) {

                    if (Number(cryptMethod) === Number(1)) {
                        firstBigramElementPos[1] = (firstBigramElementPos[1] + 1) % playfairArr[0].length
                        secondBigramElementPos[1] = (secondBigramElementPos[1] + 1) % playfairArr[0].length
                    }
                    else if (Number(cryptMethod) === Number(2)) {
                        firstBigramElementPos[1] = (firstBigramElementPos[1] - 1 + playfairArr[0].length) % playfairArr[0].length
                        secondBigramElementPos[1] = (secondBigramElementPos[1] - 1 + playfairArr[0].length) % playfairArr[0].length
                    }

                } else if (firstBigramElementPos[1] === secondBigramElementPos[1]) {

                    if (Number(cryptMethod) === Number(1)) {
                        firstBigramElementPos[0] = (firstBigramElementPos[0] + 1) % playfairArrRows
                        secondBigramElementPos[0] = (secondBigramElementPos[0] + 1) % playfairArrRows
                    }
                    else if (Number(cryptMethod) === Number(2)) {
                        firstBigramElementPos[0] = (firstBigramElementPos[0] - 1 + playfairArrRows) % playfairArrRows
                        secondBigramElementPos[0] = (secondBigramElementPos[0] - 1 + playfairArrRows) % playfairArrRows
                    }

                } else {

                    secondBigramElementPos[1] = [firstBigramElementPos[1], firstBigramElementPos[1] = secondBigramElementPos[1]][0]
                }

                break
            }
        }

        correctAnswerArr.push(playfairArr[firstBigramElementPos[0]][firstBigramElementPos[1]], playfairArr[secondBigramElementPos[0]][secondBigramElementPos[1]])

        firstBigramElementPos = [-1, -1]
        secondBigramElementPos = [-1, -1]
    }

    return correctAnswerArr.join('')
}

const VernamCipher = function (phrase, key, cryptMethod) {

    let ans = ''

    if (Number(cryptMethod) === Number(1)) {

        let fullKey = KeyFormation(phrase, key)

        for (let i = 0, len = phrase.length; i < len; i++) {

            if (phrase[i].charCodeAt(0) < 1040 || phrase[i].charCodeAt(0) > 1071) {

                continue
            }

            ans += (phrase[i].charCodeAt(0) ^ fullKey[i].charCodeAt(0)) % 32 + ' '

        }
    } else if (Number(cryptMethod) === Number(2)) {

        let numbers = phrase.split(" ")

        let fullKey = KeyFormation(numbers, key)

        for (let i = 0, len = numbers.length; i < len; i++) {

            ans += String.fromCharCode((numbers[i] ^ (fullKey[i].charCodeAt(0) - 1040)) % 32 + 1040)
        }

    }

    return String(ans).trim()
}


module.exports = {
    CheckAnswer: function (userAnswer, phrase, key, cipherType, questionType) {

        if (String(userAnswer).trim() === '') {
            return {message: 'Проверьте правильность ввода'}
        }

        if (String(phrase).trim() === '' || String(key).trim() === '') {
            return {message: 'Обновите задание или перезагрузите страницу'}
        }

        phrase = phrase.toUpperCase()
        key = key.toUpperCase()

        if (cipherType === 'caesar') {

            let correctAnswer = CaesarCipher(phrase, key, questionType)

            if (userAnswer.toUpperCase() !== correctAnswer) {
                return {result: false}
            }

            return {result: true}
        } else if (cipherType === 'vigenere') {

            let correctAnswer = VigenereCipher(phrase, key, questionType)

            if (userAnswer.toUpperCase() !== correctAnswer) {
                return {result: false}
            }

            return {result: true}
        } else if (cipherType === 'playfair') {

            let correctAnswer = PlayfairCipher(phrase, key, questionType)

            if (userAnswer.toUpperCase() !== correctAnswer) {
                return {result: false}
            }

            return {result: true}
        } else if (cipherType === 'vernam') {

            let correctAnswer = VernamCipher(phrase, key, questionType)

            if (userAnswer.toUpperCase() !== correctAnswer) {
                return {result: false}
            }

            return {result: true}
        } else {
            console.log('WTF')
            return {}
        }
    },
    CaesarCipher,
    VigenereCipher,
    PlayfairCipher,
    VernamCipher
}
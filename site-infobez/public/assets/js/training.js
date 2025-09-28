const fs = require('fs')
const {CaesarCipher, VigenereCipher, PlayfairCipher, VernamCipher} = require('./check_test')


const GetRandomPhrase = function () {
    let file = fs.readFileSync('./public/assets/data/russian-words.json');

    let phrases = JSON.parse(file);

    return phrases[Math.floor(Math.random() * phrases.length)]
}

module.exports = {
    CreateTask: function (page) {

        let pageName = String(page).split('/')[3]

        let pagesArray = ['caesar', 'vigenere', 'playfair', 'vernam']

        if (!pagesArray.includes(pageName)) {
            console.log('WTF')
            return {}
        }

        let questionType = Math.floor(Math.random() * (3 - 1)) + 1

        let key = undefined

        if (pageName === 'caesar') {
            key = Math.floor(Math.random() * (32 - 1)) + 1
        } else {
            key = GetRandomPhrase().toUpperCase()
        }

        let phrase = GetRandomPhrase().toUpperCase()
        let task = '';

        if (questionType === 1) {
            task += 'Зашифруйте слово с помощью '
        } else if (questionType === 2) {
            task += 'Расшифруйте слово, зашифрованное с помощью '

            switch (pageName) {
                case 'caesar':
                    phrase = CaesarCipher(phrase, key, 1)
                    break;
                case 'vigenere':
                    phrase = VigenereCipher(phrase, key, 1)
                    break;
                case 'playfair':
                    phrase = PlayfairCipher(phrase, key, 1)
                    break;
                case 'vernam':
                    phrase = VernamCipher(phrase, key, 1)
                    break;
            }
        }

        if (pageName === 'caesar') {
            task += `ROT${key}:`
        }
        else {
            task += `ключа "${key}":`
        }

        return {
            [pageName]: {
                task: task,
                key: key,
                phrase: phrase,
                questionType: questionType
            }
        }
    }
}



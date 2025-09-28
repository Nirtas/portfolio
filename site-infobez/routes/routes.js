const {Router} = require('express')
const {
    Connection,
    Authorization,
    Registration,
    CheckLogin,
    GetStudentVariant,
    GetAllQuestions,
    SaveResultsInDb,
    GetResultsFromDb,
    GetAllStudents,
    GetAllVariants,
    GetAllThemes,
    GetAllQuestionTypes,
    UpdateQuestion
} = require('../public/assets/js/db_work')
const multer = require('multer')
const {CreateTask} = require('../public/assets/js/training')
const {CheckAnswer} = require('../public/assets/js/check_test')


const upload = multer()
const router = Router()

router.get('/', (req, res) => {
    res.render('index', {
        title: 'Главная страница'
    })
})

router.get('/caesar', (req, res) => {
    res.render('caesar', {
        title: 'Шифр Цезаря'
    })
})

router.get('/vigenere', (req, res) => {
    res.render('vigenere', {
        title: 'Шифр Виженера'
    })
})

router.get('/playfair', (req, res) => {
    res.render('playfair', {
        title: 'Шифр Плейфера'
    })
})

router.get('/vernam', (req, res) => {
    res.render('vernam', {
        title: 'Шифр Вернама'
    })
})

router.get('/final-test', (req, res) => {
    res.render('final-test', {
        title: 'Итоговое тестирование'
    })
})

router.get('/teacher', (req, res) => {
    res.render('teacher', {
        title: 'Преподавательская'
    })
})

router.get('/teacher-questions', (req, res) => {
    res.render('questions', {
        title: 'Задания для вариантов'
    })
})

router.get('/teacher-results', (req, res) => {
    res.render('results', {
        title: 'Результаты тестирований'
    })
})


router.post('/authorization', upload.array(), async (req, res) => {
    try {
        const {login, password} = req.body

        if (String(login).trim() === '' || String(password).trim() === '') {
            res.send({message: 'Проверьте корректность ввода'})
            return
        }

        if (login.length < 4 || login.length > 25) {
            res.send({message: 'Длина логина должна быть от 4 до 25 символов'})
            return
        }

        if (password.length < 6) {
            res.send({message: 'Длина пароля должна быть от 6 символов'})
            return
        }

        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await Authorization(login, password)

        if (result.cookie) {
            res.cookie('user', result.cookie.user)
            res.send()
        } else {
            res.send(result)
        }

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/registration', upload.array(), async (req, res) => {
    try {
        const {login, password, surname, name, middlename, group} = req.body

        if (String(login).trim() === '' || String(password).trim() === '' || String(surname).trim() === '' ||
            String(name).trim() === '' || String(middlename).trim() === '' || String(group).trim() === '') {

            res.send({message: 'Проверьте корректность ввода'})
            return
        }

        if (login.length < 5 || login.length > 25) {
            res.send({message: 'Длина логина должна быть от 5 до 25 символов'})
            return
        }

        if (password.length < 6) {
            res.send({message: 'Длина пароля должна быть от 6 символов'})
            return
        }

        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await Registration(login, password, surname, name, middlename, group)

        if (result.cookie) {
            res.cookie('user', result.cookie.user)
            res.send()
        } else {
            res.send(result)
        }

        connection.end()

    } catch (err) {
        throw err
    }

})

router.post('/check-login', upload.array(), async (req, res) => {
    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        if (req.cookies.user !== undefined) {

            let result = await CheckLogin(req.cookies.user)

            if (result !== {}) {
                res.send(result)
            } else {
                res.send({})
            }
        } else {
            res.send({})
        }

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/check-answer', upload.array(), (req, res) => {

    const {userAnswer, phrase, key, cipherType, questionType} = req.body

    let result = CheckAnswer(userAnswer, phrase, key, cipherType, questionType)
    res.send(result)
})

router.post('/create-task-training', upload.array(), (req, res) => {

    let result = CreateTask(req.header('Referer'))
    res.send(result)
})

router.post('/create-task-final-test', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await CheckLogin(req.cookies.user)

        if (result !== {}) {

            let idVariant = await GetStudentVariant(result.idUser)

            result = await GetAllQuestions(idVariant)

            res.send(result)
        } else {

            res.send({})
        }

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/save-results-in-db', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        const {countCorrectAnswers} = req.body

        let result = undefined

        if (req.cookies.user !== undefined) {

            result = await CheckLogin(req.cookies.user)

            if (result !== {}) {

                result = await SaveResultsInDb(countCorrectAnswers, result.idUser)

                res.send(result)
            } else {
                res.send({})
            }
        } else {
            res.send({})
        }

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/get-results-from-db', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        const {idUser, idVariant, dateTimeMin, dateTimeMax} = req.body

        let result = await GetResultsFromDb(dateTimeMin, dateTimeMax, idUser, idVariant);

        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }

})

router.post('/get-all-students', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await GetAllStudents()
        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/get-all-variants', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await GetAllVariants()
        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/get-all-themes', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await GetAllThemes()
        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/get-all-question-types', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        let result = await GetAllQuestionTypes()
        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/create-task-question', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        const {idVariant} = req.body

        let result = await GetAllQuestions(idVariant)

        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }
})

router.post('/update-question', upload.array(), async (req, res) => {

    try {
        const connection = Connection()

        await connection.connect(err => {
            if (err) {
                console.log(err)
            }
        })

        const {idQuestion, idTheme, idQuestionType, phrase, key} = req.body

        let result = await UpdateQuestion(idQuestion, idTheme, idQuestionType, phrase, key)

        res.send(result)

        connection.end()

    } catch (err) {
        throw err
    }
})

module.exports = router
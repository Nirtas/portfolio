const mysql = require('mysql2')
const bcrypt = require('bcrypt')


let connection = undefined

module.exports = {
    Connection: function () {
        connection = mysql.createConnection({
            host: 'HOST',
            user: 'USER',
            password: 'PASSWORD',
            database: 'DATABASE'
        })

        return connection
    },
    Authorization: function (login, password) {

        let sql = `SELECT * FROM users WHERE login = '${login}';`

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data[0] === undefined) {
                    result({message: 'Пользователь с таким логином не зарегистрирован в системе'})
                } else {
                    bcrypt.compare(password, data[0]['password'], function (err, res2) {
                        if (res2) {
                            result({
                                cookie: {
                                    user: bcrypt.hashSync(`${data[0]['idUser']}`, 7)
                                }
                            })
                        } else {
                            result({message: 'Неправильный пароль'})
                        }
                    })
                }
            });
        });
    },
    Registration: function (login, password, surname, name, middlename, group) {

        let sql = `SELECT * FROM users WHERE login = '${login}';`

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data[0] === undefined) {

                    const hashPassword = bcrypt.hashSync(password, 7)

                    sql = `INSERT INTO users (login, password, idUserType) VALUES ('${login}', '${hashPassword}', 1);`

                    connection.query(sql, (err2, data2) => {
                        if (err2) {
                            throw err2
                        }
                    })

                    sql = `SELECT * FROM users WHERE login = '${login}';`

                    connection.query(sql, (err3, data3) => {
                        if (err3) {
                            throw err3
                        }

                        sql = `INSERT INTO additional_information (idUser, surname, name, middlename, stgroup) VALUES ` +
                            `('${data3[0]['idUser']}', '${surname}', '${name}', '${middlename}', '${group}');`

                        connection.query(sql, (err4, data4) => {
                            if (err4) {
                                throw err4
                            }
                        })

                        result({
                            cookie: {
                                user: bcrypt.hashSync(`${data3[0]['idUser']}`, 7)
                            }
                        })
                    })
                } else {
                    result({message: 'Пользователь с таким логином уже зарегистрирован в системе'})
                }
            });
        });
    },
    CheckLogin: function (cookie) {

        let sql = "SELECT * FROM users;"

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data !== undefined && cookie.length > 10) {

                    let isExist = false

                    for (let i = 0; i < data.length; i++) {
                        bcrypt.compare(data[i]['idUser'].toString(), cookie, function (err2, res2) {
                            if (res2) {
                                isExist = true
                                result(data[i])
                            }
                        })

                        if (isExist === true) {
                            break
                        }
                    }
                } else {
                    result({})
                }
            });
        });
    },
    GetStudentVariant: function (idUser) {

        let sql = `SELECT idVariant FROM additional_information WHERE idUser = ${idUser};`

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                result(data[0]['idVariant'])
            })
        })
    },
    GetAllQuestions: function (idVariant) {

        if (!Number.isInteger(Number(idVariant))) {
            return {}
        }

        let sql = `SELECT themes.theme, questions.idQuestion, questions.idQuestionType, questions.phrase, questions.key 
                           FROM questions 
                           INNER JOIN variants_questions ON questions.idQuestion = variants_questions.idQuestion
                           INNER JOIN themes ON questions.idTheme = themes.idTheme
                           WHERE idVariant = ${idVariant}`

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                result(data)
            })
        })
    },
    SaveResultsInDb: function (countCorrectAnswers, idUser) {

        let sql = `SELECT idVariant
                   FROM additional_information
                   WHERE idUser = '${idUser}';`

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                sql = `INSERT INTO results (idUser, idVariant, countCorrectAnswers) VALUES (${idUser}, ${data[0]['idVariant']}, ${countCorrectAnswers});`

                connection.query(sql, (err2, data2) => {
                    if (err2) {
                        throw err2
                    }

                    result({result: true})
                });
                result({result: true})
            });
        });
    },
    GetResultsFromDb: function (dateTimeMin, dateTimeMax, idUser, idVariant) {

        let filterParts = []

        if (idUser !== '-') {
            filterParts.push(`(results.idUser = ${idUser})`)
        }

        if (idVariant !== '-') {
            filterParts.push(`(results.idVariant = ${idVariant})`)
        }

        if (dateTimeMin !== undefined && dateTimeMax !== undefined) {
            filterParts.push(`(date BETWEEN '${dateTimeMin}' AND '${dateTimeMax}')`)
        }

        let sql = "SELECT date AS 'Дата сдачи', CONCAT(surname, ' ', name, ' ', middlename) AS 'Студент', " +
            "results.idVariant 'Вариант', countCorrectAnswers AS 'Кол-во правильных ответов' " +
            "FROM results INNER JOIN additional_information ON results.idUser = additional_information.idUser WHERE "

        sql += filterParts.join(" AND ")

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data !== undefined) {
                    result(data)
                } else {
                    result({})
                }
            });
        });

    },
    GetAllStudents: function () {

        let sql = "SELECT idUser, CONCAT(surname, \" \", name, \" \", middlename) AS 'Student' FROM additional_information;"

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data !== undefined) {

                    let usersArray = []

                    for (let i = 0; i < data.length; i++) {
                        usersArray.push([data[i]['idUser'], data[i]['Student']])
                    }

                    result(usersArray)
                } else {
                    result()
                }
            });
        });
    },
    GetAllVariants: function () {

        let sql = "SELECT idVariant FROM variants;"

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data !== undefined) {

                    let variantsArray = []

                    for (let i = 0; i < data.length; i++) {
                        variantsArray.push(data[i]['idVariant'])
                    }

                    result(variantsArray)
                } else {
                    result()
                }
            });
        });
    },
    GetAllThemes: function () {

        let sql = "SELECT * FROM themes;"

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data !== undefined) {

                    let themesArray = []

                    for (let i = 0; i < data.length; i++) {
                        themesArray.push([data[i]['idTheme'], data[i]['theme']])
                    }

                    result(themesArray)
                } else {
                    result()
                }
            });
        });
    },
    GetAllQuestionTypes: function () {

        let sql = "SELECT * FROM question_types;"

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                if (data !== undefined) {

                    let questionTypesArray = []

                    for (let i = 0; i < data.length; i++) {
                        questionTypesArray.push([data[i]['idQuestionType'], data[i]['questionType']])
                    }

                    result(questionTypesArray)
                } else {
                    result()
                }
            });
        });
    },
    UpdateQuestion: function (idQuestion, idTheme, idQuestionType, phrase, key) {

        let sql = `UPDATE questions
                   SET idTheme = ${idTheme},
                       idQuestionType = ${idQuestionType},
                       phrase = '${phrase}',
                       questions.key = '${key}'
                   WHERE idQuestion = ${idQuestion};`

        return new Promise(result => {
            connection.query(sql, (err, data) => {
                if (err) {
                    throw err
                }

                sql = `SELECT theme FROM themes WHERE idTheme = ${idTheme}`

                connection.query(sql, (err2, data2) => {
                    if (err2) {
                        throw err2
                    }

                    result({theme: data2[0]['theme']})
                });
            });
        });
    }
};


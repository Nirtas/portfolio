function FillTask(cipherType, form_id, questionType) {

    $(`#${form_id} #cipher-type`).val(cipherType)
    $(`#${form_id} #question-type`).val(questionType)

    if (cipherType === 'caesar') {
        $(`#${form_id} #input-example`).text('Пример ввода: ГОНЩИК')
        $(`#${form_id} #task-card-img`).attr('src', '/assets/images/caesar2.png')
        $(`#${form_id} #task-card-note`).text('Русский алфавит (без буквы ё)')
        $(`#${form_id} #training-note`).text('Важно !!! Буква "Ё" не используется! Ответ должен быть в верхнем регистре с соблюдением всех знаков препинания!')
    }
    else if (cipherType === 'vigenere') {
        $(`#${form_id} #input-example`).text('Пример ввода: ГОНЩИК')
        $(`#${form_id} #task-card-img`).attr('src', '/assets/images/vigenere2.jpg')
        $(`#${form_id} #task-card-note`).text('Таблица Виженера')
        $(`#${form_id} #training-note`).text('Важно !!! Буква "Ё" не используется! Ответ должен быть в верхнем регистре с соблюдением всех знаков препинания!')
    }
    else if (cipherType === 'playfair') {
        $(`#${form_id} #input-example`).text('Пример ввода: ГОНЩИКНЕЛЕГАЛЬНЫЙЪ')
        $(`#${form_id} #task-card-img`).attr('src', '/assets/images/caesar2.png')
        $(`#${form_id} #task-card-note`).text('Русский алфавит (без буквы ё)')
        $(`#${form_id} #training-note`).text('Важно !!! Буква "Ё" не используется! Ответ должен быть в верхнем регистре, ' +
            'без лишних символов (знаки препинания, пробелы и т.п.) и написан слитно!')
    }
    else if (cipherType === 'vernam') {

        if (Number(questionType) === Number(1)) {
            $(`#${form_id} #input-example`).text('Пример ввода: 32 74 85 123')
            $(`#${form_id} #training-note`).text('Важно !!! В ответе иных символов, кроме чисел и пробелов для их разделения, быть не должно.')
        }
        else if (Number(questionType) === Number(2)) {
            $(`#${form_id} #input-example`).text('Пример ввода: ГОНЩИК')
            $(`#${form_id} #training-note`).text('Важно !!! Буква "Ё" не используется! Ответ должен быть в верхнем регистре, ' +
                'без лишних символов (знаки препинания, пробелы и т.п.) и написан слитно!')
        }

        $(`#${form_id} #task-card-img`).attr('src', '/assets/images/vernam4.jpg')
        $(`#${form_id} #task-card-note`).text('Вторая половина таблицы кодов ASCII')
    }
}

$(document).ready(function () {

    if (window.location.pathname === '/final-test') {

        $('#final-test-questions').hide()
        $('#final-test-results').hide()

        $.ajax({
            url: '/assets/data/Критерии оценивания.csv',
            dataType: 'text',
        }).done(CreateGradeTable);
    }
})

function CreateGradeTable(data) {

    var allRows = data.split(/\r?\n|\r/);
    var table = '';
    for (var singleRow = 0; singleRow < allRows.length; singleRow++) {
        if (singleRow === 0) {
            table += '<tr>';
        } else {
            table += '<tr>';
        }
        var rowCells = allRows[singleRow].split(',');
        for (var rowCell = 0; rowCell < rowCells.length; rowCell++) {
            if (singleRow === 0) {
                table += '<td>';
                table += rowCells[rowCell];
                table += '</td>';
            } else {
                table += '<td>';
                table += rowCells[rowCell];
                table += '</td>';
            }
        }
        if (singleRow === 0) {
            table += '</tr>';
        } else {
            table += '</tr>';
        }
    }
    $('#grade-table').append(table);
}

$(document).ready(function () {
    if ($('#training').length) {
        CreateTask()
    }
})

$(document).ready(function () {
    $('form#training-form').submit(function(event) {
        event.preventDefault();
        let $form = $(this);
        $.ajax({
            type: $form.attr('method'),
            url: $form.attr('action'),
            data: {
                phrase: $('#phrase').text(),
                key: $('#key').val(),
                userAnswer: $('input[name="userAnswer"]').val(),
                cipherType: $('#cipher-type').val(),
                questionType: $('#question-type').val()
            },
            success: function (data) {
                $("p.training-message").remove();

                if (data.hasOwnProperty('message'))
                {
                    $("<p class='training-message'>"+data.message+"</p>").insertAfter("#training-form");
                }
                else if (data.hasOwnProperty('result')) {

                    $("<p class='training-message'>" + (data.result === true ? "Вы ответили правильно" : "Вы ответили неправильно") + "</p>").insertAfter("#training-form");
                }
                else
                {
                    console.log('error')
                }
            }
        })
    })
})

function CreateTask() {
    $("p.training-message").remove();

    $.ajax({
        type: 'post',
        url: '/create-task-training',
        data: JSON,
        contentType: false,
        cache: false,
        processData: false,
        success: function (data) {

            let pagesArray = ['caesar', 'vigenere', 'playfair', 'vernam']

            let pageName = Object.keys(data)[0]

            if (!pagesArray.includes(pageName)) {
                console.log('WTF')
                return
            }

            $('input[name="userAnswer"]').val('')
            $('#task').text(data[pageName].task)
            $('#phrase').text(data[pageName].phrase)
            $('#key').val(data[pageName].key)

            FillTask(pageName, 'training', data[pageName].questionType)
        }
    })
}

function GetQuestion() {

    console.log('a')

    if ($('#final-test-questions').is(':hidden')) {

        $('#final-test-start').hide()
        $('#final-test-start-button').text('Завершить тестирование')
        $('#final-test-start-button').attr('onclick', 'EndFinalTest()')
        $('#final-test-questions').show()
    }

    $.ajax({
        type: 'post',
        url: '/create-task-final-test',
        data: JSON,
        contentType: false,
        cache: false,
        processData: false,
        success: function (data) {

            if (data) {

                for (let i = 0, questionsArrLength = data.length; i < questionsArrLength; i++) {

                    CreateFinalTestTask(data[i], questionsArrLength)
                }
            }
            else {
                console.log('WTF')
            }
        }
    })
}

function CreateFinalTestTask(question, length) {

    var template = Handlebars.compile( $('#template').html() )
    $('#final-test-questions').append( template() )

    $('#final-test-task-card').attr('id', question.idQuestion)

    $(`#${question.idQuestion} input[name="userAnswer"]`).val('')
    $(`#${question.idQuestion} #final-test-theme`).text(`Вопрос ${((question.idQuestion - 1) % length + 1)}. ${question.theme}`)

    let task = ''

    if (question.idQuestionType === 1) {
        task += 'Зашифруйте фразу с помощью '
    } else if (question.idQuestionType === 2) {
        task += 'Расшифруйте фразу, зашифрованную с помощью '
    }

    if (question.theme === 'Шифр Цезаря') {
        task += `ROT${question.key}:`
        FillTask('caesar', question.idQuestion, question.idQuestionType)
    }
    else if (question.theme === 'Шифр Виженера') {
        task += `ключа "${question.key}":`
        FillTask('vigenere', question.idQuestion, question.idQuestionType)
    }
    else if (question.theme === 'Шифр Плейфера') {
        task += `ключа "${question.key}":`
        FillTask('playfair', question.idQuestion, question.idQuestionType)
    }
    else if (question.theme === 'Шифр Вернама') {
        task += `ключа "${question.key}":`
        FillTask('vernam', question.idQuestion, question.idQuestionType)
    }

    $(`#${question.idQuestion} #task`).text(task)
    $(`#${question.idQuestion} #phrase`).text(question.phrase)
    $(`#${question.idQuestion} #key`).val(question.key)
}

function EndFinalTest() {

    let isIncorrectAnswer = false
    let countCorrectAnswers = 0

    $('.final-test-task-card').each(function(i) {

        let ajax = $.ajax({
            type: $(this).find('.final-test-form').attr('method'),
            url: $(this).find('.final-test-form').attr('action'),
            data: {
                userAnswer: $(this).find('input[name="userAnswer"]').val(),
                phrase: $(this).find('#phrase').text(),
                key: $(this).find('#key').val(),
                cipherType: $(this).find('#cipher-type').val(),
                questionType: $(this).find('#question-type').val()
            },
            success: function (data) {

                if (data) {

                    if (data.hasOwnProperty('message') && !$('#message-popup.modal-wrapper.open').length) {

                        isIncorrectAnswer = true

                        MessagePopup('Ошибка', 'Проверьте правильность ввода')
                    }
                    else if (data.hasOwnProperty('result')) {

                        if (data.result) {

                            countCorrectAnswers++
                        }
                    }
                }
                else {
                    console.log('WTF')
                }
            }
        })

        $.when(ajax).done(function () {

            if (isIncorrectAnswer === true) {

                return
            }
            else {

                if ($('.final-test-task-card').length === i+1) {

                    $('#final-test-questions').hide()
                    $('#final-test-start-button').hide()
                    $('#final-test-results').show()

                    SaveResultsInDb(countCorrectAnswers)

                    ShowResults(countCorrectAnswers, i+1)
                }
            }
        })

        i++
    })
}

function SaveResultsInDb(countCorrectAnswers) {
    $.ajax({
        type: 'post',
        url: '/save-results-in-db',
        data: {
            countCorrectAnswers: countCorrectAnswers
        }
    })
}

function ShowResults(countCorrectAnswers, countQuestions) {

    let percentageOfCorrectAnswers = (countCorrectAnswers / countQuestions) * 100

    let grade = 2

    let arrData = []

    $("#grade-table tr").each(function(){

        let currentRow = $(this)

        let col1_value = currentRow.find("td:eq(0)").text()
        let col2_value = currentRow.find("td:eq(1)").text().split('%')[0]

        let obj = {}

        obj.col1 = col1_value
        obj.col2 = col2_value

        arrData.push(obj)
    })

    arrData.shift()

    if (percentageOfCorrectAnswers >= Number(arrData[0]['col2'])) {

        grade = arrData[0]['col1']
    }
    else if (percentageOfCorrectAnswers >= Number(arrData[1]['col2'])) {

        grade = arrData[1]['col1']
    }
    else if (percentageOfCorrectAnswers >= Number(arrData[2]['col2'])) {

        grade = arrData[2]['col1']
    }

    $('#final-test-results').append(`<p>Процент правильных ответов: ${percentageOfCorrectAnswers}%</p>`)
    $('#final-test-results').append(`<p>Оценка: ${grade}</p>`)
}
$(document).ready(function () {

    $('form#form-create-task-question').submit(function (event) {
        event.preventDefault();

        $.ajax({
            type: $(this).attr('method'),
            url: $(this).attr('action'),
            data: {
                idVariant: $(this).find('select[name="variant"] option:selected').val()
            },
            success: function (data) {
                $("p.questions-message").remove();
                $('#variant-questions').empty()

                if (data.length > 0) {

                    for (let i = 0, questionsArrLength = data.length; i < questionsArrLength; i++) {

                        CreateQuestionTask(data[i], questionsArrLength)
                    }

                    GetAllThemes()
                    GetAllQuestionTypes()

                } else {
                    $("<p class='questions-message'>Заданий не найдено</p>").insertAfter($("form#form-create-task-question"));
                }
            }
        })
    })
})

function GetAllThemes() {
    $.ajax({
        type: 'post',
        url: '/get-all-themes',
        dataType: 'text',
    }).done(FillThemesSelect);
}

function FillThemesSelect(data) {

    if (data.length > 0) {
        let themes = JSON.parse(data)

        for (let i = 0; i < themes.length; i++) {
            $('select[name="question-theme"]').append(`<option value=${themes[i][0]}>${themes[i][1]}</option>`)
        }
    }

    SortSelect("question-theme")
}

function GetAllQuestionTypes() {
    $.ajax({
        type: 'post',
        url: '/get-all-question-types',
        dataType: 'text',
    }).done(FillQuestionTypesSelect);
}

function FillQuestionTypesSelect(data) {

    if (data.length > 0) {
        let questionTypes = JSON.parse(data)

        for (let i = 0; i < questionTypes.length; i++) {
            $('select[name="question-type"]').append(`<option value=${questionTypes[i][0]}>${questionTypes[i][1]}</option>`)
        }
    }

    SortSelect("question-type")
}

function CreateQuestionTask(question, length) {

    let template = Handlebars.compile( $('#question-template').html() )
    $('#variant-questions').append( template() )

    $('#form-question').attr('id', question.idQuestion)

    $(`#${question.idQuestion} #question-num`).text(`Вопрос ${((question.idQuestion - 1) % length + 1)}. ${question.theme}`)

    $(`#${question.idQuestion} #question-phrase`).text(question.phrase)
    $(`#${question.idQuestion} #question-key`).text(question.key)

    $('#select-theme').attr('id', question.idQuestion)
    $('#select-type').attr('id', question.idQuestion)
}

function UpdateQuestion(button) {

    let form = $(button).closest('.form-question')

    let idQuestion = form.attr('id')
    let idTheme = form.find('select[name="question-theme"] option:selected').val()
    let idQuestionType = form.find('select[name="question-type"] option:selected').val()
    let phrase = form.find('textarea#question-phrase').val()
    let key = form.find('textarea#question-key').val()

    if (idTheme === '-') {
        MessagePopup('Ошибка', 'Выберите тему')
        return;
    }

    if (idQuestionType === '-') {
        MessagePopup('Ошибка', 'Выберите тип задания')
        return;
    }

    if ((String(phrase).trim() === '') || (String(key).trim() === '')) {
        MessagePopup('Ошибка', 'Проверьте правильность ввода')
        return;
    }

    $.ajax({
        type: form.attr('method'),
        url: form.attr('action'),
        data: {
            idQuestion: idQuestion,
            idTheme: idTheme,
            idQuestionType: idQuestionType,
            phrase: phrase,
            key: key
        },
        success: function (data) {

            if (data.hasOwnProperty('theme')) {

                form.find('h1#question-num').text(form.find('h1#question-num').text().split('.')[0] + ". " + data.theme)

                MessagePopup('Сообщение', 'Задание успешно изменено')

            }
            else {
                MessagePopup('Ошибка', 'Что-то пошло не так. Перезагрузите страницу')
            }

        }
    })
}

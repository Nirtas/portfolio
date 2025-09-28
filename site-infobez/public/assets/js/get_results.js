$(document).ready(function () {
    GetAllStudents()
    GetAllVariants()
})

function GetAllStudents() {
    $.ajax({
        type: 'post',
        url: '/get-all-students',
        dataType: 'text',
    }).done(FillUsersSelect);
}

function FillUsersSelect(data) {

    if (data.length > 0) {
        let students = JSON.parse(data)

        for (let i = 0; i < students.length; i++) {
            $('select[name="student"]').append(`<option value=${students[i][0]}>${students[i][1]}</option>`)
        }
    }

    SortSelect("student")
}

function GetAllVariants() {
    $.ajax({
        type: 'post',
        url: '/get-all-variants',
        dataType: 'text',
    }).done(FillVariantsSelect);
}

function FillVariantsSelect(data) {

    if (data.length > 0) {
        let variants = JSON.parse(data)

        for (let i = 0; i < variants.length; i++) {
            $('select[name="variant"]').append(`<option value=${variants[i]}>${variants[i]}</option>`)
        }
    }

    SortSelect("variant")
}

function SortSelect(selectName) {

    let options = $(`select[name='${selectName}'] option`);

    options.sort(function (a, b) {
        if (a.text.toUpperCase() > b.text.toUpperCase()) {
            return 1;
        } else if (a.text.toUpperCase() < b.text.toUpperCase()) {
            return -1;
        } else {
            return 0;
        }
    });

    let optionsValues = [];
    let optionsTexts = [];

    $(options).each(function (e) {
        if ($.inArray(options[e].value, optionsValues) > -1) {
            $(options[e]).remove()
        } else {
            optionsValues.push(options[e].value);
            optionsTexts.push(options[e].text);
        }
        e++
    });

    $(`select[name='${selectName}']`).empty()

    for (let i = 0; i < optionsValues.length; i++) {
        $(`select[name='${selectName}']`).append(`<option value="${optionsValues[i]}">${optionsTexts[i]}</option>`)
    }

    $(`select[name='${selectName}']`).prepend('<option selected value="-">-</option>');

}

function CheckDate() {

    let dateTimeMin = $('input[name="date-min"]').val()
    let dateTimeMax = $('input[name="date-max"]').val()

    if (new Date(dateTimeMin) - new Date(dateTimeMax) > 0) {
        $('input[name="date-min"]').val(dateTimeMax)
        $('input[name="date-max"]').val(dateTimeMin)
    }
}

function ResetDateTime(buttonName) {

    if (buttonName === "date-min-reset") {
        $('input[name="date-min"]').val("2010-01-01T00:00")
    } else if (buttonName === "date-max-reset") {
        $('input[name="date-max"]').val("2040-01-01T00:00")
    }
}

$(document).ready(function () {
    $('form#form-results-filter').submit(function (event) {
        event.preventDefault();

        $.ajax({
            type: $(this).attr('method'),
            url: $(this).attr('action'),
            data: {
                idUser: $(this).find('select[name="student"] option:selected').val(),
                idVariant: $(this).find('select[name="variant"] option:selected').val(),
                dateTimeMin: $(this).find('input[name="date-min"]').val(),
                dateTimeMax: $(this).find('input[name="date-max"]').val()
            },
            success: function (data) {
                $("p.results-message").remove();
                $("table#results-table").remove()

                if (data.length > 0) {
                    $("<table id=\"results-table\"></table>").insertAfter($("form#form-results-filter"));
                    FillResultsTable(data)
                } else {
                    $("<p class='results-message'>Результатов не найдено</p>").insertAfter($("form#form-results-filter"));
                }
            }
        })
    })
})

function FillResultsTable(data) {

    let r = []
    let j = -1;

    r[++j] = '<tr><td style="width: 300px">';
    r[++j] = 'Дата сдачи';
    r[++j] = '</td><td style="width: 500px">';
    r[++j] = 'Студент';
    r[++j] = '</td><td style="width: 100px">';
    r[++j] = 'Вариант';
    r[++j] = '</td><td style="width: 300px">';
    r[++j] = 'Кол-во правильных ответов';
    r[++j] = '</td></tr>';

    for (let i = 0, size = data.length; i < size; i++) {
        r[++j] = '<tr><td>';

        let date = new Date(data[i]['Дата сдачи'])

        let year = new Intl.DateTimeFormat('ru', {year: 'numeric'}).format(date)
        let month = new Intl.DateTimeFormat('ru', {month: 'long'}).format(date)
        let day = new Intl.DateTimeFormat('ru', {day: '2-digit'}).format(date)
        day = day.length > 1 ? day : "0" + day

        let hours = new Intl.DateTimeFormat('ru', {hour: '2-digit'}).format(date)
        hours = hours.length > 1 ? hours : "0" + hours

        let minutes = new Intl.DateTimeFormat('ru', {minute: '2-digit'}).format(date)
        minutes = minutes.length > 1 ? minutes : "0" + minutes

        let seconds = new Intl.DateTimeFormat('ru', {second: '2-digit'}).format(date)
        seconds = seconds.length > 1 ? seconds : "0" + seconds

        r[++j] = `${day} ${month} ${year} ${hours}:${minutes}:${seconds}`;
        r[++j] = '</td><td>';
        r[++j] = data[i]['Студент'];
        r[++j] = '</td><td>';
        r[++j] = data[i]['Вариант'];
        r[++j] = '</td><td>';
        r[++j] = data[i]['Кол-во правильных ответов'];
        r[++j] = '</td></tr>';
    }

    $('#results-table').append(r.join(''));
}
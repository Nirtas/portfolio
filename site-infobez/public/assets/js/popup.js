$(document).ready(function () {
    $('#close-auth-popup').on('click', function () {
        AuthPopup();
    });

    $('#close-message-popup').on('click', function () {
        MessagePopup();
    });
});

function AuthPopup() {
    if ($('#auth-popup.modal-wrapper.open').length) {
        MessagePopup('Ошибка', 'Пользоваться сайтом могут только зарегистрированные пользователи');
    }
    else {
        $('#auth-popup').toggleClass('open');
    }
}

function MessagePopup(header, message) {
    $('.popup-header').text(header);
    $('.popup-message').text(message);
    $('#message-popup').toggleClass('open');
}

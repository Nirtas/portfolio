$(function () {
    $(".btn-auth").click(function () {
        $(".form-signin").toggleClass("form-signin-left");
        $(".form-signup").toggleClass("form-signup-left");
        let frameHeight = $("#auth-popup .frame").height() === 370 ? "700px" : "370px"
        $("#auth-popup .frame").height(frameHeight)
        $(".signup-inactive").toggleClass("signup-active");
        $(".signin-active").toggleClass("signin-inactive");
    });
});

$(document).ready(function () {
    $('form#auth-form-authorization, form#auth-form-registration').submit(function (event) {
        event.preventDefault();

        $.ajax ({
            type: $(this).attr('method'),
            url: $(this).attr('action'),
            data: new FormData(this),
            contentType: false,
            cache: false,
            processData: false,
            success: function (data) {
                $("p.message").remove();

                if (data.message)
                {
                    $("<p class='message'>"+data.message+"</p>").insertAfter(".container");
                }
                else
                {
                    location.reload();
                }
            }
        })
    })
})

function CheckLogin() {
    $.ajax({
        type: 'post',
        url: '/check-login',
        data: JSON,
        contentType: false,
        cache: false,
        processData: false,
        success: function (data) {
            if (data.idUser) {

                if (data.idUserType === 1) {
                    $("<li><a href='/final-test' class=\"header-element\">ИТОГОВОЕ ТЕСТИРОВАНИЕ</a></li>").insertAfter($('#theory'));
                }
                else if (data.idUserType === 2) {
                    $("<li><a href=\"/teacher\" class=\"header-element\">ПРЕПОДАВАТЕЛЬСКАЯ</a></li>").insertAfter($('#theory'));
                }

                $('#check-login').append("<a href='#' class='header-element' onclick='Logout()'>ВЫЙТИ</a>")
            }
            else {
                $('#check-login').append("<a href='#' class='header-element' onclick='AuthPopup()'>ВОЙТИ</a>")
                AuthPopup()
            }
        }
    })
}

function Logout() {
    document.cookie = 'user=; Max-Age=-1'
    window.location.replace('/')
}

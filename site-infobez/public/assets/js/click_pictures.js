$(document).ready(function () {
    $('.image').on('click', function() {
        ClickOnPicture(this)
    });
})

function ClickOnPicture(img) {
    $(img).toggleClass('fullsize').find('img').attr('style', '');
}
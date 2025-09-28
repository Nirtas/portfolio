$(document).ready(function () {
    $('.text').find('h2[id]').each(function () {
        $('#table_of_contents').append('<li><a href="#' + $(this).attr('id') + '"><p>' + $(this).text() + '</p></a></li>')
    });
})
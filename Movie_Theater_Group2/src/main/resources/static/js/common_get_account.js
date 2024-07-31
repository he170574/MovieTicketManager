$(document).ready(function (){
    loadAccountData();
})

function loadAccountData(){
    let lsdRing = $('.lsd-ring-container');
    $.ajax({
        url: '/get-account-using',
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            if (response.data === null){    // NO LOGIN
                window.location.href = '/home'
            }else{                          //Logged
                const account = response.data;
                if (account.image !== null){
                    $('#nav-footer-avatar img').attr('src', account.image);
                }
                $('#nav-footer-title').text(account.username);
                $('#nav-footer-subtitle').text(account.role);
                $('#account-score').text(account.score);
            }
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });
}
$(document).ready(function() {
    sendForm();
});
let lsdRing = $('.lsd-ring-container');
function sendForm(){
    $('#form-forgot').on('click',function (e){

        const email = $('#email-forgot-password').val();

        $.ajax({
            url: '/forgot-pass',
            data: email,
            type: 'POST',
            contentType: 'application/json',
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                Swal.fire({
                    title: "Get Password Success",
                    icon: "success",
                    text: "Please ensure check your mail to get password",
                    confirmButtonText: "OK",
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = '/home'
                    }
                });
            },
            error: function (xhr, status, error) {
                console.log('Error:', error);
                Swal.fire({
                    title: "Get Password fail",
                    icon: "error",
                    text: "The email is not exist",
                    confirmButtonText: "OK",
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = '/forgot-password'
                    }
                });
            },
            complete: function (xhr, status) {
                lsdRing.addClass('d-none');
            }
        });
    })
}
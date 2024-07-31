$(document).ready(function () {
    loadAccountData();
});

function openLoginAndRegisterModal() {
    $("#account-logo").on("click", function () {
        Swal.fire({
            background: "#252525",
            color: "white",
            text: "Have a good day!",
            imageUrl:"https://res.cloudinary.com/dbdcejpot/image/upload/v1721152821/tvojvekecudjwcbp14bj.png",
            imageWidth: 200,
            showConfirmButton: true,
            showCancelButton: true,
            confirmButtonText: "Login",
            cancelButtonText: "Register",
            didOpen: (popup) => {
                const btnConfirm = popup.querySelector('.swal2-confirm');
                btnConfirm.setAttribute("data-bs-toggle", "modal");
                btnConfirm.setAttribute("data-bs-target", "#loginModal");

                const btnCancel = popup.querySelector('.swal2-cancel');
                btnCancel.setAttribute("data-bs-toggle", "modal");
                btnCancel.setAttribute("data-bs-target", "#registerModal");
            },
            didClose: () => {
                $('.modal-backdrop').remove();
            },
            scrollbarPadding: false
        });
    });
}

function openAccountModel(fullName){
    $("#account-logo").on("click", function () {
        Swal.fire({
            background: "#252525",
            color: "white",
            text: "Yo! " + fullName,
            imageUrl:"/img/logoNew",
            imageWidth: 200,
            showConfirmButton: true,
            showCancelButton: true,
            confirmButtonText: "Logout",
            cancelButtonText: "Account & Service",
            scrollbarPadding: false
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '/logout';
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                window.location.href = '/account-information';
            }
        });
    });
}

function checkNotEmpty(fieldIds) {
    let allFilled = true;
    fieldIds.forEach(function (fieldId) {
        const valueTag = $('#' + fieldId);
        const value = valueTag.val().trim();
        if (!value) {
            allFilled = false;
            valueTag.addClass('border-danger');
            valueTag.removeClass('border-dark-subtle');
        } else {
            valueTag.removeClass('border-danger');
            valueTag.addClass('border-dark-subtle');
        }
    });
    return allFilled;
}

function isValidEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return emailRegex.test(email);
}

function register() {
    $('#submit-form-register').on('click', function (e) {
        e.preventDefault();

        const formData = {
            username: $('#username-register').val(),
            password: $('#password-register').val(),
            fullName: $('#full-name').val(),
            gender: $('input[name="gender"]:checked').val(),
            dateOfBirth: $('#dob').val(),
            identityCard: $('#identity-card').val(),
            email: $('#email').val(),
            address: $('#address').val(),
            phoneNumber: $('#phone-number').val()
        };
        const fieldIds = ['username-register', 'password-register', 'confirm-password', 'full-name', 'dob', 'email', 'address', 'phone-number'];
        const confirmPasswordTag = $('#confirm-password');

        if (!checkNotEmpty(fieldIds)) {
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Please ensure all input fields are filled out.",
                confirmButtonText: "OK",
            });
            return;
        }

        if (confirmPasswordTag.val() !== formData.password) {
            confirmPasswordTag.addClass('border-danger');
            confirmPasswordTag.removeClass('border-dark-subtle');
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Passwords do not match. Please try again",
                confirmButtonText: "OK",
            });
            return;
        } else {
            confirmPasswordTag.removeClass('border-danger');
            confirmPasswordTag.addClass('border-dark-subtle');
        }

        if (!isValidEmail(formData.email)) {
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Invalid email address. Please enter a properly formatted email.",
                confirmButtonText: "OK",
            });
            $('#email').addClass('border-danger');
            $('#email').removeClass('border-dark-subtle');
            return;
        } else {
            $('#email').removeClass('border-danger');
            $('#email').addClass('border-dark-subtle');
        }

        let lsdRing = $('.lsd-ring-container');
        $.ajax({
            url: '/register',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                Swal.fire({
                    title: "Register Success",
                    icon: "success",
                    text: "Welcome To NEW MOVIE",
                    confirmButtonText: "Let's Go",
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.reload();
                    }
                });
            },
            error: function (xhr, status, error) {
                Swal.fire({
                    title: "Register Fail",
                    icon: "warning",
                    text: "NEW MOVIE F U",
                    confirmButtonText: "Let's Go",
                });
            },
            complete: function () {
                lsdRing.addClass('d-none');
            }
        });
    });
}

function login(){
    $('#send-form-login').on('click', function(event) {
        event.preventDefault();

        let isValidForm = true;
        $('input[name="username"], input[name="password"]').each(function() {
            if ($(this).val() === '') {
                isValidForm = false;
                $(this).addClass('border-danger').removeClass('border-dark-subtle');
            } else {
                $(this).removeClass('border-danger').addClass('border-dark-subtle');
            }
        });
        if (!isValidForm) {
            return;
        }

        const formData = {
            username: $('input[name=username]').val(),
            password: $('input[name=password]').val()
        };

        $.ajax({
            type: 'POST',
            url: '/login',
            data: formData,
            success: function(response) {
                window.location.href = '/home';
            },
            error: function(error) {
                Swal.fire({
                    title: "Login Fail",
                    icon: "error",
                    text: "Invalid Username Or Password.",
                    confirmButtonText: "OK",
                });
            }
        });
    });
}

function loadAccountData() {
    let lsdRing = $('.lsd-ring-container');
    $.ajax({
        url: '/get-account-using',
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            if (response.data === null) {
                openLoginAndRegisterModal();
                register();
                login();
            } else {
                const account = response.data;
                if (account.image !== null) {
                    $('#account-img').attr('src', account.image);
                }
                openAccountModel(account.fullName);
            }
        },
        error: function (xhr, status, error) {
            openLoginAndRegisterModal();
            register();
            login();
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });
}


$(document).ready(function () {
    $('#nav-content').find('.account-information').addClass('active');
    loadData();
    showImagePreview();
    sendChangePasswordForm();
});
let lsdRing = $('.lsd-ring-container');
function loadData() {
    $.ajax({
        url: '/get-account-using',
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            if (response.data === null) {    // NO LOGIN
                window.location.href = '/home'
            } else {                          //Logged
                console.log(response.message);

                const account = response.data;
                if (account.image !== null) {
                    $('#preview-img').attr('src', account.image);
                }
                $('#username').val(account.username);
                $('#fullName').val(account.fullName);
                $('#dob').val(account.dateOfBirth);
                $('#iCard').val(account.identityCard);
                $('#email').val(account.email);
                $('#address').val(account.address);
                $('#phoneNumber').val(account.phoneNumber);
                // Đặt giới tính
                if (account.gender.toLowerCase() === 'male') {
                    $('#male').prop('checked', true);
                } else if (account.gender.toLowerCase() === 'female') {
                    $('#female').prop('checked', true);
                }
            }
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
            updateAccountInformation();
        }
    });
}

function showImagePreview(){
    $('#image').on('change',function (event){
        var file = event.target.files[0];
        var reader = new FileReader();

        reader.onload = function(e) {
            $('#preview-img').attr('src', e.target.result);
        };

        // Đọc file ảnh
        reader.readAsDataURL(file);

        saveImage();
    })
}

function updateAccountInformation(){
    $('#submit-form').on('click',function (){
        const formData = {
            fullName:    $('#fullName').val(),
            gender : $('input[name="gender"]:checked').val(),
            dateOfBirth : $('#dob').val(),
            email: $('#email').val(),
            username: $('#username').val(),
            address: $('#address').val(),
            phoneNumber: $('#phoneNumber').val()
        }

        const fieldIds = ['fullName', 'dob', 'email', 'address', 'address', 'phoneNumber'];
        if (!checkNotEmpty(fieldIds)) {
            Swal.fire({
                title: "Update Fail",
                icon: "warning",
                text: "Please ensure all input fields are filled out.",
                confirmButtonText: "OK",
            });
            return;
        }

        //Check Email
        if (!isValidEmail(formData.email)) {
            Swal.fire({
                title: "Update Fail",
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

        //Check Done => Send data
        Swal.fire({
            title: "You sure want to update?",
            icon: "warning",
            confirmButtonText: "OK",
            showCancelButton: true,
        }).then((result) => {
            if (result.isConfirmed) {
                let lsdRing = $('.lsd-ring-container');
                $.ajax({
                    url: '/update-account',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(formData),
                    beforeSend: function () {
                        lsdRing.removeClass('d-none');
                    },
                    success: function (response) {
                        // Code to run on successful response
                        Swal.fire({
                            title: "Update Success",
                            icon: "success",
                            text: response.message,
                            confirmButtonText: "OK",
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.reload();
                            }
                        });
                    },
                    error: function (xhr, status, error) {
                        // Code to run if the request fails
                        Swal.fire({
                            title: "Update Fail",
                            icon: "error",
                            text: error,
                            confirmButtonText: "OK",
                        });
                    },
                    complete: function () {
                        lsdRing.addClass('d-none');
                    }
                });
            }
        });
    })
}

function checkNotEmpty(fieldIds) {
    let allFilled = true;
    fieldIds.forEach(function (fieldId) {
        const valueTag = $('#' + fieldId);
        const value = valueTag.val();
        if (!value) {
            allFilled = false;
            valueTag.addClass('border-danger')
            valueTag.removeClass('border-dark-subtle')
        } else {
            valueTag.removeClass('border-danger')
            valueTag.addClass('border-dark-subtle')
        }
    });
    return allFilled;
}

function isValidEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return emailRegex.test(email);
}

function saveImage(){
    $('#save-image').on('click', function() {
        Swal.fire({
            title: "You sure want to update new image?",
            icon: "warning",
            confirmButtonText: "OK",
            showCancelButton: true,
        }).then((result) => {
            if (result.isConfirmed) {
                var fileData = $('#image').prop('files')[$('#image').prop('files').length - 1];
                var formData = new FormData();
                formData.append('file', fileData);

                $.ajax({
                    url: '/save-avatar-image',
                    type: 'POST',
                    data: formData,
                    contentType: false,  // Không set Content-Type header
                    processData: false,  // Không xử lý dữ liệu của formData
                    beforeSend:function (){
                        lsdRing.removeClass('d-none');
                    },
                    success: function(response) {
                        Swal.fire({
                            title: "Save success",
                            icon: "success",
                            confirmButtonText: "Yeah!",
                        })
                    },
                    error: function(error) {
                        Swal.fire({
                            title: "Save Fail!",
                            icon: "error",
                            confirmButtonText: "Oh Shit!",
                        })
                        console.error(error);
                    },
                    complete: function (){
                        lsdRing.addClass('d-none');
                    }
                });
            }
        });
    });
}


function sendChangePasswordForm(){
    $("#form-change-pass").click(function() {
        // Lấy giá trị từ các trường input
        var oldPassword = $("#old-pass").val();
        var newPassword = $("#new-pass").val();

        // Tạo một đối tượng chứa dữ liệu để gửi
        var data = {
            oldPassword: oldPassword,
            newPassword: newPassword
        };

        // Sử dụng phương thức jQuery AJAX để gửi dữ liệu đến controller
        $.ajax({
            type: "POST", // Hoặc "GET" tùy thuộc vào phương thức mà controller yêu cầu
            url: "/changePassword", // Đường dẫn tới controller
            contentType: 'application/json',
            data: JSON.stringify(data), // Dữ liệu cần gửi
            beforeSend:function (){
                lsdRing.removeClass('d-none');
            },
            success: function(response) {
                // Xử lý kết quả trả về từ controller nếu cần
                Swal.fire({
                    title: "Change Password Success",
                    icon: "success",
                    confirmButtonText: "OK",
                });
            },
            error: function(error) {
                // Xử lý lỗi nếu có
                Swal.fire({
                    title: "Change Password Fail",
                    icon: "error",
                    text: error.message,
                    confirmButtonText: "OK",
                });
            },
            complete: function (){
                lsdRing.addClass('d-none');
            }
        });
    });

}
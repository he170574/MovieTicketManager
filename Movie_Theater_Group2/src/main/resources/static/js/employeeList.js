$(document).ready(function () {
    loadData(pageNumber, pageSize, searchByEmployeeName);
    changePageSize();
    searchByEmployeeNameFunction();
    uploadForm();
    uploadUpdateForm();
    showImagePreview();
    showEmpInactive();
})
let lsdRing = $('.lsd-ring-container');

let pageSize = 1;
let pageNumber = 0;
let searchByEmployeeName = "";


function loadData(pageNumber, pageSize, searchByEmployeeName) {
    $.ajax({
        url: '/get-all-employee',
        type: 'GET',
        data: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            fullName: searchByEmployeeName
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const table = $('#table-data');
            const data = response.data;
            if (data == null) {
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke Bro :)",
                });
                return;
            }
            const lstEmployee = data.lstEmployee;
            if (lstEmployee === null || lstEmployee.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                table.empty();
                table.append(nullRowTemplate());
                renderPagination(1, 1);
            } else {                          //Has Data

                table.empty();

                $.each(lstEmployee, function (index, value) {
                    table.append(rowTemplate(index + 1, value));
                });

                renderPagination(data.pageNumber, data.totalPage);

            }
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
            Swal.fire({
                title: "Load Data Fail",
                icon: "error",
                text: "Please try later.",
                confirmButtonText: "Oke Bro :)",
            });
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');


            //Form for edit and create new employee
            callEmployeeForm();
            //Delete employee
            deleteEmployee();
        }
    });
}

function showEmpInactive(){
    $('#show-deleted-employee').off('click')
    $('#show-deleted-employee').on('click', function (){
        $.ajax({
            url: '/get-all-employee-inactive',
            type: 'GET',
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                const table = $('#table-data-dEmp');
                const data = response.data;
                if (data == null) {
                    Swal.fire({
                        title: "Load Data Fail",
                        icon: "error",
                        text: "Please try later.",
                        confirmButtonText: "Oke Bro :)",
                    });
                    return;
                }
                const lstEmployee = response.data;
                if (lstEmployee === null || lstEmployee.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                    table.empty();
                    table.append(nullRowTemplate());
                } else {                          //Has Data

                    table.empty();

                    $.each(lstEmployee, function (index, value) {
                        table.append(rowTemplateForEmpDelete(index + 1, value));
                    });


                }
            },
            error: function (xhr, status, error) {
                console.log('Error:', error);
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke Bro :)",
                });
            },
            complete: function (xhr, status) {
                activeEmployee();
                lsdRing.addClass('d-none');
            }
        });
    })
}

function nullRowTemplate() {
    return `
    <tr>
        <td colspan="12">
            <div class="main__table-text">
                <span >No Data</span>
            </div>
        </td>
    </tr>    
    `
}

function rowTemplate(index, value) {
    return `
    <tr>
        <td>
            <div class="main__table-text">
                <span>` + value.accountId + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + value.username + `</span>
            </div>
        </td>
        
        <td>
            <div class="main__table-text main__table-text--green">
                <span class="gender1">` + value.fullName + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + formatDateString(value.dateOfBirth) + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + value.gender + `</span>
            </div>
        </td>
         <td>
            <div class="main__table-text">
                <span>` + value.email + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + value.phoneNumber + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="address">` + value.address + `</span>
            </div>
            <span class="tooltiptext"></span>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + formatDateString(value.registerDate) + `</span>
            </div>
        </td>
        <!-- Action -->
        <td>
            <div class="main__table-btns">
                <span class="main__table-btn main__table-btn--edit editBtn" data-employee-id="` + value.accountId + `" data-bs-target="#information-form" data-bs-toggle="modal">
                    <i class="bi bi-pencil-square"></i>
                </span>
                <span class="btn main__table-btn main__table-btn--delete open-modal delete-employee" data-employee-id="` + value.accountId + `">
                    <i class="bi bi-trash3"></i>
                </span>
            </div>
        </td>
    </tr>`
}
function rowTemplateForEmpDelete(index, value) {
    return `
    <tr>
        <td>
            <div class="main__table-text">
                <span>` + value.accountId + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + value.username + `</span>
            </div>
        </td>
        
        <td>
            <div class="main__table-text main__table-text--green">
                <span class="gender1">` + value.fullName + `</span>
            </div>
        </td>
         <td>
            <div class="main__table-text">
                <span>` + value.email + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span>` + value.phoneNumber + `</span>
            </div>
        </td>
        <!-- Action -->
        <td>
            <div class="main__table-btns">
                <span class="main__table-btn main__table-btn--edit activeBtn" style="display: inline-block;
    width: 70px;" data-employee-id="` + value.accountId + `" >
                    <i class="bi bi-emoji-smile-fill"></i>
                    Active
                </span>
                
            </div>
        </td>
    </tr>`
}
function formatDateString(dateString) {
    // Parse the string into a Date object
    var date = new Date(dateString);

    // Extract the parts of the date
    var day = date.getDate().toString().padStart(2, '0'); // Day (with leading zeros)
    var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month (with leading zeros, January is 0!)
    var year = date.getFullYear(); // Year

    // Format the date as "dd/mm/yyyy hh:mm"
    return day + '/' + month + '/' + year;
}

function renderPagination(pageNumber, totalPage) {
    let paginationRoot = $('.pagination');
    paginationRoot.empty();
    let html = '<li class="page-item" data-page-number="' + (pageNumber === 0 ? pageNumber : pageNumber - 1) + '"><span class="page-link">Previous</span></li>';

    for (var i = 0; i < totalPage; i++) {
        if (i === pageNumber) {
            html += '<li class="page-item" data-page-number="' + i + '"><span class="page-link active">' + (i + 1) + '</span></li>';
        } else {
            html += '<li class="page-item" data-page-number="' + i + '"><span class="page-link">' + (i + 1) + '</span></li>';
        }
    }

    html += '<li class="page-item" data-page-number="' + (pageNumber === totalPage - 1 ? pageNumber : pageNumber + 1) + '"><span class="page-link">Next</span></li>';

    paginationRoot.html(html);
    changePagination();
}

function changePagination() {
    let paginationRoot = $('.pagination');
    paginationRoot.off('click', '.page-item');
    paginationRoot.on('click', '.page-item', function () {
        pageNumber = $(this).data('page-number');
        loadData(pageNumber, pageSize, searchByEmployeeName);
    })
}

//Change Page Size
function changePageSize() {
    let pageSizeButton = $('#pageSize');
    pageSizeButton.off('change');
    pageSizeButton.on('change', function () {
        pageSize = $(this).val();
        pageNumber = 0;
        loadData(pageNumber, pageSize, searchByEmployeeName);
    })
}

function searchByEmployeeNameFunction() {
    let searchEmployeeButton = $('#search');
    searchEmployeeButton.off('click');
    searchEmployeeButton.on('click', function () {
        searchByEmployeeName = $('#search-value').val().trim();
        pageNumber = 0;
        loadData(pageNumber, pageSize, searchByEmployeeName);
    })
}

//Add new employee
function callEmployeeForm() {
    $('#addNew').on('click', function () {
        $('#modal-head').text("Add New Employee");
        $('#submit-form').removeClass('d-none');
        $('#submit-update-form').addClass('d-none');
        $('#password').attr('placeholder', '');
        $('#confirm-password').attr('placeholder', '')
        clearForm()
    });

    $('.editBtn').on('click', function () {
        $('#modal-head').text("Edit Employee Information");
        clearForm();
        $('#submit-form').addClass('d-none');
        $('#submit-update-form').removeClass('d-none');
        $('#password').attr('placeholder', 'Enter new password here');
        $('#confirm-password').attr('placeholder', 'Re new new password')
        //Fill information into form
        fillFormByEmployeeId($(this).data('employee-id'));
    });
}

function clearForm() {
    $('#username').val('');
    $('#password').val('');
    $('#confirm-password').val('');
    $('#full-name').val('');
    $('#dob').val('');
    $('#identity-card').val('');
    $('#email').val('');
    $('#address').val('');
    $('#phone-number').val('');
    $('input[name="gender"][value="Male"]').prop('checked', true);
    //$('#account-image').val('/'); // Đặt lại trường chọn file
    $('#preview-account-image').attr('src', '/img/user_img_default.png');
}

//Edit Employee
function fillFormByEmployeeId(employeeId) {
    $.ajax({
        url: '/get-employee-by-id',
        type: 'GET',
        data: {
            employeeId: employeeId
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const data = response.data;
            if (data == null) {
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Can't get this employee information!",
                    confirmButtonText: "Oke Bro :)",
                });
            } else {
                $('#username').val(data.username);
                $('#password').val('');
                $('#confirm-password').val('');
                $('#full-name').val(data.fullName);
                $('#dob').val(data.dateOfBirth);
                $('#identity-card').val(data.identityCard);
                $('#email').val(data.email);
                $('#address').val(data.address);
                $('#phone-number').val(data.phoneNumber);
                $('input[name="gender"][value="' + data.gender + '"]').prop('checked', true);
                //$('#account-image').val('/'); // Đặt lại trường chọn file
                $('#preview-account-image').attr('src', data.image == null ? '/img/user_img_default.png' : data.image);
            }

        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
            Swal.fire({
                title: "Load Data Fail",
                icon: "error",
                text: "Please try later.",
                confirmButtonText: "Oke Bro :)",
            });
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });
}

function uploadUpdateForm() {
    $('#submit-update-form').off('click');
    $('#submit-update-form').on('click', function () {
        var formData = new FormData();
        formData.append('username', $('#username').val());
        formData.append('password', $('#password').val());
        formData.append('fullName', $('#full-name').val());
        formData.append('gender', $('input[name="gender"]:checked').val());
        formData.append('dateOfBirth', $('#dob').val());
        formData.append('identityCard', $('#identity-card').val());
        formData.append('email', $('#email').val());
        formData.append('address', $('#address').val());
        formData.append('phoneNumber', $('#phone-number').val());
        formData.append('imgFile', $('#account-image').prop('files').length === 0 ? new File([], 'empty.txt') : $('#account-image').prop('files')[$('#account-image').prop('files').length - 1]); // Chỉ gửi file cuối cùng
        const fieldIds = ['username', 'full-name', 'dob', 'identity-card', 'email', 'address', 'phone-number']; // Add all your input field IDs here
        const confirmPasswordTag = $('#confirm-password');


        if (!checkNotEmpty(fieldIds)) {
            Swal.fire({
                title: "Update Fail",
                icon: "warning",
                text: "Please ensure all input fields are filled out.",
                confirmButtonText: "OK",
            });
            return;
        }
        //Check Password
        if (confirmPasswordTag.val() !== $('#password').val()) {
            confirmPasswordTag.addClass('border-danger')
            confirmPasswordTag.removeClass('border-dark-subtle')
            Swal.fire({
                title: "Update Fail",
                icon: "warning",
                text: "Passwords do not match. Please try again",
                confirmButtonText: "OK",
            });
            return;
        } else {
            confirmPasswordTag.removeClass('border-danger')
            confirmPasswordTag.addClass('border-dark-subtle')
        }
        //Check Email
        if (!isValidEmail($('#email').val())) {
            Swal.fire({
                title: "Update Fail",
                icon: "warning",
                text: "Invalid email address. Please enter a properly formatted email.",
                confirmButtonText: "OK",
            });
            $('#email').addClass('border-danger')
            $('#email').removeClass('border-dark-subtle')
            return;
        } else {
            $('#email').removeClass('border-danger')
            $('#email').addClass('border-dark-subtle')
        }

        //Check Done => Send data
        $.ajax({
            url: '/update-employee',
            type: 'POST',
            contentType: false,
            processData: false,
            data: formData,
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                // Code to run on successful response
                Swal.fire({
                    title: "Update Success",
                    icon: "success",
                    text: response.message,
                    confirmButtonText: "Oke",
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.reload();
                    }
                });
            },
            error: function (xhr, status, errorThrown) {
                // Lấy thông tin lỗi từ phản hồi
                let errorMessage = xhr.responseText;

                // Phân tích JSON nếu phản hồi là JSON
                try {
                    let responseJson = JSON.parse(xhr.responseText);
                    errorMessage = responseJson.message || "Internal Error. Please try later";
                } catch (e) {
                    console.error("Error parsing JSON response:", e);
                }

                // Hiển thị thông báo lỗi
                Swal.fire({
                    title: "Add Fail",
                    icon: "error",
                    text: errorMessage,
                    confirmButtonText: "Let's Go",
                });

                // Log lỗi ra console
                console.log('Error:', errorThrown);
            },
            complete: function () {
                lsdRing.addClass('d-none');
            }
        });


    });
}

function showImagePreview() {
    $('#account-image').on('change', function (event) {
        var file = event.target.files[0];
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#preview-account-image').attr('src', e.target.result);
        };

        // Đọc file ảnh
        reader.readAsDataURL(file);
    })
}

//Upload form for create New employee
function uploadForm() {
    $('#submit-form').off('click');
    $('#submit-form').on('click', function () {
        var formData = new FormData();
        formData.append('username', $('#username').val());
        formData.append('password', $('#password').val());
        formData.append('fullName', $('#full-name').val());
        formData.append('gender', $('input[name="gender"]:checked').val());
        formData.append('dateOfBirth', $('#dob').val());
        formData.append('identityCard', $('#identity-card').val());
        formData.append('email', $('#email').val());
        formData.append('address', $('#address').val());
        formData.append('phoneNumber', $('#phone-number').val());
        formData.append('imgFile', $('#account-image').prop('files').length == 0 ? new File([], "empty.txt") : $('#account-image').prop('files')[$('#account-image').prop('files').length - 1]); // Chỉ gửi file đầu tiên
        const fieldIds = ['username', 'password', 'confirm-password', 'full-name', 'dob', 'identity-card', 'email', 'address', 'phone-number']; // Add all your input field IDs here
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
        //Check Password
        if (confirmPasswordTag.val() !== $('#password').val()) {
            confirmPasswordTag.addClass('border-danger')
            confirmPasswordTag.removeClass('border-dark-subtle')
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Passwords do not match. Please try again",
                confirmButtonText: "OK",
            });
            return;
        } else {
            confirmPasswordTag.removeClass('border-danger')
            confirmPasswordTag.addClass('border-dark-subtle')
        }
        //Check Email
        if (!isValidEmail($('#email').val())) {
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Invalid email address. Please enter a properly formatted email.",
                confirmButtonText: "OK",
            });
            $('#email').addClass('border-danger')
            $('#email').removeClass('border-dark-subtle')
            return;
        } else {
            $('#email').removeClass('border-danger')
            $('#email').addClass('border-dark-subtle')
        }

        //Check Done => Send data
        $.ajax({
            url: '/add-employee',
            type: 'POST',
            contentType: false,
            processData: false,
            data: formData,
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                // Code to run on successful response
                Swal.fire({
                    title: "Add Success",
                    icon: "success",
                    text: response.message,
                    confirmButtonText: "Oke",
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.reload();
                    }
                });
            },
            error: function (xhr, status, errorThrown) {
                // Lấy thông tin lỗi từ phản hồi
                let errorMessage = xhr.responseText;

                // Phân tích JSON nếu phản hồi là JSON
                try {
                    let responseJson = JSON.parse(xhr.responseText);
                    errorMessage = responseJson.message || "Internal Error. Please try later";
                } catch (e) {
                    console.error("Error parsing JSON response:", e);
                }

                // Hiển thị thông báo lỗi
                Swal.fire({
                    title: "Add Fail",
                    icon: "error",
                    text: errorMessage,
                    confirmButtonText: "Let's Go",
                });

                // Log lỗi ra console
                console.log('Error:', errorThrown);
            },
            complete: function () {
                lsdRing.addClass('d-none');
            }
        });


    });
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

//Delete employee
function deleteEmployee() {
    $('.delete-employee').off('click');
    $('.delete-employee').on('click', function () {
        var employeeId = $(this).data('employee-id');
        Swal.fire({
            title: "Notification",
            icon: "warning",
            text: 'Do you want to delete employee has id: ' + employeeId,
            showCancelButton: true
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/delete-employee-by-id',
                    type: 'POST',
                    data: {employeeId: employeeId},
                    beforeSend: function () {
                        lsdRing.removeClass('d-none');
                    },
                    success: function (response) {
                        // Code to run on successful response
                        Swal.fire({
                            title: "Add Success",
                            icon: "success",
                            text: response.message,
                            confirmButtonText: "Oke",
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.reload();
                            }
                        });
                    },
                    error: function (xhr, status, errorThrown) {
                        // Lấy thông tin lỗi từ phản hồi
                        let errorMessage = xhr.responseText;

                        // Phân tích JSON nếu phản hồi là JSON
                        try {
                            let responseJson = JSON.parse(xhr.responseText);
                            errorMessage = responseJson.message || "Internal Error. Please try later";
                        } catch (e) {
                            console.error("Error parsing JSON response:", e);
                        }

                        // Hiển thị thông báo lỗi
                        Swal.fire({
                            title: "Add Fail",
                            icon: "error",
                            text: errorMessage,
                            confirmButtonText: "Let's Go",
                        });

                        // Log lỗi ra console
                        console.log('Error:', errorThrown);
                    },
                    complete: function () {
                        lsdRing.addClass('d-none');
                    }
                });
            }
        });
    });
}


function activeEmployee() {
    $('.activeBtn').off('click');
    $('.activeBtn').on('click', function () {
        var employeeId = $(this).data('employee-id');
        Swal.fire({
            title: "Notification",
            icon: "warning",
            text: 'Do you want to active employee has id: ' + employeeId,
            showCancelButton: true
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/active-employee-by-id',
                    type: 'POST',
                    data: {employeeId: employeeId},
                    beforeSend: function () {
                        lsdRing.removeClass('d-none');
                    },
                    success: function (response) {
                        // Code to run on successful response
                        Swal.fire({
                            title: "active Success",
                            icon: "success",
                            text: response.message,
                            confirmButtonText: "Oke",
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.reload();
                            }
                        });
                    },
                    error: function (xhr, status, errorThrown) {
                        // Lấy thông tin lỗi từ phản hồi
                        let errorMessage = xhr.responseText;

                        // Phân tích JSON nếu phản hồi là JSON
                        try {
                            let responseJson = JSON.parse(xhr.responseText);
                            errorMessage = responseJson.message || "Internal Error. Please try later";
                        } catch (e) {
                            console.error("Error parsing JSON response:", e);
                        }

                        // Hiển thị thông báo lỗi
                        Swal.fire({
                            title: "Add Fail",
                            icon: "error",
                            text: errorMessage,
                            confirmButtonText: "Let's Go",
                        });

                        // Log lỗi ra console
                        console.log('Error:', errorThrown);
                    },
                    complete: function () {
                        lsdRing.addClass('d-none');
                    }
                });
            }
        });
    });
}
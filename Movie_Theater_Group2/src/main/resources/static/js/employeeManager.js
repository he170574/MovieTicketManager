$(document).ready(function () {
    loadData(pageNumber, pageSize, fullName);
    onChangeRowLimit();
    onSearchByEmployeeFullName();
    showImagePreview();

})
let spinner = $('.lsd-ring-container');

spinner.show = function() {
    spinner.removeClass('d-none');
}

spinner.hide = function() {
    spinner.addClass('d-none');
};

let pageSize = 10;
let pageNumber = 0;
let fullName = "";
let status = "";
let role = "ROLE_EMPLOYEE,ROLE_ADMIN";
let recent = null;

function onFilterStatus(select) {
    console.log("OK");
    status = select.options[select.selectedIndex].value;
    console.log("status", status);
    loadData();
}

function loadData() {
    let data = {
        pageNumber: pageNumber,
        pageSize: pageSize,
    };
    if (fullName) data["fullName"] = fullName;
    if (status) data["status"] = status;
    if (role) data["role"] = role;
    $.ajax({
        url: '/get-all-account',
        type: 'GET',
        data: data,
        beforeSend: function () {
            spinner.show();
        },
        success: function (response) {
            const table = $('#table-data');
            const data = response.data;
            if (data == null) {
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Close",
                });
                return;
            }
            const result = data.result;
            if (result === null || result.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                table.empty();
                table.append(renderEmptyRow());
                renderPagination(1, 1);
            } else {

                table.empty();
                $.each(result, function (index, value) {
                    table.append(renderRow(index + 1, value));
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
                confirmButtonText: "Close",
            });
        },
        complete: function (xhr, status) {
            spinner.hide();
        }
    });
}

function onDeactivateEmployee(element) {
    let accountId = $(this).data("account-id");
}

function renderEmptyRow() {
    return `
        <tr>
            <td colspan="12">
                <div>
                    <span >No Data</span>
                </div>
            </td>
        </tr>    
    `
}
function renderStatus(value) {
    let text = value.deleted ? "INACTIVE" : "ACTIVE";
    let css = value.deleted ? "badge bg-danger" : "badge bg-primary";
    return `<div class="${css}">${text}</div>`
}
function renderAction(value) {
    let actionButton = "";
    if (!value.deleted) {
        actionButton = `
            <span onclick="deactivateEmployee(this)" class="btn main__table-btn main__table-btn--delete open-modal delete-account-button" data-account-id="${value.accountId}">
                <i class="bi bi-trash3"></i>
            </span>
        `
    } else {
        actionButton = `
            <span onclick="activateEmployee(this)" class="btn main__table-btn main__table-btn--edit open-modal activate-account-button" data-account-id="${value.accountId}">
                <i class="bi bi-box-arrow-up"></i>
            </span>
        `
    }

    return `
        <div style="display: flex">
            <span onclick="onOpenEditEmployeeForm(this)" class="main__table-btn main__table-btn--edit edit-account-button" data-account-id="${value.accountId}" data-bs-target="#edit-account-modal" data-bs-toggle="modal">
                <i class="bi bi-pencil-square"></i>
            </span>
            ${actionButton}
        </div>
    `;
}
function renderAvatar(value) {
    let src = value.image ? value.image : "/img/user_img_default.png";
    return `
        <img src="${src}" style="object-fit: cover; width: 50px; height: 50px;">
    `
}
function renderRole(value) {
    let text = value.role.slice(5);
    return `<div class="text-uppercase small badge bg-dark">${text}</div>`;
}
function renderRow(index, value) {
    console.log("value", value);
    return `
    <tr>
        <td>
            ${renderAvatar(value)}
        </td>
        <td>
            <div>
                <span>${value.username}</span>
            </div>
        </td>
       
        <td>
            <div>
                <span>${value.fullName}</span>
            </div>
        </td>
        <td>
            <div>
                <span>${formatDateString(value.dateOfBirth)}</span>
            </div>
        </td>
        <td>
            <div>
                <span>${value.gender}</span>
            </div>
        </td>
         <td>
            <div>
                <span>${value.email}</span>
            </div>
        </td>
        <td>
            <div>
                <span>${value.phoneNumber}</span>
            </div>
        </td>
        <td>
            <div>
                <span>${value.address}</span>
            </div>
        </td>
        <td>
            <div>
                <span>${formatDateString(value.registerDate)}</span>
            </div>
        </td>
        <td>${renderRole(value)}</td>
        <td>
            ${renderStatus(value)}
        </td>
        <!-- Action -->
        <td>
            ${renderAction(value)}
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
    onChangePagination();
}

function onChangePagination() {
    let paginationRoot = $('.pagination');
    paginationRoot.off('click', '.page-item');
    paginationRoot.on('click', '.page-item', function () {
        pageNumber = $(this).data('page-number');
        loadData();
    })
}

function onChangeRowLimit() {
    let rowLimitButton = $('#rowLimit');
    rowLimitButton.off('change');
    rowLimitButton.on('change', function () {
        pageSize = $(this).val();
        pageNumber = 0;
        loadData();
    })

    $("#roleSelect").on('change', function() {
        role = $(this).val();
        pageNumber = 0;
        loadData();
    })
}

function onSearchByEmployeeFullName() {
    let searchEmployeeButton = $('#search');
    searchEmployeeButton.off('click');
    searchEmployeeButton.on('click', function () {
        fullName = $('#search-value').val().trim();
        pageNumber = 0;
        loadData();
    })
}

function clearForm() {
    $("form input:not([name=gender])").val("");
    $('input[name="gender"][value="Male"]').prop('checked', true);
    $('form img').attr('src', '/img/user_img_default.png');
    $("form span").text("");
}

(function ($) {
    let debounce = null;
    let form = document.forms.createEmployeeForm;
    let fullName = form.fullName.trim();
    let username = form.username.trim();
    $(fullName).on('input', function (e) {
        clearTimeout(debounce);
        debounce = setTimeout(function () {
            fetch("/generate-username?fullName=" + decodeURIComponent(fullName.value))
                .then(response => response.json())
                .then(result => {
                    if (result.data) {
                        username.value = result.data;
                    }
                });
        }, 150);
    });
})(jQuery);


// Fill Information in Edit Employee Modal
function fillFormByEmployeeId(accountId) {
    $.ajax({
        url: '/get-account-info-by-id',
        type: 'GET',
        data: {
            id: accountId
        },
        beforeSend: function () {
            spinner.show();
        },
        success: function (response) {
            const data = response.data;
            if (data == null) {
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Can't get this employee information!",
                    confirmButtonText: "Close",
                });
            } else {
                console.log(data);
                let form = document.forms.editEmployeeForm
                $(form.username).val(data.username);
                $(form.password).val('');
                $(form.confirmPassword).val('');
                $(form.fullName).val(data.fullName);
                $(form.dateOfBirth).val(data.dateOfBirth);
                $(form.email).val(data.email);
                $(form.address).val(data.address);
                form.role.options[data.role].selected = true;
                $(form.phoneNumber).val(data.phoneNumber);
                $(form.image).val(data.image);
                $('input[name="gender"][value="' + data.gender + '"]').prop('checked', true);
                $('.image-preview').attr('src', data.image ? '/img/user_img_default.png' : data.image);
            }

        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
            Swal.fire({
                title: "Load Data Fail",
                icon: "error",
                text: "Please try later.",
                confirmButtonText: "Close",
            });
        },
        complete: function (xhr, status) {
            spinner.hide();
        }
    });
}


function onOpenAddEmployeeForm() {
    clearForm();
    $("#createEmployeeForm input").on("change", function(event) {
        $(this).siblings("span").text("");
    })
}
function onOpenEditEmployeeForm(element) {
    let accountId = $(element).data("account-id");
    clearForm();
    fillFormByEmployeeId(accountId);
    document.forms.editEmployeeForm.accountId.value = accountId;
}
function onSubmitEditEmployeeForm() {
    let form = document.forms.editEmployeeForm;
    $("form span").text("");
    $("form input").each(function() {
        let val = $(this).val().trim();
        $(this).val(val)
    })
    let formData = new FormData(form);
    $.ajax({
        url: '/update-account-info',
        type: 'POST',
        contentType: false,
        processData: false,
        data: formData,
        beforeSend: function () {
            spinner.show();
        },
        success: function (response) {
            Swal.fire({
                title: "Update Employee Successfully!",
                icon: "success",
                text: response.message,
                confirmButtonText: "Close",
            }).then((result) => {
                if (result.isConfirmed) {
                    console.log(response.data);
                    recent = response.data;
                    $("#edit-account-modal").modal("hide");
                    loadData();
                }
            });
        },
        error: function (xhr, status, errorThrown) {
            let errorMessage = xhr.responseText;
            try {
                let responseJson = JSON.parse(xhr.responseText);
                errorMessage = responseJson.data.message || responseJson.message;
                console.log(responseJson);
                for (let [key, value] of Object.entries(responseJson.data)) {
                    let element = form[key];
                    $(element).siblings("span").text(value);
                }
            } catch (e) {
                console.error("Error parsing JSON response:", e);
            }
            Swal.fire({
                title: "Update Employee Failed!",
                icon: "error",
                text: errorMessage,
                confirmButtonText: "Close",
            });
            console.log('Error:', errorThrown);
        },
        complete: function () {
            spinner.hide();
        }
    })
}
function onSubmitCreateEmployee() {
    let form = document.forms.createEmployeeForm;
    $("form span").text("");
    $("form input").each(function() {
        let val = $(this).val().trim();
        $(this).val(val)
    })
    let formData = new FormData(form);
    $.ajax({
        url: '/create-account',
        type: 'POST',
        contentType: false,
        processData: false,
        data: formData,
        beforeSend: function () {
            spinner.show();
        },
        success: function (response) {
            Swal.fire({
                title: "Create New Employee Successfully!",
                icon: "success",
                text: response.message,
                confirmButtonText: "Close",
            }).then((result) => {
                if (result.isConfirmed) {
                    console.log(response.data);
                    recent = response.data;
                    $("#create-account-modal").modal("hide");
                    loadData();
                }
            });
        },
        error: function (xhr, status, errorThrown) {
            let errorMessage = xhr.responseText;
            try {
                let responseJson = JSON.parse(xhr.responseText);
                errorMessage = responseJson.data.message || responseJson.message;
                console.log(responseJson);
                for (let [key, value] of Object.entries(responseJson.data)) {
                    let element = form[key];
                    $(element).siblings("span").text(value);
                }
            } catch (e) {
                console.error("Error parsing JSON response:", e);
            }
            Swal.fire({
                title: "Create New Employee Failed!",
                icon: "error",
                text: errorMessage,
                confirmButtonText: "Close",
            });
            console.log('Error:', errorThrown);
        },
        complete: function () {
            spinner.hide();
        }
    })
}
function showImagePreview() {
    $('input[name=imgFile]').on('change', function (event) {
        var file = event.target.files[0];
        var reader = new FileReader();

        reader.onload = function (e) {
            $('.image-preview').attr('src', e.target.result);
        };

        // Đọc file ảnh
        reader.readAsDataURL(file);
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

function deactivateEmployee(element) {
    let accountId = $(element).data('account-id');
    Swal.fire({
        title: "Notification",
        icon: "warning",
        text: 'Do you want to deactivate account with id: ' + accountId,
        showCancelButton: true
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/deactivate-account',
                type: 'POST',
                data: {
                    id: accountId
                },
                beforeSend: function () {
                    spinner.show();
                },
                success: function (response) {
                    // Code to run on successful response
                    Swal.fire({
                        title: "Deactivate Employee Successfully!",
                        icon: "success",
                        text: response.message,
                        confirmButtonText: "Close",
                    }).then((result) => {
                        if (result.isConfirmed) {
                            loadData();
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
                        title: "Deactivate Employee Fail",
                        icon: "error",
                        text: errorMessage,
                        confirmButtonText: "Close",
                    });

                    // Log lỗi ra console
                    console.log('Error:', errorThrown);
                },
                complete: function () {
                    spinner.hide();
                }
            });
        }
    })
}

function activateEmployee(element) {
    let accountId = $(element).data('account-id');
    Swal.fire({
        title: "Notification",
        icon: "warning",
        text: 'Do you want to activate account with id: ' + accountId,
        showCancelButton: true
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/activate-account-by-id',
                type: 'POST',
                data: {
                    id: accountId
                },
                beforeSend: function () {
                    spinner.show();
                },
                success: function (response) {
                    // Code to run on successful response
                    Swal.fire({
                        title: "Activate Employee Successfully!",
                        icon: "success",
                        text: response.message,
                        confirmButtonText: "Close",
                    }).then((result) => {
                        if (result.isConfirmed) {
                            loadData();
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
                        title: "Activate Employee Fail",
                        icon: "error",
                        text: errorMessage,
                        confirmButtonText: "Close",
                    });

                    // Log lỗi ra console
                    console.log('Error:', errorThrown);
                },
                complete: function () {
                    spinner.hide();
                }
            });
        }
    })
}
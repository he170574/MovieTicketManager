$(document).ready(function () {
    showImagePreview();
    loadData(pageNumber, pageSize, searchByFoodName);
    changePageSize();
    searchByFoodNameFunction();
    updateFood();
});
let pageSize = 10;
let pageNumber = 0;
let searchByFoodName = "";

let lsdRing = $('.lsd-ring-container');

function openFoodForm() {
    $('#addNew').off('click');
    $('#addNew').on('click', function () {
        clearForm();
        $('#modal-head').text("Add New Food");
        $('#submit-form').removeClass('d-none');
        $('#submit-update-form').addClass('d-none');
        uploadNewFood();
    });

    $('.edit-food').off('click');
    $('.edit-food').on('click', function () {
        $('#modal-head').text("Edit Food Information");
        $('#submit-form').addClass('d-none');
        $('#submit-update-form').removeClass('d-none');
        clearForm();

        //Edit food
        const foodId = $(this).data('food-id');
        showFoodDetailByFoodId(foodId);
    });
}

function showImagePreview() {
    $('#food-image').on('change', function (event) {
        let file = event.target.files[0];
        let reader = new FileReader();

        reader.onload = function (e) {
            $('#preview-food-image').attr('src', e.target.result);
        };

        // Đọc file ảnh
        reader.readAsDataURL(file);
    })
}

function clearForm() {
    $('#food-name').val('');
    $('#food-price').val('');
    $('#food-image').val('');
    $('#food-size').val('');
    $('#preview-food-image').attr('src', '/img/food_img_default.jpg');
}

function uploadNewFood() {
    $('#submit-form').off('click');
    $('#submit-form').on('click', function () {
        var formData = new FormData();
        formData.append('foodName', $('#food-name').val());
        formData.append('foodSize', $('#food-size').val());
        formData.append('foodPrice', $('#food-price').val());
        formData.append('foodImageFile', $('#food-image').prop('files').length == 0 ? new File([], "empty.txt") : $('#food-image').prop('files')[$('#food-image').prop('files').length - 1]); // Chỉ gửi file đầu tiên

        const fieldIds = ['food-name', 'food-size', 'food-price', 'food-image']; // Add all your input field IDs here

        if (!checkNotEmpty(fieldIds)) {
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Please ensure all input fields are filled out.",
                confirmButtonText: "OK",
            });
            return;
        }

        //Check Price
        var foodPrice = formData.get('foodPrice');
        // Chuyển đổi giá trị thành số để kiểm tra
        var foodPriceValue = parseFloat(foodPrice);
        if (isNaN(foodPriceValue) || foodPriceValue < 0) {
            $('#food-price').addClass('border-danger')
            $('#food-price').removeClass('border-dark-subtle')
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Please enter valid price",
                confirmButtonText: "OK",
            });
            return;
        } else {
            $('#food-price').removeClass('border-danger')
            $('#food-price').addClass('border-dark-subtle')
        }

        //Check Done => Send data
        $.ajax({
            url: '/add-food',
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

//Load List
function loadData(pageNumber, pageSize, searchByFoodName) {
    $.ajax({
        url: '/get-all-food',
        type: 'GET',
        data: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            foodName: searchByFoodName
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const productContainer = $('#all-product');
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
            const lstFood = data.lstFood;
            if (lstFood === null || lstFood.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                productContainer.empty();
                renderPagination(1, 1);
            } else { //Has Data
                productContainer.empty();
                $.each(lstFood, function (index, value) {
                    productContainer.append(productTemplate(index + 1, value));
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
            openFoodForm();
            deleteFood();
        }
    });
}

function productTemplate(index, value) {
    return `
    <div class="col-3 p-3">
        <div class="card rounded  bg-dark" style="width: 15rem;overflow: hidden">
            <img src="${value.foodImage}" class="rounded movie-small-img bg-dark" style="width: 100%;"
                 alt="">
                <div class="card-body border-0 text-light px-4 bg-dark">
                    <h3 class="card-title  fw-bold movie-name">${value.foodName}</h3>
                    <div class="d-flex justify-content-between">
                        <p class="card-text pt-2 col me-3">
                            Size:
                            <span class="fw-bold movie-type">${value.foodSize === "null" || value.foodSize == null ? 'No Size' : value.foodSize}</span>
                        </p>
                        <p class="card-text pt-2 col">
                            Price:
                            <span class="fw-bold movie-type">${value.foodPrice}.0 VNĐ</span>
                        </p>
                    </div>
                    <div class="d-flex justify-content-between">
                        <button class="btn btn-success col me-3 edit-food" data-food-id="${value.foodId}" data-bs-target="#food-form" data-bs-toggle="modal">Update</button>
                        <button class="btn btn-danger col delete-food" data-food-id="${value.foodId}">Delete</button>
                    </div>
                </div>
        </div>
    </div>`
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
        loadData(pageNumber, pageSize, searchByFoodName);
    })
}

function changePageSize() {
    let pageSizeButton = $('#pageSize');
    pageSizeButton.off('change');
    pageSizeButton.on('change', function () {
        pageSize = $(this).val();
        pageNumber = 0;
        loadData(pageNumber, pageSize, searchByFoodName);
    })
}

function searchByFoodNameFunction() {
    let searchFoodButton = $('#search');
    searchFoodButton.off('click');
    searchFoodButton.on('click', function () {
        searchByFoodName = $('#search-value').val().trim();
        pageNumber = 0;
        loadData(pageNumber, pageSize, searchByFoodName);
    })
}

function showFoodDetailByFoodId(foodId) {
    $.ajax({
        url: '/get-food-by-id',
        type: 'GET',
        data: {
            foodId: foodId
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
                $('#food-name').val(data.foodName);
                $('#food-price').val(data.foodPrice);
                $('#food-size').val(data.foodSize);
                $('#preview-food-image').attr('src', data.foodImage == null ? '/img/food_img_default.jpg' : data.foodImage);
                $('#submit-update-form').attr('data-food-id',data.foodId)
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

function updateFood(){
    const updateButton = $('#submit-update-form');
    updateButton.off('click');
    updateButton.on('click',function (){
        var formData = new FormData();
        formData.append('foodName', $('#food-name').val());
        formData.append('foodSize', $('#food-size').val());
        formData.append('foodPrice', $('#food-price').val());
        formData.append('foodImageFile', $('#food-image').prop('files').length == 0 ? new File([], "empty.txt") : $('#food-image').prop('files')[$('#food-image').prop('files').length - 1]); // Chỉ gửi file đầu tiên
        formData.append('foodId',$(this).data('food-id'));

        const fieldIds = ['food-name', 'food-size', 'food-price']; // Add all your input field IDs here

        if (!checkNotEmpty(fieldIds)) {
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Please ensure all input fields are filled out.",
                confirmButtonText: "OK",
            });
            return;
        }

        //Check Price
        var foodPrice = formData.get('foodPrice');
        // Chuyển đổi giá trị thành số để kiểm tra
        var foodPriceValue = parseFloat(foodPrice);
        if (isNaN(foodPriceValue) || foodPriceValue < 0) {
            $('#food-price').addClass('border-danger')
            $('#food-price').removeClass('border-dark-subtle')
            Swal.fire({
                title: "Register Fail",
                icon: "warning",
                text: "Please enter valid price",
                confirmButtonText: "OK",
            });
            return;
        } else {
            $('#food-price').removeClass('border-danger')
            $('#food-price').addClass('border-dark-subtle')
        }

        //Check Done => Send data
        $.ajax({
            url: '/update-food',
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
    })
}

function deleteFood(){
    const deleteButton = $('.delete-food');
    deleteButton.off('click');
    deleteButton.on('click',function (){
        const foodId = $(this).data('food-id');
        const foodName = $(this).closest('.card').find('.card-title').text();
        Swal.fire({
            title: "Notification",
            icon: "warning",
            text: 'Do you want to delete ' + foodName,
            showCancelButton:true
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/delete-food-by-id',
                    type: 'GET',
                    data: {
                        foodId: foodId
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
                            $('#food-name').val(data.foodName);
                            $('#food-price').val(data.foodPrice);
                            $('#food-size').val(data.foodSize);
                            $('#preview-food-image').attr('src', data.foodImage == null ? '/img/food_img_default.jpg' : data.foodImage);
                            $('#submit-update-form').attr('data-food-id',data.foodId)
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
        });

    })
}
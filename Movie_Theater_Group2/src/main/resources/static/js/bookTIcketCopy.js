const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);

const movieId = urlParams.get('movieId');

$(document).ready(function () {


    bookTicket();
    loadMovieSchedule();
    loadFood();
    loadMovie();

})

let scheduleTime = '';
let schedule = [];

let lsdRing = $('.lsd-ring-container');
function loadMovie(){
    $.ajax({
        url: '/get-movie-details',
        type: 'GET',
        data: {
            movieId: movieId
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            console.log(response)
            if (response.data !== null) {
                let data = response.data;
                $('#movie-image').attr('src',data.smallImage);
                $('#movie-name').text(data.movieName);
            } else {
                Swal.fire({
                    title: "No Data",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke Bro :)",
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
            lsdRing.addClass('d-none');
        }
    });
}
//Load movie Schedule
function loadMovieSchedule() {

    $.ajax({
        url: '/get-movie-schedule-by-movie-id',
        type: 'GET',
        data: {
            movieId: movieId
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            console.log(response)
            if (response.data !== null && response.data.length > 0) {
                schedule = response.data;
                scheduleTime = schedule[0].scheduleTime;
                renderDate();
            } else {
                Swal.fire({
                    title: "No Data",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke Bro :)",
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
            lsdRing.addClass('d-none');
        }
    });


}

function renderDate() {
    let dateContainer = $('#container-date');
    dateContainer.empty();

    // Đối tượng để lưu trữ các ngày duy nhất
    let uniqueDates = {};

    $.each(schedule, function (index, value) {
        // Định dạng ngày từ thời gian lịch trình
        let formattedDate = formatDateString(value.scheduleTime);

        // Kiểm tra xem ngày đã được thêm vào chưa
        if (!uniqueDates[formattedDate]) {
            // Nếu chưa, thêm vào uniqueDates và dateContainer
            uniqueDates[formattedDate] = true;

            if (index === 0) {
                dateContainer.append(`
                    <label class="me-3 date" data-schedule-time="` + value.scheduleTime + `">
                        <input type="radio" name="movieDate" checked="">
                        <span>` + formattedDate + `</span>
                    </label>
                `);
            } else {
                dateContainer.append(`
                    <label class="me-3 date" data-schedule-time="` + value.scheduleTime + `">
                        <input type="radio" name="movieDate">
                        <span>` + formattedDate + `</span>
                    </label>
                `);
            }
        }
    });

    changeDate();
    // Gọi hàm renderTime với ngày đầu tiên (nếu có)
    renderTime();
    $('#money').text('00.0');
}

function changeDate() {
    $('.date').off('click')
    $('.date').on('click', function () {
        scheduleTime = $(this).data('schedule-time');
        renderTime();
        $('#money').text('00.0');
    })
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

function renderTime() {
    let timeContainer = $('#container-time');
    timeContainer.empty();
    let isFirstDate = true;
    $.each(schedule, function (index, value) {
        if (formatDateString(value.scheduleTime) === formatDateString(scheduleTime)) {
            if (isFirstDate) {
                timeContainer.append(`
                    <label class="me-3 time" data-schedule-time="` + value.scheduleTime + `">
                        <input type="radio" name="radio" checked="">
                            <span>${formatDateTimeString(value.scheduleTime)}</span>
                    </label>
                `)
                isFirstDate = false;
            } else {
                timeContainer.append(`
                    <label class="me-3 time" data-schedule-time="` + value.scheduleTime + `">
                        <input type="radio" name="radio">
                        <span>${formatDateTimeString(value.scheduleTime)}</span>
                    </label>
                `)
            }
        }
    })
    onchangeTime();
    // renderSeat();
    renderSeat();
    $('#money').text('00.0');
}

function formatDateTimeString(dateString) {
    // Parse the string into a Date object
    var date = new Date(dateString);

    // Extract the parts of the date
    var hour = date.getHours().toString().padStart(2, '0'); // Hour (with leading zeros)
    var minute = date.getMinutes().toString().padStart(2, '0'); // Minute (with leading zeros)

    // Format the date as "dd/mm/yyyy hh:mm"
    return hour + ':' + minute;
}

function onchangeTime() {
    $('.time').off('click')
    $('.time').on('click', function () {
        scheduleTime = $(this).data('schedule-time');
        renderSeat();
        $('#money').text('00.0');
    })
}

function renderSeat() {
    $.ajax({
        url: '/get-seat-for-booking-ticket',
        type: 'GET',
        data: {
            movieId: movieId,
            scheduleTime: scheduleTime
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            let data = response.data;
            let html = '';
            let currentRow = null;

            // Duyệt mảng data
            data.forEach(function (item) {
                // Kiểm tra xem có phải bắt đầu hàng mới không
                if (item.seatRow !== currentRow) {
                    // Nếu không phải hàng đầu tiên, đóng thẻ div của hàng trước
                    if (currentRow !== null) {
                        html += `</div>`;
                    }
                    // Mở thẻ div mới cho hàng mới
                    html += `<div class="d-flex">`;
                    currentRow = item.seatRow;
                }

                // Tạo thẻ div cho mỗi ghế
                html += `
                <div class="checkbox-wrapper-10 mb-2 me-2">
                     <input type="checkbox" id="${item.scheduleSeatId}" class="tgl tgl-flip seat ${item.seatType === 1 ? 'vip-seat' : '' } ${item.seatStatus ? 'buyed-seat' : ''} ${item.deleted ? 'disabled-seat' : ''}" ${item.seatStatus || item.deleted ? 'disabled' : ''}    data-schedule-seat-id="${item.scheduleSeatId}" data-schedule-seat-price="${item.price}">
                    <label for="${item.scheduleSeatId}" data-tg-on="${item.seatRow}${item.seatColumn}" data-tg-off="${item.seatRow}${item.seatColumn}" class="tgl-btn seat-label ${item.seatStatus ? 'seat-selected' : ''} "></label>
                </div>
            `;
            });

            // Đóng thẻ div của hàng cuối cùng
            if (currentRow !== null) {
                html += `</div>`;
            }

            // Thêm chuỗi html vào #seat-item
            $('#seat-item').empty();
            $('#seat-item').html(html);
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
            selectSeat();
        }
    });

}

let totalSeatPrice = 0;
function selectSeat() {
    // Gỡ bỏ sự kiện 'change' cũ để tránh lắng nghe sự kiện nhiều lần
    $('.seat').off('change');

    // Lắng nghe sự kiện 'change' trên các input có class 'seat'
    $('.seat').on('change', function () {
        const inputChecked = $(this).is(':checked'); // Kiểm tra xem input đã được check chưa
        const seatPrice = $(this).data('schedule-seat-price'); // Lấy giá của ghế

        var currentMoney = parseFloat($('#money').text().replace(/[^\d]/g, '')); // Lấy số tiền hiện tại

        // Cập nhật tổng giá tiền dựa trên việc input được check hay không
        if (inputChecked) {
            totalSeatPrice = totalSeatPrice + seatPrice;
            $('#money').text((currentMoney + seatPrice).toLocaleString('vi-VN'));
        } else {
            totalSeatPrice = totalSeatPrice - seatPrice;
            $('#money').text((currentMoney - seatPrice).toLocaleString('vi-VN'));
        }
    });
}

function loadFood(){
    $.ajax({
        url: '/get-all-food',
        type: 'GET',
        data: {
            pageNumber : 0,
            pageSize : 9999,
            foodName : ''
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const table = $("#food-container");
            const data = response.data;
            if (data == null) {
                // Swal.fire({
                //     title: "Load Data Fail",
                //     icon: "error",
                //     text: "Please try later.",
                //     confirmButtonText: "Oke Bro :)",
                // });
                return;
            }
            const lstFood = data.lstFood;
            if (lstFood === null || lstFood.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                table.empty();
            } else {                          //Has Data

                table.empty();

                $.each(lstFood, function (index, value) {
                    table.append(foodCardTemplate( value));
                });
            }
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
            // Swal.fire({
            //     title: "Load Data Fail",
            //     icon: "error",
            //     text: "Please try later.",
            //     confirmButtonText: "Oke Bro :)",
            // });
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
            validateInputFoodValue();
        }
    });
}
function foodCardTemplate(value){
    return`
    <div class="col-3">
        <div class="card shadow-lg bg-secondary text-light" style="width: 18rem;">
            <img src="${value.foodImage}" class="card-img-top pt-3"
                 alt="...">
                <div class="card-body">
                    <h5 class="card-title fw-semibold">${value.foodName}</h5>
                    <p>Price: ${Intl.NumberFormat('vi-VN', {style: 'currency',currency: 'VND',}).format(value.foodPrice)}</p>
                    <select class="form-select mb-3 bg-dark text-light border border-dark" style="width: 105px;">
                        <option selected>${value.foodSize === 'null' ? '----' : value.foodSize}</option>
                    </select>
                    <div class="btn-group" role="group w-50" aria-label="Basic example">
                        <button type="button" class="btn btn-dark decrease"> -</button>
                        <input type="text" class="btn btn-dark food-input" data-food-id="${value.foodId}" data-food-price="${value.foodPrice}" style="width: 40px;" value="0">
                        <button type="button" class="btn btn-dark increase"> +</button>
                    </div>
                </div>
        </div>
    </div>`
}
let totalFoodPrice = 0;
function validateInputFoodValue() {
    // Khởi tạo biến tổng giá tiền ban đầu
    var totalFoodPrice = 0;

    // Hàm để cập nhật tổng giá tiền
    function updateTotalPrice() {
        totalFoodPrice = totalSeatPrice; // Reset giá trị tổng về 0 trước khi tính toán lại
        $('.food-input').each(function () {
            var price = $(this).data('food-price');
            var quantity = parseInt($(this).val(), 10);
            totalFoodPrice += price * quantity;
        });
        $('#money').text(totalFoodPrice.toLocaleString('vi-VN'));
    }

    $('.increase, .decrease').click(function() {
        // Tìm ô input tương ứng dựa trên vị trí của button
        var input = $(this).hasClass('increase') ? $(this).prev('.food-input') : $(this).next('.food-input');
        var value = parseInt(input.val(), 10);
        // Cập nhật giá trị của input tương ứng
        input.val($(this).hasClass('increase') ? value + 1 : Math.max(value - 1, 0));
        // Cập nhật tổng giá tiền sau mỗi click
        updateTotalPrice();
    });

    $('.food-input').change(function() {
        var value = parseInt($(this).val(), 10);
        // Đảm bảo giá trị nhập vào là hợp lệ
        if (value < 0 || isNaN(value)) {
            $(this).val(0);
        }
        // Cập nhật tổng giá tiền sau mỗi thay đổi
        updateTotalPrice();
    });
}




function bookTicket() {
    $('#booking-ticket').on('click', function () {
        // Mảng để lưu thông tin các ghế được chọn
        var checkedSeats = $('.seat:checked').map(function () {
            return $(this).data('schedule-seat-id');
        }).get();

        // Kiểm tra xem người dùng có chọn ghế không
        if (checkedSeats.length === 0) {
            Swal.fire({
                title: "Please Select Seat",
                icon: "error",
                text: "You haven't selected any seats.",
                confirmButtonText: "Ok"
            });
            return;
        }

        // Mảng để lưu thông tin các món ăn được chọn
        var inputsList = $('.food-input').map(function () {
            var value = parseInt($(this).val(), 10);
            if (value > 0) {
                return {
                    foodId: $(this).data('food-id'),
                    quantity: value
                };
            }
        }).get().filter(Boolean); // Loại bỏ các giá trị falsy (undefined, null, etc.)

        // Object chứa thông tin đặt vé và đặt thức ăn
        var bookingData = {
            seats: checkedSeats,
            foods: inputsList
        };
        $.ajax({
            url: '/book-ticket', // URL của endpoint trong Spring Boot
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(bookingData), // Chuyển object thành JSON
            success: function (response) {
                Swal.fire({
                    title: "Booking Successful",
                    icon: "success",
                    confirmButtonText: "Ok"
                }).then((result) => {
            if (result.isConfirmed) {
                var amount = $('#money').text()
                window.location.href = `/payment-gateways?invoiceId=${response.data}&amount=${amount}`;
            }
        });


            },
            error: function (error) {
                Swal.fire({
                    title: "Booking Failed",
                    icon: "error",
                    text: error.responseText || "An error occurred during booking.",
                    confirmButtonText: "Ok"
                });
                // Có thể xử lý lỗi tại đây
            }
        });
    });


}
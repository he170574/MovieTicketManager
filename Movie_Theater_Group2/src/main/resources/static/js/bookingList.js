$(document).ready(function () {
    $('#search-by-date').val(formatDateToYYYYMMDD(date));
    console.log("ready")
    loadAllRoom();
    loadData();
    searchByInvoiceItemIdFunction();
    changePageSize();
    sortByStatus();
    changeDateRenderMovieSchedule();
});

let lsdRing = $('.lsd-ring-container');
let ticketStatus = '';
let pageNumber = 0;
let pageSize = 5;
let searchByInvoiceItemId = '';
let roomName = null;
let roomId;
let date = new Date();

function sortByStatus() {
    let ticketStatusSelect = $('#ticketstatus');
    ticketStatusSelect.off('change');
    ticketStatusSelect.on('change', function () {
        ticketStatus = $(this).val();
        pageNumber = 0; // Reset page number
        loadData();
    });
}

function loadAllRoom() {
    console.log("call loadAllRoom")
    $.ajax({
        url: '/get-all-cinema-room',
        type: 'GET',
        success: function (response) {
            let result = "<option selected disabled>Select Room</option>";
            for (let room of response["data"]) {
                result += `<option value="${room.id}">${room.cinemaRoomName}</option>`;
            }
            $("#allroom").html(result);
            $("#allroom").change(function() {
                let roomName = $("#allroom").find(":selected").text();
                $("#selected-room-name").text(roomName);
                roomId = $("#allroom").find(":selected").val();
                loadData()

                $(".progress-stacked").html(`
                <div class="progress empty-schedule" role="progressbar" aria-label="Segment one"
                     aria-valuenow="0"
                     aria-valuemin="0" aria-valuemax="1440"
                     data-movie-id="">
                    <div class="progress-bar bg bg-dark">
                    </div>
                </div>
                `);
                getScheduleByRoomIdAndDay();
            });
        },
        error: function (xhr, status, error) {
            console.error(error);
        }
    });
}


function attachCheckboxClickHandlers() {
    let statusLabel = $('.status-label');
    statusLabel.off('click');
    statusLabel.on('click',function (event){
        Swal.fire({
            title: "Are you sure want to update status for this ticket",
            icon: "warning",
            confirmButtonText: "Continue Update",
            showCancelButton: true,
            cancelButtonText: "Cancel"
        }).then((result) => {
            if (result.isConfirmed) {
                const invoiceItemId = $(this).data('invoice-item-id');
                let inputId = $(this).attr('for');
                let inputElement = $('#' + inputId);
                const isChecked = inputElement.is(':checked');
                console.log(isChecked)
                $.ajax({
                    url: '/admin/update-status',
                    type: 'POST',
                    data: {
                        invoiceItemId: invoiceItemId,
                        status: !inputElement.is(':checked')
                    },
                    beforeSend: function () {
                        lsdRing.removeClass('d-none');
                    },
                    success: function(response) {
                        Swal.fire({
                            title: "Update Success",
                            icon: "success",
                            confirmButtonText: "Oke",
                        }).then((result2) => {
                            if (result2.isConfirmed) {
                                inputElement.prop('disabled', false)
                                inputElement.prop('checked', !isChecked);
                                inputElement.prop('disabled', true)
                            }
                        });
                        loadData()
                    },
                    error: function(xhr, status, error) {
                        console.error('Failed to update status for item', invoiceItemId, ':', error);
                    },
                    complete: function (xhr, status) {
                        lsdRing.addClass('d-none');
                    }
                });
            }
        });
    })
}

let lstBookingTicket = [];
function loadData() {
    let data = {
        pageNumber: pageNumber,
        pageSize: pageSize,
    }
    if (searchByInvoiceItemId) {
        data["searchByInvoiceItemId"] = searchByInvoiceItemId;
    }
    if (roomId) {
        data["roomId"] = roomId;
    }
    if (ticketStatus) {
        data["ticketStatus"] = ticketStatus
    }
    $.ajax({
        url: '/admin/get-booking-ticket',
        data: data,
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            let table = $('#table-data');
            const data = response.data;
            if(data == null){
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke",
                });
                return;
            }
            lstBookingTicket = data.lstBookingTicket;

            if (lstBookingTicket === null || lstBookingTicket.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {
                table.empty();
                table.append(nullRowTemplate());
                renderPagination(1, 1);
            } else {
                table.empty();
                $.each(lstBookingTicket, function (index, value) {
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
                confirmButtonText: "Oke",
            });
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
            updateCheckboxStatus(lstBookingTicket);
            attachCheckboxClickHandlers();
        }
    });
}

function nullRowTemplate() {
    return `
    <tr>
        <td colspan="10">
            <div class="main__table-text">
                <span >No Data</span>
            </div>
        </td>
    </tr>    
    `
}
function rowTemplate(index, value) {
    return `
    <tr id="bookingListContainer">
                            <!-- id -->
                            <td>
                                <div class="main__table-text">
                                    <span class="id1" >` + index + `</span>
                                </div>
                            </td>
                             <td>
                                <div class="main__table-text">
                                    <span class="id1" >` + value.invoiceItemId + `</span>
                                </div>
                            </td>
                            <!-- basic info -->
                            <td>
                                <div class="main__user">
                                    <div class="main__meta">
                                        <span>` + value.movieName + `</span>
                                    </div>
                                </div>
                            </td>
                            <!-- === -->
                            <td>
                                <div class="main__user">
                                    <div class="main__meta">
                                        <span>` + formatDateStringToDate(value.scheduleTime) + `</span>
                                    </div>
                                </div>
                            </td>
                             <td>
                                <div class="main__user">
                                    <div class="main__meta">
                                        <span>` + formatDateStringToTime(value.scheduleTime) + `</span>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <div class="main__table-text">
                                    <span >` + value.cinemaRoomName + `</span>
                                </div>
                            </td>
                            <td>
                                <div class="main__table-text">
                                    <span >` + value.seat + `</span>
                                </div>
                            </td>
                            <!-- Action -->
                            <td style="background-color: #3b3b3b !important;" class="pe-5">
                                <div class="checkbox-wrapper-10">
                                    <input type="checkbox" id="id-${value.invoiceItemId}" class="tgl tgl-flip status-input "  disabled>
                                    <label for="id-${value.invoiceItemId}" data-tg-on="CHECKED" data-tg-off="UNCHECK" class="tgl-btn status-label" data-invoice-item-id="${value.invoiceItemId}"></label>
                                </div>

                            </td>
                        </tr>
    `
}

function updateCheckboxStatus(lstBookingTicket) {
    $.each(lstBookingTicket, function (index, value){
        const checkbox = $("#id-" + value.invoiceItemId);

        if (value.status) {
            checkbox.prop('disabled', false)
            checkbox.prop('checked', true)
            checkbox.prop('disabled', true)
        } else {
            checkbox.prop('disabled', false)
            checkbox.prop('checked', false)
            checkbox.prop('disabled', true)
        }
    });
}

function formatDateToYYYYMMDD(date) {
    var yyyy = date.getFullYear();
    var mm = (date.getMonth() + 1).toString().padStart(2, '0'); // Lấy tháng và thêm '0' nếu cần
    var dd = date.getDate().toString().padStart(2, '0'); // Lấy ngày và thêm '0' nếu cần

    mm = mm.toString().padStart(2, '0');
    dd = dd.toString().padStart(2, '0');

    return `${yyyy}-${mm}-${dd}`;
}
function convertDateTimeStringToMinutes(dateTimeString) {
    var timePart = dateTimeString.split('T')[1]; // Lấy phần thời gian
    var hours = parseInt(timePart.split(':')[0]); // Giờ
    var minutes = parseInt(timePart.split(':')[1]); // Phút

    // Chuyển đổi giờ thành phút và cộng với phút
    return hours * 60 + minutes;
}
function convertDateTimeStringToTime(dateTimeString) {
    var dateTimeObject = new Date(dateTimeString);
    var hours = dateTimeObject.getHours();
    var minutes = dateTimeObject.getMinutes();
    return (hours < 10 ? '0' : '') + hours + ':' + (minutes < 10 ? '0' : '') + minutes;
}

function formatDateStringToDate(dateString){
    // Parse the string into a Date object
    var date = new Date(dateString);

    // Extract the parts of the date
    var day = date.getDate().toString().padStart(2, '0'); // Day (with leading zeros)
    var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month (with leading zeros, January is 0!)
    var year = date.getFullYear(); // Year
    var hour = date.getHours().toString().padStart(2, '0'); // Hour (with leading zeros)
    var minute = date.getMinutes().toString().padStart(2, '0'); // Minute (with leading zeros)

    // Format the date as "dd/mm/yyyy hh:mm"
    return day + '/' + month + '/' + year ;
}
function formatDateStringToTime(dateString){
    // Parse the string into a Date object
    var date = new Date(dateString);

    // Extract the parts of the date
    var day = date.getDate().toString().padStart(2, '0'); // Day (with leading zeros)
    var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month (with leading zeros, January is 0!)
    var year = date.getFullYear(); // Year
    var hour = date.getHours().toString().padStart(2, '0'); // Hour (with leading zeros)
    var minute = date.getMinutes().toString().padStart(2, '0'); // Minute (with leading zeros)

    // Format the date as "dd/mm/yyyy hh:mm"
    return  hour + ':' + minute;
}



function editMovieSchedule() {
    let scheduleElement = $(".schedule-element");
    scheduleElement.off('click');
    scheduleElement.on('click', function () {
        $('#headerContent').html('Update Movie Schedule');

        $('#submit-update-movie-schedule').removeClass('d-none');
        $('#submit-delete-movie-schedule').removeClass('d-none');
        $('#submit-create-movie-schedule').addClass('d-none');

        let roomId = $(this).data('room-id');
        let scheduleId = $(this).data('schedule-id');
        $.ajax({
            url: '/get-movie-schedule-room-by-room-id-and-schedule',
            type: 'GET',
            data: {
                roomId: roomId,
                scheduleId: scheduleId,
            },
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                let data = response.data[0];
                console.log(data)
                $('#roomChosenName').html(data.cinemaRoomName);
                $('#add-schedule-date').val(formatDateToYYYYMMDD(new Date(data.scheduleTime)));
                $('#add-schedule-start-time').val(convertDateTimeStringToTime(data.scheduleTime)).removeClass('is-invalid');
                $('#movieChosenName').html(data.movieName).removeClass('is-invalid');
                $('#movieChosenDuration').html(data.movieDuration).removeClass('is-invalid');
                $('#submit-update-movie-schedule').attr('data-room-id', data.cinemaRoomId);
                $('#submit-delete-movie-schedule').attr('data-movie-id', data.movieId).attr('data-schedule-id', data.scheduleId).attr('data-cinema-room-id',data.cinemaRoomId);
                $('#add-schedule-seat-normal-price').val(Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND',
                }).format(data.priceNormalSeat)).removeClass('is-invalid');

                $('#add-schedule-seat-vip-price').val(Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND',
                }).format(data.priceVipSeat)).removeClass('is-invalid');
                movieScheduleMovieId = data.movieId;
            },
            error: function (xhr, status, error) {
                console.log('Error:', error);
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke Bro 🙂",
                });
            },
            complete: function (xhr, status) {
                lsdRing.addClass('d-none');
                movieNameForSearch = '';
                pageNumber = 0;
                $('#list-movie-container').empty();
                $('#search-movie-input').val('');
                loadMovie(movieNameForSearch, pageNumber);

            }
        });
    });
}

function deleteMovieSchedule(){
    let deleteBtn = $('#submit-delete-movie-schedule');
    deleteBtn.off('click');
    deleteBtn.on('click',function (){
        Swal.fire({
            title: "Are you sure want to delete this movie schedule",
            icon: "warning",
            text: "The user may booked ticket",
            confirmButtonText: "Continue Update",
            showCancelButton: true,
            cancelButtonText: "Cancel"
        }).then((result) => {
            $.ajax({
                url: '/admin/delete-movie-schedule',
                type: 'POST',
                contentType: 'application/json',
                dataType: "json",
                data: JSON.stringify({  movieId : $(this).data('movie-id'),
                    scheduleId: $(this).data('schedule-id'),
                    cinemaRoomId: $(this).data('cinema-room-id')
                }),
                beforeSend: function () {
                    lsdRing.removeClass('d-none');
                },
                success: function (response) {
                    Swal.fire({
                        title: "Delete Movie Schedule Success",
                        icon: "success",
                        confirmButtonText: "Ok",
                    }).then((result) => {
                        if (result.isConfirmed) {

                            window.location.reload();

                        }
                    });

                }, error: function (xhr, status, error) {
                    let errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "An error occurred";
                    Swal.fire({
                        title: errorMsg, // Sử dụng thông điệp lỗi làm tiêu đề
                        icon: "error",
                        confirmButtonText: "Ok",
                    });
                }, complete: function (xhr, status) {
                    lsdRing.addClass('d-none');
                }
            });

        });
    })
}

function getScheduleByRoomIdAndDay() {
    console.log("call getScheduleByRoomIdAndDay")
    $.ajax({
        url: '/get-movie-schedule-room-by-room-id-and-day',
        type: 'GET',
        data: {
            roomId: roomId,
            date: formatDateToYYYYMMDD(date)
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            let scheduleList = response.data;
            console.log(scheduleList)
            scheduleList.forEach(function (value) {
                renderMovieSchedule(value);
            })
        }, error: function (xhr, status, error) {
            let errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "An error occurred";
            Swal.fire({
                title: errorMsg, // Sử dụng thông điệp lỗi làm tiêu đề
                icon: "error",
                confirmButtonText: "Ok",
            });
        }, complete: function (xhr, status) {
            lsdRing.addClass('d-none');
            editMovieSchedule();
            deleteMovieSchedule();
        }
    });
}

// Function to render movie schedule
function renderMovieSchedule(value) {
    let startTimeMinus = convertDateTimeStringToMinutes(value.scheduleTime);
    let scaleNum = value.movieDuration / 14.4;
    if (startTimeMinus + value.movieDuration > 1440) {
        scaleNum = (1440 - startTimeMinus) / 14.4;
    }
    var html = `
            <div class="progress movie-in-schedule h-100 border border-2 border-dark schedule-element" 
                data-bs-target="#add-movie-schedule" 
                data-bs-toggle="modal" 
                data-schedule-id="${value.scheduleId}"
                data-room-id="${value.cinemaRoomId}"
                role="progressbar" 
                aria-label="Segment one"
                aria-valuemin="${startTimeMinus}" 
                aria-valuemax="${(startTimeMinus + value.movieDuration) > 1440 ? 1440 : (startTimeMinus + value.movieDuration)}"
                style="width: ${scaleNum}%; height: 100%"
                data-movie-id="${value.movieId}">
                <div class="progress-bar bg bg-danger">
                    <div tabIndex="0" class="btn movie-time-in-schedule fw-bold" role="button"
                        style="height: 100%; width: 100%; font-size: 13px"
                        data-bs-custom-class="custom-tooltip"
                        data-bs-html="true"
                        data-bs-toggle="tooltip" 
                        data-bs-title="
                            <div class='card border-dark bg-dark text-white' style='width: 25rem; opacity:1'>
                                <img src='${value.movieSmallImage}' class='card-img-top' alt='...'>
                                <div class='card-body'>
                                    <h5 class='card-title'>${value.movieName}</h5>
                                    <p class='card-text'>Start Time: ${convertDateTimeStringToTime(value.scheduleTime)}</p>
                                    <p class='card-text'>End Time: ${convertDateTimeStringToTime(new Date(new Date(value.scheduleTime).getTime() + value.movieDuration * 60000))}</p>
                                    <p class='card-text'>Duration: ${value.movieDuration} min</p>
                                    <p class='card-text'>VIP Price: ${value.priceVipSeat} min</p>
                                    <p class='card-text'>Normal Price: ${value.priceNormalSeat} min</p>
                                    <button class='btn btn-primary'>Go somewhere</button>
                                </div>
                            </div>"    
                        data-bs-content="">
                        ${convertDateTimeStringToTime(value.scheduleTime)}
                    </div>
                </div>
            </div>`;

    let cinemaRoomSchedule = $('.schedule');
    let blankSchedule = cinemaRoomSchedule.find('.empty-schedule').filter(function () {
        let areaValueMin = $(this).attr('aria-valuemin');
        let areaValueMax = $(this).attr('aria-valuemax');
        return (value.movieDuration) <= (areaValueMax - areaValueMin) &&
            (startTimeMinus >= areaValueMin) &&
            ((startTimeMinus + value.movieDuration <= areaValueMax) || startTimeMinus + value.movieDuration > 1440);
    });

    if (blankSchedule.length === 0 || blankSchedule > 1) {
        Swal.fire({
            icon: "error",
            title: "Something went wrong!",
            text: "Can't load movie schedule",
        });
        return;
    }

    let blankScheduleBefore = blankSchedule.clone();
    let blankScheduleAfter = blankSchedule.clone();

    blankScheduleBefore.attr('aria-valuemax', startTimeMinus);
    blankScheduleBefore.width((blankScheduleBefore.attr('aria-valuemax') - blankScheduleBefore.attr('aria-valuemin')) / 14.4 + "%");

    blankScheduleAfter.attr('aria-valuemin', (startTimeMinus + value.movieDuration) > 1440 ? 1440 : (startTimeMinus + value.movieDuration));
    blankScheduleAfter.width((blankScheduleAfter.attr('aria-valuemax') - blankScheduleAfter.attr('aria-valuemin')) / 14.4 + "%");

    blankSchedule.after(blankScheduleAfter);
    blankSchedule.after(html);
    blankSchedule.after(blankScheduleBefore);

    blankSchedule.remove();

    //     // Add click event for movie schedule
    //     $(document).on('click', .progress.movie-in-schedule[data-schedule-id="${value.scheduleId}"][data-room-id="${value.cinemaRoomId}"], function () {
    //         let scheduleId = $(this).data('schedule-id');
    //         let roomId = $(this).data('room-id');
    //
    //         // AJAX request to get booking tickets
    //         $.ajax({
    //             url: '/get-booking-tickets-by-schedule',
    //             type: 'GET',
    //             data: {
    //                 scheduleSeatHis: scheduleId,
    //                 roomId: roomId
    //             },
    //             beforeSend: function () {
    //                 lsdRing.removeClass('d-none');
    //             },
    //             success: function (response) {
    //                 lstBookingTicket = response.data;
    //                 loadData();
    //             },
    //             error: function (xhr, status, error) {
    //                 console.log('Error:', error);
    //                 Swal.fire({
    //                     title: "Load Data Fail",
    //                     icon: "error",
    //                     text: "Failed to load booking tickets.",
    //                     confirmButtonText: "OK",
    //                 });
    //             },
    //             complete: function () {
    //                 lsdRing.addClass('d-none');
    //             }
    //         });
    //     });
}

// Function to activate Bootstrap tooltips
function activateTooltips() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}
activateTooltips();
// Event listener for dynamic content (after AJAX)
$(document).ajaxComplete(function () {
    activateTooltips();
});

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
        loadData(pageNumber, pageSize,searchByInvoiceItemId );
    })
}

//Change Page Size
function changePageSize(){
    let pageSizeButton = $('#pageSize');
    pageSizeButton.off('change');
    pageSizeButton.on('change',function (){
        pageSize = $(this).val();
        pageNumber = 0;
        loadData(pageNumber,pageSize,searchByInvoiceItemId);
    })
}

function searchByInvoiceItemIdFunction(){
    let searchMovieButton = $('#search-button');
    searchMovieButton.off('click');
    searchMovieButton.on('click',function (){
        searchByInvoiceItemId = $('#search-by-invoice-item-id').val();
        pageNumber = 0;
        loadData(pageNumber,pageSize,searchByInvoiceItemId);
    })

    /* Change Date */
    function changeDateRenderMovieSchedule() {
        let searchByDateInput = $('#search-by-date');
        searchByDateInput.off('change');
        searchByDateInput.on('change', function () {
            date = new Date($(this).val());
            loadData();
        })
    }

}
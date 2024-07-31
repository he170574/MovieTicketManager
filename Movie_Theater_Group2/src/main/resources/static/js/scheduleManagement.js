$(document).ready(function () {
    $('#search-by-date').val(formatDateToYYYYMMDD(date));
    loadAllCinemaRoom();
    formatPriceInput();
    changeDateRenderMovieSchedule();
})
let lsdRing = $('.lsd-ring-container');
let date = new Date();

/* Cinema */
function loadAllCinemaRoom() {
    $.ajax({
        url: '/get-all-cinema-room',
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            let cinemaRoomLists = response.data;
            let cinemaRoomContainer = $('#schedule-room-container');

            cinemaRoomContainer.empty();
            $.each(cinemaRoomLists, function (index, value) {
                cinemaRoomContainer.append(cinemaRoomTemplate(value));
            })

        }, error: function (xhr, status, error) {
            Swal.fire({
                title: "Load Data Fail",
                icon: "error",
                text: "Something wrong with sever,Try Later!",
                confirmButtonText: "Ok",
            });
        }, complete: function (xhr, status) {
            lsdRing.addClass('d-none');
            createMovieSchedule();
            getScheduleByRoomIdAndDay();
            getScheduleByRoomIdAndDayBefore();
        }
    });
}

function cinemaRoomTemplate(value) {
    return `
    <div class="row mt-4 schedule mb-5" data-room-id="` + value.id + `">
        <div class="d-flex justify-content-between pb-2">
            <div class="d-flex align-items-center justify-content-start" style="height: 100%">
                <h4 class="fw-bold room-name">` + value.cinemaRoomName + `</h4>
            </div>

            <div class="">
                <button type="button" class="btn btn-danger open-modal-add-schedule" data-bs-target="#add-movie-schedule" data-bs-toggle="modal" data-room-id="` + value.id + `" data-room-name="` + value.cinemaRoomName + `">
                    + Add Movie
                </button>
            </div>
        </div>
        <div class="col-12 px-0 time-movie-detail">
            <div class="container">
                <div class="row">
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        00:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        01:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        02:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        03:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        04:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        05:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        06:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        07:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        08:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        09:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        10:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        11:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        12:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        13:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        14:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        15:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        16:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        17:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        18:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        19:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        20:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        21:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0" style="overflow: hidden">
                        22:00
                    </div>
                    <div class="fw-bold text-center detail-time col p-0"
                         style="overflow: hidden; border-right: 2px solid black">23:00
                    </div>
                </div>
            </div>
        </div>
        <div class="col-12 px-0">
            <div class="progress-stacked bg bg-dark" style="border: 2px solid black; height: 65px;
                            border-top-left-radius: 0; border-top-right-radius: 0">

                <div class="progress empty-schedule" role="progressbar" aria-label="Segment one"
                     aria-valuenow="0"
                     aria-valuemin="0" aria-valuemax="1440"
                     data-movie-id="">
                    <div class="progress-bar bg bg-dark">
                        
                    </div>
                </div>
            </div>
        </div>

    </div>`
}

/* Load Movie Schedule*/

function getScheduleByRoomIdAndDay() {
    $('.schedule').each(function () {
        $.ajax({
            url: '/get-movie-schedule-room-by-room-id-and-day',
            type: 'GET',
            data: {
                roomId: '' + $(this).data('room-id'),
                date: formatDateToYYYYMMDD(date)
            },
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                let scheduleList = response.data;
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
    })
}

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
            data-schedule-id="` + value.scheduleId + `"
            data-room-id = "` + value.cinemaRoomId + `"
             role="progressbar" 
             aria-label="Segment one"
             aria-valuemin="` + startTimeMinus + `" 
             aria-valuemax="` + ((startTimeMinus + value.movieDuration) > 1440 ? 1440 : (startTimeMinus + value.movieDuration)) + `"
             style="width: ` + scaleNum + `%; height: 100%"
             data-movie-id="` + value.movieId + `">
            <div class="progress-bar bg bg-danger">
                <a tabIndex="0" class="btn movie-time-in-schedule fw-bold" role="button"
                   style="height: 100%; width: 100%; font-size: 13px"
                   data-bs-custom-class="custom-tooltip"
                   data-bs-html="true"
                   data-bs-toggle="tooltip" 
                   data-bs-title="
                   <div class='card border-dark bg-dark text-white' style='width: 25rem; opacity:1'>
                        <img src='` + value.movieSmallImage + `' class='card-img-top' alt='...'>
                    <div class='card-body'>
                        <h5 class='card-title'>` + value.movieName + `</h5>
                        <p class='card-text'>Start Time: ` + convertDateTimeStringToTime(value.scheduleTime) + `</p>
                        <p class='card-text'>End Time: ` + convertDateTimeStringToTime(new Date(new Date(value.scheduleTime).getTime() + value.movieDuration * 60000)) + `</p>
                        <p class='card-text'>Duration: ` + value.movieDuration + ` min</p>
                        <p class='card-text'>VIP Price: ` + value.priceVipSeat + ` min</p>
                        <p class='card-text'>Normal Price: ` + value.priceNormalSeat + ` min</p>
                        <button class='btn btn-primary'>Go somewhere</button>
                    </div>
                   </div>"    
                   data-bs-content="">
                   ` + convertDateTimeStringToTime(value.scheduleTime) + `
                </a>
            </div>
        </div>`;


    //Divide Layout Schedule
    let cinemaRoomSchedule = $('.schedule[data-room-id="' + value.cinemaRoomId + '"]');

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

    //Enable Tooltip
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    })
}

function getScheduleByRoomIdAndDayBefore() {
    let dateBefore = new Date(date);
    dateBefore.setDate(dateBefore.getDate() - 1)
    $('.schedule').each(function () {
        $.ajax({
            url: '/get-movie-schedule-room-by-room-id-and-day',
            type: 'GET',
            data: {
                roomId: '' + $(this).data('room-id'),
                date: formatDateToYYYYMMDD(dateBefore)
            },
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                let scheduleList = response.data;
                scheduleList.forEach(function (value) {
                    renderMovieScheduleForDayBefore(value);
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
    })
}

function renderMovieScheduleForDayBefore(value) {
    let startTimeMinus = convertDateTimeStringToMinutes(value.scheduleTime);
    let scaleNum = value.movieDuration / 14.4 > 100 ? 100 : value.movieDuration / 14.4;
    if (startTimeMinus + value.movieDuration < 1440) {
        return;
    }
    scaleNum = (value.movieDuration - (1440 - startTimeMinus)) / 14.4;
    startTimeMinus = 0;
    var html = `
        <div class="progress movie-in-schedule h-100 border border-2 border-dark schedule-element"  
             role="progressbar" 
             data-bs-target="#add-movie-schedule" 
             data-bs-toggle="modal" 
            data-schedule-id="` + value.scheduleId + `"
            data-room-id = "` + value.cinemaRoomId + `"
             aria-label="Segment one"
             aria-valuemin="` + startTimeMinus + `" 
             aria-valuemax="` + ((startTimeMinus + value.movieDuration) > 1440 ? 1440 : (startTimeMinus + value.movieDuration)) + `"
             style="width: ` + scaleNum + `%; height: 100%"
             data-movie-id="` + value.movieId + `">
            <div class="progress-bar bg bg-danger">
                <a tabIndex="0" class="btn movie-time-in-schedule fw-bold" role="button"
                   style="height: 100%; width: 100%; font-size: 13px"
                   data-bs-custom-class="custom-tooltip"
                   data-bs-html="true"
                   data-bs-toggle="tooltip" 
                   data-bs-title="
                   <div class='card border-dark bg-dark text-white' style='width: 25rem; opacity:1'>
                        <img src='` + value.movieSmallImage + `' class='card-img-top' alt='...'>
                    <div class='card-body'>
                        <h5 class='card-title'>` + value.movieName + `</h5>
                        <p class='card-text'>Start Time: ` + convertDateTimeStringToTime(value.scheduleTime) + `</p>
                        <p class='card-text'>End Time: ` + convertDateTimeStringToTime(new Date(new Date(value.scheduleTime).getTime() + value.movieDuration * 60000)) + `</p>
                        <p class='card-text'>Duration: ` + value.movieDuration + ` min</p>
                        <p class='card-text'>VIP Price: ` + value.priceVipSeat + ` min</p>
                        <p class='card-text'>Normal Price: ` + value.priceNormalSeat + ` min</p>
                        <button class='btn btn-primary'>Go somewhere</button>
                    </div>
                   </div>"    
                   data-bs-content="">
                   ` + convertDateTimeStringToTime(value.scheduleTime) + `
                </a>
            </div>
        </div>`;


    //Divide Layout Schedule
    let cinemaRoomSchedule = $('.schedule[data-room-id="' + value.cinemaRoomId + '"]');

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

    //Enable Tooltip
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    })
}

/* Change Date */
function changeDateRenderMovieSchedule() {
    let searchByDateInput = $('#search-by-date');
    searchByDateInput.off('change');
    searchByDateInput.on('change', function () {
        date = new Date($(this).val());
        loadAllCinemaRoom();
    })
}

/* Movie */
let movieNameForSearch = '';
let pageNumber = 0;

function loadMovie(movieName, pageNumber) {
    $.ajax({
        url: '/get-movie-by-movie-name',
        type: 'GET',
        data: {
            movieName: movieName,
            pageNumber: pageNumber
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            $('.spinner-border').addClass('d-none');

            let data = response.data;
            let movieLists = data.lstMovie;

            let movieContainer = $('#list-movie-container');
            $.each(movieLists, function (index, value) {
                movieContainer.append(movieTemplate(value));
            });


            let btnShowMore = $('#btn-show-more-movie');
            pageNumber = data.pageNumber;
            if (data.pageNumber < data.totalPage - 1) {
                btnShowMore.removeClass('d-none');
                btnShowMore.attr('data-page-number', data.pageNumber);
                viewMoreMovie();
            } else {
                btnShowMore.addClass('d-none');
            }

        }, error: function (xhr, status, error) {
            Swal.fire({
                title: "Load Data Fail",
                icon: "error",
                text: "Something wrong with sever,Try Later!",
                confirmButtonText: "Ok",
            });
        }, complete: function (xhr, status) {
            lsdRing.addClass('d-none');
            searchMovieByMovieName();
            chooseMovieForMovieSchedule();
        }
    });
}

function movieTemplate(value) {
    return `
    <div class="col-xl-4 col-6 mb-3">
        <div class="card movie-card" style="transition: all 0.15s">
            <img src="` + value.smallImage + `" class="card-img-top" alt="...">
                <div class="card-body">
                    <h5 class="card-title">` + value.movieName + `</h5>
                    <p class="card-text">Duration: ` + value.duration + ` min</p>
                    <a href="#" class="btn btn-dark choose-movie" data-movie-id="` + value.movieId + `" data-movie-name="` + value.movieName + `" data-duration="` + value.duration + `" data-small-image="` + value.smallImage + `">Choose this</a>
                </div>
        </div>
    </div>`
}

function viewMoreMovie() {
    let btnViewMoreMovie = $('#btn-show-more-movie');
    btnViewMoreMovie.off('click');
    btnViewMoreMovie.on('click', function () {
        pageNumber++;
        loadMovie(movieNameForSearch, pageNumber);
    })
}

function searchMovieByMovieName() {
    let searchMovieBtn = $('#search-movie-button');
    searchMovieBtn.off('click');
    searchMovieBtn.on('click', function () {
        movieNameForSearch = $('#search-movie-input').val();
        pageNumber = 0;
        $('#list-movie-container').empty();
        loadMovie(movieNameForSearch, pageNumber);
    });
}


/*Create Movie Schedule*/
let movieScheduleRoomId = null;
let movieScheduleDate = date;
let movieScheduleMovieId = null;

function createMovieSchedule() {
    let btnOpenModalCreateMovieSchedule = $('.open-modal-add-schedule');
    btnOpenModalCreateMovieSchedule.off('click');
    btnOpenModalCreateMovieSchedule.on('click', function () {

        $('#submit-update-movie-schedule').addClass('d-none');
        $('#submit-delete-movie-schedule').addClass('d-none');
        $('#submit-create-movie-schedule').removeClass('d-none');

        $('#headerContent').html('Create Movie Schedule');
        /* Clear Form Before */
        $('#add-schedule-start-time').val('').removeClass('is-invalid');
        $('#movieChosenName').html('-----').removeClass('is-invalid');
        $('#movieChosenDuration').html('--:--').removeClass('is-invalid');
        $('#add-schedule-seat-normal-price').val('').removeClass('is-invalid');
        $('#add-schedule-seat-vip-price').val('').removeClass('is-invalid');
        movieScheduleMovieId = null;
        /* Load Movie */
        movieNameForSearch = '';
        pageNumber = 0;
        $('#list-movie-container').empty();
        $('#search-movie-input').val('');
        loadMovie(movieNameForSearch, pageNumber);

        /* Load Schedule Information*/
        movieScheduleRoomId = $(this).data('room-id');
        $('#roomChosenName').html($(this).data('room-name'));

        $('#add-schedule-date').val(formatDateToYYYYMMDD(movieScheduleDate));

        submitMovieScheduleInformation();
    });
}

function submitMovieScheduleInformation() {
    let submitBtn = $('#submit-create-movie-schedule');
    submitBtn.off('click');
    submitBtn.on('click', function () {
        const fieldIds = ['add-schedule-date', 'add-schedule-start-time', 'add-schedule-seat-normal-price', 'add-schedule-seat-vip-price'];

        if (checkNotEmpty(fieldIds)) {
            if (movieScheduleMovieId === null) {
                Swal.fire({
                    title: "Please Choose Movie",
                    icon: "error",
                    confirmButtonText: "Ok",
                });
            } else {

                let movieSchedule = {
                    movieId: movieScheduleMovieId,
                    cinemaRoomId: movieScheduleRoomId,
                    scheduleTime: $('#add-schedule-date').val() + 'T' + $('#add-schedule-start-time').val() + ':00',
                    priceNormalSeat: $('#add-schedule-seat-normal-price').val().replace(/[\D]/g, ''),
                    priceVipSeat: $('#add-schedule-seat-vip-price').val().replace(/[\D]/g, ''),
                }
                $.ajax({
                    url: '/admin/add-new-movie-schedule-room',
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: "json",
                    data: JSON.stringify(movieSchedule),
                    beforeSend: function () {
                        lsdRing.removeClass('d-none');
                    },
                    success: function (response) {
                        Swal.fire({
                            title: "Create Movie Schedule Success",
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
            }
        }
    });
}

function chooseMovieForMovieSchedule() {
    let chooseMovieBtn = $('.choose-movie');
    chooseMovieBtn.off('click');
    chooseMovieBtn.on('click', function () {
        movieScheduleMovieId = $(this).data('movie-id');
        $('#movieChosenName').html($(this).data("movie-name"));
        $('#movieChosenDuration').html($(this).data('duration'));

        $('.movie-card').removeClass('border-2 border-secondary shadow-lg');
        let movieCard = $(this).closest('.movie-card');
        movieCard.addClass('border-2 border-secondary shadow-lg')
    })
}

/* Edit Movie Schedule */
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

                submitEditForm(data.movieId, data.cinemaRoomId, data.scheduleId, data.scheduleTime);
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
                movieNameForSearch = '';
                pageNumber = 0;
                $('#list-movie-container').empty();
                $('#search-movie-input').val('');
                loadMovie(movieNameForSearch, pageNumber);

            }
        });
    });
}

function submitEditForm(movieId, cinemaRoomId, scheduleTimeIdOld, scheduleTimeOld) {
    let submitEditMovieScheduleBtn = $('#submit-update-movie-schedule');
    submitEditMovieScheduleBtn.off('click');
    submitEditMovieScheduleBtn.on('click', function () {

        let confirmDelete = false;
        Swal.fire({
            title: "Are you sure want to update this movie",
            icon: "warning",
            text: "The user may booked ticket",
            confirmButtonText: "Continue Update",
            showCancelButton: true,
            cancelButtonText: "Cancel"
        }).then((result) => {
            if (result.isConfirmed) {
                confirmDelete = true;
                if (new Date() > new Date(scheduleTimeOld)) {
                    Swal.fire({
                        title: "The movie has been shown",
                        icon: "warning",
                        text: "The customer get error about booked ticket\n.Please contact to customer who booked this movie's ticket and delete it in Ticket Manager",
                        confirmButtonText: "Continue Update",
                        showCancelButton: true,
                        cancelButtonText: "Cancel"
                    }).then((result) => {
                        if (result.isConfirmed) {
                            submitForm(movieId,cinemaRoomId,scheduleTimeIdOld);
                        }
                    })
                }else{
                    submitForm(movieId,cinemaRoomId,scheduleTimeIdOld);
                }
            }
        });



    });
}
function submitForm(movieId,cinemaRoomId,scheduleTimeIdOld){
    const fieldIds = ['add-schedule-date', 'add-schedule-start-time', 'add-schedule-seat-normal-price', 'add-schedule-seat-vip-price'];
    if (checkNotEmpty(fieldIds)) {
        let movieSchedule = {
            movieId: movieScheduleMovieId !== null ? movieScheduleMovieId : movieId,
            cinemaRoomId: cinemaRoomId,
            scheduleTime: $('#add-schedule-date').val() + 'T' + $('#add-schedule-start-time').val() + ':00',
            priceNormalSeat: $('#add-schedule-seat-normal-price').val().replace(/[\D]/g, ''),
            priceVipSeat: $('#add-schedule-seat-vip-price').val().replace(/[\D]/g, ''),
            movieIdOld: movieId,
            scheduleIdOld: scheduleTimeIdOld
        }

        $.ajax({
            url: '/admin/update-movie-schedule',
            type: 'POST',
            contentType: 'application/json',
            dataType: "json",
            data: JSON.stringify(movieSchedule),
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                Swal.fire({
                    title: "Update Movie Schedule Success",
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

    }
}

/* Delete Movie Schedule */
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

/* etc */
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

function checkNotEmpty(fieldIds) {
    let allFilled = true;
    fieldIds.forEach(function (fieldId) {
        const valueTag = $('#' + fieldId);
        const value = valueTag.val();
        if (!value) {
            allFilled = false;
            valueTag.addClass('is-invalid')
        } else {
            valueTag.removeClass('is-invalid')
        }
    });
    return allFilled;
}

function formatPriceInput() {
    $('#add-schedule-seat-normal-price, #add-schedule-seat-vip-price').on('input', function () {
        var value = $(this).val().replace(/[\D]/g, '');

        // Cập nhật giá trị của input với giá trị đã định dạng
        $(this).val(Intl.NumberFormat('vi-VN', {}).format(value));
    }).on('blur', function () {
        var value = $(this).val().replace(/[\D]/g, '');

        // Cập nhật giá trị của input với giá trị đã định dạng
        $(this).val(Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
        }).format(value));
    }).on('focus', function () {
        var value = $(this).val().replace(/[\D]/g, '');

        // Cập nhật giá trị của input với giá trị đã định dạng
        $(this).val(Intl.NumberFormat('vi-VN', {}).format(value));
    });
}

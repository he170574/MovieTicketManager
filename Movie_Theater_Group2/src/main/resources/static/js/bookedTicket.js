$(document).ready(function () {
    $('#nav-content').find('.booked-ticket').addClass('active');
    loadData(pageNumber,pageSize, searchByMovieName);
    changePageSize();
    searchByMovieNameFunction();
})

let lsdRing = $('.lsd-ring-container');

let pageSize = 10;
let pageNumber = 0;
let searchByMovieName = "";

let lstBookedTicket = [];
function loadData(pageNumber, pageSize, searchByMovieName) {
    $.ajax({
        url: '/get-booked-ticket',
        type: 'GET',
        data: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            searchByMovieName: searchByMovieName
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const table = $('#table-data');
            const data = response.data;
            if(data == null){
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Oke Bro 🙂",
                });
                return;
            }
            const lstBookedTicket = data.lstBookedTicket;
            if (lstBookedTicket === null || lstBookedTicket.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                table.empty();
                table.append(nullRowTemplate());
                renderPagination(1, 1);
            } else {                          //Has Data

                table.empty();

                $.each(lstBookedTicket, function (index, value) {
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
                confirmButtonText: "Oke Bro 🙂",
            });
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });
}

// Render Data
function nullRowTemplate() {
    return `
    <tr>
        <td colspan="9">
            <div class="main__table-text">
                <span >No Data</span>
            </div>
        </td>
    </tr>    
    `
}

function rowTemplate(index, rowData) {
    return `
    <tr>
        <td>
            <div class="main__table-text">
                <span class="index">` + index + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="movie-name main__table-text--green status">` + rowData.movieName + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="booking-date">` + formatDateString(rowData.bookingDate) + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="booking-date">` + formatDateString(rowData.scheduleTime) + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="booking-date">` + rowData.seat + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="booking-date">` + rowData.cinemaRoomName + `</span>
            </div>
        </td>
        <td>
            <div class="main__table-text">
                <span class="amount">` + Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(rowData.price) + `</span>
            </div>
        </td>
       
    </tr>`
}

function statusForRow(status) {
    var btnColor = 'btn-primary'
    if (status.toLowerCase() === 'waiting') {
        btnColor = 'btn-warning'
    } else if (status.toLowerCase() === 'confirm'){
        btnColor = 'btn-success'
    }else if(status.toLowerCase() === 'refuse'){
        btnColor = 'btn-danger'
    }
    return `<span class="btn ` + btnColor +` status">` + status +`</span>`
}

function fomatDateStrToDate(dateString){
    // Parse the string into a Date object
    var date = new Date(dateString);
    var day = date.getDate().toString().padStart(2, '0'); // Day (with leading zeros)
    var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month (with leading zeros, January is 0!)
    var year = date.getFullYear(); // Year
    return day + '/' + month + '/' + year ;
}

function formatDateString(dateString) {
    // Parse the string into a Date object
    var date = new Date(dateString);
    var day = date.getDate().toString().padStart(2, '0'); // Day (with leading zeros)
    var month = (date.getMonth() + 1).toString().padStart(2, '0'); // Month (with leading zeros, January is 0!)
    var year = date.getFullYear(); // Year
    var hour = date.getHours().toString().padStart(2, '0'); // Hour (with leading zeros)
    var minute = date.getMinutes().toString().padStart(2, '0'); // Minute (with leading zeros)
    return day + '/' + month + '/' + year + ' - ' + hour + ':' + minute;
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
        loadData(pageNumber, pageSize, searchByMovieName);
    })
}

function changePageSize(){
    let pageSizeButton = $('#pageSize');
    pageSizeButton.off('change');
    pageSizeButton.on('change',function (){
        pageSize = $(this).val();
        pageNumber = 0;
        loadData(pageNumber,pageSize,searchByMovieName);
    })
}

function searchByMovieNameFunction(){
    let searchMovieButton = $('#search-movie');
    searchMovieButton.off('click');
    searchMovieButton.on('click',function (){
        searchByMovieName = $('#movie-name').val().trim();
        pageNumber = 0;
        loadData(pageNumber,pageSize,searchByMovieName);
    })

}
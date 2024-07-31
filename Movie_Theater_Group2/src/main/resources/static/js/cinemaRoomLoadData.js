$(document).ready(function () {
    loadCinemaRoomData(pageNumber,pageSize, searchByCinemaRoomName);
    changePageSize();
    searchByCinemaRoomNameFunction();
})


let pageSize = 5;
let pageNumber = 0;
let searchByCinemaRoomName = "";

function loadCinemaRoomData(pageNumber, pageSize, searchByCinemaRoomName) {
    $.ajax({
        url: '/admin/cinema-room-list',
        type: 'GET',
        data: {
            pageNumber: pageNumber,
            pageSize: pageSize,
            searchByCinemaRoomName: searchByCinemaRoomName
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const tbody = $('.main__table tbody');
            const data = response.data;
            if(data == null){
                Swal.fire({
                    title: "Load Data Fail",
                    icon: "error",
                    text: "Please try later.",
                    confirmButtonText: "Close",
                });
                return;
            }
            const listCinemaRooms = data.listCinemaRooms;
            if (listCinemaRooms === null || listCinemaRooms.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                tbody.empty();
                tbody.append(nullRowTemplate());
                renderPagination(1, 1);
            } else { //Has Data

                tbody.empty();

                listCinemaRooms.forEach(function (cinemaRoom) {
                    const row = $('<tr></tr>');
                    row.append(`<td><div class="main__table-text"><span>${cinemaRoom.cinemaRoomId}</span></div></td>`);
                    row.append(`<td><div class="main__table-text"><span>${cinemaRoom.cinemaRoomName}</span></div></td>`);
                    row.append(`<td><div class="main__table-text"><span>${cinemaRoom.seatQuantity}</span></div></td>`);

                    row.append(`<td>${createButtonHtml(cinemaRoom.cinemaRoomId)}</td>`);

                    tbody.append(row);
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
            lsdRing.addClass('d-none');
        }
    });
}

function nullRowTemplate() {
    return `
    <tr>
        <td colspan="8">
            <div class="main__table-text">
                <span >No Data</span>
            </div>
        </td>
    </tr>    
    `
}

//Render Số trang
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
        loadCinemaRoomData(pageNumber, pageSize, searchByCinemaRoomName);
    })
}

//Change Page Size
function changePageSize(){
    let pageSizeButton = $('#pageSize');
    pageSizeButton.off('change');
    pageSizeButton.on('change',function (){
        pageSize = $(this).val();
        pageNumber = 0;
        loadCinemaRoomData(pageNumber,pageSize,searchByCinemaRoomName);
    })
}

//Change Search
function searchByCinemaRoomNameFunction(){
    let searchMovieButton = $('#search-cinema-room-btn');
    searchMovieButton.off('click');
    searchMovieButton.on('click',function (){
        searchByCinemaRoomName = $('#cinema-room-name-searching').val().trim();
        loadCinemaRoomData(pageNumber,pageSize,searchByCinemaRoomName);
    })
}
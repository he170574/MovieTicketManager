$(document).ready(function () {
    loadMovieData(pageNumber,pageSize, searchByMovieName);
    changePageSize();
    searchByMovieNameFunction();
})


let pageSize = 5;
let pageNumber = 0;
let searchByMovieName = "";

function loadMovieData(pageNumber, pageSize, searchByMovieName) {
    $.ajax({
        url: '/admin/get-all-movie',
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
            const listMovies = data.listMovies;
            if (listMovies === null || listMovies.length === 0 || data.pageNumber === null || data.pageSize === null || data.totalPage === null) {    //No Data
                tbody.empty();
                tbody.append(nullRowTemplate());
                renderPagination(1, 1);
            } else { //Has Data

                tbody.empty();

                listMovies.forEach(function (movie) {
                    const row = $('<tr></tr>');
                    row.append(`<td><div class="main__table-text"><span>${movie.movieId}</span></div></td>`);
                    row.append(`<td><div class="main__table-text"><span>${movie.movieName}</span></div></td>`);
                    row.append(`<td>${createButtonHtml(movie.movieId)}</td>`);

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

function createButtonHtml(movieId) {
    return `
        <div class="main__table-btns">
            <button class="btn main__table-btn main__table-btn--edit" type="button"
                id="view-seat-detail-${movieId}" 
                data-bs-placement="bottom"
                data-bs-custom-class="custom-tooltip" 
                onclick="showMovieDetails(${movieId})"
                >
                 <i class="bi bi-pencil-square"></i> 
            </button>
            <button class="btn main__table-btn main__table-btn--delete"
                type="button" 
                data-bs-toggle="modal"
                data-bs-toggle="tooltip" 
                data-bs-placement="bottom"
                data-bs-custom-class="custom-tooltip" 
                data-bs-title="Delete Cinema Room" 
                onclick = "deleteMovie(${movieId})" >
                <i class="bi bi-trash3"></i>
            </button>
        </div>
    `;
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
        loadMovieData(pageNumber, pageSize, searchByMovieName);
    })
}

//Change Page Size
function changePageSize(){
    let pageSizeButton = $('#pageSize');
    pageSizeButton.off('change');
    pageSizeButton.on('change',function (){
        pageSize = $(this).val();
        pageNumber = 0;
        loadMovieData(pageNumber,pageSize,searchByMovieName);
    })
}

//Change Search
function searchByMovieNameFunction(){
    let searchMovieButton = $('#search-movie-btn');
    searchMovieButton.off('click');
    searchMovieButton.on('click',function (){
        searchByMovieName = $('#movie-name-searching').val().trim();
        loadMovieData(pageNumber,pageSize,searchByMovieName);
    })
}
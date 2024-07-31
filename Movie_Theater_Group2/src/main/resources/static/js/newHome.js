$(document).ready(function () {
    loadMovieToday();
});

let lsdRing = $('.lsd-ring-container');
let movieDate = new Date();

function formatDate(date) {
    var day = date.getDate();
    var month = date.getMonth() + 1; // getMonth() returns 0-11
    var year = date.getFullYear();

    // Pad the month and day with zeros if they are single digits
    month = month.toString().padStart(2, '0');
    day = day.toString().padStart(2, '0');

    return year + '-' + month + '-' + day;
}

function rowTemplate(index, rowData) {
    // Chuẩn bị chuỗi HTML cho các nút loại phim
    var movieTypesHtml = '';
    // Duyệt qua mảng typeMoviesString và thêm mỗi loại vào chuỗi HTML
    $.each(rowData.typeMoviesString, function (i, value) {
        movieTypesHtml += `<button type="button" class="btn btn-outline-success me-2 mb-2 fw-bold">${value}</button>`;
    });

    // Trả về chuỗi template của bạn với movieTypesHtml đã được nối vào
    return `
    <div class="movie-card" data-id="${rowData.movieId}" data-image="${rowData.largeImage}" data-title="${rowData.movieName}" data-description="${rowData.content}">
        <a href="#" class="movie-image-link">
            <img src="${rowData.smallImage}" alt="Poster of ${rowData.movieName}">
        </a>
        <div class="movie-details">
            <h5 class="movie-title">${rowData.movieName}</h5>
            <p class="movie-info">${rowData.content}</p>
            <div class="movie-types">${movieTypesHtml}</div>
        </div>
    </div>
    `;
}

function loadMovieToday() {
    $.ajax({
        url: '/get-movie-by-date',
        type: 'GET',
        data: {
            date: formatDate(movieDate)
        },
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            const container = $('#movie-items');
            const data = response.data;
            if (data == null) {
                Swal.fire({
                    title: "There're no showtimes today !",
                    icon: "error",
                    text: "See you soon !",
                    confirmButtonText: 'Okeeee',
                });
                return;
            }

            container.empty();
            $.each(data, function (index, value) {
                container.append(rowTemplate(index, value));
            });

            // Cập nhật ảnh banner từ phim đầu tiên
            updateHeroSection(data[0]);

            // Gọi hàm để gán lại sự kiện click sau khi các phần tử được thêm vào DOM
            attachCardEvents();
        },

        error: function (xhr, status, error) {
            console.log('Error:', error);
            Swal.fire({
                title: "Load Data Fail",
                icon: "error",
                text: "Please try again.",
                confirmButtonText: "Okeeee",
            });
        },
        complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });
}

function updateHeroSection(movieData) {
    const imageContainer = document.querySelector('.image-container img');
    const heroTitle = document.querySelector('.overlay-text h1');
    const heroDescription = document.querySelector('.overlay-text p');
    const overlayText = document.querySelector('.overlay-text');

    const image = movieData.largeImage;
    const title = movieData.movieName;
    const description = movieData.content;
    const id = movieData.movieId;

    imageContainer.src = image;
    heroTitle.textContent = title;
    heroDescription.textContent = description;
    overlayText.setAttribute('data-id', id);
}

function attachCardEvents() {
    const movieCards = document.querySelectorAll('.movie-card');
    const imageContainer = document.querySelector('.image-container img');
    const heroTitle = document.querySelector('.overlay-text h1');
    const heroDescription = document.querySelector('.overlay-text p');
    const overlayText = document.querySelector('.overlay-text');

    // Update hero section when a movie card is clicked
    movieCards.forEach(card => {
        card.addEventListener('click', () => {
            imageContainer.style.opacity = 0;
            heroTitle.style.opacity = 0;
            heroDescription.style.opacity = 0;

            setTimeout(() => {
                const image = card.getAttribute('data-image');
                const title = card.getAttribute('data-title');
                const description = card.getAttribute('data-description');
                const id = card.getAttribute('data-id');

                imageContainer.src = image;
                heroTitle.textContent = title;
                heroDescription.textContent = description;
                overlayText.setAttribute('data-id', id);

                imageContainer.style.opacity = 1;
                heroTitle.style.opacity = 1;
                heroDescription.style.opacity = 1;
            }, 500);
        });
    });

    // Fetch movie details when "Thông tin chi tiết" button is clicked
    const detailsButton = document.getElementById('detailsButton');
    detailsButton.addEventListener('click', () => {
        const movieId = overlayText.getAttribute('data-id');
        fetchMovieDetails(movieId);
    });
}

function fetchMovieDetails(movieId) {
    $.ajax({
        url: '/get-movie-by-id?id=' + movieId,
        type: 'GET',
        dataType: "json",
        beforeSend: function () {
            $('#lsdRing').removeClass('d-none');
        },
        success: function (response) {
            let movie = response.data;
            $('.content-detail').css({
                'backgroundImage': 'url(' + movie.largeImage + ')',
                'backgroundRepeat': 'no-repeat',
                'backgroundSize': 'cover',
                'backgroundPosition': 'center center',
            });
            $('#trailer').attr('src', movie.introVideo);
            $('#movie-image').attr('src', movie.smallImage);
            $('#movie-name').text(movie.movieName);
            $('#duration').text(movie.duration);
            $('#actor').text(movie.actor);
            $('#director').text(movie.director);
            $('#movie-content').text(movie.content);
            $('#buy-ticket').attr('href', '/booking-ticket?movieId=' + movieId);
            $('#movie-rating').attr('href','/movie-rating?id=' + movieId);
            $('#select-movie').modal('show');
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        },
        complete: function (xhr, status) {
            $('#lsdRing').addClass('d-none');
        }
    });
}

// function showDetail() {
//     $('.buy-ticket').off('click');
//     $('.buy-ticket').on('click', function () {
//         var movieId = $(this).data('movie-id');
//         $.ajax({
//             url: '/get-movie-by-id?id=' + movieId,
//             type: 'GET',
//             dataType: "json",
//             beforeSend: function () {
//                 lsdRing.removeClass('d-none');
//             },
//             success: function (response) {
//                 let movie = response.data;
//                 $('.content-detail').css({
//                     'backgroundImage': 'url(' + movie.largeImage + ')',
//                     'backgroundRepeat': 'no-repeat',
//                     'backgroundSize': 'cover',
//                     'backgroundPosition': 'center center',
//                 });
//                 $('#trailer').attr('src', movie.introVideo);
//                 $('#movie-image').attr('src', movie.smallImage);
//                 $('#movie-name').text(movie.movieName);
//                 $('#movie-content').text(movie.content);
//                 $('#buy-ticket').attr('href', '/');
//             },
//             error: function (xhr, status, error) {
//                 console.log('Error:', error);
//             },
//             complete: function (xhr, status) {
//                 lsdRing.addClass('d-none');
//             }
//         });
//     });
// }

var listType = [];
var listTypeName = [];
var globalMovieId = '';
var movieSmallImage = '';
var movieLargeImage = '';
var listCurrentMovieType = [];
$(document).ready(function () {
    validateType();
    loadAllType();
    addNewType();
    confirmTypeChosen();
    searchType();
    showSmallImagePreview();
    showLargeImagePreview();
    addNewMovie();
    editMovie();
})

let lsdRing = $('.lsd-ring-container');

function dismissModal(id) {
    // Sử dụng API của Bootstrap để đóng modal
    var myModal = new bootstrap.Modal(document.getElementById(id));
    myModal.hide();
}

function deleteMovie(movieId) {

    Swal.fire({
        title: "Do you want to delete this movie?",
        // showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: "Delete",
    }).then((result) => {
        /* Read more about isConfirmed, isDenied below */
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/movie-list/delete-movie',
                type: 'GET',
                data: {
                    movieId: movieId
                },
                beforeSend: function () {
                    lsdRing.removeClass('d-none');
                },
                success: function (response) {
                    Swal.fire({
                        title: "Success",
                        icon: "success",
                        text: "This movie is deleted",
                        confirmButtonText: "Close",
                    }).then((result) => {
                        /* Read more about isConfirmed, isDenied below */
                        if (result.isConfirmed) {
                            location.reload();
                        }
                    });
                    // loadAllMovie();
                    // dismissModal('#modalAddMovie')
                }, error: function (xhr, status, error) {
                    Swal.fire({
                        title: "Something went wrong",
                        icon: "error",
                        text: "Please try again.",
                        confirmButtonText: "Close",
                    });
                    console.log('Error:', error);
                }, complete: function (xhr, status) {
                    lsdRing.addClass('d-none');
                }
            })
        }
    });

}

function loadAllType() {
    $.ajax({
        url: '/get-all-type',
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            listType = response.data;
            $('.type-list-content').html('')
            $.each(listType, function (index, value) {
                var html = `
                     <button class="type-display d-flex-inline text-center fw-semibold fst-italic align-items-end" data-type-id="` + value.typeId + `">
                            ` + value.typeName + `
                     </button>
            `
                $('.type-list-content').append(html);
            });
            randomColorType();
            getListTypeName();
            chooseType();
        }, error: function (xhr, status, error) {
            console.log('Error:', error);
        }, complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });
}

function addNewType() {
    $('#add-new-type').on('click', function () {
        var type = {
            typeName: $('#inputTypeName').val().trim()
        }
        // if (listTypeName.indexOf(type.typeName) === -1) {
        $.ajax({
            url: '/admin/movie-list/add-new-type',
            type: 'POST',
            contentType: 'application/json',
            dataType: "json",
            data: JSON.stringify(type),
            beforeSend: function () {
                lsdRing.removeClass('d-none');
            },
            success: function (response) {
                console.log(response.message);
                const value = response.data
                var html = `
                     <button class="type-display d-flex-inline text-center fw-semibold fst-italic align-items-end"
                       data-type-id="` + value.typeId + `">
                            ` + value.typeName + `
                     </button>
                     `
                $('.type-list-content').append(html);
                listType.push(value)
                listTypeName.push(value.typeName)
                randomColorType();
                chooseType();
            }, error: function (xhr, status, error) {
                console.log('Error:', error);
                Swal.fire({
                    title: "This Type Is Already Existed",
                    icon: "error",
                    text: "Please enter another.",
                    confirmButtonText: "Close",
                });
            }, complete: function (xhr, status) {
                lsdRing.addClass('d-none');
            }
        });
    });
}

function clearModalMovieData() {
    $('#modalTitle').text('ADD NEW MOVIE')
    removeAllChosenType();
    $('.type-display-in-movie').html('');
    $('#add-new-movie').removeClass('d-none');
    $('#confirm-edit-movie').addClass('d-none');
    $('#delete-movie').addClass('d-none');

    $('#inputLargeImage').val('');
    $('#inputSmallImage').val('');
    $('#previewSmallImage').attr('src', '');
    $('#previewLargeImage').attr('src', '');
    $('#inputContent').val('');
    $('#modalAddMovie input').each(function () {
        var elementType = $(this).attr('type');
        switch (elementType) {
            case 'file':
                // For file input, clear the value by setting it to an empty string
                $(this).val('');
                break;
            case 'date':
                // For date input, clear the value by setting it to null
                $(this).val(null);
                break;
            default:
                // For other input types, set the value to an empty string
                $(this).val('');
        }
    });
}

function showMovieDetails(movieId) {
    // Navigate to the new URL
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
            console.log(response.message);
            $('#confirm-edit-movie').removeClass('d-none');
            $('#delete-movie').removeClass('d-none');
            $('#add-new-movie').addClass('d-none');
            var movie = response.data;
            listCurrentMovieType = movie.typeMovies;
            globalMovieId = movie.movieId;
            movieSmallImage = movie.smallImage;
            movieLargeImage = movie.largeImage;
            $('.type-display-in-movie').html('');
            $('#inputLargeImage').val('');
            $('#inputSmallImage').val('');
            loadAllTypeOfAMovie();
            $('#previewSmallImage').attr('src', movieSmallImage);
            $('#previewLargeImage').attr('src', movieLargeImage);
            $('#inputDirector').val(movie.director);
            $('#inputContent').val(movie.content);
            $('#inputMovieProductionCompany').val(movie.movieProductionCompany);
            $('#inputDuration').val(movie.duration);
            $('#inputMovieName').val(movie.movieName);
            $('#inputIntroVideo').val(movie.introVideo);
            $('#inputActor').val(movie.actor);
            $('#modalTitle').text('EDIT MOVIE');
            openModal('#modalAddMovie');

        }, error: function (xhr, status, error) {
            console.log('Error:', error);
            Swal.fire({
                title: "Something went wrong",
                icon: "error",
                text: "Please try again.",
                confirmButtonText: "Close",
            });
        }, complete: function (xhr, status) {
            lsdRing.addClass('d-none');
        }
    });

}

function openModal(modalId) {
    $(modalId).modal('show');
}

function validateType() {
    $('#inputTypeName').on('focus', function () {
        $('#inputTypeNameMessage').html('');
    });
}

var originalModalHeight;

function searchType() {
    $('#searchType').on('input', function () {
        var keyword = $(this).val().toLowerCase().trim();
        $('.type-list-content .type-display').removeClass('d-none').each(function () {
            var typeText = $(this).text().toLowerCase();
            if (keyword !== '' && !typeText.includes(keyword)) {
                $(this).addClass('d-none');
            }
        });
    });
}

// function searchMovie() {
//     $('#searchMovie').on('input', function () {
//         var keyword = $(this).val().toLowerCase().trim();
//         $('.movie-list-content .movie-display').removeClass('d-none').each(function () {
//             var typeText = $(this).find('.movie-name').text().toLowerCase();
//             if (keyword !== '' && !typeText.includes(keyword)) {
//                 $(this).addClass('d-none');
//             }
//         });
//     });
// }

function getListTypeName() {
    listTypeName = []
    $.each(listType, function (index, value) {
        listTypeName.push(value.typeName);
    });
}

function randomColorType() {
    const colors =
        ['#FF0000', '#008000', '#000080', '#CCCC00', '#9966FF', '#006699', '#FF6600', '#FF6666', '#009966', '#993366', '#00CCCC'];

    var listType = $(".type-display");

    listType.each(function (index) {
        // Tính toán chỉ số màu dựa trên số lần lặp qua danh sách
        var colorIndex = index % colors.length;
        $(this).css("background-color", colors[colorIndex]);
    });
}

function loadAllTypeOfAMovie() {
    var l = $('.list-current-chosen-type .type-display')
    l.each(function (index, value) {
        value.click();
    })

    $('.type-list-content .type-display').each(function (index, element) {
        let id = $(element).data('type-id');
        if (listCurrentMovieType.includes(id) && !element.disabled) {
            $(element).click();
        }
    })
    $('#confirm-type-chosen').click();
}

function removeAllChosenType() {
    $('.list-current-chosen-type .type-display').click();
}

function chooseType() {
    $('.type-display').off('click').on('click', function () {
        var clone = $(this).clone(); // Tạo bản sao của phần tử
        $('.list-current-chosen-type').append(clone);
        $(this).prop("disabled", true).addClass("disabled-style");
        removeChosenType();
    });
}

function removeChosenType() {
    $('.list-current-chosen-type .type-display').off('click').on('click', function () {
        var typeId = $(this).attr('data-type-id')
        var elementWithPromoId = $('[data-type-id="' + typeId + '" ]');
        $(this).remove();
        elementWithPromoId.prop("disabled", false);
    });
}

function confirmTypeChosen() {
    $('#confirm-type-chosen').on('click', function () {
        $('.type-display-in-movie').html($('.list-current-chosen-type').html());
        $('#searchType').val('').trigger('input')
    });
}

function showSmallImagePreview() {
    $('#inputSmallImage').on('change', function (event) {
        var file = event.target.files[event.target.files.length - 1];
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#previewSmallImage').attr('src', e.target.result);
        };
        reader.readAsDataURL(file);
    })
}

function showLargeImagePreview() {
    $('#inputLargeImage').on('change', function (event) {
        var file = event.target.files[event.target.files.length - 1];
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#previewLargeImage').attr('src', e.target.result);
        };
        reader.readAsDataURL(file);
    })
}

function editMovie() {
    $('#confirm-edit-movie').on('click', function () {
        Swal.fire({
            title: "You sure to save the new change?",
            icon: "warning",
            confirmButtonText: "Yes",
            showCancelButton: true,
            showCloseButton: true,
        }).then((result) => {
            lsdRing.removeClass('d-none');
            if (result.isConfirmed) {
                Promise.all([
                    uploadImageAndGetString('#inputSmallImage', 'null'),
                    uploadImageAndGetString('#inputLargeImage', 'null')
                ])
                    .then(function ([srcSmallImage, srcLargeImage]) {
                        if (srcLargeImage !== '') {
                            movieSmallImage = srcSmallImage;
                        }
                        if (srcLargeImage !== '') {
                            movieLargeImage = srcLargeImage;
                        }
                        var listTypeIdOfMovie = $('.type-display-in-movie .type-display').map(function () {
                            return $(this).data('type-id');
                        }).get();
                        var movie = {
                            movieId: globalMovieId,
                            smallImage: movieSmallImage,
                            largeImage: movieLargeImage,
                            movieName: $('#inputMovieName').val().trim(),
                            duration: $('#inputDuration').val().trim(),
                            actor: $('#inputActor').val().trim(),
                            movieProductionCompany: $('#inputMovieProductionCompany').val().trim(),
                            content: $('#inputContent').val().trim(),
                            director: $('#inputDirector').val().trim(),
                            introVideo: $('#inputIntroVideo').val().trim(),
                            typeMovies: listTypeIdOfMovie,
                            movieSchedules: []
                        }
                        $.ajax({
                            url: '/admin/movie-list/edit-movie',
                            type: 'POST',
                            contentType: 'application/json',
                            dataType: "json",
                            data: JSON.stringify(movie),
                            beforeSend: function () {
                                lsdRing.removeClass('d-none');
                            },
                            success: function (response) {
                                console.log(response.message);
                                Swal.fire({
                                    icon: "success",
                                    title: "Your movie has been change",
                                    showConfirmButton: false,
                                    timer: 1500
                                }).then((result) => {
                                    /* Read more about isConfirmed, isDenied below */
                                    if (result.isConfirmed) {
                                        location.reload();
                                    }
                                });
                            }, error: function (xhr, status, error) {
                                console.log('Error:', error);
                                Swal.fire({
                                    title: "Oops",
                                    icon: "Error",
                                    text: "Please enter another.",
                                    confirmButtonText: "Close",
                                }).then((result) => {
                                    /* Read more about isConfirmed, isDenied below */
                                    if (result.isConfirmed) {
                                        location.reload();
                                    }
                                });
                            }, complete: function (xhr, status) {
                                lsdRing.addClass('d-none');
                            }
                        });
                    })
                    .catch(function (error) {
                        // Xử lý khi có lỗi trong bất kỳ cuộc gọi nào
                        console.error("Error:", error);
                    });
            } else {
                location.reload();
            }
        });
    });
}

function addNewMovie() {
    $('#add-new-movie').on('click', async function () {
        const result = await Swal.fire({
            title: "You sure add a new movie",
            icon: "warning",
            confirmButtonText: "Yes",
            showCancelButton: true,
        });

        if (!result.isConfirmed) return;

        lsdRing.removeClass('d-none');

        try {
            validateFormInputs();

            // Upload images and gather form data
            const [movieSmallImage, movieLargeImage] = await Promise.all([
                uploadImageAndGetString('#inputSmallImage'),
                uploadImageAndGetString('#inputLargeImage')
            ]);

            const movie = buildMovieObject(movieSmallImage, movieLargeImage);

            // Send movie data
            await $.ajax({
                url: '/admin/movie-list/add-new-movie',
                type: 'POST',
                contentType: 'application/json',
                dataType: "json",
                data: JSON.stringify(movie),
                beforeSend: () => lsdRing.removeClass('d-none'),
                success: (response) => {
                    console.log(response.message);
                    Swal.fire({
                        icon: "success",
                        title: "Your movie has been save",
                        confirmButtonText: "Close",
                        timer: 1500
                    }).then((result) => {
                        /* Read more about isConfirmed, isDenied below */
                        if (result.isConfirmed) {
                            location.reload();
                        }
                    });
                }
            });
        } catch (error) {
            handleError(error);
        } finally {
            lsdRing.addClass('d-none');
        }
    });
}

function uploadImageAndGetString(inputId, check) {
    return new Promise((resolve, reject) => {
        let fileList = $(inputId).prop('files');
        if (check === 'null' && !fileList[0]) {
            resolve("");
        } else {
            let fileData = fileList[fileList.length - 1];
            let formData = new FormData();
            formData.append('file', fileData);

            $.ajax({
                url: '/upload-image-source',
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                beforeSend: function () {
                    lsdRing.removeClass('d-none');
                },
                success: function (response) {
                    resolve(response.data);
                },
                error: function (error) {
                    console.log(error)
                    Swal.fire({
                        title: "Upload Image fail",
                        icon: "error",
                        text: error.message || "Please, try upload other image!",
                        confirmButtonText: "Close",
                    });
                    reject(error);
                },
                complete: function () {
                    lsdRing.addClass('d-none');
                }
            });
        }
    });
}




function validateFormInputs() {
    const inputs = [
        '#inputMovieName',
        '#inputDuration',
        '#inputActor',
        '#inputMovieProductionCompany',
        '#inputContent',
        '#inputDirector'
    ];

    inputs.forEach(input => {
        if ($(input).val().trim() === '') {
            throw new Error(`${input.replace('#input', '')} cannot be empty.`);
        }
    });

    // Also check if at least one movie type is selected
    if ($('.type-display-in-movie .type-display').length === 0) {
        throw new Error("At least one movie type must be selected.");
    }
}



function buildMovieObject(smallImage, largeImage) {
    const listTypeIdOfMovie = $('.type-display-in-movie .type-display').map(function () {
        return $(this).data('type-id');
    }).get();

    return {
        smallImage,
        largeImage,
        movieName: $('#inputMovieName').val().trim(),
        duration: $('#inputDuration').val().trim(),
        actor: $('#inputActor').val().trim(),
        movieProductionCompany: $('#inputMovieProductionCompany').val().trim(),
        content: $('#inputContent').val().trim(),
        director: $('#inputDirector').val().trim(),
        introVideo: $('#inputIntroVideo').val().trim(),
        typeMovies: listTypeIdOfMovie,
        movieSchedules: []
    };
}

function handleError(error) {
    console.error('Error:', error);
    Swal.fire({
        title: "Oops...",
        icon: "error",
        text: error.message || "Something went wrong, try again later..",
        confirmButtonText: "Close",
    });
}
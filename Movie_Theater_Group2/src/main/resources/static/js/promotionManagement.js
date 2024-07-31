$(document).ready(function () {
    loadAllPromotion();
    setupEventHandlers();
});

let lsdRing = $('.lsd-ring-container');

function loadAllPromotion() {
    const titleFilter = $('#titleFilter').val().toLowerCase();

    $.ajax({
        url: '/get-promotion',
        type: 'GET',
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            var listPromotion = response.data;

            // Filter promotions by title if filter is set
            if (titleFilter) {
                listPromotion = listPromotion.filter(promotion => promotion.title.toLowerCase().includes(titleFilter));
            }

            renderPromotions(listPromotion);
            setupPromotionActions();
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        },
        complete: function () {
            lsdRing.addClass('d-none');
        }
    });
}

function renderPromotions(promotions) {
    $('.main-content tbody').empty();
    promotions.forEach((value, index) => {
        const html = `
        <tr>
            <td>${index + 1}</td>
            <td>${value.title}</td>
            <td>${new Date(value.startTime).toLocaleString()}</td>
            <td>${new Date(value.endTime).toLocaleString()}</td>
            <td>${value.discountLevel}</td>
            <td>${value.detail}</td>
            <td>${value.code}</td>
            <td>
                <button type="button" class="btn btn-secondary me-3 delete-promo" data-promo-id="${value.promotionId}">Delete</button>
                <button type="button" class="btn btn-danger edit" data-promo-id="${value.promotionId}" data-bs-toggle="modal" data-bs-target="#modalAddPromotion">Edit</button>
            </td>
        </tr>`;
        $('.main-content tbody').append(html);
    });
}

function setupEventHandlers() {
    $('.add-new-promotion').on('click', openModalAddNewPromotion);
    $('#save-promotion').on('click', savePromotion);
    $('#titleFilter').on('input', loadAllPromotion);
}

function setupPromotionActions() {
    $('.edit').on('click', editPromotion);
    $('.delete-promo').on('click', deletePromotion);
}

function openModalAddNewPromotion() {
    $('#staticBackdropLabel').text('Add A New Promotion');
    $('#form-add-promotion')[0].reset();
    $('#inputId').val('');
}

function savePromotion() {
    const promotion = gatherFormData();
    const promotionId = $('#inputId').val();

    const url = promotionId ? `/save-edit-promotion` : `/save-new-promotion`;
    const type = 'POST'; // Both use POST for simplicity

    if (promotionId) {
        promotion.promotionId = promotionId;
    }

    const currentTime = new Date();
    const endTime = new Date(promotion.endTime);

    // Kiểm tra nếu thời gian kết thúc lớn hơn thời gian hiện tại
    if (endTime <= currentTime) {
        showErrorMessage({ responseText: 'End time must be greater than the current date and time.' });
        return;
    }

    $.ajax({
        url: url,
        type: type,
        contentType: 'application/json',
        dataType: "json",
        data: JSON.stringify(promotion),
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            showSuccessMessage(response.message);
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        },
        complete: function () {
            lsdRing.addClass('d-none');
        }
    });
}

function gatherFormData() {
    return {
        detail: $('#inputDetails').val(),
        discountLevel: $('#inputDiscountLevel').val(),
        endTime: $('#inputEndTime').val(),
        startTime: $('#inputStartTime').val(),
        title: $('#inputTitle').val(),
        image: $('#inputImage').val(),
        code: $('#inputCode').val(),
    };
}

function editPromotion() {
    const promotionId = $(this).data('promo-id');
    $.ajax({
        url: '/edit-promotion',
        type: 'POST',
        contentType: 'application/json',
        dataType: "json",
        data: JSON.stringify({ promotionId }),
        beforeSend: function () {
            lsdRing.removeClass('d-none');
        },
        success: function (response) {
            populateEditForm(response.data);
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        },
        complete: function () {
            lsdRing.addClass('d-none');
        }
    });
}

function populateEditForm(promotion) {
    $('#staticBackdropLabel').text('Edit Promotion');
    $('#inputDetails').val(promotion.detail);
    $('#inputDiscountLevel').val(promotion.discountLevel);
    $('#inputEndTime').val(new Date(promotion.endTime).toISOString().slice(0, 16));
    $('#inputStartTime').val(new Date(promotion.startTime).toISOString().slice(0, 16));
    $('#inputTitle').val(promotion.title);
    $('#inputId').val(promotion.promotionId);
    $('#inputCode').val(promotion.code);
}

function deletePromotion() {
    const promotionId = $(this).data('promo-id');
    Swal.fire({
        title: "Notification",
        icon: "warning",
        text: 'Do you want to delete Promo has id: ' + promotionId,
        showCancelButton: true
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/delete-promotion',
                type: 'POST',
                data: { id: promotionId },
                beforeSend: function () {
                    lsdRing.removeClass('d-none');
                },
                success: function (response) {
                    showSuccessMessage(response.message);
                },
                error: function (xhr, status, errorThrown) {
                    showErrorMessage(xhr);
                },
                complete: function () {
                    lsdRing.addClass('d-none');
                }
            });
        }
    });
}

function showSuccessMessage(message) {
    Swal.fire({
        title: "Success",
        icon: "success",
        text: message,
        confirmButtonText: "Oke",
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.reload();
        }
    });
}

function showErrorMessage(xhr) {
    let errorMessage = xhr.responseText;
    try {
        let responseJson = JSON.parse(xhr.responseText);
        errorMessage = responseJson.message || "Internal Error. Please try later";
    } catch (e) {
        console.error("Error parsing JSON response:", e);
    }
    Swal.fire({
        title: "Error",
        icon: "error",
        text: errorMessage,
        confirmButtonText: "Oke",
    });
    console.log('Error:', errorMessage);
}

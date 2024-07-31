$(document).ready(function () {
    addSeatRowInput();
    saveCinemaRoom();
    // validate();
    updateCinemaRoomName();
    $('#disable-row-button').click(disableRow);
    $('#enable-row-button').click(enableRow);
    $('#add-seats-button').click(addSeatsToRow);
});

function disableRow() {
    const rowNumber = parseInt($('#disable-row-input').val());
    if (isNaN(rowNumber) || rowNumber <= 0) {
        alert('Please enter a valid row number.');
        return;
    }
    const checkboxes = document.querySelectorAll(`.row-${rowNumber} input[type="checkbox"]`);
    checkboxes.forEach(checkbox => {
        checkbox.checked = false;
        checkbox.disabled = true;
    });
}

function enableRow() {
    const rowNumber = parseInt($('#enable-row-input').val());
    if (isNaN(rowNumber) || rowNumber <= 0) {
        alert('Please enter a valid row number.');
        return;
    }
    const checkboxes = document.querySelectorAll(`.row-${rowNumber} input[type="checkbox"]`);
    checkboxes.forEach(checkbox => {
        checkbox.checked = true;
        checkbox.disabled = false;
    });
}

function addSeatsToRow() {
    const rowNumber = parseInt($('#add-seats-row-input').val());
    const numSeats = parseInt($('#add-seats-number-input').val());

    if (isNaN(rowNumber) || rowNumber <= 0 || isNaN(numSeats) || numSeats <= 0) {
        alert('Please enter valid row and seat numbers.');
        return;
    }

    for (let i = 0; i < numSeats; i++) {
        addSeat(rowNumber);
    }
}

function addSeat(row) {
    const checkboxes = document.querySelectorAll(`.row-${row} .checkbox-wrapper-10 input`);
    let nextSeatId;

    if (checkboxes.length === 0) {
        nextSeatId = `${row}A`;
    } else {
        const lastSeatElement = checkboxes[checkboxes.length - 1];
        const lastSeatId = lastSeatElement.id;
        nextSeatId = getNextSeatId(lastSeatId);
    }

    const checkbox = document.createElement('input');
    checkbox.id = nextSeatId;
    checkbox.type = 'checkbox';
    checkbox.className = 'tgl tgl-flip disabled-seat';
    checkbox.checked = true;

    const label = document.createElement('label');
    label.htmlFor = nextSeatId;
    label.dataset.tgOn = nextSeatId;
    label.dataset.tgOff = nextSeatId;
    label.className = 'tgl-btn';

    const wrapper = document.createElement('div');
    wrapper.className = 'col-1 checkbox-wrapper-10 mb-2';
    wrapper.appendChild(checkbox);
    wrapper.appendChild(label);

    const rowDiv = document.querySelector(`.row-${row}`);
    rowDiv.insertBefore(wrapper, rowDiv.lastChild);
}


function addSeatRowInput() {
    let hasAddedInput = false;
    $('#addInput').click(function () {
        var container = $('#inputContainer');
        var rowCount = parseInt($('#rowCount').val());

        if (rowCount <= 0 || isNaN(rowCount)) {
            alert("Please enter a positive number.");
            return;
        }

        for (let i = 0; i < rowCount; i++) {
            var allInputs = container.find('input');
            var nextNum = allInputs.length + 1;

            var newDiv = $('<div/>', {
                'class': 'input-group-seat mb-3'
            });

            var newLabel = $('<label/>', {
                text: "Seat Row " + nextNum + ": ",
                'class': "fw-semibold d-flex align-items-center"
            });

            var newInput = $('<input/>', {
                type: 'text',
                placeholder: "Enter seat number",
                'class': "input-seat form-control border border-dark-subtle ms-2",
                id: nextNum,
            });

            var newSpan = $('<span/>', {
                'class': "error-" + nextNum + " form-message form-text text-danger"
            });

            newDiv.append(newLabel).append(newInput).append(newSpan);
            container.append(newDiv);
        }

        if (!hasAddedInput) {
            hasAddedInput = true;
            $('#submitBtn').prop('disabled', false);
        }
    });
}

function viewMenuEditSeatDetail(cinemaRoomId) {
    showSeatList(cinemaRoomId);
    getCinemaRoomNameById(cinemaRoomId);
}

function showAlertDeleteCinemaRoom(cinemaRoomId) {
    $("#id-cinema-room-alert").attr("data-id", cinemaRoomId);
}

//api
function saveCinemaRoom() {
    $('#form-cinema-room').submit(function (e) {
        e.preventDefault();

        // Object to hold form data
        var formData = {
            cinemaRoomName: $('#cinema-room-name').val(),
            seatDTOS: []
        };

        // Iterate over each seat row input and add to formData
        $('.input-group-seat').each(function () {
            var seatRowId = $(this).find('input').attr('id');
            var seatRowValue = $(this).find('input').val();

            formData.seatDTOS.push({
                seatRow: seatRowId,
                seatNumber: seatRowValue
            });
        });

        if (validateFormData(formData)) {
            console.log(formData);

            $.ajax({
                url: '/admin/cinema-room-list',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function (response) {
                    // Code to run on successful response
                    console.log('Success:', response);
                    location.reload();
                },
                error: function (xhr, status, error) {
                    // Code to run if the request fails
                    console.log('Error:', error);
                }
            });
        }
    });
}

//api
function getCinemaRoomNameById(cinemaRoomId) {
    $.ajax({
        url: '/admin/get-cinema-room-name',
        type: 'POST',
        data: {
            cinemaRoomId: cinemaRoomId
        },
        success: function (response) {
            console.log('Success:', response);
            $('#cinema-room-name-update').val(response.data);
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        }
    });
}

//api
function updateCinemaRoomName() {
    $('#form-update-cinema-room').submit(function (e) {
        e.preventDefault();
        var formData = {
            id: $('#menu-edit-seat').attr('data-id'),
            cinemaRoomName: $('#cinema-room-name-update').val()
        };

        $.ajax({
            url: '/admin/update-cinema-room-name',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                console.log('Success:', response);
                location.reload();
            },
            error: function (xhr, status, error) {
                // Code to run if the request fails
                console.log('Error:', error);
            }
        });
    });
}

//api
function showSeatList(cinemaRoomId) {
    var formData = {
        id: cinemaRoomId
    };
    console.log(formData);

    $("#menu-edit-seat").attr("data-id", cinemaRoomId);

    $.ajax({
        url: '/admin/seat-detail-list',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function (response) {
            console.log('Success:', response);
            createColumnsWithCheckboxes(response.data);

        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        }
    });
}

//api
function deleteCinemaRoom() {
    var cinemaRoomId = $("#id-cinema-room-alert").data("id");

    var formData = {
        id: cinemaRoomId
    };
    console.log(formData);
    $.ajax({
        url: '/admin/delete-cinema-room',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function (response) {
            console.log('Success:', response);
            location.reload();
        },
        error: function (xhr, status, error) {
            console.log('Error:', error);
        }
    });
}

function createColumnsWithCheckboxes(seatData) {
    // Select the parent div where the rows will be added
    const seatRowList = document.querySelector('.seat-row-list');
    seatRowList.innerHTML = '';

    // Determine the total number of rows from seatData
    const totalRows = Math.max(...seatData.map(seat => seat.seatRow));

    // Loop to create each row
    for (let row = 1; row <= totalRows; row++) {
        // Filter seatData for the current row
        const seatsInRow = seatData.filter(seat => seat.seatRow === row);

        seatsInRow.sort((a, b) => a.seatColumn.localeCompare(b.seatColumn));

        // Create a row div
        const rowDiv = document.createElement('div');
        rowDiv.className = `row row-${row} justify-content-start`;
        rowDiv.style.flexWrap = 'nowrap';

        // Loop to create and append each checkbox and label in the row
        seatsInRow.forEach(seat => {
            const seatId = seat.seatId;
            const seatColumn = seat.seatColumn;
            const seatType = seat.seatType;
            const seatDeleted = seat.deleted;
            // Create checkbox input
            const checkbox = document.createElement('input');

            checkbox.id = `${row}${seatColumn}`;
            checkbox.type = 'checkbox';
            checkbox.dataset.id = seatId;
            if (!seatDeleted) {
                checkbox.className = seatType !== 0 ? 'tgl tgl-flip vip-seat' : 'tgl tgl-flip';
            } else {
                checkbox.className = 'tgl tgl-flip disabled-seat';
                checkbox.disabled = true;
            }
            // Create label
            const label = document.createElement('label');
            label.htmlFor = `${row}${seatColumn}`;
            label.dataset.tgOn = `${row}${seatColumn}`;
            label.dataset.tgOff = `${row}${seatColumn}`;
            label.className = 'tgl-btn';

            // Create a wrapper div for each checkbox
            const wrapper = document.createElement('div');
            wrapper.className = `col-1 checkbox-wrapper-10 mb-2`;

            // Append checkbox and label to the wrapper
            wrapper.appendChild(checkbox);
            wrapper.appendChild(label);

            // Append the wrapper to the row
            rowDiv.appendChild(wrapper);
        });

        seatRowList.appendChild(rowDiv);
    }

}

function createButtonHtml(cinemaRoomId) {
    return `
        <div class="main__table-btns">
            <button class="btn main__table-btn main__table-btn--edit" type="button"
                id="view-seat-detail-${cinemaRoomId}" 
                data-bs-placement="bottom"
                data-bs-target="#menu-edit-seat"
                data-bs-custom-class="custom-tooltip" 
                data-bs-title="View seat detail"
                data-bs-toggle="modal"
                onclick="viewMenuEditSeatDetail(${cinemaRoomId})"
                >
                 <i class="bi bi-pencil-square"></i> 
            </button>
            <button class="btn main__table-btn main__table-btn--delete"
                type="button" 
                data-bs-toggle="modal"
                data-bs-toggle="tooltip" 
                data-bs-placement="bottom"
                data-bs-target="#delete-cinema-room-alert"
                data-bs-custom-class="custom-tooltip" 
                data-bs-title="Delete Cinema Room" 
                onclick = "showAlertDeleteCinemaRoom(${cinemaRoomId})" >
                <i class="bi bi-trash3"></i>
            </button>
        </div>
    `;
}

//add seat column
function createAddSeatButton() {
    const seatRowList = document.querySelector('.seat-row-list');
    const totalRows = document.querySelectorAll('.seat-row-list .row').length;
    for (let row = 1; row <= totalRows; row++) {
        const rowDiv = document.querySelector(`.seat-row-list .row-${row}`);


        const wrapper = document.createElement('div');
        wrapper.className = `col-1 checkbox-wrapper-10 mb-2`;

        const addButton = document.createElement('button');
        const icon = document.createElement('i');
        icon.className = 'bi bi-plus-circle';
        addButton.appendChild(icon);
        addButton.className = 'btn tgl-btn btn-outline-dark';
        addButton.onclick = () => addSeat(row);

        wrapper.appendChild(addButton);
        rowDiv.appendChild(wrapper);

        // Append the row to the seat row list
        seatRowList.appendChild(rowDiv);
    }
}

function addSeat(row) {
    // Xác định mã ghế cuối cùng trong hàng
    const checkboxes = document.querySelectorAll(`.row-${row} .checkbox-wrapper-10 input`);
    let nextSeatId;

    // Kiểm tra xem đã có ghế nào chưa và xác định mã ghế tiếp theo
    if (checkboxes.length === 0) {
        // Nếu chưa có ghế nào, tạo ghế đầu tiên
        nextSeatId = `${row}A`;
    } else {
        // Nếu đã có ghế, xác định mã ghế cuối cùng và tính toán mã ghế tiếp theo
        const lastSeatElement = checkboxes[checkboxes.length - 1];
        const lastSeatId = lastSeatElement.id;
        nextSeatId = getNextSeatId(lastSeatId);
    }

    console.log(nextSeatId);

    // Tạo checkbox input
    const checkbox = document.createElement('input');
    checkbox.id = nextSeatId;
    checkbox.type = 'checkbox';
    checkbox.className = 'tgl tgl-flip disabled-seat';
    checkbox.checked = true;

    // Tạo label
    const label = document.createElement('label');
    label.htmlFor = nextSeatId;
    label.dataset.tgOn = nextSeatId;
    label.dataset.tgOff = nextSeatId;
    label.className = 'tgl-btn';

    // Tạo wrapper div
    const wrapper = document.createElement('div');
    wrapper.className = 'col-1 checkbox-wrapper-10 mb-2';
    wrapper.appendChild(checkbox);
    wrapper.appendChild(label);

    // Tìm rowDiv tương ứng và thêm checkbox mới
    const rowDiv = document.querySelector(`.row-${row}`);
    rowDiv.insertBefore(wrapper, rowDiv.lastChild);
}

function getNextSeatId(seatId) {
    const row = parseInt(seatId.match(/\d+/)[0]);
    let col = seatId.match(/[A-Za-z]+/)[0];

    // Tăng cột tiếp theo
    col = incrementColumn(col);

    return `${row}${col}`;
}

function incrementColumn(col) {
    // Convert column to character array
    let chars = col.split('');
    let i = chars.length - 1;
    let done = false;

    while (!done) {
        if (chars[i] === 'Z') {
            chars[i] = 'A';
            i--;
            if (i < 0) {
                chars.unshift('A');
                done = true;
            }
        } else {
            chars[i] = String.fromCharCode(chars[i].charCodeAt(0) + 1);
            done = true;
        }
    }

    return chars.join('');
}

// add seatRow
function createAddRowButton() {
    const seatRowList = document.querySelector('.seat-row-list');
    const addRowButton = document.createElement('button');
    addRowButton.innerText = 'Add Row';
    addRowButton.className = 'btn btn-primary add-row-btn';
    addRowButton.onclick = addNewRow;

    seatRowList.appendChild(addRowButton);
}

function addNewRow() {
    const seatRowList = document.querySelector('.seat-row-list');
    const totalRows = document.querySelectorAll('.seat-row-list .row').length;
    
    // Tạo hàng mới
    const newRow = document.createElement('div');
    newRow.className = `row row-${totalRows + 1} justify-content-start`;
    newRow.style.flexWrap = 'nowrap';

    // Tạo cột A cho hàng mới
    // addSeatToRow(newRow, totalRows + 1, 'A');

    const wrapperButton = document.createElement('div');
    wrapperButton.className = `col-1 checkbox-wrapper-10 mb-2`;

    const addButton = document.createElement('button');
    const icon = document.createElement('i');
    icon.className = 'bi bi-plus-circle';
    addButton.appendChild(icon);
    addButton.className = 'btn tgl-btn btn-outline-dark';
    addButton.onclick = () => addSeat(totalRows + 1);

    wrapperButton.appendChild(addButton);
    newRow.appendChild(wrapperButton);

    // Thêm hàng mới vào danh sách
    seatRowList.insertBefore(newRow, seatRowList.lastChild);
}

function validateFormData(formData) {
    let isValidName = true;
    let isValidSeat = true;

    // Validate cinema room name
    if (formData.cinemaRoomName.trim() === "") {
        isValidName = false;
        $(".name-message").text("Cinema room name is required.");
    } else {
        $(".name-message").text("");
    }

    formData.seatDTOS.forEach(function (seat) {
        if (seat.seatNumber === "") {
            isValidSeat = false;
            $(`.error-${seat.seatRow}`).text("Seat number is required.");
        } else if (isNaN(parseInt(seat.seatNumber))) {
            isValidSeat = false;
            $(`.error-${seat.seatRow}`).text("Please enter an integer value.");
        } else if (seat.seatNumber <= 0) {
            isValidSeat = false;
            $(`.error-${seat.seatRow}`).text("Seat number must be greater than 0.");
        } else {
            $(`.error-${seat.seatRow}`).text("");
        }
    });

    return isValidName && isValidSeat;
}
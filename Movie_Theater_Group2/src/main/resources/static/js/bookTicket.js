let movieId = 8;
$(document).ready(function () {
    // Gọi hàm để lấy dữ liệu lịch chiếu khi trang web được tải
    getDateTimeSchedule(8); // Truyền vào movieId mẫu
    // Hàm lấy dữ liệu lịch chiếu từ API
    function getDateTimeSchedule(movieId) {
        $.ajax({
            url: '/get-movie-schedule-by-movie-id-for-booking-ticket',
            data: {movieId: movieId},
            type: 'GET',
            contentType: 'application/json',
            success: function (response) {
                const schedulesByDate = response.data.reduce((acc, schedule) => {
                    const date = schedule.scheduleTime.split('T')[0];
                    const time = schedule.scheduleTime.split('T')[1].substr(0, 5);
                    const scheduleId = schedule.scheduleId;
                    if (!acc[date]) acc[date] = [];
                    acc[date].push({time, scheduleId});
                    return acc;
                }, {});

                // Lưu trữ dữ liệu vào localStorage
                localStorage.setItem('schedulesByDate', JSON.stringify(schedulesByDate));
                // Render các ngày lên giao diện
                renderDates(schedulesByDate);
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // Hàm render các ngày lên giao diện
    function renderDates(schedulesByDate) {
        const containerDate = document.getElementById('container-date');
        containerDate.innerHTML = ""; // Xóa các ngày hiện tại
        Object.keys(schedulesByDate).forEach((date, index) => {
            const label = document.createElement('label');
            label.className = 'me-3';
            label.innerHTML = `
                <input type="radio" name="radioDate" ${index === 0 ? 'checked' : ''} value="${date}">
                <span>${date}</span>
            `;
            containerDate.appendChild(label);
        });

        $('#container-date').change(function () {
            const selectedDate = $('input[name="radioDate"]:checked').val();
            const schedulesByDate = JSON.parse(localStorage.getItem('schedulesByDate'));
            updateTimesForSelectedDate(selectedDate, schedulesByDate[selectedDate]);
        });

        // Tự động chọn ngày đầu tiên và hiển thị thời gian tương ứng
        const firstDate = $('input[name="radioDate"]:first').val();
        updateTimesForSelectedDate(firstDate, schedulesByDate[firstDate]);
    }

    function updateTimesForSelectedDate(selectedDate, times) {
        const containerTime = document.getElementById('container-time');
        containerTime.innerHTML = ''; // Xóa các lựa chọn thời gian hiện tại

        times.forEach(({time, scheduleId}, index) => {
            const label = document.createElement('label');
            label.className = 'me-3';
            label.innerHTML = `
                <input type="radio" name="radioTime" ${index === 0 ? 'checked' : ''} data-id="${scheduleId}" onclick="getCheckedRadioDataIdInContainer()">
                <span>${time}</span>
            `;
            containerTime.appendChild(label);
        });
    }
});

function getCheckedRadioDataIdInContainer() {
    // Lấy phần tử container có ID là 'container-time'
    const checkedRadio = document.querySelector('#container-time input[name="radioTime"]:checked');

        var scheduleId =  checkedRadio.getAttribute('data-id');

    getSeatForBookingTicket(movieId, scheduleId);
}
function getSeatForBookingTicket(movieId, scheduleId) {
    $.ajax({
        url: '/get-seat-for-booking-ticket',
        data: {movieId: movieId, scheduleId: scheduleId},
        type: 'GET',
        contentType: 'application/json',
        success: function (response) {
            createColumnsWithCheckboxes(response.data);
        },
        error: function (xhr, status, error) {
            console.error('Error:', error);
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
            const seatStatus = seat.seatStatus;
            const seatType = seat.seatType;
            const seatDeleted = seat.deleted;
            // Create checkbox input
            const checkbox = document.createElement('input');

            checkbox.id = `${row}${seatColumn}`;
            checkbox.type = 'checkbox';
            checkbox.dataset.seatId = seatId;
            checkbox.dataset.movieId = seat.movieId;
            checkbox.dataset.scheduleId = seat.scheduleId;

            if (!seatDeleted) {
                checkbox.checked = seatStatus !== 0;
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
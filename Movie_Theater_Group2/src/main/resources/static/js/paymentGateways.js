
$(document).ready(function () {
    payCancel();
    getTransactionCode();
})

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);

const invoiceId = urlParams.get('invoiceId');
const amountKey = urlParams.get('amount');
const amount = Number(amountKey) * 1000;
let interval;

function payCancel(){
    $("#pay-cancel").on('click', function() {
        Swal.fire({
            title: "Do you want to cancel payment with NetFlex Cinema?",
            showCancelButton: true,
            confirmButtonText: "Yes",
            cancelButtonText: "No",
        }).then((result) => {
            if (result.isConfirmed) {
                cancelOrder();
                // window.location.href = '/payment-error';
            }
        });
    });
}

function startCountdown(duration, displayMinutes, displaySeconds) {
    var timer = duration, minutes, seconds;

    var countdownInterval = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        displayMinutes.textContent = minutes;
        displaySeconds.textContent = seconds;

        if (--timer < 0) {
            clearInterval(countdownInterval);
            displayMinutes.textContent = "00";
            displaySeconds.textContent = "00";
            Swal.fire({
                icon: "warning",
                title: "Payment period expires",
                showConfirmButton: false,
                timer: 3000
            }).then((result) => {
                cancelOrder();
            });

        }

    }, 1000);
}

window.onload = function () {
    var countdownDuration = 60 * 10;
    var displayMinutes = document.querySelector('#minutes');
    var displaySeconds = document.querySelector('#seconds');
    startCountdown(countdownDuration, displayMinutes, displaySeconds);
};

function getTransactionCode() {
    $.ajax({
        url: '/get-transaction-code',
        type: 'GET',
        contentType: 'application/json',
        success: function (response) {
            var transactionCode = response.data;
            console.log('Success:', transactionCode);

            editOderPayment(transactionCode);
            isPaymentComplete(amount, invoiceId);
            interval = setInterval(() => isPaymentComplete(amount, invoiceId), 3000);
        },
        error: function (xhr, status, error) {
            // Code to run if the request fails
            console.log('Error:', error);
        }
    });
}

function isPaymentComplete( amount, invoiceId) {
     var transactionCode = $('#transactionCode').text();
    $.ajax({
        url: '/payment-checking',
        type: 'POST',
        data: {
            transactionCode : transactionCode,
            amount : amount,
            invoiceId : invoiceId,
        },
        beforeSend: function () {
            // lsdRing.removeClass('d-none');
        },
        success: function (response) {
            console.log(response);

         if(response.data === true) {
             clearInterval(interval);
             Swal.fire({
                 icon: "success",
                 title: "Payment success",
                 confirmButtonText: "Close",
             }).then((result) => {
                 /* Read more about isConfirmed, isDenied below */
                 if (result.isConfirmed) {
                     window.location.href = "/home";
                 }
             });
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
            // lsdRing.addClass('d-none');
        }
    });
}

function cancelOrder() {
    $.ajax({
        url: '/cancel-order',
        type: 'POST',
        data: {
            invoiceId: invoiceId
        },
        beforeSend: function () {
            // lsdRing.removeClass('d-none');
        },
        success: function (response) {
            console.log(response);
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
            // lsdRing.addClass('d-none');
            window.location.href = '/payment-error';
        }
    });
}


function editOderPayment(transactionCode) {
    $('#description').text("Pay " + transactionCode);
    $('#transactionCode').text(transactionCode);
    $('#amount').text(amount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'}));
    $('.img-qr').attr('src', `https://img.vietqr.io/image/MB-8990186866886-compact2.png?amount=${amount}&addInfo=Netflex%20${transactionCode}&accountName=NetFlex`);
}


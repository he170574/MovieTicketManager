

$(document).ready(function() {
    addressConfig();
});

function addressConfig(){
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));var maxLength = 20;
    var addressElement = $("span.address");
    var address = addressElement.text();

    if (address.length > maxLength) {
        var shortAddress = address.substring(0, maxLength) + "...";
        addressElement.attr("title", address); // Set the full address as the tooltip text
        addressElement.text(shortAddress);
    }
}



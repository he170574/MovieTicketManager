$(document).ready(function () {
    showPayment();
})

function showPayment(){
        $(document).ready(function () {
            $.ajax({
                url: "https://oauth.casso.vn/v2/userInfo",
                type: "GET",
                // data: {
                //     fromDate: "2024-03-10",
                //     page: 1,
                //     pageSize: 1,
                //     sort: "ASC"
                // },
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Apikey AK_CS.686820b03c8b11efbf32abb139db7a4d.hvp6LFckj6yT35M0rMo7W2XMdozx1FoyXqbKgHlAkImpoFQBdwEAvkIo7ULFKWu4QCeNblE9"
                },
                success: function (initialResponse) {
                    console.log("Response:", initialResponse);
                    // // const hasNF12345 = response.data.records.some(record => record.description.includes("NF12345"));
                    // // console.log(hasNF12345); // In ra true hoặc false
                    // const totalPages = initialResponse.data.totalPages;
                    // fetchAndCheckPage("NF12345", 1, totalPages, pageSize);
                },
                error: function (xhr, status, error) {
                    console.error("Error:", status, error);
                }
            });
        });
}

function fetchAndCheckPage(descriptionToFind, currentPage, totalPages, pageSize) {
    $.ajax({
        url: "https://oauth.casso.vn/v2/transactions",
        type: "GET",
        data: {
            fromDate: "2021-04-01",
            page: currentPage,
            pageSize: pageSize,
            sort: "ASC"
        },
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Apikey AK_CS.686820b03c8b11efbf32abb139db7a4d.hvp6LFckj6yT35M0rMo7W2XMdozx1FoyXqbKgHlAkImpoFQBdwEAvkIo7ULFKWu4QCeNblE9"
        },
        success: function(response) {
            const records = response.data.records;
            const found = records.some(record => record.description.includes(descriptionToFind));

            if (found) {
                console.log(`Found a matching record on page ${currentPage}.`);
            } else {
                console.log(`No matching record found on page ${currentPage}.`);
                if (currentPage < totalPages) {
                    fetchAndCheckPage(descriptionToFind, currentPage + 1, totalPages, pageSize);
                } else {
                    console.log("Checked all pages. No matching record found.");
                }
            }
        },
        error: function(xhr, status, error) {
            console.error("Error fetching page " + currentPage + ": " + error);
        }
    });
}


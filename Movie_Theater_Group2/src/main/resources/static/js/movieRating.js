import { html, render, useState, useEffect, createContext, useContext } from 'https://unpkg.com/htm/preact/standalone.module.js';

const AuthContext = createContext();
const MovieContext = createContext();

function AuthProvider({children}) {
    const [account, setAccount] = useState(null);

    useEffect(() => {
        $.get("/get-account-using", function (response) {
            setAccount(response.data);
        })
    }, [])

    return html`
        <${AuthContext.Provider} value=${account}>${children}</AuthContext.Provider>
    `;
}
function MovieProvider({children}) {
    const [movie, setMovie] = useState(null);

    useEffect(() => {
        setMovie((new URLSearchParams(window.location.search)).get("id"));
    }, [])

    return html`
        <${MovieContext.Provider} value=${movie}>${children}</MovieContext.Provider>
    `;
}



function RatingStatistic() {
    const account = useContext(AuthContext);
    const movieId = useContext(MovieContext);
    const [total, setTotal] = useState(0);
    const [average, setAverage] = useState(0);
    const [count, setCount] = useState({
        "1": 0,
        "2": 0,
        "3": 0,
        "4": 0,
        "5": 0
    })

    useEffect(() => {
        if (!account || !movieId) return;

        $.get('/movie-rating-statistic', {
            movieId: movieId
        }, function (response) {
            console.log(response.data);
            setCount(response.data);
            let total = 0;
            let sum = 0;
            for (let i = 1; i <= 5; i++) {
                total += response.data[i];
                sum += response.data[i] * i;
            }
            if (total !== 0) setAverage(sum / total);
            setTotal(total);
        })
    }, [account, movieId]);

    return html`
        <div>
            <div class="fs-3 fw-bold">RATE STATISTIC</div>
            <div class="d-flex">
                <div class="m-3 text-center">
                    <div class="fs-1">${average.toFixed(1)}</div>
                    <small>${total} RATING</small>
                </div>
                <div class="m-3">
                    ${[...Array(5).keys()].map(e => html`
                        <div class="d-flex align-items-center" style="width: 200px">
                            <div class="me-3">${e + 1}</div>
                            <div class="progress w-100">
                                <div class="progress-bar" role="progressbar"
                                     style="width: ${count[e + 1] / total * 100}%"
                                     aria-valuenow="${count[e + 1] / total * 100}" aria-valuemin="0"
                                     aria-valuemax="100">${count[e + 1] > 0 ? count[e + 1] : ''}
                                </div>
                            </div>
                        </div>
                    `)}
                </div>
            </div>
        </div>
    `
}

function PaginationItem({text, pageNumber, setPageNumber, active}) {
    const onClick = function () {
        if (active) return;
        setPageNumber(pageNumber);
    }
    return html`
        <li class="page-item" onClick=${onClick}><a class="page-link${active ? 'active' : ''}">${text}</a></li>
    `
}

function Pagination({pageNumber, setPageNumber, totalPages}) {
    return html`
        <nav aria-label="Page navigation example">
            <ul class="pagination">
                ${pageNumber > 0 ? html`
                    <${PaginationItem} text="Previous" pageNumber=${pageNumber} setPageNumber=${setPageNumber}/>` : ''}
                ${pageNumber > 0 ? html`
                    <${PaginationItem} text=${pageNumber} pageNumber=${pageNumber - 1} setPageNumber=${setPageNumber}
                                       active=${true}/>` : ''}
                <${PaginationItem} text=${pageNumber + 1} pageNumber=${pageNumber} setPageNumber=${setPageNumber}/>
                ${pageNumber < totalPages - 1 ? html`
                    <${PaginationItem} text=${pageNumber + 2} pageNumber=${pageNumber + 1}
                                       setPageNumber=${setPageNumber}/>` : ''}
                ${pageNumber < totalPages - 1 ? html`
                    <${PaginationItem} text="Next" pageNumber=${pageNumber + 1} setPageNumber=${setPageNumber}/>` : ''}
            </ul>
        </nav>
    `
}

function UserRating() {
    const account = useContext(AuthContext);
    const movieId = useContext(MovieContext);

    const [editable, setEditable] = useState(true);
    const [star, setStar] = useState(0);
    const [content, setContent] = useState("");

    const submitRating = () => {
        $.post("/create-rating", {
            content: content.trim(),
            star: star,
            movieId: movieId
        }, function (response) {
            console.log(response.data);
            Swal.fire({
                title: "Success",
                icon: "success",
                text: response.message,
                confirmButtonText: "Close",
            }).then((result) => {
                setEditable(false);
                window.location.reload();
            });
        }).fail(function (data) {
            Swal.fire({
                title: "Error",
                icon: "error",
                text: data.responseJSON.message,
            })
        });
    }

    useEffect(() => {
        if (!account || !movieId) {
            setEditable(false);
        } else setEditable(true);
        $.get("/get-rating-of-movie-by-current-account", {
            movieId: movieId
        }, function (response) {
            console.log(response.data);
            let data = response.data;
            if (data) {
                setStar(data.star);
                setContent(data.content);
                setEditable(false);
            } else {
                setEditable(true);
            }
        });
    }, [account, movieId]);

    const changeStar = (event) => {
        if (!editable) return;
        let current = parseInt($(event.target).data("star"));
        setStar(current);
    }

    const mouseEnter = (event) => {
        if (!editable) return;
        let target = parseInt($(event.target).data("star"));
        $("#starRating > span").each(function () {
            let current = parseInt($(this).data("star"));
            if (current <= target) {
                $(this).attr("style", "color: gold");
            } else {
                $(this).attr("style", "color: black");
            }
        });
    }
    const mouseLeave = (event) => {
        if (!editable) return;
        $("#starRating > span").each(function () {
            let current = parseInt($(this).data("star"));
            if (current <= star) {
                $(this).attr("style", "color: gold");
            } else {
                $(this).attr("style", "color: black");
            }
        });
    }

    const deleteRating = (event) => {
        Swal.fire({
            title: "Warning",
            icon: "warning",
            text: "Do you want to delete rating?",
            showCancelButton: true
        }).then(result => {
            if (result.isConfirmed) {
                $.get("/delete-rating", {
                    movieId: movieId
                }, function (response) {
                    Swal.fire({
                        title: "Success!",
                        icon: "success",
                        text: response.message,
                        confirmButtonText: "Close",
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.reload();
                        }
                    });
                })
            }
        });
    }

    const editRating = () => {

    }

    const inputContent = (event) => {
        setContent(event.target.value);
    }

    return html`
        <hr />
        <form>
            <div class="fs-3 fw-bold">RATE MOVIE</div>
            <div class="rating-stars fs-2" id="starRating">
                ${[...Array(5).keys()].map(
                        e => html`<span data-star="${e + 1}" onMouseEnter=${mouseEnter} onClick=${changeStar} onMouseLeave=${mouseLeave}
                                        style="color: ${e + 1 <= star ? 'gold' : 'black'};">★</span>`
                )}
            </div>
            <input hidden type="text" name="star" id="formStar" value=${star}/>
            <div class="mb-3">
                <label for="formContent" class="form-label">Your Comment</label>
                <textarea disabled=${!editable} onInput=${inputContent} class="form-control" id="formContent" name="content" rows="4">${content}</textarea>
            </div>
            <div class="d-flex align-items-center">
                <div onclick=${submitRating} class="btn btn-dark btn-submit-rate me-3 ${!editable?'d-none':''}">Submit</div>
                <div onclick=${deleteRating} class="btn btn-danger btn-delete-rate ${editable?'d-none':''}">Delete</div>
            </div>
        </form>
    `
}

function range(start, end) {
    let result = []
    for (let i = start; i <= end; i++) {
        result.push(i);
    }
    return result;
}

function RatingItemStar({star}) {
    return html`
        <div>
            ${range(1, 5).map(e => html`<span style="color: ${e <= star ? 'gold' : 'black'}">★</span>`)}
        </div>
    `
}

function RatingItem({data}) {
    const account = useContext(AuthContext);

    return html`
        <div class="card mb-2">
            <div class="card-body">
                <div class="d-flex">
                    <span class="fw-bold me-3">${data.authorUsername}</span>
                    <${RatingItemStar} star=${data.star}/>
                </div>
                <div>${data.content}</div>
            </div>
        </div>
    `;
}

function MovieRating() {
    const account = useContext(AuthContext);
    const movieId = useContext(MovieContext);

    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [data, setData] = useState([]);
    const [totalPages, setTotalPages] = useState(null);

    useEffect(() => {
        if (!account || !movieId) return;
        $.get("/get-movie-rating", {
            movieId: movieId,
            pageNumber: pageNumber,
            pageSize: pageSize
        }, function (response) {
            setData(response.data.items);
            setTotalPages(response.data.totalPages);
        })
    }, [pageNumber, account, movieId]);

    return html`
        <div class="mt-3">
            <hr />
            <div class="mb-3 fs-3 fw-bold">RATING LIST</div>
            <div class="mb-3">
                <div>
                    ${data.map(row => html`<${RatingItem} data=${row}/>`)}
                </div>
                <div class="d-flex">
                    <${Pagination} pageNumber=${pageNumber} setPageNumber=${setPageNumber} totalPages=${totalPages}/>
                </div>
            </div>
        </div>
    `;
}

function App() {
    return html`
        <${AuthProvider}>
            <${MovieProvider}>
                <div class="container py-3">
                    <${RatingStatistic}/>
                    <${UserRating} />
                    <${MovieRating}/>
                </div>
            </MovieProvider>
        </AuthProvider>
    `;
}

render(html`<${App}/>`, document.getElementById("app"))
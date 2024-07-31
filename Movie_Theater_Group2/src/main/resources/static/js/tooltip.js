const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))

const tooltipTriggerList2 = document.querySelectorAll('[data-bs-toggle="modal"]')
const tooltipList2 = [...tooltipTriggerList2].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))


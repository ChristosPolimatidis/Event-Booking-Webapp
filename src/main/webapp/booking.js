document.addEventListener("DOMContentLoaded", function () {

    const xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8081/hy360_project_war_exploded/BookingServlet", true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            const bookings = JSON.parse(xhr.responseText);
            console.log(bookings);
            displayBookings(bookings);
        } else {
            console.error("Error fetching events: " + xhr.statusText);
        }
    };

    xhr.send();
});

function displayBookings(bookings) {
    const tableBody = document.querySelector("#bookings_table tbody");
    tableBody.innerHTML = "";

    bookings.forEach(booking => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${booking.booking_id}</td>
            <td>${booking.customer_id}</td>
            <td>${booking.event_id}</td>
            <td>${booking.ticket_count}</td>
            <td>${booking.booking_date}</td>
            <td>${booking.total_payment}</td>
            <td>
                <button class="delete-btn" data-id="${booking.booking_id}">Delete</button>
            </td>
        `;

        tableBody.appendChild(row);
    });

    const deleteButtons = document.querySelectorAll(".delete-btn");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const bookingID = this.getAttribute("data-id");
            deleteBooking(bookingID);
        });
    });
}

function deleteBooking(bookingID) {
    if (!confirm("Are you sure you want to delete this booking?")) return;
    console.log(bookingID)
    const xhr = new XMLHttpRequest();
    xhr.open("DELETE", `http://localhost:8081/hy360_project_war_exploded/BookingServlet?booking_id=${bookingID}`, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            alert("Booking deleted successfully!");
            location.reload();
        } else {
            alert("Failed to delete booking.");
        }
    };

    xhr.onerror = function () {
        console.error("Error deleting booking.");
    };

    xhr.send();
}
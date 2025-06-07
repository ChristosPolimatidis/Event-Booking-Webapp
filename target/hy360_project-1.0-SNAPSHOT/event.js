document.getElementById("ticket_panel").addEventListener("click",function (event){
    event.preventDefault();
    window.location.href="ticket.html";
});

document.addEventListener("DOMContentLoaded", function () {

    const xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8081/hy360_project_war_exploded/EventServlet", true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            const events = JSON.parse(xhr.responseText);
            console.log(events);
            displayEvents(events);
        } else {
            console.error("Error fetching events: " + xhr.statusText);
        }
    };

    xhr.send();
});

function displayEvents(events) {
    const tableBody = document.querySelector("#events_table tbody");
    tableBody.innerHTML = "";

    events.forEach(event => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${event.event_id}</td>
            <td>${event.event_name}</td>
            <td>${event.event_date}</td>
            <td>${event.start_time}</td>
            <td>${event.type_event}</td>
            <td>${event.capacity}</td>
            <td>
                <button class="delete-btn" data-id="${event.event_id}">Delete</button>
            </td>
        `;

        tableBody.appendChild(row);
    });

    const deleteButtons = document.querySelectorAll(".delete-btn");
    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const eventId = this.getAttribute("data-id");
            deleteEvent(eventId);
        });
    });
}

function deleteEvent(eventId) {
    if (!confirm("Are you sure you want to delete this event?")) return;
    console.log(eventId)
    const xhr = new XMLHttpRequest();
    xhr.open("DELETE", `http://localhost:8081/hy360_project_war_exploded/EventServlet?event_id=${eventId}`, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            alert("Event deleted successfully!");
            location.reload();
        } else {
            alert("Failed to delete event.");
        }
    };

    xhr.onerror = function () {
        console.error("Error deleting event.");
    };

    xhr.send();
}

document.getElementById("create_event").addEventListener("click",function (event){
    event.preventDefault();

    const data = {
        type_event: document.getElementById("type_event").value,
        event_name: document.getElementById("event_name").value,
        event_date: document.getElementById("event_date").value,
        start_time: document.getElementById("start_time").value,
        capacity: document.getElementById("capacity").value,
    };

    const jsonData = JSON.stringify(data);

    const xhr = new XMLHttpRequest();

    xhr.open('POST', 'http://localhost:8081/hy360_project_war_exploded/EventServlet');

    xhr.onload = function(){
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            alert("Success: " + response.message);
            window.location.reload();
        } else {
            alert("Error: " + xhr.status + " " + xhr.statusText);
        }
    };

    xhr.send(jsonData);
});



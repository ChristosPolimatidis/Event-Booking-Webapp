document.getElementById("create_ticket").addEventListener("click",function (event){
    event.preventDefault();

    const data={
        booking_id: "0",
        event_id: document.getElementById("event_id").value,
        vip_seats : document.getElementById("vip_seats").value,
        general_seats: document.getElementById("general_seats").value,
        student_seats: document.getElementById("student_seats").value,
        child_seats: document.getElementById("child_seats").value,
        vip_price: document.getElementById("vip_price").value,
        general_price: document.getElementById("general_price").value,
        student_price: document.getElementById("student_price").value,
        child_price: document.getElementById("child_price").value
    };

    const jsonData = JSON.stringify(data);

    const xhr = new XMLHttpRequest();

    xhr.open('POST', 'http://localhost:8081/hy360_project_war_exploded/TicketServlet');

    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function() {
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            alert("Success: " + response.message);
            window.location.reload();
        } else {
            alert("Error: " + xhr.status + " " + xhr.statusText);
        }
    };

    xhr.onerror = function() {
        alert("Request failed. Unable to connect to the server.");
    };

    xhr.send(jsonData);

});
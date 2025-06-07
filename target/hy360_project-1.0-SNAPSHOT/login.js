var customerID;

document.getElementById("login").addEventListener("click",function (event){
    event.preventDefault();

    /* getting values from form input fields */
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    /* getting the document field from the error login */
    const errorElement = document.getElementById("loginError");

    if(username.length > 0 && password.length > 0){
        const xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function (){
            if(this.readyState === 4 && this.status === 200){
                const response = JSON.parse(this.responseText);
                if(response.success){

                    document.getElementById("login_section").style.display="none";

                    if(response.username==="admin" && response.password==="admin"){
                        document.getElementById("admin_section").style.display="block";
                        document.getElementById("usernameAdmin").value=response.username;
                        document.getElementById("passwordAdmin").value=response.password;
                    }
                    else /* it is a simple customer */
                    {
                        document.getElementById("customer_section").style.display="block";
                        doCustomer(response.id);
                        customerID=response.id;
                        returnAvailable();
                    }

                }else{
                    errorElement.textContent = "Invalid username or password.";
                }
            }else if (this.readyState === 4){
                errorElement.textContent = "Error during login. Please try again.";
                console.error(`Error checking login:`, this.statusText);
            }
        };
        xhttp.open("GET", `http://localhost:8081/hy360_project_war_exploded/LoginServlet?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`, true);
        xhttp.send();
    }else{
        errorElement.textContent = "Please fill in both username and password.";
    }

});

document.getElementById("register").addEventListener("click",function (event){
    event.preventDefault();
    window.location.href = "register.html";
});

document.getElementById("event_panel").addEventListener("click",function (event){
    event.preventDefault();
    window.location.href = "event.html";
});

document.getElementById("booking_panel").addEventListener("click",function (event){
    event.preventDefault();
    window.location.href="booking.html";
});

document.getElementById("questions").addEventListener("click",function (event){
    event.preventDefault();
    document.getElementById("login_section").style.display="none";
    document.getElementById("questions-container").style.display="block";
    document.getElementById("admin_section").style.display="none";
});

function doCustomer(customer_id){

    const xhr = new XMLHttpRequest();
    xhr.open("GET", `http://localhost:8081/hy360_project_war_exploded/getCustomerBookings?customer_id=${customer_id}`, true);
    console.log(customer_id);
    xhr.onload = function () {
        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            console.log(response);
            displayCustomerBookings(response);
        } else {
            console.error("Error fetching bookings: " + xhr.statusText);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed for customer bookings.");
    };

    xhr.send();
}

function displayCustomerBookings(bookings){
    const tableBody = document.querySelector("#bookings_table tbody");

    tableBody.innerHTML = "";

    if (bookings.length > 0) {
        bookings.forEach(booking => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${booking.booking_id}</td>
                <td>${booking.event_name}</td>
                <td>${booking.ticket_count}</td>
                <td>${booking.booking_date}</td>
                <td>€${booking.total_payment}</td>
            `;

            tableBody.appendChild(row);
        });
    } else {

        const row = document.createElement("tr");
        row.innerHTML = `<td colspan="5" style="text-align: center;">Δεν υπάρχουν κρατήσεις.</td>`;
        tableBody.appendChild(row);
    }
}

/* GIA NA KLEISEI NEO EVENT*/
/* ----------------------------------------------------- */
var global;
function returnAvailable(){
    /* close new booking */

    <!-- booking date (current date)-->
    <!-- customer ID-->
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "http://localhost:8081/hy360_project_war_exploded/RegisterServlet", true); // Κλήση του servlet GetAvailableEvents
    xhr.onload = function (){
        if (xhr.status === 200){
            const response = JSON.parse(xhr.responseText); // Μετατροπή JSON σε Object
            console.log(response.events);
            populateEventDropdown(response.events);
            global=response.events;
        } else {
            console.error("Failed to fetch events");
        }
    };
    xhr.send();
}
let totalPayment={};

function populateEventDropdown(events) {
    const selectEvent = document.getElementById("select_event");
    selectEvent.innerHTML = "<option value=''>Select Event</option>";

    events.forEach(event => {
        const option = document.createElement("option");
        option.value = event.event_id;
        option.textContent = `${event.event_name} - ${event.event_date}`;

        option.setAttribute("data-vip", event.vip_available_seats);
        option.setAttribute("data-general", event.general_available_seats);
        option.setAttribute("data-student", event.student_available_seats);
        option.setAttribute("data-child", event.child_available_seats);

        /* TOTAL PAYMENT */
        totalPayment[event.event_id] = event.total_payment;
        console.log("payment is: ",totalPayment);

        selectEvent.appendChild(option);
    });
}

document.getElementById("select_event").addEventListener("change", (e) => {
    const selectedOption = e.target.options[e.target.selectedIndex]; // Παίρνουμε το επιλεγμένο option

    if (selectedOption && selectedOption.value) {
        console.log("Selected Option:", selectedOption);

        // Ανάκτηση των τιμών από τα custom attributes
        const vipMax = parseInt(selectedOption.getAttribute("data-vip"), 10) || 0;
        const generalMax = parseInt(selectedOption.getAttribute("data-general"), 10) || 0;
        const studentMax = parseInt(selectedOption.getAttribute("data-student"), 10) || 0;
        const childMax = parseInt(selectedOption.getAttribute("data-child"), 10) || 0;

        // Ορισμός των max τιμών στα input fields
        document.getElementById("vip_tickets").max = vipMax;
        document.getElementById("general_tickets").max = generalMax;
        document.getElementById("student_tickets").max = studentMax;
        document.getElementById("child_tickets").max = childMax;

        console.log("Max values set:", { vipMax, generalMax, studentMax, childMax });
    } else {
        console.warn("No valid event selected");
        clearTicketFields();
    }
});

function clearTicketFields() {
    document.getElementById("vip_tickets").max = 0;
    document.getElementById("general_tickets").max = 0;
    document.getElementById("student_tickets").max = 0;
    document.getElementById("child_tickets").max = 0;
}

/* SUBMIT THE BOOKING */
/* --------------------------------------------- */

document.getElementById("create_booking").addEventListener("click",function (event){
    event.preventDefault();

    const eventId = parseInt(document.getElementById("select_event").value);

    const vipSeats = parseInt(document.getElementById("vip_tickets").value) || 0;
    const generalSeats = parseInt(document.getElementById("general_tickets").value) || 0;
    const studentSeats = parseInt(document.getElementById("student_tickets").value) || 0;
    const childSeats = parseInt(document.getElementById("child_tickets").value) || 0;

    const ticketCount = vipSeats + generalSeats + studentSeats + childSeats;

    const totalPay = totalPayment[eventId];

    const bookingDate = new Date().toISOString().split("T")[0];

    const data={
        customer_id: customerID, // Global customer_id
        event_id: eventId,
        ticket_count: ticketCount,
        booking_date: bookingDate,
        total_payment: totalPay,
        vip_seats: vipSeats,
        general_seats: generalSeats,
        student_seats: studentSeats,
        child_seats: childSeats
    }

    const jsonData = JSON.stringify(data);
    console.log(jsonData);
    const xhr = new XMLHttpRequest();

    xhr.open('POST', 'http://localhost:8081/hy360_project_war_exploded/BookingServlet');

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

    xhr.send(jsonData);


});
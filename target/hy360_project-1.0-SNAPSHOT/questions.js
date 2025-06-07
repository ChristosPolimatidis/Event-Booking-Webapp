/* DONE */
function QuestionA_Handler()
{
    console.log("Question A button clicked");

    const event_id = document.getElementById("QuestionA-Input").value;

    const xhr = new XMLHttpRequest();

    xhr.open('GET', `http://localhost:8081/hy360_project_war_exploded/getSeatStatus?event_id=${event_id}`, true);

    xhr.onreadystatechange = function ()
    {
        if (xhr.readyState === 4)
        {
            const consoleDiv = document.getElementById("console");
            if (xhr.status === 200){
                const response = JSON.parse(xhr.responseText);
                consoleDiv.textContent = JSON.stringify(response, null, 4);
                console.log("Response:", response);
            }else
                console.error("Error from /api/questionA:", xhr.statusText);
        }
    };
    xhr.send();
}
/* DONE */
function QuestionB_Handler()
{
    console.log("Question B button clicked");

    const event_id = document.getElementById("QuestionB-Input").value;

    const xhr = new XMLHttpRequest();

    xhr.open('GET', `http://localhost:8081/hy360_project_war_exploded/getEventEarnings?event_id=${event_id}`, true);

    xhr.onreadystatechange = function ()
    {
        if (xhr.readyState === 4)
        {
            const consoleDiv = document.getElementById("console");
            if (xhr.status === 200){
                const response = JSON.parse(xhr.responseText);
                consoleDiv.textContent = JSON.stringify(response, null, 4);
                console.log("Response:", response);
            }else
                console.error("Error from /api/questionA:", xhr.statusText);
        }
    };
    xhr.send();
}
/* DONE */
function QuestionC_Handler()
{
    console.log("Question C button clicked");

    const xhr = new XMLHttpRequest();

    xhr.open('GET', 'http://localhost:8081/hy360_project_war_exploded/getMostPopularEvent', true);

    xhr.onreadystatechange = function ()
    {
        if (xhr.readyState === 4)
        {
            const consoleDiv = document.getElementById("console");
            if (xhr.status === 200){
                const response = JSON.parse(xhr.responseText);
                consoleDiv.textContent = JSON.stringify(response, null, 4);
                console.log("Response:", response);
            }else
                console.error("Error from /api/questionA:", xhr.statusText);
        }
    };
    xhr.send();
}
/* DONE */
function QuestionD_Handler()
{
    console.log("Question D button clicked");

    const start = document.getElementById("QuestionD-Input").value;
    const end = document.getElementById("QuestionD-EndInput").value;
    console.log(start,end);
    const xhr = new XMLHttpRequest();

    xhr.open('GET', `http://localhost:8081/hy360_project_war_exploded/getEventWithMostEarningsTimeRange?start_date=${start}&end_date=${end}`, true);

    xhr.onreadystatechange = function ()
    {
        if (xhr.readyState === 4)
        {
            const consoleDiv = document.getElementById("console");
            if (xhr.status === 200){
                const response = JSON.parse(xhr.responseText);
                consoleDiv.textContent = JSON.stringify(response, null, 4);
                console.log("Response:", response);
            }else
                console.error("Error from /api/questionA:", xhr.statusText);
        }
    };
    xhr.send();
}
/* DONE */
function QuestionE_Handler()
{
    console.log("Question E button clicked");

    const start = document.getElementById("QuestionE-Input").value;
    const end = document.getElementById("QuestionE-EndInput").value;

    const xhr = new XMLHttpRequest();

    xhr.open('GET', `http://localhost:8081/hy360_project_war_exploded/getBookingsForTimePeriod?start_date=${start}&end_date=${end}`, true);

    xhr.onreadystatechange = function ()
    {
        if (xhr.readyState === 4)
        {
            const consoleDiv = document.getElementById("console");
            if (xhr.status === 200){
                const response = JSON.parse(xhr.responseText);
                consoleDiv.textContent = JSON.stringify(response, null, 4);
                console.log("Response:", response);
            }else
                console.error("Error from /api/questionA:", xhr.statusText);
        }
    };
    xhr.send();
}

function QuestionF_Handler()
{
    console.log("Question F button clicked");

    const event_id = document.getElementById("QuestionF-Input").value;
    const seat_type=document.getElementById("TypeSeatMenu").value;

    const xhr = new XMLHttpRequest();

    xhr.open('GET', `http://localhost:8081/hy360_project_war_exploded/QuestionF?event_id=${event_id}&seat_type=${seat_type}`, true);

    xhr.onreadystatechange = function ()
    {
        if (xhr.readyState === 4)
        {
            const consoleDiv = document.getElementById("console");
            if (xhr.status === 200){
                const response = JSON.parse(xhr.responseText);
                consoleDiv.textContent = JSON.stringify(response, null, 4);
                console.log("Response:", response);
            }else
                console.error("Error from /api/questionA:", xhr.statusText);
        }
    };
    xhr.send();
}

document.getElementById('QuestionA-Button').addEventListener('click', QuestionA_Handler);
document.getElementById('QuestionB-Button').addEventListener('click', QuestionB_Handler);
document.getElementById('QuestionC-Button').addEventListener('click', QuestionC_Handler);
document.getElementById('QuestionD-Button').addEventListener('click', QuestionD_Handler);
document.getElementById('QuestionE-Button').addEventListener('click', QuestionE_Handler);
document.getElementById('QuestionF-Button').addEventListener('click', QuestionF_Handler);
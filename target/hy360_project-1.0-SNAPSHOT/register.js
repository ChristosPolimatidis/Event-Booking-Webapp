const passwordF=document.getElementById("password");
const EyeBtn=document.getElementById("eye");

EyeBtn.addEventListener("click",function(){

    if (passwordF.type === "password"){
        passwordF.type ="text";
        EyeBtn.textContent ="🙈";
    }else{
        passwordF.type ="password";
        EyeBtn.textContent ="🙉";
    }
})

document.getElementById('card_expiration').addEventListener('input', function (event) {
    let value = event.target.value;

    value = value.replace(/[^0-9]/g, '').substring(0, 4);

    if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2);
    }

    event.target.value = value;
});

document.getElementById('card_number').addEventListener('input', function (event) {
    let value = event.target.value;

    value = value.replace(/[^0-9]/g, '');

    if (value.length > 0) {
        value = value.match(/.{1,4}/g).join('-');
    }

    event.target.value = value.substring(0, 19);
});

function validateCard() {
    const cardOwner = document.getElementById('card_owner').value.trim();
    const cardNumber = document.getElementById('card_number').value.trim();
    const cardExpiration = document.getElementById('card_expiration').value.trim();
    const cardCVV = document.getElementById('card_cvv').value.trim();

    if (!cardOwner) {
        alert("The name of the cardHolder must be filled!");
        return false;
    }

    const cardNumberRegex = /^\d{4}-\d{4}-\d{4}-\d{4}$/;
    if (!cardNumberRegex.test(cardNumber)) {
        alert("Card number must has this form: xxxx-xxxx-xxxx-xxxx!");
        return false;
    }

    const cardExpirationRegex = /^(0[1-9]|1[0-2])\/\d{2}$/;
    if (!cardExpirationRegex.test(cardExpiration)) {
        alert("The expiration date must has this form: MM/YY!");
        return false;
    }

    const [month, year] = cardExpiration.split('/');
    const currentDate = new Date();
    const expirationDate = new Date(`20${year}`, month - 1);

    if (expirationDate <= currentDate) {
        alert("The card has already expired!");
        return false;
    }

    const cardCVVRegex = /^\d{3}$/;
    if (!cardCVVRegex.test(cardCVV)) {
        alert("CVV must has 3 digits!");
        return false;
    }

    return true;
}

/* SUBMIT THE REGISTER FORM */

document.getElementById("register_customer").addEventListener("submit",function (event){
    event.preventDefault();

    if (!validateCard()) {
        return;
    }
    const cardNumber = document.getElementById("card_number").value.replace(/-/g, ""); /* delete all - */

    const data={
        customer_username: document.getElementById("username").value,
        customer_password: document.getElementById("password").value,
        customer_firstname: document.getElementById("firstname").value,
        customer_lastname: document.getElementById("lastname").value,
        customer_email: document.getElementById("email").value,
        customer_card_owner: document.getElementById("card_owner").value,
        customer_card_number: cardNumber,
        customer_card_expiration: document.getElementById("card_expiration").value,
        customer_card_cvv: document.getElementById("card_cvv").value
    };

    const jsonData = JSON.stringify(data);
    console.log(jsonData);

    const xhr = new XMLHttpRequest();

    xhr.open('POST', 'http://localhost:8081/hy360_project_war_exploded/RegisterServlet');

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
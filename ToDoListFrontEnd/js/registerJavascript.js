


document.getElementById("registerButton").addEventListener("click", Register);


async function Register() {
    //need to collect the data from the form
    let RegisterButton = document.getElementById("registerButton");

    const username = document.getElementById("registerUsername").value.trim();
    const email = document.getElementById("registerEmail").value.trim();
    const password = document.getElementById("registerPassword").value;
    const confirmPassword = document.getElementById("registerConfirmPassword").value;
    const termsAccepted = document.getElementById("registerTerms").checked;

    let formData = [username, email, password, confirmPassword]

    //checks if all fields are filled
    for (let i = 0; i < formData.length; i++) {
        if (formData[i] === "") {
            alert("Please fill out all fields");
            throw new Error("Fields not filled");
        }
    }
    if (termsAccepted === false) {
        alert("You must agree to the terms and conditions");
        throw new Error("Terms not accepted");
    }

    //what happens when event listener is added
    try {
        const url = "http://localhost:8080/v1Home/v1Register";
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                userName: username,
                EMAIL: email,
                password: password,
            })
        })
        await response;
        if (response.status === 200) {
            alert("Registration Successful");
            // setTimeout(() => {
            //     window.location.replace("./LoginPage.html");
            // }, 1000);
            console.log(response.status);
        } else {
            alert("Registration Failed");
            console.log(response.status);
        }
    }catch (err) {
        console.error("Error registering:", err.message);
        console.log(err.message);
        alert("Server error. Please try again later.");
    }
}

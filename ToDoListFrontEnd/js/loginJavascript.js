window.onload = Login;

function Login() {
    document.getElementById("loginForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("loginUsername").value.trim();
        const password = document.getElementById("loginPassword").value;

        try {
            const response = await fetch("http://localhost:8080/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            const data = await response.json();

            if (response.ok && data) {
                localStorage.setItem("jwtToken", data["access_token"]);
                alert("Login successful!");
                window.location.href = "TaskDashboard.html"; // redirect to your main page
            } else {
                alert("Invalid username or password.");
            }
        } catch (err) {
            console.error("Error logging in:", err);
            alert("Server error. Please try again later.");
        }
    });
}
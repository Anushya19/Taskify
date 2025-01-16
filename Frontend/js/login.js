document.getElementById("login-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!email || !password) {
        alert("Please enter both email and password.");
        return;
    }

    const loginButton = document.getElementById("login-btn");
    loginButton.disabled = true;

    try {
        const response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(errorMessage || "Login failed");
        }

        const data = await response.json();
        localStorage.setItem("authToken", data.token);
        alert("Login successful!");
        window.location.href = "tasks.html";
    } catch (error) {
        alert("Login failed: " + error.message);
    } finally {
        loginButton.disabled = false;
    }
});

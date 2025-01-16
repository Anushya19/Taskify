const apiUrl = "http://localhost:8080/api/tasks"; // Replace with your API endpoint

// Helper function to handle API calls with token authentication
async function fetchProtectedRoute(url, options = {}) {
    const token = localStorage.getItem("authToken");
    console.log(token);
    if (!token) {
        alert("Please log in to continue.");
        window.location.href = "index.html"; // Redirect to login if token is missing
        throw new Error("Authentication token not found.");
    }

    const response = await fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    });

    if (response.status === 401) {
        alert("Your session has expired. Please log in again.");
        window.location.href = "index.html"; // Redirect to login page
        throw new Error("Unauthorized access");
    }

    if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || "Error fetching protected route");
    }

    return response.json();
}

// Delete a task by ID
async function deleteTask(id) {
    try {
        await fetchProtectedRoute(`${apiUrl}/delete/${id}`, {
            method: "DELETE",
        });
        alert("Task deleted successfully!");
        fetchTasks(); // Reload tasks after deletion
    } catch (error) {
        console.error("Error deleting task:", error);
    }
}
//completed function
async function markTaskAsCompleted(taskId) {
    try {
        // Use the fetchProtectedRoute function to send the request with token authentication
        const response = await fetchProtectedRoute(`${apiUrl}/update/${taskId}`, {
            method: "PATCH",
            body: JSON.stringify({ status: "completed" }), // Request body with the status change
        });

        // If the response is successful, alert the user
        alert("Task marked as completed!");
    } catch (error) {
        console.error("Error marking task as completed:", error);
    }
}




// Fetch and display tasks grouped by status and priority
async function fetchTasks() {
    try {
        const tasks = await fetchProtectedRoute(`${apiUrl}/all`, { method: "GET" });
        const tasksList = document.getElementById("tasks");

        tasksList.innerHTML = ""; // Clear previous tasks

        // Group tasks by status and priority
        const groupedTasks = {
            pending: { high: [], medium: [], low: [] },
            "in-progress": { high: [], medium: [], low: [] },
            completed: { high: [], medium: [], low: [] },
        };

        tasks.forEach(task => {
            const status = task.status.toLowerCase();
            const priority = task.priority.toLowerCase();

            if (groupedTasks[status] && groupedTasks[status][priority]) {
                groupedTasks[status][priority].push(task);
            }
        });

        // Helper function to create task groups
        const createTaskGroup = (status, priorityGroup) => {
            const statusHeading = document.createElement("h3");
            statusHeading.textContent = `${status.charAt(0).toUpperCase() + status.slice(1)} Tasks`;
            tasksList.appendChild(statusHeading);

            Object.keys(priorityGroup).forEach(priority => {
                if (priorityGroup[priority].length > 0) {
                    const priorityHeading = document.createElement("h4");
                    priorityHeading.textContent = `${priority.charAt(0).toUpperCase() + priority.slice(1)} Priority`;
                    tasksList.appendChild(priorityHeading);

                    const ul = document.createElement("ul");
                    priorityGroup[priority].forEach(task => {
                        const listItem = document.createElement("li");

                        // Create a link for the task title
                        const taskLink = document.createElement("a");
                        taskLink.href = "#";
                        taskLink.textContent = task.title;
                        taskLink.style.cursor = "pointer";
                        taskLink.onclick = (e) => {
                            e.preventDefault();

                            // Toggle description, delete button, and completed button
                            const description = listItem.querySelector(".description");
                            const deleteButton = listItem.querySelector(".delete-button");
                            const completedButton = listItem.querySelector(".completed-button");

                            if (description.style.display === "none") {
                                description.style.display = "block";
                                deleteButton.style.display = "inline-block";
                                completedButton.style.display = "inline-block";
                            } else {
                                description.style.display = "none";
                                deleteButton.style.display = "none";
                                completedButton.style.display = "none";
                            }
                        };

                        listItem.appendChild(taskLink);

                        // Add task description (initially hidden)
                        const description = document.createElement("span");
                        description.className = "description";
                        description.textContent = ` - ${task.description}`;
                        description.style.display = "none";
                        listItem.appendChild(description);

                        // Add delete button (initially hidden)
                        const deleteButton = document.createElement("button");
                        deleteButton.textContent = "Delete";
                        deleteButton.className = "delete-button";
                        deleteButton.style.marginLeft = "10px";
                        deleteButton.style.display = "none";
                        deleteButton.onclick = async () => {
                            if (confirm("Are you sure you want to delete this task?")) {
                                try {
                                    await deleteTask(task.id);

                                    // Remove the task immediately from the DOM
                                    listItem.remove();

                                    alert("Task deleted successfully!");
                                } catch (error) {
                                    console.error("Error deleting task:", error);
                                }
                            }
                        };

                        listItem.appendChild(deleteButton);

                        // Add completed button (initially hidden)
                        const completedButton = document.createElement("button");
                        completedButton.textContent = "Mark as Completed";
                        completedButton.className = "completed-button";
                        completedButton.style.marginLeft = "10px";
                        completedButton.style.display = "none";
                        completedButton.onclick = async () => {
                            if (confirm("Are you sure you want to mark this task as completed?")) {
                                await markTaskAsCompleted(task.id);
                                fetchTasks(); // Reload tasks after marking as completed
                            }
                        };
                        listItem.appendChild(completedButton);

                        ul.appendChild(listItem);
                    });
                    tasksList.appendChild(ul);
                }
            });
        };

        Object.keys(groupedTasks).forEach(status => {
            createTaskGroup(status, groupedTasks[status]);
        });

        tasksList.style.display = "block";
    } catch (error) {
        console.error("Error fetching tasks:", error);
    }
}


// Add new task
document.getElementById("task-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const taskName = document.getElementById("task-name").value.trim();
    const taskDesc = document.getElementById("task-desc").value.trim();
    const taskStatus = document.getElementById("task-status").value;
    const taskPriority = document.getElementById("task-priority").value;
    const taskDueDate = document.getElementById("task-due-date").value;

    if (!taskName || !taskDesc || !taskStatus || !taskPriority) {
        alert("Please fill in all required fields.");
        return;
    }

    if (taskDueDate && new Date(taskDueDate) <= new Date()) {
        alert("Please provide a valid future due date.");
        return;
    }

    const newTask = {
        title: taskName,
        description: taskDesc,
        status: taskStatus,
        priority: taskPriority,
        dueDate: taskDueDate || null,
    };

    try {
        await fetchProtectedRoute(`${apiUrl}/create`, {
            method: "POST",
            body: JSON.stringify(newTask),
        });
        alert("Task added successfully!");
        document.getElementById("task-form").reset();
        fetchTasks(); // Reload tasks
    } catch (error) {
        console.error("Error adding task:", error);
    }
});

// Log out
document.getElementById("logout-btn").addEventListener("click", () => {
    localStorage.removeItem("authToken");
    alert("Logged out successfully!");
    window.location.href = "index.html";
});

// Load tasks on page load
document.getElementById("show-tasks-btn").addEventListener("click", fetchTasks);

// Login check on page load

document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("authToken");
    if (!token) {
        alert("Please log in to continue.");
        window.location.href = "index.html";
        return;
    }

});


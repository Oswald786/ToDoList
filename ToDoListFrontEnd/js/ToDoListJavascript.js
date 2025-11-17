window.onload = GetTasks;


async function GetTasks() {
    try {
        console.log("Getting Tasks");
        let token = localStorage.getItem("jwtToken");
        const url = "http://localhost:8080/v1taskManagementController/getTasks";
        const response = await fetch(url,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });
        const data = await response.json();

        const taskListDomElement = document.getElementById("taskListDisplay");
        taskListDomElement.innerHTML = "";
        for (let i = 0; i < data.length; i++) {
            let currentTask = data[i];
            taskListDomElement.innerHTML +=
                `<div class="container-md m-7">
                    <li class="m-3">
                    <div class='' value="${currentTask.id}">
                    <p>Task Name: <span>${currentTask.taskName}</span></p>
                    <p>Task Description: <span>${currentTask.taskDescription}</span></p>
                    <p>Task Type: <span>${currentTask.taskType}</span></p>
                    <p>Task Level: <span>${currentTask.taskLevel}</span></p>
                    <div class="container-md m-7">
                    <button class='removeTask container-md' data-id="${currentTask.id}" onclick="removeTask(${currentTask.id})">Remove Task</button>
                    <button class='editTask container-md' data-id="${currentTask.id}" onclick="BringUpEditTaskMenu(${currentTask.id})">Edit Task</button>
                    <div class='editTaskMenu' value="${currentTask.id}" style="display: none;">
                    <label for="TaskAttributeToUpdate">Task Attribute To Update</label>
                    <select name="TaskAttributeToUpdate" id="TaskAttributeToUpdate_${currentTask.id}">
                        <option value="taskName">Task Name</option>
                        <option value="taskDescription">Task Description</option>
                        <option value="taskType">Task Type</option>
                        <option value="taskLevel">Task Level</option>
                    </select>
                    <label for="TaskAttributeReplacmentValue">Task Attribute Value</label>
                    <input type="text" name="TaskAttributeReplacmentValue" id="TaskAttributeValue_${currentTask.id}">
                    <button class='updateTask' value="${currentTask.id}" onclick="updateTaskHandler(${currentTask.id},)">Update Task</button>
                    </input>
                    </div>
                    </div>
                </div>
                </li>
                </div>`;
        }
        console.log(data);
    } catch (err) {
        console.log(err);
    }
}
async function updateTaskHandler(taskID) {
    let taskAttributeToUpdate = document.getElementById(`TaskAttributeToUpdate_${taskID}`).value;
    let taskAttributeValue = document.getElementById(`TaskAttributeValue_${taskID}`).value;
    await updateTask(taskID,taskAttributeToUpdate,taskAttributeValue);
}

async function AddTask() {
    const taskName = document.getElementById("taskName").value;
    const taskType = document.getElementById("taskType").value;
    const taskLevel = document.getElementById("taskLevel").value;
    const taskDescription = document.getElementById("taskDescription").value;
    const url = "http://localhost:8080/v1taskManagementController/createTask";
    let responseText = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
        },
        body: JSON.stringify({
            taskObjectModel : {
                taskName: taskName,
                taskType: taskType,
                taskLevel: taskLevel,
                taskDescription: taskDescription
            }
        })
    })
    if (responseText.status === 200) {
        alert("Task Added");
        await GetTasks();
    }
    else {
        alert("Task Not Added");
    }
    console.log(responseText.status);
    console.log(responseText.statusText);
    console.log(responseText.json())

}

async function removeTask(taskID) {
    const url = "http://localhost:8080/v1taskManagementController/deleteTask";
    let responseText = await fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
        },
        body: JSON.stringify({
            "id" : taskID
        })
    })
    if (responseText.status === 200) {
        alert("Task Removed");
        await GetTasks();
    }
    else {
        alert("Task Not Removed");
    }
    console.log(responseText.status);
    console.log(responseText.statusText);
    console.log(responseText.json())
}

async function updateTask(taskID,CatagoryToUpdate,ReplacementValue) {
    //Information Needed to update task

    const url = "http://localhost:8080/v1taskManagementController/updateTask";
    let response = await fetch(url,
        {
            method: "POST",
            headers : {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
            },
            body: JSON.stringify({
                updateTaskRequestPackage :   {
                id : taskID,
                fieldToUpdate : CatagoryToUpdate,
                replacementValue: ReplacementValue
                }
                }
            )
        })
    if (response.status === 200) {
        alert("Task Updated");
        await GetTasks();
    }
    else {
        alert("Task Not Updated");
    }
    console.log(response.status);
    console.log(response.statusText);
    console.log(response.json())
}

///VERSION 2.0 BABY



//API Request Helper
async function apiRequest(url, method, body = null) {
    let preBuiltRequestBody = {
        method: method,
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
        },
    }

    if (body && method !== "GET") {
        preBuiltRequestBody.body = JSON.stringify(body);
    }

    const response = await fetch(url,preBuiltRequestBody);

    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
}

function newTaskBuilder(taskName, taskType, taskLevel, taskDescription) {
    return {
        taskName: taskName,
        taskType: taskType,
        taskLevel: taskLevel,
        taskDescription: taskDescription
    };
}

async function addTask(taskObjectModel) {
    return await apiRequest("http://localhost:8080/v1taskManagementController/createTask", "POST", taskObjectModel);
}

async function getTasks() {
    return await apiRequest("http://localhost:8080/v1taskManagementController/getTasks", "GET");
}

//remove task when selected

//update taks when selected

//function to build the taks when it is retrieved from the database

//add event listener to the buttons

//BRINGS UP THE EDIT TASK MENU
function BringUpEditTaskMenu(taskId) {
    let currentStyle = document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display;
    if (currentStyle === "block") {
        document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display = "none";
    } else if (currentStyle === "none") {
        document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display = "block";
    }
}

//Event Listener for the Add Task Button
function attachTaskEventListeners() {
    document.querySelectorAll(".editTaskMenu").forEach(button => {
        button.addEventListener("click", (e) => {
            let taskId = e.target.getAttribute("value");
            BringUpEditTaskMenu(taskId);
        })
    })
}


window.onload = GetTasks;


async function GetTasks() {
    try {
        console.log("Getting Tasks");
        const url = "http://localhost:8080/v1taskManagmentController/getTasks";
        const response = await fetch(url);
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
                    <button class='removeTask container-md' value="${currentTask.id}" onclick="removeTask(${currentTask.id})">Remove Task</button>
                    <button class='editTask container-md' value="${currentTask.id}" onclick="BringUpEditTaskMenu(${currentTask.id})">Edit Task</button>
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

function BringUpEditTaskMenu(taskId) {
    let currentStyle = document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display;
    if (currentStyle === "block") {
        document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display = "none";
    } else if (currentStyle === "none") {
        document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display = "block";
    }
}

async function AddTask() {
    const taskName = document.getElementById("taskName").value;
    const taskType = document.getElementById("taskType").value;
    const taskLevel = document.getElementById("taskLevel").value;
    const taskDescription = document.getElementById("taskDescription").value;
    const url = "http://localhost:8080/v1taskManagmentController/createTask";
    let responseText = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
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
    const url = "http://localhost:8080/v1taskManagmentController/deleteTask";
    let responseText = await fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
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

    const url = "http://localhost:8080/v1taskManagmentController/updateTask";
    let response = await fetch(url,
        {
            method: "POST",
            headers : {
                "Content-Type": "application/json"
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

//Register Page Content Goes Here

function CheckTermsAndConditionsChecked() {return document.getElementById("termsAndConditions").checked;}

function CheckPasswordMatch() {
    let password = document.getElementById("password").value;
    let confirmPassword = document.getElementById("confirmPassword").value;
    return password === confirmPassword;
}

function CheckPasswordLength() {return document.getElementById("password").value.length >= 8;}

function checkValidUsername() {return document.getElementById("username").value.length >= 3;}

function checkValidEmail() {return document.getElementById("email").value.length >= 3;}

function checkAllFieldsFilled() {
    if (CheckTermsAndConditionsChecked() === false) {
        alert("You must agree to the terms and conditions");
        return false;
    }
    if (CheckPasswordMatch() === false) {
        alert("Passwords do not match");
        return false;
    }
    if (CheckPasswordLength() === false) {
        alert("Password must be at least 8 characters long");
    }
    if (checkValidUsername() === false) {
        alert("Username must be at least 3 characters long");
    }
    if (checkValidEmail() === false) {
        alert("Email must be at least 3 characters long");
    }
    return CheckTermsAndConditionsChecked() && CheckPasswordMatch() && CheckPasswordLength() && checkValidUsername() && checkValidEmail();
}


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
                `<li>
                    <div class='task' value="${currentTask.id}">
                    <p>Task Name: <span>${currentTask.taskName}</span></p>
                    <p>Task Description: <span>${currentTask.taskDescription}</span></p>
                    <p>Task Type: <span>${currentTask.taskType}</span></p>
                    <p>Task Level: <span>${currentTask.taskLevel}</span></p>
                    <button class='removeTask' value="${currentTask.id}" onclick="removeTask(${currentTask.id})">Remove Task</button>
                    <button class='editTask' value="${currentTask.id}" onclick="BringUpEditTaskMenu(${currentTask.id})">Edit Task</button>
                    <div class='editTaskMenu' value="${currentTask.id}" style="display: none;">
                    <label for="TaskAttributeToUpdate">Task Attribute To Update</label>
                    <select name="TaskAttributeToUpdate" id="TaskAttributeToUpdate">
                        <option value="taskName">Task Name</option>
                        <option value="taskDescription">Task Description</option>
                        <option value="taskType">Task Type</option>
                        <option value="taskLevel">Task Level</option>
                    </select>
                    <label for="TaskAttributeReplacmentValue">Task Attribute Value</label>
                    <input type="text" name="TaskAttributeReplacmentValue" id="TaskAttributeValue">
                    <button class='updateTask' value="${currentTask.id}" onclick="">Update Task</button>
                    </input>
                    </div>
                </div>
                </li>`;
        }
        console.log(data);
    } catch (err) {
        console.log(err);
    }
}

function BringUpEditTaskMenu(taskId) {
    let currentStyle = document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display;
    if (currentStyle === "block") {
        document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display = "none";
        return;
    } else if (currentStyle === "none") {
        document.querySelector(`.editTaskMenu[value="${taskId}"]`).style.display = "block";
        return;
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
                updateTaskRequestPackage :{
                id : taskID,
                fieldToUpdate : CatagoryToUpdate,
                replacementValue: ReplacementValue
                }
                }
            )
        })
}
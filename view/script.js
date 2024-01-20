const url = "http://localhost:8080/task/user/1";

function hideLoader() {
    document.getElementById("loading").style.display = "none";
}

function show(tasks) {
    let tab = `
    <thead>
        <th scope="col" class="bg-dark text-white">#</th>
        <th scope="col" class="bg-dark text-white">Description</th>
        <th scope="col" class="bg-dark text-white">Username</th>
        <th scope="col" class="bg-dark text-white" >User Id</th>
    </thead>
    `

    for (let task of tasks) {
        tab+= `
            <tr>
                <td scope="row" class="bg-dark text-white">${task.id}</td>
                <td class="bg-dark text-white">${task.description}</td>
                <td class="bg-dark text-white">${task.user.username}</td>
                <td class="bg-dark text-white">${task.user.id}</td>
            </tr>
        `;
        
    }

    document.getElementById("tasks").innerHTML = tab;
}
async function getApi(url) {
    const response = await fetch(url, {method: "GET"});

    var data = await response.json();
    console.log(data);

    if(response)
        hideLoader();

    show(data);
}

getApi(url);
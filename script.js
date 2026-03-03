function login(){
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    if(username === "" || password === ""){
        message.style.color = "red";
        message.innerText = "Please fill all fields!";
    } 
    else if(username === "admin" && password === "1234"){
        message.style.color = "green";
        message.innerText = "Login Successful!";
    } 
    else{
        message.style.color = "red";
        message.innerText = "Invalid credentials!";
    }
}
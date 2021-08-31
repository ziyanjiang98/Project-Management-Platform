function isLoginEmpty(){
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    if(username.length == 0 || password.length == 0){
        alert("Username or password is empty");
        return false;
    }
    return true;
}
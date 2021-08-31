function isLoginEmpty(){
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    if(username.length == 0 || password.length == 0){
        alert("用户名或密码为空！");
        return false;
    }
    return true;
}
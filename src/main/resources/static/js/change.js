function isChangeFormLegal(){
    if(!isChangeEmpty()){
        return false;
    }
    if(!checkChangeFormats()){
        return false;
    }
    if(!confirmChangePassword()){
        return false;
    }
}

function isChangeEmpty(){
    var username = document.getElementById("username").value;
    var password1 = document.getElementById("password1").value;
    var password2 = document.getElementById("password2").value;
    var phone = document.getElementById("phone").value;
    var pin = document.getElementById("pin").value;
    if(username.length == 0 || password1.length == 0 || password2.length == 0 || phone.length == 0 || pin.length == 0){
        alert("User information is empty!");
        return false;
    }
    return true;
}

function checkChangeFormats(){
    if(!checkChangeUsernameFormat()){
        alert("Username is illegal! Requirement: Length is between 4 and 16. Only letters, numbers, _ and - is allowed!");
        return false;
    }
    if(!checkChangePasswordFormat()){
        alert("Password is illegal! Requirement: Length is between 4 and 16. Only letters, numbers, _ and - is allowed!");
        return false;
    }
    if(!checkChangePhoneFormat()){
        alert("Phone number is illegal!");
        return false;
    }
    return true;
}

function checkChangeUsernameFormat(){
    var format = /^[a-zA-Z0-9_-]{4,16}$/;
    if(format.test(document.getElementById("username").value)) {
        return true;
    }
    else {
        return false;
    }
}

function checkChangePasswordFormat(){
    var format = /^[a-zA-Z0-9_-]{4,16}$/;
    if(format.test(document.getElementById("password1").value)) {
        return true;
    }
    else {
        return false;
    }
}


function confirmChangePassword() {
    var pw1 = document.getElementById("password1").value;
    var pw2 = document.getElementById("password2").value;
    if (pw1 == pw2) {
        return true;
    } else {
        alert("Password is not same!");
        return false;
    }
}

function checkChangePhoneFormat() {
    var format = /^[1][3,4,5,7,8,9][0-9]{9}$/;
    if(format.test(document.getElementById("phone").value)) {
        return true;
    }
    else {
        return false;
    }
}
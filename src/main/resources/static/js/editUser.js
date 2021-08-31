function isEditUserEmpty(){
    var level = document.getElementById("level").value;

    if(level.length == 0){
        alert("User level is empty");
        return false;
    }
    return true;
}
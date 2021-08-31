function isEditResourceEmpty(){
    var name = document.getElementById("name").value;
    var unit = document.getElementById("unit").value;
    var detail = document.getElementById("detail").value;

    if(name.length == 0 || unit.length == 0){
        alert("Resource information is empty");
        return false;
    }
    return true;
}
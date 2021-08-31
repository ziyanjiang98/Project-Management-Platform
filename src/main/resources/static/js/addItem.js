function isAddItemEmpty(){
    for (const resource of document.getElementsByName("resource")) {
        if(resource.value == ""){
            alert("Resource type is not selected!");
            return false;
        }
    }
    for (const count of document.getElementsByName("resource")) {
        if(count.value == 0){
            alert("Resource amount is 0!");
            return false;
        }
    }
}
$(document).ready(function(){
    $("#nextPage").click(function(){
        var totalPage = parseInt($("#totalPage").text());
        var pageNum = parseInt($("#pageNum").text());
        if(totalPage == pageNum){
            alert("This is the last page!");
            return false;
        }else{
            return true;
        }
    });
});
$(document).ready(function(){
    $("#lastPage").click(function(){
        var pageNum = parseInt($("#pageNum").text());
        if(pageNum == 1){
            alert("This is the first page!");
            return false;
        }else{
            return true;
        }
    });
});
$(document).ready(function(){
    $("#goTo").click(function(){
        var pageSize = parseInt($("#pageSize").text());
        var goToPage = parseInt($("#goToPage").val());
        var urlArr = location.href.split("?");
        var url = urlArr[0];
        var goToUrl = url + "?" + "pageNum=" + goToPage + "&pageSize=" + pageSize;
        location.href = goToUrl;
    });
});
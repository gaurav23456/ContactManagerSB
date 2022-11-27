console.log("working")

const toggleSidebar = () =>{
    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
        console.log("visible");
    }else{
        
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}

const search = () =>{
    console.log("clicked...");

    let query = $("#search-input").val();
    

    if(query==""){
        $(".search-result").hide()
    }else{
        console.log(query);
        let url = `http://localhost:58318/search/${query}`;

    fetch(url)
    .then((response)=>{
        return response.json();
    }).then((data)=>{
        console.log(data);
    });
        $(".search-result").show()
    }

    

}
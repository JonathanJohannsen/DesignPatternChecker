
function checkGit()
{
    var repoUrl = btoa($('#gitURL').val());
    if(repoUrl)
    {
        $.ajax(
            {
                type : "POST",
                url : "/patternChecker/" + repoUrl + "/checkGit",
                data : 
                {
                },
                success : function(result) 
                {
                    window.location.href = "/patternChecker/Results";
                },
                error: function (jqXHR, exception) 
                {
                    alert("Please enter a valid repository URL.");
                }
            });
    } 
     
    else 
    {
        alert("Not a Git URL!");
    }
    
}

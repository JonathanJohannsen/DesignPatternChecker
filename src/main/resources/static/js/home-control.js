
function checkGit()
{
    var repoUrl = btoa($('#gitURL').val());
    if(repoUrl)
    {
        $('#loading').html('<img src="http://preloaders.net/preloaders/287/Filling%20broken%20ring.gif"> loading...this may take a few moments');
        $.ajax(
            {
                type : "POST",
                url : "/patternChecker/" + repoUrl + "/checkGit",
                data : 
                {
                },
                success : function(result) 
                {
                    $('#loaderImage').hide();
                    window.location.href = "/patternChecker/Home";
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

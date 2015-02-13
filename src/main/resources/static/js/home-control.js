/**
 * Get the health check status 
 * 
 * @param userId
 * @param photoId
 */
function healthCheck() {
	$.ajax(
			{
				type : "GET",
				url  : "/iphoto/ping",
				data : {
				},
				success : function(result) {
					$('#status').text(result);
					sendStatus(result);
				},
				error: function (jqXHR, exception) {
					$('#status').text("Failed to get the status");
					sendStatus("Failed");
				}
			});
}

function sendStatus(result) {
	$.ajax(
			{
				type : "POST",
				url  : "http://course.yusun.io:8080/status",
				data : {
					data : result
				},
				success : function(result) {
					
				},
				error: function (jqXHR, exception) {
					console.log("Failed to send the status");
				}
			});
}

/**
 * Delete the photo 
 * 
 * @param userId
 * @param photoId
 */
function deletePhoto(userId, photoId) {
	$.ajax(
			{
				type : "DELETE",
				url  : "/iphoto/" + userId + "/photo/" + photoId,
				data : {
				},
				success : function(result) {
					location.reload();
				},
				error: function (jqXHR, exception) {
					alert("Failed to delete the photo.");
				}
			});
}

/**
 * Check the selected filters for the photo and 
 * make the HTTP call to trigger the process.
 * 
 * @param userId
 * @param photoId
 */
function applyFilters(userId, photoId) {
	// loop all the checkboxes by class name 
	// and check for the selected filters
	// for the specific photo
	var filters = [];
	$('.filter-checkbox').each(function(i, obj) {
		var checkboxId = $(obj).attr('id');
		var checked = $(obj).prop('checked');
		var filterName = $(obj).attr("name");
		if (checkboxId == photoId && checked) {
			filters.push(filterName);
		}
	});
	
	if (filters.length > 0) {
		var filtersStr = filters.join("-");
		$.ajax(
				{
					type : "GET",
					url  : "/iphoto/" + userId + "/photo/" + photoId + "/filter/" + filtersStr,
					data : {
					},
					success : function(result) 
					{
						location.reload();
						//alert("success");
					},
					error: function (jqXHR, exception) {
						alert("Failed to apply the filters.");
					}
				});
	}
}

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

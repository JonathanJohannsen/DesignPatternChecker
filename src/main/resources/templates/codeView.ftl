<html>

<head>
    <title>Design Pattern Checker</title>
    <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>    
    <script src="/js/home-control.js"></script>
</head>

<body>
    
    <!-- Display the code of the class -->
    <#if codeToShow?? && codeToShow?has_content>
    <hr>
    <div>
        <label>
            <pre>${codeToShow}</pre>
        </label>
    </div> 
    </#if>

</body>

</html>
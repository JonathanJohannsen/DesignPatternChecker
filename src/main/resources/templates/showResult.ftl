<html>

<head>
    <title>Design Pattern Checker</title>
    <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>    
    <script src="/js/home-control.js"></script>
</head>

<body>

    <h2>Results</h2>
    
    <!-- Submission box for a git url -->
    <hr>
    <div>
        <div>
            <label>Enter a another url of a public github repository:</label>
            <input id="gitURL" type="text" size="35"></input><button onclick="checkGit()">Submit</button>
        </div>
    </div>
    
    <!-- Singleton Names -->
    <hr>
    <div>
        <h2>Singletons:</h2> 
        <label>The following instances of Singleton were found in the repository</label>   
        <ul>
            <#if singleton??>
            <#list singleton as singletonName>
              <li>
                <div>
                    <div>
                        ${singletonName}
                    </div>
                </div>
              </li>
            </#list>
            </#if>
        </ul>
    </div>  
    
    <!-- Prototype Names -->
    <hr>
    <div>
        <h2>Prototypes:</h2> 
        <label>The following instances of Prototype were found in the repository</label>   
        <ul>
            <#if prototype??>
            <#list prototype as prototypeName>
              <li>
                <div>
                    <div>
                        ${prototypeName}
                    </div>
                </div>
              </li>
            </#list>
            </#if>
        </ul>
    </div>  
    
    <!-- Class Names -->
    <hr>
    <div>
        <h2>Classes:</h2> 
        <label>The following classes were found in the repository</label>   
        <ul>
            <#if classes??>
            <#list classes as className>
              <li>
                <div>
                    <div>
                        ${className}
                    </div>
                </div>
              </li>
            </#list>
            </#if>
        </ul>
    </div>  
    
    <!-- Method Names -->
    <hr>
    <div>
        <h2>Methods:</h2> 
        <label>The following methods were found in the repository</label>   
        <ul>
            <#if methods??>
            <#list methods as methodName>
              <li>
                <div>
                    <div>
                        ${methodName}
                    </div>
                </div>
              </li>
            </#list>
            </#if>
        </ul>
    </div>        

</body>

</html>
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
    
    <!-- Singleton Errors -->
    <#if singletonErrors?? && singletonErrors?has_content>
    <hr>
    <div>
        <h2>Possible Singleton Errors:</h2> 
        <label>The following classes might be using Singleton, but are in error</label>   
        <ul>
            <#list singletonErrors as singletonError>
              <li>
                <div>
                    <div>
                        ${singletonError.getClassName()} : <i>
                        ${singletonError.getErrorReason()} </i>
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
    
        <!-- Subject Names -->
    <hr>
    <div>
        <h2>Subjects:</h2> 
        <label>The following instances of Subject were found in the repository</label>   
        <ul>
            <#if subject??>
            <#list subject as subjectInfo>
              <li>
                <div>
                    <div>
                      ${subjectInfo.getSubjectName()}
                      <#if subjectInfo.getObservers()?has_content>
                         <ul>
                         <lh>Observers:</lh>
                         <#list subjectInfo.getObservers() as observerName>
                           <div>
                             <li>
                               ${observerName}
                             </li>
                           </div>
                         </#list>
                        </ul>
                       </#if>
                    </div>
                </div>
              </li>
            </#list>
            </#if>
        </ul>
    </div>  
    
    <!-- Subject Errors -->
    <#if subjectErrors?? && subjectErrors?has_content>
    <hr>
    <div>
        <h2>Possible Subject Errors:</h2> 
        <label>The following classes might be using Subject, but are in error</label>   
        <ul>
            <#list subjectErrors as subjectError>
              <li>
                <div>
                    <div>
                        ${subjectError.getClassName()} : <i>
                        ${subjectError.getErrorReason()} </i>
                    </div>
                </div>
              </li>
            </#list>
            </#if>
        </ul>
    </div>   

</body>

</html>
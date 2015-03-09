<html>

<head>
    <title>Design Pattern Checker</title>
    <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>    
    <script src="/js/home-control.js"></script>
    <style>
    
    body 
    {
        font:11px Verdana, Arial, Helvetica, sans-serif; 
        color:#333;
    }
    
    #codeLink
    {
        border: 0;
        padding: 0;
        display: inline;
        background: none;
        text-decoration: underline;
        color: blue;
        margin-bottom:-10px;
        margin-top: -1px;
    }
    button:hover 
    {
        cursor: pointer;
    }
    
    #loading
    {
        margin-top: 7px;
    }
    
    #text
    {
        font-size: 130%;
        margin-bottom:5px;
    }
    
    #enter
    {
        margin-left: 50px;
    }
    
    .resultTable td
    {
        padding-left: 5px;
        font:11px Verdana, Arial, Helvetica, sans-serif; 
        color:#333;
    }
    
    #listHeader
    {
        margin-top: -1px;
        margin-bottom: -1px;
        margin-left: -40px;
    }
    
    #mainTitle
    {
        color: #333; font-family: 'Droid serif', serif; font-size: 36px; font-weight: 400; 
        font-style: italic; line-height: 44px; margin: 0 0 12px; 
        text-align: left; 
    }
    </style>
    <link rel="stylesheet" type="text/css" href="Style.css" />
</head>

<body>
    <h2 id="mainTitle">Design Pattern Checker</h2>
    <!-- Submission box for a git url -->
    <div id = "enter">
        <div>
            <div id="text">
                <label>Enter a url of a public github repository:</label>
            </div>
            <#if gitURL??>
                <input id="gitURL" type="text" size="60" value="${gitURL}"></input><button onclick="checkGit()">Submit</button>
            <#else>
                <input id="gitURL" type="text" size="60"></input><button onclick="checkGit()">Submit</button>            
            </#if>
        </div>
    </div>  
    <div id="loading"></div> 
    <hr>
    <#if gitURL??>
        <h1 id = "mainTitle">Results</h1>
    </#if>
    
    <!-- Singleton Names -->
    <#if singleton?? && singleton?has_content>
    <table class = "resultTable"><tr><td valign="top">
        <h2>Singleton:</h2> 
        <label>The following instances of Singleton were found in the repository</label>   
            <ul>
                <#list singleton as singletonInfo>
                  <li>
                  <form target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${singletonInfo.getCode()}" />
                        <button id="codeLink">${singletonInfo.getSingletonName()}</button>
                    </form>
                  </li>
                </#list>
            </ul>
        </td>
        <#if !singletonErrors?has_content>
            </tr></table>
        </#if>
    </#if>
    
    <!-- Singleton Errors -->
    <#if singletonErrors?? && singletonErrors?has_content>
        <#if !singleton?has_content>
                <table class="resultTable"><tr><td valign="top">
            <#else>
                <td style='padding-left: 50px;' valign="top">
        </#if>
            <h2>Possible Singleton Errors:</h2> 
            <label>The following classes might be using Singleton, but are in error</label>   
            <ul>
                <#list singletonErrors as singletonError>
                  <li>
                  <form style="display:inline;" target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${singletonError.getCode()}" />
                        <button id="codeLink">${singletonError.getClassName()}</button>
                    </form> &nbsp;
                    <i>${singletonError.getErrorReason()} </i>
                  </li>
                </#list>
            </ul>
        </td></tr></table>
    </#if> 
    
    <#if singletonErrors?has_content || singleton?has_content>
        <hr>
    </#if>
    
    <!-- Prototype Names -->
    <#if prototype?? && prototype?has_content>
    <table class = "resultTable"><tr><td valign="top">
        <h2>Prototype:</h2> 
        <label>The following instances of Prototype were found in the repository</label>   
            <ul>
                <#list prototype as prototypeInfo>
                  <li>
                    <form target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${prototypeInfo.getCode()}" />
                        <button id="codeLink">${prototypeInfo.getPrototypeName()}</button>
                    </form>
                  </li>
                </#list>
            </ul>
        </td>
        <#if !prototypeErrors?has_content>
            </tr></table>
        </#if>
    </#if>
    
    <!-- Prototype Errors -->
    <#if prototypeErrors?? && prototypeErrors?has_content>
        <#if !prototype?has_content>
                <table class="resultTable"><tr><td valign="top">
            <#else>
                <td style='padding-left: 50px;' valign="top">
        </#if>
            <h2>Possible Prototype Errors:</h2> 
            <label>The following classes might be using Prototype, but are in error</label>   
            <ul>
                <#list prototypeErrors as prototypeError>
                  <li>
                    <form style="display:inline;" target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${prototypeError.getCode()}" />
                        <button id="codeLink">${prototypeError.getClassName()}</button>
                    </form> &nbsp;
                    <i>${prototypeError.getErrorReason()} </i>
                  </li>
                </#list>
            </ul>
        </td></tr></table>
    </#if> 
    
    <#if prototypeErrors?has_content || prototype?has_content>
        <hr>
    </#if>
    
    <!-- Subject Names -->
    <#if subject?? && subject?has_content>
    <table class = "resultTable"><tr><td valign="top">
        <h2>Subject:</h2> 
        <label>The following instances of Subject were found in the repository</label>   
            <ul>
              <#list subject as subjectInfo>
              <li>
                <div>
                    <div>
                    <form target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${subjectInfo.getCode()}" />
                        <button id="codeLink">${subjectInfo.getSubjectName()}</button>
                    </form>
                      <#if subjectInfo.getObservers()?has_content>
                         <ul>
                         <h4 id="listHeader">Observers:</h4>
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
            </ul>
        </td>
        <#if !subjectErrors?has_content>
            </tr></table>
        </#if>
    </#if>
    
    <!-- Subject Errors -->
    <#if subjectErrors?? && subjectErrors?has_content>
        <#if !subject?has_content>
                <table class="resultTable"><tr><td valign="top">
            <#else>
                <td style='padding-left: 50px;' valign="top">
        </#if>
            <h2>Possible Subject Errors:</h2> 
            <label>The following classes might be using Subject, but contain errors</label>   
            <ul>
                <#list subjectErrors as subjectError>
                  <li>
                  <form style="display:inline;" target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${subjectError.getCode()}" />
                        <button id="codeLink">${subjectError.getClassName()}</button>
                    </form> &nbsp;
                    <i>${subjectError.getErrorReason()} </i>
                  </li>
                </#list>
            </ul>
        </td></tr></table>
    </#if> 
    
    <#if subjectErrors?has_content || subject?has_content>
        <hr>
    </#if>
    
    <!-- Mediator Names -->
    <#if mediator?? && mediator?has_content>
    <table class = "resultTable"><tr><td valign="top">
        <h2>Mediator:</h2> 
        <label>The following instances of Mediator were found in the repository</label>   
            <ul>
              <#list mediator as mediatorInfo>
              <li>
                <div>
                    <div>
                    <form target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${mediatorInfo.getCode()}" />
                        <button id="codeLink">${mediatorInfo.getMediatorName()}</button>
                    </form>
                      <#if mediatorInfo.getColleagues()?has_content>
                         <ul>
                         <h4 id="listHeader">Colleagues:</h4>
                         <#list mediatorInfo.getColleagues() as colleagueName>
                           <div>
                             <li>
                               ${colleagueName}
                             </li>
                           </div>
                         </#list>
                        </ul>
                       </#if>
                    </div>
                </div>
              </li>
            </#list>
            </ul>
        </td>
        <#if !mediatorErrors?has_content>
            </tr></table>
        </#if>
    </#if>
    
    <!-- Mediator Errors -->
    <#if mediatorErrors?? && mediatorErrors?has_content>
        <#if !mediator?has_content>
                <table class="resultTable"><tr><td valign="top">
            <#else>
                <td style='padding-left: 50px;' valign="top">
        </#if>
            <h2>Possible Mediator Errors:</h2> 
            <label>The following classes might be using Mediator, but are in error</label>   
            <ul>
                <#list mediatorErrors as mediatorError>
                  <li>
                    <form style="display:inline;" target="_blank" action="/patternChecker/ShowCode" method="post">
                        <input type="hidden" name="codeToPrint" value="${mediatorError.getCode()}" />
                        <button id="codeLink">${mediatorError.getClassName()}</button>
                    </form> &nbsp;
                   <i> ${mediatorError.getErrorReason()} </i>
                  </li>
                </#list>
            </ul>
        </td></tr></table>
    </#if> 
    
    <#if mediatorErrors?has_content || mediator?has_content>
        <hr>
    </#if>  

</body>

</html>
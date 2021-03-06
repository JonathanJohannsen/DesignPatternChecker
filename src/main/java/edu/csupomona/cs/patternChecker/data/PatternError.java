package edu.csupomona.cs.patternChecker.data;

import org.apache.commons.lang.StringEscapeUtils;

public class PatternError 
{
	private String className;
	private String errorReason;
	private String Code;
	
	public PatternError(String cn, String er, String code)
	{
		className=cn;
		errorReason=er;
		String codeTemp = StringEscapeUtils.escapeHtml(code);
		Code = codeTemp.replace("<", "&lt;").replace("&", "&amp;");
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public String getErrorReason()
	{
		return errorReason;
	}
	
	public String getCode()
	{
		return Code;
	}
}

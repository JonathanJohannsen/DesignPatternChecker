package edu.csupomona.cs.patternChecker.data;

public class PatternError 
{
	private String className;
	private String errorReason;
	
	public PatternError(String cn, String er)
	{
		className=cn;
		errorReason=er;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public String getErrorReason()
	{
		return errorReason;
	}
}

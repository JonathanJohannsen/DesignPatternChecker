package edu.csupomona.cs.patternChecker.data;


import org.apache.commons.lang.StringEscapeUtils;

public class SingletonInfo 
{
	private String singletonName;
	private String Code;
	
	public SingletonInfo(String sn, String code)
	{
		singletonName = sn;
		String codeTemp = StringEscapeUtils.escapeHtml(code);
		Code = codeTemp.replace("<", "&lt;").replace("&", "&amp;");
	}
	
	public String getSingletonName()
	{
		return singletonName;
	}
	
	public String getCode()
	{
		return Code;
	}
}

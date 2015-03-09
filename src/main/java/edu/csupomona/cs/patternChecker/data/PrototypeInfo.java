package edu.csupomona.cs.patternChecker.data;


import org.apache.commons.lang.StringEscapeUtils;

public class PrototypeInfo 
{
	private String prototypeName;
	private String Code;
	
	public PrototypeInfo(String name, String code)
	{
		prototypeName = name;
		String codeTemp = StringEscapeUtils.escapeHtml(code);
		Code = codeTemp.replace("<", "&lt;").replace("&", "&amp;");
	}
	
	public String getPrototypeName()
	{
		return prototypeName;
	}
	
	public String getCode()
	{
		return Code;
	}
}

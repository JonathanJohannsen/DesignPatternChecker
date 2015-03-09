package edu.csupomona.cs.patternChecker.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class MediatorInfo 
{
	private String mediatorName;
	private String colleagueType;
	private List<String> colleagueNames;
	private String Code;
	
	public MediatorInfo(String sn, String ct, String code)
	{
		mediatorName = sn;
		//what class type the colleagues of this mediator should be/implement
		colleagueType = ct;
		colleagueNames = new ArrayList<String>();
		String codeTemp = StringEscapeUtils.escapeHtml(code);
		Code = codeTemp.replace("<", "&lt;").replace("&", "&amp;");
	}
	
	public String getMediatorName()
	{
		return mediatorName;
	}
	
	public String getColleagueType()
	{
		return colleagueType;
	}
	
	public String getCode()
	{
		return Code;
	}
	
	public void addColleague(String colleagueName)
	{
		colleagueNames.add(colleagueName);
	}
	
	public List<String> getColleagues()
	{
		return colleagueNames;
	}
}

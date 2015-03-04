package edu.csupomona.cs.patternChecker.data;

import java.util.ArrayList;
import java.util.List;

public class MediatorInfo 
{
	private String mediatorName;
	private String colleagueType;
	private List<String> colleagueNames;
	
	public MediatorInfo(String sn, String ct)
	{
		mediatorName = sn;
		//what class type the colleagues of this mediator should be/implement
		colleagueType = ct;
		colleagueNames = new ArrayList<String>();
	}
	
	public String getMediatorName()
	{
		return mediatorName;
	}
	
	public String getColleagueType()
	{
		return colleagueType;
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

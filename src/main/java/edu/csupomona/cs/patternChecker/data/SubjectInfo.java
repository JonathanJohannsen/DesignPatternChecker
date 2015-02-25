package edu.csupomona.cs.patternChecker.data;

import java.util.ArrayList;
import java.util.List;

public class SubjectInfo 
{
	private String subjectName;
	private String observerType;
	private List<String> observerNames;
	
	public SubjectInfo(String sn, String ot)
	{
		subjectName = sn;
		observerType = ot;
		observerNames = new ArrayList<String>();
	}
	
	public String getSubjectName()
	{
		return subjectName;
	}
	
	public String getObserverType()
	{
		return observerType;
	}
	
	public void addObserver(String observerName)
	{
		observerNames.add(observerName);
	}
	
	public List<String> getObservers()
	{
		return observerNames;
	}
}

package edu.csupomona.cs.patternChecker.data;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;

public class SubjectInfo 
{
	// a class name that holds information on a subject implementation in the project. It contains the following:
	// 1. Name of the class that implements the subject pattern.
	// 2. Name of the class or interface type that is contained in the subject's list of observers
	// 3. A list of strings that are the names of the observers (both interface and class) of this subject.
	
	// if "observerType" is set to "anyThatImplement", that means that the subject was implemented using Java's
	// built in Subject/Observer methods. Therefore, any class that implements "Observer" is an observer of this subject. 
	private String subjectName;
	private String observerType;
	private String Code;
	private List<String> observerNames;
	
	public SubjectInfo(String sn, String ot, String code)
	{
		subjectName = sn;
		observerType = ot;
		observerNames = new ArrayList<String>();
		String codeTemp = StringEscapeUtils.escapeHtml(code);
		Code = codeTemp.replace("<", "&lt;").replace("&", "&amp;");
	}
	
	public String getSubjectName()
	{
		return subjectName;
	}
	
	public String getObserverType()
	{
		return observerType;
	}
	
	public String getCode()
	{
		return Code;
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

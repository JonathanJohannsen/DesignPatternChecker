package edu.csupomona.cs.patternChecker.checkers;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.csupomona.cs.patternChecker.data.MediatorInfo;
import edu.csupomona.cs.patternChecker.data.SubjectInfo;

public class ObserverChecker extends VoidVisitorAdapter<Object>
{
	private List<SubjectInfo> subjects;
	private List<MediatorInfo> mediators;
	
	public ObserverChecker(List<SubjectInfo> si, List<MediatorInfo> mi)
	{
		//create a new SubjectInfo for each subject in the incoming list. As we find 
		//observers for each subject, they will be added to the correct SubjectInfo object. 
		//if no observers are found for a subject, the observer will still be printed on the showResults page
		subjects = new ArrayList<SubjectInfo>();
		subjects.addAll(si);
		mediators = new ArrayList<MediatorInfo>();
		mediators.addAll(mi);
	}
	
	// finds any class that is built to observer any of the subjects in the system
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		for(SubjectInfo si : subjects)
		{
			String testString = "implements " + si.getObserverType();
			String wholeThing = c.toString();
			if(className.equals(si.getObserverType()))
			{
				si.addObserver(className);
			}

			else if(wholeThing.contains(testString))
			{
				si.addObserver(className);
			}
		}
		for(MediatorInfo mi : mediators)
		{
		
			String testString = "implements " + mi.getColleagueType();
			String wholeThing = c.toString();
			if(className.equals(mi.getColleagueType()))
			{
				mi.addColleague(className);
			}

			else if(wholeThing.contains(testString))
			{
				mi.addColleague(className);
			}
		}
	}
	
}

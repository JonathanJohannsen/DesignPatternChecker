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
	//observer checker checks for observers of any subjects in the project as well as any colleagues
	//of any mediators in the project.
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
	
	// finds any class that is built to observe one of the subjects in the project or is a colleague
	// of one of the mediators in the project.
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		for(SubjectInfo si : subjects)
		{
			String observerType;
			if(si.getObserverType().equals("anyThatImplement"))
			{
				//if the observer is the kind that simply implements Observer (rather than constructing from scratch)
				//then any class that implements or extends "Observer" is a potential Observer of this subject
				observerType = "Observer";
			}
			else
			{
				//otherwise, the class would need to implement or extend the class type that the collection in the subject holds
				observerType = si.getObserverType();
			}

			if(className.equals(si.getObserverType()))
			{
				//if this class is of the same type as this subject's observers, add it
				si.addObserver(className);
			}
			
			else if(isObserverExtendedOrImplemented(c.getExtends(), c.getImplements(), observerType))
			{
				si.addObserver(className);
			}
		}
		for(MediatorInfo mi : mediators)
		{
		
			String impString = "implements " + mi.getColleagueType();
			String extString = "extends " + mi.getColleagueType();
			String wholeThing = c.toString();
			if(className.equals(mi.getColleagueType()))
			{
				mi.addColleague(className);
			}

			else if(wholeThing.contains(impString) || wholeThing.contains(extString))
			{
				mi.addColleague(className);
			}
		}
	}
	
	private static Boolean isObserverExtendedOrImplemented(List<ClassOrInterfaceType> extended,
			List<ClassOrInterfaceType> implemented, String observerType)
	{
		if(extended != null)
		{
			for(ClassOrInterfaceType c : extended)
			{
				if(c.toString().startsWith(observerType))
				{
					return true;
				}
			}
		}
		if(implemented != null)
		{
			for(ClassOrInterfaceType c : implemented)
			{
				if(c.toString().startsWith(observerType))
				{
					return true;
				}
			}	
		}
		return false;
	}
}

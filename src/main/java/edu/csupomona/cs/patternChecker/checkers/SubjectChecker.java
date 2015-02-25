package edu.csupomona.cs.patternChecker.checkers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.csupomona.cs.patternChecker.data.CollectionInfo;
import edu.csupomona.cs.patternChecker.data.PatternError;
import edu.csupomona.cs.patternChecker.data.SubjectInfo;

public class SubjectChecker extends VoidVisitorAdapter<Object>
{
	private static List<SubjectInfo> subjectNames;
	private static List<CollectionInfo> listOfCollections;
	private static List<PatternError> subjectErrors;
	
	public SubjectChecker()
	{
		subjectNames = new ArrayList<SubjectInfo>();
		listOfCollections = new ArrayList<CollectionInfo>();
		subjectErrors = new ArrayList<PatternError>();
	}
	
	//find an implementation of the Observer pattern. Observer requires:
	// 1. A collection that holds a specific type of object
	// 2. A routine to add an element to that collection and one to remove from it
	// 3. method to loop through the collection. In that loop, call a function
	//    that passes self or one (or more) of your private variables
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		if(className.equals("PartyImpl"))
		{
			int i=0;
			i++;
		}
		
		//a list to contain collectionInfo objects.
		List<Node> nodeList= c.getChildrenNodes();
		for(Node a : nodeList)
		{
			if(a instanceof FieldDeclaration && a.toString().contains("<")
					 && a.getChildrenNodes() != null)
			{
				//grab the 'type' of this list as well as the 'name'
				List<Node> theNodes = a.getChildrenNodes();
				if(theNodes.size() < 2)
				{
					continue;
				}
				parseDeclaration(theNodes, className);
			}
			
			else if(a instanceof MethodDeclaration)
			{
				parseMethod((MethodDeclaration)a);
				findLoop(a.getChildrenNodes());
			}
		}	
		
		//if we find a CollectionInfo in our list that has all the required attributes, add it 
		for(CollectionInfo ci : listOfCollections)
		{
			if(ci.HasAddMethod() && ci.HasRemoveMethod() && ci.HasNotifyLoop())
			{
				subjectNames.add(new SubjectInfo(ci.getClassCollectionIsIn(), ci.getType()));
			}
			else if(ci.HasAddMethod() && ci.HasNotifyLoop())
			{
				subjectErrors.add(new PatternError(ci.getClassCollectionIsIn(), "Subject has no remove method"));
			}
			
			else if(ci.HasRemoveMethod() && ci.HasNotifyLoop())
			{
				subjectErrors.add(new PatternError(ci.getClassCollectionIsIn(), "Subject has no add method"));
			}
		}
		listOfCollections.clear();
	}
	
	private void parseDeclaration(List<Node> theNodes, String className)
	{
		String myString = theNodes.get(0).toString();
		if(myString.contains("<"))
		{
			CollectionInfo ci = new CollectionInfo();
			//grab the text between the brackets. This will be the class type the collection holds
			getCollectionType(ci, myString);
			getCollectionName(ci, theNodes.get(1));
			ci.setClassCollectionIsIn(className);
			listOfCollections.add(ci);
		}	
	}
	
	private void getCollectionType(CollectionInfo ci, String myString)
	{
		//finds the type of object held in the collection that we have found
		myString=myString.substring(myString.indexOf("<")+1, myString.indexOf(">"));
		ci.setType(myString);
	}
	
	private void getCollectionName(CollectionInfo ci, Node node)
	{
		//based on an incoming string, find the name of the collection of observers
		
		//remove spaces in the case that the declaration has collectionName = new ArrayList, 
		//we only want 'collectionName'
		String myString=node.toString();
		if(myString.contains(" "))
		{
			myString=myString.substring(0, myString.indexOf(" "));
		}
		
		else if(myString.contains("="))
		{
			myString=myString.substring(0, myString.indexOf("="));	
		}
		ci.setName(myString);
	}
	
	private void parseMethod(MethodDeclaration method)
	{
		// need to see if this method has a way to add or remove from any of the collections
		// in listOfCollections
		if(!method.toString().contains(".add(") && !method.toString().contains(".remove("))
		{
			//this method doesn't have either, no point in continuing.
			return;
		}
		
		//get the incoming parameters. Make sure the incoming parameter (the observer) is what is being 
		//added or removed to/from the subject
		if(method.getParameters() == null)
		{
			//the add or remove functions require at least one parameter, no need to continue
			return;
		}
		
		List<Parameter> myParameters = method.getParameters();
		
		//loop through the list of collections to see if this method adds or removes an item from it
		for(CollectionInfo ci : listOfCollections)
		{
			for(Parameter p : myParameters)
			{
				//confirm that one of the parameters is of the same type as one of this potential 
				//observer's collections
				String paramType = p.getType().toString();
				String ciType = ci.getType();
				if(!paramType.equals(ciType))
				{
					continue;
				}
				if(method.toString().contains(ci.getName()+".add("))
				{
					ci.setHasAddMethod(true);
					return;
				}
				if(method.toString().contains(ci.getName()+".remove("))
				{
					ci.setHasRemoveMethod(true);
					return;
				}
			}
		}
	}
	
	private void findLoop(List<Node> myNodes)
	{
		//see if this method has a loop through any of the collections in our list
		for(Node b : myNodes)
		{
			if((b instanceof ForeachStmt == false) && b.getChildrenNodes() != null)
			{
				findLoop(b.getChildrenNodes());
			}
			
			
			if(b instanceof ForeachStmt)
			{
				//first, see if the loop has an if statement. Observer shouldn't have this
				if(hasIfStatement(b.getChildrenNodes()))
				{
					return;
				}
				//see if this loop is looping through any of the collections in our list
				String iterable = ((ForeachStmt) b).getIterable().toString();
				for(CollectionInfo ci : listOfCollections)
				{
					if(ci.getName().equals(iterable.toString()))
					{
						ci.setHasNotifyLoop(true);
					}
				}
			}
		}
	}
	
	private Boolean hasIfStatement(List<Node> myNodes)
	{
		for(Node b : myNodes)
		{
			if(b instanceof IfStmt)
			{
				return true;
			}
			
			else if(b.getChildrenNodes() != null)
			{
				return hasIfStatement(b.getChildrenNodes());
			}
		}
		return false;
	}
	
	public List<SubjectInfo> getSubjects(Boolean needToClear)
	{
		List<SubjectInfo> returnList=new ArrayList<SubjectInfo>();
		returnList.addAll(subjectNames);
		if(needToClear)
		{
			subjectNames.clear();
		}
		return returnList;
	}
	
	public List<PatternError> getSubjectErrors()
	{
		List<PatternError> returnList = new ArrayList<PatternError>();
		returnList.addAll(subjectErrors);
		subjectErrors.clear();
		return returnList;
	}
}

package edu.csupomona.cs.patternChecker.checkers;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.csupomona.cs.patternChecker.data.CollectionInfo;
import edu.csupomona.cs.patternChecker.data.MediatorInfo;
import edu.csupomona.cs.patternChecker.data.PatternError;
import edu.csupomona.cs.patternChecker.data.SubjectInfo;

public class SubjectMediatorChecker extends VoidVisitorAdapter<Object>
{
	//all collections in the incoming class will have their information stored in the following collection
	private static List<CollectionInfo> listOfCollections;
	private static List<SubjectInfo> subjectNames;
	private static List<String> implementsOrExtends;
	
	//collection to hold name of any classes that appear to implement subject, but are in error.
	private static List<PatternError> subjectErrors;
	private static List<MediatorInfo> mediatorNames;
	
	public SubjectMediatorChecker()
	{
		subjectNames = new ArrayList<SubjectInfo>();
		mediatorNames = new ArrayList<MediatorInfo>();
		listOfCollections = new ArrayList<CollectionInfo>();
		subjectErrors = new ArrayList<PatternError>();
		implementsOrExtends = new ArrayList<String>();
	}
	
	//find an implementation of the Observer or Mediator pattern. Observer requires:
	// 1. A collection that holds a specific type of object
	// 2. A routine to add an element to that collection and one to remove from it
	// 3. method to loop through the collection. In that loop, call a function
	//    that passes self or one (or more) of your private variables, or an incoming variable
	
	// Mediator is the same (for the most part) but has an if statement in the notify method
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		if(implementsOrExtendsObservable(c.getImplements(), c.getExtends()))
		{
			//the easy case: the class simply implements or extends observable, so we can immediately add it
			subjectNames.add(new SubjectInfo(className, "anyThatImplement", c.toString()));
			return;
		}
		
		//make a list of every other class that is extended or implemented by this class
		implementsOrExtends(c.getImplements(), c.getExtends());
		
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
			if(implementsOrExtends.contains(ci.getType()))
			{
				//could be a composite false positive: composite pattern extends or implements
				//the same type of class that its collection contains. Subject does not 
				continue;
			}
			
			if(ci.getType().equals(ci.getClassCollectionIsIn()))
			{
				//another possible composite false positive.
				continue;
			}
			
			if(ci.IsSubjectPattern())
			{
				subjectNames.add(new SubjectInfo(ci.getClassCollectionIsIn(), ci.getType(), c.toString()));
			}
			
			else if(ci.IsMediatorPattern())
			{
				mediatorNames.add(new MediatorInfo(ci.getClassCollectionIsIn(), ci.getType(), c.toString()));
			}
			
			else if(ci.HasAddMethod() && ci.HasNotifyLoop())
			{
				subjectErrors.add(new PatternError(ci.getClassCollectionIsIn(), "Subject has no remove method", c.toString()));
			}
			
			else if(ci.HasRemoveMethod() && ci.HasNotifyLoop())
			{
				subjectErrors.add(new PatternError(ci.getClassCollectionIsIn(), "Subject has no add method", c.toString()));
			}
		}
		implementsOrExtends.clear();
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
		//based on an incoming string, find the name of the collection of observers or colleagues
		
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
				//see if this loop is looping through any of the collections in our list
				String iterable = ((ForeachStmt) b).getIterable().toString();
				for(CollectionInfo ci : listOfCollections)
				{
					if(ci.getName().equals(iterable.toString()))
					{
						ci.setHasNotifyLoop(true);
					}
					
					if(hasIfStatement(b.getChildrenNodes()))
					{
						//mediator has if statement in its notify loop
						ci.setNotifyHasIfStatement(true);
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
			
			else if(!b.getChildrenNodes().isEmpty())
			{
				if(hasIfStatement(b.getChildrenNodes()))
				{
					return true;
				}
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
	
	public List<MediatorInfo> getMediators(Boolean needToClear)
	{
		List<MediatorInfo> returnList=new ArrayList<MediatorInfo>();
		returnList.addAll(mediatorNames);
		if(needToClear)
		{
			mediatorNames.clear();
		}
		return returnList;
	}
	
	public void implementsOrExtends(List<ClassOrInterfaceType> classesBeingImplemented,
			List<ClassOrInterfaceType> classesBeingExtended)
	{
		//finds out what the class is extending or implementing. We need to know this to
		//cut down on finding composite as a false positive, because composite sometimes looks 
		//like observer, but composite will extend the type of class that is the type of class
		//its "followers" collection contains. 
		if(classesBeingImplemented != null)
		{
			for(ClassOrInterfaceType ci : classesBeingImplemented)
			{
				implementsOrExtends.add(ci.toString());
			}
		}
		if(classesBeingExtended != null)
		{
			for(ClassOrInterfaceType ci : classesBeingExtended)
			{
				implementsOrExtends.add(ci.toString());
			}
		}

	}
	public Boolean implementsOrExtendsObservable(List<ClassOrInterfaceType> classesBeingImplemented,
			List<ClassOrInterfaceType> classesBeingExtended)
	{
		if(classesBeingImplemented != null)
		{
			for(ClassOrInterfaceType ci : classesBeingImplemented)
			{
				if(ci.toString().startsWith("Observable"))
				{
					return true;
				}
			}
		}
		
		if(classesBeingExtended != null)
		{
			for(ClassOrInterfaceType ci : classesBeingExtended)
			{
				if(ci.toString().startsWith("Observable"))
				{
					return true;
				}
			}
		}
		return false;
	}
}

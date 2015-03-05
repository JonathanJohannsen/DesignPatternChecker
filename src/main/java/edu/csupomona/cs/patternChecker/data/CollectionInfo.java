package edu.csupomona.cs.patternChecker.data;

public class CollectionInfo 
{
	//collection info holds all relevant info about a collection contained in a class. 
	//to be used for both the subject and mediator pattern checkers. Could be updated later
	//on to be used by future pattern checkers. 
	String classCollectionIsIn = null;
	String collectionName = null;
	//type name is what type of object this collection holds
	String typeName = null;
	Boolean hasAddMethod = false;
	Boolean hasRemoveMethod = false;
	Boolean hasNotifyLoop=false;
	
	// the notify method having an if statement implies this is mediator
	Boolean notifyHasIfStatement=false;
	
	public void setClassCollectionIsIn(String name)
	{
		classCollectionIsIn = name;
	}
	
	public String getClassCollectionIsIn()
	{
		return classCollectionIsIn;
	}
	
	public void setName(String cn)
	{
		collectionName=cn;
	}
	
	public String getName()
	{
		return collectionName;
	}
	
	public void setType(String tn)
	{
		typeName=tn;
	}
	
	public String getType()
	{
		return typeName;
	}
	
	public void setHasAddMethod(Boolean hasAdd)
	{
		hasAddMethod=hasAdd;
	}
	
	public void setHasRemoveMethod(Boolean hasRemove)
	{
		hasRemoveMethod=hasRemove;
	}
	
	public void setHasNotifyLoop(Boolean hasNotify)
	{
		hasNotifyLoop=hasNotify;
	}
	
	public void setNotifyHasIfStatement(Boolean hasIfStmt)
	{
		notifyHasIfStatement=hasIfStmt;
	}
	
	public Boolean HasAddMethod()
	{
		return hasAddMethod;
	}
	
	public Boolean HasRemoveMethod()
	{
		return hasRemoveMethod;
	}
	
	public Boolean HasNotifyLoop()
	{
		return hasNotifyLoop;
	}
	
	public Boolean NotifyHasIfStatement()
	{
		return notifyHasIfStatement;
	}
	
	public Boolean IsSubjectPattern()
	{
		return(hasRemoveMethod && hasAddMethod && hasNotifyLoop && !notifyHasIfStatement);
	}
	
	public Boolean IsMediatorPattern()
	{
		return(hasAddMethod && hasNotifyLoop && notifyHasIfStatement);
	}
}

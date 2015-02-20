package edu.csupomona.cs.patternChecker.data;

public class CollectionInfo 
{
	String collectionName = null;
	String typeName = null;
	Boolean hasAddMethod = false;
	Boolean hasRemoveMethod = false;
	Boolean hasNotifyLoop=false;
	
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
}

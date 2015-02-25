package edu.csupomona.cs.patternChecker.checkers;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.csupomona.cs.patternChecker.data.*;

public class SingletonChecker extends VoidVisitorAdapter<Object>
{
	private static List<String> singletonNames;
	private static List<PatternError> singletonErrors;
	
	public SingletonChecker()
	{
		singletonNames = new ArrayList<String>();
		singletonErrors = new ArrayList<PatternError>();
	}
	
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		
		String instanceVariableName="";
		String returnVariableName="";
		Boolean constructorIsPrivate=false;
		
		List<Node> nodeList= c.getChildrenNodes();
		for(Node a : nodeList)
		{
			if(a instanceof MethodDeclaration)
			{
				//see if the return type of this method is the same as the class name
				MethodDeclaration method = (MethodDeclaration) a;
				String returnType=method.getType().toString();
				if(returnType.equals(className))
				{
					returnVariableName=FindReturnName(method.getChildrenNodes());
				}
			}
			if(a instanceof FieldDeclaration)
			{
				//see if this is the declaration of the instance of the Singleton
				FieldDeclaration field = (FieldDeclaration) a;
				String fieldName = field.getType().toString();
				if(fieldName.equals(className))
				{
					if(field.getChildrenNodes().get(1) instanceof VariableDeclarator)
					{
						VariableDeclarator n = (VariableDeclarator)field.getChildrenNodes().get(1);
						instanceVariableName=n.getId().toString();
					}
				}
			}
			if(a instanceof ConstructorDeclaration)
			{
				if(a.toString().contains("private"))
				{
					constructorIsPrivate=true;
				}
				else
				{
					constructorIsPrivate=false;
				}
			}
		}
		if(instanceVariableName.equals(returnVariableName)
				&& !instanceVariableName.equals("") && constructorIsPrivate)
		{
			singletonNames.add(className);
		}
		
		else if(instanceVariableName.equals(returnVariableName)
				&& !instanceVariableName.equals(""))
		{
			//a possible singleton error
			singletonErrors.add(new PatternError(className, "Contains a public constructor"));
		}
	}
	
	private static String FindReturnName(List<Node> myList)
	{
		String returnName = "";
		for(Node b : myList)
		{
			if(b.getChildrenNodes() != null)
			{
				returnName = FindReturnName(b.getChildrenNodes());
				if(!returnName.equals(""))
				{
					return returnName;
				}
			}
			
			if(b instanceof ReturnStmt)
			{
				List<Node> childList = b.getChildrenNodes();
				if(!childList.isEmpty())
				{
					return childList.get(0).toString();
				}
			}
		}
		return returnName;
	}
	
	public List<String> getSingletonNames()
	{
		List<String> returnList=new ArrayList<String>();
		returnList.addAll(singletonNames);
		singletonNames.clear();
		return returnList;
	}
	
	public List<PatternError> getSingletonErrors()
	{
		List<PatternError> returnList = new ArrayList<PatternError>();
		returnList.addAll(singletonErrors);
		singletonErrors.clear();
		return returnList;
	}
}

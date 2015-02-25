package edu.csupomona.cs.patternChecker.checkers;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.csupomona.cs.patternChecker.data.PatternError;
import edu.csupomona.cs.patternChecker.data.SubjectInfo;

public class PrototypeChecker extends VoidVisitorAdapter<Object>
{
	private static List<String> prototypeNames;
	private static List<PatternError> prototypeErrors;
	
	public PrototypeChecker()
	{
		prototypeNames = new ArrayList<String>();
		prototypeErrors = new ArrayList<PatternError>();
	}
	
	public List<String> getPrototypeNames()
	{
		List<String> returnList=new ArrayList<String>();
		returnList.addAll(prototypeNames);
		prototypeNames.clear();
		return returnList;
	}
	
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		Boolean hasRequiredReturnType=false;
		Boolean constructorIsPrivate=false;
		List<Node> nodeList= c.getChildrenNodes();
		for(Node a : nodeList)
		{
			if(a instanceof MethodDeclaration)
			{
				//see if the method returns a type of this class
				ReturnStmt returnStmt=(ReturnStmt) FindReturnStatement(a.getChildrenNodes());
				if(returnStmt!=null)
				{
					//make sure the return statement is creating a new object
					Node b = returnStmt.getChildrenNodes().get(0);
					if(b !=null && b instanceof ObjectCreationExpr)
					{
						//make sure the parameter is 'this'
						List <Expression> args = ((ObjectCreationExpr) b).getArgs();
						if(args != null && args.size() == 1 && args.get(0) instanceof ThisExpr)
						{
							hasRequiredReturnType=true;
						}	
					}
					
					//the other option is the user is returning a super.clone()
					if(returnStmt.toString().contains(".clone()"))
					{
						hasRequiredReturnType=true;
					}
				}
			}
			if(a instanceof ConstructorDeclaration)
			{
				if(a.toString().contains("private"))
				{
					//if there is a constructor, it must be public
					constructorIsPrivate=true;
				}
			}
		}
		if(!constructorIsPrivate && hasRequiredReturnType)
		{
			prototypeNames.add(className);
		}
	}
	
	private static Node FindReturnStatement(List<Node> myList)
	{
		Node returnStmt=null;
		for(Node b : myList)
		{
			if(b.getChildrenNodes() != null)
			{
				returnStmt = FindReturnStatement(b.getChildrenNodes());
				if(returnStmt!=null)
				{
					return returnStmt;
				}
			}
			
			if(b instanceof ReturnStmt)
			{
				List<Node> childList = b.getChildrenNodes();
				if(!childList.isEmpty())
				{
					return b;
				}
			}
		}
		return returnStmt;
	}
}

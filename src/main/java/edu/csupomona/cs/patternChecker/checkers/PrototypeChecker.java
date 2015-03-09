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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.csupomona.cs.patternChecker.data.PatternError;
import edu.csupomona.cs.patternChecker.data.PrototypeInfo;

public class PrototypeChecker extends VoidVisitorAdapter<Object>
{
	private static List<PrototypeInfo> prototypes;
	private static List<PatternError> prototypeErrors;
	
	public PrototypeChecker()
	{
		prototypes = new ArrayList<PrototypeInfo>();
		prototypeErrors = new ArrayList<PatternError>();
	}
	
	public List<PrototypeInfo> getPrototypes()
	{
		List<PrototypeInfo> returnList=new ArrayList<PrototypeInfo>();
		returnList.addAll(prototypes);
		prototypes.clear();
		return returnList;
	}
	
	public List<PatternError> getPrototypeErrors()
	{
		List<PatternError> returnList=new ArrayList<PatternError>();
		returnList.addAll(prototypeErrors);
		prototypeErrors.clear();
		return returnList;
	}
	
	
	public void visit(ClassOrInterfaceDeclaration c, Object arg)
	{
		if(c.getName() == null)
		{
			return;
		}
		
		String className = c.getName();
		if(implementsCloneable(c.getImplements()))
		{
			//the easy case: if the class implements Cloneable, no need to check further
			prototypes.add(new PrototypeInfo(className, c.toString()));	
			return;
		}
		
		//if we get this far, we need to check the design of the class to see if prototype was 
		//implemented from scratch rather than from using Cloneable.
		List<ClassOrInterfaceType> extendsNames = new ArrayList<ClassOrInterfaceType>();
		if(c.getExtends() != null)
		{
			extendsNames.addAll(c.getExtends());
		}
		Boolean hasRequiredReturnType=false;
		Boolean possibleErrorReturnType=false;
		Boolean hasPublicConstructor=false;
		List<Node> nodeList= c.getChildrenNodes();
		for(Node a : nodeList)
		{
			if(a instanceof MethodDeclaration)
			{
				MethodDeclaration methodDeclaration=(MethodDeclaration) a;
				String md = methodDeclaration.getType().toString();
				if(!md.equals(className))
				{
					//method does not return a type of this class; make sure it's not in the extends list
					Boolean found=false;
					if(extendsNames.isEmpty())
					{
						continue;
					}
					for(ClassOrInterfaceType ex : extendsNames)
					{
						if(md.equals(ex.toString()))
						{
							found=true;
						}
					}
					if(!found)
					{
						continue;
					}
				}
				ReturnStmt returnStmt=(ReturnStmt) FindReturnStatement(a.getChildrenNodes());
				if(returnStmt!=null)
				{
					//make sure the return statement is creating a new object
					Expression b = returnStmt.getExpr();
					if(b !=null && b instanceof ObjectCreationExpr)
					{
						//make sure the parameter is 'this'
						List <Expression> args = ((ObjectCreationExpr) b).getArgs();
						if(args != null && args.size() == 1 && args.get(0) instanceof ThisExpr)
						{
							hasRequiredReturnType=true;
						}
						
						else
						{
							//if we get this far, it's possible this is a prototype error since the 
							//method is returning an instance of this type of class but it's not
							//using 'this'
							possibleErrorReturnType=true;
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
				if(!a.toString().contains("private"))
				{
					//if there is a constructor, it must be public
					hasPublicConstructor=true;
				}
			}
		}
		
		if(hasPublicConstructor && hasRequiredReturnType)
		{
			prototypes.add(new PrototypeInfo(className, c.toString()));
		}
		
		else if(hasPublicConstructor && possibleErrorReturnType)
		{
			prototypeErrors.add(new PatternError(className, "Prototype clone should return an "
					+ "instance of 'this'", c.toString()));
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
	
	private static Boolean implementsCloneable(List<ClassOrInterfaceType> classesBeingImplemented)
	{
		if(classesBeingImplemented!=null)
		{
			for(ClassOrInterfaceType ci : classesBeingImplemented)
			{
				if(ci.toString().equals("Cloneable"))
				{
					return true;
				}
			}
		}
		return false;
	}
}

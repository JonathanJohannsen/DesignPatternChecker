package edu.csupomona.cs.patternChecker.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.csupomona.cs.patternChecker.App;

/**
 * This is the controller used by Spring framework.
 * <p>
 * The basic function of this controller is to map each HTTP API Path to the
 * correspondent method.
 *
 */

@RestController
public class WebController 
{	
	private static List<String> methodNames = new ArrayList<String>();
	private static List<String> classNames = new ArrayList<String>();
	private static List<String> singletonNames = new ArrayList<String>();
	private static String url="home";

	@RequestMapping(value = "/patternChecker/welcome", method = RequestMethod.GET)
	String welcomePage() 
	{
		//main page of Pattern Checker
		//http://localhost:8080/patternChecker/welcome
		return "Welcome to Online Pattern Checker! Enter a git repository URL to check for Design Patterns";
	}

	//https://github.com/JonathanJohannsen/ibox
	@RequestMapping(value = "/patternChecker/{repoURL}/checkGit", method = RequestMethod.POST)
	void checkGit(@PathVariable("repoURL") String repoURL) throws ParseException, IOException
	{
		singletonNames.clear();
		methodNames.clear();
		classNames.clear();
		//get a UUID for the name of the directory to place the repository in
		UUID idOne = UUID.randomUUID();

		String directory = "src/main/resources/" +idOne.toString() + "repo";
		File file = new File(directory);
		byte[] decoded = Base64.decodeBase64(repoURL);
		String repoURLFinal=new String(decoded, "UTF-8");
		try 
		{
			//clone the repo from GitHub
			Git.cloneRepository().setURI(repoURLFinal).setDirectory(file).call();
        } 

		catch (InvalidRemoteException e) 
		{
			//was not a valid repository
        } 
		
		catch (TransportException e) 
		{
	        e.printStackTrace();
        } 
		
		catch (GitAPIException e) 
		{
	        e.printStackTrace();
        }

		List<File> javaFiles = new ArrayList<File>();
		findJavaFiles(file, javaFiles);
		
		for(File iterator : javaFiles)
		{
			FileInputStream in = new FileInputStream(iterator);
			CompilationUnit cu;
			try
			{
				//parse the file
				cu = JavaParser.parse(in);
			}
			finally
			{
				in.close();
			}
			// visit and print the methods names for this file
	        new MethodVisitor().visit(cu, null);
	        new FindSingleton().visit(cu, null);
		}
	}
	
	public static void findJavaFiles(File root, List<File> javaFiles)
	{ 
		//adds any file that ends in .java to the List javaFiles
	    if(root.isDirectory())
	    {
	        for(File file : root.listFiles())
	        {
	            findJavaFiles(file, javaFiles);
	        }
	    }
	    else if(root.isFile() && root.getName().endsWith(".java"))
	    {
	        javaFiles.add(root);
	    }
	}
	
	private static class MethodVisitor extends VoidVisitorAdapter<Object>
    {

        @Override
        public void visit(MethodDeclaration n, Object arg) 
        {
            // here you can access the attributes of the method.
            // this method will be called for all methods in this 
            // CompilationUnit, including inner class methods

        	if(n.getName() != null)
        	{
        		methodNames.add(n.getName());
        	}
        }
    }
	 
	private static class FindSingleton extends VoidVisitorAdapter<Object>
	{
		public void visit(ClassOrInterfaceDeclaration c, Object arg)
		{
			//search for a Singleton. Singleton requires: private constructor, 
			//instance variable with same type as class itself, and getter method with a 
			//return variable of the same name as the instanceVariable
			String className = c.getName();
			if(className != null)
			{
				String instanceVariableName="";
				String returnVariableName="";
				Boolean constructorIsPrivate=false;
				
				classNames.add(className);
				
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
							VariableDeclarator n = (VariableDeclarator)field.getChildrenNodes().get(1);
							instanceVariableName=n.getId().toString();
						}
					}
					if(a instanceof ConstructorDeclaration)
					{
						if(a.toString().contains("private"))
						{
							constructorIsPrivate=true;
						}
					}
				}
				if(instanceVariableName.equals(returnVariableName)
						&& !instanceVariableName.equals("") && constructorIsPrivate)
				{
					singletonNames.add(c.getName());
				}
			}
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
	
	@RequestMapping(value = "/patternChecker/Home", method = RequestMethod.GET)
	ModelAndView patternCheckerHome() 
	{
		ModelAndView modelAndView = new ModelAndView(url);
		modelAndView.addObject("methods", methodNames);
		modelAndView.addObject("classes", classNames);
		return modelAndView;
	}
	
	@RequestMapping(value = "/patternChecker/Results", method = RequestMethod.GET)
	ModelAndView showResults()
	{
		//prints out the methods that were found. Redirected here from checkGit()
		ModelAndView modelAndView = new ModelAndView("showResult");
		modelAndView.addObject("methods", methodNames);
		modelAndView.addObject("classes", classNames);
		modelAndView.addObject("singleton", singletonNames);
		return modelAndView;
	}
}
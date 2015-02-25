package edu.csupomona.cs.patternChecker.controller;

import java.io.File;

import edu.csupomona.cs.patternChecker.checkers.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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
	private static List<String> classNames = new ArrayList<String>();
	private static SingletonChecker singletonChecker = new SingletonChecker();
	private static PrototypeChecker prototypeChecker = new PrototypeChecker();
	private static SubjectChecker subjectChecker = new SubjectChecker();
	private static ObserverChecker observerChecker;
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

	        singletonChecker.visit(cu, null);
	        prototypeChecker.visit(cu, null);
	        subjectChecker.visit(cu,  null);
		}
		
		if(!subjectChecker.getSubjects(false).isEmpty())
		{
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
				observerChecker = new ObserverChecker(subjectChecker.getSubjects(false));
		        observerChecker.visit(cu, null);
			}
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
	
	@RequestMapping(value = "/patternChecker/Home", method = RequestMethod.GET)
	ModelAndView patternCheckerHome() 
	{
		ModelAndView modelAndView = new ModelAndView(url);
		return modelAndView;
	}
	
	@RequestMapping(value = "/patternChecker/Results", method = RequestMethod.GET)
	ModelAndView showResults()
	{
		//prints out the methods that were found. Redirected here from checkGit()
		ModelAndView modelAndView = new ModelAndView("showResult");
		modelAndView.addObject("singleton", singletonChecker.getSingletonNames());
		modelAndView.addObject("singletonErrors", singletonChecker.getSingletonErrors());
		modelAndView.addObject("prototype", prototypeChecker.getPrototypeNames());
		modelAndView.addObject("subject", subjectChecker.getSubjects(true));
		modelAndView.addObject("subjectErrors", subjectChecker.getSubjectErrors());
		return modelAndView;
	}
}
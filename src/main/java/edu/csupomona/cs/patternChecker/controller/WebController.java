package edu.csupomona.cs.patternChecker.controller;

import java.io.File;

import edu.csupomona.cs.patternChecker.checkers.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
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
	private static SingletonChecker singletonChecker = new SingletonChecker();
	private static PrototypeChecker prototypeChecker = new PrototypeChecker();
	private static SubjectMediatorChecker subjectMediatorChecker = new SubjectMediatorChecker();
	private static ObserverChecker observerChecker;
	private static String gitURL;

	//http://localhost:8080/patternChecker/Home
	//https://github.com/JonathanJohannsen/ObserverTest
	//https://github.com/JonathanJohannsen/java-design-patterns
	@RequestMapping(value = "/patternChecker/{repoURL}/checkGit", method = RequestMethod.POST)
	void checkGit(@PathVariable("repoURL") String repoURL) throws ParseException, IOException
	{
		//get a UUID for the name of the directory to place the repository in
		UUID idOne = UUID.randomUUID();

		String directory = "src/main/resources/" +idOne.toString() + "repo";
		File file = new File(directory);
		byte[] decoded = Base64.decodeBase64(repoURL);
		String repoURLFinal=new String(decoded, "UTF-8");
		gitURL = repoURLFinal;
		try 
		{
			//clone the repo from GitHub
			Git.cloneRepository().setURI(repoURLFinal).setDirectory(file).call().close();
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
	        subjectMediatorChecker.visit(cu,  null);
		}
		
		if(!subjectMediatorChecker.getSubjects(false).isEmpty())
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
				observerChecker = new ObserverChecker(subjectMediatorChecker.getSubjects(false),
						subjectMediatorChecker.getMediators(false));
				observerChecker.visit(cu, null);
			}
		}
		//once the parsing is done, delete the cloned directory
		FileUtils.deleteDirectory(file);
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
	ModelAndView showResults()
	{
		//prints out the methods that were found. Redirected here from checkGit()
		ModelAndView modelAndView = new ModelAndView("home");
		modelAndView.addObject("singleton", singletonChecker.getSingletonNames());
		modelAndView.addObject("singletonErrors", singletonChecker.getSingletonErrors());
		modelAndView.addObject("prototype", prototypeChecker.getPrototypeNames());
		modelAndView.addObject("prototypeErrors", prototypeChecker.getPrototypeErrors());
		modelAndView.addObject("subject", subjectMediatorChecker.getSubjects(true));
		modelAndView.addObject("subjectErrors", subjectMediatorChecker.getSubjectErrors());
		modelAndView.addObject("mediator", subjectMediatorChecker.getMediators(true));
		modelAndView.addObject("gitURL", gitURL);
		return modelAndView;
	}
}
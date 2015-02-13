package edu.csupomona.cs.patternChecker;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class App 
{                

	/** 
	 * The following bean configures the file upload 
	 * facility used by Spring web framework.
	 */
    @Bean
    MultipartConfigElement multipartConfigElement() 
    {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }    

    
    /** This is the running main method for the web application */
    public static void main(String[] args) throws Exception 
    {
    	// Please ignore the following system property for now.
    	// This will be used and explained in the later assignments.
        System.setProperty("java.awt.headless", "false");
        // Run Spring Boot
        SpringApplication.run(App.class, args);
    }
}

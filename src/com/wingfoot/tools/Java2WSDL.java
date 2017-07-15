package com.wingfoot.tools;

import java.io.*;
import java.util.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.registry.*;
/**
 * Java2WSDL is responsible for creating a WSDL file from
 * a Java class.  The user can specify options to customize
 * the generated WSDL file.  
 */
public class Java2WSDL 
{

  /* command line switches more can be added when more options appear */
  private static final String SWITCH = "-";
  private static final String SERVICE_ENDPOINT_SWITCH = "e";
  private static final String IS_DOCUMENT_STYLE_SWITCH = "s";
  private static final String NAME_SPACE_MAPPING_SWITCH = "m";
  private static final String OUTPUT_RESOURCE_NAME_SWITCH = "f";
  private static final String PUBLISH_SERVICE_SWITCH = "p";
  private static final String USE_LITERAL_SWITCH = "l";
  private static final String DEPLOYED_WSDL_LOCATION = "d";
  
  public Java2WSDL()
  {
  }

  /**
   * A private method responsible for parsing user arguments
   * The arguments are passed as a String[].  
   * Return an Options object encapsulating all the user inputs.
   * @param args - a String[] of arguments
   * @return options - An Options object encapsulating the args is returned
   */
  private Options parseUserOptions(String[] args)
  throws Exception
  {
    Options options = new Options();
    boolean containsEndpoint = false;
    for( int i = 0; i < args.length; i++ ) 
    {
      if (args[i].startsWith(Java2WSDL.SWITCH) && args.length > 1 && i < (args.length - 1)) 
      {
	
        String _switch = args[i].substring(1, args[i].length());
        if(_switch.equals(Java2WSDL.SERVICE_ENDPOINT_SWITCH) &&
            !args[i+1].startsWith(Java2WSDL.SWITCH)) 
        {
            options.setServiceEndPoint(args[i+1]);
            i++; /* increment the counter so it goes to the next option */
            containsEndpoint = true;
            options.setPublishWSDL(true);
        }
        else if(_switch.equals(Java2WSDL.USE_LITERAL_SWITCH))
        {
          options.setUseLiteral(true); /* just the presense of a -l will make this literal encoding */
        }
        else if(_switch.equals(Java2WSDL.IS_DOCUMENT_STYLE_SWITCH))
        {
          options.setStyleDocument(true); /* just the presense of a -s will make this document style */
        }
        /*
        else if(_switch.equals(Java2WSDL.PUBLISH_SERVICE_SWITCH)){
          options.setPublishWSDL(true);
        }
        */
        else if(_switch.equals(Java2WSDL.NAME_SPACE_MAPPING_SWITCH) &&
          i < (args.length - 3)) 
        {
          if(!args[i+1].startsWith(Java2WSDL.SWITCH) &&
             !args[i+2].startsWith(Java2WSDL.SWITCH)) 
          {
            options.setNamespaceMapping(args[i+1], args[i+2]);
            i += 2; /* increment the counter */
          }
        }//else
        else if(_switch.equals(Java2WSDL.OUTPUT_RESOURCE_NAME_SWITCH) &&
          !args[i+1].startsWith(Java2WSDL.SWITCH)) 
        {
          options.setOutputResourceName(args[i+1]);
        }
        else 
        {
            /* no options passed...should be ok for now */
        }
      } //if
    } //for
    //if(!containsEndpoint)
     // throw new Exception("An endpoint has not been specified");
      
    return options;
  }


  /**
   * This private method is responsible for all the Java2WSDL logic, which is as
   * followed
   * <ul>
   * <li>Parse the user options
   * <li>Call Class.forName() to load the class to be inspected
   * <li>Call XMLFactory.parse(), passing it the class to inspect, the Options object,
   * and the holderType to return.  In this case, the holderType is a WSDLHolder
   * <li>Call XMLFactory.toXML() to return the SerializedHolder Objects
   * <li>If no publishURL is specified, write all the WSDL objects to the file system.  
   * Otherwise, the generated wsdl is sent to the Parvus server with the URL passed in
   * </ul>
   * @param args user arguments
   * @return nothing is returned
   */
  private void run(String[] args) 
  {

    if(args.length == 0) 
    {
      /* nothing is passed, thus cannot do anything.  Print the usage. */
      printUsage();
    } 
    else 
    {
      try 
      {
        Options options = parseUserOptions(args);
        Class classToConvert = Class.forName(args[args.length - 1]);
        String rtnFromRegistry = null;
        if(options.publishWSDL())
        {
          rtnFromRegistry = Registry.publish(classToConvert, options.getServiceEndPoint(), options);
          System.out.println(rtnFromRegistry);
 
        }
        else 
        {
          /* If not publish location, simply write the WSDL to disk */
          //SerializedHolder[] sh = XMLFactory.toXML(wh);
          //writeWSDLToDisk(sh);
          rtnFromRegistry = Registry.publish(classToConvert, options);
          System.out.println(rtnFromRegistry);
        }

      } catch (Throwable t) 
      {
        System.out.println("An error occured: " + t.getMessage());
        t.printStackTrace();
        printUsage();
        System.exit(-1);
      }//catch
    }//else
  } //run

  /**
   * Private method to print the commands and options available for Java2WSDL
   */
  private void printUsage() 
  {
    StringBuffer sb = new StringBuffer();
    sb.append("\n");
    sb.append("Java2WSDL usage: java com.wingfoot.tools.Java2WSDL <options> <classname>").append("\n");
    sb.append("Available options:").append("\n");
    sb.append("  -e <ServiceEndpoint> the URI of a Parvus server.  If specified, the WSDL is published to the server.  If not specified, the WSDL is written to the local file system.").append("\n");
    sb.append("  -f <WSDL filename> the name of the WSDL to create.  If not specified, a name is assigned.").append("\n");
    sb.append("  -s generate the WSDL using document style.  If not specified, RPC style is used.").append("\n");
    sb.append("  -l generate the WSDL using literal encoding.  If not specified, Section V encoding is used.").append("\n");
    sb.append("  -m <java package> <namespace> maps a java package to a target namespace").append("\n");
    sb.append("\n");
    sb.append("Please note that <classname> refers to the java package and classname (for example: com.foo.class)").append("\n");

    System.out.println(sb.toString());
  }//printUsage

  /**
   * This is the main method.  All it does is call the run method
   */
  public static void main (String[] args) 
  {
      new Java2WSDL().run(args);
  }
}//class

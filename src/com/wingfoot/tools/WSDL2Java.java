package com.wingfoot.tools;

import java.io.*;
import java.lang.*;
import java.util.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.Constants;

/**
 * This is the class responsible for creating Java code out of a WSDL
 */
public class WSDL2Java 
{

  /* command line switches */
  private static final String SWITCH = "-";
  private static final String PACKAGE_TO_NAMESPACE_SWITCH = "p";
  private static final String USE_SESSION_SWITCH = "s";
  private static final String ALTERNATE_TRANSPORT_SWITCH = "t";
  
  public WSDL2Java()
  {
  }

  /**
   * A private method responsible for parsing user arguments
   * The arguments are passed as a String[].  This method 
   * returns an Options object encapsulating all the user inputs.
   * @param args - a String[] of arguments
   * @return options - An Options object encapsulating the args is returned
   * @throws Exception - An exception is returned if an error occurs while parsing
   * user options
   */
  private Options parserUserOptions(String[] args)
  throws Exception
  {


    Options options = new Options();
    
    for( int i = 0; i < args.length; i++ ) 
    {
      if (args[i].startsWith(WSDL2Java.SWITCH) && args.length > 1 && i < (args.length - 1)) 
      {
	
        String _switch = args[i].substring(1, args[i].length());

        if(_switch.equals(WSDL2Java.PACKAGE_TO_NAMESPACE_SWITCH) &&
            i < (args.length - 3)) 
        {
          if(!args[i+1].startsWith(WSDL2Java.SWITCH) &&
             !args[i+2].startsWith(WSDL2Java.SWITCH)) 
          {
            options.setPackageMapping(args[i+1], args[i+2]);
            i += 2; /* increment the counter */
          }
        }//else
        else if(_switch.equals(WSDL2Java.USE_SESSION_SWITCH))
        {
          options.setUseSession(true);  
        }
        else if(_switch.equals(WSDL2Java.ALTERNATE_TRANSPORT_SWITCH) &&
        !args[i+1].startsWith(WSDL2Java.SWITCH))
        {
          if(doesClassExist(args[i+1]))
          {
            options.setAlternateTransport(args[i+1]);
            i ++; /* increment the counter */
          }
          else
            throw new Exception("ERROR_WSDL_2_JAVA_001:"+Constants.ERROR_WSDL_2_JAVA_001);
        }
        else 
        {
          /* no options passed...should be ok for now */
        }
      } //if
    } //for
    //put the wsdlLocation in the options object also
    options.setWSDLLocation(args[args.length-1]);
    return options;
  }

  /**
   * Checks and see if the class exists, given the class name.
   * @param className The name of the class to check
   * @return boolean
   */
  private boolean doesClassExist(String className)
  {
    boolean doesExist = true;
    try
    {
      Class.forName(className);
    } catch (ClassNotFoundException ce)
    {
      doesExist = false;
    }
    return doesExist;
  }//doesClassExist
  
  /**
   * A utility method for writing Java files to the filesystem
   * The SerializedHolder[] contains all the Java files needed
   * @param holder - An array of SerializedHolder which constains all the Java
   * code to be written
   * @return String[] of the names of the files written to disk
   * @throws IOException - An IOException is thrown if the files cannot be written to disk
   */
  private String[] writeJavaToDisk(SerializedHolder[] holder)
    throws IOException
  {
    String[] fileNames = new String[holder.length];
    for (int i=0; holder!=null && i<holder.length; i++)
    {
      SerializedHolder s = holder[i];
      OutputStream os = new BufferedOutputStream(new FileOutputStream(s.getDestination()));
      os.write(s.getXML());
      os.close();

      //put the filename into the filename array
      fileNames[i] = s.getDestination();
    }
    return fileNames;
  } //writeJavaToDisk

  private void run(String[] args)
  {
    if(args.length == 0)
      printUsage();
    else 
    {
      try 
      {
        byte[] wsdlByte = XMLFactory.getPayload(args[args.length - 1]);
        WSDLHolder wh = (WSDLHolder)XMLFactory.parse(wsdlByte, args[args.length - 1]);
        SerializedHolder[] sh = XMLFactory.toJava(wh, parserUserOptions(args));
        if(sh != null && sh.length > 0)
        {
          String[] fileNames = writeJavaToDisk(sh);
          System.out.println("Parvus generated the following client files:");
          //write the filenames out to the console
          for(int i = 0; fileNames!=null&&i<fileNames.length; i++)
          {
            System.out.println("  "+fileNames[i]);
          }
        }
      } catch (Throwable t)
      {
        System.out.println("An error occured: " + t.getMessage());
        t.printStackTrace();
        printUsage();
        System.exit(-1);
      }
    }
    
  }

  private void printUsage()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("\n");
    sb.append("WSDL2Java usage: java com.wingfoot.tools.WSDL2Java <options> <wsdl file>").append("\n");
    sb.append("Available options:").append("\n");
    sb.append("  -p <namespace> <java package> The p switch allows for specifying namespace to package mapping").append("\n");
    sb.append("  -t <transport class> The t switch allows for specifying an alternate transport").append("\n");
    sb.append("  -s The s switch specify that the stubs maintain a session variable").append("\n");
    sb.append("The <wsdl file> can be a URL or a file on the filesystem");
    sb.append("\n");
    System.out.println(sb.toString());
  }

  public static void main (String[] args)
  {
    new WSDL2Java().run(args);
  }
  
}
 
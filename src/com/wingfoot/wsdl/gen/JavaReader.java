package com.wingfoot.wsdl.gen;
import com.wingfoot.tools.*;
import com.wingfoot.wsdl.*;
/**
 * Interface that defines the properties of a JavaReader
 * A JavaReader is used to parse a Java class and convert
 * it into a WSDLHolder.
 */
public interface JavaReader
{
  /**
   * This parse method is responsible for parsing a Java class and
   * converting it into a WSDLHolder object.  The WSDLOptions encapsulates
   * user input to customize some of the fields in WSDLHolder.
   * @param classToConvert - The class to encapsulate in WSDLHolder.
   * @param options - WSDLOptions contains user specified input for
   * customizing some of the WSDLHolder fields
   * @exception WSDLException - A WSDLException is thrown if an error 
   * occurs while creating a WSDLHolder
   * @throws Exception - An exception is thrown if an error occurs
   */
  public WSDLHolder parse(Class classToConvert, Options options)
    throws WSDLException, Exception;
}

package com.wingfoot.wsdl.gen;

import java.io.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;

/**
 * Interface that defines the properties of a JavaWriter.
 * A JavaWriter takes a WSDL and returns the Java
 * representation of the WSDL.
 */
public interface JavaWriter 
{
  /**
   * Abstract method that takes a WSDLHolder
   * and returns back the Java representation.
   * The Java representation is returned as
   * a SerializedHolder array.  Each element
   * of the array is a SerializedHolder that
   * contains the byte[] representing the Java
   * class and the name of the file in the file
   * system to write to.
   */
  public SerializedHolder[] toJava(WSDLHolder wsdl, Options options)
    throws JavaHolderException, IOException, WSDLException;
  
}

package com.wingfoot.wsdl;
import org.kxml.io.*;

/**
 * An Interface that all WSDL extensibility elements 
 * implement.
 */
public interface Extension 
{
  /**
   * Converts concrete instances of this interface
   * to a WSDL stub.  The stub is written to the
   * XMLWriter
   * @param writer XMLWriter to write the stub to.
   * @throws Exception if an error occurs while
   * writing to the writer.
   */
  public void toWSDL(XMLWriter writer);
}
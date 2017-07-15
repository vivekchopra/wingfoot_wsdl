package com.wingfoot.wsdl;
import org.kxml.io.*;

public interface MessageFormat 
{
 /**
 * Converts concrete instances of this interface
 * to a WSDL stub.  The stub is written to the
 * XMLWriter
 * @param writer XMLWriter to write the stub to.
 */
  public void toWSDL(XMLWriter writer);
}
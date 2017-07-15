package com.wingfoot.xml;
import com.wingfoot.xml.*;
import java.io.*;
/**
 * Inferface that all holders of XML documents implement.
 * The two documents currently supported are WSDL and
 * XML Schema.  Their corresponding holders are WSDLHolder
 * and SchemaHolder.
 */
public interface XMLHolder 
{
  public String getTargetNamespace();

  public String getDestination();

  public void  setTargetNamespace(String targetNamespace);

  public void  setDestination(String destination);

  public SerializedHolder[] toXML() throws XMLException, IOException;

}
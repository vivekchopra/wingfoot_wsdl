package com.wingfoot.wsdl;

/**
 * Interface that defines the properties of a WSDLReader.
 * A WSDLReader is used to parse a well formed WSDL
 * and convert to an object (Java) representation.  The object 
 * representation is an instance of WSDLHolder.  All
 * components of the WSDL (types, portType, bindings, service etc)
 * are accessible using the getter methods in WSDLHolder.
 * <p>
 * An implementation of WSDLReader is provided.  This is 
 * the default reader.  Users can create their own WSDL readers
 * by implementing instance of this interface and providing
 * a reference to the new class in WSDLReaderFactory class
 */
public interface WSDLReader 
{
  /**
   * Takes a  a well formed WSDL 
   * and returns back an instance of WSDLHolder that encapsulates
   * the WSDL.  The encapsulated WSDL can be accessed
   * using the getter methods in the WSDLHolder interface.
   * @praam payload the byte representation of the WSDL
   * @param resource String representing a WSDL resource.  The 
   * resource may reside on a web server or on the local file
   * system.  If residing on the web server, the complete URL 
   * path has to be specified (including the protocol....http://...).
   * @return WSDLHolder Object representation of the XML schema.
   * @throws Exception if any error occurs; typically concrete 
   * implementations throw a subclass of the Exception class.
   */
  public WSDLHolder parse(byte[] payload, String location) throws Exception;
}
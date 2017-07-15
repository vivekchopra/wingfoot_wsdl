package com.wingfoot.registry;

import com.wingfoot.soap.encoding.*;

/**
 * Encapsulates all the neccessary mapping required for the 
 * Client Proxy to use.  There are five things that are encapsulated 
 * in the ProxyMap:
 * <ul>
 * <li>WSDL Location - Location of the WSDL.  The WSDL can either be located in
 * a URL or on the local filesystem
 * <li>Interface class - The Interface class passed to com.wingfoot.registry.Registy.bind()
 * <li>TypeMapping Registry - The typemapping registry for seializing and deserializing a 
 * soap payload
 * <li>useSession - States that a web service being invoked maintains a session.  
 * Sessions are supported in the J2SEHTTPTransport
 * <li>transport - An custom transport can be specified
 * </ul>
 */
public class ProxyMap 
{
  private String WSDLLocation = null;
  private Class interfaceClass = null;
  private TypeMappingRegistry tmr = null;
  private Class transport = null;
  private boolean useSession = false;
  private boolean displayPayload;


  /**
   * Constructs a new instance of ProxyMap.  The constructor defaults the transport
   * to com.wingfoot.soap.transport.J2SEHTTPTransport.  It can be overridden by
   * the setTransport() method .
   * @throws Exception Thrown if J2SEHTTPTransport cannot be found
   */
  public ProxyMap()
  throws Exception
  {
    transport = Class.forName("com.wingfoot.soap.transport.J2SEHTTPTransport");
  }//ProxyMap constructor

  /**
   * Sets the Typemapping Registry for proxy use.  The TypeMapping registry
   * is used to serialize and deserialize a SOAP payload
   * @param tmr The TypeMappingRegisty to set
   * @return void
   */
  public void setTypeMappingRegistry(TypeMappingRegistry tmr)
  {
    this.tmr = tmr;
  }//setTypeMappingRegistry

  /**
   * Gets the Typemapping registry.
   * @param
   * @return TypeMappingRegistry The TypeMappingRegistry used for serialization
   * and deserialization
   */
  public TypeMappingRegistry getTypeMappingRegistry()
  {
    return tmr;
  }//getTypeMappingRegistry

  /**
   * Sets the custom transport to be used
   * @param transport The transport class the proxy uses to send the SOAP payload
   * @return void
   */
  public void setTransport(Class transport)
  {
    this.transport = transport;
  }//setTransport

  /**
   * Gets the transport class
   * @return Class The transport class
   */
  public Class getTransport()
  {
    return transport;
  }//getTransport

  /**
   * Allows for a session to be maintained
   * @param useSession true if session is to be maintained, false if no session
   * to be maintained
   * @return void
   */
  public void setUseSession(boolean useSession)
  {
    this.useSession = useSession;
  }//setUseSession

  /**
   * Gets the boolean indicating whether to use session
   * @return boolean true if useSession, false otherwise
   */
  public boolean getUseSession()
  {
    return useSession;
  }//getUseSession

  /**
   * Sets the location of the WSDL.  The location is either
   * a URL or a file on the local filesystem
   * @param WSDLLocation The location of the WSDL
   * @return void
   */
  public void setWSDLLocation(String WSDLLocation)
  {
    this.WSDLLocation = WSDLLocation;
  }//setWSDLLocation

  /**
   * Gets the wsdl location
   * @return String The WSDL Location
   */
  public String getWSDLLocation()
  {
    return WSDLLocation;
  }//getWSDLLocation

  /**
   * Gets the interface class used by the proxy
   * @return Class The interface class used in the proxy
   */
  public Class getInterfaceClass()
  {
    return interfaceClass;
  }//getInterfaceClass


  /**
   * Sets the Interface class that is passed to the 
   * com.wingfoot.registry.Registry.bind() method
   * @param interfaceClass The interface class used in the proxy
   * @return void
   */
  public void setInterfaceClass(Class interfaceClass)
  {
    this.interfaceClass = interfaceClass;
  }//setInterfaceClass

  /**
   * Please see documentation for setDisplayPayload.
   * @return 
   */
  public boolean isDisplayPayload()
  {
    return displayPayload;
  }
  
  /**
   * Allows the ability to display the XML payload sent
   * and received
   * @param newDisplayPayload true if the payload is to be
   * displayed; false otherwise.
   */
  public void setDisplayPayload(boolean newDisplayPayload)
  {
    displayPayload = newDisplayPayload;
  }



  
}//ProxyMap
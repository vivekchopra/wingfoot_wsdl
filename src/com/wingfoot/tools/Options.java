package com.wingfoot.tools;
import java.util.Hashtable;
import com.wingfoot.*;

/**
 * Encapsulates the command line options that is specified
 * when one of the command line tools in invoked.  Depending
 * on the kind of tool, some properties in this class may
 * be not applicable for the tool.
 */
public class Options 
{
  /**
   * isStyleDocument is defaulted to false
   */
  private boolean isStyleDocument = false; 
  private String serviceEndPoint = null;
  private Hashtable namespaceMapping = null;
  private Hashtable packageMapping=null;
  private String outputResourceName = null;
  private boolean useLiteral = false;
  private String WSDLLocation = null;
  private boolean useSession = false;
  private String alternateTransport = null;
  private boolean publish = false;

  public Options()
  {
  }

  /**
   * Returns the packageMapping.  A package
   * mapping is used to map a namespace to 
   * a package name.  This is used while generating
   * Java stubs from WSDL.  Other than WSDL2Java,
   * this method returns null.
   * @return Hashtable of packageMapping; the
   * key is the namespace and the value is
   * the package name; returns null if there
   * is no packagemapping.
   */
  public Hashtable getPackageMapping() 
  {
    return packageMapping;
  }

  /**
   * Returns the package name associated to the
   * namespace.  Only useful in WSDL2Java.
   * @param namespace String with the namespace for
   * which the package name is desired.
   * @return String the package name; null is the
   * namespace is not mapped to a package.
   */
  public String getPackageMapping(String namespace) 
  {
    if (packageMapping==null || namespace==null)
      return null;
    return (String) packageMapping.get(namespace);
  }

  /**
   * Associates a namespace to a package.  Only
   * used in WSDL2Java.
   * @param namespace the namespace
   * @param packageName to associate to the namespace.
   */
  public void setPackageMapping(String namespace, String packageName) 
  {
    if (packageMapping==null)
      packageMapping=new Hashtable();
    packageMapping.put(namespace, packageName);
  }

  /**
   * Retrieves the endpoint of the service.  This appears
   * in the &lt;port&gt element in the WSDL.  This
   * options is used in java2wsdl utility.
   * @return String the endpoint of the service.
   */
  public String getServiceEndPoint()
  {
    return serviceEndPoint;
  }

  /**
   * Sets the endpoint of the service.  This appears
   * in the &lt;port&gt element in the WSDL.  This
   * option is used in java2wsdl utility.
   * @param newEndPoint the endpoint of the service.
   */
  public void setServiceEndPoint(String newEndPoint)
  {
    serviceEndPoint = newEndPoint;
  }

  /**
   * Retrieves true if the Style is specified as Document.
   * The default is RPC.  This option is set in java2wsdl
   * utility.
   * @return boolean true if the style is document false
   * if not (default).
   */
  public boolean isStyleDocument()
  {
    return isStyleDocument;
  }

  /**
   * Sets the style for the WSDL.
   * The default is RPC.  This option is set in java2wsdl
   * utility.
   * @param newIsStyleDocument true if the style is document false
   * if not (default).
   */
  public void setStyleDocument(boolean newIsStyleDocument)
  {
    isStyleDocument = newIsStyleDocument;
  }


  /**
   * Retrieves the package name to namespace mapping.
   * Only useful in Java2WSDL.
   * @return Hashtable the package name-namespace mapping
   * null if there is no mapping.
   */
  public Hashtable getNamespaceMapping()
  {
    return namespaceMapping;
  }

  /**
   * Maps a package name to a namespace.  This option
   * is provided in the java2wsdl utility.  If there is
   * a package name for which a namespace is not provided,
   * a namespace is assigned.
   * @param name the name of the package.
   * @param value the namespace to associate to the package.
   */
  public void setNamespaceMapping(String name, String value)
  {
    if (namespaceMapping == null)
      namespaceMapping = new Hashtable();
    namespaceMapping.put(name, value);
  }

  /**
   * Retrieves the output resource name.
   * @return String the output resource name; null
   * if none is specified.
   */
  public String getOutputResourceName()
  {
    return outputResourceName;
  }

  /**
   * Sets the file name (absolute path) where the output has
   * to be written.  This is used in java2wsdl and wsdl2java
   * utility.
   * @param newOutputResourceName the name of the resource.
   */
  public void setOutputResourceName(String newOutputResourceName)
  {
    outputResourceName = newOutputResourceName;
  }

   /**
    * Gets the WSDL Location
    * @param String The WSDL Location
    */
   public String getWSDLLocation()
   {
     return WSDLLocation;
   }

   /**
    * Sets the WSDL Location
    * @param WSDLLocation The wsdl can either be on the local filesystem
    * or a URL
    */
   public void setWSDLLocation(String WSDLLocation)
   {
     this.WSDLLocation = WSDLLocation;
   }
  
  /**
   * Retrieves the namespace for a given package name.
   * The retrieval rules are as follows:
   * <li> If packageName is null the namespace is 
   * http://www.wingfoot.com/namespace
   * <li> If the namespace was not previously set using
   * the setNamespaceMapping method OR if the namespace
   * was set but the package name is not mapped, then the 
   * namespace is the packageName prefixed with "http://"
   * <li> otherwise the namespace is the one mapped
   * in the Hashtable.
   */
  public String getNamespaceMapping(String packageName) 
  {
    if (packageName==null)
      return Constants.DEFAULT_TARGETNAMESPACE;
    else if (this.namespaceMapping==null)
      return "http://"+packageName;
    else 
    {
      String tns= (String)this.namespaceMapping.get(packageName);
      return tns==null?"http://"+packageName:tns;
    }
  } //getNamespace

  /**
   * Gets the boolean indicating whether literal is used or not
   * @return boolean True if literal, false otherwise
   */
  public boolean isUseLiteral()
  {
    return useLiteral;
  }

  /**
   * Sets the boolean indicating whether literal is used or not
   * @param newUseLiteral True if literal, false otherwise
   */
  public void setUseLiteral(boolean newUseLiteral)
  {
    useLiteral = newUseLiteral;
  }

  /**
   * Gets the boolean indicating session usage
   * @return boolean True if use session, false otherwise
   */
  public boolean getUseSession()
  {
    return useSession;
  }

  /**
   * Sets boolean which indicates a session should be maintained
   * @param useSession True if useSession, false otherwise
   */
  public void setUseSession(boolean useSession)
  {
    this.useSession = useSession;
  }

  /**
   * Sets an Alternate tranports to be used while invoking a web service
   * The default transport is com.wingfoot.soap.transport.J2SEHTTPTransport
   * @param alternateTransport String of the class that is be used as a Transport
   */
  public void setAlternateTransport(String alternateTransport)
  {
    this.alternateTransport = alternateTransport;
  }

  /**
   * Gets the alternate transport name to be used while invoking a web service
   * @return String name of the class to be used as the transport
   */
  public String getAlternateTransport()
  {
    return alternateTransport;
  }

  /**
   * Sets the destination WSDL url
   */
  public void setPublishWSDL(boolean publish)
  {
    this.publish = publish;
  }

  public boolean publishWSDL()
  {
    return publish;
  }
}

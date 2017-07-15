package com.wingfoot.xml;
import com.wingfoot.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.tools.*;
import com.wingfoot.wsdl.gen.*;
import org.kxml.parser.*;
import org.kxml.*;
import java.io.*;
import java.net.*;

/**
 * A Factory pattern that aids in parsing or writing
 * a XML document.  The current XML documents supported
 * are WSDL and XML Schema. 
 */
public class XMLFactory 
{
  /**
   * These two static variables are passed into the Java
   * parse methods to determine whether to return
   * a WSDLHolder or a SchemaHolder
   */
  public static final int WSDLHOLDER_TYPE = 0;
  public static final int SCHEMAHOLDER_TYPE = 1;
  
  /**
   * Default constructor. This class cannot be
   * subclasses or instantiated.
   */
  private XMLFactory()
  {
  }
  
  /**
   * Takes a payload representing a XML document and
   * encapsulates it as a Holder.  The method is able
   * to determine the kind of XML document and returns
   * back the proper Holder.
   * @param payload XML document represented as a byte
   * array.  The two supported XML documents are WSDL
   * and XML Schema.
   * @param location String identifying the location
   * where the XML documented is located.  It is either
   * a location on the local file system or on a web
   * server.  The location is used in instances where
   * the XML document contains an import statement
   * and has (incorrectly) with a relative value in
   * the location attribute.  The absoluteURI is then
   * determined using theis location parameter.
   * @return Holder the XML document encapsulated as 
   * as Holder.  Depending on the kind of payload the
   * Holder is either an instance of WSDLHolder or 
   * SchemaHolder.
   * @throws IOException if an error occurs while processing
   * the payload.
   * @throws SchemaException if the XML document is
   * a schema and an error occurs while processing the
   * schema
   * @throws WSDLException if the XML document is
   * a WSDL and an error occurs while processing the
   * WSDL
   * @throws Exception if any other occurs while processing
   * the payload.
   */
  public static XMLHolder parse (byte[] payload, String location) 
  throws IOException, SchemaException, WSDLException, Exception
  {
    if (payload==null)
      return null;
    if (XMLFactory.isPayloadSchema(payload))
      return new SchemaReaderInstance().parse(payload);
    else
      return new WSDLReaderInstance().parse(payload, location);
    //Determine the kind of payload
  } //parse

  /**
   * Takes a  Java class
   * and returns a XMLHolder representation of it.  The holderType
   * parameter is used to determine whether to return a WSDLHolder
   * or a SchemaHolder.
   * @param class - The java class to convert into XMLHolder
   * @param options - This options class encapsulates user input that
   * affects the output of the XMLHolder
   * @param holderType - HolderType is used to determine which type of XMLHolder to return.
   * Currently we only support WSDLHolder, however, it is easy to extend this to
   * return any type of holder.
   * @return XMLHolder The appropriate XMLHolder is returned depending on the
   * holderType specified
   * @throws SchemaException a Schema Exception is thrown if any error occurs while
   * processing a schema
   * @throws WSDLException a WSDL Exception is thrown if any error occurs while
   * processing a WSDL
   * @throws Exception an Exception is thrown if any other error occurs
   */
  public static XMLHolder parse (Class classToConvert, Options options, int holderType)
    throws SchemaException, WSDLException, Exception
  {
    /**
     * For now, just return an WSDLHolder.  When the need for a SchemaHolder is needed
     * we can add logic for it.
     */
    return new JavaReaderInstance().parse(classToConvert, options);
  }

  /**
   * Looks at a payload to determine if it represents
   * a schema or a WSDL.
   * @param payload the payload encapsulating a XML
   * document.
   * @return boolean true if the payload is a XML Schema
   * false if not.
   */
  private static boolean isPayloadSchema(byte[] payload) throws IOException
  {
    XmlParser parser=new XmlParser(new BufferedReader(
                                   new InputStreamReader(
                                   new ByteArrayInputStream(payload))));
    ParseEvent pe=null;
    while(true) 
    {
      pe=parser.read();
      if (pe==null)
        continue;
      if (pe.getType()==Xml.START_TAG)
        break;
      else if (pe.getType()==Xml.END_DOCUMENT)
        return false;
    }
    if (pe.getName().equals("schema") &&
    pe.getNamespace().equals(Constants.SOAP_SCHEMA))
      return true;
    else
      return false;
  } //isPayloadSchema

    /**
   * Utility method that reads the content of a file
   * represented by location.
   * @param location String that contains the location
   * of a XML document.  The location is either a 
   * file in the local file system or a resource on a 
   * web server. 
   * <p>
   * The location must be a absolute location
   * (not a relative URI or location).
   * @return byte[] byte representation of the resource.
   * @throws IOException if any error occurs while reading
   * from the resource.
   */
   
  public static byte[] getPayload(String location) throws IOException
  {
    InputStream in =null;
    try 
    {
        URL u = new URL(location);
        in = u.openStream();
        in=new BufferedInputStream(in);
    } catch (MalformedURLException e) 
    {
        in = new BufferedInputStream(new FileInputStream(location));
    } 
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] temp = new byte[5056];

    while (true) 
    {
      int i = in.read(temp,0,2054);
      if (i==-1)
        break;
      else 
        bos.write(temp, 0, i);
    } //while
    temp=bos.toByteArray();
    bos.close();
    if (in!=null)
      in.close();
    return temp;
  } //getPayload

  /**
   * Takes a XMLHolder and converts it to instances
   * of SerializedHolder.  Each instance of 
   * SerializedHolder contains a well formed XML that
   * (currently) encapsulates either a WSDL or a XML
   * schema.
   * <p>
   * The 0th element of the return array is always the
   * base WSDL or XML schema.  If there are any $lt;import&gt
   * elements in the XML, the imported XML is 
   * encapsulated in the subsequent indexes in the return 
   * array.
   */
  public static SerializedHolder[] toXML(XMLHolder holder) 
  throws XMLException, IOException
  {
    return holder.toXML();
  }

  /**
   * Takes an XMLHolder and user options and
   * returns an array of SerializedHolders, which contains the
   * byte representation of all the Java classes need for the
   * WSDL2Java functionality
   * @param WSDLHolder a WSDLHolder encapsulating the WSDL to be
   * transformed into Java code
   * @param options User specified options that are needed
   * @return SerializedHolder[] this array contains the java code to be
   * written to disk
   */
  public static SerializedHolder[] toJava(WSDLHolder holder, Options options)
  throws XMLException, IOException, WSDLException, Exception
  {
    return new JavaWriterInstance().toJava(holder, options);
  }

  /**
   * Generates a Deployment Descriptor for a WSDLHolder.  Each PortType
   * in the WSDLHolder will get its own Deployment Descriptor.  The Deployment Descriptor
   * is sent to Parvus SOAP Server when deploying a new service.  The Deployment Descriptor
   * is in the form of a XML document.  The Name is [PortTypeName].DD
   * @param holder The WSDLHolder used for generating the Deployment Descriptor
   * @param classToConvert Class used to to generate the Deployment Descriptor
   * @param options User entered options
   * @return SerializedHolder[] this array contains the generated Deployment Descriptor
   * files.
   */
  public static SerializedHolder[] toDeploymentDescriptor(WSDLHolder holder, Class classToConvert, Options options)
  throws Exception, IOException
  {
    return new DeploymentDescriptorWriterInstance().toDeploymentDescriptor(holder, classToConvert, options);
  }
  
} //XMLFactory

package com.wingfoot.registry;

import java.io.*;
import java.net.*;
import java.util.*;

import org.kxml.io.*;
import com.wingfoot.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.soap.encoding.*;
/**
 * Provides the ability to publish, discover and use
 * services. 
 * <p>
 * Given a Java object, that has to be
 * exposed as a service, the methods in the Registry
 * is able to publish the WSDL to a given Parvus
 * server.
 * <p>
 * Given a WSDL and the Interface stub, the methods
 * in the Registry is able to send SOAP messages (use
 * the service) and return back the response as primitive
 * types or Java objects.
 * <p>
 * In future, methods in the Registry will be able
 * to discover services in a UDDI registry.
 */
 
public final class Registry 
{
  //Key is a String with WSDL url and the
  //value is a WSDLHolder
  private static Hashtable wsdlCache;
  static 
  {
    if (wsdlCache==null)
      wsdlCache=new Hashtable();
  }
  public Registry()
  {
  }

  /**
   * Publishes a WSDL to a Parvus server.  Takes a classToConvert and converts
   * it to a WSDL.  The converted WSDL is then sent to the Parvus server.
   * <p>
   * @param classtoConvert the class to publish as a WSDL
   * @param publishLocation the URL of the Parvus server where ths WSDL is published
   * @param options the options object encapsulating the options to be used in the generation
   * of the WSDL
   * @return String message from the server
   * @throws SchemaException is thrown while processing the schema.
   * @throws WSDLException is thrown if an error occurs while generating the WSDL
   * @throws RegistryException is thrown if an error occurs with the Registry
   * @throws Exception is thrown for general errors
   */
  public static String publish(Class classToConvert, String publishLocation, Options options)
    throws IOException, SchemaException, WSDLException, RegistryException, Exception
  {
    WSDLHolder wh = (WSDLHolder) XMLFactory.parse(classToConvert, options, XMLFactory.WSDLHOLDER_TYPE);
    byte[] returnMsg = null;
    if(wh != null)
    {
      /* set up options specified by the user */
      if(options.getOutputResourceName() != null)
        wh.setDestination(options.getOutputResourceName());
      else 
      {
        String destination = new String(classToConvert.getName()).substring(classToConvert.getName().lastIndexOf(".") + 1,
                                        classToConvert.getName().length()) + ".wsdl";
        wh.setDestination(destination);
      }
      SerializedHolder[] sh = XMLFactory.toXML(wh);
      
      if(sh != null)
        returnMsg = Registry.contactListener(options.getServiceEndPoint(), Registry.getBytes(sh));
      else
        throw new RegistryException("ERROR:ERROR_REGISTRY_003"+Constants.ERROR_REGISTRY_003);

      //write the deployment descriptor if the -d option is passed, along with
      //a WSDLlocation
      Registry.writeToDisk(XMLFactory.toDeploymentDescriptor(wh, classToConvert, options));

    }
    else 
    {
      throw new RegistryException("ERROR:ERROR_REGISTRY_002"+Constants.ERROR_REGISTRY_002);
    }

    StringBuffer sb = new StringBuffer();
      
    sb.append("The WSDL has been published and may be accessed at:").append("\n").append(new String(returnMsg)).append("\n");
    sb.append("A Deployment Descriptor has been written to the filesystem.");
      
    return sb.toString();
  }

  /**
   * Publishes WSDL to the local file system.  The options object
   * encapsulates user options, mainly package to namespace 
   * mappings.  This publish method returns void because the file is 
   * be written to disk.
   * @param classtoConvert The class to convert to WSDL representation
   * @param options The user encapsulated options
   * @return String A simple return message stating: PUBLISHED
   * @throws SchemaException is thrown if an error occurs in a schema method
   * @throws WSDLException is thrown if an error occurs while generating the WSDL
   * @throws RegistryException is thrown if an error occurs with the Registry
   * @throws Exception is thrown for general errors
   */
  public static String publish(Class classToConvert, Options options)
  throws IOException, SchemaException, WSDLException, RegistryException, Exception
  {
    WSDLHolder wh = (WSDLHolder) XMLFactory.parse(classToConvert, options, XMLFactory.WSDLHOLDER_TYPE);
        
    /* set up options specified by the user */
    if(options.getOutputResourceName() != null)
      wh.setDestination(options.getOutputResourceName());
    else 
    {
      String destination = new String(classToConvert.getName()).substring(classToConvert.getName().lastIndexOf(".") + 1,
      classToConvert.getName().length()) + ".wsdl";
      wh.setDestination(destination);
    }
          
    if(wh != null) 
    {
      SerializedHolder[] sh = XMLFactory.toXML(wh);
      Registry.writeToDisk(sh);  

      //write the deployment descriptor if the -d option is passed, along with
      //a WSDLlocation
      Registry.writeToDisk(XMLFactory.toDeploymentDescriptor(wh, classToConvert, options));
    } 
    else 
    {
      throw new RegistryException("ERROR:ERROR_REGISTRY_003"+Constants.ERROR_REGISTRY_003);
    }
    StringBuffer sb = new StringBuffer();
    sb.append("The WSDL has been written to the filesystem.").append("\n");
    sb.append("A Deployment Descriptor has been written to the filesystem.");
    return sb.toString();
  }

  /**
   * A utility method for writing a WSDL file to the file system.
   * The SerializedHolder[] contains all the wsdl files that needs to
   * be written to disk.
   * @param holder - An array of SerializedHolder which contains all the WSDLs to be written
   * @return void - Nothing is returned upon success
   * @throws IOException - An IOException is thrown if the files cannot be written to disk
   */
  private static void writeToDisk(SerializedHolder[] holder) throws IOException 
  {
    for (int i=0; holder!=null && i<holder.length; i++)
    {
      SerializedHolder s = holder[i];
      OutputStream os = new BufferedOutputStream(new FileOutputStream(s.getDestination()));
      os.write(s.getXML());
      os.close();
    }
  }//writeWSDLToDisk
  
  /**
   * Turns a SerializedHolder[] into a
   * byte[] which can be transmitted to the SOAPServer
   * @param sh The serializedHolder[] to convert
   * @return byte[] the converted serializedHolder
   * @throws IOException If an error occurs while converting the object to
   * a byte array, throw an IOException
   */
  private static byte[] getBytes(SerializedHolder[] sh)
  throws IOException
  {
    byte[] byteArray = null;
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(sh);
    byteArray = bos.toByteArray();
    bos.close();
    oos.close();    
    return byteArray;
  }

  /**
   * Interfaces with the listener to deploy, list
   * or remove a Service.
   * @param listenerURL The URL of the listener
   * @param serializedHolder The byte[] verion of the SerializedHolder[]
   * @throws IOException if any error occurs during
   * converting Service to a byte array.
   */
  private static byte[] contactListener(String listenerURL, byte[] serializedHolder) 
  throws Exception 
  {

    ByteArrayOutputStream bos=null;
    ObjectOutputStream oos=null;
    OutputStream os=null;
    InputStream inputStream=null;
    BufferedOutputStream bs=null;
    BufferedInputStream bis=null;
    URL theURL=null;
    HttpURLConnection uc=null;
    boolean isError=false;
    try 
    {
      /**
       * Take the Service and converts it to a byte array.
       */
      byte[] theByteArray=null;

      //Make the actual URL connection
      theURL = new URL(listenerURL+"?op=publish");
      uc=(HttpURLConnection) theURL.openConnection();
      uc.setUseCaches(false);
      uc.setDoOutput(true);  //becomes a POST
      uc.setRequestMethod("POST");
      uc.setRequestProperty("Content-Type", "application/octet-stream");
      if (serializedHolder!=null) 
      {
        uc.setRequestProperty("Content-Length", serializedHolder.length+"");
        os=uc.getOutputStream();
        bs=new BufferedOutputStream(os);
        bs.write(serializedHolder, 0, serializedHolder.length);
        bs.flush();
        //bs.close(); os.close();
      }

      // Get the response
      theByteArray=null;
      try 
      {
        uc.connect();
        inputStream = uc.getInputStream();
      } catch (Exception e) 
      {
        inputStream=uc.getErrorStream();
        isError=true;
        if (inputStream==null) 
        {
          uc.disconnect();
          throw new IOException(e.getMessage());
        }
      }
      bis=new BufferedInputStream(inputStream);
      bos=new ByteArrayOutputStream();
      byte[] b=new byte[250];
      while (true) 
      {
        int i=bis.read(b,0,250);
        if (i==-1) break;
        bos.write(b,0,i);
      }
      if (isError) 
      {
        return null;
      }
      else 
        return bos.toByteArray();
    } 
    finally 
    {
      if (bos !=null) bos.close();
      if (oos!=null) oos.close();
      if (os!=null) os.close();
      if (inputStream!=null) inputStream.close();
      if (bs!=null) bs.close();
      if (bis!=null) bis.close();
      if (uc !=null) uc.disconnect();
    }
  } // contactListeners
  
  /**
   * Takes an interface class and returns an Object that
   * is an implementation of the interface class.  The
   * interface is the Remote object that has a method
   * for each operation in the WSDL.  
   * @param proxyMap proxyMap encapsulates all the necessary fields needed to
   * dynamically invoke a service.  
   * @return Object that is an instanceof interfaceClass.
   */
  public static Object bind (ProxyMap proxyMap)
  throws IOException, SchemaException, WSDLException,
  RegistryException,Exception
  {
    /**
     * Try to find the WSDLHolder in the cache.
     * If not found, then read the WSDL from the
     * network or filesystem.
     */
    WSDLHolder wh=null;
    wh=(WSDLHolder)wsdlCache.get(proxyMap.getWSDLLocation());
    if (wh==null) 
    {
      byte[] wsdlByte = XMLFactory.getPayload(proxyMap.getWSDLLocation());
      wh = (WSDLHolder)XMLFactory.parse(wsdlByte, proxyMap.getWSDLLocation());
      if (wh!=null) 
      {
          synchronized (wsdlCache) 
          {
            wsdlCache.put(proxyMap.getWSDLLocation(),wh);
          }
      }
    }
    if (wh==null)
      throw new RegistryException("ERROR_REGISTRY_001:"+Constants.ERROR_REGISTRY_001);
      
    return java.lang.reflect.Proxy.newProxyInstance(
    proxyMap.getInterfaceClass().getClassLoader(),
    new Class[] {proxyMap.getInterfaceClass()},
    new ClientProxy(proxyMap,wh));
  }//bind(String, Class)
  
} /*class Registry*/

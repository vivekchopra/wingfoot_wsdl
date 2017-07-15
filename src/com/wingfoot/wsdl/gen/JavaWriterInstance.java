package com.wingfoot.wsdl.gen;
import java.io.*;
import java.net.*;
import java.util.*;
import com.wingfoot.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.wsdl.gen.*;
import com.wingfoot.wsdl.soap.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.gen.*;
import com.wingfoot.xml.schema.types.*;

public class JavaWriterInstance extends BaseWriter implements JavaWriter
{

  SchemaConverterInstance sci = null;
  
  public JavaWriterInstance()
  {
  } //constructor

  /**
   * Writes the complexTypes for all the schema in a wsdl.
   */
  private SerializedHolder[] schemaToJava(WSDLHolder wsdl, Options options)
  throws IOException, WSDLException, JavaHolderException, WSDLException
  {
    //The return serializedHolder.
    SerializedHolder[] serializedHolders = null;
    
    //Get all the imported SchemaHolders.  Create Beans if required.
    //Vector allSchemaHolders = gatherSchemaImports(wsdl);
    SchemaHolder[] shArray = wsdl.getAllType();
    if (shArray!=null)
    {
      sci = new SchemaConverterInstance(shArray, options); 
      /**
       * Generate beans if neccessary
       */
      serializedHolders = sci.toJava();
    }//if allSchemaHolders!=null
    else 
    {
      //Create a SchemaHolderInstance anyways.  This is possible because
      //if the WSDL only uses xsd types then there is no need to declare
      // a schema at all.
      sci = new SchemaConverterInstance(null, options);
    }
    return serializedHolders;
  }//schemaToJava

  /**
   * Writes the bind and interface classes for a portType.
   */
  private SerializedHolder[] portTypeToJava(WSDLHolder wsdl, Options options)
  throws WSDLException, IOException, JavaHolderException
  {
    Vector serializedHolders=new Vector();
    //PortType[] pt = wsdl.getPortType(); 
    PortType[] pt=wsdl.getAllPortType();
    for(int i = 0;pt!=null &&i< pt.length; i++)
    {
      if(isBindingSOAP(pt[i], wsdl))
      {
        generateBindClasses(pt[i], options, serializedHolders); //this will generate the Bind Classes
        generateInterfaceClasses(pt[i], options, serializedHolders, wsdl); //this will generate the Interface Classes
      }
    }//for portTypes

    /**
     * Turn the vector of SerializedHolders into an array of
     * Serialized Holders for return to client program.
     */
    SerializedHolder[] sh = new SerializedHolder[serializedHolders.size()];
    for(int j = 0; j < serializedHolders.size(); j ++)
      sh[j] = (SerializedHolder) serializedHolders.elementAt(j);
    return sh; 
  }//portTypeToJava
  
  /**
   * Takes a WSDLHolder
   * and returns back the Java representation.
   * The Java representation is returned as
   * a SerializedHolder array.  Each element
   * of the array is a SerializedHolder that
   * contains the byte[] representing the Java
   * class and the name of the file in the file
   * system to write to.
   * @param wsdl The wsdl to transform
   * @param options User encapsulated options
   * @return SerializedHolder[] this array contains all the java code
   * to be written to disk
   * @throws JavaHolderException
   * @throws IOException
   * @throws WSDLException
   */
  public SerializedHolder[] toJava(WSDLHolder wsdl, Options options)
    throws JavaHolderException, IOException, WSDLException
  {
    //The return serializedHolder.
    Vector serializedHolders = new Vector();

    //Process all the schemas, including the imported schemas.
    //Also store the return SerializedHolders into the Vector.
    SerializedHolder[] shArray = this.schemaToJava(wsdl, options);
    if (shArray!=null) 
    {
      for (int i=0; i<shArray.length; i++)
        serializedHolders.add(shArray[i]);
    }
    shArray=this.portTypeToJava(wsdl,options);
    if (shArray!=null) 
    {
      for (int i=0; i<shArray.length; i++)
        serializedHolders.add(shArray[i]);
    }

    /**
     * Turn the vector of SerializedHolders into an array of
     * Serialized Holders for return to client program.
     */
    SerializedHolder[] sh = new SerializedHolder[serializedHolders.size()];
    for(int j = 0; j < serializedHolders.size(); j ++)
      sh[j] = (SerializedHolder) serializedHolders.elementAt(j);
    return sh; 
  }//toJava

  
  /**
   * Takes a wsdl and generates Java Classes.
   * These java classes are client classes for accessing a SOAP Server.
   * The return is a vector of SerializedHolder which encapsulates
   * the Java Code to be written to disk, but not in this method.
   * @param wsdl A WSDLHolder for which to serialize into Java code.  Each
   * PortType is serialized into Java code
   * @param options The user entered options encapsulated in the Options object
   * @return Vector of SerializedHolders ready to be wriiten to disk.
   */
  private Vector generateBindClasses(PortType portType, Options options, Vector serializedHolders)
    throws JavaHolderException, IOException, WSDLException
  {
    if(serializedHolders == null)
      serializedHolders = new Vector();
    
    if(portType != null) 
    {
      String namespace = portType.getName().getNamespaceURI();
      String portTypeInterfaceName=options.getPackageMapping(namespace)==null?
      portType.getName().getLocalPart():options.getPackageMapping(namespace)+"."+portType.getName().getLocalPart();
      JavaHolder javaHolder = new JavaHolder(portType.getName().getLocalPart()+"Bind",
       JavaHolder.REGULAR_CLASS,
       options.getPackageMapping(namespace) != null ? options.getPackageMapping(namespace) : null);
					     
      JavaHolder.Method javaMethod = javaHolder.new Method("bind", portTypeInterfaceName,
      new String[] {"java.lang.Exception"});
      javaMethod.setIsStatic(true);

      //create a ProxyMap to encapsulate the typemapping registry amongst a few other things
      javaMethod.setStatement("com.wingfoot.registry.ProxyMap pm = new com.wingfoot.registry.ProxyMap()");
      //Write the debug statement.
      javaMethod.setStatement("pm.setDisplayPayload(false)");
      /**
       * Create the typeMapping statement if applicable.
       */
      javaMethod.setStatement("com.wingfoot.soap.encoding.TypeMappingRegistry tmr = new com.wingfoot.soap.encoding.TypeMappingRegistry()");
      StringBuffer sb = new StringBuffer("tmr.mapPortType(");
      sb.append("Class.forName(\"").append(portTypeInterfaceName).append("\"),");
      sb.append("new com.wingfoot.QName(\"").append(portType.getName().getNamespaceURI()).append("\",");
      sb.append("\"").append(portType.getName().getLocalPart()).append("\"))");
      javaMethod.setStatement(sb.toString());

      //Map the package from the options file.
      if (options.getPackageMapping()!=null) 
      {
        Hashtable ht=options.getPackageMapping();
        Enumeration enum=ht.keys();
        while (enum.hasMoreElements()) 
        {
          String aKey=(String)enum.nextElement();
          String aValue=(String)ht.get(aKey);
          sb=new StringBuffer("tmr.mapPackage(");
          sb.append("\"").append(aKey).append("\", \"").append(aValue).append("\")");
          javaMethod.setStatement(sb.toString());
        }
      }
      if (sci.getTypeMap()!=null) 
      {
        Vector v = sci.getTypeMap();
        for (int i=0;i<v.size();i++) 
        {
          String[] s =(String[])v.elementAt(i); 
          sb=new StringBuffer();
          sb.append("tmr.mapTypes(");
          sb.append("\"").append(s[0]).append("\",");
          sb.append("\"").append(s[1]).append("\",");
          sb.append("Class.forName(").append("\"").append(s[2]).append("\"),");
          sb.append("Class.forName(").append("\"").append("com.wingfoot.soap.encoding.ComplexTypeSerializer").append("\"),");
          sb.append("Class.forName(").append("\"").append("com.wingfoot.soap.encoding.ComplexTypeDeserializer").append("\")");
          sb.append(")");
          javaMethod.setStatement(sb.toString());
        }//for
      }//if
      javaMethod.setStatement("pm.setTypeMappingRegistry(tmr)"); //set the typemapping registry if there is one
      if(options.getUseSession()) //set the useSession in the ProxyMap
        javaMethod.setStatement("pm.setUseSession(true)");
      if(options.getAlternateTransport() != null) {
        javaMethod.setStatement("java.lang.Class tClass = Class.forName(\""+options.getAlternateTransport()+"\")");
        javaMethod.setStatement("pm.setTransport(tClass)");
      }
      javaMethod.setStatement("pm.setWSDLLocation(\""+options.getWSDLLocation()+"\")");
      javaMethod.setStatement("pm.setInterfaceClass("+portTypeInterfaceName+".class)");
      //javaMethod.setStatement("return ("+portTypeInterfaceName+") com.wingfoot.registry.Registry.bind(\""+options.getWSDLLocation()+"\", "+portTypeInterfaceName+".class"+", tmr)");
      javaMethod.setStatement("return ("+portTypeInterfaceName+") com.wingfoot.registry.Registry.bind(pm)");
      javaHolder.setMethod(javaMethod);
      serializedHolders.add(javaHolder.toJava());
    } //if
    return serializedHolders;
  }


  /**
   * Takes a wsdl and generates an interface class.
   * Each portType has an interface class.  Each operation
   * becomes a method within the interface class.
   * @param portType The PortType to create the interface class for
   * @param options User encapsulated options
   * @param serializedHolders The ever growing vector of SerializedHolders
   * @param wsdl The WSDLHolder
   * @return Vector A vector of serializedHolders generated
   * @throws WSDLException A WSDLException is thrown if an error occurs
   */
  private Vector generateInterfaceClasses(PortType portType, Options options, Vector serializedHolders, WSDLHolder wsdl)
  throws WSDLException, JavaHolderException, IOException
  {
    Operation[] operation = portType.getOperation();
    
    if(serializedHolders == null)
      serializedHolders =  new Vector();
    
    if(operation != null)
    {
      String namespace = portType.getName().getNamespaceURI();
      JavaHolder javaHolder = new JavaHolder(portType.getName().getLocalPart(),
			JavaHolder.INTERFACE,
			options.getPackageMapping(namespace) != null ? options.getPackageMapping(namespace) : null);
      for(int i = 0; i < operation.length; i++)
      {
          if(operationExistsInBinding(operation[i], wsdl.getAllBinding()))
          {
            JavaHolder.Method javaMethod = javaHolder.new Method(operation[i].getName(),
            determineReturnType(getMessageParts(operation[i].getOutputMessage(), wsdl, sci)),
            null);
            javaHolder.setMethod(javaMethod);
            Vector input = getMessageParts(operation[i].getInputMessage(), wsdl, sci);
            for (int j = 0; input != null&& j <input.size(); j++)
            {
              String[] tmpArray = (String[]) input.elementAt(j);
              javaMethod.setMethodParameter(tmpArray[1], tmpArray[0]);     
            }//for
          } //if
      } //for
      serializedHolders.add(javaHolder.toJava());
    } //if
    return serializedHolders;
  } //generateInterfaceClasses


  /**
   * Determines the return type.
   * If there are more than one part to the returnType, return an Envelope Type
   * This method creates the neccessary beans if needed and put them into the serializedHolders
   * @param partTypes This vector contains the parts returned from the getMessageParts() method.
   * Each element of the vector contains a String[] with string[0] being the return type and string[1] being
   * the name.
   * @return String The return type for this method
   */
  private String determineReturnType(Vector partTypes)
  {
    String returnType = null;
    if(partTypes == null || partTypes.size() == 0)
      returnType = "void";
    else if(partTypes.size() > 1)
      returnType = "com.wingfoot.soap.Envelope";
    else 
    {
      String[] tmpArray = (String[]) partTypes.elementAt(0);
      returnType = tmpArray[0];
    }
    return returnType;
  } //determineReturnType
  

  
  /**
   * This private method returns the PortType namespace
   * @param portType The port type to extract the namespace from
   * @return String the string representation of the namespace
   */
  private String getPortTypeNamespace(PortType portType)
  {
    if(portType != null)
      return portType.getName().getNamespaceURI();
    else
      return null;
  }

  /**
   * Generates java classes for imported wsdls.  If the parent
   * wsdl contains imports, this method loops through them and call the generation
   * methods to create java code.  The return is a vector of serialized objects
   * @param wsdl the parent wsdl file
   * @param serializedHolders the vector for holding all the SerializedHolder objects
   * @param options user options encapsulated in the Options object
   * @return Vector A Vector containing the SerializedHolders
   * @throws WSDLException A WSDLException is thrown if an error occurs
   * @throws IOException An input output exception is thrown if that type of error occurs
   * @throws JavaHolderException A Java Holder Exception is thrown if that type of error occurs
   */
  private Vector generateJavaForImportedWsdl(WSDLHolder wsdl, Options options)
    throws WSDLException, IOException, JavaHolderException
  {
    Vector serializedHolderVector=null;
    Vector imports = wsdl.getWsdlImport();

    if(imports != null)
    {
        for(int i = 0; i < imports.size(); i ++)
        {
          if(imports.elementAt(i) instanceof WSDLHolder)
          {
            WSDLHolder importWSDL = (WSDLHolder) imports.elementAt(i);
            SerializedHolder[] shArray=this.portTypeToJava(importWSDL, options);
            if (shArray!=null) 
            {
              if (serializedHolderVector==null) serializedHolderVector=new Vector();
              for (int j=0; j<shArray.length; j++)
                serializedHolderVector.add(shArray[j]);
            }//if
          } //if
        } //for
    } //if imports!=null
    return serializedHolderVector;
  } //generateJavaForImportedWsdl
} //JavaWriterInstance

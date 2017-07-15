package com.wingfoot.wsdl;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.*;
import com.wingfoot.xml.schema.groups.*;
import com.wingfoot.*;
import java.util.Vector;
import java.util.*;
import java.io.*;
import org.kxml.io.*;

/**
 * Encapsulates a well formed WSDL.  
 * <p>
 * A WSDL has many components.  The getXXX and setXXX
 * methods allow for addition of components from the
 * WSDL.
 */
public class WSDLHolder implements XMLHolder
{
  private  String targetNamespace;
  private Vector schema;
  private Vector message;
  private Vector portType;
  private Vector binding;
  private Vector service, wsdlImport;
  private Vector attributes;
  private String defaultNamespace;
  private Hashtable namespace;
  private String name;
  private String destination;

  /**
   * Creates a WSDL.  
   * @param destination String that contains the
   * file name or the URL where the WSDL is to be
   * written.
   */
  public WSDLHolder()
  {
  }

  /**
   * A WSDL may import other WSDLs.  Such import statement must be 
   * the first statement after &ltdefinitions&gt element
   * @param importedHolder WSDLHolder that contains encapsulates
   * the imported WSDL
   * @param location String with the location of the WSDL.  This
   * could be a URL on the web or a file in the local file system.
   * @throws WSDLException if the importedHolder does not have a 
   * destination specified.
   */
  public void setWsdlImport(XMLHolder importedHolder) throws WSDLException
  {
    if (importedHolder==null ||
        importedHolder.getDestination()==null)
          throw new WSDLException("ERROR_WSDL_027:" + Constants.ERROR_WSDL_027);
    if (wsdlImport==null)
      wsdlImport=new Vector();
    wsdlImport.add(importedHolder);
  }
  
  /**
   * Returns a Vector of imported WSDLs.
   * Each element of the Vector is a XMLHolder
   * @return Vector or null if no import has 
   * been set.
   */
  public Vector getWsdlImport() 
  {
    return wsdlImport;
  }
  
  /**
   * Retrieves the targetNamespace for the WSDL.
   * @return String the targetNamespace; null if none is specified.
   */
  public String getTargetNamespace()
  {
    return targetNamespace;
  }

  /**
   * Sets the targetNamespace for the WSDL.
   * @param String the targetNamespace.
   */
  public void setTargetNamespace(String targetNamespace)
  {
    this.targetNamespace=targetNamespace;
  }


  /**
   * Returns the SchemaHolder(s) for the WSDL.
   * Each SchemaHolder translates to one &lt;schema&gt;
   * element.
   * @return array of SchemaHolder; null if SchemaHolder
   * is not set.
   */
  public SchemaHolder[] getType()
  {
    if (schema==null)
      return null;
    SchemaHolder[] sh = new SchemaHolder[schema.size()];
    for (int i=0; i<schema.size(); i++) 
    {
      sh[i]=(SchemaHolder)schema.elementAt(i);
    }
    return sh;
  }

  /**
   * Retrieves all the SchemaHolder including
   * the imported SchemaHolder and SchemaHolder
   * inside imported WSDL.
   */
  public SchemaHolder[] getAllType() 
  {
    Vector v=new Vector();
    for (int i=0; schema!=null&&i<schema.size(); i++) 
    {
      v.add(schema.elementAt(i));
    }
    //Now for imports.
    if (this.wsdlImport!=null) 
    {
      for (int i=0; i<wsdlImport.size(); i++) 
      {
        if (wsdlImport.elementAt(i) instanceof WSDLHolder) 
        {
          SchemaHolder[] vw=((WSDLHolder)wsdlImport.elementAt(i)).getAllType();
          for (int j=0; vw!=null&&j<vw.length; j++) 
          {
            v.add(vw[j]);
          }
        }//wsdlholder
        else if (wsdlImport.elementAt(i) instanceof SchemaHolder) 
        {
            v.add(wsdlImport.elementAt(i));
        }//schemaholder
      }//for
    }//if wsdlImport!=null
    //Convert Vector to SchemaHolder
    if (v.size()==0)
      return null;
    SchemaHolder[] returnArray=new SchemaHolder[v.size()];
    for (int i=0; i<v.size(); i++)
      returnArray[i]=(SchemaHolder)v.elementAt(i);
    return returnArray;
  }//getAllType

  /**
   * Returns a SchemaHolder that has the same
   * targetNamespace as the input parameter.
   * @param targetNamespace the namespace that
   * should exist in the return SchemaHolder
   * @return SchemaHolder the SchemaHolder that
   * has the same namespace as the input namespace;
   * null if no such SchemaHolder exists.
   */
  public SchemaHolder getType(String targetNamespace) 
  {
    if (schema==null)
      return null;
    for (int i=0; i<schema.size(); i++) 
    {
      SchemaHolder sh = (SchemaHolder) schema.elementAt(i);
      if (sh.getTargetNamespace().equals(targetNamespace))
        return sh;
    }//for
    return null;
  }//getType(String)

  /**
   * Sets the schema for the WSDL.  The schema is defined
   * within the &lttypes&gt element in the WSDL.  There can
   * be multiple &ltschema&gt in the &lttype&gt.
   * @param Array of SchemaHolder. 
   */
  public void setType(SchemaHolder[] newSchema)
  {
    if (newSchema==null)
      schema=null;
    else 
    {
      schema=new Vector();
      for (int i=0; i<newSchema.length; i++) 
        schema.add(newSchema[i]);
    }
  }

  public void setType(SchemaHolder newSchema) 
  {
      if (schema==null)
        schema=new Vector();
      schema.add(newSchema);
  }

  /**
   * Gets the Message(s) from the WSDL.  There can be multiple
   * Message(s) in the WSDL.
   * @return an array of Message(s); null if none is set.
   */
  public Message[] getMessage()
  {
    if (message==null)
      return null;
    Message[] m = new Message[message.size()];
    for (int i=0; i<message.size(); i++)
      m[i]=(Message) message.elementAt(i);
    return m;
  }

  /**
   * Retrives a Message with a give QName.  This
   * method is able to search the WSDL imports 
   * iteratively to find the message.
   * @param name QName of the message to be retrieved.
   * @return Message the message that matches the QName;
   * null if the message with the QName does not exist.
   */
  public Message getMessage(QName name) 
  {
    for (int i=0; message!=null &&i<message.size(); i++) 
    {
      Message m = (Message) message.elementAt(i);
      QName mName=m.getName();
      if (mName.toString().equals(name.toString()))
        return m;
    }
    //Nothing has been found in this WSDLHolder.  It
    //is possible that there is an import that has the
    //message.  Check the import
    if (this.wsdlImport!=null) 
    {
      for (int i=0; i<wsdlImport.size(); i++)
      {
        if (! (wsdlImport.elementAt(i) instanceof WSDLHolder))
          continue;
        WSDLHolder tempHolder=(WSDLHolder) wsdlImport.elementAt(i);
        Message m = tempHolder.getMessage(name);
        if (m!=null)
          return m;
      } //for
    } //if
    return null;
  }
  /**
   * Sets the Message(s) for the WSDL.  There can be multiple
   * Message(s) in the WSDL.
   * @param an array of Message(s); null if none is set.
   */
  public void setMessage(Message[] newMessage)
  {
    if (newMessage==null)
      message=null;
    else 
    {
       message = new Vector();
       for (int i=0; i<newMessage.length; i++)
        message.add(newMessage[i]);
    }
  }

  public void setMessage(Message newMessage) 
  {
    if (message==null)
      message = new Vector();
    message.add(newMessage);
  }

  /**
   * Retrieves the PortType(s) for the WSDL.  There
   * may be multiple PortType(s) for the WSDL.
   * @return array of PortType.
   */
  public PortType[] getPortType()
  {
    if (portType==null)
      return null;
    PortType pt[] = new PortType[portType.size()];
    for (int i=0; i<portType.size(); i++) 
    {
      pt[i]=(PortType) portType.elementAt(i);
    }
    return pt;
  }

  /**
   * Returns the PortType in the WSDL.  If the WSDL
   * has imported WSDL and that WSDL has PortType, 
   * then the WSDL is recursed until all the porttypes
   * are returned.
   * @return PortType array of PortTypes.
   */
  public PortType[] getAllPortType()
  {
    Vector v = new Vector();
    for (int i=0; portType!=null && i<portType.size(); i++)
      v.add(portType.elementAt(i));
    if (this.wsdlImport!=null)
    {
      for (int i=0; i<wsdlImport.size(); i++)
      {
        if (wsdlImport.elementAt(i) instanceof WSDLHolder)
        {
          PortType pt[] = ((WSDLHolder)wsdlImport.elementAt(i)).getAllPortType();
          for (int j=0; pt!=null&&j<pt.length; j++)
            v.add(pt[j]);
        }
      }//for
    }//if
    if (v.size()==0)
      return null;
    PortType[] ret=new PortType[v.size()];
    for (int i=0; i<v.size(); i++)
      ret[i]=(PortType)v.elementAt(i);
    return ret;
  }//getAllPortType

  public PortType getPortType(QName qname) 
  {

    for (int i=0; portType!=null &&i<portType.size(); i++) 
    {
      PortType p = (PortType) portType.elementAt(i);
      if (p.getName().toString().equals(qname.toString()))
        return p;
    }
    //Nothing has been found in this WSDLHolder.  It
    //is possible that there is an import that has the
    //portType.  Check the import
    if (this.wsdlImport!=null) 
    {
      for (int i=0; i<wsdlImport.size(); i++)
      {
        if (! (wsdlImport.elementAt(i) instanceof WSDLHolder))
          continue;
        WSDLHolder tempHolder=(WSDLHolder) wsdlImport.elementAt(i);
        PortType p= tempHolder.getPortType(qname);
        if (p!=null)
          return p;
      } //for
    } //if
    return null;
  }

   /**
   * Sets the PortType(s) for the WSDL.  There
   * may be multiple PortType(s) for the WSDL.
   * @param array of PortType.
   */
  public void setPortType(PortType[] newPortType)
  {
    if (newPortType==null)
      portType=null;
    else 
    {
      portType=new Vector();
      for (int i=0; i<newPortType.length; i++)
        portType.add(newPortType[i]);
    }
  }

  public void setPortType(PortType newPortType) 
  {
    if (portType==null)
      portType=new Vector();
    portType.add(newPortType);
  }

  /**
   * Retrieves a Binding that has a the input
   * PortType as its type attribute.
   * @param portType the PortType that is bound
   * the the retrieved binding.
   * @return the Binding that is bound to the input
   * PortType; null if there is no Binding with the
   * given PortType
   */
   public Binding getBinding(PortType portType) 
   {
     for (int i=0; binding!=null&&i<binding.size(); i++) 
     {
       if (((Binding)binding.elementAt(i)).getPortType().equals(portType))
        return ((Binding)binding.elementAt(i));
     }//for
     //Not found.  Perhaps it is in the import.
     if (this.wsdlImport!=null) 
     {
       for (int i=0; i<wsdlImport.size(); i++) 
       {
         if (wsdlImport.elementAt(i) instanceof WSDLHolder) 
         {
           Binding b = ((WSDLHolder)wsdlImport.elementAt(i)).getBinding(portType);
           if (b!=null)
            return b;
         }//if
       }//for
     }//if
     return null;
   }//getBinding
   
  /**
   * Retrieves the Binding(s) for the WSDL.  There
   * may be multiple Binding(s) for the WSDL.
   * @return array of Binding.
   */
  public Binding[] getBinding()
  {
    if (binding==null)
      return null;
    Binding b[] = new Binding[binding.size()];
    for (int i=0; i<binding.size(); i++)
      b[i]= (Binding) binding.elementAt(i);
    return b;
  }

  public Binding[] getAllBinding()
  {
    Vector v = new Vector();
    Binding[] bArray=getBinding();
    for (int i=0; bArray!=null && i<bArray.length; i++)
      v.add(bArray[i]);
    if (this.wsdlImport!=null)
    {
      for (int i=0; i<this.wsdlImport.size(); i++) 
      {
        if (wsdlImport.elementAt(i) instanceof WSDLHolder)
        {
          Binding iArray[] = ((WSDLHolder)wsdlImport.elementAt(i)).getBinding();
          if (iArray!=null && iArray.length>0)
          {
            for (int j=0; j<iArray.length; j++)
              v.add(iArray[j]);
          }
        }
      }
    }
    if (v.size()==0)
      return null;
    Binding ret[] = new Binding[v.size()];
    for (int i=0; i<v.size(); i++)
      ret[i]=(Binding)v.elementAt(i);
    return ret;
  }
  /**
   * Sets the Binding(s) for the WSDL.  There
   * may be multiple Binding(s) for the WSDL.
   * @param array of Binding.
   */
  public void setBinding(Binding[] newBinding)
  {
    if (newBinding==null)
      binding=null;
    binding=new Vector();
    for (int i=0; i<newBinding.length; i++)
      binding.add(newBinding[i]);
  }

  public void setBinding(Binding newBinding) 
  {
    if (binding==null)
      binding=new Vector();
    binding.add(newBinding);
  }

  /**
   * Retrieves a WSDL &lt;port&gt; that is 
   * bound to a &lt;binding&gt;
   * @param binding the Binding for which a 
   * &lt;port&gt; is desired.
   * @return the Port; null if no Port is 
   * found that is bound to the Binding.
   */
   public Port getPort(Binding binding) 
   {
     if (this.getService()!=null) 
     {
       Service sArray[] = this.getService();
       for (int i=0; i<sArray.length; i++) 
       {
         Port p=sArray[i].getPort(binding.getBindingName());
         if (p!=null)
          return p;
       }
     }//if
     //Might be in the import.
     if (this.wsdlImport!=null) 
     {
       for (int i=0; i<wsdlImport.size(); i++) 
       {
         if (wsdlImport.elementAt(i) instanceof WSDLHolder) 
         {
           Port p=((WSDLHolder)wsdlImport.elementAt(i)).getPort(binding);
           if (p!=null)
            return p;
         }//if
       }//for
     }//if
     return null;
   }//getPort
  /**
   * Retrieves the Service(s) for the WSDL.  There
   * may be multiple Service(s) for the WSDL.
   * @return array of Service.
   */
  public Service[] getService()
  {
    if (service==null)
      return null;
    Service[] s = new Service[service.size()];
    for (int i=0; i<service.size(); i++)
      s[i]=(Service) service.elementAt(i);
    return s;
  }

  /**
   * Sets the Service(s) for the WSDL.  A WSDL
   * may have multiple &ltservice&gt element.
   * <p>
   * A services groups a set of related Port(s) together.
   * @param newService an array of Service.  Any existing
   * Service object defined is replaced by the Service(s) in
   * the Service array.
   */
  public void setService(Service[] newService)
  {
    if (newService==null)
      service=null;
    else 
    {
      service=new Vector();
      for (int i=0; i<newService.length; i++)
        service.add(newService[i]);
    }
  }

  /**
   * Adds a service to the WSDL.
   * @param newService a Service to add
   * to the WSDL.  Service appear as
   * &ltservice&gt element.
   */
  public void setService(Service newService) 
  {
    if (service==null)
      service=new Vector();
    service.add(newService);
  }

  public Vector getAttributes()
  {
    return attributes;
  }

  /**
   * Sets the attributes that appear in the &ltdefinitions&gt
   * element.  This is a Vector of org.kxml.Attributes.
   */
  public void setAttributes(Vector newAttributes)
  {
    attributes = newAttributes;
  }

  /**
   * Retrieves the default namespace for the WSDL.
   * @return String the default namespace
   */
  private String getDefaultNamespace()
  {
    return defaultNamespace;
  }

  /**
   * Sets the default namespace for the WSDL.
   * @param newDefaultNamespace the default namespace
   * for WSDL
   */
  private void setDefaultNamespace(String newDefaultNamespace)
  {
    defaultNamespace = newDefaultNamespace;
  }

  /**
   * Returns a hashtable of namespaces that appear
   * in the &ltdefinitions&gt element in the WSDL.
   * The key is the prefix and the value is the 
   * namespace associated to the prefix.
   * @return Hashtable of namespaes.
   */
  public Hashtable getNamespace()
  {
    return namespace;
  }

  /**
   * Sets the namespace that appears in the
   * &ltdefinitions&gt element in the WSDL.  
   * @param prefix String the prefix that has to
   * be associated to this namespace.
   * @param namespace String the namespace associated
   * to the namespace.
   * @throws Exception if the prefix is duplicated.
   */
  public void setNamespace(String prefix, String namespace) throws Exception
  {
    if (this.namespace==null)
      this.namespace=new Hashtable();
    this.namespace.put(prefix, namespace);
  }

  /**
   * Retrieves the name of the WSDL.
   * @return String the name of the WSDL.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Associates a name to a WSDL.  It appears
   * as a name attribute in the &ltdefinitions&gt
   * element.  The name is optional.
   * @param newName String with the name of the WSDL.
   */
  public void setName(String newName)
  {
    name = newName;
  }

  /**
   * Converts the instance of WSDLHolder to WSDL (XML form).
   * @return SerializedHolder[] an array of SerializedHolder
   * Each element of the array represents a well formed XML
   * document.
   * @throws IOException if an error occurs while writing
   * the XML.
   * @throws XMLException if any error occurs while processing
   * the WSDL or the schema.
   */
  public SerializedHolder[] toXML() throws IOException, XMLException
  {
    Vector htOfHolders=new Vector();
    if (getDestination()==null)
      throw new WSDLException("ERROR_WSDL_027:"+ Constants.ERROR_WSDL_027);
      
    int namespaceCounter=1;
    Hashtable defaultNS=getNamespace();
    if (defaultNS==null)
    {
      //Set your own Namespace;
      defaultNS=new Hashtable();
      defaultNS.put(Constants.WSDL_SOAP_NAMESPACE, "soap");
      defaultNS.put(Constants.WSDL_NAMESPACE, "wsdl");
      defaultNS.put(Constants.SOAP_SCHEMA, "xsd");
      defaultNS.put(Constants.SOAP_SCHEMA_INSTANCE, "xsi");
      if (getTargetNamespace()!=null)
        defaultNS.put(getTargetNamespace(),"tns");
      defaultNS.put(Constants.SOAP_ENCODING_STYLE, "SOAP-ENC");
      //Put the targetNamespace of the schema in <definition>
      //This is just for ease of WSDL use.
      if (getType()!=null) 
      {
        SchemaHolder sh[] = getType();
        for (int i=0; i<sh.length; i++) 
        {
          if (sh[i].getTargetNamespace()!=null &&
          !(defaultNS.containsKey(sh[i].getTargetNamespace())))
            defaultNS.put(sh[i].getTargetNamespace(), "wpNS"+namespaceCounter++);
        } //for
      } //if

      /** Put all the namespaces that appears in the imports here. **/
      if (this.getWsdlImport()!=null) 
      {
          Vector importedNamespaces= getImportedNamespaces(getWsdlImport());
          for (int i=0; importedNamespaces!=null&&i<importedNamespaces.size(); i++) 
          {
            String aImportedNS=(String) importedNamespaces.elementAt(i);
            if (aImportedNS!=null &&
            !(defaultNS.containsKey(aImportedNS)))
              defaultNS.put(aImportedNS, "wpNS"+namespaceCounter++);
          } //for
      }//if this.getWsdlImport()!=null
    } //if defaultNS!=null

    XMLWriter writer = new XMLWriter(defaultNS);
    writer.startElement("definitions", Constants.WSDL_NAMESPACE);
    if (getName()!=null)
      writer.attribute("name", getName());
    if (getTargetNamespace()!=null)
      writer.attribute("targetNamespace",getTargetNamespace());
    
    //Write the namespaces
    Enumeration enum = defaultNS.keys();
    while (enum.hasMoreElements()) 
    {
      String aKey = (String) enum.nextElement();
      String aValue = (String) defaultNS.get(aKey);
      writer.attribute("xmlns:"+aValue, aKey);
    }

    /**
     * Write the import statements immediately below <definitions>
     */
    if (this.wsdlImport!=null) 
    {
      for (int i=0; i<wsdlImport.size(); i++) 
      {
        String shLocation=null, shTargetNamespace=null;
        Vector additionalNS=null;
        shLocation=((XMLHolder)wsdlImport.elementAt(i)).getDestination();
        shTargetNamespace=((XMLHolder)wsdlImport.elementAt(i)).getTargetNamespace();
        if (wsdlImport.elementAt(i) instanceof WSDLHolder)
          additionalNS=this.getImportedNamespaces(((WSDLHolder)wsdlImport.elementAt(i)).getWsdlImport());

        if (shLocation!=null && shTargetNamespace!=null) 
        {
          writer.startElement("import", Constants.WSDL_NAMESPACE);
          writer.attribute("namespace", shTargetNamespace);
          writer.attribute("location", shLocation);
          writer.endTag();
          if (additionalNS!=null) 
          {
            for (int j=0; j<additionalNS.size(); j++) 
            {
              writer.startElement("import", Constants.WSDL_NAMESPACE);
              writer.attribute("namespace", (String)additionalNS.elementAt(j));
              writer.attribute("location", shLocation);
              writer.endTag();
            } //for
          } //if
        } //if
      } //for
    } //if wsdlImport!=null
     
    /**
     * Convert the schemaHolders
     */
     convertSchemaToWSDL(getType(), writer);
    
    /**
     * Convert Message to WSDL.
     */
    convertMessageToWSDL(getMessage(), writer);

    /**
     * Convert PortType to WSDL.
     */
    convertPortTypeToWSDL(getPortType(), writer);

    /**
     * Convert the Binding to WSDL.
     */
    convertBindingToWSDL(getBinding(), writer);

    /**
     * Convert the Service to WSDL
     */
     convertServiceToWSDL(getService(), writer);

    //End the definition
    writer.endTag();
    //return writer.getPayload("utf-8");

    //Write the WSDL to the file.
    htOfHolders.add(new SerializedHolder(writer.getPayload("utf-8"), this.getDestination()));

    //Write the imports to the file
    if (this.wsdlImport!=null) 
    {
      for (int i=0; i<wsdlImport.size(); i++) 
      {
        SerializedHolder[] sh = XMLFactory.toXML((XMLHolder) wsdlImport.elementAt(i));
        for (int j=0; sh!=null && j<sh.length; j++)
          htOfHolders.add(sh[j]);
      } //for
    } //if
    
    //Convert htOfHolders to an array and return
    SerializedHolder[] shArray = new SerializedHolder[htOfHolders.size()];
    for (int i=0; i<htOfHolders.size(); i++)
      shArray[i]=(SerializedHolder) htOfHolders.elementAt(i);
    return shArray;
  } //toWSDL

  /**
   * Chases down the wsdlImports to determine all the targetNamespaces
   * that are used. These targetNamespaes are declared in the parent
   * using the import verb.
   */
  private Vector getImportedNamespaces (Vector wsdlImports) 
  {
    if (wsdlImports==null)
      return null;
    Vector v = new Vector();
    for (int i=0; i<wsdlImports.size(); i++) 
    {
      Object o = wsdlImports.elementAt(i);
      if (o instanceof SchemaHolder)
        v.add(((SchemaHolder) o).getTargetNamespace());
      else 
      {
        //WSDLHolder
        WSDLHolder importWH=(WSDLHolder) o;
        v.add(importWH.getTargetNamespace());
        if (importWH.getWsdlImport()!=null) 
        {
          Vector returnVector=getImportedNamespaces(importWH.getWsdlImport());
          for (int j=0; j<returnVector.size(); j++)
            v.add(returnVector.elementAt(j));
        }
      } //else
    } //for
    return v;
  } //getImportedNamespaces
  
  /**
   * Convert the Schema to WSDL.
   * @param schemaArray array of SchemaHolder to convert
   * @param writer instance of XMLWriter.
   * @throws IOException if an error while writing the
   * WSDL
   */
   private void convertSchemaToWSDL(SchemaHolder[] schemaArray,
   XMLWriter writer) throws IOException 
   {
      if (schemaArray==null)
        return;
      writer.startElement("types", Constants.WSDL_NAMESPACE);
      for (int i=0; i<schemaArray.length; i++) 
      {
        schemaArray[i].toXML(writer);
      }
      writer.endTag();
   }
  
  /**
   * Convert the Messages to WSDL.
   * @param messageArray array of Message to convert
   * @param writer instance of XMLWriter.
   * @throws IOException if an error while writing the
   * WSDL
   */
  private void convertMessageToWSDL(Message[] messageArray,
  XMLWriter writer) throws IOException 
  {
    if (messageArray==null)
      return;
    for (int i=0; i<messageArray.length; i++)
      messageArray[i].toWSDL(writer);
  } //convertMessageToWSDL

  
  /**
   * Convert the PortType to WSDL.
   * @param portTypeArray array of PortType to convert
   * @param writer instance of XMLWriter.
   * @throws IOException if an error while writing the
   * WSDL
   */
  private void convertPortTypeToWSDL(PortType[] portTypeArray,
  XMLWriter writer) throws IOException 
  {
    if (portTypeArray==null)
      return;
    for (int i=0; i<portTypeArray.length; i++) 
      portTypeArray[i].toWSDL(writer);
  } //convertPortTypeToWSDL

  /**
   * Convert the Binding to WSDL.
   * @param bindingArray array of Binding to convert
   * @param writer instance of XMLWriter.
   * @throws IOException if an error while writing the
   * WSDL
   */
  private void convertBindingToWSDL(Binding[] bindingArray,
  XMLWriter writer) throws IOException 
  {
    if (bindingArray==null)
      return;
    for (int i=0; i<bindingArray.length; i++)
      bindingArray[i].toWSDL(writer);
  } //convertBindingToWSDL

    /**
   * Convert the Service to WSDL.
   * @param serviceArrray array of Service to convert
   * @param writer instance of XMLWriter.
   * @throws IOException if an error while writing the
   * WSDL
   */
  private void convertServiceToWSDL(Service[] serviceArray,
  XMLWriter writer) throws IOException 
  {
    if (serviceArray==null)
      return;
    for (int i=0; i<serviceArray.length; i++)
      serviceArray[i].toWSDL(writer);
  } //convertBindingToWSDL

  /**
   * Retrieves the destination of the WSDL.
   * @return String the name of the resource
   * where the WSDL is to be written.
   */
  public String getDestination()
  {
    return destination;
  }

  /**
   * Sepcifies the name of the resource where the
   * WSDL is to be written.  The resource could be
   * a file in the local file system or a URL on 
   * a HTTP web server.
   * @param newDestination a resource where the WSDL
   * is to be written.
   */
  public void setDestination(String newDestination)
  {
    destination = newDestination;
  }

  /**
   * Peruses through the Schema in the WSDL (including imported
   * Schemas and the Schemas in imported WSDL and determines
   * if an Element with the input namespace and name is present.
   * @param elementNamespace the namespace of the desired element.
   * @param elementName the name of the element.
   * @return Element the Element with the namespace and name; null
   * if no match is found.
   */
  public Element getElement(String elementNamespace, String elementName) 
  {
    if (this.getType()!=null && this.getType().length>0)
    {
      SchemaHolder[] shArray=this.getType();
      for (int i=0; i<shArray.length;i++) 
      {
        Element e=shArray[i].getElement(elementName,elementNamespace);
        if (e!=null)
          return e;
      }//for
    }//there is a schema
    //No element found. It could be in one of the imports.
    if (this.getWsdlImport()!=null) 
    {
      Element e=null;
      Vector v = this.getWsdlImport();
      for (int i=0; i<v.size(); i++) 
      {
        if (v.elementAt(i) instanceof WSDLHolder)
          e=((WSDLHolder)v.elementAt(i)).getElement(elementNamespace,elementName);
        else if (v.elementAt(i) instanceof SchemaHolder)
          e=((SchemaHolder)v.elementAt(i)).getElement(elementName,elementNamespace);
        if (e!=null)
          return e;
      }//for
    }//there is an import
    return null;
  }

     /**
    * Given a ModelGroupDefinition (a Component) that is a reference
    * to another to another ModelGroupDefinition, retrieves the 
    * ModelGroupDefinition that is being referred to.
    * <p>
    * If the mgd is not a reference then mgd
    * is returned back.
    * <p>
    * If mgd is null or if the reference to the element
    * is not present in this SchemaHolder, null is returned.
    * <p>
    * If the referenced ModelGroupDefinition is present in this SchemaHolder
    * then the referenced ModelGroupDefinition is returned.
    * @param mgd ModelGroupDefinition that has a ref attribute in it.
    * @return ModelGroupDefinition the referenced ModelGroupDefinition.
    */
    public ModelGroupDefinition getNormalizedModelGroupDefinition(ModelGroupDefinition mgd) 
    {
      if (mgd==null)
          return null;
      SchemaHolder sh[] = this.getAllType();
      if (sh==null)
        return null;
      if (!mgd.isReference())
        return mgd;
      for (int z=0; z<sh.length; z++) 
      {
          Vector content=sh[z].getComponents();
          for (int i=0; i<content.size(); i++) 
          {
            if (content.elementAt(i) instanceof ModelGroupDefinition &&
            !(((ModelGroupDefinition)content.elementAt(i)).isReference())) 
            {
              ModelGroupDefinition m =(ModelGroupDefinition) content.elementAt(i);
              if (mgd.getName().equals(m.getName())) 
              {
                if (mgd.getTargetNamespace()==null && m.getTargetNamespace()==null)
                  return m;
                else if (mgd.getTargetNamespace().equals(m.getTargetNamespace()))
                  return m;
              }
            }
          }//for
      }//for z
      return null;
    }//getModelGroupDefinition
   
}//class WSDLHolder

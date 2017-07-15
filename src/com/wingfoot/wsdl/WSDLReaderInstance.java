package com.wingfoot.wsdl;

import java.io.*;
import java.net.*;
import java.util.*;
import org.kxml.*;
import org.kxml.parser.*;
import com.wingfoot.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.soap.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.types.*;


/**
 * A concrete implementation of the WSDLReader interface.
 * This is the default WSDLReader.
 */
public class WSDLReaderInstance implements WSDLReader
{
  private String targetNamespace;
  private WSDLHolder wh;
  private String defaultNamespace;
  //private Vector importedElements;
  //private String defaultNamespace;

  /**
   * Creates a WSDLReaderInstance
   */
  public WSDLReaderInstance()
  {
    wh=new WSDLHolder();
  }
  
  public String getTargetNamespace()
  {
    return targetNamespace;
  }

  public void setTargetNamespace(String newTargetNamespace)
  {
    targetNamespace = newTargetNamespace;
  }

  /**
   * Overloaded parse method that takes a byte representation
   * of the WSDL and encapsulates it as WSDLHolder.
   * @param wsdlPayload byte array representing the WSDL in
   * XML form.
   * @param wsdlLocation String representing the location of 
   * the WSDL.  This is only used to determine the absolute
   * URI of an imported WSDL or Schema if the WSDL (incorrectly)
   * contains a relative URI.
   * @return WSDLHolder the encapsulated WSDL.
   * @throws IOException if any error occurs while reading
   * the WSDL.
   * @throws WSDLException if the WSDL is incorrect.
   * @throws SchemaException if the schema in the WSDL is incorrect.
   */
  public WSDLHolder parse(byte[] wsdlPayload, String wsdlLocation) throws
  IOException, WSDLException, SchemaException, Exception
  {
    
    ParseEvent pe=null;
    BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                              new ByteArrayInputStream(wsdlPayload)));
    XmlParser parser=new XmlParser(br);

    /**
     * Skip all the white spaces until you hit the
     * first StartTag.  This must be the <definitions> tag.
     */
    while (true) 
    {
      pe=parser.peek();
      if (pe.getType()==Xml.START_TAG &&
      pe.getName().equals("definitions")) 
              break;
      else
        parser.read();
    }//while

    /**
     * Now we have the first start tag.
     * Make sure this is <definitions>, and store
     * all the attributes.
     */
     if (pe.getName().equals("definitions") && pe.getType()==Xml.START_TAG) 
     {
       pe=parser.read();
       defaultNamespace=((StartTag)pe).getPrefixMap().getNamespace("");
       Vector v = pe.getAttributes();
       if (v!=null && v.size()>0) 
       {
          for (int i=0; i<v.size(); i++) 
          {
            org.kxml.Attribute aAttribute = (org.kxml.Attribute)
                                            v.elementAt(i);
            if (aAttribute.getName().equals("targetNamespace")) {
              setTargetNamespace(aAttribute.getValue());
              wh.setTargetNamespace(this.getTargetNamespace());
            }
          } //for

          //Set all the attributes that appear in the <definitions> element
          wh.setAttributes(v);
       } //if v!=null
     } //if

    convertWSDLToHolder(parser, wsdlLocation);
    return wh;
  } /*parse*/

  /**
   * Takes a WSDL and encapsulates it as WSDLHolder
   */
  private void convertWSDLToHolder(XmlParser parser, String wsdlLocation) 
  throws IOException, SchemaException, Exception
  {
    ParseEvent pe=null;
    Hashtable processedImports=new Hashtable();
    /**
     * The parser is pointing past the &ltdefinitions&gt element.
     * Every time you hit a START_TAG do something.
     */
      while (true) 
      {
        pe = parser.peek();
        if (pe.getType()==Xml.END_TAG &&
        pe.getName().equals("definitions")) 
        {
            parser.read();
            break;
        }
        else if (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.WSDL_NAMESPACE))
        {
          if (pe.getName().equals("message")) 
          {
            wh.setMessage(deserializeMessage(parser));
          }
          else if (pe.getName().equals("portType")) 
          {
            wh.setPortType(this.deserializePortType(parser));
          }
          else if (pe.getName().equals("binding")) 
          {
            wh.setBinding(this.deserializeBinding(parser));
          }
          else if (pe.getName().equals("service")) 
          {
            wh.setService(this.deserializeService(parser));
          }
          else if (pe.getName().equals("import")) 
          {
            String tempLocation=pe.getAttribute("location").getValue();
            if (processedImports.containsKey(tempLocation)) 
            {
              parser.read();
              continue;
            }
            processedImports.put(tempLocation, pe.getAttribute("namespace").getValue());
            XMLHolder importHolder=this.deserializeImport(parser, wsdlLocation);
            if (importHolder !=null) 
            {
              importHolder.setDestination(getAbsoluteURI(tempLocation,wsdlLocation));
              wh.setWsdlImport(importHolder);
            }
          }
          else
            //probably unsupported element or annotation.
            //Just read past it.
            parser.read();
        }
        else if (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
        pe.getName().equals("schema")) 
        {
          //get the SchemaHolder
          wh.setType(new SchemaReaderInstance().parse(parser));
        }
        else 
        {
          //probably a whitespace.  Just read past it.
          parser.read();
        }
      }  //while
  } /*convertWSDLToHolder*/

  /**
   * Returns an imported document encapsulated as either a
   * WSDLHolder or a SchemaHolder.
   */
  private XMLHolder deserializeImport(XmlParser parser, String wsdlLocation)
  throws WSDLException, IOException, Exception
  {
      String location=null;
      String namespace=null;
      XMLHolder importHolder=null;
    //Parser is pointing to the <import> element
      while (true) 
      {
         ParseEvent pe = parser.read();
         if (pe.getType()==Xml.END_TAG &&
         pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
         pe.getName().equals("import")) 
         {
           break;
         }
         else if (pe.getType()==Xml.START_TAG &&
         pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
         {
            if (pe.getName().equals("import")) 
            {
                if (pe.getAttribute("location") ==null)
                  throw new WSDLException("ERROR_WSDL_025:" + Constants.ERROR_WSDL_025);
                if (pe.getAttribute("namespace") ==null)
                  throw new WSDLException("ERROR_WSDL_025:" + Constants.ERROR_WSDL_025);
              
                location=pe.getAttribute("location").getValue();
                namespace=pe.getAttribute("namespace").getValue();
                String absoluteLocation=getAbsoluteURI(location, wsdlLocation);

                //Get back a XMLHolder for the imported element.
                byte[] importedPayload=XMLFactory.getPayload(absoluteLocation);
                importHolder=XMLFactory.parse(XMLFactory.getPayload(absoluteLocation),
                absoluteLocation);
            } //if import
         } //else startTAg
         else
            parser.read();
      } //while
      return importHolder;
  } //deserializeImport

  /**
   * In an <import> element, the location attribute has to be a
   * absolute URI.  However, some toolkits specify a relative URI.
   * This utility method converts a relative URI to a absolute URI.
   * @param location the location to test if relative or absolute URI.
   * @param wsdlLocation the location of the original WSDL.
   * @return String the absoluteURI representation of location
   */
  private String getAbsoluteURI(String location, String wsdlLocation) 
    throws Exception
  {
    try {
     URL u = new URL(location);
    } catch (MalformedURLException e) 
    {
      location=wsdlLocation.substring(0, wsdlLocation.lastIndexOf('/')) + "/" + location;
    }
    return location;
  }

  /**
   * Encapsulates a &ltmessage&gt element into a Message class
   */
   private Message deserializeMessage(XmlParser parser) 
   throws IOException, WSDLException
   {
     // The parser is pointing to the <message> element
     Message m=null;
     while (true) 
     {
       ParseEvent pe = parser.read();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals("message")) 
       {
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          if (pe.getName().equals("message")) 
          {
            String name=pe.getAttribute("name").getValue();
            if (name==null)
              throw new WSDLException("ERROR_WSDL_012:" + Constants.ERROR_WSDL_012);
            m = new Message(new QName(this.getTargetNamespace(), name));
          }
          else if (pe.getName().equals("part")) 
          {
            int dataType=0;
            TypeReference typeReference=null;
            String name=pe.getAttribute("name").getValue();
            if (name==null)
              throw new WSDLException("ERROR_WSDL_013:" + Constants.ERROR_WSDL_013);
            String[] type = ((StartTag)pe).getNormalizedAttribute(null, "type");
            if (type !=null) 
              dataType=Part.TYPE;
            else 
            {
                type=((StartTag) pe).getNormalizedAttribute(null, "element");
                if (type==null)
                  throw new WSDLException("ERROR_WSDL_014:" + Constants.ERROR_WSDL_014);
                else 
                  dataType=Part.ELEMENT;
            }
            typeReference=type[0]==null? new TypeReference(this.defaultNamespace, type[1]):
            new TypeReference(type[0], type[1]);
            m.setMessagePart(new Part(name, typeReference, dataType));
          } //else if part
       } //if START_TAG
     } //while
     return m;
   } /*deserializeMessage*/

  /**
   * Encapsulates a &ltportType&gt element into a PortType class
   */
   private PortType deserializePortType(XmlParser parser) 
   throws IOException, WSDLException
   {
     PortType pt =null;
     // The parser is pointing to the <portType> element
     while (true) 
     {
       ParseEvent pe = parser.peek();
       if (pe.getType()==Xml.END_TAG &&
       pe.getName().equals("portType")) 
       {
         parser.read();
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          if (pe.getName().equals("portType")) 
          {
            pe=parser.read();
            String portTypeName=pe.getAttribute("name").getValue();
            if (portTypeName==null)
              throw new WSDLException("ERROR_WSDL_015:"+ Constants.ERROR_WSDL_015);
            pt = new PortType(new QName(this.getTargetNamespace(), portTypeName));
          }
          else if (pe.getName().equals("operation")) 
          {
            Operation o = deserializeOperation(parser);
            pt.setOperation(o);
          }
          else 
            //Unknown type. read past it.
            parser.read();
       } //START_TAG
       else
          //probably whitespace.  Just read past it.
          parser.read();
     } //while
     return pt;
   } //deserializePortType


  /**
   * Encapsulates a &ltoperation&gt element into a Operation class
   */
   private Operation deserializeOperation(XmlParser parser) 
   throws IOException, WSDLException
   {
     Operation o =null;
     // The parser is pointing to the <operation> element
     while (true) 
     {
       ParseEvent pe = parser.read();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals("operation")) 
       {
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          if (pe.getName().equals("operation")) 
          {
            String name = pe.getAttribute("name").getValue();
            if (name==null)
              throw new WSDLException("ERROR_WSDL_016:"+Constants.ERROR_WSDL_016);
            o=new Operation(name);
          }
          else if (pe.getName().equals("input")) 
          {
             o.setInputMessage(getMessageFromOperation((StartTag) pe));
             o.setInputMessageName(pe.getAttribute("name")==null?o.getName()+"Request":pe.getAttribute("name").getValue());
          }
          else if (pe.getName().equals("output")) 
          {
              o.setOutputMessage(getMessageFromOperation((StartTag) pe));
              o.setOutputMessageName(pe.getAttribute("name")==null?o.getName()+"Response":pe.getAttribute("name").getValue());
          }
          else if (pe.getName().equals("fault")) 
          {
              o.setFaultMessage(getMessageFromOperation((StartTag) pe));
          }
       } //START_TAG
     } //while
     return o;
   } //deserializeOperation

  /**
   * Utility method to retrieve a Message from WSDLHolder that
   * is being referred to by the <input> element in a <portType>
   */
   private Message getMessageFromOperation(StartTag st) throws WSDLException
   {
     String[] aMessage = st.getNormalizedAttribute(null, "message");
      if (aMessage==null)
        throw new WSDLException("ERROR_WSDL_017:"+Constants.ERROR_WSDL_017);
      Message m=this.wh.getMessage(new QName(aMessage[0], aMessage[1]));
      if (m==null)
        throw new WSDLException("ERROR_WSDL_018:"+ Constants.ERROR_WSDL_018);
      return m;
   }

  /**
   * Encapsulates a &ltservice&gt element into a Service class
   */
   private Service deserializeService(XmlParser parser) 
   throws IOException, WSDLException
   {
     Service s =null;
     // The parser is pointing to the <service> element
     while (true) 
     {
       ParseEvent pe = parser.peek();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals("service")) 
       {
         parser.read();
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          if (pe.getName().equals("service")) 
          {
            pe=parser.read();
            String name = ((StartTag) pe).getAttribute("name").getValue();
            if (name==null) 
              throw new WSDLException("ERROR_WSDL_019:"+Constants.ERROR_WSDL_019);
            s=new Service(name);
          }
          else if (pe.getName().equals("port")) 
          {
            s.setPort(deserializePort(parser));
          }
          else
            //Read past this unknown
            parser.read();
        }//START_TAG
        else
          //Read past the whitespace
          parser.read();
     } //while
     return s;
   } //deserializeService


  /**
   * Encapsulates a &ltport&gt element into a Port class
   */
   private Port deserializePort(XmlParser parser) 
   throws IOException, WSDLException
   {
     Port p =null;
     // The parser is pointing to the <service> element
     while (true) 
     {
       ParseEvent pe = parser.read();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals("port")) 
       {
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          if (pe.getName().equals("port")) 
          {
              String name = ((StartTag)pe).getAttribute("name").getValue();
              if (name==null)
                throw new WSDLException("ERROR_WSDL_020:"+Constants.ERROR_WSDL_020);
              String[] binding = ((StartTag)pe).getNormalizedAttribute(null,"binding");
              if (binding==null)
                throw new WSDLException("ERROR_WSDL_021:" + Constants.ERROR_WSDL_021);
              p = new Port(name, new QName(binding[0], binding[1]));
          }
       } //START_TAG
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_SOAP_NAMESPACE) &&
       pe.getName().equals("address")) 
       {
         SOAPAddress sa = new SOAPAddress();
         sa.setAddress(pe.getAttribute("location").getValue());
         p.setEndPoint(sa);
       }
     } //while
     return p;
   } //deserializePort


  /**
   * Encapsulates a &ltbinding&gt element into a Binding class
   */
   private Binding deserializeBinding(XmlParser parser) 
   throws IOException, WSDLException
   {
     Binding b =null;
     String bindingName=null;
     PortType p =null;
     // The parser is pointing to the <service> element
     while (true) 
     {
       ParseEvent pe = parser.peek();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals("binding")) 
       {
         parser.read();
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          if (pe.getName().equals("binding")) 
          {
            pe=parser.read();
            bindingName=pe.getAttribute("name").getValue();
            String[] bindingType=((StartTag)pe).getNormalizedAttribute(null, "type");
            if (bindingName==null || bindingType==null)
              throw new WSDLException("ERROR_WSDL_022:" + Constants.ERROR_WSDL_022);
            //Make sure the portType exists.
            p = wh.getPortType(new QName(bindingType[0], bindingType[1]));
            if (p==null)
              throw new WSDLException("ERROR_WSDL_023:"+ Constants.ERROR_WSDL_023+":"+bindingName);
            b = new Binding(new QName(this.getTargetNamespace(),bindingName),p);
          }
          else if (pe.getName().equals("operation")) 
          {
              b.setBindingOperation(deserializeBindingOperation(parser,p));
          }
          else 
            //Probably annotation. Just skip past
            parser.read();
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_SOAP_NAMESPACE) &&
       pe.getName().equals("binding")) 
       {
         pe=parser.read();
         SOAPBinding sb = new SOAPBinding();
         String style=pe.getAttribute("style").getValue();
         if (style.equalsIgnoreCase("rpc"))
          sb.setStyle(SOAPBinding.RPC);
         sb.setTransport(pe.getAttribute("transport").getValue());
         b.setBindingExtension(sb);
       }
       else 
        //Just skip past it
        parser.read();
     } //while
     return b;
   } //deserializeBinding

   private BindingOperation deserializeBindingOperation(XmlParser parser, PortType portType) 
   throws IOException, WSDLException
   {
     BindingOperation bo =null;
     String inputMessageName=null;
     Operation[] oArray=null;
     SOAPOperation so=null;
     MessageFormat[] inputMessageFormat=null;
     MessageFormat[] outputMessageFormat=null;
     MessageFormat[] faultMessageFormat=null;
     // The parser is pointing to the <operation> element
     while (true) 
     {
       ParseEvent pe = parser.peek();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals("operation")) 
       {
         parser.read();
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE)) 
       {
          pe=parser.read();
          if (pe.getName().equals("operation")) 
          {
            String name=pe.getAttribute("name").getValue();
            oArray = portType.getOperationArray(name);
            if (oArray==null||oArray.length==0)
              throw new WSDLException("ERROR_WSDL_024:" + Constants.ERROR_WSDL_024+":"+
              portType.getName().toString() + ":" + name);
            //bo = new BindingOperation(o);
          } //operation
          else if (pe.getName().equals("input")) 
          {
              inputMessageName=pe.getAttribute("name")!=null?pe.getAttribute("name").getValue():null;
              //bo.setInputMessageFormat(getMessageFormat(parser, "input"));
              inputMessageFormat=getMessageFormat(parser, "input");
          }
          else if (pe.getName().equals("output")) 
          {
              //bo.setOutputMessageFormat(getMessageFormat(parser, "output"));
              outputMessageFormat=getMessageFormat(parser, "output");
          }
          else if (pe.getName().equals("fault")) 
          {
            //bo.setFaultMessageFormat(getMessageFormat(parser, "fault"));
              faultMessageFormat=getMessageFormat(parser, "fault");
          }
       } //START_TAG
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_SOAP_NAMESPACE) &&
       pe.getName().equals("operation")) 
       {
         pe=parser.read();
         so = new SOAPOperation();
         if (pe.getAttribute("soapAction") !=null)
          so.setSoapAction(pe.getAttribute("soapAction").getValue());
         String style = pe.getAttribute("style") !=null?
         pe.getAttribute("style").getValue():null;
         if (style !=null && style.equalsIgnoreCase("rpc"))
          so.setStyle(SOAPBinding.RPC);
         else if (style !=null && style.equalsIgnoreCase("document"))
          so.setStyle(SOAPBinding.DOCUMENT);
         //bo.setExtension(so);
       }
       else
        parser.read();
     } //while

     /**
      * Try to find the correct operation based in inputMessageName.
      */
      if (inputMessageName==null)
        bo = new BindingOperation(oArray[0]);
      else
      {
        for (int i=0; i<oArray.length; i++)
        {
          if (oArray[i].getInputMessageName() != null &&
          oArray[i].getInputMessageName().equals(inputMessageName))
          {
            bo = new BindingOperation(oArray[i]);
            break;
          }
        }//for
      } //else
      if (bo==null)
        throw new WSDLException("ERROR_WSDL_024:" + Constants.ERROR_WSDL_024+":"+
        portType.getName().toString());
      bo.setInputMessageFormat(inputMessageFormat);
      bo.setOutputMessageFormat(outputMessageFormat);
      bo.setFaultMessageFormat(faultMessageFormat);
      bo.setExtension(so);
     return bo;
   } //deserializeBindingOperation

  /**
   * Currently supports only <soap:body>
   * parts attribute is not supported.
   */
   private MessageFormat[] getMessageFormat(XmlParser parser, String endElementName) 
   throws IOException, WSDLException
   {
     Vector v = new Vector();
     // The parser is pointing to the <input> or <output> or <fault> element
     while (true) 
     {
       ParseEvent pe = parser.read();
       if (pe.getType()==Xml.END_TAG &&
       pe.getNamespace().equals(Constants.WSDL_NAMESPACE) &&
       pe.getName().equals(endElementName)) 
       {
         break;
       }
       else if (pe.getType()==Xml.START_TAG &&
       pe.getNamespace().equals(Constants.WSDL_SOAP_NAMESPACE)) 
       {
          SOAPMessage sm=null;
          if (pe.getName().equals("body")) 
          {
            sm = new SOAPMessage(SOAPMessage.BODY);
            if (pe.getAttribute("use").getValue().equals("encoded")) 
            {
              sm.setUse(SOAPMessage.ENCODED);
              sm.setEncodingStyle(pe.getAttribute("encodingStyle").getValue());
            }
            else
              sm.setUse(SOAPMessage.LITERAL);
            if (pe.getAttribute("namespace") !=null)
              sm.setNamespaceURI(pe.getAttribute("namespace").getValue());
          }
          
          if (sm!=null)
            v.add(sm);
       }
     } //while
     if (v==null)
      return null;
     MessageFormat[] mf = new MessageFormat[v.size()];
     for (int i=0; i<v.size(); i++) 
     {
       mf[i]=(MessageFormat) v.elementAt(i);
     }
     return mf;
   } //method

  public String getDefaultNamespace()
  {
    return defaultNamespace;
  }

  public void setDefaultNamespace(String newDefaultNamespace)
  {
    defaultNamespace = newDefaultNamespace;
  }

}
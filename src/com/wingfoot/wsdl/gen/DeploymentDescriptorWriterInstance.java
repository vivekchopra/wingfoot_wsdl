package com.wingfoot.wsdl.gen;

import java.util.*;
import org.kxml.io.*;
import com.wingfoot.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.wsdl.soap.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.types.*;
import com.wingfoot.soap.encoding.*;
import com.wingfoot.xml.schema.gen.*;

/**
 * Takes a WSDLHolder and generates a Deployment Descriptor file, which is
 * used when deploying a service on Parvus.  The deployment descriptor is
 * named [ClassName]DD.xml.  Each PortType becomes a <service> element containing
 * enough information to be used when deploying a service
 */
public class DeploymentDescriptorWriterInstance extends BaseWriter implements DeploymentDescriptorWriter 
{
  private SchemaConverterInstance sci = null;
  
  public DeploymentDescriptorWriterInstance()
  {
  } //DeploymentDescriptorWriterInstance

  /**
   * Takes a WSDLHolder and generates a Deployment Descriptor used 
   * when deploying a service to a Parvus Service. The name of the Deployment
   * Descriptor is the destinationName+"DD".xml The destinationName is retrieved
   * from the WSDLHolder
   * @param wsdl The WSDLHolder used for Deployment Descriptor generation
   * @param classToConvert The name of the class used for the deployment descriptor
   * @return SerializedHolder Encapsulates the byte[] representation of the
   * Deployment Descriptor
   */
  public SerializedHolder[] toDeploymentDescriptor(WSDLHolder wsdl, Class classToConvert, Options options)
  throws Exception
  {
    //Get all the imported SchemaHolders.  Create Beans if required.
    SchemaHolder[] shArray = wsdl.getAllType();
  
    sci = new SchemaConverterInstance(shArray, options);
    sci.toJava(); 
   

    //Setup the XMLWriter hashtable
    Hashtable defaultNS = new Hashtable();
    XMLWriter writer = new XMLWriter(defaultNS);
    
    PortType[] portType = wsdl.getPortType();

    for(int i = 0;portType!=null &&i< portType.length; i++)
    {
      if(isBindingSOAP(portType[i], wsdl))
      {
        writeXML(portType[i], wsdl, classToConvert, options, writer);
      }
    }//for portTypes

    //now generate Deployment Descriptors for any WSDLImports
    generateDeploymentDescriptorForImports(wsdl, classToConvert, options, writer);


    String dDescriptorFileName = null;
    
    if(options.getOutputResourceName() != null)
      dDescriptorFileName = options.getOutputResourceName().indexOf('.') == -1?
      options.getOutputResourceName()+".dd":
      options.getOutputResourceName().substring(0,options.getOutputResourceName().lastIndexOf('.'))+".dd";
    else 
      dDescriptorFileName = new String(classToConvert.getName()).substring(classToConvert.getName().lastIndexOf(".") + 1,
                                       classToConvert.getName().length()) + ".dd";

    /**
     * For now only one deployment descriptor is generated.  However in the 
     * future, multiple can be generated.  So in anticipation, a serializedHolder[]
     * is returned. - Thus explains this goofy need to put it into an array
     */
    
    return new SerializedHolder[] {new SerializedHolder(writer.getPayload("utf-8"), dDescriptorFileName)};
  } //toDeploymentDescriptor

  /**
   * Writes the Service element of the deployment descriptor.  Each PortType becomes
   * a Service element.  The <Service> element contains all 
   * the neccessary elements for deploying a service to the Parvus Server.  
   * @param portType A Deployment Descriptor is generated for the given PortType
   * @param wsdl WSDLHolder used for deployment descriptor generation
   * @param classToConvert Web Service class, which a deployment descriptor is generated for
   * @param options User encapsulated options
   * @param writer The XMLWriter being used 
   */
  private void writeXML(PortType portType, WSDLHolder wsdl, Class classToConvert, Options options, XMLWriter writer)
  throws Exception
  {

    writer.startElement("service");
    writer.attribute("name", portType.getName().getLocalPart());
    writer.attribute("style", getStyle(portType, wsdl)); //determines whether RPC or document

    //write the Namespace element
    writer.startElement("namespace");
    writer.elementBody(wsdl.getTargetNamespace());
    writer.endTag();

    
    //write the ClassName
    writer.startElement("classname");
    writer.elementBody(classToConvert.getName());
    writer.endTag();

    writer.startElement("portType");
    writer.attribute("name",portType.getName().getLocalPart());
    writer.attribute("namespace", portType.getName().getNamespaceURI());
    writer.endTag();
    
    //write out the operations
    writeOperations(writer, portType, wsdl);
    
    //write out the typemapping registry
    writeTypeMappingRegistry(writer, wsdl, options);

    writeWSDLName(writer, wsdl, classToConvert, options);
    //end the service element
    writer.endTag();

  } //writeXML

  /**
   * Writes the WSDLLocation to the deployment descriptor
   * @param writer The XMLWriter being used
   * @param holder WSDLHolder containing the information being converted into a
   * Deployment Descriptor
   * @param classToConvert is an instance of the class being converted to Deployment
   * Descriptor
   * @param options User encapsulated options
   * @wsdllocation the string representation of the wsdllocation
   */
  private void writeWSDLName(XMLWriter writer, WSDLHolder holder, Class classToConvert, Options options)
  {
    String WSDLName = null;
    
    if(options.getOutputResourceName() != null)
      WSDLName = options.getOutputResourceName();
    else 
    {
      WSDLName = new String(classToConvert.getName()).substring(classToConvert.getName().lastIndexOf(".") + 1,
      classToConvert.getName().length()) + ".wsdl";
    }
    
    if(WSDLName != null) 
    {
      writer.startElement("wsdl");
      writer.attribute("name", WSDLName);
      writer.endTag();
    }
  }

  /**
   * Write the Typemapping Registry elements for the Deployment Descriptor.  Instantiates
   * a SchemaConverterInstance with the user options and uses the Vector of types
   * from the SchemaConverterInstance to generate Typemapping registry elements
   * @param writer The XMLWriter being used 
   * @param wsdl WSDLHolder used to generate the TypeMappingRegistry
   * @param options User encapsulated options
   */
  private void writeTypeMappingRegistry(XMLWriter writer, WSDLHolder wsdl, Options options)
  {
    //Look at the SchemaHolder to see
    Vector mapVector = sci.getTypeMap();
    if(mapVector != null && mapVector.size() > 0) 
    {
      for(int i = 0; i < mapVector.size(); i++)
      {
        String[] strArr = (String[]) mapVector.elementAt(i);  
        
        writer.startElement("typemap");

        //write the namespace
        writer.startElement("typeNamespace");
        writer.elementBody(strArr[0]);
        writer.endTag();

        //write the qname
        writer.startElement("qname");
        writer.elementBody(strArr[1]);
        writer.endTag();

        writer.startElement("type");
        //write the className
        //Get the JavaType from the part.  If not found
        //then use strArr[2].
        String javaTypeName=getJavaTypeForPart(strArr[0], strArr[1], wsdl);
        if (javaTypeName!=null)
          writer.elementBody(javaTypeName);
        else
          writer.elementBody(strArr[2]);
        writer.endTag();

        //write the Java2XMLClass
        writer.startElement("Java2XMLClass");
        writer.elementBody("com.wingfoot.soap.encoding.ComplexTypeSerializer");
        writer.endTag();

        writer.startElement("XML2JavaClass");
        writer.elementBody("com.wingfoot.soap.encoding.ComplexTypeDeserializer");
        writer.endTag();
        
        //end Tag for <typemap> element
        writer.endTag();
      }
    } //mapVector != null
    
  } //writeTypeMappingRegistry

  /**
   * Iterates through the WSDLHolder to retrieve a Part with the namespace
   * and name.  Returns back the JavaTypeName attribute for the Part.
   * Returns null if there is no part by that name.
   */
  private String getJavaTypeForPart(String namespace, String name, WSDLHolder wsdl)
  {
      PortType[] ptArray = wsdl.getAllPortType();
      if (ptArray==null || ptArray.length==0)
        return null;
      TypeReference aType=null;
      for (int i=0; i<ptArray.length; i++)
      {
        Operation[] oArray = ptArray[i].getOperation();
        for (int j=0; oArray!=null&&j<oArray.length; j++)
        {
          Message inputMessage = oArray[j].getInputMessage();
          Part inputPartArray[] = inputMessage.getMessagePart();
          for (int k=0; inputPartArray!=null && k<inputPartArray.length; k++)
          {
            if (inputPartArray[k].getPartType()==Part.ELEMENT)
            {
              Element e = wsdl.getElement(inputPartArray[k].getType().getTargetNamespace(),
              inputPartArray[k].getType().getLocalPart());
              if (e==null)
                return null;
              aType=new TypeReference(e.getType().getTargetNamespace(), e.getType().getName());
            }
            else
              aType = inputPartArray[k].getType();
            if (aType.getName().equals(name) && aType.getTargetNamespace().equals(namespace))
              return inputPartArray[k].getJavaTypeName();
            else if (aType.getName().equals("ArrayOf"+name) && aType.getTargetNamespace().equals(namespace))
              return inputPartArray[k].getJavaTypeName().substring(2, (inputPartArray[k].getJavaTypeName().length()-1));
          }

          Message outputMessage = oArray[j].getOutputMessage();
          Part outputPartArray[] = outputMessage.getMessagePart();
          for (int k=0; outputPartArray!=null && k<outputPartArray.length; k++)
          {
            if (outputPartArray[k].getPartType()==Part.ELEMENT)
            {
              Element e = wsdl.getElement(outputPartArray[k].getType().getTargetNamespace(),
              outputPartArray[k].getType().getLocalPart());
              if (e==null)
                return null;
              aType=new TypeReference(e.getType().getTargetNamespace(), e.getType().getName());
            }
            else
              aType = outputPartArray[k].getType();
            if (aType.getName().equals(name) && aType.getTargetNamespace().equals(namespace))
              return outputPartArray[k].getJavaTypeName();
          }        
        }//for j==0
      }//for i==0
      return null;
  }//getJavaTypeForPart
  
  /**
   * Converts the operations from the WSDL to a method element.  Also looks at the
   * corresponding Parts to the operation and writes out <parameter> elements nested
   * inside <method>
   * @param writer The XMLWriter the <method> element is written to
   * @param portType The PortType contains all the operations to be converted to <method> element
   * @param wsdl The WSDLHolder object used for this deployment descriptor
   */
  private void writeOperations(XMLWriter writer, PortType portType, WSDLHolder wsdl)
  throws WSDLException
  {

    Operation[] operation = portType.getOperation();
    
    for(int j = 0; operation!=null&&j < operation.length; j ++)
    {
      if(operationExistsInBinding(operation[j], wsdl.getBinding()))
      {
        writer.startElement("method");
        writer.attribute("name", operation[j].getName());

        //next get the parameter names for this operation
        Vector nameVector = getMessageParts(operation[j].getInputMessage(), wsdl, sci);
        for(int k = 0; nameVector!=null&&k < nameVector.size(); k++)
        {
          String[] strArray = (String[]) nameVector.elementAt(k);
          writer.startElement("parameter");
          writer.attribute("name", strArray[1]);
          //writer.attribute("type", strArray[0]);
          writer.attribute("type", strArray[2]);
          writer.endTag();
        } //k < nameVector.size()
        writer.endTag();
      } //i = operation.length
    }
  } //writeOperations

  /**
   * Generate a deployment descriptor for any imported WSDL's it encounters.  This 
   * method loops through the importWSDL and calls the writeXML() method to generate 
   * a <Service> element.
   * @param wsdl The original WSDLHolder, which might contain imports
   * @param classToConvert The class being converted to a deployment descriptor
   * @param options User encapsulated options
   * @param writer The XMLWriter used to write the deployment descriptor
   */
  private void generateDeploymentDescriptorForImports(WSDLHolder wsdl, Class classToConvert, Options options, XMLWriter writer)
  throws Exception
  {
    Vector imports = wsdl.getWsdlImport();
    
    if(imports != null)
    {
      for(int i = 0; i < imports.size(); i ++)
      {
        if(imports.elementAt(i) instanceof WSDLHolder)
        {
          WSDLHolder importWSDL = (WSDLHolder) imports.elementAt(i);
          PortType[] portType = importWSDL.getPortType();
          for(int j = 0; j < portType.length; j++)
            writeXML(portType[j], importWSDL, classToConvert, options, writer);
        } //if
      } //for
    } //if imports!=null
  } //generateDeploymentDescriptorForImports

}

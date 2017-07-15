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
/**
 * Base class containing common methods used by DeploymentDescriptorWriterInstance
 * and JavaWriterInstance. These classes both inspect the WSDLHolder to extract 
 * information and generate their respective output. They both use common methods.  
 */
public class BaseWriter 
{

  
  public BaseWriter()
  {
  } //BaseWriter

  /**
   * Takes a WSDL and determines if there is a SchemaHolder
   * either in the WSDL or one of its imported components.
   * The imported SchemaHolders are put in a Vector and
   * returned back.
   * @return Vector of SchemaHolder; null if no SchemaHolder
   * is present.
   */
   /*
  protected Vector gatherSchemaImports(WSDLHolder wsdl) 
  {
    Vector returnVector=null;
    if (wsdl==null)
      return null;
    else if (wsdl.getType()==null && wsdl.getWsdlImport()==null)
      return null;

    SchemaHolder[] sh=wsdl.getType();
    if (sh!=null) 
    {
        for (int i=0;i<sh.length; i++) 
        {
          if (returnVector==null) returnVector=new Vector();
          returnVector.add(sh[i]);
        }//for
    }//if

    //Take care of SchemaImports
    Vector wsdlImportVector=wsdl.getWsdlImport();
    if (wsdlImportVector!=null) 
    {
      for (int i=0; i<wsdlImportVector.size(); i++) 
      {
        if (wsdlImportVector.elementAt(i) instanceof SchemaHolder) 
        {
          if (returnVector==null) returnVector=new Vector();
          returnVector.add((SchemaHolder)wsdlImportVector.elementAt(i));
        }//if schemaHolder
        else if (wsdlImportVector.elementAt(i) instanceof WSDLHolder) 
        {
          Vector tempVector=this.gatherSchemaImports((WSDLHolder)wsdlImportVector.elementAt(i));
          if (tempVector!=null) 
          {
            if (returnVector==null) returnVector=new Vector();
            for (int j=0; j<tempVector.size(); j++) 
            {
              returnVector.add(tempVector.elementAt(j));
            }//for
          }//if
        }//else WSDLHolder
      }//for
    }//if wsdlImportVector!=null
    return returnVector;
  }//gatherSchemaImports
*/
  /**
   * Checks to see if a the binding is of SOAP TYPE, either RPC or DOCUMENT
   * @param portType The portType to check
   * @param wsdl The wsdl which contains array of binding objects
   * @return boolean returns true if this portType is a SOAP binding; false if otherwise
   */
  protected boolean isBindingSOAP(PortType portType, WSDLHolder wsdl)
  {
    //boolean isSOAP = false;
    //Binding[] binding = wsdl.getBinding();
    Binding b = wsdl.getBinding(portType);
    if (b==null)
      return false;
    else if (b.getBindingExtension() instanceof SOAPBinding)
      return true;
    else  
      return false;
    /**
    for(int i = 0; binding != null && i < binding.length; i++)
    {
      if(binding[i].getPortType().equals(portType) &&
         binding[i].getBindingExtension() != null &&
         binding[i].getBindingExtension() instanceof SOAPBinding)
        isSOAP = true;

    } //for
      **/
    /**
     * If isSOAP is false, see if there are any imports.
     * Perhaps the binding element lies within an import
     * Loop through the imports and call the isBindingSOAP 
     * method again
     */
     /**
    if(!isSOAP)
    {
      if(wsdl.getWsdlImport() != null)
      {
        Vector tmpVector = wsdl.getWsdlImport();
        for(int i = 0; tmpVector != null && i < tmpVector.size() && 
        tmpVector.elementAt(i) instanceof WSDLHolder; i ++)
        {
          isSOAP = this.isBindingSOAP(portType, (WSDLHolder)tmpVector.elementAt(i));
        } //for
      } //wsdl.getWsdlImport() != null
    } //if(!isSOAP)
    return isSOAP;
    **/
  }
  
  /**
   * Determines the style of the service.  For a given portType determine if
   * the style is document or rpc
   * @param portType The portType to check
   * @param wsdl The WSDLHolder
   */
  protected String getStyle(PortType portType, WSDLHolder wsdl)
  {
    Binding binding = wsdl.getBinding(portType);
    if(((SOAPBinding)binding.getBindingExtension()).getStyle() == SOAPBinding.DOCUMENT)
      return "document";
    else
      return "rpc";
  } //getStyle

  /**
   * Takes a Message and returns a Java representation.
   * The Message may have zero or more Parts.  Each
   * Part represents a parameter to a method.
   * @param message to convert to Java.
   * @param wsdl that contains the message.  The wsdl
   * is required because the message may be referring to
   * a <complexType> that may refer to another complexType
   * in an imported WSDL.
   * @param serializedHolders a Vector of SerializedHolder.
   * This is the repository of SerializedHolders generated
   * by the WSDL.
   * @return A Vector which encapsulates a String[].
   * The encapsulated String[] has the following:
   * string[0] contains the Type of the part
   * string[1] contains the Name of the part
   * string[2] contains the name of the JavaType.  This
   * is retrieved from Part.getJavaTypeName; has
   * null if the Part does not have javaTypeName.
   */
  protected Vector getMessageParts(Message message, WSDLHolder wsdl, 
  SchemaConverterInstance sci) throws WSDLException
  {
    /**
     * A message has one or more parts.
     * Retrieve the parts here.
     */
    Vector partArray = new Vector(message.getMessagePartCount());
    
    if (message.getMessagePartCount()==0) 
      return null;
    for (int i=0; i<message.getMessagePartCount(); i++) 
    {
      String[] tmpArray = new String[3];
      Part aPart = message.getMessagePart(i);
      Type partType = null;
      
      if(aPart.getPartType() == Part.ELEMENT)
        tmpArray[0]=sci.getQualifiedJavaType(getElementFromSchema(wsdl,aPart));
      else
        tmpArray[0]=sci.getQualifiedJavaType(aPart.getType());
      if (aPart.getPartType()==Part.TYPE)
        tmpArray[1] = aPart.getPartName();
      else 
      {
        TypeReference tr=aPart.getType();
        Element e = wsdl.getElement(tr.getTargetNamespace(), tr.getName());
        if (e==null)
          throw new WSDLException("Cannot find element :" + tr);
        tmpArray[1]=e.getName();
      }
      tmpArray[2]=aPart.getJavaTypeName();
      partArray.add(tmpArray); 
    } //for
    return partArray;
  }//getMessageParts

  /**
   * This method returns an element from a part type which is
   * an element.If the element is not found in the schema, then the return
   * is null.
   * @param wsdl The wsdl which contains all the schema components and elements
   * @param part The part which contains an element instead of a type
   * @return element An element to be returned
   */
  protected Element getElementFromSchema(WSDLHolder wsdl, Part part)
  {
    Element element = null;
    String partElementName = part.getType().getName();
    String partElementNamespace = part.getType().getTargetNamespace();

    if(partElementName != null && partElementNamespace != null)
    {
      SchemaHolder[] holder = wsdl.getType();
      for(int i = 0; i < holder.length; i++)
      {
        if(holder[i].getTargetNamespace().equals(partElementNamespace)) 
        {
          SchemaHolder tmpHolder = holder[i];
          for(int j = 0; j < tmpHolder.getComponentCount(); j ++)
          {
            Component c = tmpHolder.getComponent(j);
            if(c instanceof Element)
            {
              Element tmpElement = (Element) c;
              if(tmpElement.getName().equals(partElementName) &&
               holder[i].getTargetNamespace().equals(partElementNamespace))
                element = tmpElement;
            } //if
          } //for
        } //if
      }//for
    }//if
    return element;
  }

  /**
   * Checks to see if the operation exists inside a Binding
   * @param operation The operation object
   * @param binding Binding object
   * @return boolean whether the operation exists in Binding 
   */
  protected boolean operationExistsInBinding(Operation operation, Binding[] binding)
  {
    boolean doesExist = false;
    for (int i = 0; binding!=null&&i < binding.length; i ++)
    {
      BindingOperation[] bindingOperation = binding[i].getBindingOperation();
      for (int j = 0; bindingOperation != null && j < bindingOperation.length; j ++)
      {
          if(operation.getName().equals(bindingOperation[j].getOperation().getName()))
          {
            doesExist = true;
            break;
          }
      } //for
    } //for
    return doesExist;
  }//operationExistsInBinding
}//BaseWriter
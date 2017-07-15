package com.wingfoot.wsdl;
import java.util.Vector;
import java.io.*;
import com.wingfoot.*;
import org.kxml.io.*;

/**
 * A port type is a named set of abstract operations and the
 * abstract messages involved.
 */
public class PortType 
{
  private Vector operation;
  private QName name;

  /**
   * Creates a portType with a given
   * QName.
   * @param name the QName of the PortType
   * @throws WSDLException if the name is null.
   */
  public PortType(QName name) throws WSDLException
  {
    if (name==null || name.getLocalPart()==null)
      throw new WSDLException("ERROR_WSDL_005"+Constants.ERROR_WSDL_005);
    this.name=name;
  }

  public QName getName() 
  {
    return name;
  }

  /**
   * Returns a array of Operation.  Each element of
   * the array is an Operation with the same name as
   * the input name.
   * <p>
   * Useful method to retrieve the overloaded operations
   * in a PortType.
   * @param operationName the operation name.
   * @return Operation[] array of overloaded Operation;
   * null if operationName is null or an operation with
   * the given name does not exist.
   */
  public Operation[] getOperationArray(String operationName) 
  {
    if (operation==null||this.getOperationCount(operationName)==0)
      return null;
    Operation[] oArray=new Operation[this.getOperationCount(operationName)];
    for (int i=0,ctr=0; i<operation.size(); i++) 
    {
      Operation o = (Operation) operation.elementAt(i);
      if (o.getName().equals(operationName))
        oArray[ctr++]=o;
    }
    return oArray;
  }
  
  public Operation[] getOperation()
  {
    if (operation==null)
      return null;
    Operation[] o = new Operation[operation.size()];
    for (int i=0; i<operation.size(); i++)
      o[i]=(Operation)operation.elementAt(i);
    return o;
  }

  public Operation getOperation(String name) 
  {
    if (operation==null)
      return null;
    for (int i=0; i<operation.size(); i++) 
    {
      Operation o = (Operation) operation.elementAt(i);
      if (o.getName().equals(name))
        return o;
    }
    return null;
  }

  /**
   * Returns an operation from the PortType.  The Operation is
   * searched based on the operation name and the first input
   * part name.  If the input part name is null then the search
   * is performed on the operation name with no input parts.
   * @param operationName the name of the operation
   * @param firstPartName the name of the first input parameter;
   * null if the operation expects no parameter.
   * @return Operation that operation that matches the search
   * criteria; null if no operation matches the search criteria.
   */
  public Operation getOperation(String operationName, String firstParameterName)
  {
      Operation[] oArray = this.getOperationArray(operationName);
      if (oArray==null)
        return null;
      else
      {
        //Overloaded operation; Get the correct operation based
        //on the first parameter name.
        for (int i=0; i<oArray.length; i++)
        {
          Operation o = oArray[i];
          if (firstParameterName==null && (o.getInputMessage()==null ||
          o.getInputMessage().getMessagePartCount()==0))
            return o;
          else if (o.getInputMessage().getMessagePart(0).getPartName().equals(firstParameterName))
            return o;
        }//for        
        //Try to see if there is a literal operation
        for (int i=0; i<oArray.length; i++)
        {
          Operation o = oArray[i];
          if (firstParameterName==null && (o.getInputMessage()==null ||
          o.getInputMessage().getMessagePartCount()==0))
            return o;
          else if (o.getInputMessage().getMessagePart(0).getType().getName().equals(firstParameterName))
            return o;
        }
      }
      return null;
  } //getOperation
  
  public void setOperation(Operation[] newOperation)
    throws WSDLException
  {
    if (newOperation==null)
      this.operation=null;
    else 
    {
      for (int i=0; i<newOperation.length; i++) 
        this.setOperation(newOperation[i]);
    }
  }

  public void setOperation(Operation operation)
    throws WSDLException
  {
    if (this.operation==null)
      this.operation=new Vector();
      /*
    for (int i=0; i<this.operation.size(); i++) 
    {
      Operation o = (Operation) this.operation.elementAt(i);
      if (o.equals(operation))
        throw new WSDLException("ERROR_WSDL_007:"+Constants.ERROR_WSDL_007+":"+this.getName()
          +":"+operation.getName());
    }
    */
    this.operation.add(operation);
  }

  public int getOperationCount()
  {
    if (this.operation==null)
      return 0;
    else
      return operation.size();
  }

  /**
   * A &lt;portType&gt; may have multiple operations
   * with the same name.  This method determines the
   * number of occurances of a operation with a name.
   * @param operationName String with the name of the
   * operation
   * @return int the number of operations with the input
   * operationName; 0 if no such operation exists.
   */
  public int getOperationCount(String operationName) 
  {
    int ctr=0;
    if (operation!=null) 
    {
      for (int i=0; i<operation.size(); i++) 
      {
        Operation o = (Operation) operation.elementAt(i);
        if (o.getName().equals(operationName))
          ctr++;
      }//for
    }//if operation!=null
    return ctr;
  }//getOperationCount

  public Operation getOperation(int index)
  {
    return (Operation) this.operation.elementAt(index);
  }

  /**
   * Two portTypes are identical if their QNames are
   * identical.
   */
  public boolean equals(PortType portType) 
  {
    if (getName().equals(portType.getName()))
      return true;
    else
      return false;
  }

  /**
   * Converts this instance of PortType to a WSDL stub.
   * @param writer instance of XMLWriter to write to.
   * @throws IOException if an error occurs while writing
   * the WSDL to writer.
   */
  public void toWSDL(XMLWriter writer) throws IOException
  {
    writer.startElement("portType", Constants.WSDL_NAMESPACE);
    writer.attribute("name", getName().getLocalPart());
    
    if (getOperation()!=null) 
    {
      int count=getOperationCount();
      
      //Write each <operation> below portType
      for (int i=0; i<count; i++) 
      {
        Operation o = getOperation(i);
        writer.startElement("operation", Constants.WSDL_NAMESPACE);
        writer.attribute("name", o.getName());

        if (o.getInputMessage()!=null) 
        {
          writer.startElement("input", Constants.WSDL_NAMESPACE);
          if (o.getInputMessageName()!=null)
            writer.attribute("name", o.getInputMessageName());
          writer.attribute("message",null, o.getInputMessage().getName().getLocalPart(),
          o.getInputMessage().getName().getNamespaceURI());
          writer.endTag();
        }
        if (o.getOutputMessage()!=null) 
        {
          writer.startElement("output", Constants.WSDL_NAMESPACE);
          if (o.getOutputMessageName()!=null)
            writer.attribute("name", o.getOutputMessageName());
          writer.attribute("message", null,o.getOutputMessage().getName().getLocalPart(),
          o.getOutputMessage().getName().getNamespaceURI());
          writer.endTag();
        }
        //Write the </operation>
        writer.endTag();
      } //for
    } //if
    //write the </portType>
    writer.endTag();
  } //toWSDL
  
  
} /*class PortType*/
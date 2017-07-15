package com.wingfoot.wsdl;

import com.wingfoot.*;
import com.wingfoot.wsdl.soap.*;
import java.util.*;
import org.kxml.io.*;

/**
 * Binding is the necessary step of associating a 
 * transport protocol and a data format with a 
 * bunch of messages, operations and port types.
 * In other words, binding nails down the abstract
 * onto something concrete.
 */
public class Binding 
{
  private QName bindingName;
  private PortType portType;
  private Vector bindingOperation;
  private Extension bindingExtension;

  /**
   * Creates a Binding with the given name for a
   * previously defined portType.
   * @param bindingName QName for the binding.
   * @param portType the PortType to associate the
   * Binding with.
   */
  public Binding(QName bindingName, PortType portType )
  {
    this.bindingName=bindingName;
    this.portType=portType;
  }

  public QName getBindingName() 
  {
    return this.bindingName;
  }

  public PortType getPortType() 
  {
    return this.portType;
  }

  public BindingOperation[] getBindingOperation()
  {
    if (bindingOperation==null)
      return null;
    BindingOperation[] bo = new BindingOperation[bindingOperation.size()];
    for (int i=0; i<bindingOperation.size(); i++)
      bo[i]=(BindingOperation) bindingOperation.elementAt(i);
    return bo;
  }

  public BindingOperation getBindingOperation(Operation operation)
  {
    if (bindingOperation==null)
      return null;
    for (int i=0; i<bindingOperation.size(); i++)
    {
      BindingOperation bo=(BindingOperation) bindingOperation.elementAt(i);
      if (bo.getOperation().equals(operation))
        return bo;
    }//for
    return null;
  }//getBindingOperation

  public void setBindingOperation(BindingOperation newBindingOperation)
  {
    if (bindingOperation==null)
      bindingOperation=new Vector();
    bindingOperation.add(newBindingOperation);
  }

  public int getBindingOperationCount() 
  {
    if (bindingOperation==null)
      return 0;
    else
      return bindingOperation.size();
  }

  public BindingOperation getBindingOperation(int index) 
  {
    return (BindingOperation) bindingOperation.elementAt(index);
  }

  public Extension getBindingExtension()
  {
    return bindingExtension;
  }

  /**
   * Sets the extension.  Example is &ltsoap:binding&gt element
   */
  public void setBindingExtension(Extension newBindingExtension)
  {
    bindingExtension = newBindingExtension;
  }

  /**
   * Given an operation, determines if the
   * Operation style is RPC or Document.
   * @return boolean true if the operation style is
   * RPC; false if document.
   * @throws WSDLException if the Operation cannot
   * be found in the Binding.
   * 
   */
   public boolean isOperationRPC(Operation operation) 
   throws WSDLException
   {
     //Get the correct BidningOperation.
     BindingOperation bo=this.getBindingOperation(operation);
     if (bo.getExtension() instanceof SOAPOperation) 
     {
        int messageStyle=((SOAPOperation)bo.getExtension()).getStyle();
        if (messageStyle!=SOAPBinding.DOCUMENT&&messageStyle!=SOAPBinding.RPC) 
        {
          if (this.getBindingExtension() instanceof SOAPBinding)
            messageStyle=((SOAPBinding)this.getBindingExtension()).getStyle();
        }
        if (messageStyle==SOAPBinding.RPC)
          return true;
        else if (messageStyle==SOAPBinding.DOCUMENT)
          return false;
     }
     throw new WSDLException("ERROR_WSDL_024:"+Constants.ERROR_WSDL_024+" "
     +this.getBindingName() + " " + operation.getName());
   }//isOperationRPC 
   

  /**
   * Converts an instance of this Binding to
   * WSDL
   * @param writer the XMLWriter to write the WSDL to.
   */
  public void toWSDL(XMLWriter writer) 
  {
    writer.startElement("binding", Constants.WSDL_NAMESPACE);
    writer.attribute("name", this.getBindingName().getLocalPart());
    writer.attribute("type", null, this.getPortType().getName().getLocalPart(),
    this.getPortType().getName().getNamespaceURI());

    //Write the <soap:binding> element
    if (this.getBindingExtension()!=null &&
    this.getBindingExtension() instanceof SOAPBinding) 
    {
      this.getBindingExtension().toWSDL(writer);
    }

    //Write the <wsdl:operation> element
    for (int i=0; i<this.getBindingOperationCount(); i++) 
    {
      this.getBindingOperation(i).toWSDL(writer);
    } //for

    //Write the </binding>
    writer.endTag();
  } //toWSDL
} //class Binding
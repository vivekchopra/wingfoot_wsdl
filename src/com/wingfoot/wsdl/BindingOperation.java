package com.wingfoot.wsdl;
import com.wingfoot.*;
import org.kxml.io.*;

public class BindingOperation 
{
  private Operation operation;
  private Extension extension;
  private MessageFormat[] inputMessageFormat;
  private MessageFormat[]  outputMessageFormat;
  private MessageFormat[] faultMessageFormat;
  
  public BindingOperation(Operation operation)
  {
    this.operation=operation;
  }

  public Operation getOperation()
  {
    return operation;
  }

  public Extension getExtension()
  {
    return extension;
  }

  /**
   * Sets the soap:operation element (SOAPOperation class)
   */
  public void setExtension(Extension newExtension)
  {
    extension = newExtension;
  }

  public MessageFormat[] getInputMessageFormat()
  {
    return inputMessageFormat;
  }

  public void setInputMessageFormat(MessageFormat[] newInputMessageFormat)
  {
    inputMessageFormat = newInputMessageFormat;
  }

  public MessageFormat[] getOutputMessageFormat()
  {
    return outputMessageFormat;
  }

  public void setOutputMessageFormat(MessageFormat[] newOutputMessageFormat)
  {
    outputMessageFormat = newOutputMessageFormat;
  }

  public MessageFormat[] getFaultMessageFormat()
  {
    return faultMessageFormat;
  }

  public void setFaultMessageFormat(MessageFormat[] newFaultMessageFormat)
  {
    faultMessageFormat = newFaultMessageFormat;
  }

  /**
   * Converts an instance of this BindingOperation to
   * WSDL
   * @param writer the XMLWriter to write the WSDL to.
   */
   public void toWSDL(XMLWriter writer) 
   {
     if (getOperation()!=null)
    {
        writer.startElement("operation", Constants.WSDL_NAMESPACE);
        writer.attribute("name", getOperation().getName());

        //Write the extension. This is usually the <soap:operation> element
        if (this.getExtension()!=null)
          this.getExtension().toWSDL(writer);

        //Write the input message format
        if (getInputMessageFormat()!=null) 
        {
          writer.startElement("input", Constants.WSDL_NAMESPACE);
          if (operation.getInputMessageName()!=null)
            writer.attribute("name", operation.getInputMessageName());
          for (int i=0; i<inputMessageFormat.length; i++) 
          {
            inputMessageFormat[i].toWSDL(writer);
          } //for
          //write the </wsdl:input>
          writer.endTag();
        } //inputMessageFormat

        if (getOutputMessageFormat()!=null) 
        {
          writer.startElement("output", Constants.WSDL_NAMESPACE);
          if (operation.getOutputMessageName()!=null)
            writer.attribute("name", operation.getOutputMessageName());
          for (int i=0; i<outputMessageFormat.length; i++) 
          {
            outputMessageFormat[i].toWSDL(writer);
          } //for
          //write the </wsdl:output>
          writer.endTag();
        }
        //write the </wsdl:operation>
        writer.endTag();
    } //if (getOperation()!=null)
   } //toWSDL
}
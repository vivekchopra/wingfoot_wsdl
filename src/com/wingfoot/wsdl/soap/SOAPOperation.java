package com.wingfoot.wsdl.soap;
import com.wingfoot.*;
import com.wingfoot.wsdl.*;
import org.kxml.io.*;
public class SOAPOperation implements Extension
{
  private String soapAction;
  private int style;

  public SOAPOperation()
  {
  }

  public String getSoapAction()
  {
    return soapAction;
  }

  public void setSoapAction(String newSoapAction)
  {
    soapAction = newSoapAction;
  }

  public int getStyle()
  {
    return style;
  }

  public void setStyle(int newStyle)
  {
    style = newStyle;
  }
    /**
   * Converts instances of this class 
   * to a WSDL stub.  The stub is written to the
   * XMLWriter
   * @param writer XMLWriter to write the stub to.
   */
  public void toWSDL(XMLWriter writer)
  {
    writer.startElement("operation", Constants.WSDL_SOAP_NAMESPACE);
    
    if (getSoapAction()==null)
      writer.attribute("soapAction", "");
    else
      writer.attribute("soapAction", getSoapAction());
      
    if (getStyle()==SOAPBinding.DOCUMENT)
      writer.attribute("style", "document");
    else if (getStyle()==SOAPBinding.RPC)
      writer.attribute("style", "rpc");
    writer.endTag();
  } //toWSDL
} //SOAPOperation
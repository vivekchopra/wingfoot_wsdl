package com.wingfoot.wsdl.soap;
import com.wingfoot.*;
import com.wingfoot.wsdl.*;
import org.kxml.io.*;

public class SOAPBinding implements Extension
{
  public static final int RPC=1;
  public static final int DOCUMENT=2;
  private int style=SOAPBinding.DOCUMENT;
  private String transport;

  /**
   * Returns the style of the SOAP payload.
   */
  public int getStyle()
  {
    return style;
  }

  /**
   * Sets the style of the SOAP payload.  It is
   * either RPC or Document.  The legal values are
   * SOAPBinding.RPC or SOAPBinding.DOCUMENT.
   * <p>
   * The default is SOAPBinding.DOCUMENT.
   * @param newStyle int representing the style of
   * the SOAP payload
   * @throws WSDLException if the style is invalid.
   */
  public void setStyle(int newStyle) throws WSDLException
  {
    if (newStyle!=SOAPBinding.DOCUMENT &&
        newStyle!=SOAPBinding.RPC) 
        {
          throw new WSDLException ("ERROR_WSDL_008:" + Constants.ERROR_WSDL_008);
        }
    style = newStyle;
  }

  public String getTransport()
  {
    return transport;
  }

  public void setTransport(String newTransport)
  {
    transport = newTransport;
  }

  /**
   * Converts  instances of SOAPBinding
   * to a WSDL stub.  The stub is written to the
   * XMLWriter
   * @param writer XMLWriter to write the stub to.
   */
  public void toWSDL(XMLWriter writer)
  {
    writer.startElement("binding", Constants.WSDL_SOAP_NAMESPACE);
    writer.attribute("style", this.getStyle()==this.RPC ? "rpc":"document");
    if (getTransport()!=null)
      writer.attribute("transport", this.getTransport()); 
    //Write the </soap:binding> tag
    writer.endTag();
  } //toWSDL
}
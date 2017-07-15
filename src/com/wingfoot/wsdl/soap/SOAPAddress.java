package com.wingfoot.wsdl.soap;
import com.wingfoot.wsdl.*;
import com.wingfoot.*;
import org.kxml.io.*;

public class SOAPAddress implements Extension
{
  private String address;

  public SOAPAddress()
  {
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String newAddress)
  {
    address = newAddress;
  }

  public String toString() 
  {
    return getAddress();
  }

    /**
   * Converts  instances of SOAPAddress
   * to a WSDL stub.  The stub is written to the
   * XMLWriter
   * @param writer XMLWriter to write the stub to.
   */
  public void toWSDL(XMLWriter writer)
  {
    writer.startElement("address", Constants.WSDL_SOAP_NAMESPACE);
    writer.attribute("location", getAddress());
    writer.endTag();
  } //toWSDL
} //class
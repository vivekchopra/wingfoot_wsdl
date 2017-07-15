package com.wingfoot.wsdl.soap;
import com.wingfoot.*;
import com.wingfoot.wsdl.*;
import org.kxml.io.*;

public class SOAPMessage implements Extension, MessageFormat
{
  public static final int LITERAL=1;
  public static final int ENCODED=2;
  public static final int HEADER =3;
  public static final int BODY =4;
  public static final int HEADERFAULT =5;
  public static final int FAULT=6;
  
  private String[] parts;
  private int use;
  private String encodingStyle=Constants.SOAP_ENCODING_STYLE;
  private String namespaceURI;
  private int messageType;

  /**
   * Creates instances of SOAPMessage.  
   * @param messageType.  Identifies the kind of
   * MessageFormat being encapsulated.  It is one
   * of SOAPMessage.HEADER, SOAPMessage.BODY,
   * SOAPMessage.FAULT or SOAPMessage.HEADERFAULT
   */
  public SOAPMessage(int messageType) throws WSDLException
  {
    if (messageType!=HEADER && messageType!=BODY &&
        messageType!=HEADERFAULT && messageType!=FAULT)
          throw new WSDLException ("ERROR_WSDL_010:" + Constants.ERROR_WSDL_010);
    this.messageType=messageType;
  }

  public int getMessageType() 
  {
    return this.messageType;
  }

  public String[] getParts()
  {
    return parts;
  }

  /**
   * List of Part names that must appear in the
   * SOAP Body.
   */
  public void setParts(String[] newParts)
  {
    parts = newParts;
  }

  public int getUse()
  {
    return use;
  }

  /**
   * Sets the encoding of the SOAP payload.  The two
   * possible values are literal and encoded.  If encoded
   * the encoding style must be provided else SOAP Section 5
   * encoding is used.
   * @param newUse legal values are SOAPBody.LITERAL or SOAPBody.ENCODED.
   * @throws WSDLException if newUse has an illegal value.
   */
  public void setUse(int newUse) throws WSDLException
  {
    if (newUse != LITERAL &&
        newUse != ENCODED)
          throw new WSDLException("ERROR_WSDL_009:"+Constants.ERROR_WSDL_009);
    use = newUse;
  }

  public String getEncodingStyle()
  {
    return encodingStyle;
  }

  public void setEncodingStyle(String newEncodingStyle)
  {
    encodingStyle = newEncodingStyle;
  }

  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  public void setNamespaceURI(String newNamespaceURI)
  {
    namespaceURI = newNamespaceURI;
  }

    /**
   * Converts concrete instances of this interface
   * to a WSDL stub.  The stub is written to the
   * XMLWriter
   * @param writer XMLWriter to write the stub to.
   */
  public void toWSDL(XMLWriter writer)
  {
    if (messageType==this.BODY) 
    {
        writer.startElement("body", Constants.WSDL_SOAP_NAMESPACE);
        if (use==this.ENCODED) 
        {
          writer.attribute("use", "encoded");
          writer.attribute("encodingStyle", this.encodingStyle);
        }
        else if (use==this.LITERAL)
          writer.attribute("use", "literal");
            
        if (this.namespaceURI !=null)
          writer.attribute("namespace", this.namespaceURI);
            
        //write the </soap:body>
        writer.endTag();
    } //if body
  } //toWSDL
} //class SOAPMessage
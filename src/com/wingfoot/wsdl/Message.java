package com.wingfoot.wsdl;
import com.wingfoot.*;
import java.util.*;
import org.kxml.io.*;
/**
 * Message is a collection of parameters.  A Message can
 * be used as an input to a service or could be an output
 * from a service.  The parameter(s) that constitute the
 * message are called Part.
 */
public class Message 
{
  private QName qname;
  private Vector part=null;
  /**
   * Creates a Message.  The name of the Message is
   * mandatory.  The name is mandatory and must be
   * unique amongst all Messages in the WSDL.
   * @param qname the QName of the Message.
   * @throws WSDLException if the name in the qname
   * is null.
   */
  public Message(QName qname) throws WSDLException
  { 
    if (qname.getLocalPart()==null)
      throw new WSDLException("ERROR_WSDL_001:"+Constants.ERROR_WSDL_001);
    this.qname=qname;
  } /*Message*/

  /**
   * Returns the name of the message.
   */
  public QName getName() 
  {
    return qname;
  }

  /**
   * Sets the message part.  A Message contains one or
   * more parts.  Each part is a parameter to a method
   * or a return data from a method.
   * @param part instance of Part.
   * @throws WSDLException if the part name is not unique
   * in the Message.
   */
  public void setMessagePart(Part part)
    throws WSDLException
  {
    if (this.part==null)
      this.part=new Vector();
    for (int i=0; i<this.part.size(); i++) 
    {
      Part p = (Part) this.part.elementAt(i);
      if (p.equals(part))
        throw new WSDLException("ERROR_WSDL_004:"+Constants.ERROR_WSDL_004+":"+getName()
          +":"+part.getPartName());
    }
    this.part.add(part);
  }

  /**
   * Set the Message parts.  This method
   * replaces any previous Part set using
   * the setMessagePart(Part) method.
   * @throws WSDLException if the part name is not unique
   * in the Message.
   */
  public void setMessagePart(Part[] part) 
    throws WSDLException
  {
    if (part==null)
      this.part=null;
    else 
    {
      for (int i=0; i<part.length; i++) 
      {
        this.setMessagePart( part[i]);
      } //for
    } //else
  }

  /**
   * Returns all Parts for this Message.
   * @return array of Part.
   */
  public Part[] getMessagePart() 
  {
    if (this.part ==null)
      return null;
    Part[] p = new Part[this.part.size()];
    for (int i=0; i<part.size(); i++) 
    {
      p[i]=(Part)this.part.elementAt(i);
    }
    return p;
  }

  /**
   * Returns the Part at the specified index.
   * @param index of the Vector that contains
   * the desired Part (starting from 0).
   * @return Part the part at the index.
   */
  public Part getMessagePart(int index) 
  {
    return (Part)part.elementAt(index);
  }

  /**
   * Returns the number of Part in this Message.
   * @return int the number of Part in the Message;
   * 0 if none.
   */
  public int getMessagePartCount() 
  {
    if (part==null)
      return 0;
    else
      return part.size();
  }

  /**
   * Two Messages are considered equal if their QName
   * (set in the constructor) have the same namespace AND
   * the same localPart.
   * @param message instance of Message whose QName has to
   * be compared.
   * @return if the QNames are identical.
   */
  public boolean equals(Message message) 
  {
    if (message.getName().equals(this.getName()))
      return true;
    else
      return false;
  }

  /**
   * Converts an instance of Message into a WSDL.
   * @param writer instance of XMLWriter.  The WSDL
   * is written to this XMLWriter.
   * @throws IOException if an error occurs while
   * writing to the writer.
   */
  public void toWSDL(XMLWriter writer) 
  {
    writer.startElement("message", Constants.WSDL_NAMESPACE);
    writer.attribute("name", this.getName().getLocalPart());
    for (int i=0; i<getMessagePartCount(); i++) 
    {
      Part p = getMessagePart(i);
      p.toWSDL(writer);
    }
    writer.endTag();
  } //toWSDL
} /*class*/
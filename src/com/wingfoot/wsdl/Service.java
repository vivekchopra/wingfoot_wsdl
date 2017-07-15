package com.wingfoot.wsdl;
import java.util.Vector;
import org.kxml.io.*;
import com.wingfoot.*;

public class Service 
{
  private String name;
  private Vector port;
  public Service(String name)
  {
    this.name=name;
  }

  public String getName() 
  {
    return name;
  }

  /**
   * Returns the Port that is bound to the
   * input binding name; null if no such
   * Port exists.
   */
  public Port getPort(QName bindingName) 
  {
    if (this.port==null)
      return null;
    for (int i=0; i<port.size(); i++) 
    {
      Port p = (Port)port.elementAt(i);
      if (p.getBindingName().equals(bindingName))
        return p;
    }//for
    return null;
  }//getPort
  public Port[] getPort()
  {
    if (port==null)
      return null;
    Port[] portArray = new Port[port.size()];
    for (int i=0; i<port.size(); i++)
      portArray[i]=(Port)port.elementAt(i);
    return portArray;
  }

  public void setPort(Port[] newPort)
  {
    if (newPort==null)
      this.port=null;
    else 
    {
      port=new Vector();
      for (int i=0; i<newPort.length; i++) 
        port.add(newPort[i]);
    }
  }

  public void setPort(Port port) 
  {
    if (this.port==null)
      this.port=new Vector();
    this.port.add(port);
  }

  public void toWSDL(XMLWriter writer) 
  {
    writer.startElement("service", Constants.WSDL_NAMESPACE);
    if (name!=null)
      writer.attribute("name", name);

    if (this.port!=null) 
    {
      for (int i=0; i<port.size(); i++) 
      {
        Port aPort = (Port) port.elementAt(i);
        aPort.toWSDL(writer);
      } //for
    } //if
    writer.endTag();
  } //toWSDL
  
} //class Service
package com.wingfoot.wsdl;
import com.wingfoot.*;
import org.kxml.io.*;

public class Port 
{
  private String name;
  private QName bindingName;
  private Extension endPoint;
  public Port(String name, QName bindingName)
  {
    this.name=name;
    this.bindingName=bindingName;
  }

  public String getName() 
  {
    return name;
  }

  public QName getBindingName() 
  {
    return bindingName;
  }

  public Extension getEndPoint()
  {
    return endPoint;
  }

  /**
   * Sets the &ltsoap:address&gt element.
   */
  public void setEndPoint(Extension newEndPoint)
  {
    endPoint = newEndPoint;
  }

  public void toWSDL(XMLWriter writer) 
  {
    writer.startElement("port", Constants.WSDL_NAMESPACE);
    if (name!=null)
      writer.attribute("name", name);
    if (bindingName!=null)
      writer.attribute("binding", null, bindingName.getLocalPart(),
      bindingName.getNamespaceURI());
    if (endPoint!=null)
      endPoint.toWSDL(writer);
    writer.endTag();
  } //toWSDL
} //class
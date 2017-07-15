package com.wingfoot.wsdl;
import com.wingfoot.*;
import com.wingfoot.xml.schema.types.*;
import org.kxml.io.*;
import java.io.*;
/**
 * A Message consists of one or more Part.  A Part cannot
 * occur independently; it has to occurs inside a &ltmessage&gt.
 * A Part represents a parameter in a message.
 * <p>
 * A Part can either be an element or a type (simpleType or 
 * complexType)  The element or type attribute in the &ltpart&gt
 * defines the kind of Part.
 */
public class Part 
{
  public static final int ELEMENT=1;
  public static final int TYPE=2;
  private String partName;
  private TypeReference type;
  private int partType;
  private String javaTypeName;
  /**
   * Creates a Part.  A Part is either an element or a
   * type (simpleType or complexType).
   * @param partName the name of the Part. This name has
   * to be unique amongst all the Parts in a Message.
   * @param type encapsulates either an element or a 
   * type (simpleType or complexType).
   * @param partType either Part.ELEMENT or Part.TYPE.
   * @throws WSDLException if partName is null OR if
   * partType is not Part.ELEMENT or Part.TYPE.
   */
  public Part(String partName, TypeReference type, int partType)
    throws WSDLException
  {
    if (partType!=Part.ELEMENT && partType!=Part.TYPE)
      throw new WSDLException("ERROR_WSDL_002:"+Constants.ERROR_WSDL_002);
    if (partName==null)
      throw new WSDLException("ERROR_WSDL_003:"+Constants.ERROR_WSDL_003);
    this.partName=partName;
    this.type=type;
    this.partType=partType;
  } /*constructor*/

  public String getPartName() 
  {
    return partName;
  }

  public TypeReference getType() 
  {
    return type;
  }

  /**
   * The partType is either Part.ELEMENT or Part.TYPE.
   */
  public int getPartType() 
  {
    return partType;
  }

  /**
   * Compares two instances of Part.  Parts are
   * considered equal if their partName are
   * identical.
   * @param part the Part to compare to
   * @return boolean true if the Parts are equal;
   * false otherwise.
   */
  public boolean equals(Part part) 
  {
    if (part.getPartName().equals(this.getPartName()))
      return true;
    else
      return false;
  }

  /**
   * Converts instance of this Part into a WSDL stub.
   * @param writer the XMLWriter to write to.
   */
  public void toWSDL(XMLWriter writer) 
  {
    writer.startElement("part", Constants.WSDL_NAMESPACE);
    writer.attribute("name", this.getPartName());
    if (getPartType()==ELEMENT) 
      writer.attribute("element", null, 
      this.getType().getLocalPart(), this.getType().getNamespaceURI());
    else 
      writer.attribute("type", null, 
      this.getType().getLocalPart(), this.getType().getNamespaceURI());
    writer.endTag();
  } //toWSDL

  /**
   * Returns a String that contains the name of
   * the Java type for the Part.  This is set
   * during Java2WSDL and is used to generate
   * the deployment descriptor.
   * @returns String that contains the name of
   * the class that encapsulates the Part.
   */
  public String getJavaTypeName()
  {
    return javaTypeName;
  }

    /**
   * Sets a String that contains the name of
   * the Java type for the Part.  This is set
   * during Java2WSDL and is used to generate
   * the deployment descriptor.
   * @param newJavaTypeName that contains the name of
   * the class that encapsulates the Part.
   */
  public void setJavaTypeName(String newJavaTypeName)
  {
    javaTypeName = newJavaTypeName;
  }
  
} /*class*/
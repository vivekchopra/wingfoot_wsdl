package com.wingfoot.xml.schema.types;
import com.wingfoot.xml.schema.groups.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.*;
import java.util.*;
import org.kxml.io.*;

/**
 * Encapsulates a complex type definition.  A complex type definition is either
 * <li> a restriction of a complex base type definition;
 * <li> an extenstion of a simple or complex base type definition
 * <li> a restriction of the ur-type definition (default)
 * <p>
 */

public class ComplexType extends Component implements Type
{

  private String name;
  private String targetNamespace;
  private Content content;
  private boolean isScopeGlobal;
  private Vector component;
  /**
   * Creates an instance of an ComplexType.  If the QName is null, the
   * complex type is treated as an anonymous complex type.  
   * @param name name encapsulating the name 
   * of the complex type definition; null if the complex type is anonymous
   * @param targetNamespace the targetNamespace of the element.
   * @param content encapsulates the content on the complex type.
   * The content is either a &ltsimpleContent&gt or &ltcomplexContent&gt.  A complex
   * type is derived either by extending or restricting a simpleContent or
   * complexContent.
   * @param isScopeGlobal boolean that determines if the type can be used
   * by all elements in the schema.  Complex type definition that has <schema>
   * element as the ancestor are treated as global elements.
   * @throws SchemaException if the content is null
   */
  public ComplexType(String name, String targetNamespace,
  Content content, boolean isScopeGlobal)
          throws SchemaException
  {
  
      if (content==null)
        throw new SchemaException ("ERROR_SCHEMA_012:"+Constants.ERROR_SCHEMA_012);
      this.name=name;
      this.targetNamespace=targetNamespace;
      this.content=content;
      this.isScopeGlobal=isScopeGlobal;
  }

  /**
   * Returns the name of the complexType (without the targetNamespace)
   * @return String the name of the complexType, null if the type is
   * anonymous
   */
  public String getName() 
  {
    return name;
  }

	public boolean isAnonymous() {
    if (this.name==null)
      return true;
    else
      return false;
	}

  /**
   * Returns the targetNamespace of the complexType; null if not
   * qualified or if the type is anonymous.
   * @return String the targetNamespace.
   */
  public String getTargetNamespace() 
  {
    return targetNamespace;
  }

  /**
   * Returns the content model of the complexType.  It could
   * be either a simpleContent or a complexContent.
   * @return Content the content model of the complex type.
   */
  public Content getContent() 
  {
    return this.content;
  }

  /**
   * Returns the scope of the complex type.
   * @return boolean true if the scope is global, false otherwise.
   */
  public boolean isScopeGlobal() 
  {
    return this.isScopeGlobal;
  }

  /**
   * Returns the String representation of the complexType.
   * @return String the string representation as targetNamespace:name; null
   * if the complextype is anonymous.
   */
  public String toString() 
  {
    if (name==null)
        return null;
    else
      return targetNamespace+":"+name;
  }  

  /**
	 * Converts the data type encapsulated to a XML.
	 * @param writer instance of XMLWriter to write 
   * the XML to
   * @throws Exception if an error occurs while
   * writing the XML.
	 */
  
	public void toXML(XMLWriter writer) 
  {
    writer.startElement("complexType", Constants.SOAP_SCHEMA);
    if (! isAnonymous())
      writer.attribute("name", getName());

    if (getContent()!=null)
      getContent().toXML(writer);

    if (component!=null) 
    {
      for (int i=0; i<component.size(); i++)
        ((Component) component.elementAt(i)).toXML(writer);
    }
    
    //write the </complexType> element
    writer.endTag();
  }

  /**
   * A complexType may have an attribute or attributeGroup in its
   * body.  This method allows to set such a component.
   * @param Component that is either a Attribute or AttributeGroup.
   * @throws SchemaException if the component is not Attribute
   * or AttributeGroup.
   */
  public void setAttributeAndAttributeGroup(Component component) throws SchemaException
  {
    if (!(component instanceof com.wingfoot.xml.schema.Attribute ||
    component instanceof com.wingfoot.xml.schema.groups.AttributeGroupDefinition)) 
    {
      throw new SchemaException("ERROR_SCHEMA_035:" + Constants.ERROR_SCHEMA_035+
      ":" + name);
    }
    if (this.component==null)
      this.component=new Vector();
    this.component.add(component);
  }

  public Vector getAttributeAndAttributeGroup() 
  {
    return component;
  }

  /**
   * Determines if the ComplexType encapsulates an array.
   * Arrays (per WSDL 1.1) are complexTypes that are a
   * restriction on SOAP-ENC:Array
   * @return true if the complexType encapsulates an
   * array; false if not.
   */
  public boolean isArray() 
  {
    if (this.getContent().getDerivation()==Content.RESTRICTION)
    {
      if (getContent().getBaseType().getName().equals("Array") &&
          getContent().getBaseType().getTargetNamespace().equals(Constants.SOAP_ENCODING_STYLE))
            return true;
    }
    return false;
  }

  public boolean isLiteralArray()
  {
    if (this.getContent()!=null && this.getContent().getDerivation()==Content.RESTRICTION &&
    this.getContent() instanceof ComplexContent)
    {
      ComplexContent cc=(ComplexContent)this.getContent();
      if (cc.getContentCount()>0) 
      {
        Vector v = cc.getContentList();
        if (v.elementAt(0) instanceof ModelGroupImplementation)
        {
          ModelGroupImplementation mgi = (ModelGroupImplementation)v.elementAt(0);
          List l = mgi.getContent();
          if (l!=null && l.get(0) instanceof Element &&
          ((Element)l.get(0)).getMaxOccurs()>1)
          {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Determines if the ComplexType encapsulates a Hashtable
   * or Hashmap.
   * @return true if ComplexType encapsulates a Hashtable
   */
  public boolean isHashMap()
  {
    boolean isHashMap = false;

    if(this.getContent().getDerivation() == Content.RESTRICTION &&
    this.name!=null)
    {
      if((this.name.equals("Map") ||
      this.name.equals("Hashtable")) &&
      this.targetNamespace.equals(Constants.DEFAULT_NAMESPACE))
        isHashMap = true;
    }
    return isHashMap;
    
  }
   
  /**
   * Determines if the ComplexType encapsulates an Vector
   * Vectors have a targetnamespace of Constants.SOAP_SCHEMA
   * @return true if the complexType encapsulates vector
   */
  public boolean isVector()
  {
    boolean isVector = false;
    if(this.getContent().getDerivation() == Content.RESTRICTION)
    {
    if(name!=null && this.name.equals("Vector") &&
       this.targetNamespace.equals(Constants.DEFAULT_NAMESPACE))
      isVector = true;
    }
    return isVector;
  }

  /**
   * Retrieves all the Attributes that are part of 
   * the ComplexType.  If the ComplexType is an
   * extension and the extended type has attributes,
   * they are also included.
   * @return Attribute[] array of Attribute or null
   * if the ComplexType has no Attribute
   */
  public Attribute[] getAllAttributes() 
  {
    Vector returnVector = new Vector();
    //The two lines below was commented in v1.1.
    //The content might be null but there might
    //be attributes and attribueGroup (component)
    //defined.  This happens in an empty ComplexType.
    
    //if (this.getContent()==null)
      //return null;
    if (this.getContent() != null)
    {
      Vector contentList=this.getContent().getContentList();
      //Patch for version 1.1 as described above.
      //if (contentList==null)
        //return null;
      //Vector returnVector=new Vector();
      if (contentList!=null)
      {
        for (int i=0; i<contentList.size(); i++) 
        {
          if (contentList.elementAt(i) instanceof Attribute) 
            returnVector.add(contentList.elementAt(i));
          else if (contentList.elementAt(i) instanceof AttributeGroupDefinition) 
          {
            Attribute[] v = ((AttributeGroupDefinition)contentList.elementAt(i)).getAllAttributes();
            if (v!=null && v.length>0)
            {
                for (int j=0; j<v.length; j++)
                  returnVector.add(v[j]);
            }
          }//else
        }//for
      }//contentList!=null
    } //if getContent !=null
    if (this.getAttributeAndAttributeGroup()!=null && 
    this.getAttributeAndAttributeGroup().size()>0)
    {
      for (int i=0; i<getAttributeAndAttributeGroup().size(); i++) 
      {
        if (this.getAttributeAndAttributeGroup().elementAt(i) instanceof Attribute)
          returnVector.add(this.getAttributeAndAttributeGroup().elementAt(i));
        else if (this.getAttributeAndAttributeGroup().elementAt(i) instanceof
        AttributeGroupDefinition) 
        {
          Attribute[] a=((AttributeGroupDefinition)this.getAttributeAndAttributeGroup().elementAt(i)).
          getAllAttributes();
          if (a!=null && a.length>0) 
          {
            for (int j=0; j<a.length; j++)
              returnVector.add(a[j]);
          }
        }
      }//for
    }//if
    
    if (returnVector.size()==0)
      return null;
    Attribute a[] = new Attribute[returnVector.size()];
    for (int i=0; i<returnVector.size(); i++)
      a[i]=(Attribute)returnVector.elementAt(i);
    return a;
  }//getAllAttribute

  /**
   * Returns all the elements that are part of the complex type.
   * If the complex types is an extension of another complex type,
   * that elements of that complextype are also returned.
   * @return an array of Elements of the complexType; null if
   * there are no elements.
   */
  public Element[] getAllElements(WSDLHolder wsdlHolder)
  {
    Vector returnVector=new Vector();
    if (this.getContent()==null)
      return null;
    else if (this.getContent() instanceof SimpleContent)
      return null;
    else if (this.getContent() instanceof ComplexContent)
    {
      ComplexContent cc = (ComplexContent)this.getContent();
      if (cc.getContentList()==null || cc.getContentCount()==0)
        return null;
      Vector v = cc.getContentList();
      for (int i=0; i<v.size(); i++)
      {
        Element[] e=null;
        if (v.elementAt(i) instanceof ModelGroupImplementation)
           e = ((ModelGroupImplementation)v.elementAt(i)).getAllElements();
        else if (v.elementAt(i) instanceof ModelGroupDefinition)
        {
           ModelGroupDefinition mgd=
           wsdlHolder.getNormalizedModelGroupDefinition((ModelGroupDefinition)v.elementAt(i)); 
           e=mgd.getAllElements();
        }
        for (int j=0; e!=null && j<e.length; j++)
        {
          returnVector.add(e[j]);
        }
      }
    }
    if (returnVector.size()==0)
      return null;
    Element[] e = new Element[returnVector.size()];
    for (int i=0; i<returnVector.size(); i++)
      e[i]=(Element)returnVector.elementAt(i);
    return e;
  }//getAllElements
  
} /** class complexType **/

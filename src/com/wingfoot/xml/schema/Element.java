
package com.wingfoot.xml.schema;

/**
 * Encapsulates an Element.  An Element can
 * be either a SimpleType or ComplexType.
 */
import com.wingfoot.*;
import com.wingfoot.xml.schema.types.*;
import org.kxml.io.*;

public class Element extends Component {

	private QName name;
	private Type type;
	private int min=1;
	private int max=1;
	private String theDefault;
  private String fixed;
  private boolean nillable=false, isScopeGlobal=false, isReference=false;
  private boolean formQualified;
	/**
	 * Creates an Element.  The name is mandatory.
	 * If Type is null, it is defaulted to xsd:anyType.
	 */
	public Element (String name, String targetNamespace, Type type, boolean isScopeGlobal) 
  throws SchemaException {
		if (name==null)
			throw new SchemaException("ERROR_SCHEMA_013:"+Constants.ERROR_SCHEMA_013);
    this.name=new QName(targetNamespace, name);
    if (type==null)
      this.type=new TypeReference(Constants.SOAP_SCHEMA, "ur-type");
		else
      this.type=type;
    this.isScopeGlobal=isScopeGlobal;
	}

	/**
	 * Creates an Element that has a reference
	 * to someother Element in the schema.
	 * @param ref the value of this parameter
	 * is a reference to an element in the schema.
	 * @throws SchemaException indicates that the
	 * value of ref is null.
	 */
	public Element(QName ref) 
		throws SchemaException {
		if (ref==null)
			throw new SchemaException("ERROR_SCHEMA_014:"+Constants.ERROR_SCHEMA_014+":"+toString());
		this.name=ref;
    this.isReference=true;
	}
  /**
   * Returns the name of the element.
   * @return String the element name.
   */
	public String getName() {
		return name.getLocalPart();
	}

  /**
   * Determines if this Element is a reference to
   * someother global element.
   * @return true if the Element is a reference; false
   * if not.
   */
  public boolean isReference() 
  {
    return this.isReference;
  }

  /**
   * Returns the scope of the element.  A global scope
   * indicates that the element is declared under
   * the &ltschema&gt element.
   */
  public boolean isScopeGlobal() 
  {
    return this.isScopeGlobal;
  }

  /**
   * Returns the targetnamespace of the element
   * @return String the targetnamespace
   */
   
	public String getNamespace() {
		return name.getNamespaceURI();
	}

	public Type getType() {
		return type;
	}

	public void setMinOccurs(int min) 
		throws SchemaException {
		if (min !=0 && min !=1)
			throw new SchemaException("ERROR_SCHEMA_015:"+Constants.ERROR_SCHEMA_015+":"+toString());
    this.min=min;
	}

	public int getMinOccurs() {
		return min;
	}

  /**
   * Sets the maxOccurs property for the element.
   * @int max the value of the maxOccurs property; unbounded
   * is represented as Integer.MAX_VALUE.
   */
	public void setMaxOccurs(int max)
		throws SchemaException {
		if (max<min || max<0)
			throw new SchemaException("ERROR_SCHEMA_016:"+Constants.ERROR_SCHEMA_016);
		this.max=max;
	}

	public int getMaxOccurs() {
		return max;
	}

	/**
	 * Sets the default for this Element. It is
	 * @param default the default value of this attribute
	 */

	public void setDefault(String theDefault) {
		this.theDefault=theDefault;
	}

	/**
	 * Returns the default for this Element.
	 * @return the default for this Element; null
	 * if no default is set.
	 */

	public String getDefault() {
		return this.theDefault;
	}

	/**
	 * Returns the value of the fixed property. 
	 * @param the value of the fixed property of
	 * the Element.
	 */ 
	public void setFixed(String fixed) {
		this.fixed=fixed;
	}

	public String getFixed() {
		return fixed;
	}

  public void setNillable(boolean nillable) 
  {
    this.nillable=nillable;
  }

  public boolean isNillable() 
  {
    return this.nillable;
  }

  public String toString() 
  {
    return this.name.toString();
  }
  
  /**
   * Converts the component to XML representation.
   * @param writer XMLWriter to write the XML to.
   */
  public void toXML(XMLWriter writer) 
  {
    writer.startElement("element", Constants.SOAP_SCHEMA);

    if (isReference())
      writer.attribute("ref", null, getName(), getNamespace());
    else
      writer.attribute("name", getName());
      
    if (getDefault()!=null)
      writer.attribute("default", this.getDefault());
    if (getFixed()!=null)
      writer.attribute("fixed", getFixed());
    if (getMinOccurs()!=1)
      writer.attribute("minOccurs", getMinOccurs()+"");
    if (getMaxOccurs() != 1) 
    {
      if (getMaxOccurs() == Integer.MAX_VALUE)
        writer.attribute("maxOccurs", "unbounded");
      else
        writer.attribute("maxOccurs", getMaxOccurs()+"");
    }
    if (isNillable())
      writer.attribute("nillable", "true");

    if (isFormQualified())
      writer.attribute("form", "qualified");
      
    if (this.getType() !=null) 
    {
      if (getType() instanceof XSDType) 
      {
        XSDType x = (XSDType) getType();
        writer.attribute("type", null, x.getType(), x.getSchemaURI());
      }
      else if (getType() instanceof TypeReference)
        writer.attribute("type", null, ((TypeReference)getType()).getLocalPart(),
        ((TypeReference)getType()).getNamespaceURI());
      else if (getType() instanceof SimpleType)
        ((SimpleType)getType()).toXML(writer);
      else if (getType() instanceof ComplexType)
        ((ComplexType)getType()).toXML(writer);
    }

    //end the <element> tag
    writer.endTag();
  } //toXML

  /**
   * Returns true if the form attribute in the
   * &ltelement&gt is set to qualified; false
   * if not set or set to unqualified.
   */
  public boolean isFormQualified()
  {
    return formQualified;
  }

  /**
   * True if the form attribute in the
   * &ltelement&gt is set to qualified; false
   * if not set or set to unqualified.
   */
  public void setFormQualified(boolean newFormQualified)
  {
    formQualified = newFormQualified;
  }
} /*class*/

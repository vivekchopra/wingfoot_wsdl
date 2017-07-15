
package com.wingfoot.xml.schema;

import com.wingfoot.*;
import com.wingfoot.xml.schema.types.*;
import org.kxml.io.*;
import java.util.*;
/**
 * Encapsulates an attribute or a reference to
 * an attribute.  An attribute is always of a
 * simple type.  The simple type is defined either
 * using the base attribute or using an inline
 * simple type
 * <p>
 * In an XML instance, an Attribute may occur once
 * or not at all.
 * <p> 
 * Note that an attribute declaration must appear
 * at the end of complex type definition.
 */
 public class Attribute extends Component {

	public static final int REQUIRED=1;
	public static final int OPTIONAL=2;
	public static final int PROHIBITED=3;
 	private QName name;
	private Type type;
	private  int use=Attribute.OPTIONAL;
	private String theDefault;
	private String fixed;
  private boolean isReference;
  private boolean isScopeGlobal;
  private boolean formQualified;
  private Hashtable additionalAttributes;
	/**
	 * Creates an instance of the Attribute.
	 * @param name QName of the attribute.
	 * The name is mandatory. If type is null
	 * it is defaulted to xsd:anyType.
	 * @param the namespace associated to this
	 * attribute; null if there is no namespace
   * @param isScopeGlobal true if the attribute
   * can be accessed by any element or attributeGroup;
   * false if not. Global attributes have the
   * &ltschema&gt element as its parent.
	 * @throws SchemaException if the name is null
	 * or type is not SimpleType or XSDType
	 */

	public Attribute(String name, String targetNamespace, Type type, boolean isScopeGlobal) 
			 throws SchemaException {

		if (name==null)
			throw new SchemaException("ERROR_SCHEMA_001"+Constants.ERROR_SCHEMA_001);
		if (type==null) {
      type=new XSDType(new QName(Constants.SOAP_SCHEMA, "ur-type"));
		}
		if (!(type instanceof SimpleType ||
			type instanceof XSDType ||
      type instanceof TypeReference))
			throw new SchemaException("ERROR_SCHEMA_002"+Constants.ERROR_SCHEMA_002);

		this.name=new QName(targetNamespace, name);
    this.type=type;
    this.isScopeGlobal=isScopeGlobal;
	} //Constructor

	/**
	 * Creates an Attribute that is a 
	 * reference to another Attribute in 
	 * the schema.
	 * @param ref the value of this parameter
	 * is a reference to another Attribute
	 * instance in the schema.
	 * @throws SchemaException if the ref
	 * attribute is null
	 */
	public Attribute(QName ref) 
		throws SchemaException {
		if (ref==null)
			throw new SchemaException("ERROR_SCHEMA_003"+Constants.ERROR_SCHEMA_003);
		this.name=ref;
    this.isReference=true;
	}

  /**
   * Returns the scope of the attribute.
   * @return boolean true if the scope is global
   * false if not global.
   */
  public boolean isScopeGlobal() 
  {
    return isScopeGlobal;
  }
  /**
   * Method to determine if the Attribute is a reference to
   * another global attribute declaration.  A reference
   * Attribute is created using the Attribut(String ref) 
   * constructor.
   * @return boolean true if it is a reference; false if not.
   */
  public boolean isReference() 
  {
    return isReference;   
  }

	public String getName() {
		return this.name.getLocalPart();
	}

	public String getNamespace() {
		return this.name.getNamespaceURI();
	}

	public Type getType() {
		return type;
	}

	/**
	 * Controls the occurance of the Attribute in 
	 * the XML instance.  The parameter is one of
	 * Attribute.REQUIRED, Attribute.OPTIONAL or
	 * Attribute.PROBIHITED.
	 * @param use the occurance parameter.  It is
	 * one of the final integers defined in this
	 * class.
	 * @throws SchemaException if the use is invalid OR
	 * if a default is set for the Attribute.
	 */
	public void setUse(int use) 
		throws SchemaException {
		if (use <1 || use>PROHIBITED)
			throw new SchemaException("ERROR_SCHEMA_004"+Constants.ERROR_SCHEMA_004);
		if (getDefault()!=null &&
        use != OPTIONAL)
			throw new SchemaException("ERROR_SCHEMA_005"+Constants.ERROR_SCHEMA_005);
		this.use=use;
	} //setUse

	/**
	 * Retruns the usage occurance of the Attribute.
	 * The default is optional.
	 * @return the usage occurance
	 */
	public int getUse() {
		return use;
	}

	/**
	 * Sets the default for this Attribute. It is
	 * an error to provide a default when the occurance
	 * is NOT Attribute.Optional.
	 * @param default the default value of this attribute
	 * @throws SchemaException if getUse method returns
	 * a value other than Attribute.OPTIONAL OR if
	 * the fixed property is set.
	 */

	public void setDefault(String theDefault) 
		throws SchemaException {
		if (theDefault!=null &&
		     this.getUse() != Attribute.OPTIONAL)
		     throw new SchemaException("ERROR_SCHEMA_005"+Constants.ERROR_SCHEMA_005);

		this.theDefault=theDefault;
	}

	/**
	 * Returns the default for this Attribute.
	 * @return the default for this Attribute; null
	 * if no default is set.
	 */

	public String getDefault() {
		return this.theDefault;
	}

	/**
	 * Returns the value of the fixed property. 
	 * @param the value of the fixed property of
	 * the Attribute.
	 * @throws SchemaException if the default is
	 * already fixed.
	 */
	public void setFixed(String fixed) 
		throws SchemaException  {
		if (getDefault()!=null &&
			fixed!=null) {
			throw new SchemaException("ERROR_SCHEMA_006"+Constants.ERROR_SCHEMA_006);
		}
		this.fixed=fixed;
	}

	public String getFixed() {
		return fixed;
	}

	/**
	 * Retruns the namespace:name representation
	 * of the Attribute.
	 * @return the String representation of the 
	 * Attribute.
	 */
	public String toString() {
		return name.toString();
	}

  /**
   * Converts the component to XML representation.
   * @param writer XMLWriter to write the XML to.
   */
  public void toXML(XMLWriter writer) 
  {
    writer.startElement("attribute", Constants.SOAP_SCHEMA);
    if (getDefault()!=null)
      writer.attribute("default", getDefault());
    if (getFixed()!=null)
      writer.attribute("fixed", getFixed());
    if (this.isFormQualified())
      writer.attribute("form", "qualified");
    if (this.isReference())
      writer.attribute("ref", null, getName(), this.getNamespace());
    else 
      writer.attribute("name", getName());
      /**
    if (this.getUse()==this.OPTIONAL)
      writer.attribute("use", "optional");
      **/
    if (this.getUse()==this.REQUIRED)
      writer.attribute("use", "required");
    else if (this.getUse()==this.PROHIBITED)
      writer.attribute("use", "prohibited");
    if (this.getType() instanceof XSDType ||
    this.getType() instanceof TypeReference)
      writer.attribute("type", null, this.getType().getName(), this.getType().getTargetNamespace());
    else if (getType() instanceof SimpleType)
      ((SimpleType)this.getType()).toXML(writer);

    if (additionalAttributes!=null) 
    {
      Enumeration keys=additionalAttributes.keys();
      while (keys.hasMoreElements()) 
      {
        QName aKey=(QName) keys.nextElement();
        QName aValue = (QName) additionalAttributes.get(aKey);
        writer.attribute(aKey.getLocalPart(), aKey.getNamespaceURI(),
        aValue.getLocalPart(), aValue.getNamespaceURI());
      } //while
    } //if

    writer.endTag();
  } //toXML

  public boolean isFormQualified()
  {
    return formQualified;
  }

  public void setFormQualified(boolean newFormQualified)
  {
    formQualified = newFormQualified;
  }

  /**
   * An &ltattribute&gt element iteself can have additional attributes.
   * This method sets such attributes.  An instance where such additional
   * attributes are needed is wsdl:arrayType for defining the default
   * data type for SOAP Section V array encoding
   * @param attrName QName of the attribute name.  If no namespace is
   * desired for the attribute name, leave it as null.
   * @param attrValue QName of the attribute name.  If no namespace is
   * desired for the attribute name, leave it as null.
   */
  public void setAdditionalAttributes(QName attrName, QName attrValue) 
  {
    if (additionalAttributes==null)
      additionalAttributes=new Hashtable();
    additionalAttributes.put(attrName, attrValue);
  }

  public void setAdditionalAttributes(Hashtable additionalAttributes) 
  {
    this.additionalAttributes=additionalAttributes;
  }
  /**
   * Returns any additonal attributes previously set.
   * @return Hashtable of any additional attributes;
   * null if none has been set.
   */
  public Hashtable getAdditionalAttributes() 
  {
    return additionalAttributes;
  }

 } /*Attribute*/

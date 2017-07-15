
package com.wingfoot.xml.schema.types;

import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import java.util.*;
import org.kxml.io.*;


/**
 * Encapsulates an atomic simple type.  Simple types define the data type using
 * the base attribute.  The value of the base attribute is either a xsd type or
 * another simple type.  If the base attribute is absent, the data type may be defined
 * by creating an inline simple type inside the simple type.
 * <p>
 * If a simple type definition has <schema> as its parent, then the scope is global;
 * otherwise the scope is local.
 * <p>
 * Currently the list and union types are not supported.  Only restriction is supported.
 */

public class SimpleType extends Component implements Type {

	private String name;
  private String targetNamespace;
	private Type dataType;
	private boolean isScopeGlobal;
	Hashtable facet;
	
	/**
	 * Creates an instance of SimpleType
	 * @param name the name of the simple type, 
	 * null if this is a anonymous type
   * @param targetNamespace the targetNamespace for
   * the simpleType.  Usually, it is always the
   * targetNamespace definied in the &ltschema&gt
	 * @param dataType the base type of the simple type.
   * It is either an instance of TypeReference or 
   * SimpleType or XSDType
	 * @param isScopeGlobal true if the simple type has
	 * <schema> element as its parent, false if not.
	 * @throws SchemaException if the base type of the 
	 * SimpleType is NOT a xsd type or another simple type.
	 */

	public SimpleType (String name,
                     String targetNamespace,
	                   Type dataType,
                    boolean isScopeGlobal) 
                    throws SchemaException {
		if (! (dataType instanceof XSDType || 
			dataType instanceof SimpleType ||
      dataType instanceof TypeReference))
			throw new SchemaException ("ERROR_SCHEMA_23:"+Constants.ERROR_SCHEMA_023+":"+name.toString());

		this.dataType=dataType;
		this.name=name;
    this.targetNamespace=targetNamespace;
		this.isScopeGlobal=isScopeGlobal;
	}

  /**
   * Returns the name of the SimpleType; null if
   * it is an anonymous type.
   * @return the name of the SimpleType
   */
	public String getName() {
    return name;
	}

  /**
   * Returns the targetnamespace associated with
   * the SimpleType; null is there is none
   * @return String the targetnamespace
   */
	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setFacet(Hashtable facet) {
		this.facet=facet;
	}

	public Hashtable getFacet() {
		return this.facet;
	}

	public boolean isScopeGlobal () {
		return this.isScopeGlobal;
	}
  
	public boolean isAnonymous() {
    return this.name==null?true:false;
	}

	/**
	 * Returns the data type of the 
   * SimpleType.  This is set
   * in the constructor.
   * @param Type the base type of the 
   * SimpleType
	 */
	public Type getDataType() {
		return dataType;
	}
	
	/**
	 * Returns a xsd:dataType representation of this
	 * simple type.  If this is a nested simple type 
	 * simple type with inline simple type, the simple
	 * type is chased down until a xsd base type is 
	 * encountered.
	 * @return String the xsd:type representation of 
	 * a simple type
	 */
	public String toString() {
		if (this.dataType instanceof XSDType) 
			return ((XSDType)dataType).toString();
    else if (this.dataType instanceof TypeReference &&
    this.dataType.getTargetNamespace().equals(Constants.SOAP_SCHEMA))
      return this.dataType.getTargetNamespace()+":"+this.dataType.getName();
		else {
			//This is a simple type. Chase down till
			// you get a xsd type.
			return ((SimpleType)dataType).toString();
		}
	} //toString

  /**
	 * Converts the data type encapsulated to a XML.
	 * @param writer instance of XMLWriter to write 
   * the XML to
   * @throws Exception if an error occurs while
   * writing the XML.
	 */
	public void toXML(XMLWriter writer) 
  {
    writer.startElement("simpleType", Constants.SOAP_SCHEMA);
    if (!this.isAnonymous())
      writer.attribute("name", getName());

    /**
     * Currently only restriction is supported.  When list
     * and union is supported, place a check here.
     */
     writer.startElement("restriction", Constants.SOAP_SCHEMA);
     if (getDataType() instanceof XSDType) 
     {
       XSDType x = (XSDType) getDataType();
       writer.attribute("base", null, x.getType(), x.getSchemaURI());
     }
     else if (getDataType() instanceof TypeReference) 
     {
       TypeReference x = (TypeReference) getDataType();
       writer.attribute("base", null, x.getLocalPart(), x.getNamespaceURI());
     }
     else if (getDataType() instanceof SimpleType)
     {
       SimpleType x = (SimpleType) getDataType();
       x.toXML(writer);
     }
     //close the <restriction>
     writer.endTag();
     //close the <simpleType>
     writer.endTag();
  } //toXML

} /*class SimpleType */


package com.wingfoot.xml.schema.types;

import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import org.kxml.io.*;
/**
 * Encapsulates a simple type that is built in the XML Schema.
 */
public class XSDType /*extends Component*/ implements Type {

	private QName typeDefinition;

	/**
	 * Creates an instance of XSDType with a QName. 
	 * The namespaceURI identifies the XML schema 
	 * being used (2001 schema, 1999 schema etc) and
	 * the localPart identifies the data type.
	 * @param typeDefinition QName that encapsulates 
	 * the xsd type.
         */

	public XSDType(QName typeDefinition) {
		this.typeDefinition=typeDefinition;
	}

	/**
	 * Returns the xsd type that is encapsulated in 
	 * this instance of XSDType.
	 * @return String with the xsd type (string, int etc).
	 */
	public String getType () {
		return typeDefinition.getLocalPart();
	}

	/**
	 * Returns the URI representing the XML schema being used.
	 * @return String the schema being used.
	 */
	public String getSchemaURI () {
		return typeDefinition.getNamespaceURI();
	}

  /**
   * Identical to the getSchemaURI method.
   */
  public String getTargetNamespace() 
  {
    return typeDefinition.getNamespaceURI();
  }

  public String getName() 
  {
    return typeDefinition.getLocalPart();
  }

	/**
	 * Returns the built in simple type being encapsulated in the XML
	 * schema in namespace:localPart format.
	 * @param String the datatype being encapsulated.
	 */
	public String toString() {
		return typeDefinition.toString();
	}

} /* class*/

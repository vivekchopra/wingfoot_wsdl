package com.wingfoot.xml.schema;

import java.util.*;
import org.kxml.io.*;
/**
 * The parent of all XML Schema components. 
 */
public abstract class Component {

	private String annotation;
  private Hashtable attribute;
	/**
	 * Sets the annotation for the component.
	 * @param annotation the comment for the Component
	 */

	public void setAnnotation(String annotation) {
		this.annotation=annotation;
	}

	/**
	 * Retrives the annotation for the component.
	 * @return String the comment for the component
	 */

	public String getAnnotation() {
		return this.annotation;
	}

  /**
   * Sets the attribute(s) of the component.
   * @param attribute Hashtable with the atribute name
   * as the key and attribute value as the value for
   * the key
   */
  public void setAttributes(Hashtable attribute) 
  {
    this.attribute=attribute;
  }

  /**
   * Returns the Hashtable of attributes previously
   * set.
   * @param Hashtable name-value pairs of attributes;
   * null if none was specified.
   */
  public Hashtable getAttributes() 
  {
    return attribute;
  }

  /**
   * Returns the value of an attribute.
   * @return String the attribute value; null
   * if no attribute was specified or if the
   * attribute key is non existant.
   */
  public String getAttribute(String attributeName) 
  {
    if (this.attribute==null)
      return null;
    return (String) attribute.get(attributeName);
  }

  /**
   * Converts the component to XML representation.
   * This is an abstract method.  The concrete
   * implementation is in the subclass of this class.
   * @param writer XMLWriter to write the XML to.
   */
  public abstract void toXML(XMLWriter writer);
} /*class*/

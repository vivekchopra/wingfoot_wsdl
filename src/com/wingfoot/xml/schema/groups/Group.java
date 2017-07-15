
package com.wingfoot.xml.schema.groups;
/**
 * Defines the properties of a Group. Group definition
 * is either an AttributeGroupDefinition or a 
 * ModelGroupDefinition.
 */

 import com.wingfoot.xml.schema.*;
 import java.util.*;
 import org.kxml.io.*;

public interface Group  {

	/**
	 * Get the name of the Group.
	 * @return the name of the group.
	 */
	public String getName();

	/**
	 * Get the namespace of the group
	 * @param the namespace associated to the group
	 */
	public String getTargetNamespace();

	/**
	 * Provides the ability to add a content
	 * to the Group.
	 * @param content the content of the Group.
	 * Concrete implementations decide the specific
	 * content that is legally permissible.
   * @throws SchemaException if the Component is 
   * illegal (not of the expected type).
	 */
	public void setContent(Component content) throws SchemaException;

	/**
	 * Returns a List of contents of the Group.
	 * @return Vector the contents of the Group; null
	 * if there are no contents.
	 */
	public Vector getContent();

	/**
	 * The String representation of the Group
	 * @return String the String representation of the Group
	 */
	public String toString();

  public boolean isReference();
  
	public void toXML(XMLWriter writer);
} /*interface*/

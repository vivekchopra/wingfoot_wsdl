
package com.wingfoot.xml.schema.types;
//import org.kxml.io.*;

/**
 * An abstract class that all types implement.  
 * Any custom data type has to implement this 
 * interface.
 */

 public interface Type {

	/**
	 * Converts the data type encapsulated to a XML.
	 * @param writer instance of XMLWriter to write 
   * the XML to.
	 */
  
	//public void toXML(XMLWriter writer);

  public String getName();
  public String getTargetNamespace();

 }

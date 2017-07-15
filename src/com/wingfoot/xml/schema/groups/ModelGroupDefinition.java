
package com.wingfoot.xml.schema.groups;

import java.util.*;
import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import org.kxml.io.*;
/**
 * A model group definition associates a name with
 * a model group.  Model group definitions are 
 * identified by their name and targetNamespace; 
 * model group identities must be unique within an
 * XML Schema.
 */

public class ModelGroupDefinition extends Component
	implements Group {

	private QName name;
	private Vector content;
  private boolean isReference;

	/**
	 * Creates an instance of ModelGroupDefinition.
	 * Once created, the name and targetNamespace 
	 * of the model group definition cannot be changed.
	 * @param name the name of the ModelGroupDefinition. 
	 * The name is mandatory.
   * @param targetNamespace usually the value of the
   * targetNamespace attribute in the &ltschema&gt element.
	 * @param isReference true if the ModelGroupDefinition
   * to create is a reference to another ModelGroupDefinition;
   * false if not.
	 * @throws SchemaException if the name is null
	 */
	public ModelGroupDefinition(String name, String targetNamespace)
			      throws SchemaException {
		if (name==null)
			throw new SchemaException("ERROR_SCHEMA_017:"+Constants.ERROR_SCHEMA_017);

		this.name=new QName(targetNamespace, name);
    this.isReference=false;
	} /*ModelGroupDefinition*/

  /**
   * Creates a ModelGroupDefinition that is a reference
   * to another globally defined ModelGroupDefinition.
   * @param ref QName of the ModelGroupDefinition being
   * referred to.
   * @throws SchemaException if the ref is null.
   */
  public ModelGroupDefinition(QName ref) throws SchemaException 
  {
    if (ref==null)
      throw new SchemaException("ERROR_SCHEMA_017:"+Constants.ERROR_SCHEMA_017);
    this.name=ref;
    this.isReference=true;
  }

  /**
   * Determines if the ModelGroupDefinition is a 
   * reference to a previously defined ModelGroupDefinition.
   * @return boolean true if it is a reference; false
   * otherwise
   */
   public boolean isReference() 
   {
     return isReference;
   }
	/**
	 * Returns the name of the model group definition. 
	 * A name is required to create an instance of
	 * ModelGroupDefinition.
	 * @return String the name of the ModelGroupDefinition
	 */
	public String getName() {
		return name.getLocalPart();
	} /*getName*/

	/**
	 * Returns the targetNamespace of the 
	 * ModelGroupDefinition. The targetNamespace 
	 * is optional.
	 * @return String the targetNamespace; null if no
	 * namespace is associated to the ModelGroupDefinition.
	 */
	public String getTargetNamespace() {
		return name.getNamespaceURI();
	}

	/**
	 * An ModelGroupDefinition is a set of ModelGroup.  
	 * Provides the ability to add a ModelGroup to the
	 * ModelGroupDefinition
	 * @param content a ModelGroup to be added as
	 * a content to the ModelGroupDefinition
	 */
	public void setContent(Component content) throws SchemaException {
    if (!(content instanceof ModelGroupImplementation)) 
      throw new SchemaException("ERROR_SCHEMA_019:"+Constants.ERROR_SCHEMA_019+":"+toString());
		if (this.content==null)
			this.content=new Vector();
		this.content.add(content);
	}

	/**
	 * Retrieve the content of the ModelGroupDefinition
	 * @return Vector the content of the ModelGroupDefinition
	 * Each element of the List is an instance of
	 * ModelGroup; returns null if the content of the
	 * ModelGroupDefinition is empty.
	 */
	public Vector getContent() {
		return content;
	}

	/**
	 * Returns the String representation of the 
	 * ModelGroupDefinition.  The representation takes
	 * targetNamespace:name format.
	 * @return String the String representation of
	 * ModelGroupDefinition
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
    writer.startElement("group", Constants.SOAP_SCHEMA);

    if (this.isReference()) 
    {
      writer.attribute("ref", null, this.getName(), this.getTargetNamespace());
    }
    else 
    {
      writer.attribute("name", getName());
      if (content!=null && content.size()>0) 
      {
        for (int i=0; i<content.size(); i++) 
        {
          ((Component) content.elementAt(i)).toXML(writer);
        } //for
      } //if
    } //else
    //Write the </group> tag
    writer.endTag();
  } //toXML

  public Element[] getAllElements()
  {
    Vector v=new Vector();
    if (this.getContent()==null || this.getContent().size()==0)
      return null;
    for (int i=0; i<getContent().size(); i++)
    {
      if (content.elementAt(i) instanceof ModelGroupImplementation)
      {
        Element e[] = ((ModelGroupImplementation) content.elementAt(i)).getAllElements();
        for (int j=0; e!=null && j<e.length; j++)
          v.add(e[j]);
      }
    }
    if (v.size()==0)
      return null;
    Element e[] = new Element[v.size()];
    for (int i=0; i<v.size(); i++)
      e[i]=(Element)v.elementAt(i);
    return e;
  }
} /*class*/

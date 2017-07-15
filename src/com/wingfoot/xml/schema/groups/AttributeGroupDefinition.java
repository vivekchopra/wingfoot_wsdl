package com.wingfoot.xml.schema.groups;
import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import  java.util.*;
import org.kxml.io.*;
/**
 * A schema can name a group of attribute declaration
 * so that they may be incorporated as a group into
 * complex type definition.  This group is known as
 * Attribute Group definition.
 * <p>
 * When used in complex type definition, the effect is
 * as if the attribute declarations in the group were 
 * present in the type definition.
 * <p>
 * Attribute groups are identified by their name and
 * targetNamespace and must be unique within a schema.
 * <p> 
 * Note that an attribute group declaration must appear
 * at the end of complex type definition.
 * <p>
 * An attribute group definition might contain a ref attribute
 * that is a reference to a previously declared attribute group.
 * If the ref attribute is not present, it indicates that a new
 * attribute group is being defined.  The content of the
 * &ltattributeGroup&gt  is a set of &ltattribute&gt AND/OR
 * &ltattributeGroup&gt with the ref attribute.
 */

public class AttributeGroupDefinition 
	extends Component implements Group {

	private QName name;
	private Vector content;
  private boolean isReference=false;

	/**
	 * Creates an instance of AttributeGroupDefinition.
	 * Once created, the name and targetNamespace of the
	 * AttributeGroupDefinition cannot be changed.
	 * @param name Name of the AttributeGroupDefinition. 
	 * The name is mandatory.
   * @param targetNamespace String with the targetNamespace
   * defined in the &ltschema&gt element; null if none is
   * defined.
   * @param isReference true if the attribute group is 
   * a reference to another attribute group; false if
   * this is a new attribute group definition.
	 * @throws SchemaException if the name is null
	 */
	public AttributeGroupDefinition(String name, String targetNamespace)
			      throws SchemaException {
		if (name==null)
			throw new SchemaException("ERROR_SCHEMA_007"+Constants.ERROR_SCHEMA_007);
    this.name = new QName(targetNamespace, name);
    this.isReference=false;
	} /*AttributeGroup*/

  /**
   * Creates a AttributeGroupDefinition that is a reference
   * to another attribute group.  Such &ltattributeGroup&gt
   * have a ref attribute and DO NOT have a name attribute.
   * They DO NOT have any content model either.
   * @param ref QName of the AttributeGroupDefinition being
   * referred to.
   */
  public AttributeGroupDefinition(QName ref) 
  {
    this.name=ref;
    this.isReference=true;
  }

  /**
   * Determines if the AttributeGroupDefinition is a 
   * definition or a reference to another AttributeGroupDefinition.
   * @return boolean true is a reference; false otherwise.
   */
  public boolean isReference() 
  {
    return isReference;
  }
	/**
	 * Returns the name of the AttributeGroup.  A 
	 * name is required for an instance of 
	 * AttributeGroup to be successfully created.
	 * @return String the name of the AttributeGroup
	 */
	public String getName() {
		return this.name.getLocalPart();
	} /*getName*/

	/**
	 * Returns the targetNamespace of the AttributeGroup.
	 * The targetNamespace is optional.
	 * @return String the targetNamespace; null if no
	 * namespace is associated to the AttributeGroup.
	 */
	public String getTargetNamespace() {
		return this.name.getNamespaceURI();
	}

	/**
	 * An AttributeGroup is a group of attributes.
	 * Provides the ability to add an attribute to
	 * the attribute group.
	 * @param content an Attribute to be added as
	 * a content to the AttributeGroup.
   * @throws SchemaException if the content is 
   * other than Attribute or AttributeGroup.
	 */
	public void setContent(Component content) throws SchemaException {
    if (! (content instanceof Attribute || content instanceof AttributeGroupDefinition))
      throw new SchemaException("ERROR_SCHEMA_009"+Constants.ERROR_SCHEMA_009+toString());
		if (this.content==null)
			this.content=new Vector();
		this.content.add(content);
	}

  /**
   * Sets the content of the attributeGroup.  Any
   * previous content is completely lost.  
   * @param content a Vector of Attribute and 
   * AttributeGroup.  These are the only two valid
   * componenets in a attributeGroup.
   * @param content a Vector of components.
   * @throws SchemaException if an invalid content
   * model was detected.
   */
  public void setContent(Vector content)  throws SchemaException
  {
    if (content!=null) 
    {
      if (this.isReference())
        throw new SchemaException("ERROR_SCHEMA_034:" + Constants.ERROR_SCHEMA_034 + ":" + 
        getTargetNamespace() + ":" + getName());
        
      for (int i=0; i<content.size(); i++) 
      {
        Component c = (Component) content.elementAt(i);
        if (c!=null && 
        !(c instanceof Attribute || c instanceof AttributeGroupDefinition)) 
        {
          throw new SchemaException("ERROR_SCHEMA_009"+Constants.ERROR_SCHEMA_009+toString());
        }
        if (c instanceof AttributeGroupDefinition) 
        {
          AttributeGroupDefinition ag = (AttributeGroupDefinition) c;
          if (! ag.isReference()) 
          {
            throw new SchemaException("ERROR_SCHEMA_009"+Constants.ERROR_SCHEMA_009+toString());
          } //if
        } //if
      } //for
    } //if
    this.content=content;
  }

	/**
	 * Retrieve the content of the AttributeGroup
	 * @return Vector the content of the AttributeGroup
	 * Each element of the List is an instance of
	 * Attribute; returns null if the content of the
	 * AttributeGroup is empty.
	 */
	public Vector getContent() {
		return content;
	}

	/**
	 * Returns the String representation of the 
	 * AttributeGroup.  The representation takes
	 * targetNamespace:name format.
	 * @return String the String representation of
	 * AttributeGroup
	 */
	public String toString() {
    return this.name.toString();
	}

  /**
   * Converts the component to XML representation
   * @param writer XMLWriter to write the XML to.
   */
  public void toXML(XMLWriter writer) 
  {
    writer.startElement("attributeGroup", Constants.SOAP_SCHEMA);
    if (isReference())
      writer.attribute("ref", null, getName(), getTargetNamespace());
    else
      writer.attribute("name", getName());

    if (content!=null && content.size()>0) 
    {
      for (int i=0; i<content.size(); i++)
        ((Component)content.elementAt(i)).toXML(writer);
    }
    //Write the </attributeGroup> element.
    writer.endTag();
  } //toXML

  /**
   * Retrieves all the Attributes in the AttributeGroup.
   * If the AttributeGroup contains another AttributeGroup
   * then the nested AttributeGroup is recursed to include
   * all the Attributes.
   * @return Attribute[] array of all the Attributes in the
   * AttributeGroup.
   */
  public Attribute[] getAllAttributes() 
  {
    if (this.content==null || content.size()==0)
      return null;
    Vector returnVector=new Vector();
    for (int i=0; i<content.size(); i++) 
    {
      if (content.elementAt(i) instanceof Attribute) 
        returnVector.add(content.elementAt(i));
      else if (content.elementAt(i) instanceof AttributeGroupDefinition) 
      {
        Attribute v[] = ((AttributeGroupDefinition)content.elementAt(i)).getAllAttributes();
        if (v!=null && v.length >0)
        {
          for (int j=0; j<v.length; j++)
            returnVector.add(v[j]);
        }//if
      }//else
    }//for
    if (returnVector.size()==0)
      return null;
    Attribute a[] = new Attribute[returnVector.size()];
    for (int i=0; i<returnVector.size(); i++)
      a[i]=(Attribute)returnVector.elementAt(i);
    return a;
  }//getAllAttributes
} /*class*/

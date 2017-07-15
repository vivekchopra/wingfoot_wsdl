package com.wingfoot.xml.schema.types;
import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.groups.*;
import java.util.*;
import org.kxml.io.*;

/**
 * Defines a &ltsimpleContent&gt content model for a complex type.
 * A simpleContent content model is used to create a complex type
 * that extends or restricts an existing SimpleType or XSDType
 * and defines one or more attributes.
 */
public class SimpleContent implements Content
{
  private int derivation=0;
  private TypeReference baseType=new TypeReference(Constants.SOAP_SCHEMA, "ur-type");
  private Hashtable facet;
  private Vector content;

  /**
   * Creates a SimpleContent.
   * @param derivation either Content.RESTRICTION or Content.EXTENSION.
   * @throws SchemaException is the derivation is not
   * Content.EXTENSION or Content.RESTRICTION.
   */
  public SimpleContent(int derivation) throws SchemaException
  {
    if (derivation!=Content.EXTENSTION &&
        derivation!=Content.RESTRICTION)
          throw new SchemaException("ERROR_SCHEMA_21:"+Constants.ERROR_SCHEMA_021);
    this.derivation=derivation;
  }

  /**
   * Returns the derivation of the SimpleContent.
   * @return int the derivation; it is either Content.RESTRICTION 
   * or Content.EXTENSION.
   */
  public int getDerivation() 
  {
    return this.derivation;
  }

  /**
   * Returns the base type of the content model.  After the
   * &ltsimpleContent&gt element, a &ltrestriction&gt or an
   * &ltextension&gt element is required.  This element 
   * identifies the base type (via the base attribute) that 
   * is being extended or restricted.
   * @return Type the base type of the content model.
   */
  public TypeReference getBaseType()
  {
    return baseType;
  }

  /**
   * Sets the base type of the content model.  The content model 
   * either restricts or extends this base type.  In a schema after the
   * &lt;simpleContent&gt; element, a &lt;restriction&gt; or an
   * &lt;extension&gt; element is required.  This element 
   * identifies the base type (via the base attribute) that 
   * is being extended or restricted.
   * @param newBaseType Type encapsulating the base type that
   * is restricted or extended.
   */
  public void setBaseType(TypeReference newBaseType)
  {
    baseType = newBaseType;
  }

  /**
   * Retrives the facets for the simpleContent.
   * @return Hashtable name value pair of facets; null if none
   * is defined.
   */
  public Hashtable getFacet()
  {
    return facet;
  }

  /**
   * Sets the facet for the SimpleContent. For a <simpleContent> the
   * facets are applicable only if the derivation is Content.RESTRICTION.
   * @param name value pairs of facets.
   * @throws SchemaException if the derivation is not Content.RESTRICTION
   */
  public void setFacet(Hashtable newFacets) throws SchemaException
  {
    if (derivation != Content.RESTRICTION)
      throw new SchemaException("ERROR_SCHEMA_22:"+Constants.ERROR_SCHEMA_022);
    facet = newFacets;
  }

  /**
   * Adds a component to the conetnt.  For simpleCompoent the
   * following rules apply
   * <li> if the derivation is Content.EXTENSION then the 
   * legal Component is either a SimpleType, Attribute or 
   * AttributeGroupDefinition;
   * <li> if the derivation is Content.RESTRICTION then the
   * legal Component is either a SimpleType, Attribute or
   * AttributeGroupDefinition;
   * <p>
   * If the content for the simpleContent is empty (ie this
   * method is never called) OR if the content contains only
   * Attribute and AttributeGroup then the content defaults
   * to the value of the base attribute (ie the value specified
   * using the setType attribute).
   * @param attribute the attribute to add to the content model
   * @throws SchemaException if the newComponent is an illegal
   * component.  The legal components are listed above.
   */
  public void addContent(Component newContent) throws SchemaException
  {
    if (!(newContent instanceof SimpleType ||
    newContent instanceof com.wingfoot.xml.schema.Attribute ||
    newContent instanceof AttributeGroupDefinition)) 
    {
      throw new SchemaException("ERROR_SCHEMA_027:"+Constants.ERROR_SCHEMA_027);
    }
    if (this.content==null)
      this.content=new Vector();
    this.content.add(newContent);
  }

  /**
   * Adds a Vector of content to  the content list.
   * Any existing content is overriden.
   * @param content A list of Components that constitute
   * the content.
   */
  public void addContentList(Vector content) 
  {
    this.content=content;
  }

  public Vector getContentList() 
  {
    return content;
  }

  /**
   * Returns the count of Components in 
   * the content model.
   * @return int the number of Components;
   * 0 if there is none.
   */
  public int getContentCount() 
  {
    return this.content==null?0:content.size();
  }  

  /**
   * Converts a &ltsimpleContent&gt to a XML stub.
   * @param writer XMLWriter to write the XML
   * stub to.
   */
  public void toXML(XMLWriter writer) 
  {
    writer.startElement("simpleContent", Constants.SOAP_SCHEMA);
    writer.startElement(derivation==Content.EXTENSTION?"extension":"restriction",
    Constants.SOAP_SCHEMA);
    writer.attribute("base",null,getBaseType().getName(), getBaseType().getTargetNamespace());

    if (content!=null && content.size()>0) 
    {
      for (int i=0; i<content.size(); i++) 
        ((Component)content.elementAt(i)).toXML(writer);
    }

    //write </extension> or </restriction>
    writer.endTag();
    //write </simpleContent>
    writer.endTag();
  } //toXML
} /*SimpleContent*/
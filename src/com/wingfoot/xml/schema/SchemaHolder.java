package com.wingfoot.xml.schema;

import java.util.*;
import java.io.*;
import org.kxml.io.*;
import com.wingfoot.*;
import com.wingfoot.xml.*;
import com.wingfoot.xml.schema.types.*;
import com.wingfoot.xml.schema.groups.*;

/**
 * Encapsulates the properties of a well formed XML schema.
 * A XML schema has many individual components.  The get and
 * set methods allow for addition or removal of components from
 * the schema.
 */
 
public class SchemaHolder implements XMLHolder
{
  private Vector content;
  private Hashtable attribute;
  private String targetNamespace,defaultNamespace;
  private boolean isAttributeQualified=true, isElementQualified=true;  
  private String destination;
  private Vector importedSchema;
  
  /**
   * Creates an instance of SchemaHolderInstance.
   */
  public SchemaHolder()
  {
  }

  /**
   * Sets the attributes that appear in the &ltschema&gt element.
   * @parameter attribute Hashtable of name value pairs.
   */
  public void setSchemaAttributes(Hashtable attribute) 
  {
    this.attribute=attribute;
  }

  /**
   * Returns name value pairs of the attributes that appear
   * at the top level &ltschema&gt element.
   * @return Hashtable name value pair of the attributes; null
   * if none is specified.
   */
  public Hashtable getSchemaAttributes() 
  {
    return attribute;
  }

  /**
   * Returns the value of a schema attribute.
   * @return String value of the attribute; null if the
   * attribute is not present in the &ltschema&gt element.
   */
  public String getAttributeValue(String attributeName) 
  {
    if (attribute==null || attributeName==null)
      return null;
    else
      return (String) attribute.get(attributeName);
  }
  /**
   * Adds a Component to the schema.
   * @param component the Component to add to the schema
   */
  public void setComponent(Component component) 
  {
    if (component!=null) 
    {
      if (content==null)
        content=new Vector();
      content.add(component);
      
    }
  }

  /**
   * Replaces any existing components in the schema with
   * the Vector of components passed in.  A null parameter
   * is equivalent to removing all the components from the
   * schema.
   * @param components a List of components
   */
  public void setComponents(Vector components) 
  {
    content=components;
  }

  /**
   * Returns back a List of components in the schema.
   * @return Vector the list of components or null if there are
   * no components in the schema.
   */
  public Vector getComponents() 
  {
    return content;
  }

  /**
   * Returns back the Component at the specified index.
   * @param index the index (starting from zero) of the component
   * to return;
   * @return Component the component at the specified index; null
   * if the index is greater than the value returned from 
   * the getComponentCount method.
   */
  public Component getComponent(int index) 
  {
    if (content==null ||
        (index+1)>content.size())
        return null;
    else
      return (Component)content.elementAt(index);
  }

  /**
   * Returns the number of components in the schema.
   * @return the number of components in the schema; zero
   * if there are no components specified.
   */
  public int getComponentCount() 
  {
    if (content==null)
      return 0;
    else
      return content.size();
  }

  /**
   * Retrieves the targetnamespace for the
   * XML schema.
   * @return String the targetnamespace for the
   * schema.
   */
  public String getTargetNamespace()
  {
    return targetNamespace;
  }

  /**
   * Sets the targetnamespace for the schema.
   * @param newTargetNamespace the targetnamespace
   * for the XML schema.
   */
  public void setTargetNamespace(String newTargetNamespace)
  {
    targetNamespace = newTargetNamespace;
  }

  /**
   * Returns true if locally declared attributes are to
   * be qualified; false otherwise.
   * @boolean true or false.
   */
  public boolean isAttributeQualified()
  {
    return isAttributeQualified;
  }

  /**
   * Sets the attributeFormDefault attribute in the
   * &ltschema&gt element.  If set to true (default behavior)
   * then all locally declared attributes in the instance is
   * qualified with namespace.  If set to false the locally
   * declared attributes are not qualified.
   * <p>
   * For better interoperability, it is recommended that
   * this be set to true.
   * @param newIsAttributeQualified true if local attributes
   * are to be qualified with namespace; false otherwise.
   */
  public void setAttributeQualified(boolean newIsAttributeQualified)
  {
    isAttributeQualified = newIsAttributeQualified;
  }

  /**
   * Returns true if locally declared attributes are to
   * be qualified; false otherwise.
   * @boolean true or false.
   */
   
  public boolean isElementQualified()
  {
    return isElementQualified;
  }

  /**
   * Sets the elementFormDefault attribute in the
   * &ltschema&gt element.  If set to true (default behavior)
   * then all locally declared elements in the instance is
   * qualified with namespace.  If set to false the locally
   * declared elements are not qualified.
   * <p>
   * For better interoperability, it is recommended that
   * this be set to true.
   * @param newIsElementQualified true if local elements
   * are to be qualified with namespace; false otherwise.
   */
  public void setElementQualified(boolean newIsElementQualified)
  {
    isElementQualified = newIsElementQualified;
  }

  /**
   * Retrieves the default namespace of the XML Schema
   * @return String the default namespace.
 
  public String getDefaultNamespace()
  {
    return defaultNamespace;
  }
  */
  /**
   * Sets the default namespace for the XML Schema
   * If none is specified, the default namespace is
   * http://www.w3.org/2001/XMLSchema.
   * @param newDefaultNamespace the default namespace

  public void setDefaultNamespace(String newDefaultNamespace)
  {
    defaultNamespace = newDefaultNamespace;
  }
     */
  /**
   * Converts SchemaHolder to XML and writes to the resource
   * specified in the setDestination method.
   * @return SerializedHolder the SchemaHolder converted to
   * XML and stored in SerializedHolder.  The client may
   * use the getter methods in SerializedHolder to writer
   * to a physical resource.
   * @throws SchemaException if an error occurs while
   * processing the schema.
   * @throws UnsupportedEncodingException if the host platform
   * does not support UTF-8 encoding.
   */
   
  public SerializedHolder[] toXML() throws SchemaException, UnsupportedEncodingException
  {
    if (this.getDestination()==null)
      throw new SchemaException("ERROR_SCHEMA_036:" + Constants.ERROR_SCHEMA_036);
    Hashtable defaultNS=null;
    defaultNS=new Hashtable();
    if (getTargetNamespace()!=null)
      defaultNS.put(getTargetNamespace(), "tns");
    XMLWriter aWriter = new XMLWriter(defaultNS);
    this.toXML(aWriter);
    return new SerializedHolder[] {
    new SerializedHolder(aWriter.getPayload("utf-8"), this.getDestination())};
  }
  
  /**
   * Converts the SchemaHolder and all its components
   * to XML.
   * @param writer XMLWriter to write the XML to.
   */
  public void toXML(XMLWriter writer) 
  {
    writer.startElement("schema", Constants.SOAP_SCHEMA);
    if (getTargetNamespace()!=null)
      writer.attribute("targetNamespace", getTargetNamespace());
    writer.attribute("xmlns", Constants.SOAP_SCHEMA);
    if (isElementQualified)
      writer.attribute("elementFormDefault", "qualified");
    if (isAttributeQualified)
      writer.attribute("attributeFormDefault", "qualified");

    /**
     * If there are any imports, this is a good time to
     * write them.
     */
     if (this.importedSchema!=null) 
     {
       for (int i=0; i<importedSchema.size(); i++) 
       {
         String[] s=(String[]) importedSchema.elementAt(i);
         writer.startElement("import",Constants.SOAP_SCHEMA);
         if (s[0]!=null)
          writer.attribute("namespace", s[0]);
         if (s[1]!=null)
          writer.attribute("location", s[1]);
         writer.endTag();
       }
     }
    
    /**
     * Start processing individual components that
     * constitute this schema.
     */
     int count = getComponentCount();
     for (int i=0; i<count; i++)
      this.getComponent(i).toXML(writer);

     writer.endTag();
  }

  public String getDestination()
  {
    return destination;
  }

  public void setDestination(String newDestination)
  {
    destination = newDestination;
  }

  /**
   * Check the SchemaHolder to determine if a global
   * ComplexType exists.  Global complex types are
   * defined directly underneath the &lt;schema&gt;
   * element.
   * @param name the name of the complexType.
   * @param namespace the targetNamespae of the
   * complexType.
   * @return ComplexType the ComplexType that has
   * the same name as the parameter passed in; returns
   * null if a ComplexType with the name and namespace
   * does not exist.
   */
   public ComplexType getComplexType(String name, String namespace) 
   {
     if (content==null)
      return null;
     for (int i=0; i<content.size(); i++) 
     {
       if (content.elementAt(i) instanceof ComplexType)
       {
         ComplexType c = (ComplexType)content.elementAt(i);
         if (c.getName().equals(name) &&
         c.getTargetNamespace().equals(namespace))
          return c;
       } //if
     } //for
     return null;
   } //getComplexType

  /**
   * Check the SchemaHolder to determine if a global
   * Eleemnt exists.  Global elements are
   * defined directly underneath the &lt;schema&gt;
   * element.
   * @param name the name of the Element.
   * @param namespace the targetNamespace of the
   * Element.
   * @return Element the Element that has
   * the same name as the parameter passed in; returns
   * null if a Element with the name and namespace
   * does not exist.
   */
   public Element getElement(String name, String namespace) 
   {
       if (content==null)
        return null;
       for (int i=0; i<content.size(); i++) 
       {
         if (content.elementAt(i) instanceof Element)
         {
           Element c = (Element)content.elementAt(i);
           if (c.getName().equals(name) &&
           c.getNamespace().equals(namespace))
            return c;
         } //if
       } //for
       return null;
   }//getElement

   /**
    * Limited support is available for schema imports.
    * This methods allows for specifying the namespace
    * and location of the schema.  This appears as
    * part of &lt;import&gt; element in the schema 
    * definition.
    * <p>
    * If an attempt is made to add a duplicate namespace,
    * the operation fails.
    * @param namespace the namespace to associate with
    * the schema.
    * @param location the physical location of the schema.
    */
   public void setImport(String namespace, String location) 
   {
     if (namespace!=null || location!=null)
     {
       if (this.importedSchema==null) importedSchema=new Vector();
       /**
        * Make sure the namespace is not already present.
        */
       int i=0;
       for (; i<importedSchema.size(); i++) 
       {
         String s[] = (String[]) importedSchema.elementAt(i);
         if (s[0].equals(namespace))
          break;
       }
       if (i==importedSchema.size())
        importedSchema.add(new String[] {namespace, location});
     }
   }

   /**
    * Returns a Vector of imported schemas if any was
    * specified.
    * @return Vector of schemas.  Each element of the 
    * Vector is a String[]. Index 0 contains the namespace
    * and index 1 contains the location; returns null
    * if no schema import was specified.
    */
   public Vector getImport() 
   {
     return importedSchema;
   }

   /**
    * Given a Attribute (a Component) that is a reference
    * to another to another attribute, retrieves the 
    * Attribute that is being referred to.
    * <p>
    * If the refAttribute is not a reference then refAttribute
    * is returned back.
    * <p>
    * If refAttribute is null or if the reference to the attribute
    * is not present in this SchemaHolder, null is returned.
    * <p>
    * If the referenced attribute is present in this SchemaHolder
    * then the referenced Attribute is returned.
    * @param refAttribute Attribute that has a ref element in it.
    * @return Attribute the referenced Attribute.
    */
    public Attribute getNormalizedAttribute(Attribute refAttribute) 
    {
      if (this.content==null||refAttribute==null)
        return null;
      if (!refAttribute.isReference())
        return refAttribute;
      
      for (int i=0; i<content.size(); i++) 
      {
        if (content.elementAt(i) instanceof Attribute &&
            !((Attribute)content.elementAt(i)).isReference())
        {
          Attribute a = (Attribute) content.elementAt(i);
          if (refAttribute.getNamespace()==null && a.getNamespace()==null &&
          refAttribute.getName().equals(a.getName()))
            return a;
          else if (refAttribute.getNamespace().equals(a.getNamespace()) &&
          refAttribute.getName().equals(a.getName()))
            return a;
        } //if
      }//for
      return null;
    } //getNormalizedAttriute

   /**
    * Given a AttributeGroupDefinition (a Component) that is a reference
    * to another to another attribute, retrieves the 
    * AttributeGroupDefinitoin that is being referred to.
    * <p>
    * If the refAttribute is not a reference then refAttribute
    * is returned back.
    * <p>
    * If refAttribute is null or if the reference to the attribute
    * is not present in this SchemaHolder, null is returned.
    * <p>
    * If the referenced attribute is present in this SchemaHolder
    * then the referenced AttributeGroupDefinition is returned.
    * @param refAttribute AttributeGroupDefinition that has a ref element in it.
    * @return AttributeGroupDefinition the referenced AttributeGroupDefinition.
    */
    public AttributeGroupDefinition getNormalizedAttributeGroupDefinition
    (AttributeGroupDefinition refAttribute) 
    {
      if (this.content==null||refAttribute==null)
        return null;
      if (!refAttribute.isReference())
        return refAttribute;
      
      for (int i=0; i<content.size(); i++) 
      {
        if (content.elementAt(i) instanceof AttributeGroupDefinition &&
            !((AttributeGroupDefinition)content.elementAt(i)).isReference())
        {
          AttributeGroupDefinition a = (AttributeGroupDefinition) content.elementAt(i);
          if (refAttribute.getTargetNamespace()==null && a.getTargetNamespace()==null &&
          refAttribute.getName().equals(a.getName()))
            return a;
          else if (refAttribute.getTargetNamespace().equals(a.getTargetNamespace()) &&
          refAttribute.getName().equals(a.getName()))
            return a;
        } //if
      }//for
      return null;
    } //getNormalizedAttriute

   /**
    * Given a Element (a Component) that is a reference
    * to another to another Element, retrieves the 
    * Element that is being referred to.
    * <p>
    * If the refElement is not a reference then refElement
    * is returned back.
    * <p>
    * If refElement is null or if the reference to the element
    * is not present in this SchemaHolder, null is returned.
    * <p>
    * If the referenced element is present in this SchemaHolder
    * then the referenced Element is returned.
    * @param refElement Element that has a ref element in it.
    * @return Element the referenced Element.
    */
    public Element getNormalizedElement(Element refElement) 
    {
      if (refElement==null||content==null)
        return null;
      if (!refElement.isReference())
        return null;
      for (int i=0; i<content.size(); i++) 
      {
        if (content.elementAt(i) instanceof Element &&
        !(((Element)content.elementAt(i)).isReference())) 
        {
          Element e = (Element) content.elementAt(i);
          if (refElement.getName().equals(e.getName())) 
          {
            if (refElement.getNamespace()==null && e.getNamespace()==null)
              return e;
            else if (refElement.getNamespace().equals(e.getNamespace()))
              return e;
          }//if
        }//if
      }//for
      return null;
    }//getNormalizedElement
    
   /**
    * Given a ModelGroupDefinition (a Component) that is a reference
    * to another to another ModelGroupDefinition, retrieves the 
    * ModelGroupDefinition that is being referred to.
    * <p>
    * If the mgd is not a reference then mgd
    * is returned back.
    * <p>
    * If mgd is null or if the reference to the element
    * is not present in this SchemaHolder, null is returned.
    * <p>
    * If the referenced ModelGroupDefinition is present in this SchemaHolder
    * then the referenced ModelGroupDefinition is returned.
    * @param mgd ModelGroupDefinition that has a ref attribute in it.
    * @return ModelGroupDefinition the referenced ModelGroupDefinition.
    */
    public ModelGroupDefinition getNormalizedModelGroupDefinition(ModelGroupDefinition mgd) 
    {
      if (mgd==null || content==null)
        return null;
      if (!mgd.isReference())
        return null;
      for (int i=0; i<content.size(); i++) 
      {
        if (content.elementAt(i) instanceof ModelGroupDefinition &&
        !(((ModelGroupDefinition)content.elementAt(i)).isReference())) 
        {
          ModelGroupDefinition m =(ModelGroupDefinition) content.elementAt(i);
          if (mgd.getName().equals(m.getName())) 
          {
            if (mgd.getTargetNamespace()==null && m.getTargetNamespace()==null)
              return m;
            else if (mgd.getTargetNamespace().equals(m.getTargetNamespace()))
              return m;
          }
        }
      }//for
      return null;
    }//getModelGroupDefinition
    
} /*SchemaHolder*/
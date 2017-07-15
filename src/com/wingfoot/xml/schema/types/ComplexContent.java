package com.wingfoot.xml.schema.types;

import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.groups.*;
import java.util.*;
import org.kxml.io.*;

/**
 * Defines a <complexContent> content model for a complex type.
 * A complexContent content model is used to create a complex type
 * extends (adds new elements and/or attributes) OR restrict (removes
 * elements from) an existing Type.
 * <p>
 * The default base type for a ComplexContent is a "ur-type".
 */
public class ComplexContent implements Content
{
  private int derivation;
  TypeReference baseType=new TypeReference(Constants.SOAP_SCHEMA, "ur-type");
  Vector content;

  /**
   * Creates a ComplexContent content model.
   * @param derivation one of Content.EXTENSION or 
   * Content.RESTRICTION.
   */
  public ComplexContent(int derivation) throws SchemaException
  {
    if (derivation != Content.EXTENSTION &&
        derivation != Content.RESTRICTION)
          throw new SchemaException("ERROR_SCHEMA_010"+Constants.ERROR_SCHEMA_010);
    this.derivation=derivation;
  }
  
  /**
   * Returns the derivation of the SimpleContent.
   * @return int the derivation; it is either Content.RESTRICTION 
   * or Content.EXTENSION.
   */
  public int getDerivation() 
  {
    return derivation;
  }

  /**
   * Returns the base type of the content model
   * @return Type the base type.
   */
  public TypeReference getBaseType()
  {
    return baseType;
  }

   /**
   * Sets the base type of the content model.  The content model 
   * either restricts or extends this base type.
   * @param newBaseType.
   */
  public void setBaseType(TypeReference newBaseType)
  {
    baseType = newBaseType;
  }

  /**
   * Sets a component as a content of the complexContent. This method
   * is called iteratively to set multiple components in the complex
   * content.  Valid components in a complexContent are AttributeGroupDefinition,
   * Attribute, ModelGroup and ModelGroupDefinition
   * @param component that is part of the complex content.
   * @throws SchemaException if the component is not one of AttributeGroup,
   * Attribute, ModelGroup or ModelGroupDefinition.
   */
  public void addContent(Component component) throws SchemaException 
  {
    if (!(component instanceof AttributeGroupDefinition ||
        component instanceof com.wingfoot.xml.schema.Attribute ||
        component instanceof ModelGroupImplementation ||
        component instanceof ModelGroupDefinition))
          throw new SchemaException("ERROR_SCHEMA_011"+Constants.ERROR_SCHEMA_011);
    if (this.content==null)
      this.content=new Vector();
    this.content.add(component);
  }

  /**
   * Adds a Vector of content to  the content list.
   * Any existing content is overriden.  The Vector
   * contains a list of Components.
   * @param content A list of Components that constitute
   * the content.
   */
  public void addContentList(Vector content) 
  {
    this.content=content;
  }
  
  /**
   * Returns the content  of the complexContent.
   * @return Vector the content; null if there is none.
   */
  public Vector getContentList()
  {
    return content;
  }

  public int getContentCount() 
  {
    return content==null?0:content.size();
  }

  /**
   * Convert the ComplexContent to XML.
   * @param writer instance of XMLWriter to
   * write the stub to.
   */
  public void toXML(XMLWriter writer) 
  {
    /**
     * Write the complexContent only if the derivation
     * is NOT restriction AND the base is NOT ur-type
     * This is because even the spec says it is the default,
     * it poses interop problems.
     */
     boolean closeComplexContent=false;
     
     if (!(this.getDerivation()==Content.RESTRICTION &&
     getBaseType().getName().equals("ur-type") &&
     getBaseType().getTargetNamespace().equals(Constants.SOAP_SCHEMA)))
     {
       closeComplexContent=true;
       writer.startElement("complexContent", Constants.SOAP_SCHEMA);
       if (this.getDerivation()==Content.EXTENSTION)
          writer.startElement("extension", Constants.SOAP_SCHEMA);
       else 
          writer.startElement("restriction", Constants.SOAP_SCHEMA);
       writer.attribute("base", null, getBaseType().getName(), 
       getBaseType().getTargetNamespace());
     }
    
    //Deserialize each component that is part of the content model
    if (content!=null && content.size()>0) 
    {
      for (int i=0; i<content.size(); i++)
        ((Component) content.elementAt(i)).toXML(writer);
    }

    if (closeComplexContent) 
    {
        //write the </restriction> or </extension>  
        writer.endTag();
        //write </complexContent>
        writer.endTag(); 
    }
  } //toWSDL
} //class
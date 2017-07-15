package com.wingfoot.xml.schema.types;
import com.wingfoot.xml.schema.*;
import java.util.*;
import org.kxml.io.*;
/**
 * Encapsulates the content model of a complex type.
 * The content is either is &ltsimpleContent&gt or a 
 * &ltcomplexContent&gt
 */
public interface Content 
{
  public static final int RESTRICTION=1;
  public static final int EXTENSTION=2;

  /**
   * Irrespctive of the type of the content model (&ltsimpleContent&gt or
   * &ltcomplexContent&gt) of a complexType, the actual definition is either
   * an extension or a restriction of a base type.  The base type is either
   * a simpleType or a complexType.
   * <p>
   * This method returns the derivation type of the Content.
   * @param int the derivation type.  It is either Content.RESTRICTION or
   * Content.EXTENSION.
   */
  public int getDerivation();

  /**
   * Sets the base type for the content model.  A complex type
   * is either an extension or a restriction on the some type.
   * This method identifies that base type.
   * @param newBaseType the base type being set.
   */
  public void setBaseType(TypeReference newBaseType);

  /**
   * Returns the base type previously set.
   * @return Type the base type.
   */
  public TypeReference getBaseType();

  /**
   * Adds the content to the content model.  Concrete 
   * implementations can decide the valid components.
   * A SchemaException is thrown if an illegal component
   * is passed in.
   */
  public void addContent(Component component) throws SchemaException;

  /**
   * Returns a List of Component(s) that encapsulates the
   * content.
   * @return Vector the content model; null if there is no content.
   */
  public Vector getContentList();

  /**
   * Returns the number of components in the content model.
   * @return int the number of components; 0 if the content
   * is empty.
   */
  public int getContentCount();

  /**
   * Adds a Vector of content to  the content list.
   * Any existing content is overriden.
   * @param content A list of Components that constitute
   * the content.
   */
  public void addContentList(Vector content);

  /**
   * Converts the Content to XML.
   * @param writer instance of XMLWriter to write
   * the XML to.
   */
  public void toXML(XMLWriter writer);
}
package com.wingfoot.xml.schema;
import org.kxml.parser.*;

/**
 * Interface that defines the properties of a SchemaReader.
 * A SchemaReader is used to parse a well formed XML schema
 * and convert to an object (Java) representation.  The object 
 * representation is an instance of SchemaHolder.  All
 * components of the xml schema (elements, attributes, groups etc)
 * are accessible using the getter methods in SchemaHolder.
 * <p>
 * An implementation of SchemaReader is provided.  This is 
 * the default reader.  Users can create their own schema readers
 * by implementing instance of this interface and providing
 * a reference to the new class in SchemaReaderFactory class
 */
public interface SchemaReader 
{
  /**
   * Takes a byte array representing a well formed XML schema
   * and returns back an instance of SchemaHolder that encapsulates
   * the schema into Components.  These Components can be accessed
   * using the getter methods in the SchemaHolder interface.
   * @param schemaPayload a byte representation of a well formed 
   * XML schema.
   * @return SchemaHolder Object representation of the XML schema.
   * @throws Exception if any error occurs; typically concrete 
   * implementations throw a subclass of the Exception class.
   */
  public SchemaHolder parse(byte[] schemaPayload) throws Exception;
  public SchemaHolder parse(XmlParser parser) throws Exception;
  
}
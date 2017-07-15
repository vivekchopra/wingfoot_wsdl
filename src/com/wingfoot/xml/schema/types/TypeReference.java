package com.wingfoot.xml.schema.types;
import com.wingfoot.QName;
import org.kxml.io.*;

/**
 * Encapsulates the data type that is present in the base
 * attribute.  A SimpleType or ComplexType can have the
 * its data type defined by using the base attribute that
 * points to a simpletype or complextype (whichever is legal)
 * declared elsewhere OR by using an inline type declaration.
 * <p>
 * This class encapsulates the content of the base attribute.
 */
public class TypeReference extends QName implements Type
{
  /**
   * Creates an instance of TypeReference.
   * @param namespaceURI - String with a URI identifying a URI
	 * @param localPart - String with the local part of the qualified name
   */
  public TypeReference(String namespaceURI, String localPart)
  {
    super(namespaceURI, localPart);
  }

  /**
   * Returns the localPart.
   */
  public String getName() 
  {
    return super.getLocalPart();
  }

  public String getTargetNamespace() 
  {
    return super.getNamespaceURI();
  }
} //class

package com.wingfoot;

/**
 * QName represents XML qualified name.  It is defined in
 * <a href="http://www.w3.org/TR/xmlschema-2/#QName">XML Schema Part 2: Datatypes specification</a>.
 * <p>
 * A QName consists of namespaceURI and localPart.  The namespaceURI is a URI identifying
 * a namespace.  The localPart provides the local part of the qualified name.
 * @author - Wingfoot Software.
 */

public class QName {

	private String namespaceURI;
	private String localPart;


	/**
	 * Creates an instance of QName with a namespace and local part.
	 * @param namespaceURI - String with a URI identifying a URI
	 * @param localPart - String with the local part of the qualified name
	 */
	public QName (String namespaceURI, String localPart) {
		this.namespaceURI=namespaceURI;
		this.localPart=localPart;
	} /*constructor*/

	/**
	 * Returns the URI identifying a namespace
	 * @return String the URI identifying a namespace; null if 
	 * no namespaceURI was specified.
	 */
	public String getNamespaceURI () {
		return namespaceURI;
	}

	/**
	 * Returns the localpart of the QName.
	 * @return String the local part of the qualified name.
	 */
	public String getLocalPart() {
		return localPart;
	}

	/**
	 * Returns a String representation of the QName.  
	 * <p>
	 * If namespaceURI is not null, the String returned is 
	 * namespaceURI:localPart else (if namespaceURI is null) 
	 * the String returned is localPart.
	 * @return String the string representation of the QName
	 */
	public String toString() {
		if (namespaceURI==null)
			return localPart;
		else
			return namespaceURI+":"+localPart;
	}

  /**
   * Two QNames are equal if their targetNamespace
   * and the localPart are identical.
   * @param qName the QName to comapre with the 
   * current instance of QName
   * @return boolean true if the QNames are identical;
   * false otherwise.
   */
  public boolean equals(Object object) 
  {
    if (!(object instanceof QName))
      return false;
    QName qName=(QName)object;
    if (qName.getNamespaceURI()==null && this.getNamespaceURI()==null &&
    qName.getLocalPart()==null && this.getLocalPart()==null)
      return true;
    else if (qName.getNamespaceURI()==null && this.getNamespaceURI()==null &&
    qName.getLocalPart()!=null && this.getLocalPart()!=null &&
    qName.getLocalPart().equals(this.getLocalPart()))
      return true;
    else if (qName.getNamespaceURI()!=null && this.getNamespaceURI()!=null &&
    qName.getLocalPart()==null && this.getLocalPart()==null &&
    qName.getNamespaceURI().equals(this.getNamespaceURI()))
      return true;
    else if (qName.getNamespaceURI().equals(this.getNamespaceURI()) &&
    qName.getLocalPart().equals(this.getLocalPart()))
      return true;
    else
      return false;
  } //equals
  
} /* class QName */

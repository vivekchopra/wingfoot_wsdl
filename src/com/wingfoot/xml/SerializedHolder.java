package com.wingfoot.xml;

  /**
   * Stores (as a byte array) the XML representation of
   * a XMLHolder along with the absolution location of
   * the resource where the XML representation is to be
   * written.
   * <p>
   * The WSDLWriter or the SchemaWriter never write to
   * a physical file.  An array of SerializedHolder is
   * returned back to the client.  The client is then
   * responsible to write to the resource.
   */
public class SerializedHolder implements java.io.Serializable 
{
  private byte[] xml;
  private String destination;
  /**
   * The only constructor.  Creates an instance
   * of SerializedHolder
   * @param xml a well formed xml, created by the core
   * API, that is either a WSDL or a XML Schema.
   */
  public SerializedHolder(byte[] xml, String destination)
  {
    this.xml=xml;
    this.destination=destination;
  }

  public byte[] getXML() 
  {
    return xml;
  }

  public String getDestination() 
  {
    return destination;
  }
}

package com.wingfoot.xml.schema.groups;
import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import java.util.*;
import org.kxml.io.*;
/**
 * A concrete implementation of ModelGroup.
 */

public class ModelGroupImplementation extends Component
	implements ModelGroup {

	private int modelGroupType;
	private Vector content;
  private int minOccurs=1;
  private int maxOccurs=1;

	public void setModelGroupType(int modelGroupType) {
		this.modelGroupType=modelGroupType;
	}

	public int getModelGroupType() {
		return modelGroupType;
	}

  /**
   * Sets the content of a ModelGroup.  Valid
   * contents are Element or a ModelGroup.
   * @param content either an Element or a ModelGroup.
   * @throws SchemaException if the content is neither
   * an Element or a ModelGroup.
   */
	public void setContent(Component content) throws SchemaException  {
    if (!(content instanceof Element ||
          content instanceof ModelGroup))
            throw new SchemaException("ERROR_SCHEMA_037:"+Constants.ERROR_SCHEMA_037);
		if (this.content==null)
			this.content=new Vector();
		this.content.add(content);
	}

	public List getContent() {
		return content;
	}

  public int getMinOccurs()
  {
    return minOccurs;
  }

  public void setMinOccurs(int newMinOccurs) throws SchemaException
  {
    if (newMinOccurs!=0 && newMinOccurs!=1)
      throw new SchemaException(Constants.ERROR_SCHEMA_015);
    minOccurs = newMinOccurs;
  }

  public int getMaxOccurs()
  {
    return maxOccurs;
  }

  public void setMaxOccurs(int newMaxOccurs) throws SchemaException
  {
    if (newMaxOccurs<0)
      throw new SchemaException(Constants.ERROR_SCHEMA_016);
    maxOccurs = newMaxOccurs;
  }

  /**
   * Converts the component to XML representation.
   * @param writer XMLWriter to write the XML to.
   */
  public void toXML(XMLWriter writer) 
  {
    if (getModelGroupType()==ModelGroup.ALL)
      writer.startElement("all", Constants.SOAP_SCHEMA);
    else if (getModelGroupType()==ModelGroup.CHOICE)
      writer.startElement("choice", Constants.SOAP_SCHEMA);
    else
      writer.startElement("sequence", Constants.SOAP_SCHEMA);

    if (getMinOccurs()!=1)
      writer.attribute("minOccurs", getMinOccurs()+"");
    if (getMaxOccurs()!=1)
    {
      if (getMaxOccurs()==Integer.MAX_VALUE)
        writer.attribute("maxOccurs", "unbounded");
      else
        writer.attribute("maxOccurs", getMaxOccurs()+"");
    }

    if (content!=null && content.size()>0) 
    {
      for (int i=0; i<content.size(); i++) 
      {
        ((Component) content.elementAt(i)).toXML(writer);
      }
    } //if

    //endTag
    writer.endTag();
  } //toXML

   /**
   * Returns all the elements that are part of the ModelGroup
   * @return an array of Elements of the model group; null if
   * there are no elements.
   */
  public Element[] getAllElements()
  {
    Vector returnVector=new Vector();
    if (this.getContent()==null)
      return null;
    for (int i=0; i<this.getContent().size(); i++)
    {
      if (this.content.elementAt(i) instanceof Element)
        returnVector.add(this.content.elementAt(i));
      else if (this.content.elementAt(i) instanceof ModelGroupImplementation)
      {
        Element eArray[]=((ModelGroupImplementation)content.elementAt(i)).getAllElements();
        if (eArray!=null)
        {
          for (int j=0; j<eArray.length; j++)
            returnVector.add(eArray[j]);
        }
      }//else
    }//for
    if (returnVector.size()==0)
      return null;
    Element[] returnElement = new Element[returnVector.size()];
    for (int i=0; i<returnVector.size(); i++)
      returnElement[i]=(Element)returnVector.elementAt(i);
    return returnElement;
  }//method
} //ModelGroupImplementation


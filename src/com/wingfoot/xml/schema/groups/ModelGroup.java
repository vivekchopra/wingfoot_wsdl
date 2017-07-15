package com.wingfoot.xml.schema.groups;
import com.wingfoot.xml.schema.*;
import java.util.*;
public interface ModelGroup {

	public static final int SEQUENCE = 1;
	public static final int CHOICE = 2;
	public static final int ALL = 3;

	/**
	 * Defines the variety of ModelGroup.  A ModelGroup
	 * is either a &ltsequence&gt, &ltchoice&gt or&lt<all&gt.  
	 * @param modelGroupType the value of this int is
	 * on of the final integers defined in this interface.
	 */
	public void setModelGroupType(int modelGroupType);

	/**
	 * Retrieves the modelGroupType set earlier
	 * @return int the modelGroupType
	 */
	public int getModelGroupType();

	/**
	 * Sets the content of the ModelGroup.  Concrete
	 * implementations of this interface ensure that
	 * the Component is legal.  
   * <p>
   *  The legal
	 * Component for a content is either an Element or 
	 * an instanceof ModelGroup
	 */
	public void setContent(Component content) throws SchemaException;

	/**
	 * Returns a List of contents.  Each element of the
	 * List is a Component.
	 * @return List of Components representing the content; 
	 * returns null if the content is empty.
	 */
	public List getContent();

  /**
   * Sets the maxOccurs attribute for the ModelGroup
   */
  public void setMaxOccurs(int maxOccurs) throws SchemaException;

  /**
   * Set the minOccurs attribute for the ModelGroup
   */
  public void  setMinOccurs(int minOccurs) throws SchemaException;

  /**
   * Retrieves the maxOccurs for the ModelGroup
   */
  public int getMaxOccurs() ;

  /**
   * Retrieves the minOccurs for the ModelGroup
   */
  public int getMinOccurs() ;

} /*interface*/

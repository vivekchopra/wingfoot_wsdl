package com.wingfoot.xml.schema;

import java.io.*;
import java.util.*;
import org.kxml.parser.*;
import org.kxml.*;
import com.wingfoot.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.types.*;
import com.wingfoot.xml.schema.groups.*;
/**
 * A concrete implementation of the SchemaReader interface.
 * This is the default schema reader.
 */
public class SchemaReaderInstance implements SchemaReader
{
  private SchemaHolder sh;
  private String targetNamespace,defaultNamespace;
  private boolean isAttributeFormDefaultQualified;
  private boolean isElementFormDefaultQualified;  
  private final int SCHEMA=1;
  private final int COMPLEX_TYPE=2;
  private final int ATTRIBUTE_GROUP=4;
  private final int SIMPLE_TYPE=5;
  private final int RESTRICTION=6;
  private final int ATTRIBUTE=7;
  private final int ELEMENT=8;
  //private final int GROUP=9;
  private final int MODEL_GROUP=10;
  private final int MODEL_GROUP_DEFINITION=11;
  /**
   * Creates an instance of SchemaReaderInstance.
   */
  public SchemaReaderInstance()
  {
    sh=new SchemaHolder();
  }

  /**
   * Returns the targetNamespace defined in the
   * &ltschema&gt element.
   * @return String the targetNamespace, null
   * if none was defined.
   */
  public String getTargetNamespace()
  {
    return targetNamespace;
  }

  /**
   * Sets the targetNamespace that appears
   * in the schema element.
   * @param newTargetNamespace the targetnamespace.
   */
  public void setTargetNamespace(String newTargetNamespace)
  {
    targetNamespace = newTargetNamespace;
  }
   
  public SchemaHolder parse(XmlParser parser) 
    throws IOException, SchemaException
  {
    ParseEvent pe=null;
    
    /**
     * Skip all the white spaces until you hit the
     * first StartTag.  This must be the <schema> tag.
     */
    while (true) 
    {
      pe=parser.peek();
      if (pe.getType()==Xml.START_TAG) 
              break;
      else
        parser.read();
    }//while

    /**
     * Now we have the first start tag.
     * Make sure this is <schema>, and store
     * all the attributes.
     */
     if (pe.getName().equals("schema") && pe.getType()==Xml.START_TAG) 
     {
       pe=parser.read();
       String tempDef=((StartTag)pe).getPrefixMap().getNamespace("");
       if (tempDef!=null && tempDef!="")
        defaultNamespace=tempDef;
       Vector v = pe.getAttributes();
       if (v!=null && v.size()>0) 
       {
          Hashtable ht = new Hashtable();
          for (int i=0; i<v.size(); i++) 
          {
            org.kxml.Attribute aAttribute = (org.kxml.Attribute)
                                            v.elementAt(i);
            QName attrName = new QName(aAttribute.getNamespace(),
                                        aAttribute.getName());
            ht.put(attrName, aAttribute.getValue());
            if (aAttribute.getName().equals("targetNamespace")) {
              setTargetNamespace(aAttribute.getValue());
              sh.setTargetNamespace(this.getTargetNamespace());
            }
            else if (aAttribute.getName().equals("elementFormDefault") &&
            aAttribute.getValue().equals("qualified"))
              this.isElementFormDefaultQualified=true;
            else if (aAttribute.getName().equals("attributeFormDefault") &&
            aAttribute.getValue().equals("qualified"))
              this.isAttributeFormDefaultQualified=true;
          } //for
          sh.setAttributeQualified(this.isAttributeFormDefaultQualified);
          sh.setElementQualified(this.isElementFormDefaultQualified);

          //Set all the attributes that appear in the <schema> element
          sh.setSchemaAttributes(ht);
       } //if v!=null

       /**
        * The <schema> element is now parsed.  Start processing
        * each element below the <schema> element.
        */
        while (true) 
        {
          pe=parser.peek();
          if (pe!=null && ((pe.getType()==Xml.END_TAG && pe.getName().equals("schema"))
                || pe.getType()==Xml.END_DOCUMENT))
          {
                  break;
          }
          else if (pe !=null && pe.getType()==Xml.START_TAG)
          {
            /**
             * Some element; read it, convert it to Java, and place
             * the Componenet in SchemaHolder
             */
             Component c = convertXMLElement(parser, this.SCHEMA);
             if (c!=null)
              sh.setComponent(c);
            /**
             else
              //read past the unknown
              parser.read();
            **/
          }//else
          else
            //some whitespace.  Just read
            parser.read();
        } //while
     } //if schema
     else 
      throw new SchemaException("ERROR_SCHEMA_024:"+Constants.ERROR_SCHEMA_024);

      return sh;
  }
  
  /**
   * Takes a byte array representing a well formed XML schema
   * and returns back an instance of SchemaHolder that encapsulates
   * the schema into Components.  These Components can be accessed
   * using the getter methods in the SchemaHolder interface.
   * @param schemaPayload a byte representation of a well formed 
   * XML schema.
   * @return SchemaHolder Object representation of the XML schema.
   * @throws IOException if an error occurs while reading the
   * schemaPayload.
   * @throws SchemaException if any error occurs while converting
   * the schemaPayload to object (Java) representation.
   */
   public SchemaHolder parse(byte[] schemaPayload) 
    throws IOException, SchemaException
   {
      ParseEvent pe=null;
      BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                              new ByteArrayInputStream(schemaPayload)));
      XmlParser parser=new XmlParser(br);
      this.parse(parser);
      //return the SchemaHolder
      br.close();
      return sh;
   } /* parse(byte{}) */

  /**
   * Reads an element from the parser and converts to Component.
   * The parser MUST point to the start tag in the schema document.
   * @param parser pointer to the payload;
   * @param parent int that indicates the parent of the element
   * being deserialized.  It is one of the final instance variables
   * defined above.
   * @throws IOException if any error occurs while reading the 
   * schemaPayload (by the parser).
   * @throws SchemaException thrown if during reading the schema, an
   * unsupported element is encountered.
   */
  private Component convertXMLElement(XmlParser parser, int parent)
    throws IOException,  SchemaException
  { 
    ParseEvent pe= parser.peek();
    if (pe.getType()==Xml.START_TAG && pe.getNamespace().equals(Constants.SOAP_SCHEMA)) 
    {
      
      if (pe.getName().equals("element"))
        return this.deserializeElement(parser, parent);
      else if (pe.getName().equals("simpleType"))
        return deserializeSimpleType(parser, parent);
      else if (pe.getName().equals("complexType"))
        return deserializeComplexType(parser, parent);
      else if (pe.getName().equals("attribute"))
        return this.deserializeAttribute(parser, parent);
      else if (pe.getName().equals("attributeGroup"))
        return this.deserializeAttributeGroup(parser, parent);
      else if (pe.getName().equals("group"))
        return this.deserializeModelGroupDefinition(parser, parent);
      else if (pe.getName().equals("sequence") || pe.getName().equals("all")
      || pe.getName().equals("choice"))
        return this.deserializeModelGroup(parser, parent);
      else 
      {
        //Unsupported element.  Just skip past it.
        String name=pe.getName();
        while (true)
        {
          if (pe.getType()==Xml.END_TAG &&
          pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
          pe.getName().equals(name))
            break;
          pe=parser.read();
        }
        return null;
      }
    } /* if START_TAG*/
    else
      throw new SchemaException("ERROR_SCHEMA_025:"+Constants.ERROR_SCHEMA_025+":"+pe.getName());
  } /*convertXMLElement*/

  /**
   * Takes an XML representation of simpleType and encapsulates
   * it to SimpleType.  The &ltsimpleType&gt is part of the
   * &ltschema&gt element.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltsimpleType......&gt element.
   * @param parentElement int to indicate the parent of the element
   * being processed. It is one of the final instance variables. This is useful to
   * determine if the simpleType is of global scope or local scope.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */
  private SimpleType deserializeSimpleType(XmlParser parser, 
  int parent) 
    throws SchemaException, IOException
  {
    Type type=null;
    String name=null;
    /**
     * The parser is pointing to <simpleType> element. Check to
     * see if there is a name attribute.
     */
     StartTag st=(StartTag)parser.read();
     org.kxml.Attribute attr=st.getAttribute("name");
     if (attr!=null)
      name=attr.getValue();
      
     /**
      * Keep reading iteratively until you get past the </simpleType>
      * tag.
      */
      while (true) 
      {
        ParseEvent pe=parser.peek();
        if (pe!=null && ((pe.getType()==Xml.END_TAG &&
        pe.getName().equals("simpleType") && 
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
        pe.getType()==Xml.END_DOCUMENT)) 
        {
          parser.read();
          break;
        }
        else if (pe !=null && (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) )
        {
          if (pe.getName().equals("restriction"))
          {
            parser.read();
            String[] normAttr=((StartTag)pe).getNormalizedAttribute(null, "base");
            if (normAttr!=null) 
            {
              if (type!=null)
                throw new SchemaException
                  ("ERROR_SCHEMA_026:"+Constants.ERROR_SCHEMA_026+":"+st.getName());
              if (normAttr[0]==null)
                normAttr[0]=defaultNamespace;
              type = new TypeReference(normAttr[0], normAttr[1]);
            } 
          } //if restriction
          else if (pe.getName().equals("simpleType")) 
          {
            if (type!=null)
                throw new SchemaException
                  ("ERROR_SCHEMA_026:"+Constants.ERROR_SCHEMA_026+":"+st.getName());
            type=deserializeSimpleType(parser, this.RESTRICTION);
          }
          else 
          {
            parser.read();
            //some kind of facet
          }
        } //else START_TAG
        else
          //Some whitespace; just read it.
          parser.read();
      } //while
      
      return (parent==this.SCHEMA)?
      new SimpleType(name, targetNamespace,type, true):
      new SimpleType(name,targetNamespace,type, false);
  } /*deserializeSimpleType*/

  
  /**
   * Takes an XML representation of complexType and encapsulates
   * it to ComplexType.  The &ltcomplexType&gt is part of the
   * &ltschema&gt element.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltcomplexType......&gt element.
   * @param parent one of the final int defined in the class.  This is useful to
   * determine if the complexType is of global scope or local scope.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */
  private ComplexType deserializeComplexType(XmlParser parser, 
  int parent)
      throws SchemaException, IOException
  {
      Type type=null;
      String name=null;
      String[] base =null;
      Content content=null;
      Vector component=null;
      /**
       * The parser is pointing to <complexType> element. Check to
       * see if there is a name attribute.
       */
       StartTag st=(StartTag)parser.read();
       org.kxml.Attribute attr=st.getAttribute("name");
       if (attr!=null)
          name=attr.getValue();
      /**
       * Even though this is against the spec in XML Schema, so
       * implementations seem to send the base type in the 
       * <complexType> element.  In such a case, if there is no
       * content model, create one and restrict on this base type.
       */
       base = st.getNormalizedAttribute(null, "base");
       if (base!=null && base[0]==null)
          base[0]=this.defaultNamespace;
      
       /**
        * Keep reading iteratively until you get past the </complexType>
        * tag.
        */
        while (true) 
        {
          ParseEvent pe=parser.peek();
          if (pe!=null && ((pe.getType()==Xml.END_TAG &&
          pe.getName().equals("complexType") && 
          pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
          pe.getType()==Xml.END_DOCUMENT)) 
          {
            parser.read();
            break;
          }
          else if (pe !=null && (pe.getType()==Xml.START_TAG &&
          pe.getNamespace().equals(Constants.SOAP_SCHEMA)) )
          {
            if (pe.getName().equals("simpleContent"))
            {
              content=
              deserializeSimpleContent(parser);
            } //if simpleContent
            else if (pe.getName().equals("complexContent")) 
            {
              content=
              deserializeComplexContent(parser);
            } //if complexContent

            else if (pe.getName().equals("group")) 
            {
              content=new ComplexContent(Content.RESTRICTION);
              content.setBaseType(new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
              content.addContent(this.deserializeModelGroupDefinition(parser, this.COMPLEX_TYPE));
            }
            else if (pe.getName().equals("all")|| pe.getName().equals("choice")
            || pe.getName().equals("sequence")) 
            {
              content=new ComplexContent(Content.RESTRICTION);
              content.setBaseType(new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
              content.addContent(this.deserializeModelGroup(parser, this.COMPLEX_TYPE));
            }
            else if (pe.getName().equals("attribute")) 
            {
              Component cc = this.deserializeAttribute(parser,this.COMPLEX_TYPE);
              if (component==null) component=new Vector();
              component.add(cc);
            }
            else if (pe.getName().equals("attributeGroup")) 
            {
              Component cc = this.deserializeAttributeGroup(parser,this.COMPLEX_TYPE);
              if (component==null) component=new Vector();
              component.add(cc);
            }
            else 
              //not sure what this is; just read
              parser.read();
          } //if START_TAG
          else
            //must be a whitespace; just read
            parser.read();
        } //while true 

        if (content==null) 
        {
          //the content is null. create one.
          content=new ComplexContent(Content.RESTRICTION);
          content.setBaseType(base!=null?new TypeReference(base[0], base[1]):
          new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
        }
        ComplexType ct = (parent==this.SCHEMA)? 
        new ComplexType(name,targetNamespace,content,true):
        new ComplexType(name,targetNamespace,content,false);
        if (component!=null) 
        {
          for (int i=0; i<component.size(); i++)
            ct.setAttributeAndAttributeGroup((Component) component.elementAt(i));
        }
        return ct;
  } /*deserializeComplexType*/
  

  /**
   * Takes an XML representation of simpleContent and encapsulates
   * it to SimpleContent.  The &ltsimpleContent&gt is part of the
   * &ltschema&gt element.  The parent element of a &ltsimpleContent&gt
   * is always &ltcomplexType&gt.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltsimpleContent......&gt element.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */
  private  SimpleContent deserializeSimpleContent(XmlParser parser)
  throws SchemaException, IOException
  {
    int derivation=0;
    Vector content=null;
    TypeReference baseType=null;
    //The parser is pointing to <simpleContent>
    while (true) 
    {
      ParseEvent pe=parser.peek();
      if (pe!=null &&((pe.getType()==Xml.END_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("simpleContent")) ||
      pe.getType()==Xml.END_DOCUMENT)) 
      {
        parser.read();
        break;
      }
      else if (pe!=null &&pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      (pe.getName().equals("restriction") ||
       pe.getName().equals("extension"))) 
      {
        pe=parser.read();
        if (pe.getName().equals("restriction"))
          derivation=Content.RESTRICTION;
        else
          derivation=Content.EXTENSTION;
          
        String[] baseAttr=((StartTag)pe).getNormalizedAttribute(null,"base");
        if (baseAttr==null)
          throw new SchemaException("ERROR_SCHEMA_028:"+Constants.ERROR_SCHEMA_028);
        if (baseAttr[0]==null)
          baseAttr[0]=defaultNamespace;
        baseType=new TypeReference(baseAttr[0],baseAttr[1]);
      } //restriction || extension
      
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("attribute")) 
      {
        if (content==null)
          content=new Vector();
        content.add(this.deserializeAttribute(parser,this.COMPLEX_TYPE));
      }
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("attributeGroup")) 
      {
        if (content==null)
          content=new Vector();
        content.add(this.deserializeAttributeGroup(parser, this.COMPLEX_TYPE));
      }
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("simpleType")) 
      {
        if (derivation!=Content.RESTRICTION)
          throw new SchemaException("ERROR_SCHEMA_029:"+Constants.ERROR_SCHEMA_029);
        if (content==null)
          content=new Vector();
        content.add(deserializeSimpleType(parser, this.RESTRICTION));
      }
      else 
      {
        //Probably a facet or white space. Just read.
        parser.read();
      }
    }  //while true

    /**
     * Create the SimpleContent
     */
     SimpleContent sc = new SimpleContent(derivation);
     sc.setBaseType(baseType);
     sc.addContentList(content);
     return sc;
  } /*deserializeSimpleContent*/

  
  /**
   * Takes an XML representation of complexContent and encapsulates
   * it to ComplexContent.  The &ltcomplexContent&gt is part of the
   * &ltschema&gt element. The parent element of a &ltsimpleContent&gt
   * is always &ltcomplexType&gt.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltcomplexContent......&gt element.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */
  private ComplexContent deserializeComplexContent(XmlParser parser) 
  throws SchemaException, IOException
  {
    int derivation=0;
    Vector content=new Vector();
    TypeReference baseType=null;
    //The parser is pointing to <complexContent>
    /**
     * We do not support mixed=true attribute in the
     * complexContent element.  When we do support
     * it, inside the while true check for START_TAG
     * and complexContent.
     */
    
    String endName=parser.peek().getName();
    
    while (true) 
    {
      ParseEvent pe=parser.peek();
      if (pe!=null &&((pe.getType()==Xml.END_TAG &&
      pe.getName().equals(endName) &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA)) ||
      pe.getType()==Xml.END_DOCUMENT)) 
      {
        if (! pe.getName().equals("complexType"))
         parser.read();
        break;
      }
      else if (pe!=null &&pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      (pe.getName().equals("restriction") ||
       pe.getName().equals("extension"))) 
      {
        pe=parser.read();
        if (pe.getName().equals("restriction"))
          derivation=Content.RESTRICTION;
        else
          derivation=Content.EXTENSTION;
          
        String[] baseAttr=((StartTag)pe).getNormalizedAttribute(null,"base");
        if (baseAttr==null)
          throw new SchemaException("ERROR_SCHEMA_028:"+Constants.ERROR_SCHEMA_028);
        if (baseAttr[0]==null)
          baseAttr[0]=defaultNamespace;
        baseType=new TypeReference(baseAttr[0],baseAttr[1]);
      } //restriction || extension
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("attribute")) 
      {
        content.add(this.deserializeAttribute(parser, this.COMPLEX_TYPE));
      }
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("attributeGroup")) 
      {
        content.add(this.deserializeAttributeGroup(parser, this.COMPLEX_TYPE));
      }
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      pe.getName().equals("group")) 
      {
        content.add(this.deserializeModelGroupDefinition(parser, this.COMPLEX_TYPE));
      }
      else if (pe!=null && pe.getType()==Xml.START_TAG &&
      pe.getNamespace().equals(Constants.SOAP_SCHEMA) &&
      (pe.getName().equals("all")|| pe.getName().equals("choice")
      || pe.getName().equals("sequence"))) 
      {
        content.add(this.deserializeModelGroup(parser, this.COMPLEX_TYPE));
      }
      else 
      {
        //Probably a facet or white space. Just read.
        parser.read();
      }
    }  //while true

    /**
     * Create the ComplexContent
     */
     if (derivation==0) {
      derivation=Content.RESTRICTION;
      baseType=new TypeReference(Constants.SOAP_SCHEMA, "ur-type");
     }
     ComplexContent cc = new ComplexContent(derivation);
     cc.setBaseType(baseType);
     if (content!=null && content.size()>0)
      cc.addContentList(content);
     return cc;
  } /*deserializeComplexContent*/

  /**
   * Takes an XML representation of attribute and encapsulates
   * it as Attribute.  The &ltattribute&gt is part of the
   * &ltschema&gt element or may occur as part of &ltcomplexType&gt
   * or &ltattributeGroup&gt.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltsimpleType......&gt element.
   * @param parentElement int to indicate the parent of the element
   * being processed. It is one of the final instance variables. This is useful to
   * determine if the simpleType is of global scope or local scope.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */
  private Attribute deserializeAttribute(XmlParser parser, 
  int parent) throws SchemaException, IOException 
  {
    Type type=null;
    String name=null;
    String attrDefault=null, fixed=null;
    boolean qualified=false, isReference=false;
    QName ref=null;
    int use=Attribute.OPTIONAL;
    Hashtable additionalAttributes=null;
    /**
     * The parser is pointing to <attribute> element. Check to
     * see if there is an attribute of interest.
     */
     StartTag st=(StartTag)parser.read();
     org.kxml.Attribute attr=st.getAttribute("name");
     if (attr!=null)
      name=attr.getValue();
      
     attr=st.getAttribute("default");
     if (attr!=null)
      attrDefault=attr.getValue();

     attr=st.getAttribute("fixed");
     if (attr!=null)
      fixed=attr.getValue();

     attr=st.getAttribute("form");
     if (attr!=null && attr.getValue().equals("qualified"))
      qualified=true;

     String[] attrArray=st.getNormalizedAttribute(null, "ref");
     if (attrArray!=null) 
     {
      ref=new QName(attrArray[0]==null?this.targetNamespace:attrArray[0], attrArray[1]); 
     }

     attrArray=st.getNormalizedAttribute(null, "type");
     if (attrArray!=null) 
     {
      if (attrArray[0]==null)
        attrArray[0]=defaultNamespace;
      type = new TypeReference(attrArray[0], attrArray[1]);
     }

     attr=st.getAttribute("use");
     if (attr!=null) 
     {
       if (attr.getValue().equals("prohibited"))
        use=Attribute.PROHIBITED;
       else if (attr.getValue().equals("required"))
        use=Attribute.REQUIRED;
     }

     /**
      * Read any additional attributes 
      */
      Vector additionalAttr=st.getAttributes();
      if (additionalAttr!=null && additionalAttr.size()>0) 
      {
        for (int i=0; i<additionalAttr.size(); i++) 
        {
            org.kxml.Attribute aAttr=(org.kxml.Attribute) additionalAttr.elementAt(i);
            if (!aAttr.getName().equals("default") &&
            !aAttr.getName().equals("fixed") &&
            !aAttr.getName().equals("form") &&
            !aAttr.getName().equals("id") &&
            !aAttr.getName().equals("name") &&
            !aAttr.getName().equals("ref") &&
            !aAttr.getName().equals("type") &&
            !aAttr.getName().equals("use")) 
            {
              String additionalAttrName=aAttr.getName();
              String additionalAttrNS = aAttr.getNamespace();
              String normAdditionalAttr[]=st.getNormalizedAttribute(additionalAttrNS,
              additionalAttrName);
              if (additionalAttributes==null)
                additionalAttributes=new Hashtable();
              additionalAttributes.put(new QName(additionalAttrNS,additionalAttrName),
              new QName(normAdditionalAttr[0]==null?defaultNamespace:normAdditionalAttr[0], 
              normAdditionalAttr[1]));
            }
        } //for
      } //if
     /**
      * Keep reading iteratively until you get past the </attribute>
      * tag.
      */
      while (true) 
      {
        ParseEvent pe=parser.peek();
        if (pe!=null && ((pe.getType()==Xml.END_TAG &&
        pe.getName().equals("attribute") && 
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
        pe.getType()==Xml.END_DOCUMENT)) 
        {
          parser.read();
          break;
        }
        else if (pe !=null && (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) ) 
        {
          if (pe.getName().equals("simpleType")) 
          {
            type=this.deserializeSimpleType(parser, this.ATTRIBUTE);
          }
          else
            //Could be a whitespace or annotation. Just read;
            parser.read();
        }
        else
          //Not sure what this is. Just read
          parser.read();
      } //while
      com.wingfoot.xml.schema.Attribute newAttr=null;
      if (parent==this.SCHEMA) 
      {
        newAttr= 
        new com.wingfoot.xml.schema.Attribute
        (name,getTargetNamespace(),type,true);
      }
      else if ((parent==this.ATTRIBUTE_GROUP ||
      parent==this.COMPLEX_TYPE) && ref==null) 
      {
        QName attrQName=null;
        if (qualified==true || this.isAttributeFormDefaultQualified==true)
          newAttr=new Attribute(name, getTargetNamespace(), type,false);
        else
          newAttr=new Attribute(name, null, type, false);
        newAttr.setUse(use);
      }
      else if ((parent==this.ATTRIBUTE_GROUP ||
      parent==this.COMPLEX_TYPE) && ref!=null) 
      {
        newAttr=new com.wingfoot.xml.schema.Attribute(ref);
        newAttr.setUse(use);
      }
      newAttr.setDefault(attrDefault);
      newAttr.setFixed(fixed);
      newAttr.setFormQualified(qualified);
      newAttr.setAdditionalAttributes(additionalAttributes);
      return newAttr;
      
  } /*deserializeAttribute*/

  /**
   * Takes an XML representation of attributeGroup and encapsulates
   * it as AttributeGroupDefinition.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltsimpleType......&gt element.
   * @param parentElement int to indicate the parent of the element
   * being processed. It is one of the final instance variables. This is useful to
   * determine if the simpleType is of global scope or local scope.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */  
  private AttributeGroupDefinition deserializeAttributeGroup(XmlParser parser, 
  int parent) throws SchemaException, IOException 
  {
    QName ref=null;
    String name=null;
    Vector content=null;
    /**
     * The parser is pointing to <attributeGroup> element. Check to
     * see if there is a name attribute.
     */
     StartTag st=(StartTag)parser.read();
     org.kxml.Attribute attr=st.getAttribute("name");
     if (attr!=null) {
      if (parent!=this.SCHEMA)
        throw new SchemaException("ERROR_SCHEMA_030:"+Constants.ERROR_SCHEMA_030+":"+attr.getValue());
      name=attr.getValue();
     }
     else 
     {
       //check for the ref attribute
       String[] attrArray = st.getNormalizedAttribute(null,"ref");
       if ((parent!=this.ATTRIBUTE_GROUP && parent!=this.COMPLEX_TYPE) && attrArray!=null)
        throw new SchemaException("ERROR_SCHEMA_032:"+Constants.ERROR_SCHEMA_032+":"+attr.getValue());
       if (attrArray!=null)
        ref=new QName(attrArray[0]==null?this.targetNamespace:attrArray[0], attrArray[1]);
     }

     if (name!=null && ref!=null)
      throw new SchemaException("ERROR_SCHEMA_031:"+Constants.ERROR_SCHEMA_031+":"+attr.getValue());
      
     /**
      * Keep reading iteratively until you get past the </attributeGroup>
      * tag.
      */
      
      while (true) 
      {
        Component c = null;
        ParseEvent pe=parser.peek();
        if (pe!=null && ((pe.getType()==Xml.END_TAG &&
        pe.getName().equals("attributeGroup") && 
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
        pe.getType()==Xml.END_DOCUMENT)) 
        {
          parser.read();
          break;
        }
        else if (pe !=null && (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) )
        {
          if (pe.getName().equals("attribute"))
            c=this.deserializeAttribute(parser, this.ATTRIBUTE_GROUP);
          else if (pe.getName().equals("attributeGroup")) 
          {
            c=this.deserializeAttributeGroup(parser, this.ATTRIBUTE_GROUP);
          }
          else
            //probably annotation element.  For the time being, just skip.
            parser.read();
          if (c!=null) 
          {
            if (content==null)
              content=new Vector();
            content.add(c);
          }
        }
        else
          //probably a white space.  Just read.
          parser.read();
      } //while  

      AttributeGroupDefinition agd=
      (ref==null) ? new AttributeGroupDefinition(name,this.getTargetNamespace()):
                    new AttributeGroupDefinition(ref);
      agd.setContent(content);
      return agd;
  } /*deserializeAttributeGroup*/

    /**
   * Takes an XML representation of element and encapsulates
   * it as Element.  The &ltelement&gt is part of the
   * &ltschema&gt element or may occur as part of &ltcomplexType&gt
   * or &ltschema&gt.
   * <p> This method is called by instances of SchemaReaderInstance
   * @param parser instance of XmlParser.  Before this method
   * is called, parser is so positioned that the next call to
   * parser.read() returns the &ltsimpleType......&gt element.
   * @param parentElement int to indicate the parent of the element
   * being processed. It is one of the final instance variables. This is useful to
   * determine if the simpleType is of global scope or local scope.
   * @param targetNamespace the value of the targetNamespace attribute
   * in the &ltschema&gt element.
   * @throws IOException if an errors while reading the schema.
   * @throws SchemaException if the schema is semantically incorrect.
   */
  private Element deserializeElement(XmlParser parser, 
  int parent) throws SchemaException, IOException 
  {
    Type type=null;
    String name=null;
    String attrDefault=null, fixed=null;
    boolean qualified=false, nillable=false;
    QName ref=null;
    int minOccurs=1, maxOccurs=1;
    
    /**
     * The parser is pointing to <element>. Check to
     * see if there is an attribute of interest.
     */
     StartTag st=(StartTag)parser.read();
     org.kxml.Attribute attr=st.getAttribute("name");
     if (attr!=null)
      name=attr.getValue();
     else 
     {
       String[] attrArray=st.getNormalizedAttribute(null, "ref");
       if (attrArray!=null) 
       {
        ref=new QName(attrArray[0]==null?this.targetNamespace:attrArray[0], attrArray[1]); 
       }
       else 
       {
         new SchemaException("ERROR_SCHEMA_013:" + Constants.ERROR_SCHEMA_013);
       }
     } //else
      
      
     attr=st.getAttribute("default");
     if (attr!=null)
      attrDefault=attr.getValue();

     attr=st.getAttribute("fixed");
     if (attr!=null)
      fixed=attr.getValue();

     attr=st.getAttribute("form");
     if (attr!=null && attr.getValue().equals("qualified"))
      qualified=true;

     String[] attrArray=st.getNormalizedAttribute(null, "type");
     if (attrArray!=null) 
     {
      if (attrArray[0]==null)
        attrArray[0]=defaultNamespace;
      type = new TypeReference(attrArray[0], attrArray[1]);
     }

     attr=st.getAttribute("minOccurs");
     if (attr!=null) 
     {
        minOccurs=Integer.parseInt(attr.getValue());
     }

     attr=st.getAttribute("maxOccurs");
     if (attr!=null) 
     {
        if (attr.getValue().equals("unbounded"))
          maxOccurs=Integer.MAX_VALUE;
        else
          maxOccurs=Integer.parseInt(attr.getValue());
     }

     attr=st.getAttribute("nillable");
     if (attr!=null) 
     {
        if (attr.getValue().equals("true"))
          nillable=true;
     }
     
     /**
      * Keep reading iteratively until you get past the </element>
      * tag.
      */
      while (true) 
      {
         ParseEvent pe=parser.peek();
        if (pe!=null && ((pe.getType()==Xml.END_TAG &&
        pe.getName().equals("element") && 
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
        pe.getType()==Xml.END_DOCUMENT)) 
        {
          parser.read();
          break;
        }
        else if (pe !=null && (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) ) 
        {
          if (pe.getName().equals("simpleType")) 
          {
            type=this.deserializeSimpleType(parser, this.ELEMENT);
          }
          else if (pe.getName().equals("complexType")) 
          {
            type=this.deserializeComplexType(parser, this.ELEMENT);
          }
          else
            //Could be a whitespace or annotation. Just read;
            parser.read();
        }
        else
          //Not sure what this is. Just read
          parser.read();
      } //while
      
      Element element=null;
      if (parent==this.SCHEMA) 
      {
        element= 
        //new Element(new QName(this.getTargetNamespace(),name), type, true);
        new Element(name, getTargetNamespace(), type, true);
        element.setDefault(attrDefault);
        element.setFixed(fixed);
        element.setNillable(nillable);
      }
      else if ((parent==this.MODEL_GROUP ||
      parent==this.COMPLEX_TYPE) && ref==null) 
      {
        String attrQName=null;
        if (qualified==true || this.isElementFormDefaultQualified==true)
          attrQName=this.getTargetNamespace();
        element=new Element(name,attrQName, type, false);
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(maxOccurs);
        element.setNillable(nillable); //added in v1.1
        element.setDefault(attrDefault); //added in v1.1
        element.setFixed(fixed); //added in v1.1
        
      }
      else if ((parent==this.MODEL_GROUP ||
      parent==this.COMPLEX_TYPE) && ref!=null) 
      {
        element=new Element(ref);
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(maxOccurs);
        element.setNillable(nillable); //added in v1.1
        element.setDefault(attrDefault); //added in v1.1
        element.setFixed(fixed); //added in v1.1
      }
      element.setFormQualified(qualified);
      return element;
      
  } /*deserializeElement*/

  private ModelGroupImplementation deserializeModelGroup(XmlParser parser, 
  int parent) throws SchemaException, IOException 
  {
    QName ref=null;
    String name=null;
    Vector content=null;
    /**
     * The parser is pointing to <all>, <sequence> or <choice> element. Check to
     * see if there is a name attribute.
     */
     ModelGroupImplementation mgi=new ModelGroupImplementation();
     StartTag st=(StartTag)parser.read();

     String groupName=st.getName();
     if (groupName.equals("all"))
        mgi.setModelGroupType(ModelGroup.ALL);
     else if (groupName.equals("choice"))
        mgi.setModelGroupType(ModelGroup.CHOICE);
     else
        mgi.setModelGroupType(ModelGroup.SEQUENCE);

     org.kxml.Attribute attr=st.getAttribute("minOccurs");
     if (attr!=null) {
       mgi.setMinOccurs(Integer.parseInt(attr.getValue()));
     }

     attr=st.getAttribute("maxOccurs");
     if (attr!=null) {
       if (attr.getValue().equals("unbounded"))
        mgi.setMaxOccurs(Integer.MAX_VALUE);
       else 
        mgi.setMaxOccurs(Integer.parseInt(attr.getValue()));
     }
     
     /**
      * Keep reading iteratively until you get past the </modelGroup 
      * (all|choice|sequence)> tag.
      */
      while (true) 
      {
        ParseEvent pe=parser.peek();
        if (pe!=null && ((pe.getType()==Xml.END_TAG &&
        pe.getName().equals(groupName) && 
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
        pe.getType()==Xml.END_DOCUMENT)) 
        {
          parser.read();
          break;
        }
        else if (pe !=null && (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) ) 
        {
          if (pe.getName().equals("element")) 
          {
            mgi.setContent(deserializeElement(parser, this.MODEL_GROUP));
          }
          else if (pe.getName().equals("all") ||
          pe.getName().equals("choice") ||
          pe.getName().equals("sequence")||
          pe.getName().equals("group")) 
          {
            if (mgi.getModelGroupType()==ModelGroup.ALL) 
              throw new SchemaException("ERROR_SCHEMA_033:"+Constants.ERROR_SCHEMA_033);
            if (pe.getName().equals("all")) 
              throw new SchemaException("ERROR_SCHEMA_033:"+Constants.ERROR_SCHEMA_033);
            if (pe.getName().equals("group"))
              mgi.setContent(this.deserializeModelGroupDefinition(parser, this.MODEL_GROUP));
            else
              mgi.setContent(deserializeModelGroup(parser, this.MODEL_GROUP));
          }
          else
            //probably annotation. Just skip.
            parser.read();
        }
        else
        //probably whitespace
        parser.read();
      } //while true
      return mgi;
  } //deserializeModelGroup


  private ModelGroupDefinition deserializeModelGroupDefinition(XmlParser parser, 
  int parent) throws SchemaException, IOException 
  {
    Type type=null;
    String name;
    ModelGroupDefinition mgd =null;
    /**
     * The parser is pointing to <group>. Check to
     * see if there is an attribute of interest.
     */
     StartTag st=(StartTag)parser.read();
     org.kxml.Attribute attr=st.getAttribute("name");
     if (attr!=null) 
     {
       name=attr.getValue();
       mgd = new ModelGroupDefinition(name, this.targetNamespace);
     }
     else 
     {
       String[] attrArray=st.getNormalizedAttribute(null, "ref");
       if (attrArray!=null) 
       {
         mgd = new ModelGroupDefinition( new QName(attrArray[0]==null?this.targetNamespace:attrArray[0], attrArray[1]));
       }
       else //no ref AND name; this is an error.
         throw new SchemaException("ERROR_SCHEMA_017:" + Constants.ERROR_SCHEMA_017);
     }
     
     /**
      * Keep reading iteratively until you get past the </group>
      * tag.
      */
      while (true) 
      {
        ParseEvent pe=parser.peek();
        if (pe!=null && ((pe.getType()==Xml.END_TAG &&
        pe.getName().equals("group") && 
        pe.getNamespace().equals(Constants.SOAP_SCHEMA)) || 
        pe.getType()==Xml.END_DOCUMENT)) 
        {
          parser.read();
          break;
        }
        else if (pe !=null && (pe.getType()==Xml.START_TAG &&
        pe.getNamespace().equals(Constants.SOAP_SCHEMA )) ) 
        {
          if (pe.getName().equals("sequence") ||
          pe.getName().equals("all") ||
          pe.getName().equals("choice")) 
          {
              mgd.setContent(
              this.deserializeModelGroup(parser, this.MODEL_GROUP_DEFINITION));
          } //if sequence
          else
            //probably annotation. Just read
            parser.read();
        }
        else
          //Probably whitespace. Just read
          parser.read();
      } //while
      return mgd;
  } //deserializeModelGroupDefinition

} /**class SchemaReaderInstance */
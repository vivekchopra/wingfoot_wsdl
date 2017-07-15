package com.wingfoot.xml.schema.gen;

import com.wingfoot.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.gen.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.groups.*;
import com.wingfoot.xml.schema.types.*;
import java.util.*;
import java.io.*;

/**
 * Takes a set of SchemaHolders and converts them
 * into Java objects.  The current capability is
 * limited to generating &lt;complexTypes&gt;
 */
public class SchemaConverterInstance 
{
  private SchemaHolder[] schemaHolderArray;
  private Options options;
  private Hashtable primitiveMap;
  private Vector serializedHolderVector;
  private Vector typeMap;

  /**
   * Creates a of SchemaWriterInstance.
   * @param schemaHolderArray array of SchemaHolder.
   * The array contains all the SchemaHolder that
   * are converted to Java representation.  It is
   * also possible that some components in a SchemaHolder
   * use components defined in another SchemaHolder (ideally
   * with different targetNamespace).  In such a case
   * both the SchemaHolders are present in the array.
   */
  public SchemaConverterInstance(SchemaHolder[] schemaHolderArray, Options options)
  {
    this.options=options;
    this.schemaHolderArray=schemaHolderArray;

    primitiveMap=new Hashtable();
    primitiveMap.put("string", "java.lang.String");
    primitiveMap.put("byte", "byte");
    primitiveMap.put("short", "short");
    primitiveMap.put("integer", "int");
    primitiveMap.put("int", "int");
    primitiveMap.put("long", "long");
    primitiveMap.put("float", "float");
    primitiveMap.put("double", "double");
    primitiveMap.put("decimal", "java.math.BigDecimal");
    primitiveMap.put("date", "java.util.Date");
    primitiveMap.put("dateTime", "java.util.Date");
    primitiveMap.put("boolean", "boolean");
    primitiveMap.put("ur-type", "java.lang.Object");
    primitiveMap.put("anyType", "java.lang.Object");
    //primitiveMap.put("byte", "byte");
    primitiveMap.put("base64", "com.wingfoot.soap.encoding.Base64");
    primitiveMap.put("base64Binary", "com.wingfoot.soap.encoding.Base64");
    primitiveMap.put("hexBinary", "com.wingfoot.soap.encoding.HexBinary");
    primitiveMap.put("vector", "java.util.Vector");
    primitiveMap.put("map", "java.util.HashMap");
  }

  /**
   * Given a primitive class, returns a String with
   * the fully qualified name of the wrapper class.
   * @param primitiveClass Class that encapsulates
   * the primitive class 
   * @return String with the fully qualified name
   * of the corresponding wrapper class; null if 
   * the primitiveClass does not encapsulate a
   * primitive type.
   */
  public String getWrapperClassName(String name) 
  {
    /**
     * In case the name is an array with a [] as suffix,
     * return the wrapper class with the []
     */
    if (name==null)
      return null;
    if (name.equals("byte"))
      return "java.lang.Byte";
    else if (name.equals("byte[]"))
      return "java.lang.Byte[]";
    else if (name.equals("short"))
      return "java.lang.Short";
    else if (name.equals("short[]"))
      return "java.lang.Short[]";
    else if (name.equals("int")||name.equals("integer"))
      return "java.lang.Integer";
    else if (name.equals("int[]")||name.equals("integer[]"))
      return "java.lang.Integer[]";
    else if (name.equals("long"))
      return "java.lang.Long";
    else if (name.equals("long[]"))
      return "java.lang.Long[]";
    else if (name.equals("float"))
      return "java.lang.Float";
    else if (name.equals("float[]"))
      return "java.lang.Float[]";
    else if (name.equals("double"))
      return "java.lang.Double";
    else if (name.equals("double[]"))
      return "java.lang.Double[]";
    else if (name.equals("boolean"))
      return "java.lang.Boolean";
    else if (name.equals("boolean[]"))
      return "java.lang.Boolean[]";
    else
      return null;
  }
  /**
   * Overloaded method that takes an Element.  The
   * element has a type.  Returns the fully qualfied
   * Java type (ex. java.lang.Integer) for the
   * Element.
   */
  public String getQualifiedJavaType(Element element) 
  {
    Type t = element.getType();
    if (t instanceof ComplexType &&
    ((ComplexType)t).isAnonymous()) 
    {
       ComplexType realT=(ComplexType) t;
       return options.getPackageMapping(realT.getTargetNamespace())==null?
       element.getName():
        options.getPackageMapping(realT.getTargetNamespace())+"."+element.getName();
    }
    
    else 
    {
      return this.getQualifiedJavaType(t);
    }
  }//getQualifiedJavaType
  
  /**
   * Takes a Type and returns back a Java type encapsulated
   * by the Type.  The String returned is of the format
   * java.lang.String or com.wingfoot.Employee.
   * <p>
   * The array of SchemaHolder passed in the constructor is
   * used to determine the 
   * @param tr the Type that has to be converted to a 
   * qualified name.  The Type encapsulates one of XSDType,
   * TypeReference, SimpleType or ComplexType.
   * @return String the fully qualified Java type; returns
   * java.lang.Object if an unsupported type is detected.
   * 
   */
  public String getQualifiedJavaType(Type tr) 
  {
    Type realT=tr;
    if (tr.getTargetNamespace()!=null) 
    {
      if (tr.getTargetNamespace().equals(Constants.SOAP_SCHEMA) ||
      tr.getTargetNamespace().equals(Constants.SOAP_ENCODING_STYLE)) 
      {
        /**
         * A type belonging to the XML Schema.  Check to
         * see if this is one of the inbuilt data types.
         */
         String str=(String)primitiveMap.get(tr.getName());
         if (str!=null)
          return str;
      }//if
      
     /**
      * This is not an inbuilt data type. 
      * If this is a TypeHolder, check the schemaHolder
      * to see if there is a schema with the namespace.
      */
      if (tr instanceof TypeReference) 
      {
        TypeReference tReference=(TypeReference) tr;
        SchemaHolder aSH=this.retrieveSchema(tReference.getTargetNamespace());
        realT= this.getComponent(tReference, aSH);
      }// if

      if (realT!=null) 
      {
        if (realT instanceof ComplexType &&
        ((ComplexType)realT).isArray()) 
        {
          return this.getQualifiedJavaTypeForArray((ComplexType)realT);
        }
        else if (realT instanceof ComplexType && ((ComplexType)realT).isLiteralArray())
        {
          return this.getQualifiedJavaTypeForLiteralArray((ComplexType)realT);
        }
        else if (realT instanceof ComplexType) 
        {
          if(realT instanceof ComplexType &&
          ((ComplexType)realT).isVector())
          {
            return "java.util.Vector";
          }
          else if(realT instanceof ComplexType &&
          ((ComplexType)realT).isHashMap())
          {
            return "java.util.Hashtable";
          }
          else 
          {
            //Anonymous types come here.
            return options.getPackageMapping(realT.getTargetNamespace())==null?
            ((ComplexType)realT).getName():
            options.getPackageMapping(realT.getTargetNamespace())+"."+((ComplexType)realT).getName();
          }
	  
        }
        else if (realT instanceof SimpleType)
          return this.getQualifiedJavaType(((SimpleType)realT).getDataType());
      } //realT!=null
    } //if targetNamespace!=null
    //returning null to get the class to compile
    return "java.lang.Object";
  } //getQualifiedJavaType

  private String getQualifiedJavaTypeForLiteralArray(ComplexType ct) 
  {
    if (ct.getContent()!=null && ct.getContent().getDerivation()==Content.RESTRICTION &&
    ct.getContent() instanceof ComplexContent)
    {
      ComplexContent cc=(ComplexContent)ct.getContent();
      if (cc.getContentCount()>0) 
      {
        Vector v = cc.getContentList();
        if (v.elementAt(0) instanceof ModelGroupImplementation)
        {
          ModelGroupImplementation mgi = (ModelGroupImplementation)v.elementAt(0);
          List l = mgi.getContent();
          if (l!=null && l.get(0) instanceof Element &&
          ((Element)l.get(0)).getMaxOccurs()>0)
          {
             Element e = (Element)l.get(0);
             if (e.isReference())
              e=this.getNormalizedElement(e);
             if (e!=null)
             {
               String typeName=e.getType().getName();
               String typeNamespace=e.getType().getTargetNamespace();
               if (typeName==null)
                typeName=e.getName();
               return this.getQualifiedJavaType(new TypeReference(typeNamespace,typeName))+"[]";
             }
          }
        }
      }
    }
    return "java.lang.Object[]";
  }//getQualifiedJavaTypeForLiteralArray

  private String getQualifiedJavaTypeForArray(ComplexType ct) 
  {
    ComplexContent c = (ComplexContent)ct.getContent();
    if (c.getContentCount()==0)
      return "java.lang.Object[]";
    Vector v = c.getContentList();
    for (int i=0; i<v.size(); i++)
    {
      if (!(v.elementAt(i) instanceof Attribute))
        continue;
      Attribute a = (Attribute)v.elementAt(i);
      Hashtable ht = a.getAdditionalAttributes();
      if (ht!=null) 
      {
        //QName baseQName=(QName)ht.get(new QName(Constants.WSDL_NAMESPACE,"arrayType"));
        QName baseQName=this.get(ht,new QName(Constants.WSDL_NAMESPACE,"arrayType"));
        if (baseQName!=null) 
        {
          return this.getQualifiedJavaType(new TypeReference(baseQName.getNamespaceURI(),baseQName.getLocalPart().substring(0,baseQName.getLocalPart().indexOf("["))))+"[]";
        }
      }//if
    }//for
    return "java.lang.Object[]";
  }//getQualifiedJavaTypeForArray

  private QName get(Hashtable qnames, QName key) 
  {
    if (qnames==null)
      return null;
    Enumeration keys=qnames.keys();
    while (keys.hasMoreElements()) 
    {
      QName aKey = (QName) keys.nextElement();
      if (aKey.equals(key))
        return (QName)qnames.get(aKey);
    }//while
    return null;
  } //QName
  
  /**
   * Given an array of SchemaHolder, retrieves one SchemaHolder
   * that has the targetNamespace identical to the input
   * targetNamespace.
   * Returns null if there is no match.
   */
  private SchemaHolder retrieveSchema(String targetNamespace) 
  {
    if (this.schemaHolderArray==null)
      return null;
    for (int i=0; i<this.schemaHolderArray.length; i++) 
    {
      if (this.schemaHolderArray[i].getTargetNamespace().equals(targetNamespace))
        return this.schemaHolderArray[i];
    }
    return null;
  } //retrieveSchema

  /**
   * Given the type reference, returns the Component
   * whose name matches the TypeReference name in the
   * SchemaHolder.  Returns null if there is no match.
   * <p>
   * Because this funciton is WSDL specific, the first
   * preference is given to a Type (ComplexType, SimpleType
   * or XSDType).  If the type is not found then the
   * next preference is given to an Element.
   * @return Type;
   * returns null if a match is not found.
   */
  private Type getComponent(TypeReference tr, SchemaHolder sh) 
  {
    if (sh==null)
      return null;
    for (int i=0; i<sh.getComponentCount(); i++) 
    {
      Component c = sh.getComponent(i);
      if (c instanceof ComplexType ||
          c instanceof SimpleType) 
      {
        Type t = (Type) c;
        if (t.getName().equals(tr.getName()) &&
        t.getTargetNamespace().equals(tr.getTargetNamespace()))
          return t;
      }
    }//for
    return null;
  }//getComponent.

  /**
   * Converts to Java each SchemaHolder in the array
   * of SchemaHolders (passed in the constructor).
   * <p>
   * The only component of interest is &lt;complexType&gt;
   * that is converted to a JavaBean.
   * @return SerializedHolder[] array that contains
   * the byte representation of all the necessary Java
   * Beans; returns null if no Java Beans are created
   * from the SchemaHolders.
   */
  public SerializedHolder[] toJava() throws IOException, JavaHolderException
  {
    serializedHolderVector=new Vector();
    
    if(schemaHolderArray != null) 
    {
      for (int i=0; i<this.schemaHolderArray.length; i++) 
      {
        SchemaHolder sh=schemaHolderArray[i];
        if (sh!=null) 
        {
          for (int j=0; j<sh.getComponentCount(); j++) 
          {
            Component c = sh.getComponent(j);
            this.componentToJava(c,null);
          } //for
        } //if  
      }//for
    }
    //Convert vector to array and return.
    if (serializedHolderVector!=null && serializedHolderVector.size()>0) 
    {
      SerializedHolder[] sholder = new SerializedHolder[serializedHolderVector.size()];
      for (int i=0; i<serializedHolderVector.size(); i++) 
      {
        SerializedHolder sh=(SerializedHolder) serializedHolderVector.elementAt(i);
        sholder[i]=sh;
      }
      return sholder;
    }
    else
      return null;
  }//toJava

  /**
   * Takes the schema component and converts it to a 
   * Java representation.
   * @param component the schema Component to convert
   * to a Java representation.
   * @param jHolder the JavaHolder that encapaulates
   * the physical Java class. If this parameter is null
   * a new JavaHolder is created.
   * @param preferredName the name for the Bean that encapsulates
   * the complexType.  Usually the name of the Bean is identical
   * to the name of the complexType.  In case the complextype is
   * anonymous, then the preferredName is used.
   */
  private void componentToJava(Component component, String preferredName) 
  throws JavaHolderException, IOException
  {
      if (component==null)
        return ;
      //Generate beans only if the complextype is not 
      //an array. 
      //Fix for bug found by Sourabh Ahuja:  If the complexType
      //is an anoynomous type, you should generate the beans
      //because there is no seperate declaration for the type.
      //debug
      if (component instanceof ComplexType &&
      ((!((ComplexType)component).isArray() &&
      !((ComplexType)component).isVector() &&
      !((ComplexType)component).isHashMap() &&
      !((ComplexType)component).isLiteralArray()) ||
      ((ComplexType)component).isAnonymous()))
      {
        ComplexType ct = (ComplexType) component;
        JavaHolder jHolder=new JavaHolder(ct.getName()==null?preferredName:ct.getName(),JavaHolder.BEAN,this.options==null?null:options.getPackageMapping(ct.getTargetNamespace()));
        complexTypeToJava(ct,jHolder);
        this.serializedHolderVector.add(jHolder.toJava());
        //Add the complexType to TypeMappingRegistry.
        addComplexTypeToTypeMap(jHolder.getPackage(), jHolder.getClassName(),
        ct.getName()!=null?ct.getName():preferredName, ct.getTargetNamespace());
      }
      else if (component instanceof Element &&
      ((Element)component).getType() instanceof ComplexType) 
      {
        Element e = (Element) component;
        ComplexType ct = (ComplexType)e.getType();
        this.componentToJava(ct, ((Element)component).getName());
      }
  } //componentToJava


  /**
   * Takes a complexType and converts it into JavaObject.
   */
  private void complexTypeToJava(ComplexType ct,JavaHolder jHolder)
  throws JavaHolderException, IOException
  {
    if ((ct.getContent()==null||ct.getContent().getContentCount()==0) &&
    (ct.getAttributeAndAttributeGroup()==null || 
    ct.getAttributeAndAttributeGroup().size()==0))
      return; //A ComplexType without any content is as good as null.
    if (jHolder==null)
      throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);

     /**
      * If the complexType is an extension of some other type,
      * then write it to JavaHolder.  If restriction, we do not
      * care because all the elements that are part of the complexType
      * are repeated in the complexType definition.
      */
      if (ct.getContent().getDerivation()==Content.EXTENSTION)
        this.extensionToJava(ct.getContent().getBaseType(), jHolder);

      /**
       * Retrieve the components from the Content.
       * Depending on SimpleContent or ComplexContent, the
       * component could be one of Attribute, AttributeGroupDefinition,
       * ModelGroup, ModelGroupDefinition or SimpleType
       */
      Vector v = ct.getContent().getContentList()==null?null:
      ct.getContent().getContentList();
      if (v!=null)
      {
        for (int i=0; i<v.size(); i++) 
        {
          Component c = (Component)v.elementAt(i);
          if (c instanceof Attribute)
            attributeToJava((Attribute)c, jHolder);
          else if (c instanceof AttributeGroupDefinition)
            attributeGroupToJava((AttributeGroupDefinition)c, jHolder);
          else if (c instanceof ModelGroup) 
            modelGroupToJava((ModelGroup)c, jHolder);
          else if (c instanceof ModelGroupDefinition) 
            modelGroupDefinitionToJava((ModelGroupDefinition)c, jHolder);
          else if (c instanceof SimpleType)
            simpleTypeToJava((SimpleType)c, jHolder);
        } //for
      }

      /**
       * The body of the complexType may contain attribute or 
       * attributeGroup. Process them here.
       */
       v=ct.getAttributeAndAttributeGroup();
       if (v!=null) 
       {
         for (int i=0; i<v.size(); i++) 
         {
           if (v.elementAt(i) instanceof Attribute)
            this.attributeToJava((Attribute)v.elementAt(i), jHolder);
           else if (v.elementAt(i) instanceof AttributeGroupDefinition)
            this.attributeGroupToJava((AttributeGroupDefinition)v.elementAt(i),jHolder);
         }
       }
  } //complexTypeToJava

  /**
   * Convert a simpleType to Java. This is only
   * used to call from complexType.
   */
  private void simpleTypeToJava(SimpleType st, JavaHolder jHolder) 
  throws JavaHolderException
  {
    if (jHolder==null)
      throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);
    if (st==null)
      return;
    String javaType=this.getQualifiedJavaType(st.getDataType());
    jHolder.setProperty(st.getName(), javaType);
  } //simpleTypeToJava
  
  /**
   * Converts a ModelGroupDefinition to Java.
   * A ModelGroupDefinition contains a set of
   * other ModelGroup.
   * @param mgd ModelGroupDefinition to convert to Java
   * @param jHolder JavaHolder to write the Java representation
   * to.
   */
  private void modelGroupDefinitionToJava(ModelGroupDefinition mgd, JavaHolder jHolder) 
  throws JavaHolderException, IOException
  {
    if (jHolder==null)
      throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);
    if (mgd==null)
      return;
    ModelGroupDefinition realmgd=mgd;
    if (mgd.isReference()) 
    {
      for (int i=0; i<this.schemaHolderArray.length; i++) 
      {
        realmgd=schemaHolderArray[i].getNormalizedModelGroupDefinition(mgd);
        if (realmgd!=null)
          break;
      }
    }//if
    if (realmgd==null || realmgd.isReference())
        throw new JavaHolderException("ERROR_JAVAHOLDER_004:"+Constants.ERROR_JAVAHOLDER_004);
    Vector v = realmgd.getContent();
    if (v==null) return;
    for (int i=0; i<v.size(); i++) 
    {
      if (v.elementAt(i) instanceof ModelGroup)
        this.modelGroupToJava((ModelGroup)v.elementAt(i), jHolder);
    }
  } //modelGroupDefinitionToJava
  
  /**
   * Takes a ModelGroup and converts it to
   * Java presentation.  A Model Group contains
   * other ModelGroup or Element.
   */
  private void modelGroupToJava(ModelGroup mg, JavaHolder jHolder) 
  throws JavaHolderException, IOException
  {
    if (jHolder==null)
      throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);
    if (mg==null)
      return;
    List list=mg.getContent();
    for (int i=0; list!=null&&i<list.size(); i++) 
    {
      Object o = list.get(i);
      if (o instanceof Element)
        elementToJava((Element)o, jHolder);
      else if (o instanceof ModelGroup)
        modelGroupToJava((ModelGroup)o, jHolder);
    }//for
  }//modelGroupToJava
  
  /**
   * Takes an Element (a Component) and converts it to
   * a Java representation.
   * @param element The element to encapsulate in Java.
   * @param jHolder the Java Holder to write the Java 
   * representation to.
   */
  private void elementToJava(Element element, JavaHolder jHolder) 
  throws JavaHolderException, IOException
  {
    if (jHolder==null)
      throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);
    Element realElement=element;
    if (element.isReference()) 
    {
      for (int i=0; i<this.schemaHolderArray.length; i++) 
      {
        realElement=schemaHolderArray[i].getNormalizedElement(element);
        if (realElement!=null)
          break;
      }
    }//if
    if (realElement==null || realElement.isReference())
        throw new JavaHolderException("ERROR_JAVAHOLDER_003:"+Constants.ERROR_JAVAHOLDER_003);
    /**
     * The element may contain a anonymous complextype.
     * Process this.
     */
    this.componentToJava(realElement,realElement.getName());
    String javaType=this.getQualifiedJavaType(realElement);
    jHolder.setProperty(realElement.getName(), realElement.getMaxOccurs()>1?javaType+"[]":javaType);
  }//elementToJava

  
  /**
   * Converts a AttributeGroupDefinition to Java representation.
   * @param AttributeGroupDefinition to converto to Java
   * @param jHolder to write the Java representation to.
   */
  private void attributeGroupToJava(AttributeGroupDefinition agd, JavaHolder jHolder)
  throws JavaHolderException
  {
      AttributeGroupDefinition realAGD=agd;
      if (jHolder==null)
        throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);
      if (agd.isReference())
      {
        // Get the real attribute.
        for (int i=0; i<this.schemaHolderArray.length; i++) 
        {
          realAGD=schemaHolderArray[i].getNormalizedAttributeGroupDefinition(agd);
          if (realAGD!=null)
            break;
        }//for
      }//if

      if (realAGD==null || realAGD.isReference())
        throw new JavaHolderException("ERROR_JAVAHOLDER_002:"+Constants.ERROR_JAVAHOLDER_002);
      Vector v = realAGD.getContent();
      for (int i=0; v!=null && i<v.size(); i++) 
      {
        if (v.elementAt(i) instanceof Attribute)
          this.attributeToJava((Attribute)v.elementAt(i),jHolder);
        else if (v.elementAt(i) instanceof AttributeGroupDefinition)
          this.attributeGroupToJava((AttributeGroupDefinition)v.elementAt(i), jHolder);
      }
  } //attributeGroupDefinition
  
  /**
   * Takes a attribute and converts to Java representation.
   * Attributes are converted to properties in a Bean.
   * @param attribute the attribute to convert as a bean.
   * @param jHolder the JavaHolder to write the attribute to.
   * @throws JavaHolderException if the jHolder is null
   */
  private void attributeToJava(Attribute attribute, JavaHolder jHolder)
  throws JavaHolderException
  {
    Attribute realAttribute=attribute;
    if (jHolder==null)
      throw new JavaHolderException("ERROR_JAVAHOLDER_001:"+Constants.ERROR_JAVAHOLDER_001);
    if (attribute.isReference())
    {
      // Get the real attribute.
      for (int i=0; i<this.schemaHolderArray.length; i++) 
      {
        realAttribute= realAttribute=schemaHolderArray[i].getNormalizedAttribute(attribute);
        if (realAttribute!=null)
          break;
      }
    }
    if (realAttribute==null || realAttribute.isReference())
      throw new JavaHolderException("ERROR_JAVAHOLDER_002:"+Constants.ERROR_JAVAHOLDER_002);

    String javaType=this.getQualifiedJavaType(realAttribute.getType());
    jHolder.setProperty(realAttribute.getName(), javaType);
  } //attributeToJava

  /**
   * Utility method to determine if a Java type is a first
   * class object or is a primitive type.  This is useful
   * to determine if a complexType should extend a class
   * or should just have it as a property.
   * <p>
   * Returns true if the String passed in represents a
   * first class object; false otherwise
   * 
   */
   private boolean isTypeObject(String className) 
   {
     if (className==null || className.startsWith("java.lang.")||
     className.equals("java.util.Date") ||
     className.equals("com.wingfoot.soap.encoding.Base64") ||
     className.equals("com.wingfoot.soap.encoding.HexBinary") ||
     className.equals("java.math.BigDecimal"))
      return false;
     else if (this.primitiveMap.get(className)!=null)
      return false;
     else
      return true;
   } //isTypeObject

   /**
    * Utility method to process an ComplexType extension.
    * If a ComplexType is extended then it is an inheritence
    * in Java else it is a property named baseValue.
    * @param baseType the TypeReference.  This is the value
    * of the base attribute in a <extension> element.
    * @param jHolder the JavaHolder to write the Java stub to.
    */
    private void extensionToJava(TypeReference baseType, JavaHolder jHolder) 
    throws JavaHolderException
    {
      String str = this.getQualifiedJavaType(baseType);
      if (this.isTypeObject(str))
        jHolder.setExtend(str);
      else
        jHolder.setProperty("bodyValue", str);
    }//extensionToJava

    /**
     * Takes a complexType and associates the fully qualified
     * Java object that corresponds to the complexType and
     * associates it with the complextype name and namespace.
     * @param packageName the name of the bean package.
     * @param className the name of the bean.
     * @param complexTypeName the name of the complexTypeObject;
     * if the complexType is anonymous, it is the element name.
     * @param complexTypeNS the namespace of the complexType;
     * if the complexType is anonymous, it is the element namespace.
     */
    private void addComplexTypeToTypeMap(String packageName, String className,
    String complexTypeName, String complexTypeNS) 
    {
      String qualifiedClass=className;
      if (packageName!=null)
        qualifiedClass=packageName+"."+className;
      if (this.typeMap==null)
        typeMap=new Vector();
      typeMap.add(new String[] {complexTypeNS,complexTypeName,qualifiedClass});
    }//addComplexTypeToTypeMap

    /**
     * Returns a Vector of type maps.  A type map is
     * a association between a ComplexType and the 
     * corresponding Java object generated.
     * <p>
     * Each element of the Vector is a String[]. The
     * String[] contains :
     * <li> 0 - complexType namespace.
     * <li> 1 - complexType name.
     * <li> 2 - fully qualified class name.
     * @return Vector of type maps.
     */
    public Vector getTypeMap() 
    {
      return this.typeMap;
    }

    /**
     * Given an element with href, returns the
     * refered Element
     */
    private Element getNormalizedElement(Element e) 
    {
      if (e==null)
        return null;
      if (!e.isReference())
        return e;
      for (int i=0; i<this.schemaHolderArray.length; i++)
      {
        Element newElement=schemaHolderArray[i].getNormalizedElement(e);
        if (newElement!=null)
          return newElement;
      }//for
      return null;
    }//getNormalizedElement
} //class

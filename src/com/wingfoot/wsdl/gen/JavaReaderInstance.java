package com.wingfoot.wsdl.gen;

import java.util.*;
import java.lang.reflect.*;
import com.wingfoot.*;
import com.wingfoot.tools.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.wsdl.soap.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.xml.schema.types.*;
import com.wingfoot.xml.schema.groups.*;

/**
 * A concrete implementation of the JavaReader.
 * It converts a Java class into a WSDLHolder.  
 */
 
public class JavaReaderInstance implements JavaReader 
{
  WSDLHolder wh=null;
  /**
   * The counter below keeps track of the suffix for
   * a parameter.  Parvus generates unique parameter
   * names throughout the WSDL.
   */
   int parameterCounter=0;
  /**
   * Constructor to create a JavaReaderInstance
   */
  public JavaReaderInstance()
  {
  }

  /**
   * Creates a WSDLHolder from a Java Class.
   * @param classToConvert - This is the class to convert into a WSDLHolder
   * @param options - Options passed in by the client
   * @exception WSDLException - A WSDLException is be thrown if an error occurs 
   * while creating a WSDLHolder
   */
  public WSDLHolder parse(Class classToConvert, Options options) 
  throws WSDLException, SchemaException
  {
     wh=new WSDLHolder();
     wh.setTargetNamespace(options.getNamespaceMapping(classToConvert.getPackage()==null?null:classToConvert.getPackage().getName()));
     /**
      * Create a PortType for the Class passed in.
      */
      this.createPortType(classToConvert, options);
     /**
      * Create a binding for each portType.
      */
     this.createBinding(wh.getPortType(new QName(options.getNamespaceMapping(classToConvert.getPackage()==null?null:classToConvert.getPackage().getName()),
     this.getUnQualifiedClassName(classToConvert.getName()))), options);

     /**
      * Create the Service element.
      */
     this.createService(classToConvert, options);
     return wh;
  }//parse(Class, options)

  /**
   * Creates the portType element for the classtoConvert.
   * Additionally, populates the portType with the 
   * necessary operations.
   */
   private void createPortType(Class classToConvert, Options options)
   throws WSDLException, SchemaException
   {
      /**
       * Operations names are equals when the following are equal
       * 1.  The operation name;
       * 2.  The Operation's input message name;
       * 3.  The Operation's output message name;
       * Parvus' policy is to generate unique message names
       * for each operation's input and output message.
       */
       int inputMessageNameCTR=0;
       int outputMessageNameCTR=0;
     /**
      * Create a PortType for the Class passed in.
      */
     PortType pt = new PortType(new QName(options.getNamespaceMapping(classToConvert.getPackage()==null?null:classToConvert.getPackage().getName()),
     this.getUnQualifiedClassName(classToConvert.getName())));
     /**
      * Create a Operation for each method in the class.
      * Place the operation in the PortType.
      */
     Vector methods = getMethodsToExposeAsWebService(classToConvert);
     for (int i=0; methods!=null && i<methods.size(); i++) 
     {
        Method m = (Method) methods.elementAt(i);
        Operation o = new Operation(m.getName());
        pt.setOperation(o);
        /**
         * Get the Message for the operation. Index 0 is
         * input Index 1 is output. Place the Message in 
         * Operation.
         */
        Message[] message = this.getMessage(m, options, options.getNamespaceMapping(classToConvert.getPackage()==null?null:classToConvert.getPackage().getName()));
        o.setInputMessage(message[0]);
        o.setOutputMessage(message[1]);
        o.setInputMessageName("inputMessageName"+ ++inputMessageNameCTR);
        o.setOutputMessageName("outputMessageName"+ ++outputMessageNameCTR);
     }//for
     wh.setPortType(pt);
   }//createPortType

  /**
   * Creates the SOAPOperation for a BindingOperation.
   * The SOAPAction is defaulted to the Operation name.
   * @param o Operation for which a soapAction element is
   * required.
   * @param options the Options provided by the user.
   * @return SOAPOperation for the input Operation.
   */
  private SOAPOperation createSOAPOperation(Operation o, Options options) 
  {
    SOAPOperation so = new SOAPOperation();
    so.setSoapAction(o.getName());
    so.setStyle(options.isStyleDocument()?SOAPBinding.DOCUMENT:SOAPBinding.RPC);
    return so;
  }//SOAPOperation
  
  /**
   * Given a PortType, creates and returns the
   * binding for the PortType.
   * @param portType the PortType for which a 
   * Binding is required.
   * @return Binding for the input PortType.
   */
   private Binding createBinding(PortType pt, Options options) 
   throws WSDLException
   {
     Binding b = new Binding(new QName(wh.getTargetNamespace(),pt.getName().getLocalPart()+"Binding"),pt);
     wh.setBinding(b);
     SOAPBinding soapBinding=this.createSOAPBinding(options);
     b.setBindingExtension(soapBinding);
     
     for (int i=0; i<pt.getOperationCount(); i++) 
     {
       Operation o=pt.getOperation(i);
       BindingOperation bo = new BindingOperation(o);
       //Put the BindingOperation inside the Binding element.
       b.setBindingOperation(bo);
       //Create the <soap:operation> element
       bo.setExtension(this.createSOAPOperation(o,options));
       //Create the input MessageFormat.
       SOAPMessage sm = new SOAPMessage(SOAPMessage.BODY);
       sm.setUse(options.isUseLiteral()?SOAPMessage.LITERAL:SOAPMessage.ENCODED);
       if (!options.isUseLiteral()) 
       {
         sm.setEncodingStyle(Constants.SOAP_ENCODING_STYLE);
         //sm.setNamespaceURI(wh.getTargetNamespace());
       }
       if (soapBinding.getStyle()==SOAPBinding.RPC)
        sm.setNamespaceURI(wh.getTargetNamespace());
       //Put the message format to the BidingOperation.
       //SHOULD NOT ACCEPT AN ARRAY BUT JUST ONE MESSAGEFORMAT
       //THIS IS A BUG IN THE BINDIGOPERATION API
       bo.setInputMessageFormat(new MessageFormat[] {sm});
       bo.setOutputMessageFormat(new MessageFormat[] {sm});
     }//for
     return b;
   }//createBinding

  /**
   * Creates the soap:binding element in the <binding> element.
   * Currently the transport is defaulted to HTTP and the style
   * is determined from options.
   */
   private SOAPBinding createSOAPBinding(Options options) 
   throws WSDLException
   {
      SOAPBinding sb = new SOAPBinding();
      if (options.isStyleDocument())
        sb.setStyle(SOAPBinding.DOCUMENT);
      else
        sb.setStyle(SOAPBinding.RPC);
      sb.setTransport(Constants.SOAP_HTTP);
      return sb;
   }//SOAPBinding
   
  /**
   * Given a Method determines and returns the Message
   * for the Method.  Adds the Messages to the WSDLHolder
   * @param method the Method to inspect and determine the
   * messages
   * @param options Options that encapsulates the options
   * provided by the client.
   * @param namespace the targetNamespace for the element.
   * The namespace depends on the package of the class that
   * contains the message.
   * @return Message[] the 0th index of the Array contains
   * the input Message; the 1st index contains the output
   * Message.
   */
   private Message[] getMessage(Method method, Options options, String namespace)
   throws WSDLException, SchemaException
   {
     String inputMessageName=method.getName()+"Request";
     String outputMessageName=method.getName()+"Response";
     //Make sure the inputMessage is not duplicated
     int ctr=0;
     while (true) 
     {
       if (wh.getMessage(new QName(namespace, inputMessageName))!=null)
        inputMessageName=method.getName()+"Request"+(++ctr);
       else
        break;
     }
     //Make sure the outputMessage is not duplicated
     ctr=0;
     while (true) 
     {
       if (wh.getMessage(new QName(namespace, outputMessageName))!=null)
        outputMessageName=method.getName()+"Response"+(++ctr);
       else
        break;
     }
     
     Class[] inputParam = method.getParameterTypes();
     Message inputMessage = new Message(new QName(namespace,inputMessageName));
     for (int i=0; i<inputParam.length; i++) 
     {
       int partType=Part.TYPE;
       TypeReference tr=this.getType(inputParam[i], options);
       if (options.isUseLiteral()) 
       {
         tr=this.wrapTypeAsElement(tr, method.getName(), namespace);
         partType=Part.ELEMENT;
       }
       Part p = new Part("parameter"+(++parameterCounter),tr,partType);
       p.setJavaTypeName(inputParam[i].getName());
       inputMessage.setMessagePart(p);
     }//for
     wh.setMessage(inputMessage);
     
     Message outputMessage = new Message(new QName(namespace,outputMessageName));
     Class outputParam = method.getReturnType();
     if (!outputParam.getName().equals("void")) 
     {
         int partType=Part.TYPE;
         TypeReference tr=this.getType(outputParam, options);
         if (options.isUseLiteral()) 
         {
           tr=this.wrapTypeAsElement(tr, method.getName(), namespace);
           partType=Part.ELEMENT;
         }
         Part p = new Part("return",tr,partType);
         p.setJavaTypeName(outputParam.getName());
         outputMessage.setMessagePart(p);
     }
     wh.setMessage(outputMessage);
     return new Message[] {inputMessage, outputMessage};
   } //getMessage

   /**
    * Encapsulates a given Class as a Type.  This
    * method is invoked to encapsulate a parameter
    * Class as a Type.
    * <p>
    * If the parameter is encapsulated as a complexType
    * then the complexType is written to the SchemaHolder
    * and put in WSDLHolder.  The TypeReference to the
    * complexType is then returned.
    * @param parameter the Class that represents a
    * parameter to a method that is to be encapsulated
    * as a type.
    * @param options the parameters passed in by the
    * client.  Of interest is the package name to
    * namespace mapping.
    * @return Type a concrete instance of Type that
    * encapsulates the parameter.
    */
   private TypeReference getType(Class parameter, Options options) 
   throws SchemaException, WSDLException
   {
     if (parameter.isPrimitive())
      return new TypeReference(Constants.SOAP_SCHEMA, parameter.getName());
     else if (parameter.getName().startsWith("java.lang.")) 
     {
       String lastBit=this.getUnQualifiedClassName(parameter.getName()).toLowerCase();
       if (lastBit.equals("integer"))
        lastBit="int";
      else if (lastBit.equals("object"))
        lastBit="ur-type";
       return new TypeReference(Constants.SOAP_SCHEMA, lastBit);
     }
     else if (parameter.getName().equals("java.util.Date"))
        return new TypeReference(Constants.SOAP_SCHEMA, "dateTime");
     else if (parameter.getName().equals("java.util.Hashtable") ||
	      parameter.getName().equals("java.util.HashMap"))
     {
       ComplexType ct = this.deserializeHashtable();
       this.addComplexTypeToSchema(ct);
       return new TypeReference(ct.getTargetNamespace(), ct.getName());
     }
     else if (parameter.getName().equals("java.util.Vector"))
     {
       ComplexType ct = this.deserializeVector();
       this.addComplexTypeToSchema(ct);
       return new TypeReference(ct.getTargetNamespace(), ct.getName());
     }
     else if (parameter.getName().equals("java.math.BigDecimal"))
       return new TypeReference(Constants.SOAP_SCHEMA, "decimal");
     else if (parameter.getName().equals("com.wingfoot.soap.encoding.Base64") ||
     (parameter.isArray() && parameter.getComponentType().getName().equals("byte")))
       return new TypeReference(Constants.SOAP_SCHEMA, "base64Binary");
     else if (parameter.getName().equals("com.wingfoot.soap.encoding.HexBinary"))
       return new TypeReference(Constants.SOAP_SCHEMA, "hexBinary");
     else if (parameter.isArray()) 
     {  
       ComplexType ct = this.deserializeArray(parameter, options);
       this.addComplexTypeToSchema(ct);
       return new TypeReference(ct.getTargetNamespace(), ct.getName());
     }
     else if (isBean(parameter))
     {
       //A custom Java object. First check if the complexType already exists in Schema
       SchemaHolder sh=wh.getType(options.getNamespaceMapping(parameter.getPackage()==null?null:parameter.getPackage().getName()));
       if (sh!=null && (sh.getComplexType(this.getUnQualifiedClassName(parameter.getName()),
       options.getNamespaceMapping(parameter.getPackage()==null?null:parameter.getPackage().getName()))!=null)) 
       {
         //The bean already exists. Just return a reference.
         return new TypeReference(
         options.getNamespaceMapping(parameter.getPackage()==null?null:parameter.getPackage().getName()), 
         this.getUnQualifiedClassName(parameter.getName()));
       }
       else 
       {
         ComplexType ct=deserializeBean( parameter,  options);
         this.addComplexTypeToSchema(ct);
         return new TypeReference(ct.getTargetNamespace(), ct.getName());
       }
     }//custom java object 
     else
      throw new WSDLException("ERROR_WSDL_028:"+Constants.ERROR_WSDL_028 + " " +parameter.getName());
   } //getType

   private boolean isBean(Class aClass) 
   {
     if (aClass==null)
      return false;
     Class[] interfaces = aClass.getInterfaces();
     for (int i=0; interfaces!=null && i<interfaces.length; i++) 
     {
       if (interfaces[i].getName().equalsIgnoreCase("java.io.serializable"))
        return true;
     }
     return false;
   }
   /**
    * Takes a complexType and wraps it inside an
    * $lt;element&gt.  Pleces the <element> in
    * the SchemaHolder.  If the element exists,
    * creates one with a number suffixed to the 
    * end.
    * <p>
    * The wrapping is required for literal style.
    * @param type Type that has to be wrapped inside
    * an element.
    * @param targetNamespace the namespace that the
    * element must belong to.
    * @return TypeReference to the element just created.
    */
   private TypeReference wrapTypeAsElement(TypeReference type, String preferedName
   , String targetNamespace) 
   throws SchemaException
   {
      SchemaHolder sh=null;
      int ctr=0;
      if (preferedName==null)
        preferedName="param";
      //Get the correct SchemaHolder based on the namespace
      sh =wh.getType(targetNamespace);
      if (sh==null) 
      {
        sh=new SchemaHolder();
        sh.setTargetNamespace(targetNamespace);
        wh.setType(sh);
      }
      while (true) 
      {
        Element e=sh.getElement(preferedName+(++ctr), targetNamespace);
        if (e==null)
          break;
      } //while
      Element e = new Element(preferedName+ctr, targetNamespace, 
      new TypeReference(type.getTargetNamespace(), type.getName()),
      true);
      sh.setComponent(e);
      if (!e.getType().getTargetNamespace().equals(sh.getTargetNamespace())&&
      !e.getType().getTargetNamespace().equals(Constants.SOAP_SCHEMA))
        sh.setImport(e.getType().getTargetNamespace(),null);
      return new TypeReference(e.getNamespace(), e.getName());
   }//wrapTypeAsElement

   /**
    * Adds a ComplexType to the SchemaHolder.  The
    * ComplexType is added only if another ComplexType
    * with same name and namespaceURI does not exist.
    * <p>
    * The ComplexType is added to a SchemaHolder with the
    * same namespace as the namespace of the ComplexType.
    * If such a SchemaHolder does not exist, one is created
    * and added to the SchemaHolder
    * @param ct ComplexType to add.
    */
   private void addComplexTypeToSchema(ComplexType ct) 
   {
       SchemaHolder sh = wh.getType(ct.getTargetNamespace());
       if (sh==null) 
       {
          sh=new SchemaHolder();
          sh.setTargetNamespace(ct.getTargetNamespace());
          wh.setType(sh);
       }
       if (sh.getComplexType(ct.getName(), ct.getTargetNamespace())==null) 
       {
         sh.setComponent(ct);
       }
       //Add the necessary import statemnts if Array
       if (ct.isArray())
        sh.setImport(Constants.SOAP_ENCODING_STYLE, null);
        
       //If there are any <element> in the complexType that is
       // in someother namespace, import that here.
       Content c = ct.getContent();
       if (c!=null && c.getBaseType()!=null && !c.getBaseType().getTargetNamespace().equals(Constants.SOAP_SCHEMA)
       && !c.getBaseType().getTargetNamespace().equals(sh.getTargetNamespace()))
          sh.setImport(c.getBaseType().getTargetNamespace(), null);
        
       Vector v = c==null? null:c.getContentList();
       for (int i=0; v!=null && i<v.size(); i++) 
       {
         if (v.elementAt(i) instanceof ModelGroup) 
         {
           ModelGroup mg = (ModelGroup) v.elementAt(i);
           List mgContent = mg.getContent();
           for (int j=0; mgContent!=null&&j<mgContent.size(); j++) 
           {
             if (mgContent.get(j) instanceof Element) 
             {
               Element e = (Element) mgContent.get(j);
               Type t = e.getType();
               if (t.getTargetNamespace()!=null && !t.getTargetNamespace().equals(Constants.SOAP_SCHEMA) &&
               !t.getTargetNamespace().equals(sh.getTargetNamespace()))
                  sh.setImport(t.getTargetNamespace(), null);
             }
           }
         }
       }
   } //addComplexTypeToSchema
  
   /**
    * Encapsulates a hashtable as a ComplexType.
    * @return ComplexType hashtable encapsulated as a
    * ComplexType
    */
   private ComplexType deserializeHashtable()
   throws SchemaException, WSDLException
   {

      //This block of code creates the name/value pair representation
      XSDType xsdType = new XSDType(new QName(Constants.SOAP_SCHEMA, "anyType"));
      ModelGroupImplementation mgi = new ModelGroupImplementation();
      mgi.setModelGroupType(ModelGroup.ALL);
      Element keyElement = new Element("key", null, xsdType, false);
      Element valueElement = new Element("value", null, xsdType, false);
      mgi.setContent(keyElement);
      mgi.setContent(valueElement);

      //add the name/value pair rep to a complex content
      Content hashContent = new ComplexContent(Content.RESTRICTION);
      hashContent.setBaseType(new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
      hashContent.addContent(mgi);

      //add the name/value pair complex content to a complex type
      ComplexType ct = new ComplexType(null, Constants.SOAP_SCHEMA, hashContent, /*true*/ false);
      Element itemElement = new Element("item", null, ct, /*true*/ false);
      itemElement.setMinOccurs(0);
      itemElement.setMaxOccurs(Integer.MAX_VALUE);
      
      ModelGroupImplementation mg = new ModelGroupImplementation();
      mg.setContent(itemElement);
      
      Content content = new ComplexContent(Content.RESTRICTION);
      content.setBaseType(new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
      content.addContent(mg);
      return new ComplexType("Map", Constants.DEFAULT_NAMESPACE, content, true);
   }

   /**
    * Encapsulates a vector as a ComplexType.
    * @return ComplexType vector encapsulated as a
    * ComplexType
    */
   private ComplexType deserializeVector()
   throws SchemaException, WSDLException
   {
      XSDType xsdType = new XSDType(new QName(Constants.SOAP_SCHEMA, "anyType"));
      ModelGroupImplementation mgi = new ModelGroupImplementation();
      mgi.setModelGroupType(ModelGroup.SEQUENCE);
      Element item = new Element("item", null, xsdType, false);
      item.setMinOccurs(0);
      item.setMaxOccurs(Integer.MAX_VALUE);
      mgi.setContent(item);

      Content content = new ComplexContent(Content.RESTRICTION);
      content.setBaseType(new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
      content.addContent(mgi);
      return new ComplexType("Vector", Constants.DEFAULT_NAMESPACE, content, true);
   }
   
  
   /**
    * Encapsulates an Array as a ComplexType.
    * @param parameter Class that encapsulates an
    * Array.
    * @param options the Options provided by the 
    * client.
    * @return ComplexType array encapsulated as a 
    * ComplexType.
    */
   private ComplexType deserializeArray(Class parameter, Options options) 
   throws SchemaException, WSDLException
   {
      //Get the base class of the Array.
      Class baseClass=parameter.getComponentType();
      //Take care of link lists here.
    /**
      * A small feature left out. If a bean (Employee) contains
      * an array of the same bean as a property (Employee[])
      * the tool barfs and throw a StackOverflow error.
      * To this method, we have to pass in the class that contains
      * the array.
      */
      TypeReference tr = baseClass.getName().equals(parameter.getName())?
      new TypeReference(options.getNamespaceMapping(baseClass.getPackage()==null?null:baseClass.getPackage().getName()),
      this.getUnQualifiedClassName(baseClass.getName())):
      this.getType(baseClass, options);

      Content content=new ComplexContent(Content.RESTRICTION);
      if (options.isUseLiteral())
      {
        //Wrap a LiteralArray. Let the basetype for Content as default
        //The default is "ur-type".
        ModelGroupImplementation mgi = new ModelGroupImplementation();
        Element e = new Element("arrayItem",tr.getNamespaceURI(),tr,false);
        e.setMaxOccurs(Integer.MAX_VALUE);
        mgi.setContent(e);
        content.addContent(mgi);
      }
      else
      {
        //Get the parameter Class name
        Attribute attr = new Attribute(new QName(Constants.SOAP_ENCODING_STYLE, "arrayType"));
        attr.setAdditionalAttributes(new QName(Constants.WSDL_NAMESPACE, "arrayType"),
        new QName(tr.getTargetNamespace(), tr.getName()+"[]"));
        content.setBaseType(new TypeReference(Constants.SOAP_ENCODING_STYLE, "Array"));
        content.addContent(attr);
      }
      return new ComplexType("ArrayOf"+ this.getUnQualifiedClassName(baseClass.getName()),
      baseClass.isPrimitive()||baseClass.getName().equals("java.lang.String")||
      baseClass.getName().equals("java.lang.Object") || baseClass.getName().equals("java.util.Date")
      ? Constants.SOAP_SCHEMA:
      options.getNamespaceMapping(baseClass.getPackage()==null?null:baseClass.getPackage().getName()),
      content,true);
   } //deserializeArray

   /**
    * Takes a custom Java object and returns back a
    * ComplexType that encapsulates the Java object.
    */
   private ComplexType deserializeBean(Class parameter, Options options) 
   throws SchemaException, WSDLException
   {
    //Stores the property added so that it is not duplicated.
    //this is the method name without the set and the get.
    
     Vector propertyAdded=new Vector();
     
     ModelGroupImplementation mgi = new ModelGroupImplementation();
     Vector v = getMethodsToExposeAsWebService(parameter);
     
     if (v!=null) 
     {
       //Process each set and get method in the bean.
       for (int i=0; i<v.size(); i++) 
       {
         Method m = (Method) v.elementAt(i);
         String methodName=null;
         if ((m.getName().startsWith("get")|| m.getName().startsWith("set"))&&m.getName().length()>3) 
         {
          //Get the methodName without the get or set.
           methodName=m.getName().substring(3);
           methodName=methodName.toLowerCase().charAt(0) +
           methodName.substring(1);
              
           if (! isStringPresent(propertyAdded, methodName)) 
           {
             propertyAdded.add(methodName);
             if (m.getName().startsWith("get")&& !(m.getReturnType().getName().equals("void"))) 
             {
               //Get the return parameter.  Put this in the bean.
               //Make sure that the return parameter is not an instance
               //of the bean itself as is possible in a link list.

               TypeReference tr=m.getReturnType().getName().equals(parameter.getName())?
               new TypeReference(options.getNamespaceMapping(m.getReturnType().getPackage()==null?null:m.getReturnType().getPackage().getName()),
               this.getUnQualifiedClassName(m.getReturnType().getName())):
               this.getType(m.getReturnType(), options);
               Element mgiElement=new Element(methodName,tr.getNamespaceURI(),tr,false);
               if (m.getReturnType().getName().equals(parameter.getName()))
                mgiElement.setMinOccurs(0);
               mgi.setContent(mgiElement);
             } //if get
             else
             {
               //A set method.  Check for the input parameters
               Class[] paramClass = m.getParameterTypes();
               for (int j=0; j<paramClass.length; j++) 
               {
                 //Make sure that the return parameter is not an instance
                 //of the bean itself as is possible in a link list.
                 TypeReference tr=paramClass[j].getName().equals(parameter.getName())?
                 new TypeReference(options.getNamespaceMapping(paramClass[j].getPackage().getName()),
                 this.getUnQualifiedClassName(paramClass[j].getName())):             
                 this.getType(paramClass[j], options);
                 Element mgiElement=new Element(methodName, tr.getNamespaceURI(), tr, false);
                 if (paramClass[j].getName().equals(parameter.getName()))
                  mgiElement.setMinOccurs(0);
                 mgi.setContent(mgiElement);
               }
             } //if set
           } //if method not added
         } //if starts with get or set
       } //for
     } //if v!=null
     
     /**
      * If there are any public methods, add them here
      * to the content model. The content model is the
      * complexContent-ModelGroupImplementation combo.
      */
      Field[] f = parameter.getFields();
      for (int i=0; f!=null&&i<f.length; i++) 
      {
        Field aField = f[i];
        if (! isStringPresent(propertyAdded, aField.getName())&&
        parameter.getName().equals(aField.getDeclaringClass().getName())) 
        {
               propertyAdded.add(aField.getName());
               TypeReference tr=aField.getType().getName().equals(parameter.getName())?
               new TypeReference(options.getNamespaceMapping(aField.getType().getPackage()==null?null:aField.getType().getPackage().getName()),
               this.getUnQualifiedClassName(aField.getType().getName())):
               this.getType(aField.getType(),options);

               //TypeReference tr = this.getType(aField.getType(),options);
               mgi.setContent(new Element(aField.getName(),tr.getNamespaceURI(),tr,false));
        }
      } //for field
     
     Content content=null;
     Class superClass = this.getSuperClass(parameter);
     if (superClass!=null) 
     {
       TypeReference superReference=this.getType(superClass,options);
       content = new ComplexContent(Content.EXTENSTION);
       content.setBaseType(new TypeReference(superReference.getTargetNamespace(),superReference.getName()));
     }
     else 
     {
       content = new ComplexContent(Content.RESTRICTION);
       content.setBaseType(new TypeReference(Constants.SOAP_SCHEMA, "ur-type"));
     }
     content.addContent(mgi);
     return new ComplexType(this.getUnQualifiedClassName(parameter.getName()),
     options.getNamespaceMapping(parameter.getPackage()==null?null:parameter.getPackage().getName()),
     content, true);
   }//deserializeBean

   /**
    * Retrieves the superclass for a given class.
    * If the super class is Object or if the 
    * superclass belongs to java.* package,
    * then it is not included.
    * @param aClass the class for which a superclass
    * is needed.
    * @return Class representing the superclass; null
    * if there is no superclass.
    */
    private Class getSuperClass(Class aClass) 
    {
      Class c = aClass.getSuperclass();
      if (c!=null && !c.getName().startsWith("java."))
        return c;
      else return null;
    }

   /**
    * Checks to see if a String is present in an Vector
    * of Strings. Returns true if present; false if not.
    */
    private boolean isStringPresent(Vector v, String s) 
    {
      if (v==null||s==null)
        return false;
      for (int i=0; i<v.size(); i++) 
      {
        if (((String)v.elementAt(i)).equals(s))
          return true;
      }
      return false;
    }//isStringPresent
    
  /**
   * Inspects a class and determines the methods that
   * may be exposed as a web service.  Does NOT return
   * inherited methods.  Returns public methods.
   * @return Vector each element of the Vector is a 
   * Method; null if no method may be exposed.
   */
  private Vector getMethodsToExposeAsWebService(Class classToConvert) 
  {
    Vector returnValue=null;
    Method[] m = classToConvert.getMethods();
    if (m==null)
      return null;
    for (int i=0; i<m.length; i++) 
    {
      if (classToConvert.getName().equals(m[i].getDeclaringClass().getName())) 
      {
        if (returnValue==null) returnValue=new Vector();
        returnValue.add(m[i]);
      }//if
    }//for
    return returnValue;
  }//getMethodsToExposeAsWebService
  
  /** 
   * Introspects a class and creates a Service object.
   * The Service object is set in the WSDLHolder
   * @param classToConvert the Class to convert into a WSDL
   * @param options the options class which encapsulated user input
   * @return Service - A service object is returned upon success of 
   * this method
   */ 
  private void createService(Class classToConvert, Options options) 
  {
    String unQualifiedClassName = getUnQualifiedClassName(classToConvert.getName());
    Service service = new Service(unQualifiedClassName+"Service");
    Binding[] binding = wh.getBinding(); /* get the bindings from WSDLHolder */
    Port[] port = new Port[binding.length];
    for(int i = 0; binding!=null&&i<binding.length; i++){
      port[i]=createPort(classToConvert, binding[i], options);
    }
    service.setPort(port);
    wh.setService(service);
  }

  /**
   * Creates a port object.  The port object contains
   * the endpoint for the service being deployed.
   * @param binding the binding object
   * @param options options contain the endpoint specified by the end user
   * @return Port a newly created port object 
   */
  private Port createPort(Class classToConvert, Binding binding, Options options){
    String ns=options.getNamespaceMapping(classToConvert.getPackage()==null?null:classToConvert.getPackage().getName());
    String portName = binding.getBindingName().getLocalPart()+"Port";
    Port port = new Port(portName, new QName(binding.getBindingName().getNamespaceURI(), binding.getBindingName().getLocalPart()));
    port.setEndPoint(createSOAPAddress(classToConvert,options));
    return port;
  }

  /**
   * Creates a SOAPAddress object
   * @param options the options object contains the endpoint specified
   * by the end user
   */
  private SOAPAddress createSOAPAddress(Class classToConvert, Options options){
    SOAPAddress soapAddress = new SOAPAddress();
    String deleteMe = classToConvert.getName();
    String suffix=null;
    if (options.isStyleDocument())
      suffix=options.getServiceEndPoint()!=null && options.getServiceEndPoint().endsWith("/")      
      ? classToConvert.getName() : "/"+classToConvert.getName();

    if (!options.isStyleDocument())
        soapAddress.setAddress(options.getServiceEndPoint()!=null?
        options.getServiceEndPoint():Constants.SERVICE_DEFAULT_ENDPOINT);
    else
        soapAddress.setAddress(options.getServiceEndPoint()!=null?
        options.getServiceEndPoint()+suffix:Constants.SERVICE_DEFAULT_ENDPOINT+suffix);
    return soapAddress;
  }
  
  /**
   * Gets the unqualified class name
   * This is needed so that a service object can make use of
   * the className, but com.wingfoot.className.
   * @param className the className to transform
   * @return String the unqualified className
   */
  private String getUnQualifiedClassName(String className){
    if(className.lastIndexOf(".") == -1)
      return className;
    else
      return className.substring(className.lastIndexOf(".") + 1, className.length());
  }
}

package com.wingfoot;

import java.util.*;
/**
 * Class to store common constants.
 */

public class Constants
{

    
  /**
   * The only constructor available.  This class
   * can never be instantiated.
   */
  private Constants(){}

  /**
   * Constant that represents the namespace used in WSDL for SOAP specific
   * bindings.
   * <p>
   * http://schemas.xmlsoap.org/wsdl/soap/
   */
  public final static String WSDL_SOAP_NAMESPACE="http://schemas.xmlsoap.org/wsdl/soap/";

  /**
   * The default targetnamespace while generating a WSDL if none is provided.
   * The value is http://www.wingfoot.com/namespace.
   */
  public final static String DEFAULT_TARGETNAMESPACE="http://www.wingfoot.com/namespace";

  /**
   * The URI identifying HTTP in soap binding.
   * <p>
   * http://schemas.xmlsoap.org/soap/http
   */
  public final static String SOAP_HTTP="http://schemas.xmlsoap.org/soap/http";

  /**
   * The default endpoint used in &lt;soap:address&gt element in a WSDL
   * if none is provided.
   */
  public static final String SERVICE_DEFAULT_ENDPOINT="http://localhost";
     
  /**
   * Constant representing the namespace for the
   * elements of a SOAP message.
   * http://schemas.xmlsoap.org/soap/envelope/
   */
  /**
   * Constants representing the WSDL namespace.
   * <p>
   * http://schemas.xmlsoap.org/wsdl/
   */
  public final static String WSDL_NAMESPACE=
    "http://schemas.xmlsoap.org/wsdl/";
            
  public final static String SOAP_NAMESPACE = 
    "http://schemas.xmlsoap.org/soap/envelope/";
  /**
   * Constant representing the default schema instance
   * Used if no specific schema instance is provided
   * in the Envelope
   * http://www.w3.org/2001/XMLSchema-instance
   */
  public final static String SOAP_SCHEMA_INSTANCE =
    "http://www.w3.org/2001/XMLSchema-instance";
  /**
   * Constant representing the default schema.  Used
   * if no specific schema is provided in the Envelope.
   * http://www.w3.org/2001/XMLSchema
   */
  public final static String SOAP_SCHEMA =
    "http://www.w3.org/2001/XMLSchema";
  /**
   * Constant representing the SOAP Section 5
   * encoding style.
   * http://schemas.xmlsoap.org/soap/encoding/
   */
  public final static String SOAP_ENCODING_STYLE =
    "http://schemas.xmlsoap.org/soap/encoding/";
  /**
   * The namespace used, for serializing and deserializing
   * java.lang.Vector and java.lang.Hashtable.
   * http://xml.apache.org/xml-soap
   */
  public final static String DEFAULT_NAMESPACE=
    "http://xml.apache.org/xml-soap";

  /**
   * Constant representing the 1999 schema instance
   * Users are encouraged to use this constant if
   * there is a need to use 1999 schema instance
   * instead of the default 2001 schema instance.
       tp://www.w3.org/1999/XMLSchema-instance
   */
  public final static String SOAP_SCHEMA_INSTANCE_1999 =
    "http://www.w3.org/1999/XMLSchema-instance";
  /**
   * Constant representing the 1999 schema. Users are
   * encouraged to use this constant if there is a need
   * to use 1999 schema instead of the default 2001
   * schema.
   * http://www.w3.org/1999/XMLSchema
   */
  public final static String SOAP_SCHEMA_1999 =
    "http://www.w3.org/1999/XMLSchema";
  /**
   * Constant that identifies the verion of
   * Wingfoot SOAP Server.
   */
  public final static String VERSION=" 1.1";

  /**
   * Identifies the build name for the this product.
   */
  public final static String BUILD="$Name:  $";

  /**
   * Error to indicate that an attribute name in the schema
   * does not have a name. 
   * <p>
   * An attribute name is mandatory.
   */
  public final static String ERROR_SCHEMA_001="Mandatory name property is null";

  /**
   * Error indicates that an attribute in the schema has a complex type
   * as the base type of the attribute.  This is invalid.
   * <p>
   * An attribute must have an XSDType or a SimpleType as its base type.
   */
  public final static String ERROR_SCHEMA_002="An attribute can only have a simple type";

  /**
   * Error indicates that the value of a ref attribute in a 
   * attribute declaration was null.
   * <p>
   * A ref attribute can NEVER be null.
   */
  public final static String ERROR_SCHEMA_003="ref attribute in an Attrbiute declaration cannot be null";

  /**
   * Error indicates that the use attribute in a attribute declaration
   * has an incorrect value.  The value should be one of Attribute.REQUIRED,
   * Attribute.OPTIONAL or Attribute.PROHIBITED.
   */
  public final static String ERROR_SCHEMA_004="Invalid value for use attribute in Attribute declaration";

  /**
   * Error indicates that an attribute is declared as either prohibited or required
   * and the default is specified.  This is not possible.
   * <p>
   * Only an optional attribute can have a default.
   */
  public final static String ERROR_SCHEMA_005="A default attribute can occur only if the use is optional";

  /**
   * Error exists that an attribute declaration has both the fixed
   * attribute and the default attribute.  The two of them cannot 
   * coexist.
   */
  public final static String ERROR_SCHEMA_006="Fixed and default for attribute cannot coexist";

  /**
   * Error indicates that an attributegroup declaration in the schema
   * does not have a name.  Every attributegroup must have a name.
   */
  public final static String ERROR_SCHEMA_007="AttributeGroup must have a name";

  /**
   * Error indicates that an attributegroup declaration in the schema has a
   * ref attribute that does not have a name.  This is invalid.  Every ref attribute
   * must have a name
   * <p>
   * This error is followed by the name of the attributegroup.
   */
  public final static String ERROR_SCHEMA_008="ref attribute in AttributeGroup cannot be null";

  /**
   * Error indicates that a attributegroup was detected in the schema that had
   * something other that an attribute or attributeGroup in its content model.  
   * This is invalid.
   * <p>
   * This error is followed by the name of the attributegroup.
   */
  public final static String ERROR_SCHEMA_009="attributegroup can have only attribute or attributeGroup  in its content model";

  /**
   * The restriction for a complexcontent is either Content.RESTRICTION or
   * Content.EXTENSION.
   */
  public final static String ERROR_SCHEMA_010="Invalid restriction";

  /**
   * The content model for a complexcontent is one of Attribute AttributeGroupDefinition,
   * ModelGroup or ModelGroupDefiniton.  The error indicates that the schema reader
   * detected a Component other the the four listed above as a content model for a
   * complexcontent.
   */
  public final static String ERROR_SCHEMA_011="Invalid content model for complexcontent";

  /**
   * Error indicates that an attempt was made to create a complex type 
   * with a null Content.  A complex type must have a content model.
   */
  public final static String ERROR_SCHEMA_012="The content model for complex type cannot be null";

  /**
   * Error indicates that an attempt was made to create an element with null QName.
   * The most probable reason is that the name attribute in the &ltelement&gt is 
   * not specified in the XML schema.
   * An element cannot be anonymous; it must have a name.  
   */
  public final static String ERROR_SCHEMA_013="The element QName cannot be null";

  /**
   * Error indicates that the ref attribute in an element declaration is missing.  
   * This is invalid.  A ref must have a value.
   * <p>
   * This error is followed by the name of the element.
   */

  public final static String ERROR_SCHEMA_014="ref attribute in an element declaration cannot be null";

  /**
   * Error indicates that a min attribute for a component has a value
   * other that 0 or 1.  This is illegal. 
   * <p>
   * The error is followed by the name of the element.
   */
      
  public final static String ERROR_SCHEMA_015="Legal values for min is 0 or 1";

  /**
   * Error indicates that a component was detected where the minoccurs was greater
   * than maxoccurs attribute.  This is not possible.
   * <p>
   * The error is also triggered if the maxValue is less that 0.
   * <p>
   * The error is followed by the name of the element.
   */
  public final static String ERROR_SCHEMA_016="Illegal value for maxOccurs attribute.";

  /**
   * Error indicates that an attempt was made to create a ModelGroupDefinition with
   * a null parameter for QName.  This is illegal.
   */
  public final static String ERROR_SCHEMA_017="ModelGroupDefinition must have a name";

  /**
   * Error indicates that a ref element in a model group definition did not have a value.
   * All ref elements must have a value.
   * <p>
   * This error is usually followed by the name of the model group definition.
   */
  public final static String ERROR_SCHEMA_018="ref element in model group definition must have value.";

  /**
   * Error indicates that an attempt was made to set an illegal Component
   * as a content model for a ModelGroupDefinition.  The content model
   * for a ModelGroupDefinition can only be instances of a ModelGroup.
   * <p>
   * This error is followed by the name of the modelGropuDefinition.
   */
  public final static String ERROR_SCHEMA_019="Invalid content model for model group definition.";

  /**
   * Error indicates that the user declared a custom schema reader that
   * does not implement the SchemaReader interface.  This is incorrect.
   * To successfully implement a custom schema reader, it must implement
   * the SchemaReader interface.
   */
  public final static String ERROR_SCHEMA_020="Custom schema readers must implement SchemaReader";

  /**
   * A simplecontent must be followed by either a restriction or an extension.
   * The error indicates that the schema reader detected neither of them.
   */
  public final static String ERROR_SCHEMA_021="Invalid derivation for simpleContent";

  /**
   * The error indicates that a facets were detected with a simpleContent followed by
   * an extension derivaation.  When simpleContent is used, the facets are allowed only
   * if the derivation is a restriction.
   */
  public final static String ERROR_SCHEMA_022="Invalid use of facets. Cannot use them withension and simplecontent";

  /**
   * Error indicates that an attempt was made to create a SimpleType with a base type
   * other than SimpleType or XSDType.  This is invalid.
   * <p>
   * This error is followed by the name of the simple type.
   */
  public final static String ERROR_SCHEMA_023="SimpleType can have another SimpleType or XSDType as its base type";

  /**
   * Error indicates that the schema reader did not detect the first element as
   * &ltschema&gt.  This is invalid.  A XMLschema has to be enclosed within
   * the &ltschema&gt element.
   */
  public final static String ERROR_SCHEMA_024="Invalid schema definition. The first element has to be a &ltschema&gt";


  /**
   * Error indicates the an element was not recognized while reading the schema.
   * <p>
   * The error is followed by the name of the offending element.
   */
  public final static String ERROR_SCHEMA_025="SchemaReader does not currently support the following element: ";

  /**
   * Error indicates that a type declaration (simpleType or complexType) had multiple
   * type declaration.  This is invalid.  It can have only one type declaration that
   * is declared using either a base attribute or an inline declaration.
   */
  public final static String ERROR_SCHEMA_026="Type cannot have multiple base type definitions";

  /**
   * Error indicates that an illegal content was detected for a simpleContent 
   * content model.  The legal Component in a simpleContent is either a
   * SimpleType, Attribute or AttributeGroupDefinition.
   */
  public final static String ERROR_SCHEMA_027="Illegal content for a simpleContent content model";

  /**
   * A ComplexType might have a &ltsimpleContent&gt or &ltcomplexContent&gt.
   * Either content extends or restricts some Type.  The type being extended
   * is specified as a value of the base attribute in the &ltrestriction&gt
   * or &ltextension&gt element.
   * <p>
   * This error indicates that an extension or restriction was specified 
   * without the base attribute.
   */
  public final static String ERROR_SCHEMA_028="&ltrestriction&gt OR &ltextension&gt must have a mandatory base attribute";

  /**
   * A ComplexType that has a simpleContent with a restriction derivation can
   * define a simpleType.  This error indicates that possible an extension
   * derivation tried to define a new simple type.
   * <p>
   * In the schema, look for a &ltsimpleContent&gt followed by &ltrestriction&gt
   * followed by a &ltsimpleType&gt tag.  This is an error.
   */
  public final static String ERROR_SCHEMA_029="In a simpleContent only a restriction can have a &ltsimpleType&gt tag";

  /**
   * An &ltattributeGroup&gt definition (one that has a name attribute) can
   * only appear as a direct descendent of the &ltschema&gt element. This error
   * indicates that a schema reader detected otherwise.
   * <p>
   * The error is followed by the name of the offending &ltattributeGroup&gt
   */
  public final static String ERROR_SCHEMA_030="An attributeGroup can only be defined as a daughter of &ltschema&gt";

  /**
   * An &ltattributeGroup&gt can have either name or ref attribute but not both.
   * The error indicates that the schema reader detected both.
   * <p>
   * The error is followed by the name of the offending element.
   */
  public final static String ERROR_SCHEMA_031="An attributeGroup can have either the name or ref attribute but NOT BOTH";

  /**
   * An &ltattributeGroup&gt that is a reference to another &ltattributeGroup&gt definition
   * can only occurs as part of complex type definition or attribute group definition.
   * The error indicates that the schema reader detected otherwise.
   * <p>
   * The error is followed by the offending attribute group.
   */
  public final static String ERROR_SCHEMA_032="An attributeGroup with a ref attribute can occur as a part of complex type definition or attribute group definition";

  /**
   * Error indicates that an &ltall&gt Element Information Item has an 
   * &ltall&gt or &ltsequence&gt or &ltchoice&gt element.  This is invalid.
   * An all should only have an annotation or an element as its children.
   * <p>
   * The error is also generated if a nested model group was detected with
   * an &ltall&gt as a child of either &ltall&gt, &ltsequence&gt or
   * &ltchoice&gt.
   */
  public final static String ERROR_SCHEMA_033="A modelGroup with &ltall&gt can have only an element as its children";

  /**
   * The error is triggered when a content is added to a AttributeGroupDefinition and
   * that AttributeGroupDefinition is a reference to another AttributeGroupDefinition.
   * This is illegal.
   * <p>
   * The error is followed by the name of the AttributeGroupDefinition.
   */
  public final static String ERROR_SCHEMA_034="An AttributeGroupDefinition that is a reference cannot have a content model";

  /**
   * Error indicates that a &ltcomplexType&gt was detected that has a Component
   * other than Content, Attribute or AttributeGroup in its body.  This is invalid.
   * <p>
   * The error is followed by the name of the ComplexType.
   */
  public final static String ERROR_SCHEMA_035="A complex type can have either a content, an attributeGroup or an attribute in its body";

  /**
   * Error indicates that a call was made to SchemaHolder.toXML method but the destination
   * was not set.  This method tries to convert the SchemaHolder to a XML Schema.
   * <p>
   * To avoid the error, use the setDestination method to specify the destination
   * before calling the toXML method.
   */
  public final static String ERROR_SCHEMA_036="Cannot write a XML schema without specifying the destination";

  /**
   * Error indicates that an attempt was made to set the content of a ModelGroupImplementation
   * with something other than a Element or a ModelGroup.
   */
  public final static String ERROR_SCHEMA_037="A ModelGroup may have only an Element or a ModelGroup as its content";

  /**
   * Error indicates that an attempt was made to convert a ComplexType to a JavaBean
   * but the complexType was anonymous.  This is illegal.
   */
  public final static String ERROR_SCHEMA_038 = "Illegal attempt made to create a JavaBean for a anonymous ComplexType";

  public final static String ERROR_WSDL_001="A &ltmessage&gt must have a name attribute";

  /**
   * Error indicates that an attempt to create Part failed because the partType parameter
   * had an invalid value.  It should be either Part.ELEMENT or Part.TYPE
   */
  public final static String ERROR_WSDL_002="Part type is either Part.ELEMENT OR Part.TYPE";

  /**
   * Error indicates that a &ltpart&gt element did not have a mandatory name attribute.
   */
  public final static String ERROR_WSDL_003="Missing mandatory name attribute in &ltpart&gt element";

  /**
   * A &ltmessage&gt has multiple &ltpart&gt.  Each &ltpart&gt has a mandatory name attribute.
   * The part name must be unique among all the parts of the enclosing message.  This error
   * indicates that duplicate part names was detected.
   * <p>
   * The error is followed by the name of the message and part.
   */
  public final static String ERROR_WSDL_004="A message cannot have multiple &ltpart&gt with same name";

  /**
   * Error indicates that an attempt was made to create a PortType without a name.
   * This is illegal.  A PortType must have a name attribute.
   */
  public final static String ERROR_WSDL_005="A PortType must have a name attribute";

  /**
   * Error indicates that an attempt was made to create an Operation with
   * a null name.  This is illegal.
   */
  public final static String ERROR_WSDL_006="An Operation name cannot be null";

  /**
   * Error indicates that two operations were detected under a portType with
   * identical names.  This is illegal.
   * <p>
   * This error is followed by the name of the portType and operation.
   */
  public final static String ERROR_WSDL_007="An &ltoperation&gt cannot be duplicated in a &ltportType&gt";

  /**
   * The style in SOAPBinding was set to something other than
   * SOAPBinding.DOCUMENT or SOAPBinding.RPC.
   */
  public final static String ERROR_WSDL_008="Invalid style in SOAPBinding.";

  /**
   * The use attribute in SOAPBody was set to something other than
   * SOAPBody.LITERAL or SOAPBody.ENCODED.
   */
  public final static String ERROR_WSDL_009="Invalid use in SOAPBody";

  /**
   * Error indicates that an attempt was made to create SOAPMessage with
   * an invalid messageType.
   * <p>
   * The legal values for messageType are SOAPMessage.BODY, SOAPMessage.HEADER or
   * SOAPMessage.FAULT.
   */
  public final static String ERROR_WSDL_010="SOAPMessage is invalid.";

  /**
   * Error indicates that the user declared a custom schema reader that does 
   * not implement the SchemaReader interface. This is incorrect. To successfully 
   * implement a custom schema reader, it must implement the SchemaReader interface
   */
  public final static String ERROR_WSDL_011="A custom WSDL reader must implement WSDLReader interface";

  /**
   * Error indicates that in the WSDL a &ltmessage&gt is missing the name attribute.
   * This is invalid as the name attribute in &ltelement&gt is mandatory
   */
  public final static String ERROR_WSDL_012="A &ltmessage&gt element is missing a mandatory name attribute";

  /**
   * Error indicates that in the WSDL a &ltpart&gt is missing the name attribute.
   * This is invalid as the name attribute in &ltpart&gt is mandatory
   */
  public final static String ERROR_WSDL_013="A &ltpart&gt element is missing a mandatory name attribute";

  /**
   * A &ltpart&gt element in a WSDL must have either a type attribute or 
   * element attribute.  This error indicates that a &ltpart&gt element was
   * detected without either attribute.
   * <p>
   * This error is followed by the name of the part.
   */
  public final static String ERROR_WSDL_014="A &ltpart&gt element is missing a mandatory type or element attribute";

  /**
   * Error indicates that a &ltporType&gt element in the WSDL is mising a name attribute.
   * This is illegal since the name attribute in a &ltportType&gt is mandatory.
   */
  public final static String ERROR_WSDL_015="A &ltportType&gt element is missing a mandatory name attribute";

  /**
   * Error indicates that a &ltoperation&gt element inside a &ltportType&gt element
   * is missing a mandatory name attribute.  This is illegal since an operation
   * must have a name attribute.
   */
  public final static String ERROR_WSDL_016="A &ltoperation&gt element is missing a mandatory name attribute";

  /**
   * Error indicates that a &ltinput&gt or &ltoutput&gt or &ltfault&gt element inside
   * an portType and operation element is missing a mandatory message attribute
   */
  public final static String ERROR_WSDL_017="A &ltinput&gt element is missing a mandatory message attribute";

  /**
   * Error indicates that a message attribute in a &ltinput&gt or &ltoutput&gt or 
   * &ltfault&gt element is refering to a message that is not previously defined.
   */
  public final static String ERROR_WSDL_018="A &ltinput&gt element is refering to an undefined message";

  /**
   * Error indicates that a &ltservice&gt element was detected in WSDL that did not have
   * a name attribute.  This is illegal.
   * <p>
   * Each &ltservice&gt element must have a name attribute.
   */
  public final static String ERROR_WSDL_019="A &ltservice&gt element is missing a mandatory name attribute";

  /**
   * Error indicates that a &ltport&gt element was detected in WSDL that did not have
   * a name attribute.  This is illegal.
   * <p>
   * Each &ltport&gt element must have a name attribute.
   */
  public final static String ERROR_WSDL_020="A &ltport&gt element is missing a mandatory name attribute";

  /**
   * Error indicates that a &ltport&gt element was detected in WSDL that did not have
   * a binding attribute.  This is illegal.
   * <p>
   * Each &ltport&gt element must have a binding attribute.
   */
  public final static String ERROR_WSDL_021="A &ltport&gt element is missing a mandatory binding attribute";

  /**
   * Error indicates that a &ltbinding&gt element is missing a name or a type attribute.
   * This is illegal.
   * <p>
   * A name attribute is mandatory.  The type (QName) must point to a portType.
   */
  public final static String ERROR_WSDL_022="A &ltbinding&gt element is missing a mandatory name attribute";

  /**
   * Error indicates that the &ltbinding&gt element has a portType attribute that
   * is referring to a non existant portType.
   * <p>
   * This error is followed by the name of the binding element.
   */
  public final static String ERROR_WSDL_023="A &ltbinding&gt element refering to an invalid portType";

  /**
   * Error indicates that an &ltoperation&gt inside a &ltbinding&gt is 
   * refering to a non-existing operation for the portType.
   * <p>
   * The error is followed by the name of the portType and the operation.
   */
  public final static String ERROR_WSDL_024="A &ltoperation&gt element is refering to an invalid operation";

  /**
   * Error indicates that an &ltimport&gt element was detected in WSDL that did
   * not have a mandatory name or namespace attribute.
   */
  public final static String ERROR_WSDL_025="A &ltimport&gt in WSDL does not have a mandatory name and/or namespace attribute";

  /**
   * Error indicates that the value of the namespace element in an &ltimport&gt element
   * does not match with the targetNamespace attribute in the WSDL specified by the
   * location attribute.  This is illegal.
   * <p>
   * This error is followed by the name of the import element.
   */
  public final static String ERROR_WSDL_026="The namespace in an &ltimport&gt element does not match with the targetNamespace in the imported WSDL";

  /**
   * Error indicates that an attempt was made to convert a WSDLHolder to
   * WSDL, but the destination of the WSDL was not specified. 
   * <p>
   * To fix the error, use the setDestination method in WSDLHolder.
   */
  public final static String ERROR_WSDL_027="Cannot write a WSDL with null destination";

  /**
   * Error indicates that a data type was encountered that is not supported
   * by java2wsdl and hence cannot be represented in WSDL.
   */
  public final static String ERROR_WSDL_028="Unsupported data type.  If the problem is in a bean, please make sure that the bean implements java.io.Serializable";

  /**
   * Error indicates that an attempt was made to create a ObjectHolder
   * with a null class name.  This is invalid.
   * <p>
   * Unless the user is explicitly instantiating this class, this exception
   * is never thrown.  If it is thrown, it indicates a fatal error in 
   * converting a WSDL to Java representation.
   */
  public final static String ERROR_WSDL_029="A class name is required.";

  /**
   * Error indicates that the ObjectHolder.setProperty method was called
   * but the class was not decalred as a bean.
   */
  public final static String ERROR_WSDL_030="Attempt to set a property on a class that is not a bean";

  /**
   * This is a fatal error.  Error indicates that an attempt was made to create
   * a Java representation of a schema but the OutputStream was null.
   */
  public final static String ERROR_JAVAHOLDER_001="Cannot write to a null JavaHolder";

  /**
   * Error indicates that an attribute with a ref attribute does not have
   * a corresponding referenced attribute
   */
  public final static String ERROR_JAVAHOLDER_002="Cannot properly reference to an Attribute with a ref attribute";

  /**
   * Error indicates that an element with a ref attribute does not have
   * a corresponding referenced element
   */
  public final static String ERROR_JAVAHOLDER_003="Cannot properly reference to an Element with a ref attribute";

  /**
   * Error indicates that an element with a ref attribute does not have
   * a corresponding referenced ModelGroupDefinition
   */
  public final static String ERROR_JAVAHOLDER_004="Cannot properly reference to an ModelGroupDefinition with a ref attribute";

  /**
   * Error indicates that an attempt was made to bind a remote object to a WSDL,
   * but the WSDL was not found in the cache and could not be accessed.
   * <p>
   * This is a fatal error. Perhaps the web server that hosts the WSDL is not 
   * accessible.  Please try the operation again later.
   */
  public final static String ERROR_REGISTRY_001="Cannot access the WSDL";

  /**
   * Error indicates that the WSDL was not generated
   */
  public final static String ERROR_REGISTRY_002="No WSDLHolder was created";

  /**
   * Error indicates SerializedHolder was not obtained
   */
  public final static String ERROR_REGISTRY_003="SerializedHolder cannot be created";

  /**
   * This error is generated during runtime.  A method was called on the proxy 
   * object (proxy of the portType Interface) but while generating the payload,
   * the mapping of the class to the &lt;portType&gt; in WSDL was not found.
   * <p>
   * This is a fatal error.  It could be caused when the WSDL has changed since
   * the stubs were generated.  Try regenerating the stubs and then executing
   * the method.  If the problem persists, please report the problem.
   */
  public final static String ERROR_CLIENTPROXY_001="PortType class (Interface) not mapped to a QName";

  /**
   * Error indicates that a given PortType is not in the WSDL.  This error is
   * generated when the client proxy is trying to generate a SOAP payload.
   * <p>
   * This is a fatal error.  It could be caused when the WSDL has changed since
   * the stubs were generated.  Try regenerating the stubs and then executing
   * the method.  If the problem persists, please report the problem.
   */
  public final static String ERROR_CLIENTPROXY_002="Cannot find the PortType in WSDL";

  /**
   * Error indicates that a given operation is not found in the WSDL. This 
   * error is generated by the client proxy while trying to generate the payload.
   * <p>
   * This is a fatal error.  It could be caused when the WSDL has changed since
   * the stubs were generated.  Try regenerating the stubs and then executing
   * the method.  If the problem persists, please report the problem.
   */
  public final static String ERROR_CLIENTPROXY_003="Cannot find operation in portType";

  /**
   * Error indicates that a given method name cannot be resolved to a 
   * operation in WSDL.  This error is generated by the client proxy while trying
   * to generate the payload.
   * <p>
   * This is a fatal error.  It could be caused when the WSDL has changed since
   * the stubs were generated.  Try regenerating the stubs and then executing
   * the method.  If the problem persists, please report the problem.
   */
  public final static String ERROR_CLIENTPROXY_004="Cannot determine an operation for the method";

  /**
   * Error indicates that in a WSDL a &lt;wsdl:binding&gt; could not be determined
   * for a given PortType.  This error is followed by the QName of the portType.
   * This error indicates an error in the WSDL.
   */
  public final static String ERROR_CLIENTPROXY_005="Cannot determine a Binding for a PortType.";

  /**
   * Error indicates that in a WSDL a &lt;wsdl:port&gt; could not be determined
   * for a given Binding.  This error is followed by the QName of the Binding.
   * This error indicates an error in the WSDL.
   */
  public final static String ERROR_CLIENTPROXY_006="Cannot determine Port for a Binding";

  /**
   * Error indicates that a style (RPC|Document) could not be determined
   * for a given Binding, Operation combination.  This error indicates an
   * error in the WSDL.
   * <p>
   * This error is followed by the name of the Binding and Operation.
   */
  public final static String ERROR_CLIENTPROXY_007="Cannot determine style for a Binding";

  /**
   * Error indicates that an endpoint could not be determined.  This could
   * be because the &lt;soapAddress&gt; is absent from the WSDL or the
   * transport is not HTTP.
   */
  public final static String ERROR_CLIENTPROXY_008="Cannot determine endpoint";

  /**
   * Error indicates that the alternate transport being used does not implement
   * com.wingfoot.soap.transport.Transport interface
   */
  public final static String ERROR_CLIENTPROXY_009="Specified transport does not implement com.wingfoot.soap.transport.Transport";

  /**
   * Error indicates an invalid class passed.
   */
  public final static String ERROR_WSDL_2_JAVA_001="Invalid class passed";

  /**
   * This is a fatal error generated in AbstractSerializer.  Error indicates that
   * an attempt was made to serialize XSDType but the type encapsulated is not
   * a inbuilt schema type.
   * <p>
   * If encountered, please report this problem.
   */
  public final static String ERROR_SOAPSERIALIZER_001="Cannot determine an inbuilt schema type";

  /**
   * This is a fatal error generated in AbstractSerializer.
   * <p>
   * If encountered, please report this problem.
   */
  public final static String ERROR_SOAPSERIALIZER_002 = "Cannot deserialize a null ComplexType";

  /**
   * Error indicates that in the serializeBody method the PortType or Operation passed
   * in as parameters is null.  This is illegal.  Both these parameters should never
   * be null.
   */
  public final static String ERROR_SOAPSERIALIZER_003="Cannot serialize with a null PortType or Operation";

  /**
   * Error indicates that an attempt was made to instantiate LiteralSerializer but
   * the WSDLHolder or the XMLWriter was null.  This is illegal.
   */
  public final static String ERROR_SOAPSERIALIZER_004="Cannot construct a LiteralSerializer";

  /**
   * This error occurs while serializing a set of parameters.  Error indicates
   * that in the serializeBody method, the Operation passed in had the number
   * of message parts no equal to the number of parameters passed in.
   * <p>
   * Common reason for this error is that the WSDL contains two operations
   * with the same name (overloaded methods) and an incorrect Operation
   * was passed in.
   */
  public final static String ERROR_SOAPSERIALIZER_005="Incorrect Operation passed in to serialize";

  /**
   * Error indicates that in the WSDL an element was detected with
   * maxoccurs attribute set to a value greater than 1 or unbounded
   * but the corresponding parameter value is not an array.
   * <p>
   * This error is followed by the name of the element.
   */
  public final static String ERROR_SOAPSERIALIZER_006="An element with maxoccurs>1 does not have an array";

  /**
   * The class ComplexTypeSerializer serializes ComplexType.  Error indicates that
   * ComplexTypeSerializer encountered a Type other than ComplexType.
   * <p>
   * This is a fatal error.  Please report this error.
   */
  public final static String ERROR_SOAPSERIALIZER_007="Cannot process a Type other than ComplexType in ComplexTypeSeriazlier";

  /**
   * Error indicates that a bean is missing a property for a mandatory attribute.
   * This is an error in a bean.  Check the bean and make sure that all mandatory
   * attributes have getXXX and setXXX methods.
   */
  public final static String ERROR_SOAPSERIALIZER_008="Missing property for a mandatory attribute";

  /**
   * Error indicates that an attempt was made to construct a LiteralDeserializer
   * with either a null LiteralEnvelope or XmlParser or WSDLHolder.
   */
  public final static String ERROR_SOAPSERIALIZER_009="Cannot deserialize a payload with null parameters)";

  /**
   * Error is thrown in SectionVSerializer.  According to WSDL 1.1, a payload where the
   * use is encoded may only have a message part with the type attribute.  This error
   * indicates that in the WSDL, a message was detected where the part contained an
   * element attribute to identify the type of a part.
   * <p>
   * Change the WSDL to remove the element and replace with part.
   */
  public final static String ERROR_SOAPSERIALIZER_010="Detected a message part with element when expecting a type";

  /**
   * Error indicates that a parameter was detected whose WSDL definition 
   * does not have a data type.
   * <p>
   * Check to WSDL to determine if all the parameters has a data type defined.
   */
  public final static String ERROR_SOAPSERIALIZER_011="Cannot determine the data type for a parameter with null";

  /**
   * Indicates that an unsupported data type was detected in the WSDL or in the 
   * Envelope parameter.  The error is followd by the name of the data type.
   */
  public final static String ERROR_SOAPSERIALIZER_012="Unsupported data type. Cannot serialize:";
  /**
   * Error generated by ComplexTypeDeserializer.unmarshall method.  The Component
   * passed in as a parameter has to be a ComplexType.  Error indicates that
   * some other component was passed in.
   */
  public final static String ERROR_SOAPDESERIALIZER_001="Expecting ComplexType but found another component";

  /**
   * This is a fatal error during deserialization.  Please report this problem.
   */
  public final static String ERROR_SOAPDESERIALIZER_002="Cannot deserialize non XSDType or SimpleType";
} /* Constants */


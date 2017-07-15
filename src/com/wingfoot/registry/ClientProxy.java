package com.wingfoot.registry;
import com.wingfoot.*;
import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.soap.encoding.*;
import com.wingfoot.xml.schema.types.*;
import com.wingfoot.xml.schema.gen.*;
import com.wingfoot.xml.schema.*;
import com.wingfoot.wsdl.soap.*;
import com.wingfoot.soap.*;
import com.wingfoot.soap.transport.*;
import com.wingfoot.soap.SOAPException;
import java.util.*;
import java.lang.reflect.*;


/**
 * Class that acts as a proxy to the SOAP client
 * calls.  Methods in this class are able to 
 * determine the method called by the client
 * on the Remote interface.  Based on the method
 * called, the SOAP message is constructed using 
 * the WSDLHolder and the return data is sent back
 * to the remote object.
 * <p>
 * The class is of default access because no class
 * other than the class in the com.wingfoot.registry
 * is able to access this class.
 */
public class ClientProxy implements java.lang.reflect.InvocationHandler
{
  private String wsdlURL;
  private WSDLHolder wsdlHolder;
  private TypeMappingRegistry tmr;
  private Class interfaceClass;
  private SchemaConverterInstance sci;
  private Transport transport = null;
  private boolean useSession = false;
  private boolean displayPayload = false;
  private final int INPUTMESSAGE=1;
  private final int OUTPUTMESSAGE=2;
  /**
   * Creates a ClientProxy.  
   * @param wsdlURL the URL of the WSDL that the
   * proxy works on.
   * @param wsdlHolder the wsdlURL encapsulated as
   * a WSDLHolder.
   * @param interfaceClass the Class (an Interface) for
   * which this Proxy is generated.
   */
  public ClientProxy(ProxyMap map, WSDLHolder wsdlHolder)
  throws Exception
  {
    this.wsdlURL=map.getWSDLLocation();
    this.wsdlHolder=wsdlHolder;
    this.tmr=map.getTypeMappingRegistry();
    this.interfaceClass=map.getInterfaceClass();
    this.useSession = map.getUseSession();
    this.displayPayload = map.isDisplayPayload();

    /**
     * set the transport.  If there is a transport class in ProxyMap,
     * use that one, if not, load the J2SEHTTPTransport 
     */
    Object transportObject = map.getTransport().newInstance();
    
    if(transportObject instanceof com.wingfoot.soap.transport.J2SEHTTPTransport)
    {
      J2SEHTTPTransport jTransport = (J2SEHTTPTransport) transportObject;
      if(useSession)
        jTransport.useSession(true, null);
      //set the Transport object
      transport = jTransport;
    }//if instance of com.wingfoot.soap.transport.J2SEHTTPTransport
    else 
    {
      if(implementsTransport(map.getTransport()))
        transport = (Transport) transportObject; 
      else
        throw new ClientProxyException("ERROR_CLIENTPROXY_009:"+Constants.ERROR_CLIENTPROXY_009);
    }
  
    //Set the debug
    transport.setDisplayPayload(this.displayPayload);
    /**
     * Create the Options.  We are only interested
     * in namespace to Package mapping.
     */
     Options options=new Options();
     if (tmr.getPackage()!=null) 
     {
       Hashtable ht=tmr.getPackage();
       Enumeration enum=ht.keys();
       while (enum.hasMoreElements()) 
       {
         String aNS=(String)enum.nextElement();
         String aPackage=(String)ht.get(aNS);
         options.setPackageMapping(aNS,aPackage);
       }//while
     }//if
     sci=new SchemaConverterInstance(wsdlHolder.getAllType(),options);
  }//constructor

  /**
   * Processes a method invocation on a proxy instance and 
   * returns the result. This method is invoked on an 
   * invocation handler when a method is invoked on a proxy 
   * instance that it is associated with.
   * @param proxy the proxy instance that the method was invoked on
   * @param method the Method instance corresponding to the interface 
   * method invoked on the proxy instance. The declaring class of the 
   * Method object is the interface that the method was declared 
   * in, which may be a superinterface of the proxy interface that the 
   * proxy class inherits the method through.
   * @param args an array of objects containing the values of the 
   * arguments passed in the method invocation on the proxy instance, 
   * or null if interface method takes no arguments. Arguments of 
   * primitive types are wrapped in instances of the appropriate 
   * primitive wrapper class, such as java.lang.Integer or 
   * java.lang.Boolean
   * @return Object the value to return from the method invocation 
   * on the proxy instance. If the declared return type of the 
   * interface method is a primitive type, then the value returned 
   * by this method must be an instance of the corresponding primitive 
   * wrapper class; otherwise, it must be a type assignable to the 
   * declared return type. If the value returned by this method is null 
   * and the interface method's return type is primitive, then a 
   * NullPointerException is thrown by the method invocation on the 
   * proxy instance. If the value returned by this method is otherwise 
   * not compatible with the interface method's declared return type 
   * as described above, a ClassCastException is thrown by the 
   * method invocation on the proxy instance.
   */
  public Object invoke(Object proxy, Method method, Object[] args) 
  throws  Throwable 
  {
    QName portQName=tmr.getPortType(this.interfaceClass);
    if (portQName==null)
      throw new ClientProxyException("ERROR_CLIENTPROXY_001:"+Constants.ERROR_CLIENTPROXY_001+
      ":"+this.interfaceClass.getName());
      
    PortType pt=wsdlHolder.getPortType(portQName);
    //Error if PortType is not found.
    if (pt==null)
      throw new ClientProxyException("ERROR_CLIENTPROXY_002:"+Constants.ERROR_CLIENTPROXY_002+
      ":"+this.interfaceClass.getName());

    //Get the correct operation from the WSDL.
    Operation operation=this.getOperationFromMethod(pt,method.getName(),method.getParameterTypes());

    //Get the Binding for the PortType.
    Binding binding=wsdlHolder.getBinding(pt);
    if (binding==null)
      throw new ClientProxyException("ERROR_CLIENTPROXY_005:"+Constants.ERROR_CLIENTPROXY_005+" "+pt.getName());

    //get the Port; it contains the endpoint
    Port port=wsdlHolder.getPort(binding);
    if (port==null)
       throw new ClientProxyException("ERROR_CLIENTPROXY_006:"+Constants.ERROR_CLIENTPROXY_006+" "+binding.getBindingName());

    //Create the request Envelope and set the parameters.
    LiteralEnvelope requestEnvelope=new LiteralEnvelope(this.wsdlHolder);
    requestEnvelope.setPortType(pt);
    requestEnvelope.setOperation(operation);
    requestEnvelope.setMessage(operation.getInputMessage());
    if (args!=null && args.length>=0)
    {
      for (int i=0; i<args.length; i++)
        requestEnvelope.setParameter(args[i]);
    }

    //Invoke the call.
    Call theCall = new Call(requestEnvelope);
    theCall.setMappingRegistry(tmr);
    transport.setEndpoint(this.getEndPoint(port,binding));
    transport.setSOAPAction(this.getSOAPAction(binding,operation));
    LiteralEnvelope responseEnvelope = theCall.invoke(transport);
    return this.extractResponseFromEnvelope(method,responseEnvelope);
  }//invoke

  /**
   * Takes the response envelope and extracts the return
   * parameter.
   * <p>
   * This method returns null if the return type is void.
   */
  private Object extractResponseFromEnvelope(Method method, LiteralEnvelope responseEnvelope)
  throws Exception
  {
    /**
     * If there is a Fault, throw an exception and return;
     */
     if (responseEnvelope.isFaultGenerated()) 
     {
        StringBuffer sb=new StringBuffer();
        if (responseEnvelope.getFault().getFaultString()!=null)
          sb.append(responseEnvelope.getFault().getFaultString()).append("\n");
        Vector v = responseEnvelope.getFault().getDetail();
        for (int i=0; v!=null && i<v.size(); i++)
          sb.append(v.elementAt(i).toString()).append("\n");
        throw new Exception(sb.toString());
     }
    Class returnType=method.getReturnType();
    if (returnType.getName().equals("void"))
      return null;
    else if (returnType.getName().equals("com.wingfoot.soap.Envelope"))
      return responseEnvelope;
    else if (responseEnvelope.getParameter(0)!=null &&
    responseEnvelope.getParameter(0).getClass().isArray() &&
    method.getReturnType().isArray() && method.getReturnType().getComponentType().isPrimitive())
    {
      /**
       * The client is expecting a primitive array but the SOAP engine is
       * returning a primitive wrapper array.  Do the conversion here.
       */
       Object primitiveArray=Array.newInstance(method.getReturnType().getComponentType(),
       Array.getLength(responseEnvelope.getParameter(0)));
       Object wrapperArray[] = (Object[])responseEnvelope.getParameter(0);
       for (int i=0; i<wrapperArray.length; i++)
        Array.set(primitiveArray,i,wrapperArray[i]);
       return primitiveArray;
    }
    else
      return responseEnvelope.getParameter(0);
  }//extractResponseFromEnvelope

  /**
   * Retrieve the endpoint.  Only retrieve the
   * endpoint if the transport is HTTP.
   */
  private String getEndPoint(Port port, Binding binding)
  throws ClientProxyException
  {
    if (binding.getBindingExtension() instanceof SOAPBinding) 
    {
      if (((SOAPBinding)binding.getBindingExtension()).getTransport().equals(Constants.SOAP_HTTP)) 
      {
        if (port.getEndPoint() instanceof SOAPAddress) 
          return ((SOAPAddress)port.getEndPoint()).getAddress();
      }
    }
    throw new ClientProxyException("ERROR_CLIENTPROXY_008:"+Constants.ERROR_CLIENTPROXY_008);
  }//getEndPoint
 
  /**
   * Checks the WSDL to determine if a PortType has an Operation
   * with a given name. An operation is a methodName.  
   * @param PortType to look the Operation in.
   * @param methodName the name of the Operation to looks for.
   * @param args the date type of the operation's message part.
   * @return Operation the name of the Operation.
   * @throws ClientProxyException if a proper method name cannot
   * be determined.
   */
  private Operation getOperationFromMethod(PortType pt,String methodName,Class[] args)
  throws ClientProxyException
  {
    int count=pt.getOperationCount(methodName);
    //Check to see if the operation is overloaded.
    if (count==0) 
    {
      //The operation does not exist in PortType.  This is an error.
      throw new ClientProxyException("ERROR_CLIENTPROXY_003:"+Constants.ERROR_CLIENTPROXY_003+
      ":"+pt.getName().getLocalPart()+"."+methodName);
    }
    else 
    {
      /**
       * There is one or more than one operation in WSDL with same name.
       * This could be overloaded overloaded methods.  The method is now 
       * selected based on the parameter types.
       */
       Operation oArray[]=pt.getOperationArray(methodName);
       for (int i=0; oArray!=null&&i<oArray.length; i++) 
       {
         if (this.isMethodParameterIdentical(oArray[i],args))
          return oArray[i];
       }//for
    }//else
    throw new ClientProxyException("ERROR_CLIENTPROXY_004:"+Constants.ERROR_CLIENTPROXY_004+
    ":"+methodName);
  }//getOperationFromMethod

  /**
   * Compares the input Part of an operation with an array of arguments.
   * Returns true if the type and order of the Part is identical to the
   * type and order of the args.
   * @param operation encapsulates the method name for which the parameters
   * has to be determined.
   * @param args an array of expected arguments.
   * @return boolean true if the method parameter type matches with the
   * expected parameter types; false otherwise.
   */
  private boolean isMethodParameterIdentical(Operation operation, Class[] args) 
  {
    //If no parameters expected return true.
    int messageCount=operation.getInputMessage().getMessagePartCount();
    if (messageCount==0 &&(args==null||args.length==0)) 
    {
      return true;
    }
    else if (messageCount>0 && args==null)
    {
      //WSDL expects parameters but the number of arguments is null. This is not the method.
      return false;
    }
    else if (messageCount==0 && args!=null && args.length>0)
    {
      //WSDL does not expect parameters but the remote Interface is passing parameters
      return false;
    }
    else if (messageCount==0 && args==null)
      return true;
    else if (messageCount!=args.length) 
    {
      //The number of arguments do not match.  This cannot be the method.
      return false;
    }
    else 
    {
      //The number of arguments in WSDL and remote interface match.
      //Make sure the parameter type (in the given order) is identical.
      Part[] inputPart=operation.getInputMessage().getMessagePart();
      int i=0;
      for (i=0; i<inputPart.length; i++) 
      {
        String qualifiedName=null;
        Part aPart=inputPart[i];
        Type aType=aPart.getType();
        if (aPart.getPartType()==Part.TYPE) 
        {
          qualifiedName=sci.getQualifiedJavaType(aType);
        }
        else if (aPart.getPartType()==Part.ELEMENT) 
        {
          Element aElement=this.wsdlHolder.getElement(aType.getTargetNamespace(),aType.getName());
          qualifiedName=sci.getQualifiedJavaType(aElement);
        }
        //We have converted XML type to Java form.  Check to see if this is identical
        //to input parameter
        String argumentQualifiedName=args[i].isArray()?args[i].getComponentType().getName()+"[]":
        args[i].getName();
        if (!(qualifiedName.equals(argumentQualifiedName))) 
        {
          if (sci.getWrapperClassName(qualifiedName)==null ||
          !(sci.getWrapperClassName(qualifiedName).equals(argumentQualifiedName)))
            break;
        }
      }//for
      if (i==inputPart.length)
        return true;
    }//else
    return false;
  }//isMethodParameterIdentical

  /**
   * Determines if the class passed in implements
   * com.wingfoot.soap.transport.Transport
   * @param altTransport The alternate transport to test
   * @returns true if it implements Transport, false if not
   */
   private boolean implementsTransport(Class altTransport)
   {
     boolean implTransport = false;
     Class[] cl = altTransport.getInterfaces();
     if(cl == null)
       implTransport = false;
     else 
     {
       for (int i = 0; cl != null && i < cl.length; i++)
       {
         if(cl[i].getName().equals("com.wingfoot.soap.transport.Transport"))
           implTransport = true;
       }//for
     }//else
     return implTransport;     
   }//implementsTransport

  /**
   * Retrieve the SOAPAction for an Operation.
   */
  private String getSOAPAction(Binding binding, Operation operation)
  throws ClientProxyException
  {
    BindingOperation bo=this.getBindingOperation(binding, operation);
    if (bo.getExtension() instanceof SOAPOperation)
    {
      return ((SOAPOperation)bo.getExtension()).getSoapAction();
    }
    return "";
  }//getSOAPAction 
   /**
    * Given a binding and operation, return the bindingOperation
    */
   private BindingOperation getBindingOperation(Binding binding, Operation operation) 
   throws ClientProxyException
   {
     for (int i=0; i<binding.getBindingOperationCount();i++)
     {
       BindingOperation bo=binding.getBindingOperation(i);
       if (bo.getOperation().equals(operation))
        return bo;
     }//for
    throw new ClientProxyException("ERROR_CLIENTPROXY_007:"+Constants.ERROR_CLIENTPROXY_007+" "
     +binding.getBindingName() + " " + operation.getName());
   }//getBindingOperation
}//class ClientProxy

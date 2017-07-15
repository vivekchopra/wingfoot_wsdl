package com.wingfoot.wsdl.gen;

import com.wingfoot.wsdl.*;
import com.wingfoot.tools.*;
import com.wingfoot.xml.*;


public interface DeploymentDescriptorWriter 
{
  /**
   * Takes a WSDLHolder and generates a Deployment Descriptor used 
   * when deploying a service to a Parvus Service. The name of the Deployment
   * Descriptor is the destinationName+"DD".xml The destinationName is retrieved
   * from the WSDLHolder
   * @param wsdl The WSDLHolder used for Deployment Descriptor generation
   * @param classToConvert The name of the class used for the deployment descriptor
   * @return SerializedHolder Encapsulates the byte[] representation of the
   * Deployment Descriptor
   */
  public SerializedHolder[] toDeploymentDescriptor(WSDLHolder wsdl, Class classToConvert, Options options)
  throws Exception;
}
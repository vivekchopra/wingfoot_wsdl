package com.wingfoot.wsdl;

import com.wingfoot.*;

/**
 * An Operation is a collection of Messages.  Its purpose
 * is to define which messages are inputs and which are 
 * outputs of the remote method.
 */
public class Operation 
{
  private String name;
  private Message inputMessage;
  private Message outputMessage;
  private String inputMessageName;
  private String outputMessageName;
  private String faultMessageName;
  private Message faultMessage;
  /**
   * Creates an Operation.  The name is 
   * mandatory.
   */
  public Operation(String name) throws WSDLException
  {
    if (name==null)
      throw new WSDLException("ERROR_WSDL_006:"+Constants.ERROR_WSDL_006);
    this.name=name;
  } //constructor;

  public String getName() 
  {
    return name;
  }

  public Message getInputMessage()
  {
    return inputMessage;
  }

  public void setInputMessage(Message newInputMessage)
  {
    inputMessage = newInputMessage;
  }

  public Message getOutputMessage()
  {
    return outputMessage;
  }

  public void setOutputMessage(Message newOutputMessage)
  {
    outputMessage = newOutputMessage;
  }

  public String getInputMessageName()
  {
    return inputMessageName;
  }

  public void setInputMessageName(String newInputMessageName)
  {
    inputMessageName = newInputMessageName;
  }

  public String getOutputMessageName()
  {
    return outputMessageName;
  }

  public void setOutputMessageName(String newOutputMessageName)
  {
    outputMessageName = newOutputMessageName;
  }

  /**
   * Compares two instance of Operation for equality.
   * Two Operations are equal if they have the same name AND
   * their inputMessageNames and outputMessageNames are identical.
   */
  public boolean equals(Operation operation) 
  {
    if (operation.getName().equals(this.getName()) &&
    ((operation.getInputMessageName()==null && this.inputMessageName==null) || 
    operation.getInputMessageName()!=null && operation.getInputMessageName().equals(this.inputMessageName)) &&
    ((operation.getOutputMessageName()==null && this.outputMessageName==null) || 
    operation.getOutputMessageName()!=null && operation.getOutputMessageName().equals(this.outputMessageName)))
      return true;
    else
      return false;
  }

  public String getFaultMessageName()
  {
    return faultMessageName;
  }

  public void setFaultMessageName(String newFaultMessageName)
  {
    faultMessageName = newFaultMessageName;
  }

  public Message getFaultMessage()
  {
    return faultMessage;
  }

  public void setFaultMessage(Message newFaultMessage)
  {
    faultMessage = newFaultMessage;
  }
}
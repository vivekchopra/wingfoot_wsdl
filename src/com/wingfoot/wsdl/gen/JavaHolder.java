package com.wingfoot.wsdl.gen;
import com.wingfoot.*;
import com.wingfoot.xml.*;
import com.wingfoot.wsdl.*;
import java.util.*;
import java.io.*;

/**
 * Class that encapsulates a Java Class.  This is
 * used to convert a WSDL stub to a Java class.
 * <p>
 * Instances of this class are created by wsdl2java
 * utility.  The toJava methods in this class are
 * responsible to return a byte[] representation
 * of this class.  This byte[] representation may
 * be written to a file system.
 */
 
public class JavaHolder 
{
  public static final int REGULAR_CLASS=1;
  public static final int INTERFACE=2;
  public static final int BEAN=3;
  private final int WHITESPACE_MULTIPLE=4; 
  private String className = null;
  private Hashtable properties;
  private Vector methods;
  private int classType;
  private Vector implement = null;
  private String extend = null;
  private String packge = null;
  
  /**
   * Creates an instance of a class.  Currently
   * only public classes are created.
   * @param className the name of the class.  
   * @param classType identifies the type of
   * class (interface, bean or regular).  The
   * value is one of the public int defined 
   * in this class.
   * @param packge the name of the package this class 
   * belongs in; null if default package.
   * @throws JavaHolderException if the className is null
   */
  public JavaHolder(String className, int classType, String packge) throws JavaHolderException
  {
    if (className==null|| (classType<REGULAR_CLASS && classType>BEAN))
      throw new JavaHolderException("ERROR_WSDL_029:"+Constants.ERROR_WSDL_029);
    this.className=className;
    this.classType=classType;
    this.packge=packge;
    if (classType==this.BEAN) 
    {
      implement=new Vector();
      implement.add("java.io.Serializable");
    }
  } //constructor

  /**
   * Returns the class name for the encapsulated class.
   * @return the class name.
   */
  public String getClassName() 
  {
    return this.className;
  }

  /**
   * Returns the type of the class encapsulated.
   * @return int the type of class encapsulated.
   * The value is one of the static int defined
   * in this class.
   */
  public int getClassType() 
  {
    return this.classType;
  }

  /**
   * Returns the package of the encapsulated class.
   * @return String with the package name; null if
   * no package was specified.
   */
  public String getPackage() 
  {
    return this.packge;
  }

  /**
   * Sets the property of a java bean.  This method is only
   * applicable if the constructor declares this class as
   * a bean.
   * @param propertyName the name of this property
   * @param propertyType the class of this property.  Please
   * note that the type must be a fully qualified class name
   * (java.lang.String).
   * @param JavaHolderException if an attempt is made to call 
   * this method and the class is not decalred as a bean.
   */
  public void setProperty(String propertyName, String propertyType)
  throws JavaHolderException 
  {
    if (classType!=this.BEAN) 
      throw new JavaHolderException("ERROR_WSDL_030:" + Constants.ERROR_WSDL_030);
    if (properties==null)
      properties=new Hashtable();
    properties.put(propertyName, propertyType);

    /**
     * In addition to creating properties, create 
     * the get and set methods.
     */
    String mName=propertyName.toUpperCase().charAt(0)+propertyName.substring(1);
    Method setM=new Method("set"+mName, null, null);
    setM.setMethodParameter(propertyName, propertyType);
    StringBuffer setSB= new StringBuffer("this.");
    setSB.append(propertyName);
    setSB.append("=");
    setSB.append(propertyName);
    setM.setStatement(setSB.toString());
    this.setMethod(setM);

    //Now the gets.
    Method getM=new Method("get"+mName, propertyType,null);
    StringBuffer getSB=new StringBuffer("return this.");
    getSB.append(propertyName);
    getM.setStatement(getSB.toString());
    this.setMethod(getM);
  } //setProperty

  /**
   * Returns a Hashtable of properties.  Properties are
   * private instance variables.
   * @return Hashtable of properties.  The key is the 
   * property name and the value is the property type;
   * returns null if no properties are set.
   */
  public Hashtable getProperties() 
  {
    return properties;
  }

  /**
   * Creates a method in the class.  A class can have
   * two methods with the same name.
   * @param methodName the name of the method.
   * @param returnType the return type encapsulated as
   * a String.  The returnType must be a fully qualified
   * name (java.lang.String etc.).
   * @param parameters a Vector of parameters for the 
   * method.  Each element of the Vector is a String[].
   * Index 0 contains the method Name and index 1 contains
   * the method type (fully qualified).
   * @param exceptionList a String[] with each element of the
   * array is a fully qualified Exception class that encapsulates
   * the exception thrown by the method; null if no exception 
   * is thrown.
   */
  public void setMethod(String methodName, String returnType, Vector parameters, String[] exceptionList) 
  {
    if (methods==null)
      methods=new Vector();
    Method m = new Method(methodName, returnType, exceptionList);
    methods.add(m);
    if (parameters!=null) 
    {
      for (int i=0; i<parameters.size(); i++) 
      {
        String[] s = (String[]) parameters.elementAt(i);
        m.setMethodParameter(s[0], s[1]);
      }
    } //if
  } //setMethod

  /**
   * Overloaded method to create a Method for a class.
   */
  public void setMethod(Method m) 
  {
    if (methods==null)
      methods=new Vector();
    methods.add(m);
  }

  /**
   * Returns a Vector of methods.  Each element
   * is an instance of the inner class Method.
   * @return Vector of Method (inner class); null
   * if no method is set.
   */
  public Vector getMethods() 
  {
    return methods;
  }

  /**
   * Sets the fully qualified name of the interface
   * that is implemented by the encapsulated class.
   * @param name fully qualified name of the implemented
   * interface.
   */
  public void setImplements (String name) 
  {
    if (implement==null)
      implement=new Vector();
    implement.add(name);
  }

  /**
   * Returns a Vector of Strings.  Each
   * String contains the fully qualified
   * name of the implemented interface.
   */
  public Vector getImplements() 
  {
    return implement;
  }
  /**
   * Sets the fully qualified name of the class
   * that is extended.
   * @param name of the base class.  The name
   * is fully qualified.
   */
  public void setExtend(String name) 
  {
    this.extend=name;
  }

  /**
   * Returns a String that encapsulates the
   * class extended by this class.
   * @return String the fully qualified name
   * of the extended class; null if none is
   * extended.
   */
  public String getExtend() 
  {
    return extend;
  }

  /**
   * Inner class that encapsulates the methods in the class.
   * A class may have multiple methods.
   */
  public class Method 
  {
      private String methodName, returnType;
      //Vector of String array. 0th element of array
      // is the parameter name and the 1st element
      // is the parameter type.
      private boolean isStatic = false;
      private Vector methodParameter;
      private Vector statement;
      private String[] exceptionArray=null;;
      
      public Method(String name, String retType, String[] exceptionList) 
      {
        methodName=name;
        returnType=retType;
        exceptionArray=exceptionList;
      }

      public String[] getExceptionList() 
      {
        return exceptionArray;
      }
      public String getMethodName() 
      {
        return methodName;
      }

      public String getMethodReturnType() 
      {
        return returnType;
      }

      public boolean getIsStatic()
      {
        return isStatic;
      }

      public void setIsStatic(boolean isStatic)
      {
        this.isStatic = isStatic;
      }
    
      public void setMethodParameter(String parameterName, String parameterType) 
      {
        if (methodParameter==null)
          methodParameter=new Vector();
        methodParameter.add(new String[] {parameterName, parameterType});
      }

      /**
       * Gets the parameters for the method.  The key is the 
       * parameter name and the value is the parameter type.
       */
      public Vector getMethodParameters() 
      {
        return methodParameter;
      }

      public void setStatement(String statement) 
      {
        if (this.statement==null)
          this.statement=new Vector();
        this.statement.add(statement);
      }

      /**
       * Returns a Vector of statements.  Each
       * element of the vector is a String.
       * @return Vector of statements; null if
       * no statement is specified for the method.
       */
      public Vector getStatements() 
      {
        return statement;
      }
  }//inner class Method

  /**
   * Converts instance of this JavaHolder to a
   * .java representation.  This representation
   * is returned as a byte[] array.
   * @return SerializedHolder encapsulates the 
   * byte representation and the name of the file
   * in the file system to write to.
   * @throws IOException if an error occurs while
   * writing the output stream.
   */
  public SerializedHolder toJava() throws IOException
  {
    StringBuffer sb = new StringBuffer();
    writeClassComment(sb,1);
    writePackage (sb,1);
    writeClassDefinition (sb, 1);
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bos));
    String str=sb.toString();
    bw.write(str,0,str.length());
    bw.flush();
    byte[] b = bos.toByteArray();
    SerializedHolder sh = new SerializedHolder(b, this.getClassName()+".java");
    if (bw!=null)
      bw.close();
    if (bos!=null)
      bos.close();
    return sh;
  }

  /**
   * Writes the class definition.  A class
   * definition declares the class.
   * @param sb the StringBuffer where the
   * class definition is appended.
   * @param numberOfSpaces the number of
   * whitespaces to prefix each line with.
   */
   private void writeClassDefinition(StringBuffer sb, int numberOfSpaces) 
   {
     writeIndent(sb, numberOfSpaces);
     sb.append("public ");
     sb.append(this.getClassType()==this.INTERFACE? "interface ":"class ");
     sb.append( this.getClassName()).append(" ");
     if (this.getExtend()!=null) 
     {
       sb.append("extends ");
       sb.append(this.getExtend()+" ");
     }
     if (this.getImplements()!=null) 
     {
       sb.append("implements ");
       Vector v = this.getImplements();
       for (int i=0; i<v.size(); i++) 
       {
         if (i!=0)
          sb.append(", ");
         String s = (String)v.elementAt(i);
         sb.append(s);
         //sb.append(" ");
       } //for
     } //if
     
     //Write the opening brace for the class.
     sb.append("\n");
     this.writeIndent(sb,numberOfSpaces);
     sb.append("{ \n");

     //Write the properties of this class.  These are always private.
     writeProperties(sb, numberOfSpaces+this.WHITESPACE_MULTIPLE);

     //Write the methods of this class.
     writeMethods(sb, numberOfSpaces+this.WHITESPACE_MULTIPLE);

     //Write the closing brace for the class
     //sb.append("\n");
     this.writeIndent(sb, numberOfSpaces);
     sb.append("} \n");
   }//writeClassDefinition

   /**
    * Writes the methods and its statements.
    */
   private void writeMethods(StringBuffer sb, int numberOfSpaces) 
   {
     if (this.getMethods()==null)
      return;
     Vector v = this.getMethods();
     for (int i=0; i<v.size(); i++) 
     {
       this.writeIndent(sb, numberOfSpaces);
       Method m = (Method)v.elementAt(i);
       sb.append("public ");
       
       if(m.getIsStatic())
        sb.append("static ");
       
       //Write the return type
       sb.append(m.getMethodReturnType()==null?"void ":m.getMethodReturnType()+" ");
       //Write the method name
       sb.append(m.getMethodName());
       sb.append("(");
       //Write the parameters
       if (m.getMethodParameters()!=null) 
       {
         Vector ht = m.getMethodParameters();
         for (int k=0; k<ht.size(); k++) 
         {
           String[] s = (String[]) ht.elementAt(k);
           if (k!=0)
            sb.append(", ");
           sb.append(s[1]);
           sb.append(" ");
           sb.append(s[0]);
         } //for
       } //if
       sb.append(")");

       //If applicable declare the exceptions that are thrown.
       String exceptionList[]=m.getExceptionList();
       if (exceptionList!=null) 
       {
         sb.append(" throws");
         for (int z=0; z<exceptionList.length; z++) 
         {
           String s = exceptionList[z];
           if (s==null) continue;
           if (z!=0)
            sb.append(",");
           sb.append(" ").append(s);
         }//for
       }//if
       
       //If this is an interface, just close the method.
       if (this.getClassType()==this.INTERFACE)
        sb.append(";");
       else 
       {
         //Write the method body.
         sb.append("\n");
         this.writeIndent(sb,numberOfSpaces);
         sb.append("{");
         if (m.getStatements()!=null) 
         {
           Vector vs= m.getStatements();
           for (int j=0; j<vs.size(); j++) 
           {
             sb.append("\n");
             this.writeIndent(sb, numberOfSpaces+this.WHITESPACE_MULTIPLE);
             sb.append((String)vs.elementAt(j));
             sb.append(";");
           } //for
         } //if
         sb.append("\n");
         this.writeIndent(sb,numberOfSpaces);
         sb.append("}");
       } //else
       sb.append("\n\n");
     } //for
   } //writeMethods
   
   /**
    * Write the properties.  These are class instance variables.
    * @param sb the StringBuffer to write to.
    * @param the number of white spaces to prefix each line with.
    */
   private void writeProperties(StringBuffer sb, int numberOfSpaces) 
   {
     if (this.getProperties()!=null) 
     {
       Hashtable ht = this.getProperties();
       Enumeration keys=ht.keys();
       while(keys.hasMoreElements()) 
       {
         String aParameterName=(String)keys.nextElement();
         String aParameterType=(String)ht.get(aParameterName);
         this.writeIndent(sb,numberOfSpaces);
         sb.append("private ");
         sb.append(aParameterType + " ");
         sb.append(aParameterName);
         sb.append("; \n\n");
       }//while
     } //if
   } //writeProperties
   
  /**
   * Writes the comment that appears at the
   * top of each package.
   */
  private void writeClassComment(StringBuffer sb, int numberOfSpaces) 
  {
    writeIndent(sb, numberOfSpaces);
    sb.append("/**").append("\n");
    writeIndent(sb, numberOfSpaces);
    sb.append(" * Class generated from WSDL by Wingfoot Parvus " + Constants.VERSION).append("\n");
    sb.append("  * on ");
    sb.append(new java.util.Date().toString()).append(".\n");
    writeIndent(sb, numberOfSpaces);
    sb.append(" */").append("\n").append("\n");
  }
  
  /**
   * Writes the package statement.
   */
  private void writePackage (StringBuffer sb, int numberOfSpaces) 
  {
    if(this.getPackage() != null) {
      writeIndent(sb, numberOfSpaces);
      sb.append("package ");
      sb.append(this.getPackage());
      sb.append(";");
      sb.append("\n").append("\n");
    }
  } //writePackage

  /**
   * Prefixes a StringBuffer with spaces.  The number
   * of spaces is determined by leadingSpaces parameter.
   */
  private void writeIndent(StringBuffer sb, int leadingSpaces) 
  {
    if (leadingSpaces<0)
      return;
    for (int i=0; i<leadingSpaces; i++)
      sb.append(" ");
  } //writeIndent
  
} //ObjectHolder

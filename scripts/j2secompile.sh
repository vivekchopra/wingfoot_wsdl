#! /bin/bash
# 
# Compile script for wingfoot_wsdl
#

if test -z ${WINGFOOT_HOME}
then
     echo "Please set the WINGFOOT_HOME variable."
     exit 0
fi

if test -z ${WINGFOOT_SUFFIX}
then
     echo "Please set the WINGFOOT_SUFFIX variable."
     echo "It contains the version number to suffix to the jar files.  For example to create parvus1_0.jar, the suffix is 1_0"
     exit 0
fi

WINGFOOT_WSDL=${WINGFOOT_HOME}/wingfoot_wsdl
#CLASSPATH=../extern/j2se_kxml.jar:./src
#COMPILED_CLASSES=./build/j2se_classes
CLASSPATH=${WINGFOOT_WSDL}/src:${WINGFOOT_HOME}/wingfoot_parser/build/lib/j2se_kxml.jar:${WINGFOOT_HOME}/wingfoot_soapserver/build/lib/wsoapServer.jar
COMPILED_CLASSES=${WINGFOOT_WSDL}/build/j2se_classes
SERVERNAME=parvus${WINGFOOT_SUFFIX}.jar
ADMINNAME=deploymentAdmin${WINGFOOT_SUFFIX}.jar

case $1 in
    compileall)
      ## Copy the src code from wingfoot_wsdl and wingfoot_soapserver 
      ## to a temp directory and compile them all.
      if test ! -d ${WINGFOOT_WSDL}/build/j2se_classes 
      then
	    mkdir ${WINGFOOT_WSDL}/build/j2se_classes 
      fi
      WINGFOOT_TMP=${WINGFOOT_HOME}/tmpCompileDir
      ALLCLASSPATH=${WINGFOOT_TMP}:${WINGFOOT_HOME}/wingfoot_parser/build/lib/j2se_kxml.jar:${WINGFOOT_HOME}/wingfoot_extern/java/servlet.jar:${WINGFOOT_HOME}/wingfoot_extern/ant-1.5.1/ant.jar

      if test -d ${WINGFOOT_TMP}
      then
      	   echo "Removing ${WINGFOOT_TMP}....."
      	   rm -rf ${WINGFOOT_TMP}
      fi
      echo Creating temp directory....
      mkdir ${WINGFOOT_TMP}
      echo Copying wingfoot_soapserver src....
      cp -R ${WINGFOOT_HOME}/wingfoot_soapserver/src/* ${WINGFOOT_TMP}
      echo Copying wingfoot_wsdl src....
      cp -R ${WINGFOOT_HOME}/wingfoot_wsdl/src/* ${WINGFOOT_TMP}
      echo Copying wingfoot_ant src....
      cp -R ${WINGFOOT_HOME}/wingfoot_ant/src/* ${WINGFOOT_TMP}

      echo Compiling java classes....
      for javafile in `find ${WINGFOOT_TMP} -type f -name '*.java' -print`
      do
	  echo $javafile
	  javac -g:none -classpath $ALLCLASSPATH \
              -d $COMPILED_CLASSES $javafile
	done
      rm -rf ${WINGFOOT_TMP}
      ;;

    compile)
      ## Create the j2se_classes directory.  This is a temp directory
      ## and is deleted at the end of the script
	##if test ! -d ${WINGFOOT_WSDL}/build/j2se_classes 
	  ##  then
	    ##mkdir ${WINGFOOT_WSDL}/build/j2se_classes 
	##fi
	
	##echo Compiling java classes....
	##for javafile in `find ${WINGFOOT_WSDL}/src -type f -name '*.java' -print`
	  ##do
	  ##echo $javafile
	  ##javac -g:none -classpath $CLASSPATH \
            ##  -d $COMPILED_CLASSES $javafile
	##done
	echo "Please use compileall option.  It pulls source from wingfoot_wsdl AND wingfoot_soapserver to generate the necessary binaries"
	;;
    
    obfuscate)
	cd $COMPILED_CLASSES
	jar -cvf classes.jar com/*
	java -classpath ${WINGFOOT_HOME}/wingfoot_parser/build/lib/j2se_kxml.jar:${WINGFOOT_HOME}/wingfoot_extern/retroguard-v1.1/retroguard.jar:${WINGFOOT_HOME}/wingfoot_extern/java/servlet.jar:${WINGFOOT_HOME}/wingfoot_extern/ant-1.5.1/ant.jar RetroGuard classes.jar parvus.jar ${WINGFOOT_WSDL}/build/scriptj2se.rgs
	mv parvus.jar ${WINGFOOT_WSDL}/build/lib/
        rm -rf ${WINGFOOT_WSDL}/build/j2se_classes 
	cd  ${WINGFOOT_WSDL}
	;; 
    
    clean)
	rm -f  ${WINGFOOT_WSDL}/build/lib/*.jar
	rm -rf ${WINGFOOT_WSDL}/build/tempjar
	;;
    
    package)
	mkdir ${WINGFOOT_WSDL}/build/tempjar
	cp ${WINGFOOT_WSDL}/build/lib/parvus.jar ${WINGFOOT_WSDL}/build/tempjar
	cp ${WINGFOOT_HOME}/wingfoot_parser/build/lib/j2se_kxml.jar ${WINGFOOT_WSDL}/build/tempjar
	cd ${WINGFOOT_WSDL}/build/tempjar
	jar -xvf j2se_kxml.jar
	jar -xvf parvus.jar
	rm -f parvus.jar
	rm -rf META-INF
	jar -cvf ${SERVERNAME} org/* com/wingfoot/soap/*.class com/wingfoot/soap/encoding/*.class com/wingfoot/soap/transport/*.class com/wingfoot/soap/server/HTTPListener.class com/wingfoot/soap/server/RouterException.class com/wingfoot/soap/server/Service.class com/wingfoot/soap/server/SOAPRouter.class com/wingfoot/*.class com/wingfoot/registry/*.class com/wingfoot/tools/*.class com/wingfoot/wsdl/*.class com/wingfoot/wsdl/soap/*.class com/wingfoot/wsdl/gen/*.class com/wingfoot/xml/*.class com/wingfoot/xml/schema/*.class  com/wingfoot/xml/schema/gen/*.class com/wingfoot/xml/schema/groups/*.class com/wingfoot/xml/schema/types/*.class 

	jar -cf ${ADMINNAME} org/* com/wingfoot/soap/server/DeploymentAdmin.class com/wingfoot/soap/server/DeploymentException.class com/wingfoot/soap/server/Service.class com/wingfoot/tools/ant/*.class

	cp ${SERVERNAME} ${WINGFOOT_WSDL}/build/lib/
	cp ${ADMINNAME} ${WINGFOOT_WSDL}/build/lib/
	cd ${WINGFOOT_WSDL}
	rm -rf ${WINGFOOT_WSDL}/build/tempjar
	rm -f ${WINGFOOT_WSDL}/build/lib/parvus.jar
	;;
    *)
	echo "usage compile.sh [compile|compileall|obfuscate|package|clean|usage]"
	;;
esac

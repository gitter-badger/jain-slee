/*
 * ConcreteUsageParameterClassGenerator.java
 * 
 * Created on Jan 9, 2005
 * 
 * Created by: M. Ranganathan
 *
 * The Mobicents Open SLEE project
 * 
 * A SLEE for the people!
 *
 * The source code contained in this file is in in the public domain.          
 * It can be used in any project or product without prior permission, 	      
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
 * AND DATA ACCURACY.  We do not warrant or make any representations 
 * regarding the use of the software or the  results thereof, including 
 * but not limited to the correctness, accuracy, reliability or 
 * usefulness of the software.
 */

package org.mobicents.slee.container.deployment;

import java.beans.Introspector;
import java.util.HashSet;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import javax.slee.usage.SampleStatistics;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.component.ClassPool;
import org.mobicents.slee.runtime.usage.AbstractUsageParameterSet;

/**
 * @author M.Ranganathan
 * @author martins
 * @author baranowb 
 */
public class ConcreteUsageParameterClassGenerator {

    private static Logger logger = Logger.getLogger(ConcreteUsageParameterClassGenerator.class);

    private static final String _METHOD_INCREMENT_="incrementParameter";
    private static final String _METHOD_GET_PARAMETER_="getParameter";
    private static final String _METHOD_SAMPLE_="sampleParameter";
    private static final String _METHOD_GET_SAMPLE_="getParameterSampleStatistics";
    private static final String _METHOD_GET_PARAM_NAMES_="getParameterNames";

    
    private final ClassPool classPool;

    private final String usageParameterInterfaceName;
    
    private final String deploymentDir;
    
    private final HashSet<String> generatedFields = new HashSet<String>();

    public ConcreteUsageParameterClassGenerator(String usageParameterInterfaceName, String deploymentDir, ClassPool classPool) {
        this.usageParameterInterfaceName = usageParameterInterfaceName;
        this.deploymentDir = deploymentDir;
    	this.classPool = classPool;
    }

	public Class<?> generateConcreteUsageParameterClass() throws Exception {
		CtClass usageParamInterface = classPool.get(usageParameterInterfaceName);

		String concreteClassName = usageParameterInterfaceName + "Impl";

		CtClass abstractSuperClass = classPool.get(AbstractUsageParameterSet.class.getName());

		CtMethod[] methods = usageParamInterface.getMethods();

		CtClass ctClass = classPool.makeClass(concreteClassName);

		try {
			//Generates the implements link
            ConcreteClassGeneratorUtils.createInterfaceLinks(ctClass,
                    new CtClass[] { usageParamInterface });
            ConcreteClassGeneratorUtils.createInheritanceLink(ctClass, abstractSuperClass);
			
            for (int i = 0; i < methods.length; i++) {
                generateConcreteMethod(ctClass, methods[i]);
            }
            
            generateParamNamesGetter(abstractSuperClass,ctClass);
            //generateResetMethod(ctClass);
         // write file
            ctClass.writeFile(deploymentDir);
            if (logger.isDebugEnabled())
                logger.debug("UsageParameterGenerator Writing file "
                        + concreteClassName);
            Class<?> retval = Thread.currentThread().getContextClassLoader()
                    .loadClass(concreteClassName);
            return retval;
		} finally {

			ctClass.defrost();
		}
	}
    
  

    private void generateParamNamesGetter(CtClass declaring, CtClass destination) throws NotFoundException, CannotCompileException {
		//little helper method, it is used on init, only once.
    	CtMethod abstractMethod = declaring.getDeclaredMethod(_METHOD_GET_PARAM_NAMES_);
    	CtMethod concreteMethod = CtNewMethod.copy(abstractMethod,
    			destination, null);
		String body="{";
		body += java.util.HashSet.class.getName()+" ret = new "+java.util.HashSet.class.getName()+"();";
		for(String fieldName : this.generatedFields)
		{
			body +="ret.add(\""+fieldName+"\");";
		}
		
		
		body += "return ($r)ret;";
		body += "}";
		
		 if (logger.isDebugEnabled()) {
	        	logger.debug("Adding get usage param names getter with source "+body);
	        }
		 
		 concreteMethod.setBody(body);
		 destination.addMethod(concreteMethod);
	}

	
    /**
     * @param method
     * @return
     */
    private void generateConcreteMethod(CtClass ctClass, CtMethod method)
            throws Exception {
        /*
         * The SLEE derives the usage parameter type associated with this usage
         * parameter name from the method name of the declared method.The SBB
         * Developer declares an increment method to declare the presence of and
         * to permit updates to a counter-type usage parameter. The method name
         * of the increment method is derived by adding an �increment� prefix to
         * the usage parameter name. ( See chapter 11.2.1 ).
         */
        String methodName = method.getName();

        String paramName = null;
        if (methodName.startsWith("increment")) {
            
        	paramName = Introspector.decapitalize(methodName.substring("increment".length()));
            
            
            this.generatedFields.add(paramName);

        } else if (methodName.startsWith("sample")) {
            /*
             * Sample-type usage parameters. The SBB can add sample values to a
             * sample -type usage parameter. The Adminstrator can get the
             * minimum, maximum, mean, and number of the sample values added to
             * a sample -type usage parameter and reset the state of the usage
             * parameter through the SLEE�s management interface.
             */
            
            paramName = Introspector.decapitalize(methodName.substring("sample".length()));
            
           
            this.generatedFields.add(paramName);

        } else
            return; // TODO -- should I throw exception here ?
        
        if ( logger.isDebugEnabled() )
            logger.debug("generateConcreteMethod(): USAGEPARAM variable is " + paramName);
        
        if (logger.isDebugEnabled())
            logger.debug("Generating usage method = " + methodName);
        
        String body = "";
        String getterBody = "";
        String getter11Body = "";

        if (methodName.startsWith("increment")) {

            body += "public synchronized void " + methodName + "( long longValue ) { ";
            body +="super."+_METHOD_INCREMENT_+"(\""+paramName+"\",longValue);";

            body += "}";

            getterBody += "public long get" + methodName.substring("increment".length())
                    + "( boolean reset) { ";
            getterBody +="return super."+_METHOD_GET_PARAMETER_+"(\""+paramName+"\",reset);";
            getterBody += "}";
            
            getter11Body += "public long get" + methodName.substring("increment".length())
            + "() { return super."+_METHOD_GET_PARAMETER_+"(\""+paramName+"\",false); }";

        } else if (methodName.startsWith("sample")) {
            /*
             * Sample-type usage parameters. The SBB can add sample values to a
             * sample -type usage parameter. The Adminstrator can get the
             * minimum, maximum, mean, and number of the sample values added to
             * a sample -type usage parameter and reset the state of the usage
             * parameter through the SLEE�s management interface.
             */

            body += "public void " + methodName + "( long longValue ) { ";
            body += "super."+_METHOD_SAMPLE_+"(\""+paramName+"\",longValue);";
            body += "}";

            getterBody += "public " + SampleStatistics.class.getName() + " get"
                    + methodName.substring("sample".length()) + "( boolean reset) { ";
            getterBody += "return super."+_METHOD_GET_SAMPLE_+"(\""+paramName+"\",reset);";
            getterBody += "}";
            
            getter11Body += "public " + SampleStatistics.class.getName() + " get" + methodName.substring("sample".length())
            + "() { return super."+_METHOD_GET_SAMPLE_+"(\""+paramName+"\",false); }";          
            
        } else {
            return;
        }

        if (logger.isDebugEnabled()) {
        	logger.debug("Adding SLEE 1.0 usage param getter with source "+getterBody);
        }
        CtMethod getterMethod = CtNewMethod.make(getterBody, ctClass);
        ctClass.addMethod(getterMethod);
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Adding SLEE 1.1 usage param getter with source "+getter11Body);
        }
        CtMethod getter11Method = CtNewMethod.make(getter11Body, ctClass);
        ctClass.addMethod(getter11Method);
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Adding usage param setter with source "+body);
        }
        CtMethod setterMethod = CtNewMethod.make(body, ctClass);
        ctClass.addMethod(setterMethod);
    }
   // private void generateResetMethod(CtClass ctClass) throws Exception {
    //	
    //}
}

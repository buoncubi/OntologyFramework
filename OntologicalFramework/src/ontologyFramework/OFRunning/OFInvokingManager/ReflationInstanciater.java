package ontologyFramework.OFRunning.OFInvokingManager;

import ontologyFramework.OFContextManagement.synchronisingManager.OFSynchroniserManagmentInterface;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFErrorManagement.OFException.OFExceptionNotifierInterface;
import ontologyFramework.OFEventManagement.OFEventParameterInterface;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerInterface;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;

/**
 *
 * This is a static class which collects common methods to instantiate OFInterfaces using 
 * Java Reflection. It use neither generic nor dynamic usage of the Reflection API to 
 * decrease the computational complexity.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */
// ps: tutti i metodi sono un copia e incolla tra loro ....
public class ReflationInstanciater {

	/**
	 * Name of an ontological individual which must exist belong to the class {@code DebuggedClass}
	 * and has to have an object property {@code logsDebuggingData exactly 1 Boolean}
	 */
	public static final String REFLACTIONDERDEBUG_individualName = "C_ReflactionDebug";

	@SuppressWarnings("unused")
	private static OFDebugLogger logger = new OFDebugLogger( ReflationInstanciater.class, false);//DebuggingClassFlagData.getFlag( REFLACTIONDERDEBUG_individualName));

	/**
	 *  Static class with all static method. Constructor non instatiable 
	 */
	private ReflationInstanciater() {
		throw new AssertionError();
	}

	/**
	 * Given a class name as string it creates a new instances of {@link OFDataMapperInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 * 
	 * @param packageClassName full class qualifier 
	 * @return mapperInst an new instance of the named class which implements {@link OFDataMapperInterface}.
	 */
	@SuppressWarnings("rawtypes")
	static synchronized public OFDataMapperInterface<?, ?> instanciateOFDataMapperByName( String packageClassName){
		Class<?> t1;
		OFDataMapperInterface<?, ?> mapperInst = null;
		try {
			t1 = Class.forName( packageClassName);
			mapperInst = ( OFDataMapperInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + mapperInst);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( mapperInst);
	}

	/** 
	 * Given a class name as string it creates a new instances of {@link OFBuilderInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 * 
	 *
	 * @param packageClassName full class qualifier 
	 * @return builderInst an new instance of the named class which implements {@link OFBuilderInterface}.
	 */
	@SuppressWarnings("rawtypes")
	static synchronized public OFBuilderInterface instanciateOFBuilderByName( String packageClassName){
		Class<?> t1;
		OFBuilderInterface builderInst = null;
		try {
			t1 = Class.forName( packageClassName);
			builderInst = ( OFBuilderInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + builderInst);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( builderInst);
	}

	/**
	 * Given a class name as string it creates a new instances of {@link OFExceptionNotifierInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 *  
	 * @param packageClassName full class qualifier 
	 * @return exectNotifyInst an new instance of the named class which implements {@link OFExceptionNotifierInterface}.
	 */
	static synchronized public OFExceptionNotifierInterface instanciateOFExceptionNotifierByName( String packageClassName){
		Class<?> t1;
		OFExceptionNotifierInterface exectNotify = null;
		try {
			t1 = Class.forName( packageClassName);
			exectNotify = ( OFExceptionNotifierInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + exectNotify);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( exectNotify);
	}

	/**
	 * Given a class name as string it creates a new instances of {@link OFEventParameterInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 * 
	 * @param packageClassName full class qualifier 
	 * @return eventParamInst an new instance of the named class which implements {@link OFEventParameterInterface}.
	 */
	static synchronized public OFEventParameterInterface instanciateOFEventParameterByName( String packageClassName){
		Class<?> t1;
		OFEventParameterInterface eventParam = null;
		try {
			t1 = Class.forName( packageClassName);
			eventParam = ( OFEventParameterInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + eventParam);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( eventParam);
	}

	/**
	 * Given a class name as string it creates a new instances of {@link OFSynchroniserManagmentInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 * 
	 * @param packageClassName full class qualifier 
	 * @return synchInst an new instance of the named class which implements {@link OFSynchroniserManagmentInterface}.
	 */
	static synchronized public OFSynchroniserManagmentInterface instanciateOFSynchroniseerManagerByName( String packageClassName){
		Class<?> t1;
		OFSynchroniserManagmentInterface synchInst = null;
		try {
			t1 = Class.forName( packageClassName);
			synchInst = ( OFSynchroniserManagmentInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + synchInst);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( synchInst);
	}

	/**
	 * Given a class name as string it creates a new instances of {@link OFEventInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 * 
	 * @param packageClassName full class qualifier 
	 * @return eventInst an new instance of the named class which implements {@link OFEventInterface}.
	 */
	static synchronized public OFEventInterface instanciateOFEventByName( String packageClassName){
		Class<?> t1;
		OFEventInterface eventInst  = null;
		try {
			t1 = Class.forName( packageClassName);
			eventInst = ( OFEventInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + eventInst);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( eventInst);
	}

	/**
	 * Given a class name as string it creates a new instances of {@link OFTimeTriggerInterface},
	 * and returns it. It returns null value if an exception is thrown; in this case the message is 
	 * handled by {@link OFDebugLogger}
	 * 
	 * @param packageClassName full class qualifier 
	 * @return timeTriggetInst an new instance of the named class which implements {@link OFTimeTriggerInterface}.
	 */
	public synchronized static OFTimeTriggerInterface instanciateOFTimeTriggrtByName(
			String packageClassName) {
		Class<?> t1;
		OFTimeTriggerInterface timeTriggetInst = null;
		try {
			t1 = Class.forName( packageClassName);
			timeTriggetInst = ( OFTimeTriggerInterface) t1.newInstance();
			//logger.addDebugStrign( " Java Reflaction instanciates new : " + timeTriggetInst);
		} catch (ClassNotFoundException e) {
			notifyError( packageClassName, e);
		} catch (InstantiationException e) {
			notifyError( packageClassName, e);
		} catch (IllegalAccessException e) {
			notifyError( packageClassName, e);
		}
		return( timeTriggetInst);
	}

	private static void notifyError( String className, Exception e){
		//logger.addDebugStrign( " error in instanciate class at" + className + " java stackTrace: " + e.getStackTrace().toString(), true);
		//System.err.println( " error in instanciate class at" + className + " java stackTrace: " + e.getCause());
	}
}

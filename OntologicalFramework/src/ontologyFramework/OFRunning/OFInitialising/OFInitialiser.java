package ontologyFramework.OFRunning.OFInitialising;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;


/**
 * This class is called during software startup. It porposes is to create an ontology reference ({@link ontologyFramework.OFContextManagement.OWLReferences})
 * and build up properties which do not change frequently during execution. 
 * Finally, them are organise inside a static and common HashMap accessible through the
 * class {@link OFBuiltMapInvoker}.
 * <p>
 * By default it calls all the implementation of the interface {@link OFBuilderInterface}
 * which are described by an ontological individual as:
 * <pre>
 * {@code BuilderIndividual € OFBuilder }         [ please refer to ... {@link #BUILDER_className}]
 * 		{@code hasKeyWords exactly 1 KeyWordInd}           [{@link OFBuilderCommon#HASKEYWORDS_objProp}]
 * 		{@code hasListName exactly 1 ListName}              [{@link OFBuilderCommon#BUILDLISTNAME_objProp}]
 * 		{@code implementsOFBuilderName exactly 1 PathName}  [{@link OFBuilderCommon#CLASSPACKAGE_objProp}]
 * </pre>
 * Than, when {@link OFBuilderInterface#buildInfo(String[], OWLReferences, OFBuiltMapInvoker)}
 * has been called this class gets from {@link OFBuilderInterface#getInitialisedObject()} a Map of
 * Objects that will be available on the static class {@link OFBuiltMapInvoker} for further usage.
 * 
 * <p> 
 *
 * @see OFBuilderCommon
 * @see OFBuilderInterface
 * @see OFBuiltMapInvoker
 * @see OWLReferences
 *
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
  
public class OFInitialiser {

	/**
	 * Define the symbol for the name separator. Equal to {@value #INVOKERNAME_separatorSymb}
	 */
	public static final String INVOKERNAME_separatorSymb = "@";
	/**
	 * IdentifiesgetFlag the name of the new instance of the class {@link OFBuiltMapInvoker} which can
	 * be used to statically refer to the initialised list builded from this class. It
	 * is equal to {@code "listInvoker" + this.toString().substring( this.toString().indexOf( INVOKERNAME_separatorSymb ))}.
	 */
	public final String INVOKER_InstanceName =  "listInvoker" + this.toString().substring( this.toString().indexOf( INVOKERNAME_separatorSymb));
	/**
	 * It defines the name of the ontological class in which  
	 * individuals must be located, to run up procedures for initialising classes. 
	 * It is, by default, equal to {@value #BUILDER_className}. 
	 */
	public static final String BUILDER_className = "OFBuilder";
	/**
	 * If the name of an individual inside the ontological class: {@value #BUILDER_className};
	 * contains the String {@link #MAPPER_NameContains} ( by default equal to {@value #MAPPER_NameContains}).
	 * Than, is assured that the first occurrence of such individuals will fire the
	 * building mechanism (described in this class) always before of the other
	 * individual belong to the same ontological class.
	 */
	public static final String MAPPER_NameContains = "Mapper";
	/**
	 * If the name of an individual inside the ontological class: {@value #BUILDER_className};
	 * contains the String {@link #PROCEDURE_NameContains} ( by default equal to {@value #PROCEDURE_NameContains}).
	 * Than, is assured that the first occurrence of such individuals will fire the
	 * building mechanism (described in this class) after that the other
	 * individual belong to the same ontological class has been initialised.
	 */
	public static final String PROCEDURE_NameContains = "Procedure";
	/**
	 * It defines the name of the ontological class in which the master debug
	 * configuration is belong to. By defaults it is set to {@value #DEBUGGER_ClassName}.
	 * More detail on {@link OFBuilderCommon#buildDebugger}
	 */
	public static final String DEBUGGER_ClassName = "Debugger";
	
	/**
	 * Defines the name of an individual belong to the ontological class {@value ontologyFramework.OFErrorManagement.DebuggingClassFlagData#DEBUGGER_classFlags}
	 * which describe, with the boolean value, if this class should produce logs or not.
	 * More detail on {@link DebuggingClassFlagData}  
	 */
	public static final String BUILDERDEBUG_individualName = "C_DebugBuilder";
	public static final String SERIALIZATORDEBUG_individualName = "C_SerializationDebug";
	
	
	// list that this class creates
	private final OFBuiltMapInvoker staticList = new OFBuiltMapInvoker( INVOKER_InstanceName);
	// reference to the ontology which contains the individual to initialise the framework
	private OWLReferences ontoRef;
	// dissallow to initialise the class if this has been created without load an ontology 
	private boolean initialised = false;
	// instance to print out information
	private OFDebugLogger logger;
	private boolean initialDebugginFlag = true;
	
	/**
	 * Create new, without any effects.
	 * 
	 * It does not have any effects. It can be used to access only to the methods
	 * {@link #buildIndividual(String, OWLReferences)} and {@link #buildIndividual(OWLNamedIndividual, OWLReferences)}.
	 */
	public OFInitialiser(){
		ontoRef = null;
		logger = new OFDebugLogger( this, false);
	}
	
	/**
	 * Create new reference to an ontology
	 *
	 * @param ontoName a key Name attached to this ontology reference
	 * @param filepath directory absolute path to the owl file 
	 * @param ontologyPath IRI path associated to the relative ontology
	 * @param command to create or load (from file or from web) an ontology
	 * @throws OWLOntologyCreationException
	 * <p>
	 * Create new reference to ontology building a new {@link ontologyFramework.OFContextManagement.OWLReferences#OWLReferences(String, String, String, int)};
	 * where inputs value are passed with the same meaning and order between those constructors.
	 */
	public OFInitialiser( String ontoName, String filepath, String ontologyPath, int command) throws OWLOntologyCreationException{
			
		logger = new OFDebugLogger( this, "INITIALISER STARTS loading ontology... ", initialDebugginFlag);
		// get onto References (load ontology)
		ontoRef = new OWLReferences( ontoName, filepath, ontologyPath, command);
		
		if( DebuggingClassFlagData.rebuild( ontoRef))
			logger.setFlagToFollow( DebuggingClassFlagData.getFlag( BUILDERDEBUG_individualName)); 
		
		initialised = true;
		logger.addDebugStrign( " ... create OWLReferences to : " + ontoRef.getOntology());
	}
	
	/**
	 * Initialise individual belong to ontological class {@value #BUILDER_className}.
	 * 
	 * @return the manager of the map which contains all the initialised class ({@link #staticList}).
	 * <p>
	 * It process all the ontological individuals belong to the class which has name {@value #BUILDER_className}.
	 * For all of them it runs {@link #buildIndividual(OWLNamedIndividual, OWLReferences)}
	 * and update the list of initialised class. Moreover, to assure consistency, it
	 * look for an individual which as a name that contains the key word {@value #MAPPER_NameContains}
	 * If it exist it is processed for first. This methods return null and does not
	 * have any further computation if it is called from a class which has been created
	 * using the constructor {@link #OFInitialiser()} 
	 * 
	 */
	public OFBuiltMapInvoker initialise(){
		
		if( initialised){
			// build debugger configuration
			OWLNamedIndividual debuggInd = ontoRef.getOnlyIndividualB2Class( DEBUGGER_ClassName);
			OFBuilderCommon.buildDebugger( debuggInd, staticList, ontoRef);
			
			logger.addDebugStrign( "system initialisation --> FOBuilder interfaces running ...");
			logger.addDebugStrign( " -------------------------------------------- " );
	
			// get the builder from ontology (ind € OFBuilder)
			OWLClass buildersClass = ontoRef.getOWLClass( BUILDER_className);
			Set<OWLNamedIndividual> buildersInd = ontoRef.getIndividualB2Class( buildersClass);
			
			// catch the mapper first
			boolean find = false;
			for( OWLNamedIndividual i : buildersInd){
				String name = OWLLibrary.getOWLObjectName( i);
				if( name.contains( MAPPER_NameContains)){
					// build mapper
					buildIndividual( i, ontoRef);
					// remove from list
					buildersInd.remove( i);
					find = true;
					break;
				}
			}
			if( ! find)
				logger.addDebugStrign("Exception : mapper not found");
			
			
			// build other classes
			OWLNamedIndividual procedureBuilder = null;
			for( OWLNamedIndividual i : buildersInd){
				if( OWLLibrary.getOWLObjectName(i).contains( PROCEDURE_NameContains))
					procedureBuilder = i;
				else buildIndividual( i, ontoRef);
			}
			
			// build procedure as the last
			if( procedureBuilder != null)
				buildIndividual( procedureBuilder, ontoRef);
			else logger.addDebugStrign("Exception : procedures not found"); 
			
			
			// initialise logger NOT WORKING !!!!
			//org.apache.log4j.BasicConfigurator.configure();
			
			logger.addDebugStrign( " final static lists : " + staticList.getMap());
			logger.addDebugStrign( "############################   Onotlogical Framework initialised   ############################");
			return( staticList);
		}
		else{
			logger.addDebugStrign("EXCEPTIOM");
			return( null);
		}
	}
	
	/**
	 * Retrieve the OWLNameIndividual thought  {@link OWLLibrary#getOWLIndividual(String, OWLReferences)}
	 * and call {@link #buildIndividual(OWLNamedIndividual, OWLReferences)}.
	 * 
	 * @param individualName name of the ontological individual which belongs to {@value #BUILDER_className} class 
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return the same instance of the map with the up to date Object. 
	 */
	public synchronized OFBuiltMapInvoker buildIndividual( String individualName, OWLReferences ontoRef){
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( buildIndividual( ind, ontoRef));
	}
	
	/**
	 * Build class through the interface {@link OFBuilderInterface}
	 * 
	 * @param individual which belongs to {@value #BUILDER_className} class 
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return the same instance of the map with the up to date initialization ({@link OFBuiltMapInvoker}).
	 * 
	 * To build classes for an ontological individual it goes across the 
	 * following step:
	 *  <pre> 
	 *  {@code 1 ->	gets complete "Java.pakage.class" directory to a 
	 * 		class that must implement the interface} 
	 * 		{@link OFBuilderInterface} {@code (this implements how 
	 * 		data should be organized in classes during initialization). 
	 * 		It uses} {@link OFBuilderCommon#getImplementsName(OWLNamedIndividual, OWLReferences)}.
	 * 		{@code To do so, and returns null if even it does.}
	 * 		  
	 *  {@code 2 -> uses Java reflection to instantiate the class 
	 *  	described from the 	String get in step (1). It uses:}  
	 *  	{@link ReflationInstanciater#instanciateOFBuilderByName(String)}
	 *  
	 *  {@code 3 ->	get array of string which contains key words to 
	 *  	inject in the class instantiate in step (2). It uses:}  
	 *  	{@link OFBuilderCommon#getKeyWords(OWLNamedIndividual, OWLReferences)}
	 *  
	 *  {@code 4 ->	call} {@link OFBuilderInterface#buildInfo(String[], OWLReferences, OFBuiltMapInvoker)}
	 *  
	 *  {@code 5 ->	get the name of the list which the individual wants 
	 *  	to build using} {@link OFBuilderCommon#getBuildedListName(OWLNamedIndividual, OWLReferences)} 
	 *  
	 *  {@code 6 -> 	add to the Map available in the class}  
	 *  	{@link OFBuiltMapInvoker }
	 *  	{@code a new element which has the name retrieved on step (5) 
	 *  	as a key. And the Map, returned from step (4), as value. 
	 * }</pre>
	 */
	public synchronized OFBuiltMapInvoker buildIndividual( OWLNamedIndividual individual, OWLReferences ontoRef){
		String listName = runBuilder( individual, ontoRef, staticList);
		logger.addDebugStrign( " " + listName + " : " + staticList.getMap( listName));
		logger.addDebugStrign( " -------------------------------------------- " );
		return( staticList);
	}
	
	/**
	 * as {@link OFInitialiser#buildIndividual(String, OWLReferences)} but instead of update the
	 * internal {@link OFBuiltMapInvoker} it updates the parameter staticList
	 * 
	 * @param individualName which belongs to {@value #BUILDER_className} class 
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @param staticList that will be update with the a new builded map
	 */
	public synchronized OFBuiltMapInvoker buildIndividual( String individualName, OWLReferences ontoRef, OFBuiltMapInvoker staticList){
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( buildIndividual( ind, ontoRef, staticList));
	}
	/**
	 * as {@link #buildIndividual(OWLNamedIndividual, OWLReferences)} but instead of update the
	 * internal {@link OFBuiltMapInvoker} it updates the parameter staticList
	 * 
	 * @param individual which belongs to {@value #BUILDER_className} class 
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @param staticList that will be update with the a new builded map  
	 */
	public synchronized OFBuiltMapInvoker buildIndividual( OWLNamedIndividual individual, OWLReferences ontoRef, OFBuiltMapInvoker staticList){
		String listName = runBuilder( individual, ontoRef, staticList);
		logger.addDebugStrign( " " + listName + " : " + staticList.getMap( listName));
		logger.addDebugStrign( " -------------------------------------------- " );
		return( staticList);
	}
	
	@SuppressWarnings("rawtypes")
	private synchronized String runBuilder( OWLNamedIndividual buildOntoInterface, OWLReferences ontoRef, OFBuiltMapInvoker globalList){
		// get java class name
		String className = OFBuilderCommon.getImplementsName( buildOntoInterface, ontoRef);
		String listName = null;		
		if( className != null){
			// Instantiate the interface
			OFBuilderInterface builder = ReflationInstanciater.instanciateOFBuilderByName( className);
			// get keyWords
			String[] keyWords = OFBuilderCommon.getKeyWords( buildOntoInterface, ontoRef);
			// Initialised the builder
			builder.buildInfo( keyWords, ontoRef, globalList);//staticList);
			// get the listName
			listName = OFBuilderCommon.getBuildedListName( buildOntoInterface, ontoRef);
			// get the list
			Map<?, ?> list = builder.getInitialisedObject();
			// store the list with its name to be reused later
			globalList.addTobuildedList(listName, list);
		} else logger.addDebugStrign( "EXCEPTION");
		return( listName);
	}

	/**
	 * @return initialisedList the manager of the map with the initialised class so far.
	 * 
	 * it returns empty Map if no class are initialised.
	 */
	public synchronized OFBuiltMapInvoker getInitialisedList(){
		return( staticList);
	}
}
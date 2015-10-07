package ontologyFramework.OFContextManagement;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

/**
 * This class define a complete reference to a OWL ontology. 
 * In particular, it should be create to introduce an ontology 
 * into the framework. This class is compatible in all the part of the framework
 * and it helps in moving ontologies through the computational flow.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OWLReferences{
	private OFDebugLogger logger = new OFDebugLogger( this, true);
	
	// OWL used Class
	private OWLOntologyManager manager;
	private OWLDataFactory factory;
	private OWLOntology ontology;
	private PrefixOWLOntologyFormat pm;
	private OWLReasoner reasoner;
	private OWLLibrary owlLibrary;

	// Ontology file paths
	private IRI iriFilePath;
	private IRI iriOntologyPath;
	
	// to serialise class
	private int usedCommand;
	private String filePath;
	private String ontologyPath;
	private String ontoName;
	private boolean consistent;
	private boolean bufferingReasoner;

	// static constants for command field in the constructor
	private static final String SAVING_format = ".owl";
	/**
	 * Value to describe the create ontology command during class construction. 
	 * It will create a new ontology as a file considering a given filePath. 
	 */
	static public final int CREATEcommand = 0;
	/**
	 * Value to describe the load from file command during class construction
	 * It will load an ontology w.r.t. filePath and ontoPath.
	 */
	static public final int LOADFROMFILEcommand = 1;
	/**
	 * Value to describe the load ontology from web command during class construction.
	 * In this case filePath will be set to {@code null}.
	 */
	static public final int LOADFROMWEBcommand = 2;

	/**
	 * Constructor to resume this class from its serialization
	 * variable. It can be retrieved using {@link #getSerialisableData( String)}.
	 * This is done since the reasoner and the ontology are not
	 * serializable trougth the interface {@link java.io.Serializable}
	 * 
	 * @param serial
	 */
	public OWLReferences( OWLReferencesSerializable serial){
		this.ontoName = serial.getOntoName();
		this.filePath = serial.getFilePath();
		this.ontologyPath = serial.getOntologyPath();
		this.usedCommand = serial.getUsedCommand();
		TrackedClass();
		initialiser( filePath, ontologyPath, usedCommand, true);
	}
	
	// constructor, initialise OWL classes
	/**
	 * Create a new references to an ontology using the standard reasoner.
	 * By default it is set to Pellet reasoner with a buffering synchronisation.
	 * 
	 * @param ontologyName name of this OWLReferences instances, used to refer to this instance.
	 * @param filePath IRI path to the file where the ontology is stored.
	 * @param ontologyPath IRI path of the ontology.
	 * @param command value to define the create or load from file or web comand.
	 */
	public OWLReferences( String ontologyName, String filePath, String ontologyPath, int command){
		// track this instance of this class
		this.ontoName = ontologyName;
		this.filePath = filePath;
		this.ontologyPath = ontologyPath;
		this.usedCommand = command;
		if( ontoName != null){
			if( TrackedClass())
				initialiser( filePath, ontologyPath, command, true);
		} else  initialiser( filePath, ontologyPath, command, true); 
	}
	//reasoner is not serializable
	/**
	 * Create a new references to an ontology.
	 * 
	 * @param ontologyName name of this OWLReferences instances, used to refer to this instance.
	 * @param filePath IRI path to the file where the ontology is stored.
	 * @param ontologyPath IRI path of the ontology.
	 * @param reasonerInstance instance to the reasoner to attach to this ontology.
	 * @param command value to define the create or load from file or web comand.
	 */
	public OWLReferences( String ontologyName, String filePath, String ontologyPath, OWLReasoner reasonerInstance, int command) {
		// track this instance of this class
		this.ontoName = ontologyName;	
		this.filePath = filePath;
		this.ontologyPath = ontologyPath;
		this.usedCommand = command;
		if( ontoName != null){
			if( TrackedClass()){
				initialiser( filePath, ontologyPath, command, false);
				reasoner = reasonerInstance;
			}
		} else {
			initialiser( filePath, ontologyPath, command, false);
			reasoner = reasonerInstance;
		}
	}
	// reasoner buffering = true by default	
	// called by the constructors it manage the "command" variables.
	private synchronized void initialiser( String filepath, String ontologyPath, int command, boolean usePellet) {
		// initialise path to ontology and to file
		this.owlLibrary = new OWLLibrary();
		this.iriOntologyPath = IRI.create( ontologyPath);

		switch( command){
		case 0: // then create new Ontology
			iriFilePath = IRI.create( filepath);
			manager = owlLibrary.createOntologyManager( this);
			try {
				ontology = owlLibrary.createOntology( this);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
			break;
		case 1: // then load Ontology from file
			iriFilePath = IRI.create( new File(filepath));
			manager = owlLibrary.createOntologyManager( this);
			try {
				ontology = owlLibrary.loadOntologyFromFile( this);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
			break;
		case 2: // then load Ontology from web
			iriFilePath = null;
			manager = owlLibrary.createOntologyManager( this);
			iriFilePath = IRI.create( filepath);
			break;
		default : System.out.println( " EXCEPRIONT : OWLReference command ( " + command + ") not correct"); 
		}

		factory =  owlLibrary.getOWLDataFactory( this);
		pm = owlLibrary.getPrefixFormat( this);

		if( usePellet) // Initialise  pellet as reasoner
			setPelletReasoner( false); //buffering
	}

	/**
	 * Set the reasoner attached to this ontology as Pellet.
	 * If @param buffering is true than the ontology buffers 
	 * changes that will be synchronized by the reasoner in one call to 
	 * {@code reasoner.flush();}. If it is false than the reasoner will
	 * be synchronized at every changes of the ontology structure.
	 * 
	 * @param buffering flag to set a buffering, or not buffering Pellet.
	 */
	public synchronized void setPelletReasoner( Boolean buffering) {
		reasoner = owlLibrary.getPelletReasoner( this, buffering);
		//checkConsistent();
		this.bufferingReasoner = buffering;
	}
	/**
	 * Set the reasoner attached to this ontology as Hermit.
	 * If @param buffering is true than the ontology buffers 
	 * changes that will be synchronized by the reasoner in one call to 
	 * {@code reasoner.flush();}. If it is false than the reasoner will
	 * be synchronized at every changes of the ontology structure.
	 * 
	 * @param buffering reasoner buffering flag
	 */
	public synchronized void setHermitReasoner( Boolean buffering) {
		reasoner = owlLibrary.getHermitReasoner( this, buffering);
		checkConsistent();
		this.bufferingReasoner = buffering;
	}
	/**
	 * Set the reasoner attached to this ontology as Snorocket.
	 * If @param buffering is true than the ontology buffers 
	 * changes that will be synchronized by the reasoner in one call to 
	 * {@code reasoner.flush();}. If it is false than the reasoner will
	 * be synchronized at every changes of the ontology structure.
	 * 
	 * @param buffering reasoner buffering flag
	 */
	public synchronized void setSnorocketReasoner( Boolean buffering) {
		reasoner = owlLibrary.getSnorocketReasoner( this, true);
		checkConsistent();
		this.bufferingReasoner = buffering;
	}
	/**
	 * Set the reasoner attached to this ontology as Fact++.
	 * If @param buffering is true than the ontology buffers 
	 * changes that will be synchronized by the reasoner in one call to 
	 * {@code reasoner.flush();}. If it is false than the reasoner will
	 * be synchronized at every changes of the ontology structure.
	 * 
	 * @param buffering reasoner buffering flag
	 */
	public synchronized void setFactReasoner( Boolean buffering) {
		reasoner = owlLibrary.getFactReasoner( this, buffering);
		checkConsistent();
		this.bufferingReasoner = buffering;
	}
	
	/**
	 * Returns true if the reasoner has a buffering synchronisation.
	 * false if the reasoner is apdated at every ontological changes.
	 * 
	 * @return the bufferingReasoner flag
	 */
	public boolean isBufferingReasoner() {
		return bufferingReasoner;
	}

	/**
	 * Every OWLReferences has only one ontology attached to it.
	 * Moreover, every of them has a name used to statically refer to the different
	 * OWLReferences inside the framework. This name must be different for
	 * every OWLReferences since they are stored in an {@code HashMap<String, OWLReferences>}. 
	 * 
	 * @return the name of the OWLReferences.
	 */
	public  String getOntoName() {
		return ontoName;
	}


	/**
	 * Returns the IRI path to the file where the ontology is.
	 * 
	 * @return the iriFilePath
	 */
	public  IRI getIriFilePath() {
		return iriFilePath;
	}

	/**
	 * Returns the IRI path to the file where the ontology is, as a String.
	 * 
	 * @return the iriFilePath as a String
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Set the file Path of the ontology refereed by this instance. 
	 * As long as is not the case in which an ontology should be
	 * locally saved from web is recommended to set this variable
	 * in during class constructors. 
	 * 
	 * @param filePath the filePath to set.
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		iriFilePath = IRI.create( filePath);
	}

	/**
	 * Returns the command used to initialize this instance.
	 * 
	 * @return the used command.
	 */
	public int getUsedCommand() {
		return usedCommand;
	}

	/**
	 * Returns the IRI path associate to the ontology
	 * refereed by this class as a String.
	 * 
	 * @return the IRI ontology Path as a String 
	 */
	public String getOntologyPath() {
		return ontologyPath;
	}


	/**
	 * Returns the IRI path associate to the ontology
	 * refereed by this class.
	 * 
	 * @return the IRI ontology Path
	 */
	public  IRI getIriOntologyPath() {
		return iriOntologyPath;
	}

	/**
	 * Returns the OWL manager associate only to the ontology 
	 * refereed by this instance. 
	 * 
	 * @return the OWL manager
	 */
	public OWLOntologyManager getManager() {
		return manager;
	}


	/**
	 * Returns the OWL data factory, used to get object used
	 * for ontological changes.
	 * 
	 * @return the factory
	 */
	public OWLDataFactory getFactory() {
		return factory;
	}

	/**
	 * Set an external reasoner instance to this ontology.
	 * The old relation to a reasoner will be deleted. 
	 * 
	 * @param reasoner the reasoner to set
	 */
	public void setReasoner(OWLReasoner reasoner) {
		this.reasoner = reasoner;
		checkConsistent();
	}

	/**
	 * Get the ontology refereed by this class.
	 * 
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * Returns a prefix manager to semply the IRI representation.
	 * 
	 * @return the prefix manager
	 */
	public PrefixOWLOntologyFormat getPm() {
		return pm;
	}

	/**
	 * @return the reasoner instance associate to this ontology
	 */
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	/**
	 * Serialize this class saving important quantities and using
	 * a special constructor: {@link #OWLReferences(OWLReferencesSerializable)}.
	 * Basically, it calls {@link #getSerialisableData(String)} 
	 * with input parameter {@code filePath = this.getIriFilePath()}.
	 * 
	 * @return serializable Data relate to this OWLReferences
	 */
	public synchronized OWLReferencesSerializable getSerialisableData(){
		return( new OWLReferencesSerializable( ontoName, filePath, ontologyPath, usedCommand));
	}
	/**
	 * Serialize this class saving important quantities and using
	 * a special constructor: {@link #OWLReferences(OWLReferencesSerializable)}.
	 * 
	 * @param filePath new file Path for the serializated class
	 * @return serializable Data relate to this OWLReferences
	 */
	public synchronized OWLReferencesSerializable getSerialisableData( String filePath){
		return( new OWLReferencesSerializable( ontoName, filePath, ontologyPath, usedCommand));
	}
	
	// static class tracker. It collects all the instances of this class in an HashMap.
	private static Map<String, OWLReferences> allInstances;  
	static  {  
		allInstances = new HashMap<String, OWLReferences>();  
	}    
	protected synchronized void finalize()  {  
		allInstances.values().remove( this);  
	}
	private synchronized boolean TrackedClass()  { 
		//if( ! isInAllInstances( ontoName)){
			allInstances.put( ontoName, this);
			return( true);
		//}
		//System.out.println( "Exception : cannot create another Ontology with name : " + ontoName);
		//return( false);
	}
	/**
	 * Returns a map that contains all the instances of OWLReferences class
	 * create into the framework. Instances are organized w.r.t the ontoName
	 * attached to them   
	 * 
	 * @return Map between ontoName and OWKReferences
	 */
	public static  HashMap<String, OWLReferences> getAllInstances(){
		return( ( HashMap<String, OWLReferences>) allInstances );  
	}
	/**
	 * Return a particular OWLReferences, given its ontoName. Basically it just 
	 * calls: {@code return( this.getAllInstances().get(referenceName))}.
	 * 
	 * @param referenceName the name attached to a particolar OWLReferences (ontoName).
	 * @return the instance of this class attached to a particular name
	 */
	public static OWLReferences getOWLReferences( String referenceName){
		return( allInstances.get( referenceName));
	}
	/**
	 * check if exist an OWLReferences with a particolar name already stored in the
	 * Map ({@link #getAllInstances()}). Basically it just calls:
	 * {@code return( this.getAllInstance.containsKey( key))}
	 * 
	 * @param key ontoName used to store a OWLReferences.
	 * @return true if it exist, false otherwise.
	 */
	public static boolean isInAllInstances( String key){
		return( allInstances.containsKey( key));
	}
	/**
	 * @return the consistency flag
	 */
	protected boolean isConsistent() {
		return consistent;
	}
	/**
	 * call the reasoner to check ontology consistency and synchronizes the consistency flag
	 */
	public void checkConsistent() {
		consistent = reasoner.isConsistent();
	}
	
	// if nameToSerialize = null --> save all
	/**
	 * It goes trough all the named instances of this class and for each of them
	 * calls {@link #getSerialisableData(String)}. Note that to properly serialize
	 * an OWLOntology that has to be saved in owl format.
	 * 
	 * @param basePath folder path in which save the ontologies
	 * @param nameToSerialize name of the ontologies to save. If this is null then all keys of {@link #getAllInstances()} will be considered.
	 * @param exportInfer  if true all the asserted axiom will be exported in the Ontology 
	 * @return the map of serializable data.
	 */
	public static Map< String, OWLReferencesSerializable> getAllSerializableInstances( String basePath, Set< String> nameToSerialize, boolean exportInfer) {
		OFDebugLogger serializeLog = new OFDebugLogger( OWLReferences.class, true);//DebuggingClassFlagData.getFlag( OFInitialiser.SERIALIZATORDEBUG_individualName));
		
		Map< String, OWLReferencesSerializable> serial = new  HashMap< String, OWLReferencesSerializable>();
		boolean doit;
		for( String s : allInstances.keySet()){
			if( nameToSerialize == null)
				doit = true;
			else if( nameToSerialize .contains( s))
				 doit = true;
			else doit = false;
				
			if( doit){		
				OWLReferences ontoRef = allInstances.get( s);
				if( basePath == null){
					serial.put(s, ontoRef.getSerialisableData());
					OWLLibrary.saveOntology( exportInfer, ontoRef);
					serializeLog.addDebugStrign( " Saving ontology " + ontoRef.getOntoName() + " (overrwriting). With exporting reasoner assertions?" + exportInfer);
				}else{
					String path = basePath + s + SAVING_format;
					serial.put(s, ontoRef.getSerialisableData( path));
					OWLLibrary.saveOntology( exportInfer, path, ontoRef);
					serializeLog.addDebugStrign( " Saving ontology " + ontoRef.getOntoName() + " ( on path :" + path + "). With exporting reasoner assertions?" + exportInfer);
				}
			}
		}
		return( serial);
	}
	
	// ##################################   to OWLLIBRARY !!!!!!!!!!!!!
	// mutex
	private Lock mutexReasoner = new ReentrantLock();
	private Lock mutexIndividualB2Class = new ReentrantLock();
	private Lock mutexDataPropB2Ind = new ReentrantLock();
	private Lock mutexObjPropB2Ind = new ReentrantLock();
	private Lock mutexSubClass = new ReentrantLock();
	private Lock mutexSuperClass = new ReentrantLock();
	private Lock mutexSetSubClass = new ReentrantLock();
	private Lock mutexAxiom = new ReentrantLock();
	private Lock mutexRemoveAxiom = new ReentrantLock();
	private Lock mutexApplyChanges = new ReentrantLock();
	private Lock mutexAddObjPropB2Ind = new ReentrantLock();
	private Lock mutexAddDataPropB2Ind = new ReentrantLock();
	private Lock mutexAddIndB2Class = new ReentrantLock();
	private Lock mutexRemoveObjPropB2Ind = new ReentrantLock();
	private Lock mutexRemoveDataPropB2Ind = new ReentrantLock();
	private Lock mutexRemoveIndB2Class = new ReentrantLock();
	private Lock mutexRemoveInd = new ReentrantLock();
	private Lock mutexReplaceDataProp = new ReentrantLock();
	
	// methods
	public PrefixOWLOntologyFormat getPrefixFormat(){
		return owlLibrary.getPrefixFormat( this);
	}
	public OWLDataFactory getOWLDataFactory(){
		return owlLibrary.getOWLDataFactory( this);
	}
	public void printOntonolyOnConsole(){
		try {
			owlLibrary.printOntonolyOnConsole( this);
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}
	
	
	public void synchroniseReasoner(){
		long t = System.nanoTime(); 
		mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.synchroniseReasoner( this);
			loggLockTime( t, t1);
		} finally{
			mutexReasoner.unlock();
		}
	}
	
	
	public OWLClass getOWLClass( String className){
		long t = System.nanoTime(); 
		//mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			OWLClass out = owlLibrary.getOWLClass(className, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			//mutexReasoner.unlock();
		}
	}
	public OWLNamedIndividual getOWLIndividual( String individualName){
		long t = System.nanoTime(); 
		//mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			OWLNamedIndividual out = owlLibrary.getOWLIndividual(individualName, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			//mutexReasoner.unlock();
		}
	}
	public OWLDataProperty getOWLDataProperty(String dataPropertyName){
		long t = System.nanoTime(); 
		//mutexReasoner.lock();
		try{
			long t2 = System.nanoTime();
			OWLDataProperty out = owlLibrary.getOWLDataProperty(dataPropertyName, this);
			loggLockTime( t, t2);
			return out;
		} finally{
			//mutexReasoner.unlock();
		}
	}
	public OWLObjectProperty getOWLObjectProperty( String objPropertyName){ 
		long t = System.nanoTime();
		//mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			OWLObjectProperty out = owlLibrary.getOWLObjectProperty(objPropertyName, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			//mutexReasoner.unlock();
		}
	}
	public OWLLiteral getOWLLiteral( Object value){ 
		long t = System.nanoTime();
		//mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			OWLLiteral out = owlLibrary.getOWLLiteral(value, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			//mutexReasoner.unlock();
		}
	}
	public OWLLiteral getOWLLiteral( Object value, OWLDatatype type){ 
		long t = System.nanoTime();
		//mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			OWLLiteral out = owlLibrary.getOWLLiteral( value, type, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			//mutexReasoner.unlock();
		}
	}
	
		
	public Set<OWLNamedIndividual> getIndividualB2Class( String className){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexIndividualB2Class.lock();
		try{
			Set<OWLNamedIndividual> out = owlLibrary.getIndividualB2Class( className, this);
			long t1 = System.nanoTime();
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexIndividualB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	public Set<OWLNamedIndividual> getIndividualB2Class( OWLClass ontoClass){ 
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexIndividualB2Class.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLNamedIndividual> out = owlLibrary.getIndividualB2Class(ontoClass, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexIndividualB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLNamedIndividual getOnlyIndividualB2Class( String className){ 
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexIndividualB2Class.lock();
		try{
			long t1 = System.nanoTime();
			OWLNamedIndividual out = owlLibrary.getOnlyIndividualB2Class( className, this);
			loggLockTime( t, t1);
			return  out;
		} finally{
			mutexIndividualB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLNamedIndividual getOnlyIndividualB2Class( OWLClass ontoClass){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexIndividualB2Class.lock();
		try{
			long t1 = System.nanoTime();
			OWLNamedIndividual out = owlLibrary.getOnlyIndividualB2Class(ontoClass, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexIndividualB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public Set< OWLClass> getIndividualClasses( OWLNamedIndividual individual){
		long t = System.nanoTime();
		mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLClass> out = owlLibrary.getIndividualClasses( individual, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexReasoner.unlock();
		}
	}
	
	
	public Set<OWLLiteral> getDataPropertyB2Individual( String individualName, String propertyName){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLLiteral> out = owlLibrary.getDataPropertyB2Individual( individualName, propertyName, this);
			loggLockTime( t, t1);
			return out; 
		} finally{
			mutexDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public Set<OWLLiteral> getDataPropertyB2Individual( OWLNamedIndividual individual, OWLDataProperty property){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLLiteral> out = owlLibrary.getDataPropertyB2Individual( individual, property, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLLiteral getOnlyDataPropertyB2Individual( String individualName, String propertyName){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			OWLLiteral out = owlLibrary.getOnlyDataPropertyB2Individual( individualName, propertyName, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLLiteral getOnlyDataPropertyB2Individual( OWLNamedIndividual individual, OWLDataProperty property){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			OWLLiteral out = owlLibrary.getOnlyDataPropertyB2Individual( individual, property, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public Set<OWLNamedIndividual> getObjectPropertyB2Individual( String individualName, String propertyName){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLNamedIndividual> out = owlLibrary.getObjectPropertyB2Individual( individualName, propertyName, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public Set<OWLNamedIndividual> getObjectPropertyB2Individual( OWLNamedIndividual individual, OWLObjectProperty property){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLNamedIndividual> out = owlLibrary.getObjectPropertyB2Individual( individual, property, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLNamedIndividual getOnlyObjectPropertyB2Individual( String individualName, String propertyName){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			OWLNamedIndividual out = owlLibrary.getOnlyObjectPropertyB2Individual( individualName, propertyName, this);
			loggLockTime( t, t1);
			return out;
		} finally{
				mutexObjPropB2Ind.unlock();
				mutexReasoner.unlock();
		}
	}
	public OWLNamedIndividual getOnlyObjectPropertyB2Individual( OWLNamedIndividual individual, OWLObjectProperty property){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			OWLNamedIndividual out = owlLibrary.getOnlyObjectPropertyB2Individual( individual, property, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
		
		
	public Set<OWLClass> getSubClassOf( String className){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSubClass.lock();
		try{
			long t2 = System.nanoTime();
			Set<OWLClass> out = owlLibrary.getSubClassOf( className, this);
			loggLockTime( t, t2);
			return out;
		} finally{
			mutexSubClass.unlock();
			mutexReasoner.unlock();
		}
	}
	public Set<OWLClass> getSubClassOf( OWLClass cl){
		long t = System.nanoTime(); 
		mutexReasoner.lock();
		mutexSubClass.lock();
		try{
			long t1 = System.nanoTime(); 
			Set<OWLClass> out = owlLibrary.getSubClassOf( cl, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSubClass.unlock();
			mutexReasoner.unlock();
		}	
	}
		
		
	public Set<OWLClass> getSuperClassOf( String className){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSuperClass.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLClass> out = owlLibrary.getSuperClassOf( className, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSuperClass.unlock();
			mutexReasoner.unlock();
		}	
	}
	public Set<OWLClass> getSuperClassOf( OWLClass cl){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSuperClass.lock();
		try{
			long t1 = System.nanoTime();
			Set<OWLClass> out = owlLibrary.getSuperClassOf( cl, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSuperClass.unlock();
			mutexReasoner.unlock();
		}	
	}
	
	
	public OWLAxiom setSubClassOf( String superClassName, String subClassName){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSetSubClass.lock();
		try{
			long t1 = System.nanoTime();
			OWLAxiom out = owlLibrary.setSubClassOf( superClassName, subClassName, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSetSubClass.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLAxiom setSubClassOf( String superClassName, String subClassName, boolean addAxiom){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSetSubClass.lock();
		try{
			long t1 = System.nanoTime();
			OWLAxiom out = owlLibrary.setSubClassOf( superClassName, subClassName, addAxiom, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSetSubClass.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLAxiom setSubClassOf( String superClassName, String subClassName, boolean addAxiom, boolean applyChanges){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSetSubClass.lock();
		try{
			long t1 = System.nanoTime();
			OWLAxiom out = owlLibrary.setSubClassOf( superClassName, subClassName, addAxiom, applyChanges, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSetSubClass.unlock();
			mutexReasoner.unlock();
		}
		
	}
	public OWLAxiom setSubClassOf( OWLClass superClass, OWLClass subClass){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSetSubClass.lock();
		try{
			long t1 = System.nanoTime();
			OWLAxiom out = owlLibrary.setSubClassOf( superClass, subClass, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSetSubClass.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLAxiom setSubClassOf( OWLClass superClass, OWLClass subClass, boolean addAxiom){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSetSubClass.lock();
		try{
			long t1 = System.nanoTime();
			OWLAxiom out = owlLibrary.setSubClassOf( superClass, subClass, addAxiom, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSetSubClass.unlock();
			mutexReasoner.unlock();
		}
	}
	public OWLAxiom setSubClassOf( OWLClass superClass, OWLClass subClass, boolean addAxiom, boolean applyChanges){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexSetSubClass.lock();
		try{
			long t1 = System.nanoTime();
			OWLAxiom out = owlLibrary.setSubClassOf(superClass, subClass, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexSetSubClass.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> getAddAxiom( OWLAxiom axiom){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAxiom.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.getAddAxiom(axiom, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAxiom.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> getAddAxiom( OWLAxiom axiom, boolean addToChangeList){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAxiom.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.getAddAxiom( axiom, addToChangeList, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAxiom.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> getRemoveAxiom( OWLAxiom axiom){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveAxiom.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.getRemoveAxiom( axiom, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveAxiom.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> getRemoveAxiom( OWLAxiom axiom, boolean addToChangeList){
		long t = System.nanoTime(); 
		mutexReasoner.lock();
		mutexRemoveAxiom.lock();
		try{
			long t1 = System.nanoTime(); 
			List<OWLOntologyChange> out = owlLibrary.getRemoveAxiom( axiom, addToChangeList, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveAxiom.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public void applyChanges(){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexApplyChanges.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.applyChanges( this);
			loggLockTime( t, t1);
		}finally{
			mutexApplyChanges.unlock();
			mutexReasoner.unlock();
		}
	}
	public void applyChanges( OWLOntologyChange addAxiom){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexApplyChanges.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.applyChanges(addAxiom, this);
			loggLockTime( t, t1);
		} finally{
			mutexApplyChanges.unlock();
			mutexReasoner.unlock();
		}
	}
	public void applyChanges( List<OWLOntologyChange> addAxiom){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexApplyChanges.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.applyChanges(addAxiom, this);
			loggLockTime( t, t1);
		} finally{
			mutexApplyChanges.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	/*public Set<OWLObjectPropertyExpression> getSubObjectProperty( String objectPropName){
		return owlLibrary.getSubObjectProperty( objectPropName, this);
	}
	public Set< OWLObjectPropertyExpression> getSubObjectProperty( OWLObjectProperty objectProp){
		return owlLibrary.getSubObjectProperty( objectProp, this);
	}*/
	/*public Object getOnlyElement( Set< ?> set){
		return owlLibrary.getOnlyElement(set);
	}*/
	public String getOnlyString( Set< OWLLiteral> set){
		return OWLLibrary.getOnlyString(set);
	}
	/*public List<OWLOntologyChange> renameEntity( OWLEntity entity, IRI newIRI){
		return owlLibrary.renameEntity( entity, newIRI, this);
	}
	public List<OWLOntologyChange> renameEntity( OWLEntity entity, IRI newIRI, boolean applyChanges){
		return owlLibrary.renameEntity( entity, newIRI, applyChanges, this);
	}*/
	public String getOWLObjectName( OWLObject o){
		return OWLLibrary.getOWLObjectName( o);
	}
	/*public Set< String> getOWLSetAsString( Set< OWLObject> set){
		return owlLibrary.getOWLSetAsString( set);
	}
	public Set< String> getOWLLiteralAsString( Set<OWLLiteral> set){
		return owlLibrary.getOWLLiteralAsString( set);
	}*/
	
	
	public List<OWLOntologyChange> addObjectPropertyB2Individual( OWLNamedIndividual ind, OWLObjectProperty prop,  
			OWLNamedIndividual value, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAddObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.addObjectPropertyB2Individual( ind, prop, 	value, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAddObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> addObjectPropertyB2Individual( String individualName, String propName,  
			String valueName, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAddObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.addObjectPropertyB2Individual( individualName, propName, valueName, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAddObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> addDataPropertyB2Individual(OWLNamedIndividual ind, 
			OWLDataProperty prop, OWLLiteral value, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAddDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.addDataPropertyB2Individual(ind, prop, value, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAddDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> addDataPropertyB2Individual( String individualName, 
			String propertyName, Object value, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAddDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.addDataPropertyB2Individual( individualName, propertyName, value, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAddDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> addIndividualB2Class(OWLNamedIndividual ind, OWLClass c, 
			boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAddIndB2Class.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.addIndividualB2Class(ind, c, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAddIndB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> addIndividualB2Class(String individualName, String className, 
			boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexAddIndB2Class.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.addIndividualB2Class(individualName, className,bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexAddIndB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> removeObjectPropertyB2Individual( OWLNamedIndividual ind, OWLObjectProperty prop,   
			OWLNamedIndividual value, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeObjectPropertyB2Individual( ind, prop,value, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> removeObjectPropertyB2Individual( String individualName, String propName,   
			String valueName, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveObjPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeObjectPropertyB2Individual( individualName, propName,valueName, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveObjPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> removeDataPropertyB2Individual(OWLNamedIndividual ind, 
			OWLDataProperty prop, OWLLiteral value, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeDataPropertyB2Individual( ind, prop, value, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> removeDataPropertyB2Individual( String individualName, 
			String propertyName, Object value, boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveDataPropB2Ind.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeDataPropertyB2Individual( individualName, propertyName, value, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveDataPropB2Ind.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> removeIndividualB2Class(OWLNamedIndividual ind, OWLClass c, 
			boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveIndB2Class.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeIndividualB2Class(ind, c,bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveIndB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> removeIndividualB2Class(String individualName, String className, 
			boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveIndB2Class.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeIndividualB2Class( individualName, className, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveIndB2Class.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public List<OWLOntologyChange> removeIndividual( OWLNamedIndividual individual, Boolean bufferize){ 
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveInd.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeIndividual(individual, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveInd.unlock();
			mutexReasoner.unlock();
		}
	}
	public List<OWLOntologyChange> removeIndividual( Set< OWLNamedIndividual> individuals, Boolean bufferize){ 
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexRemoveInd.lock();
		try{
			long t1 = System.nanoTime();
			List<OWLOntologyChange> out = owlLibrary.removeIndividual(individuals, bufferize, this);
			loggLockTime( t, t1);
			return out;
		} finally{
			mutexRemoveInd.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public void replaceDataProperty( OWLNamedIndividual ind,  
				OWLDataProperty prop, Set< OWLLiteral> oldValue, OWLLiteral newValue, Boolean bufferize){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexReplaceDataProp.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.replaceDataProperty( ind, prop, oldValue, newValue, bufferize, this);
			loggLockTime( t, t1);
		} finally{
			mutexReplaceDataProp.unlock();
			mutexReasoner.unlock();
		}
	}
	public void replaceDataProperty( OWLNamedIndividual ind,  
			OWLDataProperty prop, OWLLiteral oldValue, OWLLiteral newValue, Boolean buffered){
		long t = System.nanoTime();
		mutexReasoner.lock();
		mutexReplaceDataProp.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.replaceDataProperty( ind, prop, oldValue, newValue, buffered, this);
			loggLockTime( t, t1);
		} finally{
			mutexReplaceDataProp.unlock();
			mutexReasoner.unlock();
		}
	}
	
	
	public void replaceObjectProperty( OWLNamedIndividual ind,  
			OWLObjectProperty prop, OWLNamedIndividual oldValue, OWLNamedIndividual newValue, Boolean buffered){
		long t = System.nanoTime();
		mutexReasoner.lock();
		try{
			long t1 = System.nanoTime();
			owlLibrary.replaceObjectProperty(ind, prop, oldValue, newValue, buffered, this);
			loggLockTime( t, t1);
		} finally{
			mutexReasoner.unlock();
		}
	}
	
	
	public void replaceIndividualClass( OWLNamedIndividual ind,  
			OWLClass oldValue, OWLClass newValue, Boolean buffered){
		mutexReasoner.lock();
		long t = System.nanoTime();
		try{
			long t1 = System.nanoTime();
			owlLibrary.replaceIndividualClass(ind, oldValue, newValue, buffered, this);
			loggLockTime( t, t1);
		} finally{
			mutexReasoner.unlock();
		}
	}

	
	public synchronized OWLReferences reloadOnrology(){
		synchronized( this.getReasoner()){
			this.getReasoner().dispose();
			return new OWLReferences(
					this.getOntoName(), 
					this.getFilePath(), 
					this.getOntologyPath(),
					OWLReferences.LOADFROMFILEcommand);
		}
	}
	
	
	
	
	private void loggLockTime( long initialTime, long unlockingTime){
		Double time = (unlockingTime - initialTime) / 1000000D;
		Double time2 = (System.nanoTime() - unlockingTime) / 1000000D;
		if( time > 400 || time2 > 400){
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			String method = "", caller = "", caller2 = "";
			if( trace.length > 3)
				method = trace[2].getMethodName();
			if( trace.length > 4)
				caller = trace[3].getMethodName();
			if( trace.length > 5)
				caller2 = trace[4].getMethodName();
			logger.addDebugStrign( this.getOntoName() + " locked on " + method + " for " + time + " [ms] (called by " + caller +" <- " + caller2 + ")");
			logger.addDebugStrign( this.getOntoName() + " spent " + time2 + "[ms] ont OWLLibrary");
		}
	}
	
	public void setOWLVerbose( Boolean flag){
		logger.setFlagToFollow( flag);
		owlLibrary.setOWLVerbose( flag);
	}
}
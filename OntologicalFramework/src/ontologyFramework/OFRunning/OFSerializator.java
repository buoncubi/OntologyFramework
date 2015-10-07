package ontologyFramework.OFRunning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFContextManagement.OWLReferencesSerializable;
import ontologyFramework.OFContextManagement.synchronisingManager.OFSerializeSynchroniserData;
import ontologyFramework.OFContextManagement.synchronisingManager.OFSynchroniserData;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This static class collects common methods to serialize and de-serialize the framework.
 * Exception and errors are handled by {@link OFDebugLogger}
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */

// TODO add logger
public class OFSerializator {

	private static OFDebugLogger logger = new OFDebugLogger( OFSerializator.class, true);//DebuggingClassFlagData.getFlag( OFInitialiser.SERIALIZATORDEBUG_individualName));
	
	/**
	 * the format of the file automatically added to directory/name".{@value #SERIALIZATION_fileExtension}" 
	 */
	public static final String SERIALIZATION_fileExtension = ".ser";
	/**
	 * The name of the ontological individual which represent the scheduler that will be initialize
	 * after de-serializaition.
	 */
	public static final String SCHEDULER_individualName = "B_SchedulerBuilder";
	
	/**
	 * The name of the ontological individual which represent the algorithms that will be initialize
	 * after de-serializaition.
	 */
	public static final String PROCEDURE_individualName = "B_OFProcedureBuilder";
	
	/**
	 *  Static class with all static method. Constructor non instatiable 
	 */
	private OFSerializator() {
        throw new AssertionError();
    }
	
	
	/**
	 * It calls {@link #saveFrameworkState(Set, Set, String, boolean)} with all parameter equal to "null".
	 * 
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	 * @return OFSystemStates set of classes which represents the state of the OntologicalFramework
	 */
	public static synchronized Set<OFSystemState> saveFrameworkState( boolean exportInferd){
		return( saveFrameworkState( null, null, null, exportInferd));
	}
	/**
	 * It calls {@link #saveFrameworkState(Set, Set, String, boolean)} with the first two 
	 * parameter equal to "null" and the third equal to ontoFilePath.
	 * 
	 * @param ontoFilePath the folder directory in which you want to store the ontologies
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	 * @return OFSystemStates set of classes which represents the state of the OntologicalFramework
	 */
	public static synchronized Set<OFSystemState> saveFrameworkState( String ontoFilePath, boolean exportInferd){
		return( saveFrameworkState( null, null, ontoFilePath, exportInferd));
	}
	/**
	 * It goes across all the instances of {@link OFBuiltMapInvoker} and, for each of them
	 * Instantiates a new {@link OFSystemState}. Those are collected in a Set and
	 * given as output. All the parameter of this function are passed to constructor:
	 * {@link OFSystemState#OFSystemState(OFBuiltMapInvoker, String, Set, Set, boolean)}
	 *
	 * 
	 * <p>
	 * @param ontoToSerializeName list of {@link OWLReferences} instance names that we want to serialize. If it is equal to "null" than all the instances are serialized
	 * @param listToSerializeName list of Names of the Individual linked to the builder by Object Property {@value ontologyFramework.OFRunning.OFInitialising.OFBuilderCommon#BUILDLISTNAME_objProp}
	 * @param ontoFilePath the folder directory in which you want to store the ontologies
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	 * @return OFSystemStates set of classes which represents the state of the OntologicalFramework
	 */
	public static synchronized Set<OFSystemState> saveFrameworkState( Set<String> ontoToSerializeName, Set<String> listToSerializeName, String ontoFilePath, boolean exportInferd){
		// get all the initialised map
		HashMap<String, OFBuiltMapInvoker> builded = OFBuiltMapInvoker.getAllInstances();
		Set< OFSystemState> states = new HashSet< OFSystemState>();
		for( String key : builded.keySet()){
			logger.addDebugStrign( " stroring new System State for OFBuildedListInvoker " + key + " list " + builded ); 
			OFSystemState tmp = new OFSystemState( builded.get( key), key, ontoToSerializeName, listToSerializeName, ontoFilePath, exportInferd); 
			states.add( tmp);
		}
		logger.addDebugStrign( " ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] got:SystState [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[");
		return( states);
	}
	
	/**
	 * It calls {@link #serializeObjectToFile(String, String, Set)} where the first two 
	 * parameters are "null" and the third is: toSerialize 
	 * 
	 * @param toSerialize set of classes which represents the state of the OntologicalFramework
	 * @return serializationPaths the set of paths in which objects has been serialized in a .{@value #SERIALIZATION_fileExtension} file.
	 */
	public static synchronized Set<String> serializeObjectToFile( Set<OFSystemState> toSerialize){
		return( serializeObjectToFile( null, null, toSerialize));
	}
	/**
	 * It iterate over all the value of toSerialize. For each of them it retrieves the 
	 * serializable Map ( of type {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}) and writes it in a file.
	 * <p>
	 * If filePath is "null" than the base files path will be:
	 * {@code oFBuildedListInvoker_SerialMap.}{@link OFSystemState#getOntologyFilePath()}.
	 * Otherwise filePath it must be an absolute map to a folder, in which
	 * serialize the framework Java classes
	 * <p>
	 * If fileName is "null" than the name of a serialized Java Class
	 * will be {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker#getInstanceName()}.
	 * Otherwise it will be {@code fileName + ( count++).toString()}.
	 * <p>
	 * In any case the complete path to a file will be:
	 * {@code filePath + FileName + }{@value #SERIALIZATION_fileExtension}} 
	 * 
	 * @param filePath folder path in which save the serialized Classes 
	 * @param fileName base name of the serialized Classes belong to the folder linked by filePath
	 * @param toSerialize set of classes which represents the state of the OntologicalFramework
	 * @return serializationPaths the set of paths in which objects has been serialized in a .{@value #SERIALIZATION_fileExtension} file.
	 */
	public static synchronized Set<String> serializeObjectToFile( String filePath, String fileName, Set<OFSystemState> toSerialize){
		Set< String> paths = new HashSet< String>();
		int count = 0;
		for( OFSystemState j : toSerialize){
			try{
				OFBuiltMapInvoker i = j.getSerialMap();
				// Serialize data object to a file
				String tmp;
				if( filePath == null)
					filePath = j.getOntologyFilePath();
				if( fileName == null)
					tmp = filePath + i.getInstanceName() + SERIALIZATION_fileExtension;
				else tmp = filePath + fileName + (count++) + SERIALIZATION_fileExtension;
				paths.add( tmp);
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream( tmp));
				out.writeObject( i);
				out.close();
				logger.addDebugStrign( " ... serializated " + i.getMap().keySet() + " on path : " + tmp);
			} catch (IOException e) {
				logger.addDebugStrign( " IOException in method serializeObjectToFile( filePath=" + 
						filePath + ", fileName=" + fileName + ", toSerialize=" + toSerialize + 
						" java stackTrace: " + e.getStackTrace().toString(), true);
			}
		}
		logger.addDebugStrign( " ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] serialized:SystState [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[");
		return( paths);  // path of the file.ser
	}
	
	
	/**
	 * It load the frame status from files and re-instantiate it. 
	 * It load all the files and retreive the related Map than, for each of it makes this steps:
	 * <pre>
	 * 		{@code 1 -> get all} {@link OWLReferencesSerializable} 
	 * 			{@code load all the ontologies calling new} {@link OWLReferences}.
	 * 			{@code eliminate them from the Map}
	 *  	{@code 2 -> for all the classes which are not Serializable.}
	 *  		{@code Get serializable objects and re-instantiate them.}
	 *  		{@code substitute those class between each other in the Map}
	 *   	{@code 3 -> re-build the scheduler since is not Serializable}
	 *  		{@code substitute those class between each other in the Map}
	 *  	{@code 4 -> re-build Debugging Map}
	 *  		{@code eliminate them from the Map}
	 *  	{@code 5 -> re-build static property of Map and return it}
	 * </pre>
	 * 
	 * @param filePaths the set of paths in which objects has been serialized in a .{@value #SERIALIZATION_fileExtension} file.
	 * @return loadedList the set of list builded and stored during serialization.
	 */
	public static synchronized Set< OFBuiltMapInvoker> deserializeOFBuildedListInvoker( Set< String> filePaths, Boolean rebuildScheduler){
		Set< OFBuiltMapInvoker> ret = new HashSet< OFBuiltMapInvoker>();
		for( String s : filePaths){
			logger.addDebugStrign( " deserialization from file on : " + s);
			FileInputStream fis = null;
			ObjectInputStream in = null;
			OFBuiltMapInvoker map = null;
			try {
				fis = new FileInputStream( s);
			    in = new ObjectInputStream(fis);
			    map = ( OFBuiltMapInvoker) in.readObject();
			    in.close();
			} catch (Exception e) {
				System.out.println( " IOException in method deserializeOFBuildedListInvoker( filePaths=" + 
						filePaths + ", rebuildScheduler=" + rebuildScheduler + 
						" java stackTrace: " );//, true);
				e.printStackTrace();
			}
			ret.add( remapInvoker( map, rebuildScheduler));
		}
		logger.addDebugStrign( " ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] loaded:SystState [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[");
		return( ret);
	}
	
	private static synchronized OFBuiltMapInvoker remapInvoker( OFBuiltMapInvoker serialisedMap, Boolean rebuildScheduler){
		Map<String, Map<String, Object>> map = serialisedMap.getMap();
		
		// relead otologies
		Map<String, Object> ref = map.get( OFSystemState.OWLREFERENCES_keyWord);
		for( String s : ref.keySet()){
			OWLReferencesSerializable obj = ( OWLReferencesSerializable) ref.get( s);
			new OWLReferences( obj);
			logger.addDebugStrign( " rebulding OWLreferences from : " + obj.getFilePath() + "  ||  " + obj.getOntologyPath() );
		}
		serialisedMap.removeTobuildedList( OFSystemState.OWLREFERENCES_keyWord);
		
		// reload builded classes
		Set< String> serializable = OFSystemState.getSerializableListName();	
		for( String uns : map.keySet()){
			if( ! serializable.contains( uns)){
				Map<String, Object> rebuilded = new HashMap< String, Object>();
				Map<String, Object> builded = map.get( uns);
				if( uns.equals( OFSystemState.SYNCRHONISERLIST_keyWord)){
					for( String s : builded.keySet()){
						OFSerializeSynchroniserData serial = ( OFSerializeSynchroniserData) builded.get( s);
						rebuilded.put( s, new OFSynchroniserData( serial));
					}
					// toRemove.add( uns); overwrite, since has the same key
					serialisedMap.addTobuildedList( uns, rebuilded, true);
				}
				logger.addDebugStrign( " rebuilding unserializable data : key = " + uns + " | value = " + rebuilded); 
			}
			logger.addDebugStrign( " copy serializable data : key = " + uns); 
		}
		
		// rebuild Scheduler
		/*for( String ss : serialisedMap.getMap().keySet()){
			if( map.get(ss).isEmpty())
				map.remove( ss);
		}*/
		if( rebuildScheduler)
				rebuildScheduler( serialisedMap);
		
		// add this invoker to the static list 
		OFBuiltMapInvoker.addToAllInstances( serialisedMap);
		
		// rebuild debugging map
		@SuppressWarnings("unchecked")
		Map<String, Boolean> debuggingMap = (Map<String, Boolean>) serialisedMap.getStaticListFromName( DebuggingClassFlagData.DEBUGGERLISTNAME_mapKey);
		DebuggingClassFlagData.setDebuggingMap( debuggingMap);
		serialisedMap.removeTobuildedList( DebuggingClassFlagData.DEBUGGERLISTNAME_mapKey);
		logger.addDebugStrign( " rebuilding debugging Flags "  + debuggingMap);
		
		return( serialisedMap);
	}
	
	// change "predefinedOntology" has onto name !!!!!!!!!!! can load in NULL EXCEPTION
	public static void rebuildScheduler( OFBuiltMapInvoker serialisedMap){
		serialisedMap = new OFInitialiser().buildIndividual( "B_MapperBuilder", OWLReferences.getOWLReferences("predefinedOntology"), serialisedMap);
		serialisedMap = new OFInitialiser().buildIndividual( "B_SubOntologyBuilder", OWLReferences.getOWLReferences("predefinedOntology"), serialisedMap);
		serialisedMap = new OFInitialiser().buildIndividual( SCHEDULER_individualName, OWLReferences.getOWLReferences("predefinedOntology"), serialisedMap);
		serialisedMap = new OFInitialiser().buildIndividual( PROCEDURE_individualName, OWLReferences.getOWLReferences("predefinedOntology"), serialisedMap);
		logger.addDebugStrign( " rebuilding Scheduuler from individual : " + SCHEDULER_individualName + ". As : " + serialisedMap);
	}
	
}

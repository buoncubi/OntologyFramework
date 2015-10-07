package ontologyFramework.OFRunning;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFContextManagement.OWLReferencesSerializable;
import ontologyFramework.OFContextManagement.synchronisingManager.OFSynchroniserData;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class represent the state of the framework.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */

@SuppressWarnings("serial")
public class OFSystemState implements Serializable{

	private OFDebugLogger logger = new OFDebugLogger( this, true);//DebuggingClassFlagData.getFlag( OFInitialiser.SERIALIZATORDEBUG_individualName));
	
	private static Set< String> serializableListName;
	private String builderListName;
	private OFBuiltMapInvoker serialMap;
	
	/**
	 * Key with which the OWLReferences will be added into OFBuildedListInvoker
	 */
	public static final String OWLREFERENCES_keyWord = "OWLReferencesSerializable";
	/**
	 *  Key with which the OFSynchroniserData has been added into OFBuildedListInvoker.
	 *  Used since OFSynchroniserData not serializable.
	 */
	public static final String SYNCRHONISERLIST_keyWord = "ShyncroniserList";
	/**
	 * Format of folder path in default usage
	 */
	public static final String DATAFORMAT = "dd-MM-yyyy_HH-mm-ss";
	private String ontologyFilePath = System.getProperty("user.dir") + "/files/Serialized/";

	static{
		serializableListName = new HashSet< String>();
		serializableListName.add( "TimeTriggerList");
		//serializableListName.add( "MappersList");
		serializableListName.add( "ExceptionList");
		//serializableListName.add( "SchedulerList"); // nothing to do with quartz --> rebuild Algorithms
		//serializableListName.add( "ProcedureList");
		//serializableListName.add( "OntologyList");
		serializableListName.add( "EventList");
	}
	
	/**
	 * It calls {@link #OFSystemState(OFBuiltMapInvoker, String, Set, Set, String, boolean)}. Where
	 * the last three parameter are set to null.
	 * 
	 * @param builded map to be serialized
	 * @param listInvokerInstanceName name of the Map to be initialized
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	 */
	public OFSystemState( OFBuiltMapInvoker builded, String listInvokerInstanceName, boolean exportInferd){
		initialized( builded, listInvokerInstanceName, null, null, null, exportInferd);
	}
	/**
	 * It calls {@link #OFSystemState(OFBuiltMapInvoker, String, Set, Set, String, boolean)}. Where
	 * the two central parameter are set to null.
	 * 
	 * @param builded map to be serialized
	 * @param listInvokerInstanceName name of the Map to be initialized
	 * @param ontoFilePath folder directory in which save the owl file created from the serialization mechanism. Null value loads in a path as: {@code System.getProperty("user.dir") + "/files/Serialized/" + new SimpleDateFormat( DATAFORMAT).format(date)}}
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	 */
	public OFSystemState( OFBuiltMapInvoker builded, String listInvokerInstanceName, String ontoFilePath, boolean exportInferd){
		initialized( builded, listInvokerInstanceName, null, null, ontoFilePath, exportInferd);
	}
	/**
	 * It calls {@link #OFSystemState(OFBuiltMapInvoker, String, Set, Set, String, boolean)}. Where the last
	 * parameter is null and the other are propagated.
	 * 
	 * @param builded map to be serialized
	 * @param listInvokerInstanceName name of the Map to be initialized
	 * @param ontoToSerializeName set of keys belong to Map that will be serialized. Null value loads in a serialization of all the map: {@link OFBuiltMapInvoker#getMap()}
	 * @param listToSerializeName set of keys belong to the OWLReferences map that will be serialized. Null value loads in a serialization of all the map: {@link OWLReferences#getAllInstances()}
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	  */
	public OFSystemState( OFBuiltMapInvoker builded, String listInvokerInstanceName, Set<String> ontoToSerializeName, Set<String> listToSerializeName, boolean exportInferd){
		initialized( builded, listInvokerInstanceName, ontoToSerializeName, listToSerializeName, null, exportInferd);
	}
	/**
	 * Create a new System state relate to informations carried by builded.
	 * It ask for serializable representation of non serializable class and store them inside the Map.
	 * Than it adds additional data has debugging flags, carried by {@link DebuggingClassFlagData};
	 * and {@link ontologyFramework.OFContextManagement.OWLReferencesSerializable}. In this last case the frameworks saves ontologies
	 * trough {@link OWLReferences#getAllSerializableInstances(String, Set, boolean)}  
	 * 
	 * @param builded map to be serialized
	 * @param listInvokerInstanceName name of the Map to be initialized
	 * @param ontoToSerializeName set of keys belong to Map that will be serialized. Null value loads in a serialization of all the map: {@link OFBuiltMapInvoker#getMap()}
	 * @param listToSerializeName set of keys belong to the OWLReferences map that will be serialized. Null value loads in a serialization of all the map: {@link OWLReferences#getAllInstances()}
	 * @param ontoFilePath folder directory in which save the owl file created from the serialization mechanism. Null value loads in a path as: {@code System.getProperty("user.dir") + "/files/Serialized/" + new SimpleDateFormat( DATAFORMAT).format(date)}}
	 * @param exportInferd if true all the asserted axiom will be exported in the Ontology that will be saved
	 */
	public OFSystemState( OFBuiltMapInvoker builded, String listInvokerInstanceName, Set<String> ontoToSerializeName, Set<String> listToSerializeName, String ontoFilePath, boolean exportInferd){
		initialized( builded, listInvokerInstanceName, ontoToSerializeName, listToSerializeName, ontoFilePath, exportInferd);
	}
	private synchronized void initialized( OFBuiltMapInvoker builded, String listInvokerInstanceName, Set<String> ontoToSerializeName, Set<String> listToSerializeName, String ontoFilePath, boolean exportInferd){
		
		logger.addDebugStrign( listInvokerInstanceName + " is initialising with : serializableListName = " + serializableListName + 
				" |  OFBuildedListInvoker = " + builded.getMap().keySet() +
				" | ontoToSerializeName = " + ontoToSerializeName +
				" | listToSerializeName = " + listToSerializeName +
				" | listToSerializeName = " + listToSerializeName +
				" | exportInferd = " + exportInferd);
		
		// get the name of the instance to restart during de-serialization
		builderListName = builded.getInstanceName();
		
		// get a copy of the builded Map with all serializable objects
		saveDataTime();
		String name = listInvokerInstanceName.substring( 0, listInvokerInstanceName.indexOf( OFInitialiser.INVOKERNAME_separatorSymb)) +
				"SER" + dataTime + 
				listInvokerInstanceName.substring( listInvokerInstanceName.indexOf( OFInitialiser.INVOKERNAME_separatorSymb));
		serialMap = new OFBuiltMapInvoker( name);
		Map< String, Map< String, Object>> unserialMap = builded.getMap();
		boolean doit;
		for( String uns : unserialMap.keySet()){
			// logical block to know if this should be serialized
			if( listToSerializeName == null)
				doit = true;
			else if( listToSerializeName .contains( uns))
				 doit = true;
			else doit = false;
			// make OWLReference serializable
			if( doit){
				if( ! serializableListName.contains( uns)){ 
					logger.addDebugStrign( " analizing " + uns + " ... Unserializable");
					Map<String, Object> staticList = unserialMap.get(uns);
					if( uns.equals( SYNCRHONISERLIST_keyWord)){
						Map<String, Object> tmp = makeSyncrhoniserSerializable( staticList);
						serialMap.addTobuildedList( uns, tmp);
					} // add new if else to add new unseralizable mechanisms
				} else{ // copy the map
					logger.addDebugStrign( " analizing " + uns + " ... Serializable");
					serialMap.addTobuildedList( uns, unserialMap.get(uns));
				}
			}
		}
		
		// add axiliar information
		addDebuggingFlagData();
		addOWLReferences( ontoToSerializeName, ontoFilePath, exportInferd);
	}
	
	private String dataTime = "";
	private synchronized void saveDataTime(){
		Date date = Calendar.getInstance().getTime();
		dataTime = new SimpleDateFormat( DATAFORMAT).format(date);
	}
	
	private synchronized void addOWLReferences( Set< String> ontoToSerializeName, String filePath, boolean exportInferd){
		// Initialized path to save ontologies
		String hourlyPath = ontologyFilePath + dataTime + System.getProperty( "file.separator");
		if( filePath == null)
			ontologyFilePath = hourlyPath;
		else ontologyFilePath = filePath; 
		// add OWLReferences serializable data to the map
		Map<String, OWLReferencesSerializable> object = OWLReferences.getAllSerializableInstances( ontologyFilePath, ontoToSerializeName, exportInferd);
		serialMap.addTobuildedList( OWLREFERENCES_keyWord, object);
		logger.addDebugStrign( " add OWLReferencesSerializable dato the  serialMap : key = " + OWLREFERENCES_keyWord + " | value = " + object);
	}
	private synchronized void addDebuggingFlagData(){
		Map<String, Boolean> object = DebuggingClassFlagData.getDebuggingMap();
		String key = DebuggingClassFlagData.DEBUGGERLISTNAME_mapKey;
		serialMap.addTobuildedList(key, object);
		logger.addDebugStrign( " add debugging flag data to the serialMap : key = " + key + " | value = " + object);
	}
	
	private synchronized Map<String, Object> makeSyncrhoniserSerializable(Map<String, Object> staticList) {
		Map< String, Object> serial = new HashMap< String, Object>();
		for( String s : staticList.keySet()){
			OFSynchroniserData obj = ( OFSynchroniserData) staticList.get( s);
			serial.put( s, obj.getSerialisableData());
		}
		return( serial);
	}

	
	
	/**
	 * @return builderListName the name of the instance of {@link OFBuiltMapInvoker} which can been serialized 
	 */
	public synchronized String getBuilderListName() {
		return builderListName;
	}
	/**
	 * @return serialMap the instance of {@link OFBuiltMapInvoker} which can been serialized
	 */
	public synchronized OFBuiltMapInvoker getSerialMap() {
		return serialMap;
	}

	/**
	 * @return ontologyFilePath the folder directory in which the ontologies are saved
	 */
	public synchronized String getOntologyFilePath() {
		return ontologyFilePath;
	}

	
	/**
	 * @return serializableListName get the keys of the map {@link OFBuiltMapInvoker} which are not serializable and need further computations.
	 */
	public synchronized static Set<String> getSerializableListName() {
		return serializableListName;
	}
	/**
	 * @param unserializableName set the keys of the map {@link OFBuiltMapInvoker} which are not serializable and need further computations.
	 */
	public synchronized static void setSerializableListName( Set<String> unserializableName) {
		serializableListName = unserializableName;
	}
	/**
	 * @param entry add the key of the map {@link OFBuiltMapInvoker} which are not serializable and need further computations.
	 */
	public synchronized static void addToSerializableListName( String entry){
		serializableListName.add( entry);
	}
	
}

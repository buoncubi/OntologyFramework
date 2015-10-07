package ontologyFramework.OFRunning.OFInvokingManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;

/**
 *
 * This class is the manager of a synchornized {@link java.util.HashMap}. This has String keys
 * equal to the value of the objectProperty named as {@link ontologyFramework.OFRunning.OFInitialising.OFBuilderCommon#BUILDLISTNAME_objProp}
 * for each individuals belong to the ontological class {@link OFInitialiser#BUILDER_className}. The 
 * relative value linked to this unique name is the returning value of the method {@link OFBuilderInterface#getInitialisedObject()}
 * which still is an hashMap with String keys.
 * <p>
 * It is used to initialize classes during the initialization phase of the framework trough the 
 * Interface {@link OFBuilderInterface}. Than, during system evolution, those classes can be 
 * retrieved using this class for further computation.
 * <p>
 * Access to this class is returned by {@link OFInitialiser}. Static access to this 
 * individual can been done also by name. Since every instances are collected in an 
 * static Map by name.
 * 
 * <P>
 * @author Buoncomapgni Luca
 * @version 1.0
 * @see OFInitialiser
 * @see OFBuilderInterface
 *  
 */
@SuppressWarnings("serial")
public class OFBuiltMapInvoker implements Serializable{

	private final Map< String,  Map< String,  Object>> buildedList = new HashMap< String, Map< String,  Object>>(); 
	
	private String individualName;
	
	/**
	 * Create a new map and add this instance to the static map of OFBuildedListInvoker. 
	 * (see {@link #getAllInstances()} to track an instance).
	 * 
	 * @param individualName the name of this instance of this class. If it is null
	 * this instance will not be tracked. 
	 */
	public OFBuiltMapInvoker( String individualName){
		this.individualName = individualName;
		if( individualName != null)
			this.trackedClass( individualName);
	}
	
	/**
	 * @return the name of this instances saved in the static map retrievable from  {@link #getOFBuildedListInvoker( String)}
	 */
	public String getInstanceName(){
		return( individualName);
	}
	
	/**
	 * Add a value to the hashMap calling:
	 * {@code buildedList.put( key, ( Map< String, Object>) object)}.
	 * It returns false if {@code buildedList.containsKey( key)}, and does
	 * not add the map.
	 * By default, during framework initialization keys are the value of the objectProperty 
	 * named as {@link ontologyFramework.OFRunning.OFInitialising.OFBuilderCommon#BUILDLISTNAME_objProp} for each individuals belong 
	 * to the ontological class {@link ontologyFramework.OFRunning.OFInitialising.OFInitialiser#BUILDER_className}. Example : 
	 * "MapperList", "EventList" ...
	 * 
	 * @param key of the main hasMap
	 * @param object added to the main hashMap
	 * @return key already used flag
	 */
	@SuppressWarnings("unchecked")
	// occhio a errori sui cast
	public synchronized boolean addTobuildedList( String key, Map< ?, ?> object){
		if( ! buildedList.containsKey( key)){
			buildedList.put( key, ( Map< String, Object>) object);
			return( true);
		}
		System.out.println("EXCEPTION : the OFBuildedListInvoker cannot addToBuildedList if it already contains the key " + key);
		return( false);
	}
	/**
	 * It calls {@link #addTobuildedList(String, Map)}. 
	 * If overwrite is true the system forces OFBuildedListInvoker
	 * to substitute the value to the key if this already exist.
	 * 
	 * @param key of the main hasMap
	 * @param object added to the main hashMap
	 * @param overwrite force system to overwrite if the key already exist
	 * @return key already used flag
	 */
	@SuppressWarnings("unchecked")
	public synchronized boolean addTobuildedList( String key, Map< ?, ?> object, boolean overwrite){
		if( ! buildedList.containsKey( key))
			buildedList.put( ( String) key, ( Map< String, Object>) object);
		else if( overwrite)
			buildedList.put( ( String) key, ( Map< String, Object>) object);
		else{
			System.out.println("EXCEPTION : the OFBuildedListInvoker cannot addToBuildedList if it already contains the key " + key);
			return( false);
		}
		return( true);
	}
	
	/**
	 * Remove a value to the hashMap calling:
	 * {@code buildedList.remove( key)}.
	 * It returns false if {@code ! buildedList.containsKey( key)}.
	 * 
	 * @param  key of the main hasMap
	 * @return operation successful flag
	 */
	public synchronized boolean removeTobuildedList( String key){
		if( buildedList.containsKey( key)){
			buildedList.remove( key);
			return( true);
		}
		System.out.println("EXCEPTION : impossible to remove an unexisting indicidual from builded list");
		return( false);
	}
	
	/**
	 * clears all the builded list calling: {@code buildedList.clear()}
	 */
	public synchronized void clearbuildedList(){
		buildedList.clear();
	}
	
	/**
	 * @return returns the overall builded Map
	 */
	public synchronized Map<String, Map< String,  Object>> getMap(){
		return( buildedList);
	}
	
	/**
	 * returns a field of the overall builded Map calling
	 * {@code buildedList.get(key)}.
	 * 
	 * @param key of the main hasMap
	 * @return the object of the map relate to key
	 */
	public synchronized Map< String,  Object> getMap( String key){
		return( buildedList.get(key));
	}
	/**
	 * it calls { @link {@link OFBuiltMapInvoker#getMap(String)}}.
	 * and returns its value as an Object.
	 * 
	 * @param key of the main hasMap
	 * @return the object of the map relate to key
	 */
	public synchronized Object getStaticListFromName( String key){
		return( buildedList.get( key));	
	}
	/**
	 * It returns the value of {@code buildedList.get( listName).get( key)},
	 * which the actually builded class during the initialization phase. 
	 * 
	 * @param listName key of the main hasMap
	 * @param key of the map returned from {@link OFBuilderInterface#getInitialisedClasses}
	 * @return the initialized class
	 */
	public synchronized Object getObject( String listName, String key){
		Object o = getStaticListFromName( listName);
		if( o instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String, Object> list = ( Map<String, Object>) o;
			return( list.get(key));//getClassFromList( list, key));
		}
		else return null;
	}
	
	/*public synchronized Object getClassFromList( String listName, Integer index){
		Object o = getStaticListFromName( listName);
		if( o instanceof List){
			@SuppressWarnings("unchecked")
			List< Object> list = ( List< Object>) o;
			return( getClassFromList( list, index));
		}
		else return null;
	}
	public synchronized Object getClassFromList( Map<String, Object> list, String key){ 
		return( list.get( key));
	}
	public synchronized Object getClassFromList( List< Object> list, Integer index){
		return( list.get( index));
	}*/
	
	
	// static class tracker. It collects all the instances of this class in an HashMap.
	private static Map<String, OFBuiltMapInvoker> allInstances;  
	static  {  
		allInstances = new HashMap<String, OFBuiltMapInvoker>();  
	}    
	protected void finalize()  {  
		allInstances.values().remove( this);  
	}
	private boolean trackedClass( String individualName)  { 
		if( ! isInAllInstances( individualName)){
			allInstances.put( individualName, this);
			return( true);
		}
		System.out.println( "Exception");
		return( false);
	}
	/**
	 * Add an instance of OFBuildedListInvoker to the static HasMap which
	 * collect them by {@link #getInstanceName()}. 
	 * This method is used when the instance has been created
	 * with a null name and later we want that it appears in the static instance tracked.
	 * It adds the instance in according with the name retrieved with: {@code inv.getInstanceName()};
	 * if this is null the instance inv will no be added.
	 * 
	 * @param inv instance to track.
	 */
	public synchronized static void addToAllInstances( OFBuiltMapInvoker inv){
		String key = inv.getInstanceName();
		if( key != null)
			if( ! isInAllInstances( key))
				allInstances.put(key, inv);
	}
	/**
	 * @return all the Map where instances of this class are tracked by instanceName
	 */
	public static synchronized HashMap<String, OFBuiltMapInvoker> getAllInstances(){
		return( ( HashMap<String, OFBuiltMapInvoker>) allInstances );  
	}
	/**
	 * Returns the instance of this class calling : {@code return( llInstances.get( referenceName))}.
	 * If no instance exist with this name than, the methods returns null.
	 * 
	 * @param referenceName the name of the instance to retrieve
	 * @return the instance with the speciefied name. 
	 */
	public static synchronized OFBuiltMapInvoker getOFBuildedListInvoker( String referenceName){
		return( allInstances.get( referenceName));
	}
 	/**
 	 * It simply uses: {@code return( allInstances.containsKey( key));}
 	 * 
 	 * @param key the instance name
 	 * @return true if it exist
 	 */
 	public static boolean isInAllInstances( String key){
		return( allInstances.containsKey( key));
	}
}

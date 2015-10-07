package ontologyFramework.OFErrorManagement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.primitiveDataMapper.BooleanDataMapper;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * This static class collects all the data needed to initialize
 * logs. Indeed it manage the initialization of the debugging
 * configuration and the flags to decide the behavior of
 * the debugger. All this information are collected in a map which
 * is managed by this class. The keys of this map is the name of
 * the ontological individual that describe this debugging configuration.
 * After system start up this map is available into the {@code OFBuildedListInvoker},
 * with the name: {@value #DEBUGGERLISTNAME_mapKey}
 * Please refer to  {@link OFDebugLogger} for more info.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class DebuggingClassFlagData implements Serializable {

	/**
	 * Name of the ontological class where must find place individuals 
	 * to describe if particular logs must be notified or not. 
	 */
	public static final String DEBUGGER_classFlags = "DebuggedClass";
	/**
	 * Name of the ontological object property to define if the log 
	 * should be notified. Tha value of this property must be an individual
	 * belong to the class Boolean.
	 */
	public static final String DEBUGGINGFLAG_objectProperty = "logsDebuggingData";
	/**
	 * Name (key) of the map initialised in this class and stored
	 * into the {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
	 */
	public static final String DEBUGGERLISTNAME_mapKey = "DebuggingFlagList";

	private static Map<String, Boolean> debuggingMap = new HashMap< String, Boolean>();
	
	
	// set as non instatiable
	private DebuggingClassFlagData() {
        throw new AssertionError();
    }
	
	/**
	 * Flags are initialised during system start up in base at
	 * the information stored into the ontology.
	 * 
	 * @param nameKey ontological individual that specify this 
	 * debugging configuration
	 * @return the value of the flag. If it is true, than the 
	 * logs will be notified, no otherwise. 
	 */
	public static Boolean getFlag( String nameKey){ 
		return( debuggingMap.get( nameKey));
	}
	
	/**
	 * @return the debugging map between ontological individual name
	 * and their boolean value.
	 */
	public synchronized static Map<String, Boolean> getDebuggingMap() {
		return debuggingMap;
	}

	/**
	 * @param debuggingMap the debuggingMap to set
	 */
	public static void setDebuggingMap(Map<String, Boolean> debuggingMap) {
		DebuggingClassFlagData.debuggingMap = debuggingMap;
	}

	
	/**
	 * This method clear the old map and rebuild it taking 
	 * data from the ontology.
	 * 
	 * @param ontoRef reference to an OWL ontology
	 * @return true if the initial map is not null, false otherwise
	 */
	public static synchronized boolean rebuild( OWLReferences ontoRef){
		Map<String, Boolean> map = getDebugginConfiguration( ontoRef);
		if( map != null){
			debuggingMap.clear();
			debuggingMap.putAll( map);
			return( true);
		}
		return( false);
	}
	
	// initialise debuggin flags
	static synchronized private Map<String, Boolean> getDebugginConfiguration( OWLReferences ontoRef){
		// get loggerFlag (hard way, without mapper)
		OWLClass cl = ontoRef.getOWLClass( DEBUGGER_classFlags);
		Map<String, Boolean> map = new HashMap< String, Boolean>();
		try{
			Set<OWLNamedIndividual> individuals = ontoRef.getIndividualB2Class( cl);
			for( OWLNamedIndividual ind : individuals){
				//OWLNamedIndividual ind = ontoRef.getOWLIndividual("$*BuilderDebug", ontoRef);
				OWLObjectProperty prop = ontoRef.getOWLObjectProperty( DEBUGGINGFLAG_objectProperty);
				OWLNamedIndividual flagInd = ontoRef.getOnlyObjectPropertyB2Individual(ind, prop);
				OWLDataProperty dataprop = ontoRef.getOWLDataProperty( BooleanDataMapper.BOOLEANMAPPING_dataProperty);
				OWLLiteral value = ontoRef.getOnlyDataPropertyB2Individual(flagInd, dataprop);
				Boolean flag = Boolean.valueOf( value.getLiteral());
				String name = ontoRef.getOWLObjectName( ind);
				map.put( name, flag);
			}
			return( map);
		} catch( Exception e){
			System.out.println( "Exception");
			return( null);
		}
	}
	
}

package ontologyFramework.OFEventManagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventBuilder;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class contains a basic implementation of how store initializate mechanism
 * from the OFlanguage in data property.
 * Thasnks to this class it is possible to use the Event mapping mecchanism 
 * semply defining how to compute them. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public abstract class OFEventRepresentation implements Serializable{

	private List< String> order;
	private String className;
	private Map< String, OFEventParameterDefinition> parameterMap = new HashMap< String, OFEventParameterDefinition>();
	
	/**
	 * Create a new definition of event
	 * 
	 * @param packageClassName the full qualify to a class that represent the event provedure 
	 * implementing {@link OFEventInterface}
	 */
	public OFEventRepresentation( String packageClassName){
		className = packageClassName;
		//System.out.println( this.getClass().getSimpleName() + " create new with className: " + packageClassName);
	}
	
	/** 
	 * @return varNameOrder the ordered variable names to compute parameter for the event.
	 */
	public List<String> getOrder() {
		//System.out.println( this.getClass().getSimpleName() + " called get order : " + order);
		return order;
	}
	/**
	 * @param varNameOrder the ordered variable names to compute parameter for the event.
	 */
	public void setOrder(List<String> varNameOrder) {
		//System.out.println( this.getClass().getSimpleName() + " called set order : " + order);
		this.order = varNameOrder;
	}
	/**
	 * @return the fully java quilifier to the event class that implements {@link OFEventInterface}
	 */
	public String getClassName() {
		//System.out.println( this.getClass().getSimpleName() + " called get class name : " + className);
		return className;
	}
	
	/**
	 * @return the parameterMap. It contains an unordered set of {@link OFEventParameterDefinition} linked
	 * by variableName string value.
	 */
	public Map<String, OFEventParameterDefinition> getParameterMap() {
		//System.out.println( this.getClass().getSimpleName() + " called get parameter map : " + parameterMap);
		return parameterMap;
	}
	/**
	 * @param parameterMap set the parameterMap. It contains an unordered set of {@link OFEventParameterDefinition} linked
	 * by variableName string value.
	 */
	public void setParameterMap(
			Map<String, OFEventParameterDefinition> parameterMap) {
		//System.out.println( this.getClass().getSimpleName() + " called set parameter map : " + parameterMap);
		this.parameterMap = parameterMap;
	}
	
	/**
	 * add a parameter into the event tagged by its variable name. Those names must be 
	 * coherent with the one retrieved during the event building; managed
	 * by {@link OFEventBuilder}
	 * 
	 * @param varName the name of the parameter
	 * @param epd a parameter to inject as input into the Event implementstion 
	 * (interface of {@link OFEventInterface})
	 */
	public void addToParameterMap( String varName, OFEventParameterDefinition epd){
		//System.out.println( this.getClass().getSimpleName() + " add to parameter map : " + epd);
		parameterMap.put( varName, epd);
	}
	/**
	 * add parameters into the event as a map where keys are variable names and
	 * value initialized parameter. The names, must be coherent with the one retrieved 
	 * during the event building managed by {@link OFEventBuilder}
	 * 
	 * @param map of varName and parameter to inject as input into the Event implementation 
	 * (interface of {@link OFEventInterface})
	 */
	public void addToParameterMap( Map<String, OFEventParameterDefinition> map){
		//System.out.println( this.getClass().getSimpleName() + " add to parameter map : " + map);
		parameterMap.putAll( map);
	}
	
	/**
	 * @param varName name of the variable which define the parameter to remove
	 * from this event.
	 */
	public void removeFromParameterMap( String varName){
		parameterMap.remove( varName);
		//System.out.println( this.getClass().getSimpleName() + " remove from map : " + key);
	}
	
	/**
	 * It goes trough all the parameter following the ordered name of variables.
	 * For each of them it instantiates an new {@link EventComputedData} with the 
	 * correspondent parameter (computed each time using {@link OFEventParameterDefinition#getParameter()})
	 * and its ontology reference. All of them are than collected in a ordered List.
	 * 
	 * @return update computed list of parameter results
	 */
	public List< EventComputedData> getComputedParameterList() {
		List< EventComputedData> parList = new ArrayList< EventComputedData>();
		for( String s : this.getOrder()){
			OFEventParameterDefinition parDef = this.getParameterMap().get( s);
			EventComputedData ecd = new EventComputedData( parDef.getParameter(), parDef.getOWLReferences());
			parList.add( ecd);
		}
		return( parList);
	}
	
	/**
	 * 	/**
	 * Here the creation of a new instance of the event implementation should be done
	 * ( by default for {@link OFTimeTriggerInterface} and {@link OFEventInterface}. 
	 * Using {@link #getClassName()} is possible to load a new instance of such
	 * Interface (you must define your own methods to do so). Than the ordered and 
	 * update parameter values can be retrieved using {@link OFEventRepresentation#getComputedParameterList()},
	 * to have the inputs to check your own event implementation. 
	 * 
	 * 
	 * @param invoker lsit of builded class during initialization to be used by {@link OFEventInterface#evaluateEvent(List, OFBuiltMapInvoker)}
	 * @return the event result
	 */
	public abstract Object compute( OFBuiltMapInvoker invoker);
}

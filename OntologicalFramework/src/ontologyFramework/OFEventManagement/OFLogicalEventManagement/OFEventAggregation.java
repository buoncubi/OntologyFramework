package ontologyFramework.OFEventManagement.OFLogicalEventManagement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.mvel2.MVEL;

/**
 *
 * This class represent the event linked to every individual belong to the ontological class {@code "Event"}.
 * <p>
 * It is based on a boolean parameterized expression given as a String. And a list of parameters that can 
 * be added or removed. All parameters are expressed in therms of instances 
 * of {@link OFEventDefinition} and they must tagged with the name used in the expression. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */
@SuppressWarnings("serial")
public class OFEventAggregation implements Serializable{

	private final Map< String, OFEventDefinition> event = new HashMap<String, OFEventDefinition>();
	private String low;
	
	/**
	 * Create new event with a specific boolean low as a String. This will be processed by
	 * the library {@link MVEL} on runtime and must return always a boolean value.
	 * 
	 * @param aggregationLow parameterized logical relation
	 */
	public OFEventAggregation( String aggregationLow){
		low = aggregationLow;
		//System.out.println( this.getClass().getSimpleName() + " create new EventAggregation with low : " + low);
	}
	
	/**
	 * Add a new parameter to the definition of this event tagged with the variable
	 * name used to define the aggregation low. This class must contains one OFEventDefinition 
	 * for each name used in the {@code String aggregationLow}. 
	 * (ex: {@code "!r1 && r2"}) where r1 and r2 are variables.
	 *  
	 * @param varName the name of the variable used in the String {@code aggregationLow}
	 * @param eventDef initialized definition of the parameter
	 */
	public synchronized  void addParameter( String varName, OFEventDefinition eventDef){
		event.put( varName, eventDef);
		//System.out.println( this.getClass().getSimpleName() + " addedPrameter (varName, ofEventDefinition): " + varName + ", " + eventDef);
	}
	
	/**
	 * Remove a parameter from definition of this event.
	 * 
	 * @param varName the name of the variable used in the String {@code aggregationLow}
	 */
	public synchronized void removeParameterMap( String varName){
		event.remove( varName);
	}
	
	/**
	 * Remove all the parameters from definition of this event.
	 */
	public synchronized void clearParameterMap(){
		event.clear();
		//System.out.println( this.getClass().getSimpleName() + " parameterMap cleared");
	}
	
	/**
	 * Compute event result. It goes for all the parameter added to 
	 * this class and calls {@code ofEventDefinition.compute( invoker)}. 
	 * Finally, the returning boolean value is used to compute the aggregation low. 
	 * 
	 * @param invoker builded list of class during startup used by {@link OFEventInterface#evaluateEvent(java.util.List, OFBuiltMapInvoker)}
	 * @return true if the event occurs in this moment
	 */
	public synchronized boolean compute( OFBuiltMapInvoker invoker){
		Map<String, Boolean> context = new HashMap<String, Boolean>();
		for( String s : event.keySet()){
			OFEventDefinition e = event.get( s);
			context.put( s, (Boolean) e.compute( invoker));
		}
		return( MVEL.evalToBoolean( low, context));
	}
	
}

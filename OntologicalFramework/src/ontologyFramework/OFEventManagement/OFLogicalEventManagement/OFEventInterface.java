package ontologyFramework.OFEventManagement.OFLogicalEventManagement;

import java.io.Serializable;
import java.util.List;

import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFEventParameterDefinition;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This interface is used to define the event procedure. It is called
 * from {@link OFEventDefinition#compute(OFBuiltMapInvoker)} which calls
 * {@link #isCorrectInput(List)} first and, if the result is true it calls
 * {@link #evaluateEvent(List, OFBuiltMapInvoker)} and propagate the result
 * to the {@link OFEventAggregation#compute(OFBuiltMapInvoker)}.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public interface OFEventInterface extends Serializable {

	/**
	 * sviluppated with safety pourposes it is called to check
	 * if the type of parameter in inputs are correct. If this return
	 * false the event result of {@link #evaluateEvent(List, OFBuiltMapInvoker)}
	 * will be setted to null.
	 * 
	 * @param inputs ordered in accord with the ontological definition of the events trhougth the
	 * object property {@code "hasTypeEventDefinition}
	 * @return true if the inputs are corrects. If return else, event computation dennied.
	 */
	public boolean isCorrectInput( List< EventComputedData> inputs);
	
	/**
	 * implements how compute the event results starting from the inputs retrieved in
	 * {@link OFEventParameterDefinition#getParameter()}
	 * 
	 * @param inputs parameter
	 * @param invoker access to a builded class duriing software initialization
	 * @return true f the event occurs, false otherwise.
	 */
	public Boolean evaluateEvent( List< EventComputedData> inputs, OFBuiltMapInvoker invoker);
}

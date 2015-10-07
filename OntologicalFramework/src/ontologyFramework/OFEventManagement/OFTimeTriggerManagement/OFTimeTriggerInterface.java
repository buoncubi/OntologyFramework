package ontologyFramework.OFEventManagement.OFTimeTriggerManagement;

import java.util.List;

import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFEventParameterDefinition;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class is used to initialise, store and compute Temporal Trigger.
 * {@link #isCorrectInput(List)} is called frist and if it returns true
 * than {@link #getTrigger(List, OFBuiltMapInvoker)}} is called with the
 * same inputs. This is by default done from {@link OFTimeTriggerDefinition#compute(OFBuiltMapInvoker)}
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public interface OFTimeTriggerInterface {

	/**
	 * sviluppated with safety pourposes it is called to check
	 * if the type of parameter in inputs are correct. If this return
	 * false the event result of {@link #getTrigger(List, OFBuiltMapInvoker)}
	 * will be setted to null.
	 * 
	 * @param inputs ordered in accord with the ontological definition of the events trhougth the
	 * object property {@code "hasTypeTimeTriggereDefinition}
	 * @return true if the inputs are corrects. If return else, event computation dennied.
	 */
	public boolean isCorrectInput( List< EventComputedData> inputs);
	
	/**
	 * implements how to get the temporal trigger starting from the inputs retrieved in
	 * {@link OFEventParameterDefinition#getParameter()}
	 * 
	 * @param inputs parameter
	 * @param invoker access to a builded class duriing software initialization
	 * @return true f the event occurs, false otherwise.
	 */
	public Object getTrigger( List< EventComputedData> inputs,  OFBuiltMapInvoker invoker);
	
}

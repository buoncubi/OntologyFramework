package ontologyFramework.OFEventManagement.OFTimeTriggerManagement;

import java.io.Serializable;
import java.util.List;

import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFEventParameterDefinition;
import ontologyFramework.OFEventManagement.OFEventRepresentation;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;


// TODO : apparte per il metodo "compute(..)" questa classe è uguale a OFEventDefinition (usa extends...)

/**
 * This class contains the initialisated definition for all the temporal trigger. 
 * It collets also references to {@link OFEventParameterDefinition}.
 * And a method to get the actual trigger object. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */
@SuppressWarnings("serial")
public class OFTimeTriggerDefinition extends OFEventRepresentation implements Serializable {



	public OFTimeTriggerDefinition(String packageClassName) {
		super(packageClassName);
		// TODO Auto-generated constructor stub
	}
	
	/*and use the above list to call {@link OFTimeTriggerInterface#isCorrectInput(List)} frist; and, if
	  the result is true  {@link OFTimeTriggerInterface#getTrigger(List, OFBuildedListInvoker)} is 
	  called and its result is returned. Otherwise the method returns null.*/
	public Object compute( OFBuiltMapInvoker invoker){
		List< EventComputedData> parList = this.getComputedParameterList();
		OFTimeTriggerInterface trigger = ReflationInstanciater.instanciateOFTimeTriggrtByName( this.getClassName());
		Object triggerResult = null;
		if( trigger.isCorrectInput( parList)){
			triggerResult = trigger.getTrigger( parList, invoker);
		}
		return( triggerResult);
	}

}

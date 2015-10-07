package ontologyFramework.OFEventManagement.OFLogicalEventManagement;

import java.io.Serializable;
import java.util.List;

import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFEventParameterDefinition;
import ontologyFramework.OFEventManagement.OFEventRepresentation;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;

//TODO : apparte per il metodo "compute(..)" questa classe è uguale a OFTimeTriggerDefinition (usa extends...)

/**
 * This class contains the initialisated definition for all the events. 
 * It collets also references to {@link OFEventParameterDefinition}.
 * And a method to compute the event result. Which is called by {@link OFEventAggregation#compute(OFBuiltMapInvoker)}
 * and calls {@link OFEventParameterDefinition#getParameter()}.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */
@SuppressWarnings("serial")
public class OFEventDefinition extends OFEventRepresentation implements Serializable {

	
	
	public OFEventDefinition(String packageClassName) {
		super(packageClassName);
	}

	/*
	  Finaly, it creates a new instance of {@link OFEventInterface}, using {@link #getClassName()},
	  and use the above list to call {@link OFEventInterface#isCorrectInput(List)} frist; and, if
	  the result is true  {@link OFEventInterface#evaluateEvent(List, OFBuildedListInvoker)} is 
	  called and its result is returned. Otherwise the method returns null. return always a boolean.*/
	 
	public Object compute( OFBuiltMapInvoker invoker){
		List<EventComputedData> parList = this.getComputedParameterList();
		OFEventInterface event = ReflationInstanciater.instanciateOFEventByName( this.getClassName());
		Boolean eventResult = null;
		if( event.isCorrectInput( parList))
			eventResult = event.evaluateEvent( parList, invoker);
		return( eventResult);
	}
	
}

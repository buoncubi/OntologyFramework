package ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerImplementation;

import java.util.List;

import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;


/**
 * This class create a new Quartz Trigger with particular parameter. 
 * The ontology must contain an individual which has those properties:
 * <pre> 
 * 	{@code implementsOFTimeTriggerName "ontologyFramework.OFEventManagement.
 * 		OFTimeTriggerManagement.OFTimeTriggerImplementation.TriggNow"^^string}
 *  {@code hasTimeTriggerDefinition "}
 *  {@code in:ontologyFramework.OFEventManagement.OFEventParameter.
 *			?priority 3.AsInteger
 *			!( ?priority)"^^strign}
 * </pre> 
 * So the method {@link #isCorrectInput(List)} returns true only if: 
 * <pr>
 * 	{@code if( inputs.get( 0).getParameter() != null)
 *			if( inputs.get( 0).getParameter() instanceof Integer)
 *				if( ( (Integer) inputs.get( 0).getParameter() > 0) && ( 
 *						( Integer) inputs.get( 0).getParameter() < 10))
 *					return( true);
 *		   else return false;}
 * </pr>
 * Practically the trigger builded by the method {@link #getTrigger(List, OFBuiltMapInvoker)}
 * is given by:
 * {@code TriggerBuilder.newTrigger()
 *				.withPriority( (Integer) inputs.get( 0).getParameter())
 *	            .startNow()
 *	            .build());}
 * 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class TriggerNow implements OFTimeTriggerInterface{

	/*@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get( 0).getParameter() != null)
			if( inputs.get( 0).getParameter() instanceof Integer)
				if( ( (Integer) inputs.get( 0).getParameter() > 0) && ( 
						( Integer) inputs.get( 0).getParameter() < 11))
					return( true);
		return false;
	}

	@Override
	public Object getTrigger(List<EventComputedData> inputs,
			OFBuiltMapInvoker invoker) {

		Trigger trigger = TriggerBuilder.newTrigger()
				.withPriority( (Integer) inputs.get( 0).getParameter())
	            .startNow()
	            .build();
	    return( trigger);
	}*/
	
	@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get(0).getParameter() instanceof Integer)
			if( ( (Integer) inputs.get( 0).getParameter() > 0) && ( 
					( Integer) inputs.get( 0).getParameter() < 11))
				return( true);
		return( false);
	}

	@Override
	public Object getTrigger(List<EventComputedData> inputs,
			OFBuiltMapInvoker invoker) {
		// get quantity
		Integer priority = (Integer) inputs.get(0).getParameter();
		
		Trigger trigger = TriggerBuilder.newTrigger()
			      .withSchedule(  
	                    SimpleScheduleBuilder.simpleSchedule()
	                    .withIntervalInMilliseconds(100)
	                    .withRepeatCount( 2))
	                    .withPriority( priority)
	              .build();
		
		return( trigger);
	}

}

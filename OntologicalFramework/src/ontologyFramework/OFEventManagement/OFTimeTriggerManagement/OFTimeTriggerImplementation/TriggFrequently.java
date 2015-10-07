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
 * 		OFTimeTriggerManagement.OFTimeTriggerImplementation.TriggFrequently"^^string}
 *  {@code hasTypeTriggerDefinition "}
 *  {@code in:ontologyFramework.OFEventManagement.OFEventParameter.
 *			?frequency 10.AsInteger
 *			?couter ^.AsInteger
 *			?priority 6.AsInteger
 *			!r triggFrequentlyInSeconds( ?frequency ?priority ?couter)"^^strign}
 * </pre> 
 * So the method {@link #isCorrectInput(List)} returns true only if: {@code
 * inputs.get(0).getParameter() instanceof Integer}, {@code inputs.get(1).getParameter() instanceof Integer}
 * and {@code (inputs.get(2).getParameter() == null) || (inputs.get(2).getParameter() instanceof Integer)} are true.
 * While the method {@link #getTrigger(List, OFBuiltMapInvoker)} returns a quartz trigger with
 * the specified parameter. If count number is equal to null than, the trigger has 
 * {@code repeatForever()} property.
 * 
 * if counter is 0 than the trigger is "fired now" only once
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class TriggFrequently implements OFTimeTriggerInterface{


	@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get(0).getParameter() instanceof Integer)
			if( inputs.get(1).getParameter() instanceof Integer){
				if( inputs.get(2).getParameter() == null)
					return( true);
				if( inputs.get(2).getParameter() instanceof Integer)
					return( true);
			}
		return( false);
	}

	@Override
	public Object getTrigger(List<EventComputedData> inputs,
			OFBuiltMapInvoker invoker) {
		// get quantity
		Integer frequency = (Integer) inputs.get(0).getParameter();
		Integer priority = (Integer) inputs.get(1).getParameter();
		
		Trigger trigger = null;
		
		if( inputs.get(2).getParameter() == null){
			
			trigger = TriggerBuilder.newTrigger()
				      .withSchedule(  
		                    SimpleScheduleBuilder.simpleSchedule()
		                    .withIntervalInSeconds( frequency)
		                    .repeatForever())
		                    .withPriority( priority)
		              .build();
			
		} else {
			Integer counter = (Integer) inputs.get(2).getParameter();
			trigger = TriggerBuilder.newTrigger()
				      .withSchedule(  
		                    SimpleScheduleBuilder.simpleSchedule()
		                    .withIntervalInSeconds( frequency)
		                    .withRepeatCount( counter))
		                    .withPriority( priority)
		              .build();
		}
		return( trigger);
	}

}

package ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerImplementation;

import java.util.List;

import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

public class TriggerFrequentlyInMilliseconds implements OFTimeTriggerInterface{

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
		Long frequency = Long.valueOf( (Integer) inputs.get(0).getParameter());
		Integer priority = (Integer) inputs.get(1).getParameter();
		
		Trigger trigger = null;
		
		if( inputs.get(2).getParameter() == null){
			
			trigger = TriggerBuilder.newTrigger()
				      .withSchedule(  
		                    SimpleScheduleBuilder.simpleSchedule()
		                    .withIntervalInMilliseconds( frequency)
		                    .repeatForever())
		                    .withPriority( priority)
		              .build();
			
		} else {
			Integer counter = (Integer) inputs.get(2).getParameter();
			trigger = TriggerBuilder.newTrigger()
				      .withSchedule(  
		                    SimpleScheduleBuilder.simpleSchedule()
		                    .withIntervalInMilliseconds( frequency)
		                    .withRepeatCount( counter))
		                    .withPriority( priority)
		              .build();
		}
		return( trigger);
	}

}

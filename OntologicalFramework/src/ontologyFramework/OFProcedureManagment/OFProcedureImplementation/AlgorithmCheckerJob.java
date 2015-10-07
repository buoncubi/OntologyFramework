package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventAggregation;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerDefinition;
import ontologyFramework.OFProcedureManagment.Algorithm;
import ontologyFramework.OFProcedureManagment.OFProcedureInterface;
import ontologyFramework.OFProcedureManagment.OFProcedureSynchronisation;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * This job is called periodically based with a procedure definition
 * (see {@link ontologyFramework.OFProcedureManagment.OFProcedureBuilder}) and it is used to check changes 
 * into the ontology. In particular for all procedure a specific 
 * instance of this job will run with the following proposes.
 * Check which are the Event and the TimeTrigger attached to the 
 * individual which describes a procedure. Than the results are
 * propagate to the {@link Algorithm} which decide if run or not the procedure,  
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AlgorithmCheckerJob implements Job{ //extends OFJobAbstract{//

	// if a trigger and synchronization are in the same individual than 
	// quartz trigger is skyped
	private static final String oldTrName = "oldTrName";
	
	private static Integer VERBOSE_count = 0;
	private static Integer VERBOSE_limit = 1000;
	
	public AlgorithmCheckerJob(){
		super();
	}
	
	private OFDebugLogger logger = new OFDebugLogger( this, true);
	
	@Override
	public synchronized void execute(JobExecutionContext context)
			throws JobExecutionException {
	//@Override
	//void runJob(JobExecutionContext context) throws JobExecutionException {	
		JobDataMap data = context.getJobDetail().getJobDataMap();  
		
		// get onto references and procedure individual name
		String inPar = data.getString( OFJobAbstract.ontoName);
		OWLReferences ontoRef = OWLReferences.getOWLReferences( inPar);
		String indProcName = data.getString( OFJobAbstract.procedureName);

		// get event Name
		OFProcedureInterface inter = Algorithm.getOFProcedureInterface( indProcName);
		String evName = inter.getEventName( ontoRef);

		// get list invoker
		inPar = data.getString( OFJobAbstract.invokerName);
		OFBuiltMapInvoker invoker = OFBuiltMapInvoker.getOFBuildedListInvoker( inPar);
		
		// get caller 
		inPar = data.getString( OFJobAbstract.callerName);
		OFProcedureInterface caller = Algorithm.getOFProcedureInterface( inPar);
		
		// get event, compute result and set it
		OFEventAggregation event = (OFEventAggregation) invoker.getObject( "EventList", evName);//"EventTest");
		boolean bool = true;
		if( event != null)
			bool = event.compute( invoker);
		caller.setEventResult( bool);

		// get trigger Name
		String trName = inter.getTimeTriggerName( ontoRef);
		String holdTrName = data.getString( oldTrName);		
					
		if( bool){
			// get value of the synchronizationWith object property
			Set< OWLNamedIndividual> synchInd = ontoRef.getObjectPropertyB2Individual( indProcName, context.getJobDetail().getJobDataMap().getString( 
					OFJobAbstract.SYNCHRONIZATION_objPropertyName));
			if( synchInd != null){
				new OFProcedureSynchronisation( indProcName, synchInd);

				//System.err.println( indProcName +" addding new Synchronisation " + OFProcedureSynchronisation.getAllInstances());
			} //else {
				// get trigger and set it if the name of the ontological individual is different from before
				if( trName == null){
					System.out.println( "Exception: an OFProcedure must contain a time trigger component");
				}else if( ! trName.equals( holdTrName)){
					OFTimeTriggerDefinition timeTriggerDef = ( OFTimeTriggerDefinition) invoker.getObject( "TimeTriggerList", trName);
					Object tr = timeTriggerDef.compute( invoker);
					inter.setTimeTrigger( tr);
				}
			//}
			context.getJobDetail().getJobDataMap().put( oldTrName, trName);
		}
		
		VERBOSE_count++;
		if( VERBOSE_count > VERBOSE_limit){
			logger.addDebugStrign( "(verbose=" + VERBOSE_limit + ") cheking " + indProcName + " for event " + evName + " = " + bool + ". And trigger " + trName);
			VERBOSE_count = 0;
		}
	
	}	
}

package ontologyFramework.OFProcedureManagment;

import java.util.HashMap;
import java.util.Map;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.reservatedDataType.NameMapper;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerDefinition;
import ontologyFramework.OFProcedureManagment.OFProcedureImplementation.AlgorithmCheckerJob;
import ontologyFramework.OFProcedureManagment.OFProcedureImplementation.OFJobAbstract;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import arq.tokens;

//descrition of method that are overwrited ?????????????????????????

/**
 * This class is an implementation of {@link OFProcedureInterface}
 * which is designed to run procedure using Quartz API;
 * it works with data initialized from {@link OFProcedureBuilder}.
 * It is design with the following behaviors: a Procedure must be
 * linked to a scheduler and a tread pool. Also, it has a 
 * checker job to update the state of the ontology that
 * is describing the procedure. This job is automatically created and
 * runs with a specific frequency defined in the ontology. 
 * The checker implementation is given by {@link ontologyFramework.OFProcedureManagment.OFProcedureImplementation.AlgorithmCheckerJob}.
 * Also, a procedure can contains an Event, if not is considered by 
 * default that the it has an Event always true. Furthermore, a procedure
 * can be synchronized with another individual that describe a procedure.
 * In this last case the event is not considered. When the individual
 * that synchronize this procedure ends, or when an event is true
 * than the procedure is ready to run. Anyway, it will actually run
 * in accord with its TimeTrigger ontological definition that 
 * describe its behavior when the previous consideration 
 * are favorable to run this procedure. Remember that the checker
 * job will always run on background and its frequency affect
 * the velocity of the changes that the system can appreciated.
 * Is recommended to build checker that are not computational
 * complex and make their frequency high enough. Synchronization
 * properties are not affected by the checker frequency. Finally,
 * the always need features for a Procedure are: Checker, TimeTrigger,
 * thread pool size, full qualifier to the procedure implementation
 * and an Event or a Synchronization property. If both of them exist
 * than the system will consider only one of them in accord with
 * the considerations above.       
 * 
 * In particular this class initialize a procedure in accord
 * with {@link OFProcedureBuilder} during building time. This will
 * set inside this class all the interesting characteristics of
 * procedure and also it created the related CheckerJob and MainJob
 * (which contains the runnable implementation of the procedure). Than 
 * the CheckerJob is run and will be stopped only by {@link #shotdown()}.
 * The checker set the quantity relate to Events and TimeTrigger and this
 * changes make this class calling {@link  #stop()} or {@link #run()} in a 
 * way to implement the above considerations. Moreover, it will care
 * to give to the implementing procedural job the most up to date
 * information required as input througth the Quartz JobDataMap. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class Algorithm implements OFProcedureInterface{

	public final static String chekerName = "-checker";
	// keyWords[ 1] : chekerFreq = "hasOFProcedureCheckerFrequencyInMilliSeconds";
	// keyWords[2] : procedureScheduler = "hasOFProcedureScheduler";
	// keyWords[3] : procedureEvent = "hasOFProcedureEvent";
	// keyWords[4] : procedureTrigger = "hasOFProcedureTrigger";
	// keyWords[5] : pooSize_dataProp = "hasOFProcedureConcurrentPoolSize";
	// keyWords[6] : "hasOFProcedureSynchronisationWith" 
	// !!!!!!!!!! "SchedulerList" line 91
	
	private JobDetail mainJob, checkerJob;
	private SimpleTrigger checkerTrigger;
	private Scheduler scheduler;
	private boolean eventsResult;
	private String algorithmName;
	private SimpleTrigger trigger;
	private Class<OFJobAbstract> mainJobClass = null;
	private OFBuiltMapInvoker listInvoker;
	private OWLReferences ontoRef;
	private OWLNamedIndividual procedureInd;
	private Integer concurrencePoolSize;
	private String[] keyWords;

	@SuppressWarnings("unchecked")
	@Override
	public void initialise(OWLNamedIndividual procedureInd,
			OWLReferences ontoRef, OFBuiltMapInvoker listInvoker, String[] keyWords) {
		this.keyWords = keyWords;
		this.listInvoker = listInvoker;
		this.ontoRef = ontoRef;
		this.procedureInd = procedureInd;
		algorithmName = OWLLibrary.getOWLObjectName(procedureInd);
		trackedClass( algorithmName);
		
		// get the name of the algorithm (must implement OFProcedureMainJob)
		// prop: implementsOFProcedureName
		/*OWLObjectProperty property = OWLLibrary.getOWLObjectProperty( procedureName_objProp, ontoRef);
		OWLNamedIndividual ind = OWLLibrary.getOnlyObjectPropertyB2Individual( procedureInd, property, ontoRef);
		String packageClassName = NameMapper.getNameFromOntology(ind, ontoRef);*/
		String packageClassName = NameMapper.getNameFromOntology( procedureInd, ontoRef);
		
		// get class of the main job
		try {
			mainJobClass = (Class<OFJobAbstract>) Class.forName( packageClassName);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// initialize to be main job, getting pool size
		/*property = OWLLibrary.getOWLObjectProperty( pooSize_objProp, ontoRef);
		OWLNamedIndividual sizeInd = OWLLibrary.getOnlyObjectPropertyB2Individual( procedureInd, property, ontoRef);
		OFDataMapperInterface intMapper = (OFDataMapperInterface) listInvoker.getClassFromList("MappersList", "Integer");
		concurrencePoolSize = (Integer) intMapper.mapFromOntology(sizeInd, ontoRef);*/
		OWLDataProperty property = ontoRef.getOWLDataProperty( keyWords[ 5]);
		OWLLiteral sizeInd = ontoRef.getOnlyDataPropertyB2Individual( procedureInd, property);
		concurrencePoolSize = Integer.valueOf( sizeInd.getLiteral());
		
		
		// get scheduler
		OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 2]);
		OWLNamedIndividual schInd = ontoRef.getOnlyObjectPropertyB2Individual(procedureInd, prop);
		String schName = OWLLibrary.getOWLObjectName( schInd);
		scheduler = ( Scheduler) listInvoker.getObject( "SchedulerList", schName);
		
		// initialize main job trigger
		OFTimeTriggerDefinition timeTriggerDef = ( OFTimeTriggerDefinition) listInvoker.getObject( "TimeTriggerList", getTimeTriggerName( ontoRef));
		if( trigger != null)
			trigger = (SimpleTrigger) timeTriggerDef.compute( listInvoker);
		
		
		// get checker frequency
		property = ontoRef.getOWLDataProperty( keyWords[ 1]);
		//OWLNamedIndividual freqInd = OWLLibrary.getOnlyObjectPropertyB2Individual( procedureInd, prop, ontoRef);
		OWLLiteral freqLit = ontoRef.getOnlyDataPropertyB2Individual(procedureInd, property);
		// if the job hasCheckerFrequencyInMilliseconds property than initialize
		// and start the checker.
		//System.out.println("!! "+procedureInd+" " + freqLit);
		if( freqLit != null){
			//OFDataMapperInterface integerMapper = (OFDataMapperInterface) listInvoker.getClassFromList( "MappersList", "Integer");
			//Integer freq = (Integer) integerMapper.mapFromOntology( freqInd, ontoRef);
			Long freq = Long.valueOf( freqLit.getLiteral());
			
			// create checker job
			checkerJob = JobBuilder.newJob( AlgorithmCheckerJob.class)
				    .withIdentity( OWLLibrary.getOWLObjectName(procedureInd) + chekerName)
				    .build();
		
			// create trigger for checker job
			checkerTrigger = TriggerBuilder.newTrigger()
				      .withSchedule(  
		                    SimpleScheduleBuilder.simpleSchedule()
		                    .withIntervalInMilliseconds( freq)
		                    .repeatForever()).forJob( checkerJob)  
	                           .build();
		
		
			// prepare inputs for the cheker jobs
			checkerJob.getJobDataMap().put( OFJobAbstract.invokerName, listInvoker.getInstanceName());
			checkerJob.getJobDataMap().put( OFJobAbstract.ontoName, ontoRef.getOntoName());
			checkerJob.getJobDataMap().put( OFJobAbstract.callerName, algorithmName);
			checkerJob.getJobDataMap().put( OFJobAbstract.procedureName, OWLLibrary.getOWLObjectName( procedureInd));
			checkerJob.getJobDataMap().put( OFJobAbstract.SYNCHRONIZATION_objPropertyName, keyWords[ 6]);			
		
			// schedule checkerJob
			try {
				scheduler.scheduleJob( checkerJob, checkerTrigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		} else {
			setEventResult( true);
			setTimeTrigger( trigger);
		}
	}

	@Override
	public void run() {
		SimpleTrigger mainTrigger = (SimpleTrigger) getTimeTrigger();
		// see if conditions are matched
		Boolean eventResult = getEventResult();
		
		try {
			// create main job
			mainJob = JobBuilder.newJob( mainJobClass) //new AlgorithmMainJob().getClass())//AlgorithmMainJob.class)
				    .withIdentity( OWLLibrary.getOWLObjectName( procedureInd))
				    .build();
			
			// prepare inputs for the main jobs
			mainJob.getJobDataMap().put( OFJobAbstract.invokerName, listInvoker.getInstanceName());
			mainJob.getJobDataMap().put( OFJobAbstract.ontoName, ontoRef.getOntoName());
			mainJob.getJobDataMap().put( OFJobAbstract.callerName, algorithmName);
			mainJob.getJobDataMap().put( OFJobAbstract.procedureName, OWLLibrary.getOWLObjectName( procedureInd));
			mainJob.getJobDataMap().put( OFJobAbstract.CONCURRENCYPOOLSYZE_varName, concurrencePoolSize);
			
			// scheduling main Job
			if( ( mainTrigger != null) && ( eventResult) && ( ! scheduler.checkExists( mainJob.getKey()))){
				if( ! scheduler.checkExists( mainJob.getKey()))
					//scheduler.deleteJob( mainJob.getKey());
					scheduler.scheduleJob( mainJob, mainTrigger);
				//System.out.println( algorithmName + "  (((((((((((((((  " + (mainTrigger != null) + " && " + eventResult + " && " + (! scheduler.checkExists( mainJob.getKey())) + " " + mainJob + " " + mainTrigger);
			}else System.out.println( "Exception : procedure does not match the events to run");
			
			// start scheduling
			scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		// if condition are not matched anymore stop it
		try {
			if( ! getEventResult())
				scheduler.deleteJob( mainJob.getKey());
			if( trigger != null)
				scheduler.unscheduleJob( trigger.getKey());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.NullPointerException e){
			// TODO Auto-generated catch block
		}
	}

	@Override
	public Boolean getEventResult() {
		return( eventsResult);
	}
	
	@Override
	public String getEventName( OWLReferences ontoRef){
		OWLNamedIndividual procedureInd = ontoRef.getOWLIndividual( algorithmName);
		OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 3]);
		OWLNamedIndividual schInd = ontoRef.getOnlyObjectPropertyB2Individual( procedureInd, prop);
		return( OWLLibrary.getOWLObjectName( schInd));
	}
	
	@Override
	public void setEventResult( boolean result) {
		boolean wasResult = getEventResult();
		eventsResult = result;		
		if( (! eventsResult) && ( wasResult)){
			// is false now and was true before
			stop();
		}else if( ( eventsResult) && (! wasResult)){
			// is true now and was false before
			run();
		}
	}

	@Override
	public Object getTimeTrigger() {
		return( trigger);
	}

	@Override
	public String getTimeTriggerName( OWLReferences ontoRef) {
		OWLNamedIndividual procedureInd = ontoRef.getOWLIndividual( algorithmName);
		OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 4]);
		OWLNamedIndividual schInd = ontoRef.getOnlyObjectPropertyB2Individual( procedureInd, prop);
		return( OWLLibrary.getOWLObjectName( schInd));
	}
	
	@Override
	public void setTimeTrigger(Object timeTrigger) {
		if( timeTrigger != null){
			stop();
			trigger = (SimpleTrigger) timeTrigger;
			run();
		}
	}

	@Override //wait end of task
	public void shotdown() {
		try {
			scheduler.shutdown( true);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public JobDetail getCheckerJob() {
		return checkerJob;
	}

	@Override
	public JobDetail getMainJob() {
		return mainJob;
	}
	
	
	// static class tracker. It collects all the instances of this class in an HashMap.
	private static Map<String, OFProcedureInterface> allInstances;  
	static  {  
		allInstances = new HashMap<String, OFProcedureInterface>();  
	}    
	protected void finalize()  {  
		allInstances.values().remove( this);  
	}
	private boolean trackedClass( String individualName)  { 
		if( ! isInAllInstances( individualName)){
			allInstances.put( individualName, this);
			return( true);
		}
		System.out.println( "Exception");
		return( false);
	}
	/**
	 * All the time that this class is created its instance is
	 * also collected into a HashMap with its ontological individual 
	 * name as key. This method returns the map of the create
	 * instances so far created for this class.  
	 * 
	 * @return all the instance of this class created so far and collected into a map.
	 */
	public static synchronized HashMap<String, OFProcedureInterface> getAllInstances(){
		return( ( HashMap<String, OFProcedureInterface>) allInstances );  
	}
	/**
	 * This methods calls: 
	 * {@code Algorithm#getAllInstances().get( referenceName)}
	 * to return an instance of this class which is relate to an
	 * ontological individual with name equal {@link tokens} the 
	 * input parameter.
	 * 
	 * @param referenceName
	 * @return The instance of this individual which has individual 
	 * named as the input parameter.
	 */
	public static synchronized OFProcedureInterface getOFProcedureInterface( String referenceName){
		return( allInstances.get( referenceName));
	}
 	/**
 	 * It calls: {@code Algorithm#getAllInstances().containsKey( key)}
 	 * to return true if an instance with the key name has been 
 	 * already created for this class, false otherwise.
 	 * 
 	 * @param key the name of the instance of this class 
 	 * (name of the ontological individual relate to this procedure).
 	 * @return true if an instance of this class already exist with such
 	 * name; false if not.
 	 */
 	public static boolean isInAllInstances( String key){
		return( allInstances.containsKey( key));
	}

	
}

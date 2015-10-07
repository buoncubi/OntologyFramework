package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFProcedureManagment.Algorithm;
import ontologyFramework.OFProcedureManagment.OFProcedureInterface;
import ontologyFramework.OFProcedureManagment.OFProcedureSynchronisation;
import ontologyFramework.OFProcedureManagment.ProcedureConcurrenceData;
import ontologyFramework.OFProcedureManagment.ProcedureConcurrenceManager;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import fileManagerAPI.FileReader;

/**
 * This class encapsulate a the running method of a procedure implementing
 * common methods. In particular it retrieve quantity that are useful during the
 * coding of the procedure. Moreover it manage the concurrent pool size and get
 * information about deadlines, computational time and synchronization. Please
 * see {@link Algorithm} for more info about this entities.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 * 
 */
@PersistJobDataAfterExecution
// @DisallowConcurrentExecution
public abstract class OFJobAbstract implements Job{

	/**
	 * Key into the Quartz contest about the size (Integer) of the concurrent
	 * Thread Pool.
	 */
	public static final String CONCURRENCYPOOLSYZE_varName = "concurrencyPoolSize";
	/**
	 * Key into the Quartz contest about the name of the ontology in which the
	 * procedure is referring to.
	 */
	public static final String ontoName = "ontoName";
	/**
	 * Key into the Quartz contest about the name of the builded list of classes
	 * during system start up.
	 */
	public static final String invokerName = "invokerName";
	/**
	 * Key into the Quartz contest about the name of the instance of
	 * {@link Algorithm} which activate this Job.
	 */
	public static final String callerName = "callerName";
	/**
	 * Key into the Quartz contest about the name of the individual related to
	 * this procedure.
	 */
	public static final String procedureName = "procedureName";
	/**
	 * Key into the Quartz contest about the name of the object property used to
	 * synchronize procedure between each other.
	 */
	public static final String SYNCHRONIZATION_objPropertyName = "synchronizationPropertyName";

	private OFDebugLogger logger = new OFDebugLogger(this,
			DebuggingClassFlagData
					.getFlag(OFInitialiser.BUILDERDEBUG_individualName));

	private static Set<String> toRemoveTimeTrigger = new HashSet<String>();

	private static Integer verbose = 50;
	private Integer verboseCount = 0;
	private boolean verboseShow = true;

	private static boolean initialised = false;
	private static Map<String, ProcedureConcurrenceManager> pcm = new HashMap<String, ProcedureConcurrenceManager>();

	private Integer concurrencePoolSize;
	private OWLReferences ontoRef;
	private String procedureIndividualName;
	private OFBuiltMapInvoker invoker;
	private OFProcedureInterface algorithmCaller;
	private String algorithmInstanceNameBase;
	private String algorithmInstanceName;
	private JobExecutionContext context;

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		Long initialTime = System.currentTimeMillis();
		
		try {
		
			this.context = context;
			initialiseContext(context);
		
			// ind = context.getJobDetail().getJobDataMap().getString(
			// AlgorithmCheckerJob.procedureName);
		
			if (context.getTrigger() == null)
				initialised = false;

			ProcedureConcurrenceData run = null;
			if ((!initialised) || (!pcm.containsKey(algorithmInstanceNameBase))) {
				pcm.put(algorithmInstanceNameBase,
						new ProcedureConcurrenceManager(
								algorithmInstanceNameBase, concurrencePoolSize));
				initialised = false;
			} else
				run = setRunning(context); // != null => true

			if (run != null)
				runJob(context);

			if (initialised && (run != null))
				setScheduled(context, run);
			initialised = true;
		} catch (Exception e) {
			pcm.remove(algorithmInstanceNameBase);
		}

		logger.addDebugStrign("....." + this.getProcedureIndividualName()
				+ " ends in:" + (System.currentTimeMillis() - initialTime)
				+ " [ms].");
	}

	private void initialiseContext(JobExecutionContext context) {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();

			String inPar = data.getString(ontoName);
			ontoRef = OWLReferences.getOWLReferences(inPar);

			procedureIndividualName = data.getString(procedureName);

			inPar = data.getString(invokerName);
			invoker = OFBuiltMapInvoker.getOFBuildedListInvoker(inPar);

			inPar = data.getString(callerName);
			algorithmCaller = Algorithm.getOFProcedureInterface(inPar);

			concurrencePoolSize = context.getJobDetail().getJobDataMap()
					.getInt(CONCURRENCYPOOLSYZE_varName);

			algorithmInstanceNameBase = context.getJobDetail().getKey()
					.getName();
		} catch (java.lang.ClassCastException e) {

		}
	}

	private ProcedureConcurrenceData setRunning(
			JobExecutionContext context) {
		ProcedureConcurrenceData pcd = pcm.get(algorithmInstanceNameBase)
				.generateID();
		if (pcd != null) {
			if (pcd.getID() != null) {
				algorithmInstanceName = pcd.toString();
				if (!pcd.isDeadlineSetted()) // set only default!!!
					pcd.setDeadline(context.getTrigger().getNextFireTime());

				logger.addDebugStrign(" run new job. Instance Name "
						+ algorithmInstanceName);
				Date date = Calendar.getInstance().getTime();
				String t = new SimpleDateFormat("-dd-MM-yy_HH-mm-ss-SSS")
						.format(date);
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! usa mapper!!!!
				/*ontoRef.addDataPropertyB2Individual(ontoRef
						.getOWLIndividual(pcd.toString() + t), ontoRef
						.getOWLDataProperty("hasTypeTimeStamp"), ontoRef
						.getOWLLiteral(
								System.currentTimeMillis(),
								ontoRef.getFactory().getOWLDatatype(
										OWL2Datatype.XSD_LONG.getIRI())), false);*/

				return (pcd);
			}
		}
		if( this.verboseCount++ > verbose){
			logger.addDebugStrign(
					context.getJobDetail().getKey()
							+ " does not run since the pool is full. Pool : "
							+ pcm.get(algorithmInstanceNameBase)
									.getProcedureActivated(), true);
			verboseCount = 0;
		}
		return (null);
	}

	private void setScheduled(JobExecutionContext context,
			ProcedureConcurrenceData pcd) {
		if (pcd != null) {
			Date now = pcd.getTriggerNow();// new Date()
			Date deadline = pcd.getDeadline();
			if (now.after(deadline)) {
				logger.addDebugStrign(algorithmInstanceNameBase
						+ " miss its deadline scheduled on :" + deadline, true);
			} else {
				logger.addDebugStrign(algorithmInstanceNameBase
						+ " ends in its deadline scheduled on :" + deadline);
			}
			pcm.get(algorithmInstanceNameBase).removeID(pcd);

			for (String s : OFProcedureSynchronisation.getAllInstances()
					.keySet()) {
				List<String> synchInd = OFProcedureSynchronisation
						.getOFProcedureSynchronisation(s).getSynchIndividuals();
				// System.err.println( "addding new Trigger " + synchInd +
				// " contains " + algorithmInstanceNameBase );
				if (synchInd.contains(algorithmInstanceNameBase)) {
					// System.err.println( " sould unblock : " + s);

					/*
					  OWLLibrary.addObjectPropertyB2Individual(	 
					   OWLLibrary.getOWLIndividual( s, ontoRef),
					  OWLLibrary.getOWLObjectProperty( "hasOFProcedureTrigger"
					  , ontoRef), OWLLibrary.getOWLIndividual(
					  "TT_TriggerSynch", ontoRef), false, ontoRef);
					 */

					OFProcedureInterface synch = (OFProcedureInterface) invoker
							.getObject("ProcedureList", s);
					synch.setTimeTrigger(TriggerBuilder
							.newTrigger()
							.withSchedule(
									SimpleScheduleBuilder.simpleSchedule()
											.withIntervalInMilliseconds(50)
											.withRepeatCount(2))
							.withPriority(8).startNow().build());
					synch.setEventResult(true);

					// OFProcedureSynchronisation.removeSynchronisation( s);
					toRemoveTimeTrigger.add(s);
					break;
				}
			}
			if (toRemoveTimeTrigger.contains(algorithmInstanceNameBase)) {
				// toRemoveTimeTrigger.remove( algorithmInstanceNameBase);
				OFProcedureInterface synch = (OFProcedureInterface) invoker
						.getObject("ProcedureList", algorithmInstanceNameBase);
				synch.setEventResult(false);
				synch.setTimeTrigger(null);
			}

		}
	}

	/**
	 * @return the concurrencePoolSize
	 */
	public Integer getConcurrencePoolSize() {
		return concurrencePoolSize;
	}

	/**
	 * @return the ontoRef, the reference to an OWL ontology
	 */
	public OWLReferences getOWLOntologyRefeferences() {
		//return ontoRef;
		//return OWLReferences.getOWLReferences("predefinedOntology");
		JobDataMap data = context.getJobDetail().getJobDataMap();
		String inPar = data.getString(ontoName);
		return( OWLReferences.getOWLReferences(inPar));
	}

	/**
	 * @return the procedure Individual Name
	 */
	public String getProcedureIndividualName() {
		return procedureIndividualName;
	}

	/**
	 * @return the builded list manager containing initialized classes during
	 *         system start up.
	 */
	public OFBuiltMapInvoker getInvoker() {
		return invoker;
	}

	/**
	 * @return the instance of {@link Algorithm} which activates this class.
	 */
	public OFProcedureInterface getAlgorithmCaller() {
		return algorithmCaller;
	}

	/**
	 * @return the algorithm Instance Name Base
	 */
	public String getAlgorithmInstanceNameBase() {
		return algorithmInstanceNameBase;
	}

	/**
	 * @return the algorithm Instance Name (which has the base name and its
	 *         index inside the thread pool).
	 */
	public String getAlgorithmInstanceName() {
		return algorithmInstanceName;
	}

	/**
	 * Add a logging string to the debugger using this logger
	 * 
	 * @param text
	 *            to log
	 */
	public void addLogStrign(String text) {
		logger.addDebugStrign(text);
	}

	/**
	 * Add a logging string to the debugger using this logger, defining if the
	 * log is an error or not.
	 * 
	 * @param text
	 *            to log
	 * @param error
	 *            if it is true than the log is considered as an error, no
	 *            otherwise
	 */
	public void addLogStrign(String text, boolean error) {
		logger.addDebugStrign(text, error);
	}

	/**
	 * This class should contains the implementation of the actual procedure to
	 * been run.
	 * 
	 * @param context
	 *            quartz map to inject data inside a job.
	 * @throws JobExecutionException
	 */
	abstract void runJob(JobExecutionContext context)
			throws JobExecutionException;

}

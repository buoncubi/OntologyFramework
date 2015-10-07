package ontologyFramework.OFDataMapping.reservatedDataType;

import org.semanticweb.owlapi.model.OWLLiteral; 
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;

/**
 * This class represents the default procedure mapping used 
 * from this framework. This requires that default procedure mast
 * be represented in this way, other procedures may use customizable mappers. 
 * To allow so, this class should implement the java object to use to 
 * address to a procedure.
 * 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class Procedure {

	private OWLNamedIndividual scheduler;
	private OWLNamedIndividual event;
	private OWLNamedIndividual timeTrigger;
	private OWLNamedIndividual synchronization;
	private OWLLiteral procedureNameLitteral;
	private String procedureName = null;
	private OWLLiteral concurrrentPoolSizeLittteral;
	private Integer concurrentPoolSize = null;
	private OWLLiteral checkerFreqInMillisecLitteral;
	private Long checkerFreq = null;
	
	/**
	 * Create a new procedure object initialize with some needed quantities.  
	 * 
	 * @param scheduler which address to the individual that describe a 
	 * scheduler in the ontology
	 * @param event top abstraction individual that represent the event for
	 * this procedure. It will run only at timeTriggrt time & if its event result 
	 * true at checking time. 
	 * @param timeTrigger ontological individual that represents the
	 * quartz object to define the next trigger for this procedure.
	 * @param synchronization individual that represents if this procedure should run
	 * only when other procedures have done their work. 
	 * @param procedureName which represent a string with the fully qualifier to 
	 * the implementation of the procedure.
	 * @param concurrrentPoolSize which represents an Integer for the size of the quartz
	 * scheduler pool.
	 * @param checkerFreqInMillisec which represents a Long to describe the frequency
	 * too check the state of the events assign to this object.
	 */
	public Procedure( OWLNamedIndividual scheduler, OWLNamedIndividual event, 
			OWLNamedIndividual timeTrigger, OWLNamedIndividual synchronization, 
			OWLLiteral procedureName, OWLLiteral concurrrentPoolSize, OWLLiteral checkerFreqInMillisec){
		this.scheduler = scheduler;
		this.event = event;
		this.timeTrigger = timeTrigger;
		this.synchronization = synchronization; 
		this.procedureNameLitteral = procedureName;
		if( procedureName != null)
			this.procedureName = procedureName.getLiteral();
		this.concurrrentPoolSizeLittteral = concurrrentPoolSize;
		if( concurrrentPoolSize != null)
			this.concurrentPoolSize = Integer.valueOf( concurrrentPoolSize.getLiteral());
		this.checkerFreqInMillisecLitteral = checkerFreqInMillisec;
		if( checkerFreqInMillisec != null)
			this.checkerFreq = Long.valueOf( checkerFreqInMillisec.getLiteral());
	}
	
	/**
	 * @return the scheduler
	 */
	public OWLNamedIndividual getScheduler() {
		return scheduler;
	}

	/**
	 * @return the event
	 */
	public OWLNamedIndividual getEvent() {
		return event;
	}

	/**
	 * @return the timeTrigger
	 */
	public OWLNamedIndividual getTimeTrigger() {
		return timeTrigger;
	}

	/**
	 * @return the synchronization
	 */
	public OWLNamedIndividual getSynchronization() {
		return synchronization;
	}

	/**
	 * @return the procedureNameLitteral
	 */
	public OWLLiteral getProcedureNameLitteral() {
		return procedureNameLitteral;
	}

	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {
		return procedureName;
	}

	/**
	 * @return the concurrrentPoolSizeLittteral
	 */
	public OWLLiteral getConcurrrentPoolSizeLittteral() {
		return concurrrentPoolSizeLittteral;
	}

	/**
	 * @return the concurrentPoolSize
	 */
	public Integer getConcurrentPoolSize() {
		return concurrentPoolSize;
	}

	/**
	 * @return the checkerFreqInMillisecLitteral
	 */
	public OWLLiteral getCheckerFreqInMillisecLitteral() {
		return checkerFreqInMillisecLitteral;
	}

	/**
	 * @return the getCheckerFreqInMillisec
	 */
	public Long getCheckerFreqInMillisec() {
		return checkerFreq;
	}
	
	/**
	 * Static methods which returns a simple Procedure object.
	 * It will be initialized with respect to the following 
	 * individual:
	 * <pre> 
	 * {@code new Procedure(
	 *			 ontoRef.getOWLIndividual( "Sc_Scheduler1"), 
	 *			 ontoRef.getOWLIndividual( "Ev_CleanerEvent"),
	 *			 null,
	 *			 ontoRef.getOWLIndividual( "P_ReasonerUpdater"),
	 *			 ontoRef.getOWLLiteral( "ontologyFramework.OFProcedureManagment.OFProcedureImplementation.Cleaner"),
	 *	 		 ontoRef.getOWLLiteral( 1L),
	 *			 ontoRef.getOWLLiteral( 100000L))}
	 * </pre>
	 * 
	 * @param ontoRef reference to an OWLOntology
	 * @return a simple reasoner updating procedure.
	 */
	public static Procedure getSimpleCleaner( OWLReferences ontoRef){
		return( new Procedure(
				 ontoRef.getOWLIndividual( "Sc_Scheduler1"), 
				 ontoRef.getOWLIndividual( "Ev_CleanerEvent"),
				 null,
				 ontoRef.getOWLIndividual( "P_ReasonerUpdater"),
				 ontoRef.getOWLLiteral( "ontologyFramework.OFProcedureManagment.OFProcedureImplementation.Cleaner"),
				 ontoRef.getOWLLiteral( 1L),
				 ontoRef.getOWLLiteral( 100000L)));
	}
}

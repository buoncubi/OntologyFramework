package ontologyFramework.OFProcedureManagment;

import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This interface represents a Procedure object.
 * Instances of this interface will be create in accord
 * from {@link OFProcedureBuilder}.
 * An implementation of this Interface is {@link Algorithm}
 * which uses Quartz engine.    
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public interface OFProcedureInterface extends Serializable{
	
	// initialize it to run
	// quartz : retrive frequency and priority
	/**
	 * This method will be called just after all
	 * building times. It should be used to initialize 
	 * variables that need to get data from 
	 * the ontology.    
	 * 
	 * @param procedureInd ontological individual which is reflacting a procedure
	 * @param ontoRef reference to an OWL ontology
	 * @param listInvoker static list manager of builded entity
	 * @param keyWords coming from the builder ontological definition througth data type:
	 * {@code hasTypeKeyWor exactly 1 string}
	 */
	public void initialise(OWLNamedIndividual procedureInd, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker, String[] keyWords);
	
	// run and stop computation
	/**
	 *  Should run this procedure immediately.
	 */
	public void run();
	/**
	 *  Should stop this procedure immediately 
	 *  (or just after is computation, if it is running).
	 */
	public void stop();
	/**
	 *  After a call to this method the procedure should be
	 *  no more schedulable ( at least, as long as a new building time
	 *  occurs).
	 */
	public void shotdown();
	
	// make a run over events and get a result 
	/**
	 * Get the individual name of the Event attached to the ontological
	 * representation of a procedure. It should be able to update 
	 * themselves without call a building mechanism.    
	 * 
	 * @param ontoRef reference to an OWL ontology
	 * @return the individual name of the Event attached to this procedure.
	 */
	public String getEventName( OWLReferences ontoRef); // {1}
	/**
	 * The name returned by: {@link #getEventName(OWLReferences)}
	 * will be used during checking (as an example: 
	 * {@link ontologyFramework.OFProcedureManagment.OFProcedureImplementation.AlgorithmCheckerJob})
	 * time to retrieve their result through the Event list 
	 * ({@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}). When its boolean 
	 * result is available this method is called to deal
	 * with a possible change of event result. So, it should 
	 * use {@link #run()} {@link #stop()} or {@link #shotdown()} to change
	 * the state of the procedure. 
	 * 
	 * @param result the boolean value associate to the Event which name is 
	 * given by {@link #getEventName( OWLReferences)} 
	 */
	void setEventResult(boolean result);		// {2}
	/**
	 * should return the most up to date Event result given by;
	 * {@link #setEventResult(boolean)}
	 * 
	 * @return the boolean value associate to the Event which name is 
	 * given by {@link #getEventName( OWLReferences)}
	 */
	public Boolean getEventResult();		// {3} called in this order
	
	// get TimeTrigger
	/**
	 * Get the individual name of the TimeTrigger attached to the ontological
	 * representation of a procedure. It should be able to update 
	 * themselves without call a building mechanism.    
	 * 
	 * @param ontoRef reference to an OWL ontology
	 * @return the individual name of the TimeTrigger attached to this procedure.
	 */
	public String getTimeTriggerName( OWLReferences ontoRef); // {1}
	/**
	 * The name returned by: {@link #getTimeTriggerName(OWLReferences)}
	 * will be used during checking (as an example: 
	 * {@link ontologyFramework.OFProcedureManagment.OFProcedureImplementation.AlgorithmCheckerJob})
	 * time to retrieve their result through the TimeTrigger list 
	 * ({@link OFBuiltMapInvoker}). When its returnig variable 
	 * is available this method is called to deal
	 * with a possible change of trigger. So, it should 
	 * use {@link #run()} {@link #stop()} or {@link #shotdown()} to change
	 * the state of the procedure. 
	 * 
	 * @param timeTrigger the Trigger Object associate to the TimeTrigger individual
	 * which name is given by {@link #getEventName( OWLReferences)} 
	 */
	public void setTimeTrigger( Object timeTrigger); // {2}
	/**
	 * should return the most up to date Trigger object given by;
	 * {@link #setEventResult(boolean)}
	 * 
	 * @return the Trigger Object associate to the TimeTrigger individual
	 * which name is given by {@link #getEventName( OWLReferences)}
	 */
	public Object getTimeTrigger(); // {3}
	
	
	/**
	 * If a scheduler mechanism is used it should return the 
	 * instance to scheduler object associate to this procedure
	 * 
	 * @return the scheduler
	 */
	public Object getScheduler();
	/**
	 * Since a procedure is always linked to a Checker procedure, which has the 
	 * objective to synchronize ontological changes of the procedure individual
	 * it is convenient to represent the relate Checker object inside this
	 * implementation giving an external access to it througth this method.
	 * 
	 * @return the checker procedure (must be a runnable implementation)
	 */
	public Object getCheckerJob();
	/**
	 * It may be useful to describe a procedure in a way that its
	 * computational steps are in an separate runnable class. This
	 * allows more flexibility in scheduling and building phases.
	 * If this is the case than, this implementaion will define the 
	 * shape of a generic algorithm. Than different Instances of
	 * this class will be related to particolar scripts pointed by
	 * the returning value of this method.
	 * 
	 * @return the runnable implementation of this specific procedure.
	 */
	public Object getMainJob();
}

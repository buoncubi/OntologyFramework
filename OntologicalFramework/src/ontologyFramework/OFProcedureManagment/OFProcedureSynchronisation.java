package ontologyFramework.OFProcedureManagment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * This class contains, for a given procedure individual name,
 * a list of names related to other ontological individuals that represent
 * procedures. In particular,
 * the list represents the name of ontological procedures that
 * have to finish its computation before to run this particular
 * procedure. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OFProcedureSynchronisation {

	private String individualName;
	private List< String> synchIndividuals;
	
	/**
	 * Create a new synchronization object relate to an procedure
	 * individual.
	 * 
	 * @param individualName of the ontological procedure for this instance.
	 */
	public OFProcedureSynchronisation( String individualName){
		this.individualName = individualName;
		synchIndividuals = new ArrayList< String>();
		trackedClass( individualName);
	}
	
	/**
	 * Create a new synchronization object relate to a procedure
	 * individual. Also it initializes it with some synchronization relations
	 * with respect to other procedure. Practically this method
	 * will call {@code this.addSynchronisation( i)} for all {@code i}
	 * into the parameter {@code synchInd}. 
	 * If this class has been already created for the procedure 
	 * defined by individualName, than this method does not create 
	 * a new instance and update the synchronization individuals 
	 * to the already existing one, avoiding to duplicate data.   
	 * 
	 * @param individualName of the ontological procedure for this instance.
	 * @param synchInd set of individual to synchronize
	 */
	public OFProcedureSynchronisation(String individualName,
			Set<OWLNamedIndividual> synchInd) {
		this.individualName = individualName;
		synchIndividuals = new ArrayList< String>();
		if( trackedClass( individualName))
			addSynchronisation( synchInd);
		else{
			OFProcedureSynchronisation synch = getOFProcedureSynchronisation( individualName);
			for( OWLNamedIndividual i : synchInd)
				if( ! synch.getSynchIndividuals().contains( OWLLibrary.getOWLObjectName( i)))
					synch.addSynchronisation( i);
		}
	}

	/**
	 * @return the individual name relate to the procedure assign to this
	 * instance.
	 */
	private String getSyncName() {
		return this.individualName;
	}
	/**
	 * Add an individual to the synchronization list. This means that the
	 * procedure with the individual name defined by {@link #getSyncName()}
	 * before to run has to wait until also the procedure refereed to the individual
	 * given as parameter ends it work.
	 * 
	 * @param ind procedure individual to be synchronized with.
	 */
	public synchronized void addSynchronisation( OWLNamedIndividual ind){
		synchIndividuals.add( OWLLibrary.getOWLObjectName( ind));
	}
	/**
	 * It calls {@link #addSynchronisation(OWLNamedIndividual)} 
	 * for all component inside the set given as input parameter.
	 * 
	 * @param ind set of procedure individual to be synchronized with.
	 */
	public synchronized void addSynchronisation( Set< OWLNamedIndividual> ind){
		for( OWLNamedIndividual i : ind)
			addSynchronisation( i);
	}

	/**
	 * @return the actual list of synchronization individuals
	 */
	public List< String> getSynchIndividuals() {
		return synchIndividuals;
	}

	@Override
	public String toString(){
		return( individualName + " synchronised over : " + synchIndividuals);
	}
	
	// static class tracker. It collects all the instances of this class in an HashMap.
	private static Map<String, OFProcedureSynchronisation> allInstances;  
	static  {  
		allInstances = new HashMap<String, OFProcedureSynchronisation>();  
	}    
	protected void finalize()  {  
		allInstances.values().remove( this);  
	}
	private synchronized boolean trackedClass( String name)  { 
		if( ! allInstances.containsKey( name)){
			allInstances.put( name, this);
			return( true);
		}
		return( false);
	}
	/**
	 * static method that returns all instance of this class created by
	 * the framework. Those are collected in an HashMap with keys equal to the
	 * ontological name of the procedure.
	 *  
	 * @return all created instance of this class.
	 */
	public static synchronized HashMap<String, OFProcedureSynchronisation> getAllInstances(){
		return( ( HashMap<String, OFProcedureSynchronisation>) allInstances );  
	}
	/**
	 * It looks inside the returning value of {@link #getAllInstances()}
	 * and return an istance of this class in accord with its name inside the map.
	 * Basically it just return; 
	 * {@code OFProcedureSynchronisation.getAllInstances.get( referenceName)}
	 * 
	 * @param referenceName the individual name of the procedure for 
	 * which refer to its synchronization list.
	 * @return an instance of this class relate to the procedure defined by
	 * an individual with name: {@code referenceName}
	 */
	public static synchronized OFProcedureSynchronisation getOFProcedureSynchronisation( String referenceName){
		return( allInstances.get( referenceName));
	}
	/**
	 * It remove the instance with individual name equal to the input
	 * parameter from the map that collect all the active instance of
	 * this class. Practically, it just call:
	 * {@code OFProcedureSynchronisation.getAllInstances.remove( synchName)}
	 * 
	 * @param synchName individualName for which the instance of this 
	 * class must be removed.
	 */
	public static synchronized void removeSynchronisation( String synchName){
		allInstances.remove( synchName);
	}
	/**
	 * It remove the instance given as input parameter
	 * from the map that collect all the active instance of
	 * this class. Practically, it just call:
	 * {@code OFProcedureSynchronisation.getAllInstances.remove( synchObj.getSyncName())}
	 *
	 * @param synchObj
	 */
	public static synchronized void removeSynchronisation( OFProcedureSynchronisation synchObj){
		allInstances.remove( synchObj.getSyncName());
	}
}

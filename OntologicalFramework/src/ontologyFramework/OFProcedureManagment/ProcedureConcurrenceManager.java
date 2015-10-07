package ontologyFramework.OFProcedureManagment;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used to initialize and manage a Procedure with its ID which
 * is necessary to manage a concurrent pool approach.
 * Practically, this class represent a List of {@link ProcedureConcurrenceData}
 * with fixed size.
 * Initially all places of the list are null and this means that the pool
 * is empty. Where an instance is running, the first place of the list will
 * be associate to a ProcedureConcurrenceData object and it will means
 * that that place inside the pool is unusable. Than, when the instance
 * Finishes its work its relate place inside the list will go back to null.
 * So, if this list does not contain null places means that the pool
 * is full, namely the procedure cannot run even if all its conditions
 * are satisfied. The ID associate to the procedure will be equal to the index 
 * inside the list represented by this class.
 * 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class ProcedureConcurrenceManager{
	
	private String procedureName;
	private List< ProcedureConcurrenceData> procedureActivated;
	
	/**
	 * Create a new ProcedureConcurrenceManager associate to a particular 
	 * Procedure individual.
	 * 
	 * @param procedureName ontological name of the procedure
	 * @param concurrencyOrder the concurrency pool size for this procedure.
	 */
	public ProcedureConcurrenceManager( String procedureName, Integer concurrencyOrder){
		this.procedureName = procedureName;
		// initializa fix size array to null
		procedureActivated = Arrays.asList( new ProcedureConcurrenceData[ concurrencyOrder]);
		for( int i = 0; i < procedureActivated.size(); i++){
			procedureActivated.set( i, null);
		}	
	}
	
	/**
	 * Returns an new instance of {@link ProcedureConcurrenceData}
	 * initialized with procedureName and the index (ID)
	 * inside the concurrent pool. If the pool is full than this 
	 * method will return null. 
	 * 
	 * @return the procedure data in terms of concurrency if the thread pool is
	 * not full.
	 */
	public synchronized ProcedureConcurrenceData generateID(){
		for( int i = 0; i < procedureActivated.size(); i++){
			if( procedureActivated.get( i) == null){
				ProcedureConcurrenceData pcd = new ProcedureConcurrenceData( procedureName, i);
				procedureActivated.set( i, pcd);
				return( pcd);
			}
		}
		return( null);
	}
	
	/**
	 * remove a procedure from the concurrency pool.
	 * It should be called as soon as the procedure ends.
	 * 
	 * @param pcd concurrency data with respect to a procedure ended that
	 * should be removed from the thread pool.
	 */
	public synchronized void removeID( ProcedureConcurrenceData pcd){
		Integer i = procedureActivated.indexOf( pcd);
		procedureActivated.set( i, null);
	}

	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {
		return procedureName;
	}

	/**
	 * @return the list of all the instances running inside the pool related to 
	 * this procedure.
	 */
	public List<ProcedureConcurrenceData> getProcedureActivated() {
		return procedureActivated;
	}
}

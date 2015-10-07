package ontologyFramework.OFProcedureManagment;

import java.util.Date;

/**
 * This class describes a procedure while is running to 
 * manage dead line and concurrent pool monitoring.
 * Base on this information the system is aware about
 * trigger missing and also it helps in concurrency managing 
 * describing a thread pool implementation. This means that every
 * procedure need an integer value to describe how many
 * instances of the same procedure can run concurrently.  
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 * @see ProcedureConcurrenceManager
 */
public class ProcedureConcurrenceData{
	
	String procedureName;
	Integer ID;
	Date deadline;
	
	public static final String NAMEID_symbSeparator = "-";
	
	/**
	 * Create a new Procedure time tracking. Useful just when
	 * the procedure is running.
	 * 
	 * @param procedureName name of the ontological individual 
	 * wich describe a procedure
	 * @param ID an integer number between 0 and the value defined 
	 * by a particular concurrent pool size
	 */
	public ProcedureConcurrenceData( String procedureName, Integer ID){
		this.procedureName = procedureName; 
		this.ID = ID;
	}
	
	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {
		return procedureName;
	}

	/**
	 * @return the procedure ID
	 */
	public Integer getID() {
		return ID;
	}
	
	@Override
	public String toString(){
		return procedureName + NAMEID_symbSeparator + ID;
	}

	/**
	 * it just returns {@code new Date();}
	 * 
	 * @return the triggerNow
	 */
	public Date getTriggerNow() {
		return( new Date());
	}

	/**
	 * @return the deadline
	 */
	public Date getDeadline() {
		return deadline;
	}

	/** 
	 * @return true if the dead line is set, false otherwise.
	 */
	public boolean isDeadlineSetted(){
		if( deadline == null)
			return( false);
		return( true);
	}
	
	/**
	 * @param deadline the deadline to set
	 */
	public void setDeadline(Date deadline) {
		this.deadline = (Date) deadline.clone();
	}
}

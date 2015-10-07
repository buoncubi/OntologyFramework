package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This class implement a procedure to update the system.
 * Basically it just call:
 * {@code OWLLibrary.synchroniseReasoner( this.getOWLOntologyRefeferences());}
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class ReasonerUpdater extends OFJobAbstract{

	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		//this.getOWLOntologyRefeferences().synchroniseReasoner();
	}

}

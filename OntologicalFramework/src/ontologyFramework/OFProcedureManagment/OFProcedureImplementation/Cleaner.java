package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * This class implement a cleaning procedure. 
 * In particular, it remove from the ontology all 
 * the individual that are inside the class: 
 * {@literal TW-CleanerPool}.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class Cleaner extends OFJobAbstract{

	public Cleaner(){
		super();
	}

	//private static OFDebugLogger logger = new OFDebugLogger( Cleaner.class, true);

	public static final String cleaninigClass = "Tw-cleaner";
	
	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		/*Map<OWLNamedIndividual, OWLReferences> listeners = DataImporterAcc.getListenersMap();
		if( listeners != null){
			for( OWLNamedIndividual li : listeners.keySet()){
				OWLReferences ontoRef = listeners.get( li);
				Set<OWLNamedIndividual> toclean = ontoRef.getIndividualB2Class( cleaninigClass);
				for( OWLNamedIndividual i : toclean){
					ontoRef.removeIndividual( i, false);
				}
				logger.addDebugStrign( "individual " + toclean + " cleaned from ontology: " + ontoRef.getOntoName() + " axiom: " + ontoRef.getOntology().getAxiomCount());
			}
		}*/
		Integer i = 54 * 8;
		//logger.addDebugStrign( "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$44 " + i);
		System.err.println( "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$44" + i);
	}
}

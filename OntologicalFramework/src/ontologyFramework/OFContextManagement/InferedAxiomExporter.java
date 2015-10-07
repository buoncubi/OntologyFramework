package ontologyFramework.OFContextManagement;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * This static class is used to export an ontology.
 * If a Reasoner built asserted property,
 * they will be exported as fixed one.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class InferedAxiomExporter {

	private static boolean importingClosure = true;
	
	// set as non instatiable
	private InferedAxiomExporter() {
        throw new AssertionError();
    }
	
	/**
	 * Given an ontology it changes all the asserted 
	 * property into a fixed one, if any. This method just ask 
	 * for all the asserted entity and make a copy of them. 
	 * Note that this procedure may be computational expensive. 
	 * 
	 * @param ontoRef the ontology to export
	 * @return the input ontology with asserted entity exported
	 */
	public synchronized static OWLReferences exportOntology( OWLReferences ontoRef){
		Set< OWLNamedIndividual> allIndividuals = ontoRef.getIndividualB2Class( ontoRef.getFactory().getOWLThing());
		Set<OWLObjectProperty> allObjProp = ontoRef.getOntology().getObjectPropertiesInSignature( importingClosure);
		Set<OWLDataProperty> allDataProp = ontoRef.getOntology().getDataPropertiesInSignature( importingClosure);
		for( OWLNamedIndividual i : allIndividuals){ //for all individuals belong to the ontology
			exportObjectProperties( allObjProp, i, ontoRef);
			
			exportDataProperties( allDataProp, i, ontoRef);
			
			exportClassAssertion( i, ontoRef);
			
		}
		ontoRef.applyChanges();
		//ontoRef.getReasoner().dispose();
		ontoRef.setPelletReasoner( ontoRef.isBufferingReasoner());
		return( ontoRef);
	}

	private synchronized static void exportObjectProperties( Set< OWLObjectProperty> allProp, OWLNamedIndividual ind, OWLReferences ontoRef){
		synchronized( ontoRef.getReasoner()){
			OWLReasoner reasoner = ontoRef.getReasoner();
			for( OWLObjectProperty p : allProp){ 
				// for all the object property in the ontology
				Set< OWLNamedIndividual> indWithThisProp = reasoner.getObjectPropertyValues(ind, p).getFlattened();
				for( OWLNamedIndividual i : indWithThisProp){
					ontoRef.addObjectPropertyB2Individual( ind, p, i, true); // ad an axiom in the applied change list of ontoRef
				}	
			}
		}
	}
		
	
	private synchronized static void exportDataProperties( Set< OWLDataProperty> allProp, OWLNamedIndividual ind, OWLReferences ontoRef){
		synchronized( ontoRef.getReasoner()){
			OWLReasoner reasoner = ontoRef.getReasoner();
			for( OWLDataProperty p : allProp){ 
				// for all the data property in the ontology
				Set< OWLLiteral> indWithThisProp = reasoner.getDataPropertyValues(ind, p);
				for( OWLLiteral i : indWithThisProp){
					ontoRef.addDataPropertyB2Individual( ind, p, i, true); // ad an axiom in the applied change list of ontoRef
				}	
			}
		}
	}
	
	
	private synchronized static void exportClassAssertion( OWLNamedIndividual ind, OWLReferences ontoRef){
		synchronized( ontoRef.getReasoner()){
			OWLReasoner reasoner = ontoRef.getReasoner();
			Set<OWLClass> allClass = reasoner.getTypes( ind, false).getFlattened();
			for( OWLClass c : allClass){ 
				ontoRef.addIndividualB2Class( ind, c, true); // ad an axiom in the applied change list of ontoRef
			}
		}
	}
	
	
	
	/**
	 * @return the importingClosure
	 */
	public static boolean isImportingClosure() {
		return importingClosure;
	}

	/**
	 * @param importingClosure set to true to import.
	 */
	public static void setImportingClosure(boolean importingClosure) {
		InferedAxiomExporter.importingClosure = importingClosure;
	}	
}

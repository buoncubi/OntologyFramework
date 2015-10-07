package ontologyFramework.OFRunning.OFInitialising;

import java.util.Map;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;


/**
 * This interface is instantiated and called during the initialization phase of the software
 * for the method {@link OFInitialiser#buildIndividual(org.semanticweb.owlapi.model.OWLNamedIndividual, OWLReferences)}.
 * Its proposes is to be used to load classes into the framework.
 * Than, they will be available trough the class: {@link OFBuiltMapInvoker} 
 * as a Map where the initialized class from an implementation of this Procedure can be retrieved
 * based on the list name specified by the builder ontological individual.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */ 
 /* @see ontologyFramework.OFRunning.OFInitialising.OFInitialiser
  @see ontologyFramework.OFRunning.OFInitialising.OFBuilderCommon
  @see ontologyFramework.OFDataMapping.OFDataMapperBuilder
  @see ontologyFramework.OFErrorManagement.OFException.OFExceptionBuilder
  @see ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerBuilder
  @see ontologyFramework.OFEventManagement.OFEventBuilder
  @see ontologyFramework.synchronisingManager.OFSynchroniserBuilder
  @see ontologyFramework.OFProcedureManagment.OFSchedulingBuilder*/
public interface OFBuilderInterface< T extends Object> {
	
	/**
	 * Given references to the ontology, already initialized classes and key words it
	 * discover the ontology to retrieve information and initialize other classes.
	 * A call to this method should clear all stored variables that are used in 
	 * {@link #getInitialisedClasses}  
	 * 
	 * @param keyWords retrieved by {@link OFBuilderCommon#getKeyWords(org.semanticweb.owlapi.model.OWLNamedIndividual, OWLReferences)}
	 * @param ontoRef reference to the ontology which contains the builder individual
	 * @param listInvoker references to already initialized classes from {@link OFInitialiser}
	 */
	public void buildInfo( String[] keyWords, OWLReferences ontoRef, OFBuiltMapInvoker listInvoker);
		
	
	/**
	 * During initialization phase, {@link OFInitialiser} calls {@link #buildInfo(String[], OWLReferences, OFBuiltMapInvoker)}
	 * first and than retries the initialized Map from this method. Its returning value is add to the Map 
	 * managed by {@link OFBuiltMapInvoker} with key value given by {@link OFBuilderCommon#getBuildedListName(org.semanticweb.owlapi.model.OWLNamedIndividual, OWLReferences)}. 
	 * 
	 * @return initialisedMap a Map which contains the initialized classes linked to a key (by default it 
	 * is of type String} 
	 */
	public Map< String, T> getInitialisedObject();

}
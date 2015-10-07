package ontologyFramework.OFDataMapping;

import java.io.Serializable;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLObject;


/// la definizione ontologica del mapper????????????|||||||||||||||||||
/**
 * This class defines the method that must be created into
 * a class to define a one to one Mapper between an Ontological entity and
 * a Data Type. This allows to standardize the methods for all
 * the mapper that can be used in the framework. By default they are
 * created from {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder} and stored in 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 * @param <OntoEntity> Ontological entity to map
 * @param <R> Java Object to map
 */
public interface OFDataMapperInterface <OntoEntity extends OWLObject, R extends Object> extends Serializable{
	
	/**
	 * Get informations from the ontology and returns a Java Object
	 * 
	 * @param entity ontological entity from which retrieve informations. 
	 * @param ontoRef OWL reference to the ontology
	 * @return a Java Object which represent the ontological entity mapped in a Java data type
	 */
	public R mapFromOntology( OntoEntity entity, OWLReferences ontoRef);
	
	/**
	 * Store informations given as java Object into the ontology. It returns 
	 * {@code true} if success.
	 * 
	 * @param entity ontological entity to create, delete or modify into the description 
	 * @param value java Object which represent the data in a particular data-type to be added into the ontology
	 * @param ontoRef OWL references to the ontology
	 * @return true if the operation is successfully completed
	 */
	public boolean mapToOntology( OntoEntity entity, R value, OWLReferences ontoRef);

	/**
	 * Delete from the ontology a particolar entity that is represented
	 * by the given Java Object. 
	 * 
	 * @param entity ontological entity to delete (if it exists) from the description
	 * @param value java Object which represent the data in a particular data-type to be added into the ontology
	 * @param ontoRef OWL references to the ontology
	 * @return true if the operation is successfully completed
	 */
	public boolean removeFromOntology( OntoEntity entity, R value, OWLReferences ontoRef);
	
	// if null remove( ... ) ????????????
	/**
	 * Replace the value of an ontological entity in atomic way. 
	 * Which means that, given an ontological entity E with a particular
	 * property A. The method will assign to A the new value
	 * and will remove the old one with no possibilities for the
	 * reasoner to update the data structure during those operations.
	 * Note that E is given as input while A should be encoded in the 
	 * implementation of the interface. 
	 * 
	 * @param entity ontological entity for which replace the values
	 * @param oldArg value to remove
	 * @param newArg value to add
	 * @param ontoRef OWL references to the ontology
	 * @return true if the operation is successfully completed
	 */
	public boolean replaceIntoOntology( OntoEntity entity, R oldArg, R newArg, OWLReferences ontoRef);
	
	/**
	 * this method is called from {@link OFDataMapperBuilder#buildInfo(String[], OWLReferences, ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker)}
	 * and should be used to store internally the name of interesting
	 * data represented in the ontology. Those data are setted in the
	 * ontology itself and are used all the time the mapper is called.
	 * 
	 * @param kw words used in the ontology
	 */
	public void setKeyWords(String[] kw);
}

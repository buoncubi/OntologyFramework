package ontologyFramework.OFEventManagement;

import java.io.Serializable;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventBuilder;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventDefinition;

/**
 * This class is interface to implement the definition a parameter to be used
 * during event computation. It is instanciated and called by 
 * {@link OFEventParameterDefinition#getParameter()}
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public interface OFEventParameterInterface extends Serializable {

	
	/**
	 * It compute and return a new value of the parameter to be used during
	 * event computation. Inputs are defined by {@link OFEventBuilder} and
	 * {@link OFEventDefinition} and are the first rigth token of a parameter chain
	 * starting from the name of the class that implements this interface.
	 * Example1: {@code exc.AsString} if the implementation is a class called "AsString"
	 * than, on this method {@code input = "exc"}. (String by default.)
	 * Example2: {@code @OntoName exc.AsOWLNamedIndividual.Exist} if the implementation is a class
	 * with name "Exist" than, {@code input = OWLNAMEDINDIVIDUAL_withName_exc}
	 * and "OntoName" is the name associated to the ontology in with the individual "exc"
	 * should be retrieved.
	 * 
	 * @param input of the parameter coming from ontological definition
	 * @param ontoRef ontological reference of this input
	 * @return the actual parameter for event computation.
	 */
	public Object getParameter( Object input, OWLReferences ontoRef);
}

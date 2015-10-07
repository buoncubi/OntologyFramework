package ontologyFramework.OFDataMapping.reservatedDataType;

import java.util.Set;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * This static class is used to represent a Data Type mapper
 * which is by default defined in the framework.
 * 
 * In particular, it is used to map the full 
 * classifier or a Java Class. So, given an ontological individual I
 * which has only one Data-Property:<br>
 * {@code 	I {@value #propName} "full.classifier.toJava.Class"^^string}<br>
 * it returns a string which contains the pat. Namely: 
 * {@code full.classifier.toJava.Class}.
 * 
 * Note that this mechanism is equivalent to the String Mapper
 * but has been divided from him to guarantee more maintainability.
 * Moreover, it does not implement the mapping of a string into the 
 * ontology, but only the reading of such information. This is done
 * to allow the usage of customisable Mapper for such data type
 * without affect the initialization phase of the framework.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class NameMapper {

	// set as non instatiable
	private NameMapper() {
        throw new AssertionError();
    }
	
	/**
	 * Defines the default name of the ontologica Data Property
	 * to map java full qualifier between the system and the data structure. 
	 */
	public static final String NAME_propName = "hasTypeName";
	
	/**
	 * given the name of an individual as a String
	 * it retrieves the actual individual and use it to call
	 * {@link #getNameFromOntology(OWLNamedIndividual, OWLReferences)}.
	 * The returning value is than propagated.
	 * 
	 * @param individualName name of the ontological individual
	 * @param ontoRef OWL references to the ontology
	 * @return the name (full qualifier) of a class
	 */
	public static String getNameFromOntology( String individualName, OWLReferences ontoRef){
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( getNameFromOntology( ind, ontoRef));
	}
	/**
	 * Given an ontological individual which has a data property {@value #NAME_propName}
	 * it returns a string containing the name which should
	 * point to a class through its full qualifier. 
	 * The result can be different if the property
	 * does not contain a string ( see {@link OWLLiteral#getLiteral()} for more).
	 * It returns {@code Null} if the individual does not exist or if
	 * it does not have such data property. 
	 * 
	 * @param individual ontological individual from which retrieve the name
	 * @param ontoRef OWL references to the ontology
	 * @return string stored in the property
	 */
	public static String getNameFromOntology( OWLNamedIndividual individual, OWLReferences ontoRef){
		OWLDataProperty prop = ontoRef.getOWLDataProperty( NAME_propName);	
		Set<OWLLiteral> literals = (Set<OWLLiteral>) ontoRef.getDataPropertyB2Individual( individual, prop);
		String name = ontoRef.getOnlyString(literals);
		return( name);
	}
}

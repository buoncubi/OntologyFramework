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
 * In particular it is able to map an Array of String as:<br>
 * {@code 	strings = ["A" "B" "C" ...]}
 * w.r.t an ontological individual I which as the default DataProperty:<br>
 * {@code	I {@value #propName} "A B C ..."^^string} 
 * 
 * If key words exist in an individual that are builded from the 
 * framework they are mapped with the porpuses to inject names
 * in the builded classes. Basically to make thir coode more
 * general with respect to different ontologies. 
 * 
 * Note that this mapper does not store arrays in the ontology
 * but only read them. In fact, due non trade-safe capability
 * of the ontology, an ArrayMapper should be used for this kind
 * of data types. Anyway this class permits the implementation
 * of customizable approaches to describe the succession of data inside 
 * an array, without affect the initialization procedure
 * of the framework.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 */
public class KeyWordsMapper {
	// set as non instatiable
	private KeyWordsMapper() {
        throw new AssertionError();
    }
	
	/**
	 * Defines the default name of the ontologica Data Property
	 * to map KeyWords between the system and the data structure. 
	 */
	public static final String KEYWORD_propName = "hasTypeKeyWord";
	
	/**
	 * Given the name of an individual it retrieve that specific
	 * individual. Than, it calls {@link #getKeyWordFromOntology(OWLNamedIndividual, OWLReferences)}
	 * and the returning value is propagated.
	 * 
	 * @param individualName name of the ontological individual for which get the key words.
	 * @param ontoRef OWL references to the ontology.
	 * @return an Array of string where every cell contains a key word
	 */
	public static String[] getKeyWordFromOntology( String individualName, OWLReferences ontoRef){
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( getKeyWordFromOntology( ind, ontoRef));
	}
	/**
	 * Given an ontological individual which has a data property {@value #KEYWORD_propName}
	 * it returns an array of string containing, all the word (in each
	 * cell a word separated by " "). The result can be different if the property
	 * does not contain string ( see {@link OWLLiteral#getLiteral()} for more).
	 * It returns {@code Null} if the individual does not exist or if
	 * it does not have such data property. 
	 * 
	 * 
	 * @param individual ontological individual from where retrieve the key words
	 * @param ontoRef OWL references to the ontology
	 * @return the key words collected in an array of strings.
	 */
	public static String[] getKeyWordFromOntology( OWLNamedIndividual individual, OWLReferences ontoRef){
		OWLDataProperty prop = ontoRef.getOWLDataProperty( KEYWORD_propName);
		Set<OWLLiteral> literals = ontoRef.getDataPropertyB2Individual( individual, prop);
		String keyWords = ontoRef.getOnlyString( literals);
		String[] splitedkeyWords = keyWords.split("\\s+");
		return( splitedkeyWords);
	}
}

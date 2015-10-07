package ontologyFramework.OFDataMapping.reservatedDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;


/**
 * This static class is used to implement mapper that are by default
 * used from the framework; at least during start up. For this reason
 * it works only unidirectionally from the ontology to the framework.
 * 
 * In particular, It maps, separately, every part that describe
 * the top abstraction description of an ontological Event. This must
 * be an individual, for instance {@literal I}, that contains data properties, 
 * as for example: {@code I {@value #EVENTCOMPUTATIONLOW_propName} "risult1 && result2"^^string}.
 * Where the function thanks to which the events is evaluated can be written
 * w.r.t to the limitation given by the implementation
 * of {@link ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface}. 
 * Moreover, the variables: result1 and result2
 * must be defined in the same individual as:
 * {@code I hasEventAggregation_result1 Ev_Event1Individual}<br>   
 * {@code I hasEventAggregation_result2 Ev_Event2Individual}.<br>
 * and they can be defined by any string.
 * This relation is done to give the possibility to collect more
 * event in unique boolean result.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */

public class EventComputeLowMapper {
	// set as non instatiable
	private EventComputeLowMapper() {
        throw new AssertionError();
    }
	
	/**
	 * Data property name to define an aggregation of events into the ontology.
	 */
	public static final String EVENTCOMPUTATIONLOW_propName = "hasTypeEventComputeLow";
	private static Set< String> keySymbols = null;
	
	/**
	 * This method semply retrieve the ontological individual from its name
	 * calling: {@link ontoRef#getOWLIndividual(String, OWLReferences)}. Than,
	 * it is used to call: {@link #getNameFromOntology(OWLNamedIndividual, OWLReferences)}.
	 * Finally its returning value is propagated. 
	 * 
	 * @param individualName name of the ontological individual that represent an
	 * aggregations between events.
	 * @param ontoRef references to an OWL ontology.
	 * @return the string that is define in the data property {@value #EVENTCOMPUTATIONLOW_propName}.
	 */
	public static String getNameFromOntology( String individualName, OWLReferences ontoRef){
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( getNameFromOntology( ind, ontoRef));
	}
	
	/**
	 * Returns the string defined in the data property {@value EVENTCOMPUTATIONLOW_propName}
	 * for a particular individual inside the refering ontology.
	 * 
	 * @param individual individual for which locking for the value of the data property.
	 * @param ontoRef references to an OWL ontology.
	 * @return the string that is define in the data property {@value #EVENTCOMPUTATIONLOW_propName}.
	 */
	public static String getNameFromOntology( OWLNamedIndividual individual, OWLReferences ontoRef){
		OWLDataProperty prop = ontoRef.getOWLDataProperty( EVENTCOMPUTATIONLOW_propName);		
		Set<OWLLiteral> literals = (Set<OWLLiteral>) ontoRef.getDataPropertyB2Individual( individual, prop);
		String low = ontoRef.getOnlyString(literals);
		return( low);
	}
	
	/**
	 * Given the low 
	 * it returns the variable names simply removing all the symbols
	 * returned by {@link #getKeySymbols()} and than separating the 
	 * remaining chars by empty spaces.
	 * 
	 * @param low string defined by the data property {@value #EVENTCOMPUTATIONLOW_propName}
	 * @return set of variables name
	 */
	public static Set< String> getVariablesName( String low){
		if( ( keySymbols == null))
			defaultKeySymbols();
		
		// replace symbols with empty space such that only variables name remain
		for( String s : keySymbols){
			low = low.replace( s, " ");
		}
		
		// tokenaize for empty space
		StringTokenizer st = new StringTokenizer( low);
		Set< String> out = new HashSet< String>();
		while (st.hasMoreElements())
			out.add( st.nextElement().toString());
		
		return( out);
	}
	
	/**
	 * @return the symbols that is possible to use
	 * in the aggregation formula specified by the 
	 * data property {@value #EVENTCOMPUTATIONLOW_propName}
	 */
	public static Set<String> getKeySymbols() {
		return keySymbols;
	}
	/**
	 * @param keySymbols symbols that is possible to use
	 * in the aggregation formula specified by the 
	 * data property {@value #EVENTCOMPUTATIONLOW_propName} to set.
	 */
	public static void setKeySymbols(Set<String> keySymbols) {
		EventComputeLowMapper.keySymbols = keySymbols;
	}
	
	/**
	 * Defines the symbols that is possible to use
	 * in the aggregation formula specified by the 
	 * data property {@value #EVENTCOMPUTATIONLOW_propName}
	 */
	private static void defaultKeySymbols(){
		Set< String> keySymbols = new HashSet< String>();
		keySymbols.add( "(");
		keySymbols.add( ")");
		keySymbols.add( "&");
		keySymbols.add( "|");
		keySymbols.add( "!");
		keySymbols.add( "=");
		keySymbols.add( "<");
		keySymbols.add( ">");
		keySymbols.add( "?");
		keySymbols.add( "+");
		keySymbols.add( "-");
		keySymbols.add( "/");
		keySymbols.add( "*");
		setKeySymbols( keySymbols);
	}
}

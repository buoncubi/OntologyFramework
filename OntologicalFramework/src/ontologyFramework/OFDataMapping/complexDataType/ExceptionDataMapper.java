package ontologyFramework.OFDataMapping.complexDataType;

import java.util.Set;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * This class implements a mapping mechanism between an individual
 * and a String to represent a particolar message carried out from
 * an exception. This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved trougth the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}. A further possibility is to build a
 * similar mapper between individual an an Exception Object, eventually
 * defined into the system. Anyway, this has not been done since the
 * behavior of the system when an error occurs could have very
 * different nature. For this reason a particular building process
 * (defined by {@link ontologyFramework.OFErrorManagement.OFException.OFExceptionBuilder}) 
 * has been define for exceptions.
 * 
 * In particular it returns a string pointed by the data property:
 * {@literal hasTypeExceptionMessage}; which is defined by the 
 * second key word assign to the individual: 
 * {@literal M_ExeptionMessageMapper}.
 * This implementation supposes that an individual can have only
 * one property like it. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ExceptionDataMapper implements OFDataMapperInterface< OWLNamedIndividual, String> {
	
	private String[] keyWords;
	
	@Override
	public void setKeyWords(String[] kw) {
		this.keyWords = kw;
	} 
	
	@Override
	public String mapFromOntology(OWLNamedIndividual individual,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		Set<OWLLiteral> values = ontoRef.getDataPropertyB2Individual( individual, prop);
		String value = null;
		for( OWLLiteral v : values){
			value = String.valueOf( v.getLiteral());
			break;
		}
		return value;
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual individual, String value,
			OWLReferences ontoRef) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeFromOntology(OWLNamedIndividual arg, String value,
			OWLReferences ontoRef) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg, String oldArg,
			String newArg, OWLReferences ontoRef) {
		// TODO Auto-generated method stub
		return false;
	}
}

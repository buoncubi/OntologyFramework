package ontologyFramework.OFDataMapping.primitiveDataMapper;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;

/**
 * This class implements {@link ontologyFramework.OFDataMapping.OFDataMapperInterface} between an
 * ontological individual and an Integer. This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved trough the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 * 
 * In particular, using the predefined ontology, any individual can be
 * classified into the class: (Think -> PredefinedOntology ->
 * DataType -> PrimitiveDataType ->) Integer just adding to it
 * a data property as:
 * {@code hasTypeInteger "52"^^integer}.
 * Where the name {@literal hasTypeInteger} is defined by the second
 * key word associate to the builder of the mapper represented by the
 * individual: {@literal M_IntegerMapper} belonging to the ontological
 * class: {@literal OFDataMapper}. 
 *  
 * Moreover, this mapping implementation consider that an individual can
 * have only one data type Integer associated to him. This helps in system
 * flexibility pushing to structure the representation. 
 *   
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class IntegerMapper implements OFDataMapperInterface< OWLNamedIndividual, Integer> {

	private String[] keyWords;
	
	@Override
	public void setKeyWords(String[] kw) {
		this.keyWords = kw;
	} 
	
	@Override
	public Integer mapFromOntology( OWLNamedIndividual individual, OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral values = ontoRef.getOnlyDataPropertyB2Individual( individual, prop);
		if( values != null)
			return( Integer.valueOf( values.getLiteral()));
		return null;
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual arg, Integer value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral(value);
		//synchronized( ontoRef.getReasoner()){
			ontoRef.addDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean removeFromOntology(OWLNamedIndividual arg, Integer value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral(value);
		//synchronized( ontoRef.getReasoner()){
			ontoRef.removeDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg, Integer oldArg,
			Integer newArg, OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral oldl = ontoRef.getOWLLiteral(oldArg);
		OWLLiteral newl = ontoRef.getOWLLiteral(newArg);

		ontoRef.replaceDataProperty(arg, prop, oldl, newl, false);
		return true;
	}
}

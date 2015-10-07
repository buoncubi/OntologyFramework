package ontologyFramework.OFDataMapping.primitiveDataMapper;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;

/**
 * This class implements {@link ontologyFramework.OFDataMapping.OFDataMapperInterface} between an
 * ontological individual and a Boolean. This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved trougth the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 * 
 * In particular, using the predefined ontology, any individual can be
 * classified into the class: (Think -> PredefinedOntology ->
 * DataType -> PrimitiveDataType ->) Bolean just adding to it
 * a data property as:
 * {@code hasTypeBolean "true"^^boolean}.
 * Where the name {@literal hasTypeBoolean} is defined by the second
 * key word associate to the builder of the mapper represented by the
 * individual: {@literal M_BooleanMapper} belonging to the ontological
 * class: {@literal OFDataMapper}. 
 *  
 * This mapping implementation consider that an individual can
 * have only one data type Boolean associated to him. This helps in system
 * flexibility pushing to structure the representation. 
 *  
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class BooleanDataMapper implements OFDataMapperInterface<OWLNamedIndividual, Boolean> {

	private static String[] keyWords;
	public static final String BOOLEANMAPPING_dataProperty = "hasTypeBoolean";//keyWords[ 1];
	
	@Override
	public void setKeyWords(String[] kw) {
		keyWords = kw;
	} 
	
	@Override
	public Boolean mapFromOntology(OWLNamedIndividual individual,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral valueL = ontoRef.getOnlyDataPropertyB2Individual( individual, prop);
		if( valueL != null)
			return( Boolean.valueOf( valueL.getLiteral()));
		return null;
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual arg, Boolean value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral(value);
		//synchronized( ontoRef.getReasoner()){
			ontoRef.addDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean removeFromOntology(OWLNamedIndividual arg, Boolean value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral(value);
		//synchronized( ontoRef.getReasoner()){
			ontoRef.removeDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg,
			Boolean oldArg, Boolean newArg,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral oldl = ontoRef.getOWLLiteral(oldArg);
		OWLLiteral newl = ontoRef.getOWLLiteral(newArg);

		ontoRef.replaceDataProperty(arg, prop, oldl, newl, false);
		return true;
	}

}

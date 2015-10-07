package ontologyFramework.OFDataMapping.primitiveDataMapper;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;

/**
 * This class implements {@link ontologyFramework.OFDataMapping.OFDataMapperInterface} between an
 * ontological individual and a Long variable which represents an
 * Unix time stamp value in milliseconds. This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved trough the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 * 
 * In particular, using the predefined ontology, any individual can be
 * classified into the class: (Think -> PredefinedOntology ->
 * DataType -> PrimitiveDataType ->) TimeInstant just adding to it
 * a data property as:
 * {@code hasTypeTimeStamp "1383042183166"^^long}.
 * Where the name {@literal hasTypeTimeStamp} is defined by the second
 * key word associate to the builder of the mapper represented by the
 * individual: {@literal M_TimeStampMapper} belonging to the ontological
 * class: {@literal OFDataMapper}. 
 *  
 * Different applications may require different representation to 
 * define timing entity into the ontology, anyway this type of 
 * has been adopted since is the most non ambiguous one.  
 * Moreover, this mapping implementation consider that an individual can
 * have only one data type "TimeInstance" associated to him. This helps in system
 * flexibility pushing to structure the representation. 
 *
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class TimeStampDataMapper implements OFDataMapperInterface< OWLNamedIndividual, Long> {

	private String[] keyWords;

	@Override
	public void setKeyWords(String[] kw) {
		this.keyWords = kw;
	} 
	
	@Override
	public Long mapFromOntology(OWLNamedIndividual individual,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral values = ontoRef.getOnlyDataPropertyB2Individual( individual, prop);
		if( values != null)
			return Long.valueOf( values.getLiteral());
		return null;
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual arg, Long value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral(value);
		//synchronized( ontoRef.getReasoner()){
			ontoRef.addDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean removeFromOntology(OWLNamedIndividual arg, Long value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral(value);
		//synchronized( ontoRef.getReasoner()){
			ontoRef.removeDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg, Long oldArg,
			Long newArg, OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral oldl = ontoRef.getOWLLiteral(oldArg);
		OWLLiteral newl = ontoRef.getOWLLiteral(newArg);

		ontoRef.replaceDataProperty(arg, prop, oldl, newl, false);
		return true;
	}

}

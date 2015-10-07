package ontologyFramework.OFDataMapping.primitiveDataMapper;

import java.io.File;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;

/**
 * This class implements {@link ontologyFramework.OFDataMapping.OFDataMapperInterface} between an
 * ontological individual and a File. This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved trougth the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 * 
 * In particular, using the predefined ontology, any individual can be
 * classified into the class: (Think -> PredefinedOntology ->
 * DataType -> PrimitiveDataType ->) File just adding to it
 * a data property as:
 * {@code hasTypeFile "/src/quartz.property"^^string}.
 * Where the name {@literal hasTypeFile} is defined by the second
 * key word associate to the builder of the mapper represented by the
 * individual: {@literal M_FileMapper} belonging to the ontological
 * class: {@literal OFDataMapper}. 
 *  
 * The data property represent as a String the relative path
 * to the file starting from the running directory of the 
 * framework. 
 * Moreover, this mapping implementation consider that an individual can
 * have only one data type File associated to him. This helps in system
 * flexibility pushing to structure the representation. 
 *
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class FileDataType implements OFDataMapperInterface< OWLNamedIndividual, File> {

	private static String[] keyWords;
	
	@Override
	public void setKeyWords(String[] kw) {
		keyWords = kw;
	} 
	
	@Override
	public File mapFromOntology(OWLNamedIndividual individual,
			OWLReferences ontoRef) {
		// get file path
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral value = ontoRef.getOnlyDataPropertyB2Individual( individual, prop);
		String filePath = value.getLiteral();
		if( filePath != null)
			return( new File( filePath));
		return null;
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual arg, File value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral( value.getPath());
		//synchronized( ontoRef.getReasoner()){
			ontoRef.addDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}
	
	@Override
	public boolean removeFromOntology(OWLNamedIndividual arg, File value,
			OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral l = ontoRef.getOWLLiteral( value.getPath());
		//synchronized( ontoRef.getReasoner()){
			ontoRef.removeDataPropertyB2Individual(arg, prop, l, false);
			return true;
		//}
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg, File oldArg,
			File newArg, OWLReferences ontoRef) {
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral oldl = ontoRef.getOWLLiteral(oldArg.getPath());
		OWLLiteral newl = ontoRef.getOWLLiteral(newArg.getPath());

		ontoRef.replaceDataProperty(arg, prop, oldl, newl, false);
		return true;
	}
}

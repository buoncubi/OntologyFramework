package ontologyFramework.OFDataMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.reservatedDataType.KeyWordsMapper;
import ontologyFramework.OFDataMapping.reservatedDataType.NameMapper;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;


/**
 * This class implements the builder for Data Types.
 * 
 * When {@link #buildInfo(String[], OWLReferences, OFBuiltMapInvoker)} is called
 * (by default from {@link ontologyFramework.OFRunning.OFInitialising.OFInitialiser}) it creates
 * and initialise a set of {@link OFBuilderInterface} that are colled in a hasMap with keys 
 * equals to the name of the keyWord at index 0 given from {@link KeyWordsMapper#getKeyWordFromOntology}.
 * When the building process is complete the HasMap is added into the static map available
 * througth {@link OFBuiltMapInvoker} using the method: {@link #getInitialisedObject()}.
 *  
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("rawtypes")
public class OFDataMapperBuilder implements OFBuilderInterface< OFDataMapperInterface> {
	
	private final Map< String, OFDataMapperInterface> initialised = new HashMap< String, OFDataMapperInterface>();

	// get debugger
	private OFDebugLogger logger = new OFDebugLogger( this, true);//DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));
		
	@Override
	public void buildInfo( String[] keyWords, OWLReferences ontoRef, OFBuiltMapInvoker listInvoker) {
		logger.addDebugStrign( "OFDataMapperBuilder starts to build info ...");
		
		initialised.clear();
		//get all the mappers belong to FODataMapper class
		Set<OWLNamedIndividual> mapperInd = ontoRef.getIndividualB2Class( keyWords[ 0]);
		for( OWLNamedIndividual ind : mapperInd){
			// Retrieve the name of the mapper java Class
			/*OWLObjectProperty prop = OWLLibrary.getOWLObjectProperty( "implementsOFDataMapperName", ontoRef);
			Set<OWLNamedIndividual> values = OWLLibrary.getObjectPropertyB2Individual( ind, prop, ontoRef);
			OWLNamedIndividual nameIndividual = (OWLNamedIndividual) OWLLibrary.getOnlyElement( values);
			String name = NameMapper.getNameFromOntology( nameIndividual, ontoRef);*/
			String name = NameMapper.getNameFromOntology( ind, ontoRef);
			
			// Instanciate the mapper
			OFDataMapperInterface<?, ?> mapper = ReflationInstanciater.instanciateOFDataMapperByName( name);
			// get the type of the mapper
			/*OWLObjectProperty typeProp = OWLLibrary.getOWLObjectProperty( keyWords[ 1], ontoRef);
			Set<OWLNamedIndividual> typeIndividuals = OWLLibrary.getObjectPropertyB2Individual( ind, typeProp, ontoRef);
			OWLNamedIndividual typeInd = (OWLNamedIndividual) OWLLibrary.getOnlyElement( typeIndividuals);
			String typeKeyWord = KeyWordsMapper.getKeyWordFromOntology( typeInd, ontoRef)[ 0];*/
			String[] kw = KeyWordsMapper.getKeyWordFromOntology( ind, ontoRef);
			mapper.setKeyWords( kw);
					
			String typeKeyWord = kw[0];
			// add to list
			initialised.put( typeKeyWord, mapper);
			
			//print builded data
			logger.addDebugStrign( " DataMapper " + typeKeyWord + " : " + mapper);
		}
		logger.addDebugStrign( "- . - . - . - . - . - . - . - . - . - . - . - . - . - . -" );
	}

	@Override
	public Map< String, OFDataMapperInterface> getInitialisedObject() {
		return( initialised);
	}
}

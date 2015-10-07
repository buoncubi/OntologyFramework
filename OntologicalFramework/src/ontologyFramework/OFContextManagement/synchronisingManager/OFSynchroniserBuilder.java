package ontologyFramework.OFContextManagement.synchronisingManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFDataMapping.reservatedDataType.NameMapper;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFErrorManagement.OFException.OFExceptionNotifierInterface;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;

public class OFSynchroniserBuilder  implements OFBuilderInterface{

	private final Map<String, OFSynchroniserData> initialised = new HashMap<String, OFSynchroniserData>();
	private final Map<String, OFSynchroniserManagmentInterface> instanciatedSynch = new HashMap<String, OFSynchroniserManagmentInterface>();

	// get debugger
	private OFDebugLogger logger = new OFDebugLogger( this, DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));
			
	
	@Override
	public void buildInfo(String[] keyWords, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker) {
		
		logger.addDebugStrign( "OFSynchroniserBuilder starts to build info ...");
		initialised.clear(); //rebuild all
		// get all the individual inside the class named as in the key word
		Set<OWLNamedIndividual> exceptionInd = ontoRef.getIndividualB2Class( keyWords[ 0]);
		for( OWLNamedIndividual ind : exceptionInd){
			// get individualName :  name of the synchroniser
			String synchName = ontoRef.getOWLObjectName( ind);
			
			// get order of the synchroniser
			OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 1]);
			OWLNamedIndividual value = ontoRef.getOnlyObjectPropertyB2Individual( ind, prop);
			OFDataMapperInterface integerMapper = (OFDataMapperInterface) listInvoker.getObject( "MappersList", "Integer");
			Integer order = (Integer) integerMapper.mapFromOntology( value, ontoRef);
			
			// get the scheduling roles
			// ...
			
			// get the synchronisation roles
			prop = ontoRef.getOWLObjectProperty( keyWords[ 2]);
			OWLNamedIndividual synchManager = ontoRef.getOnlyObjectPropertyB2Individual( ind, prop);
			/*prop = OWLLibrary.getOWLObjectProperty( keyWords[ 3], ontoRef);
			OWLNamedIndividual synchNameInd = OWLLibrary.getOnlyObjectPropertyB2Individual( synchManager, prop, ontoRef);
			String synchClassName = NameMapper.getNameFromOntology( synchNameInd, ontoRef);
			OFSynchroniserManagmentInterface synchInstance = getOFSynchronisatorManager( synchClassName);*/
			String synchClassName = NameMapper.getNameFromOntology( synchManager, ontoRef);
			OFSynchroniserManagmentInterface synchInstance = getOFSynchronisatorManager( synchClassName);
			
			
			// create new synchroniser and add it to synchroniserList
			OFSynchroniserData s = new OFSynchroniserData( order, synchName, ontoRef, synchInstance);
			initialised.put( synchName, s);
		
			// print builded data
			logger.addDebugStrign( " individual: " + synchName + " create synchroniser (order=" + order + ") circular array containing: " + s.getList());
				
		}
		logger.addDebugStrign( "- . - . - . - . - . - . - . - . - . - . - . - . - . - . -" );
	}

	@Override
	public Map<?, ?> getInitialisedObject() {
		return initialised;
	}

	// instance a new only if is not jet instanciate, in this case it retun it
	private OFSynchroniserManagmentInterface getOFSynchronisatorManager( String synchName){
		if( instanciatedSynch.containsKey( synchName))
			return( instanciatedSynch.get( synchName));
		// else instanciate it
		
		OFSynchroniserManagmentInterface synchInstance = ReflationInstanciater.instanciateOFSynchroniseerManagerByName(synchName);
		instanciatedSynch.put( synchName, synchInstance);
		return( synchInstance);
	}
	public void clearInstanciateNotifier(){
		instanciatedSynch.clear();
	}
}

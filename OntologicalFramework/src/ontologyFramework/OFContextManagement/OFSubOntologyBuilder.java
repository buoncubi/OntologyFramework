package ontologyFramework.OFContextManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

public class OFSubOntologyBuilder implements OFBuilderInterface< OWLReferences> {

	private final Map<String, OWLReferences> toBuild = new HashMap<String, OWLReferences>();
	private OFDebugLogger logger = new OFDebugLogger( this, true);// DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));
	
	@Override
	public void buildInfo(String[] keyWords, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker) {
		toBuild.clear();
		logger.addDebugStrign( "OFSubOntologyBuilder starts to build info ...");
		// get all ontology to load
		Set<OWLNamedIndividual> subOntologiesSet = ontoRef.getIndividualB2Class( keyWords[0]);
		for( OWLNamedIndividual subOntologyInd : subOntologiesSet){
			String ontoName = ontoRef.getOnlyDataPropertyB2Individual(subOntologyInd, 
					ontoRef.getOWLDataProperty( keyWords[1])).getLiteral();
			String ontoFilePath = ontoRef.getOnlyDataPropertyB2Individual(subOntologyInd, 
					ontoRef.getOWLDataProperty( keyWords[2])).getLiteral();
			String ontoIRIPath = ontoRef.getOnlyDataPropertyB2Individual(subOntologyInd, 
					ontoRef.getOWLDataProperty( keyWords[3])).getLiteral();
			OWLReferences loadedOnto = new OWLReferences( ontoName, ontoFilePath, ontoIRIPath, OWLReferences.LOADFROMFILEcommand);
			toBuild.put(ontoName, loadedOnto);
			logger.addDebugStrign( " added ontology named: " + ontoName + " with FilePath: " + ontoFilePath + " and IriPath: " + ontoIRIPath);
		}
		logger.addDebugStrign( "- . - . - . - . - . - . - . - . - . - . - . - . - . - . -" );
	}

	@Override
	public Map<String, OWLReferences> getInitialisedObject() {
		return toBuild;
	}

}

package ontologyFramework.OFErrorManagement.OFException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;

/**
 * This class build Exception from is ontological description,
 * which is based in an individual {@literal Ex_Exception} belonging to 
 * the class {@literal Exception}. To do this individual must have 
 * the following proprieties:
 * <pr>
 * 	{@code hasExceptionKill exactily 1 Boolean}
 * 	{@code hasExceptionNotify exactly 1 Boolean}
 * 	{@code hasExceptionBackTraceSteps exactly 1 Integer}
 * 	{@code hasExceptionNotifier exactly 1 ExceptionNotifier}
 * 	{@code hasExceptionMessage "error message"^^string}
 * </pr>
 * Please refer to {@link ExceptionData} for more information
 * about this entity.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OFExceptionBuilder implements OFBuilderInterface{

	private final Map<String, ExceptionData> initialised = new HashMap<String, ExceptionData>();
	private final Map<String, OFExceptionNotifierInterface> instanciatedNotifier = new HashMap<String, OFExceptionNotifierInterface>();
	
	// get debugger
	private OFDebugLogger logger = new OFDebugLogger( this, DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));	
	
	@Override
	public void buildInfo( String[] keyWords, OWLReferences ontoRef, OFBuiltMapInvoker listInvoker) {
		logger.addDebugStrign( "OFExceptionBuilder starts to build info ...");
		
		initialised.clear(); //rebuild all
		// get all the individual inside the class named as in the key word
		Set<OWLNamedIndividual> exceptionInd = ontoRef.getIndividualB2Class( keyWords[ 0]);

		for( OWLNamedIndividual ind : exceptionInd){
			// get individualName
			String indName = ontoRef.getOWLObjectName( ind);
			// get object properties hasExceptionBackTraceStep
			OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 1]);
			OWLNamedIndividual value = ontoRef.getOnlyObjectPropertyB2Individual(ind, prop);
			if( value != null){
				OFDataMapperInterface integerMapper = (OFDataMapperInterface) listInvoker.getObject( "MappersList", "Integer");
				Integer backSteps = (Integer) integerMapper.mapFromOntology( value, ontoRef);
				// get Object properties  hasExceptionKill
				prop = ontoRef.getOWLObjectProperty( keyWords[ 2]);
				value = ontoRef.getOnlyObjectPropertyB2Individual(ind, prop);
				if( value != null){
					OFDataMapperInterface booleanMapper = (OFDataMapperInterface) listInvoker.getObject( "MappersList", "Boolean");
					Boolean kill = (Boolean) booleanMapper.mapFromOntology( value, ontoRef);
					// get Object properties  hasExceptionNotify
					prop = ontoRef.getOWLObjectProperty( keyWords[ 3]);
					value = ontoRef.getOnlyObjectPropertyB2Individual(ind, prop);
					if( value != null){
						booleanMapper = (OFDataMapperInterface) listInvoker.getObject( "MappersList", "Boolean");
						Boolean notify = (Boolean) booleanMapper.mapFromOntology( value, ontoRef);
						// get Data properties  hasTypeString (message)
						OFDataMapperInterface exeptionMessageMapper = (OFDataMapperInterface) listInvoker.getObject( "MappersList", "Exception");
						String message = (String) exeptionMessageMapper.mapFromOntology( ind, ontoRef);
						// get notifier
						prop = ontoRef.getOWLObjectProperty( keyWords[ 5]);
						OWLNamedIndividual notifier = ontoRef.getOnlyObjectPropertyB2Individual( ind, prop);
						if( notifier != null){
							OWLDataProperty propo = ontoRef.getOWLDataProperty( keyWords[ 6]);
							OWLLiteral notifierNameInd = ontoRef.getOnlyDataPropertyB2Individual(notifier, propo);
							OFExceptionNotifierInterface notifierInstance = getOFExceptionNotifier( notifierNameInd.getLiteral());//notifierName);
							
							// create exception
							ExceptionData exD = new ExceptionData( indName, message, notify, kill, backSteps, notifierInstance);
							// add exception to the exceptionList
							initialised.put( indName, exD);
							
							//print builded data
							logger.addDebugStrign( " individual: " + indName + " || message   : " + message + " || kill      : " + kill + " || notify    : " + notify + " || backsteps : " + backSteps);
						} else
							System.out.println( "EXCEPTION4 : excemptionIndividual " + ind + " has not value for : " + prop);
					}else
						System.out.println( "EXCEPTION3 : excemptionIndividual " + ind + " has not value for : " + prop);
				} else
					System.out.println( "EXCEPTION2 : excemptionIndividual " + ind + " has not value for : " + prop);
			} else
				System.out.println( "EXCEPTION1 : excemptionIndividual " + ind + " has not value for : " + prop);
		}
		logger.addDebugStrign( "- . - . - . - . - . - . - . - . - . - . - . - . - . - . -" );
	}

	@Override
	public Map<?, ?> getInitialisedObject() {
		return( initialised);
	}
	
	// instance a new only if is not jet instanciate, in this case it retun it
	private OFExceptionNotifierInterface getOFExceptionNotifier( String notifierName){
		if( instanciatedNotifier.containsKey( notifierName))
			return( instanciatedNotifier.get( notifierName));
		// else instanciate it
		OFExceptionNotifierInterface notifierInstance = ReflationInstanciater.instanciateOFExceptionNotifierByName( notifierName);
		instanciatedNotifier.put( notifierName, notifierInstance);
		return( notifierInstance);
	}
	public void clearInstanciateNotifier(){
		instanciatedNotifier.clear();
	}
	
}

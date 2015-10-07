package ontologyFramework.OFProcedureManagment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.primitiveDataMapper.FileDataType;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class is design to initialize (build) a Quartz scheduler
 * object with respect to the ontology into the framework. It
 * will build an HashMap between the name of an ontological individual 
 * and a {@link org.quartz.Scheduler} which is given by:
 * {@code sch = new StdSchedulerFactory( propertyPath).getScheduler();}.
 * This map will be available than into the static map manager: 
 * {@link OFBuiltMapInvoker}.
 * 
 * By definition the individual which reflect the building mechanism 
 * used in this implementation is:
 * <pre>
 * 	{@code B_SchedulerBuilding € OFBuilder }  
 * 		{@code hasTypeName "ontologyFramework.OFProcedureManagment.OFSchedulingBuilder"^^string}
 * 		{@code buildList "SchedulerList"^^string}      
 * 		{@code hasTypeKeyWord "QuartzScheduler hasQuartzSchedulerProperty"^^string} 
 * </pre>
 * Where the first key word is relate to the name of the ontological
 * class to looking for scheduling individuals. While the second is
 * the name of Object Property which link an individual to its
 * .properties file.  
 * This basically means that to define an individual which will be 
 * one to one relate with a Quartz scheduler object just create it as:
 * <pre>
 * 	{@code S_Scheduler1 € QuartzScheduler }        
 * 		{@code hasQuartzSchedulerProperty exactly 1 FileInd}
 * 			where:{@code FileInd € File}
 * 					{@code KeyFileInd hasTypeFile "/src/sempleSch.property"^^string} 
 * </pre>
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("rawtypes")
public class OFSchedulingBuilder implements OFBuilderInterface {

	private final static Map<String, Scheduler> initialised = new HashMap<String, Scheduler>();
	
	// get debugger
	private OFDebugLogger logger = new OFDebugLogger( this, true);//DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));
			
	
	@Override
	public void buildInfo(String[] keyWords, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker) {
		
		
		logger.addDebugStrign( "OFSchedulerBuilder starts to build info ...");
		initialised.clear(); //rebuild all
		// get all the individual inside the class named as in the key word
		Set<OWLNamedIndividual> exceptionInd = ontoRef.getIndividualB2Class( keyWords[ 0]);
		for( OWLNamedIndividual ind : exceptionInd){
			// for each individual inside the class: QuartzScheduler
			// get the relative path to the quartz.propriety File
			OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 1]);
			OWLNamedIndividual propInd = ontoRef.getOnlyObjectPropertyB2Individual(ind, prop);
			
			FileDataType fileMapper = (FileDataType) listInvoker.getObject( "MappersList", "File");
			String propertyPath = fileMapper.mapFromOntology( propInd, ontoRef).getPath();
			//String propertyPath = NameMapper.getNameFromOntology( propInd, ontoRef);
			
			propertyPath = System.getProperty("user.dir") + propertyPath;
			Scheduler sch;
			try {
				// create new quartze scheduler
				sch = new StdSchedulerFactory( propertyPath).getScheduler();
				// add it to the map
				initialised.put( ontoRef.getOWLObjectName( ind), sch);
				// print builded data
				logger.addDebugStrign( " " + ontoRef.getOWLObjectName( ind) + " : " + sch.getSchedulerName() + " (with .proprieties file at : " + propertyPath + ")");	
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.addDebugStrign( " " + ontoRef.getOWLObjectName( ind) + " falils to initialise scheduler with propriety file at : " + propertyPath );
			}	
		}
		logger.addDebugStrign( "- . - . - . - . - . - . - . - . - . - . - . - . - . - . -" );
	}

	@Override
	public Map<?, ?> getInitialisedObject() {
		return( initialised);
	}
	
	// da aggiungerne altre ?????????????????????????
	/**
	 * shouts down ({@code scheduler.shoutdown()}) all the scheduler builded from
	 * this class during previous building time.
	 */
	public static void shotDownAllScheduler(){
		for( String s : initialised.keySet())
			try {
				initialised.get(s).shutdown();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

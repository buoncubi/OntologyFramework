package ontologyFramework.OFProcedureManagment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class is design to initialize (build) a Procedure
 * object with respect to the ontology into the framework. It
 * will build an HashMap between the name of an ontological individual 
 * and a {@link OFProcedureInterface}.
 * This map will be available than into the static map manager: 
 * {@link OFBuiltMapInvoker}.
 * 
 * By definition the individual which reflect the building mechanism 
 * used in this implementation is:
 * <pre>
 * 	{@code B_OFProcedureBuilder € OFBuilder }  
 * 		{@code hasTypeName "ontologyFramework.OFProcedureManagment.OFProcedureBuilder"^^string}
 * 		{@code buildList "ProcedureList"^^string}      
 * 		{@code hasTypeKeyWord "OFProcedure hasOFProcedureCheckerFrequencyInMilliSeconds} 
 * 			{@code hasOFProcedureScheduler hasOFProcedureEvent hasOFProcedureTrigger}
 * 			{@code hasOFProcedureConcurrentPoolSize hasOFProcedureSynchronisationWith"^^string} 
 * </pre>
 * Where the first key word is relate to the name of the ontological
 * class to looking for procedure individuals. While the other are
 * the name of Object Property which link an individual to its
 * properties.  
 * This basically means that to define an individual which will be 
 * one to one relate with a Procedure object just create it as:
 * <pre>
 * 	{@code P_Procedure1 € OFProcedure}        
 * 		{@code [1] hasTypeName "fullQualifier.ToImplementationOF.OFJobAbstract"}
 * 		{@code [2] hasOFProcedureConcurrentPoolSize "4"^^integer}[]
 * 		{@code [3] hasOFProcedureCheckerFrequencyInMillisecond "200"^^long}
 * 		{@code [4] hasOFProcedureTimeTrigger TT_TriggerNow}
 * 		{@code [5] hasOFProcedureEvent Ev_EventTrue}
 * 		{@code [6] hasOFProcedureSynchronizationWith P_ReasonerUpdater}	 
 * </pre>
 * Where [1] refer to the full Java qualifier to the object that
 * actually implements an procedure; it should extend the class {@link ontologyFramework.OFProcedureManagment.OFProcedureImplementation.OFJobAbstract}.
 * [2] defines the maximum number of instance of the same procedure that 
 * can run at the same time. Moreover, [3] indicates the running period of the
 * checker object, aimed to synchronize the individual P_Procedure1 between its
 * ontological representation and a relate istance of {@link OFProcedureInterface}.
 * [4] and [5] define the only individual that, in turns, defines the TimeTrigger,
 * and Event respectively associate to this procedure. Finally, [5] define if
 * a procedure should wait the end of another befaure to run. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("rawtypes")
public class OFProcedureBuilder implements OFBuilderInterface {
	
	private OFDebugLogger logger = new OFDebugLogger( this, true);//DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));

	private final static Map<String, OFProcedureInterface> toInitialize = new HashMap<String, OFProcedureInterface>();
	
	@Override
	public void buildInfo(String[] keyWords, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker) {
		logger.addDebugStrign( "OFPreocedureBuilder starts to build info ...");
		toInitialize.clear(); //rebuild all
		
		// for all the algorithm individuals, € OFProcedure
		Set<OWLNamedIndividual> algorithmInd = ontoRef.getIndividualB2Class( keyWords[ 0]);
		for( OWLNamedIndividual ind : algorithmInd){
			OFProcedureInterface proc = new Algorithm();
			proc.initialise( ind, ontoRef, listInvoker, keyWords);
			toInitialize.put( OWLLibrary.getOWLObjectName( ind), proc);
		}
	}

	@Override
	public Map<?, ?> getInitialisedObject() {
		return( toInitialize);
	}
	
	/**
	 * It will call {@link OFProcedureInterface#run()} for all the classes
	 * Initialized during the last building time.
	 */
	public static void runAllProcedure(){
		for( String s : toInitialize.keySet()){
			OFProcedureInterface proc = (OFProcedureInterface) toInitialize.get( s);
			proc.run();
		}
	}
	// return Map<String, OFProcedureInterface> 
	/**
	 * It will call {@link OFProcedureInterface#run()} for all the 
	 * individual given as an input parameter
	 * {@code Map< String, OFProcedureInterface>}.
	 * 
	 * @param toRun map of procedure to run.
	 */
	public static void runAllProcedure( Map<String, Object> toRun){	
		for( String s : toRun.keySet()){
			OFProcedureInterface proc = (OFProcedureInterface) toRun.get( s);
			proc.run();
		}
	}
	
	/**
	 * It will look by keys over the last builded
	 * map to find the relate procedure to run.
	 * Than it will call {@link OFProcedureInterface#run()}.
	 * 
	 * @param individualName key name of the class OFProcedureInterface to run.
	 */
	public static void runProcedure( String individualName){
		toInitialize.get( individualName).run();
	}
	/**
	 * It will look by keys over the 
	 * map given as input parameter to find the relate procedure to run.
	 * Than it will call {@link OFProcedureInterface#run()}.
	 * 
	 * @param individualName key of the procedure to run.
	 * @param toRun map between string (name) and OFProcedureInterface.
	 */
	public static  void runProcedure( String individualName, Map<String, Object> toRun){
		((OFProcedureInterface) toRun.get( individualName)).run();
	}
	
	/**
	 * It will call {@link OFProcedureInterface#stop()} for all the classes
	 * Initialized during the last building time.
	 */
	public static  void stopAllProcedure(){
		for( String s : toInitialize.keySet()){
			OFProcedureInterface proc = toInitialize.get( s);
			proc.stop();
		}
	}
	/**
	 * It will call {@link OFProcedureInterface#stop()} for all the 
	 * individual given as an input parameter {@code Map< String, OFProcedureInterface>}.
	 * 
	 * @param toStop map of procedure to stop.
	 */
	public static  void stopAllProcedure( Map<String, Object> toStop){
		for( String s : toStop.keySet()){
			OFProcedureInterface proc = (OFProcedureInterface) toStop.get( s);
			proc.stop();
		}
	}
	
	/**
	 * It will look by keys over the last builded
	 * map to find the relate procedure to stop.
	 * Than it will call {@link OFProcedureInterface#stop()}.
	 * 
	 * @param individualName key name of the class OFProcedureInterface to stop.
	 */
	public  static void stopProcedure( String individualName){
		toInitialize.get( individualName).stop();
	}
	/**
	 * It will look by keys over the 
	 * map given as input parameter to find the relate procedure to stop.
	 * Than it will call {@link OFProcedureInterface#stop()}.
	 * 
	 * @param individualName key of the procedure to stop.
	 * @param toStop map between string (name) and OFProcedureInterface.
	 */
	public  static void stopProcedure( String individualName, Map<String, Object> toStop){
		((OFProcedureInterface) toStop.get( individualName)).stop();
	}
	

}

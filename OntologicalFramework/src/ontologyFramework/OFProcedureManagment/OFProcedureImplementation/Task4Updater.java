package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFProcedureManagment.Algorithm;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;



public class Task4Updater extends OFJobAbstract{

	private static OFDebugLogger logger = new OFDebugLogger( Task4Updater.class, true);

	private static Integer recognitionTimes = 0;

	private static final String COMMONSAVING_filePath = System.getProperty("user.dir") 
			+ "/files/ontologies/SmartHome/evolved/task4/";

	private static String ontoIndividual = "O_Task4DetacherOntology";
	private static final String PERFORMINGVIRTUALSENSOR_indName = "Vs_Performing";
	private static final String PERFORMINGTIME_indName = "C_PerformingTime";
	private static final String PROCEDUREIMPORTER_indNAme = "P_DataImporter";

	private static final String BELONGTW_objProp = "belongsToTimeWindows";
	private static final String IMPORTSFROMFILE_objProp = "importsFromFile";

	private static final String WINDOWSIZE_dataProp = "hasTypeTimeWindowsSize";
	private static final String TIMEINSTANT_dataProp = "hasTypeTimeStamp";
	private static final String TYPEFILE_dataProp = "hasTypeFile";



	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		logger.addDebugStrign( this.getProcedureIndividualName() + " Reasoning Updating and Control");
		
		// get ontology references
		// individual € Ontology class in PredefinedOntology
		//synchronized( this.getOWLOntologyRefeferences().getReasoner()){
		OWLNamedIndividual ontoInd = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual(
						this.getProcedureIndividualName(),	Task4Dethacer.IMPORTTOONTOLOGY_objProp);
		// get name of Ontology
		String ontoName = this.getOWLOntologyRefeferences().getOnlyDataPropertyB2Individual( ontoInd,
				this.getOWLOntologyRefeferences().getOWLDataProperty( Task4Dethacer.ONTOLOGYNAME_dataProp)).getLiteral();
		//}
		// get Ontology by name from list invoker 
		OWLReferences task1OntoRef = OWLReferences.getOWLReferences(ontoName);
		
		//synchronized( task1OntoRef.getReasoner()){
		// Synchronize reasoner
		task1OntoRef.synchroniseReasoner();

		// update performing time
		Set<OWLNamedIndividual> performingWindows = task1OntoRef.getObjectPropertyB2Individual( PERFORMINGVIRTUALSENSOR_indName, BELONGTW_objProp);
		Long performingTime = 0L;
		OWLDataProperty siseProp = task1OntoRef.getOWLDataProperty( WINDOWSIZE_dataProp);
		for( OWLNamedIndividual performingInd : performingWindows){
			performingTime += Long.valueOf( task1OntoRef.getOnlyDataPropertyB2Individual(performingInd, siseProp).getLiteral()); 
		}
		logger.addDebugStrign(" performing time to add " + performingTime + " from " + performingWindows);
		OWLLiteral oldArg = task1OntoRef.getOnlyDataPropertyB2Individual( PERFORMINGTIME_indName, TIMEINSTANT_dataProp);
		OWLNamedIndividual timingInd = task1OntoRef.getOWLIndividual( PERFORMINGTIME_indName);
		OWLDataProperty timingProp = task1OntoRef.getOWLDataProperty( TIMEINSTANT_dataProp);
		OWLLiteral newArg = task1OntoRef.getOWLLiteral( performingTime);
		//synchronized (task1OntoRef.getReasoner()) {
			if( oldArg != null)
				task1OntoRef.replaceDataProperty( timingInd,	timingProp,	oldArg, newArg, false);
			else
				task1OntoRef.addDataPropertyB2Individual( timingInd, timingProp, newArg, false);
		//}
		
		// check if the activity has been recognised and clean ontology saving its state
		Set<OWLNamedIndividual> inds = task1OntoRef.getIndividualB2Class( Task4Dethacer.VIRTUALSENSOR_className);
		logger.addDebugStrign( this.getProcedureIndividualName() +  " activated virtual sensors: " + inds);
		for( OWLNamedIndividual i : inds){
			if( OWLLibrary.getOWLObjectName( i).equals( Task4Dethacer.RECOGNISED_indName)){
				// get test name from dataset file
				OWLNamedIndividual fileInd = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual( PROCEDUREIMPORTER_indNAme, IMPORTSFROMFILE_objProp);
				String completePath = this.getOWLOntologyRefeferences().getOnlyDataPropertyB2Individual(fileInd, 
						this.getOWLOntologyRefeferences().getOWLDataProperty( TYPEFILE_dataProp)).getLiteral();
				String name = completePath.substring( completePath.lastIndexOf( System.getProperty("file.separator")) + 1);
				name = name.replace(".", "_");
				String filePath = COMMONSAVING_filePath + name + "-" + recognitionTimes + ".owl";
				recognitionTimes++;
				OWLLibrary.saveOntology(false, filePath, task1OntoRef);
		
				cleanOntology( this.getInvoker(), ontoName);
					
				logger.addDebugStrign( this.getProcedureIndividualName() +  " activity recognised !!!!!!!! task1Ontology cleaned, copy available in: " + filePath);
				break;
			}
		}		
	}

	
	public static synchronized void cleanOntology(OFBuiltMapInvoker invoker, String ontologyName){
		// reset counter
		Task4Dethacer.setNameCounters( 0);
		Task4Dethacer.setWindowsCount( 0);
		Task4Dethacer.clearTimeline();
		
		// remove and reload ontology
		OWLReferences ontoRefTask = OWLReferences.getOWLReferences("task4Ontology");
		OWLReferences ontoRef = OWLReferences.getOWLReferences("predefinedOntology");
		
		/*OWLNamedIndividual ontoInd = ontoRef.getOWLIndividual( ontoIndividual);
		OWLDataProperty iriProp = ontoRef.getOWLDataProperty( "hasOntologyIRIPath");
		OWLDataProperty fileProp = ontoRef.getOWLDataProperty( "hasOntologyFilePath");
		OWLDataProperty ontoProp = ontoRef.getOWLDataProperty( "hasOntologyName");
		String iriPath = ontoRef.getOnlyDataPropertyB2Individual(ontoInd, iriProp).getLiteral();
		String filePath = ontoRef.getOnlyDataPropertyB2Individual(ontoInd, fileProp).getLiteral();
		String ontoName = ontoRef.getOnlyDataPropertyB2Individual(ontoInd, ontoProp).getLiteral();*/
		String iriPath = ontoRefTask.getOntologyPath();
		String filePath = ontoRefTask.getFilePath();
		String ontoName = ontoRefTask.getOntoName();
		
		logger.addDebugStrign( " cleaning ontology: " + ontoIndividual + " --> " + ontoRefTask.getOntology());
		
		synchronized( ontoRefTask.getReasoner()){
			ontoRefTask.getReasoner().dispose();
			new OWLReferences(ontoName, filePath, iriPath, OWLReferences.LOADFROMFILEcommand);
		}
		/*
		long cleaningTime = System.nanoTime();
	 	synchronized( Algorithm.class){
	 		String predefinedOntoName = ontoRef.getOntoName();
			String predefinedOntoFile = ontoRef.getFilePath();
			String predefinedOntoIri = ontoRef.getOntologyPath();
			
			synchronized( ontoRef){
				ontoRef.getReasoner().dispose();
				new OWLReferences( predefinedOntoName, 
						predefinedOntoFile, 
						predefinedOntoIri, 
						OWLReferences.LOADFROMFILEcommand);
			}
		}
		logger.addDebugStrign( "predefined ontology cleaning in : " + ((System.nanoTime() - cleaningTime)/1000000) + "[ms]");
*/
	}
}
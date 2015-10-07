import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import ontologyFramework.OFContextManagement.InferedAxiomExporter;
import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFGUI.GuiRunner;
import ontologyFramework.OFProcedureManagment.OFProcedureBuilder;
import ontologyFramework.OFProcedureManagment.OFSchedulingBuilder;
import ontologyFramework.OFRunning.OFSerializator;
import ontologyFramework.OFRunning.OFSystemState;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderCommon;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


public class SmartHomeRunner {

	// load ontology parameter
	private static String ontoName = "predefinedOntology";
	private static String filepath = System.getProperty("user.dir") + "/files/ontologies/SmartHome/PredefinedOntology.owl";
	private static String ontopath = "http://www.semanticweb.org/OntologyFramework/SmartHome/PredefinedOntology";
	private static Integer command = OWLReferences.LOADFROMFILEcommand;
	
	// Serialization system parameter
	static String serializationPath = System.getProperty("user.dir") + "/files/ontologies/SmartHome/serialised/";
				
	public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
		// Initialize system through PredefinedOntology
		//load new predefined ontology and serialise system
		OFBuiltMapInvoker listInvoker = SmartHomeRunner.loadReference();
		SmartHomeRunner.serialiseSystem();
				
		//deserialise system
/*		Set<String> savingPaths = new HashSet<String>();
		savingPaths.add( serializationPath + "SerializedMap0.ser");
		OFBuiltMapInvoker listInvoker = SmartHomeRunner.deserialiseSystem( savingPaths);*/
		
		// start scheduling
		Map<String, Object> procs = listInvoker.getMap( "ProcedureList");  
		OFProcedureBuilder.runAllProcedure( procs);
		
		//P_DataInporter hasOFProcedureTrigger TT_TriggerInporter
		// add trigger to procedure to make it runing
		OWLReferences ontoRef = OWLReferences.getOWLReferences( ontoName);
		ontoRef.addObjectPropertyB2Individual("P_DataImporter", 
				"hasOFProcedureTrigger", "TT_TriggerImporter", false);
		ontoRef.getReasoner().flush();		
	
		//Scanner keyIn = new Scanner(System.in);
		// wait to finish
		try {
			while( true){//for( int i = 0; i < 1000 ; i++){ // i< 100 && sleep( 1500 ~= 4hours)
				Scanner keyIn = new Scanner(System.in);
				System.out.println("Press the 'g'+enter key to open GUi");
				String in = keyIn.next();
				//run GUI if individual has true "runsGui" OnjectProperty
				//if( OFDebugLogger.getStartGui() ){
				if( in.equals("g")){	
					Thread t = new Thread( new GuiRunner( ontoName, 40000L, 40000L, 400000L, 400000L));
					t.start();
					System.out.println("opening GUi");
				}
		
				Thread.sleep(1500);
			}
		} catch (InterruptedException e) {}
		
		OFProcedureBuilder.stopAllProcedure();
		OFSchedulingBuilder.shotDownAllScheduler();
	}
	
	public static OFBuiltMapInvoker loadReference(){
		OFBuiltMapInvoker listInvoker = null;
		try {
			OFInitialiser init = new OFInitialiser( ontoName, filepath, ontopath, command);
			listInvoker = init.initialise( );
			
			// static list invoking after initialisation
			String invokerName = init.INVOKER_InstanceName;
			if( listInvoker.equals( OFBuiltMapInvoker.getOFBuildedListInvoker( invokerName)))
					System.out.println( "====================  staticInvoking ==================== ");
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return( listInvoker);
	}
	
	private static void serialiseSystem(){
		// save inferred ontology to file exporting Reasoner inference
		OWLReferences exporter = InferedAxiomExporter.exportOntology( OWLReferences.getOWLReferences(ontoName));
		exporter.setFilePath( serializationPath + "predefinedOntology.owl");
		OWLLibrary.saveOntology( false, exporter);
						
		// serialize initialized classes
		// decide to save only some ontologies, equal for builded classes
		Set<String> ontoToSerializeName = new HashSet<String>();
		ontoToSerializeName.add( ontoName);
		ontoToSerializeName.add( "placingOntology");
		ontoToSerializeName.add( "task1Ontology");
		ontoToSerializeName.add( "task2Ontology");
		ontoToSerializeName.add( "task3Ontology");
		ontoToSerializeName.add( "task4Ontology");
		ontoToSerializeName.add( "task5Ontology");
		ontoToSerializeName.add( "task6Ontology");
		ontoToSerializeName.add( "task7Ontology");
		ontoToSerializeName.add( "task8Ontology");
		// get system state (it saves ontologies)
		Set<OFSystemState> listInvokers = OFSerializator.saveFrameworkState( ontoToSerializeName, null, serializationPath, true);
		// save system state (it serializes OFBuildedListInvoker)
		OFSerializator.serializeObjectToFile( serializationPath, "SerializedMap", listInvokers); 
		//System.err.println( savingPaths);
	}
	
	static OFBuiltMapInvoker deserialiseSystem(Set<String> savingPaths){
		// deserializate
		Set<OFBuiltMapInvoker> allDes = OFSerializator.deserializeOFBuildedListInvoker( savingPaths, true);
		OFBuiltMapInvoker listInvoker = (OFBuiltMapInvoker) allDes.toArray()[0]; // only one in this case*/
		OWLReferences ontoRef = OWLReferences.getOWLReferences(ontoName);
		DebuggingClassFlagData.rebuild( ontoRef);
		OWLNamedIndividual debuggInd = ontoRef.getOnlyIndividualB2Class( OFInitialiser.DEBUGGER_ClassName);
		OFBuilderCommon.buildDebugger( debuggInd, listInvoker, ontoRef);
		return( listInvoker);
	}
}

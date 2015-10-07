import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Scheduler;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import ontologyFramework.OFContextManagement.AxiomImporterCSV;
import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFDataMapping.complexDataType.TimeLine;
import ontologyFramework.OFDataMapping.complexDataType.TimeWindowsDataMapper;
import ontologyFramework.OFDataMapping.reservatedDataType.AbsoluteTimeWindow;
import ontologyFramework.OFDataMapping.reservatedDataType.Procedure;
import ontologyFramework.OFDataMapping.reservatedDataType.TimeWindow;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFErrorManagement.OFException.ExceptionData;
import ontologyFramework.OFErrorManagement.OFGUI.GuiRunner;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventAggregation;
import ontologyFramework.OFEventManagement.OFTimeTriggerManagement.OFTimeTriggerDefinition;
import ontologyFramework.OFProcedureManagment.OFProcedureBuilder;
import ontologyFramework.OFProcedureManagment.OFSchedulingBuilder;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * @author Buoncomapgni Luca
 *
 */
public class runner {

	public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
		test();
		
		// TO CREATE ONTOLOGIES FOR ACCELEROMETERS
		// name definition
		/*String newOntoName = "task7Ontology";
		String folderName = "stairs";
		String name = "stairs";
		String[] csvName = new String[]{ name + "1.csv", name + "2.csv", name + "3.csv"};//, name + "4.csv", name + "5.csv"};
				
		
		
		Set<String> savingPaths = new HashSet<String>();
		savingPaths.add( SmartHomeRunner.serializationPath + "SerializedMap0.ser");
		OFBuiltMapInvoker listInvoker = SmartHomeRunner.deserialiseSystem( savingPaths);
		
		// import csv from ontology
		// path definition
		String filecsv = "files/DataSets/accellerometer/baseFunction/" + folderName +"/";
		String fileOnto = "/Data/school/unige/Master_Thesis/cooding/eclipse/OntologicalFramework/files/ontologies/accelerometer/";
		String ontoPath = "http://www.semanticweb.org/OntologyFramework/accelerometer/" + newOntoName;
		
		// create or load ontology
		boolean created = false;
		String fileOntoName = name + ".owl";
		OWLReferences ontoRef;
		if( created)
			ontoRef = new OWLReferences("imprting", fileOnto + "baseOntology.owl", 
					ontoPath + name, OWLReferences.CREATEcommand);
		else
			ontoRef = new OWLReferences("imprting", fileOnto + "baseOntology.owl", 
					ontoPath, OWLReferences.LOADFROMFILEcommand);
		
		// set property to import
		List< String> dataPropertyName = new ArrayList< String>();
		dataPropertyName.add( "hasXValue");
		dataPropertyName.add( "hasYValue");
		dataPropertyName.add( "hasZValue");
		String individualNames = "Bf_baseValue";
		String className = "BaseFunction";
		
		// import to ontology
		OFDataMapperInterface<OWLNamedIndividual, TimeWindow> twMapper = (OFDataMapperInterface< OWLNamedIndividual, TimeWindow>) 
				listInvoker.getObject("MappersList", "TimeWindow");
		Integer order = 1;
		Long central = 0L;
		Long subCentral = 0L;
		Integer windowsCount = 0;
		Long size = null;
		Integer oldOrder = 0;
		for( int i = 0; i < csvName.length; i++){
			AxiomImporterCSV importer = new AxiomImporterCSV( filecsv + csvName[ i], true, ontoRef);
			// import properties
			order = importer.importToOntology( dataPropertyName, individualNames, className, oldOrder-(i+1));			
			
			// add Global time window
			size = Long.valueOf( order * ( 10000/32));
			if (size % 2 != 0)
				size = size + 1;
			TimeWindow tw = new TimeWindow( size, central - (size / 2));
			central = central - size;
			tw.setClassName( "Tw" + windowsCount);
			tw.setIndividualName( "tw" + windowsCount);
			tw.setRootClass( "TimeRepresentation");
			twMapper.mapToOntology( ontoRef.getOWLIndividual( tw.getIndividualName()), tw, ontoRef);
			
			// add sub time windows
			for( int j = 0; j < order - 1; j++){
				Long subSize = Long.valueOf( 10000/32);
				if ( subSize % 2 != 0)
					subSize = subSize + 1;
				TimeWindow subTw = new TimeWindow( subSize, subCentral - (subSize / 2));
				subCentral = subCentral - subSize;
				subTw.setClassName( "Tw" + windowsCount + "-" + j);
				subTw.setIndividualName( "tw" + windowsCount + "-" + j);
				subTw.setRootClass( tw.getClassName());
				twMapper.mapToOntology( ontoRef.getOWLIndividual( subTw.getIndividualName()), subTw, ontoRef);
			}
			oldOrder += order;
			
			windowsCount = windowsCount + 1;
		}				
		Long day = 2592000000L;//month
		TimeWindow cleaning = new TimeWindow( day, central - (day / 2) );
		cleaning.setClassName( "Tw-cleaner");
		cleaning.setIndividualName( "tw-cleaner");
		cleaning.setRootClass( "TimeRepresentation");
		twMapper.mapToOntology( ontoRef.getOWLIndividual( cleaning.getIndividualName()), cleaning, ontoRef);
		OWLLibrary.saveOntology( false, fileOnto + name + ".owl", ontoRef);
		System.exit(0);*/
	}
	
		
	public static void test()throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
		// initialising
		String serializationPath = System.getProperty("user.dir") + "/bin/Serialized/";
		OFBuiltMapInvoker listInvoker = null;
		String ontoName = "ontoName";

//		String filepath = System.getProperty("user.dir") + "/files/ontologies/tests/interfaces.owl";
		String filepath = System.getProperty("user.dir") + "/files/ontologies/interfacesExported.owl";
		String ontopath = "http://www.semanticweb.org/OntologyFramework/PredefinedOntology/Interfaces";
		try {
			OFInitialiser init = new OFInitialiser( ontoName, filepath, ontopath, OWLReferences.LOADFROMFILEcommand);
			listInvoker = init.initialise( );
			
			// static list invoking after initialisation
			String invokerName = init.INVOKER_InstanceName;
			if( listInvoker.equals( OFBuiltMapInvoker.getOFBuildedListInvoker( invokerName)))
					System.out.println( "====================  staticInvoking ==================== ");
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	/*	
		// save inferred ontology to file exporting Reasoner inference
		OWLReferences exporter = InferedAxiomExporter.exportOntology( OWLReferences.getOWLReferences(ontoName));
		exporter.setFilePath( System.getProperty("user.dir") + "/files/ontologies/interfacesExported0.owl");
		OWLLibrary.saveOntology( false, exporter);
				
		// serialize initialized classes
		// decide to save only some ontologies, equal for builded classes
		Set<String> ontoToSerializeName = new HashSet<String>();
		ontoToSerializeName.add( "ontoName");
		ontoToSerializeName.add( "ontoName(S1-1)");
		ontoToSerializeName.add( "ontoName(S2-2)");
		// get system state (it saves ontologies)
		Set<OFSystemState> listInvokers = OFSerializator.saveFrameworkState( ontoToSerializeName, null, serializationPath, true);
		// save system state (it serializes OFBuildedListInvoker)
		Set<String> savingPaths = OFSerializator.serializeObjectToFile( serializationPath, "SerializedMap", listInvokers); //null, serializationPath
		
		// deserializate
		Set<OFBuildedListInvoker> allDes = OFSerializator.deserializeOFBuildedListInvoker( savingPaths, true);
		listInvoker = (OFBuildedListInvoker) allDes.toArray()[0]; // only one in this case*/
		
		
		// get an event boolean result
		OFEventAggregation event = (OFEventAggregation) listInvoker.getObject( "EventList", "Ev_EventTest");	
		System.out.println(" EVENT RESULT : " + event.compute( listInvoker));
		
		// get time trigger
		OFTimeTriggerDefinition trigger = (OFTimeTriggerDefinition) listInvoker.getObject( "TimeTriggerList", "TT_TriggerFrequently2");
		System.out.println(" TRIGGER RESULT : " + trigger.compute( listInvoker));
		
		// call a mapper
		OFDataMapperInterface booleanMapper = (OFDataMapperInterface) listInvoker.getObject( "MappersList", "Boolean");
		OWLReferences ontoRef = OWLReferences.getOWLReferences( ontoName);
		Boolean bool = (Boolean) booleanMapper.mapFromOntology(
				ontoRef.getOWLIndividual( "falseFlag"), ontoRef);
		System.out.println( " MapperResult : individual falseFlag has boolean : " + bool);
		
		// call an exception
		ExceptionData exD = (ExceptionData) listInvoker.getObject( "ExceptionList", "Ex_Exc");
		exD.notifyException();		
		
		// get synchronized data
		//OFSynchroniserData sD = (OFSynchroniserData) listInvoker.getClassFromList( "ShyncroniserList", "S1");
		//sD.synchronise();
		
		// get a quartz Scheduler
		Scheduler s = ( Scheduler) listInvoker.getObject( "SchedulerList", "Sc_Scheduler1");
		System.out.println( " Schedulers : " + listInvoker.getMap("SchedulerList") );//s);
		
		// create time windows representation 
		//TimeLine timeLine = TimeLine.getSquareTimeLine( 80000L, 0L, 200L, 2,
		TimeLine timeLine = TimeLine.getConstantTimeLine( 60000L, 0L, 20000L,
				"Tw_i", "TW", ontoRef.getOWLClass( "TimeRepresentation"));
		timeLine.setListInvoker(listInvoker);
		timeLine.mapNames( Procedure.getSimpleCleaner( ontoRef)); // ???????????????????????????????????????????
		timeLine.mapToOntology( ontoRef.getOWLIndividual( "P_CleanerApi"), timeLine, ontoRef);
		//timeLine.mapToOntologt(OWLLibrary.getOWLIndividual( "P_CleanerApi", ontoRef), null, ontoRef);
		// get windows representation
		//timeLine.mapToOntologt(null, null, ontoRef);
		//System.out.println( timeLine.mapFromOntology(null, ontoRef).getTimeLine());

		
		// run GUI if individual has true "runsGui" OnjectProperty
		if( OFDebugLogger.getStartGui()){
			Thread t = new Thread( new GuiRunner());
			t.start();
		}
		
		// start scheduling
		Map<String, Object> procs = listInvoker.getMap( "ProcedureList");  
		OFProcedureBuilder.runAllProcedure( procs);
		
		//time windows test
		TimeWindowsDataMapper twMapper = (TimeWindowsDataMapper) listInvoker.getObject( "MappersList", "TimeWindow");
		TimeWindow t = twMapper.mapFromOntology( ontoRef.getOWLIndividual( "Tw_i-1"), ontoRef);
		System.out.print( " time windows individual T_c1: relativeCentre : " + t.getRelativeCentre() + ". windowsSize:" + t.getSize());
		AbsoluteTimeWindow ta = t.getAbsoluteTimeWindows( System.currentTimeMillis());
		System.out.println( " computed for " + ta.getActualClock() + ". Absolute lowerBound: " + ta.getLowerBound() + ". Absolute centre" + ta.getCentralTime() + ". Absolute upperBound" + ta.getUpperBound());
		//TimeWindow tw = new TimeWindow( 1000L, 20L);
		//tw.setClassName( "AAA");
		//twMapper.mapToOntologt( OWLLibrary.getOWLIndividual("AA", ontoRef), (Object) tw, ontoRef);		

		
		try {
			//Thread.sleep(30000);
			//OFDataMapperInterface timeStampMapper = (OFDataMapperInterface) listInvoker.getClassFromList( "MapperList", "timeInstant");
			for( int i = 0; i < 1000 ; i++){ // i< 100 && sleep( 1500 ~= 4hours)
				//timeStampMapper.mapToOntologt( OWLLibrary.getOWLIndividual("AAAAAA", ontoRef), 
						//System.currentTimeMillis(), ontoRef);
				Thread.sleep(15000);
			}
		} catch (InterruptedException e) {}
		
		OFProcedureBuilder.stopAllProcedure();
		OFSchedulingBuilder.shotDownAllScheduler();
	}

}


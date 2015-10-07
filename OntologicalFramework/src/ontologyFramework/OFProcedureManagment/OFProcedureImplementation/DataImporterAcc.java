package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ontologyFramework.OFContextManagement.AxiomImporterCSV;
import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class DataImporterAcc extends OFJobAbstract{

	private static OFDebugLogger logger = new OFDebugLogger( DataImporterAcc.class, true);
	
	public static final String pathToFile = System.getProperty("user.dir") + System.getProperty("file.separator") 
			+ "files/DataSets/accellerometer/allDownChair.csv";//randomSample.csv";
	
	public static final List< String> dataPropertyName = new ArrayList< String>();
	public static final String Impoting_className = "Sensor";
	
	public static final String NEWDATA_individualName = "S_acceleration";
	private Integer newDataNumber = 0;
	
	public static final String TYPETIME_dataProp = "hasTypeTimeStamp";
	private static final String CLOCK_indName = "C_SystClock";
	
	private static final String LISTENEDBY_objProp = "listenedBy";
	public static final String NEWDATA_dataProp = "importsNewData";
	
	public static final Integer reductionRate = 10;
	public static final Integer PERIOD = (1000 / 32) * reductionRate; // in millisec
	
	private static final Object NOISE_LABEL = 0.0;
	
	public static final String SIMULATIONTIMESCALE_dataProp = "importsWithTimeScale";
	private Double temporalRapport = 1.0;
	
	static{
		dataPropertyName.add( "hasXValue"); dataPropertyName.add( "hasYValue"); dataPropertyName.add( "hasZValue");
	}

	private static Lock mutexCleaningOntology = new ReentrantLock();
	
	private static Map< OWLNamedIndividual, OWLReferences> listeners = new HashMap< OWLNamedIndividual, OWLReferences>();
	private long totalDelay = 0;

	private List<List<Double>> dataMatrix;

	
	
	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		int taskTestcount = 0;
		for( String path : getDataSetPath()){
			logger.addDebugStrign("\n\n NEW DATA SET, on " + "." + (taskTestcount++) + " " + path);
			try{
				// chatch temporal rapport for simulation
				String value = this.getOWLOntologyRefeferences()
						.getOnlyDataPropertyB2Individual(
								this.getProcedureIndividualName(),
								SIMULATIONTIMESCALE_dataProp).getLiteral();
				try {
					temporalRapport = Double.valueOf(value);
				} catch (Exception e) {	}
				
				// initialised list of listeners procedure and relative ontology
				Set<OWLNamedIndividual> listene = this.getOWLOntologyRefeferences()
						.getObjectPropertyB2Individual(
								this.getProcedureIndividualName(), LISTENEDBY_objProp);
				for (OWLNamedIndividual i : listene) {
					try{
						OWLObjectProperty ontologyPointer = this.getOWLOntologyRefeferences().getOWLObjectProperty("importsToOntology");
						OWLDataProperty ontoNameProp = this.getOWLOntologyRefeferences().getOWLDataProperty("hasOntologyName");
						
						OWLNamedIndividual ontoInd = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual(i, ontologyPointer);
						String ontoName = this.getOWLOntologyRefeferences().getOnlyDataPropertyB2Individual(ontoInd, ontoNameProp).getLiteral();
						OWLReferences ontoRef = OWLReferences.getOWLReferences(ontoName);
						listeners.put(i, ontoRef);
						ontoRef.synchroniseReasoner();
					} catch (Exception e){
						logger.addDebugStrign( e.getMessage() + " " + e.getStackTrace());
					}
				}
				
				AxiomImporterCSV importer = new AxiomImporterCSV( path, false);
				dataMatrix = importer.manipulateFile();
			
				getFromFile( dataMatrix);
			} catch( Exception e){
				e.printStackTrace();
			}
			
			try {
				Thread.sleep( 120000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// clean all ontologies
			for (OWLNamedIndividual i : listeners.keySet()) {
				OWLReferences reference = listeners.get(i);
				reference.reloadOnrology();
			}
			logger.addDebugStrign( " total delay " + totalDelay);
			totalDelay = 0;
			newDataNumber = 0;
		}
		
		/*while( true){
			try {
				Thread.sleep(40000);
				OFDebugLogger.flush();
				logger.addDebugStrign( "TEST ENDED .....");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		OFDebugLogger.flush();
		System.exit(0);	
	}
	

	private static final Long intitialTime = System.currentTimeMillis();

	static private Integer noiseCount = 0;
	private synchronized Long importData(List<Double> dataRow) {
		logger.addDebugStrign(" Importing data:[" + dataRow.get(0) + ", " + dataRow.get(1) + ", " + dataRow.get(2) + ", " + dataRow.get(3).intValue()  + "] at line "	+  (newDataNumber + 1));
		String name = NEWDATA_individualName + newDataNumber;
		Long time = intitialTime + PERIOD * newDataNumber;//System.currentTimeMillis();
		OWLDataProperty newDataProp = this.getOWLOntologyRefeferences().getOWLDataProperty(NEWDATA_dataProp);
		if( dataRow.get( 3).equals( NOISE_LABEL)){
			if( noiseCount++ > 3){
				for( OWLNamedIndividual li : listeners.keySet()){
					OWLReferences ontoRef = listeners.get( li);
					OWLReferences newOntoRef = ontoRef.reloadOnrology();
				}
				noiseCount = 0;
			}
			return( time);
		}
		for( OWLNamedIndividual li : listeners.keySet()){
			OWLReferences ontoRef = listeners.get( li); 
			int rowCount = 0;
			for( Double r : dataRow){
				if( rowCount >= dataPropertyName.size())
					break;
				// add data into the ontology
				String dataProp = dataPropertyName.get( rowCount); 
				ontoRef.addDataPropertyB2Individual(name, dataProp, r, false);
				ontoRef.addIndividualB2Class( name, Impoting_className , false);
				
				rowCount++;
			}	
			ontoRef.addDataPropertyB2Individual( name, TYPETIME_dataProp, time, false);
			
			logger.addDebugStrign( " add to the individual " + name + " data property " + dataPropertyName + " with value " + dataRow);
			// update clock
			OWLNamedIndividual clock = ontoRef.getOWLIndividual( CLOCK_indName);
			OWLDataProperty timeProp = ontoRef.getOWLDataProperty( TYPETIME_dataProp);
			Set<OWLLiteral> oldValue = ontoRef.getDataPropertyB2Individual( clock , timeProp);
			OWLLiteral newValue = ontoRef.getOWLLiteral( time);
			if( oldValue != null){
				for( OWLLiteral v : oldValue)
					ontoRef.removeDataPropertyB2Individual(clock, timeProp, v, false);
			} 
			//ontoRef.replaceDataProperty(clock, timeProp, oldValue, newValue, false);
			ontoRef.addDataPropertyB2Individual(clock, timeProp, newValue, false);
			
			logger.addDebugStrign(" change syste clock in " + ontoRef.getOntoName() + " in " + time);
			
			OWLLibrary.saveOntology(false, "/Data/school/unige/Master_Thesis/cooding/eclipse/OntologicalFramework/prova.owl", ontoRef);
			
			this.getOWLOntologyRefeferences().addDataPropertyB2Individual( li, newDataProp,
					this.getOWLOntologyRefeferences().getOWLLiteral(Boolean.valueOf(true)),
					false);
		}
		newDataNumber++;
		return( time);
	}
	
	
	public void getFromFile( List< List< Double>> dataMatrix){
			
		for( List< Double> dataRow : dataMatrix){
			Long intialTime = System.currentTimeMillis();
			
			mutexCleaningOntology.lock();
			try{
				// initialised list of listeners procedure and relative ontology
				Set<OWLNamedIndividual> listene = this.getOWLOntologyRefeferences()
						.getObjectPropertyB2Individual(
								this.getProcedureIndividualName(), LISTENEDBY_objProp);
				for (OWLNamedIndividual i : listene) {
					try{
						OWLObjectProperty ontologyPointer = this.getOWLOntologyRefeferences().getOWLObjectProperty("importsToOntology");
						OWLDataProperty ontoNameProp = this.getOWLOntologyRefeferences().getOWLDataProperty("hasOntologyName");
						
						OWLNamedIndividual ontoInd = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual(i, ontologyPointer);
						String ontoName = this.getOWLOntologyRefeferences().getOnlyDataPropertyB2Individual(ontoInd, ontoNameProp).getLiteral();
						OWLReferences ontoRef = OWLReferences.getOWLReferences(ontoName);
						listeners.put(i, ontoRef);
					} catch (Exception ex){
						logger.addDebugStrign( ex.getMessage() + " " + ex.getStackTrace());
					}
				}
			
				// import data into the ontologies
				importData( dataRow);
			}catch( org.semanticweb.owlapi.model.OWLRuntimeException e){
				e.printStackTrace();
			} finally{
				mutexCleaningOntology.unlock();
			}
			
			
			// simulate incoming streaming of data
			Long computationalTime = System.currentTimeMillis();// - intialTime;
			logger.addDebugStrign( "importing ended with computational time " + computationalTime + " [ms]");
			Long simulatedPeriodEnd = intialTime + (long) (PERIOD * temporalRapport);
			if( computationalTime >= simulatedPeriodEnd){
				Long late = computationalTime - simulatedPeriodEnd;  
				logger.addDebugStrign( "importing simulation late !!! " + late + " / " + temporalRapport + " [ms]. Simulated Period: " + (long) (PERIOD * temporalRapport), true);
				totalDelay += late;
			} else {
				Long waiting = simulatedPeriodEnd - computationalTime;
				logger.addDebugStrign("importing simulation wait for " + waiting);
				try {
					Thread.sleep( waiting);
				} catch (InterruptedException e) {
					logger.addDebugStrign( e.getMessage());
				}
			}
		}
	}

	public static synchronized void updateListenersOntology( OWLNamedIndividual ind, OWLReferences ontoRef){
		listeners.put(ind, ontoRef);
	}
	
	private static List< String> getDataSetPath(){
		String base = System.getProperty("user.dir") + "/files/DataSets/accellerometer/";
		List<String> paths = new ArrayList<String>();/*
		paths.add( base + "1-5Reduced_UpBed.csv");
		paths.add( base + "1-5Reduced_DownChair.csv");
		paths.add( base + "1-5Reduced_UpChair.csv");
		paths.add( base + "1-5Reduced_Drink.csv");
		paths.add( base + "1-5Reduced_Pour.csv");
		paths.add( base + "1-5Reduced_Eat.csv");
		paths.add( base + "1-5Reduced_Stairs.csv");
		paths.add( base + "1-5Reduced_Walk.csv"); */
		//paths.add( base + "reducedRandomSample.csv");
		
		
		paths.add( base + "6-13Reduced_UpBed.csv");
		paths.add( base + "6-13Reduced_DownChair.csv");
		paths.add( base + "6-13Reduced_UpChair.csv");
		paths.add( base + "6-13Reduced_Drink.csv"); 	
		paths.add( base + "6-13Reduced_Pour.csv"); 
		paths.add( base + "6-10Reduced_Eat.csv");
		paths.add( base + "6-13Reduced_Stairs.csv");
		paths.add( base + "6-13Reduced_Walk.csv"); 
		paths.add( base + "ReducedRandom1.csv");
		paths.add( base + "ReducedRandom2.csv");
		
		
		/*paths.add( base + "allUpBed.csv");
		paths.add( base + "allDownChair.csv");
		paths.add( base + "allUpChair.csv");
		paths.add( base + "allDownChair.csv"); 
		paths.add( base + "allPour.csv"); 
		paths.add( base + "allEat.csv");
		paths.add( base + "allStairs.csv");
		paths.add( base + "allWalk.csv");*/
		
		return( paths);
	}


	public static synchronized Lock getMutexCleaningOntology() {
		return mutexCleaningOntology;
	}
}

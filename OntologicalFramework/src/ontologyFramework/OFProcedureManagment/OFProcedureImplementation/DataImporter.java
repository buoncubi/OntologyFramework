package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFProcedureManagment.Algorithm;
import ontologyFramework.OFProcedureManagment.OFProcedureBuilder;
import ontologyFramework.OFProcedureManagment.OFProcedureInterface;
import ontologyFramework.OFProcedureManagment.OFSchedulingBuilder;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;

public class DataImporter extends OFJobAbstract {

	private static long totalDelay = 0; 
	
	private static OFDebugLogger logger = new OFDebugLogger(DataImporter.class,
			true);

	private static final String basePath = System.getProperty("user.dir")
			+ "/files/DataSets/homeTest";

	private static Long stateChangesTime = null;

	// reset to false the property of sensor state change at the next incoming
	// data
	private static Set<OWLNamedIndividual> stateReset = new HashSet<OWLNamedIndividual>();

	// data set sensor name symbols
	private static final String motion_symb = "M";
	private static final String item_symb = "I";
	private static final String door_symb = "D";
	private static final String temperature_symb = "T";
	private static final String phone_symb = "P";
	private static final String couldWater_symb = "AD-C";
	private static final String hotWater_symb = "AD-B";
	private static final String burner_symb = "AD-A";
	// ontology description sensorial individual name
	private static final String motion_name = "S_Motion";
	private static final String item_name = "S_Item";
	private static final String door_name = "S_Door";
	private static final String temperature_name = "S_Temperature";
	private static final String phone_name = "S_Phone";
	private static final String burner_name = "S_Ad1A";
	private static final String hotwater_name = "S_Ad1B";
	private static final String couldwater_name = "S_Ad1C";
	// data set value definition
	private static final String motion_trueValue = "ON";
	private static final String motion_falseValue = "OFF";
	private static final String item_trueValue = "PRESENT";
	private static final String item_falseValue = "ABSENT";
	private static final String door_trueValue = "OPEN";
	private static final String door_falseValue = "CLOSE";
	// temperature -> real number
	private static final String phone_trueValue = "START";
	private static final String phone_falseValue = "END";
	// water -> real number
	// burner -> real number

	// ontological informations in predefined ontology and importing file
	// (SOURCE)
	private static final String timeFormat = "yyyy-MM-dd-HH:mm:ss.SSS";
	private static final Double defaultTemporalRapport = 1.0;
	private static final String IMPORTFROMFILE_objProp = "importsFromFile";
	private static final String SIMULATIONTIMESCALE_dataProp = "importsWithTimeScale";
	public static final String IMPORTTOONTOLOGY_objProp = "importsToOntology";
	public static final String ONTOLOGYNAME_dataProp = "hasOntologyName";
	private static final String CHANGESTATEPRESERVINGTIME_dataProp = "stateChangingPreservedInImportingForMillisec";
	private static final String LISTENEDBY_objProp = "listenedBy";
	public static final String NEWDATA_dataProp = "importsNewData";
	// ontological information in placing ontology (DESTINATION)
	private static final String motionValue_dataProp = "hasMotionValue";
	private static final String itemValue_dataProp = "hasItemValue";
	private static final String doorValue_dataProp = "hasDoorValue";
	private static final String temperatureValue_dataProp = "hasTemperatureValue";
	private static final String phoneValue_dataProp = "hasPhoneValue";
	private static final String waterValue_dataProp = "hasWaterValue";
	private static final String burnerValue_dataProp = "hasBurnerValue";
	private static final String STATECHANGED_dataProp = "stateChangesGetSensorLocation";

	private static Integer importCount = 1;

	private static final String notDetachedFilePath = System
			.getProperty("user.dir")
			+ "/files/ontologies/SmartHome/evolved/notDetached/";

	public DataImporter() {
		super();
	}

	@Override
	void runJob(JobExecutionContext context) throws JobExecutionException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("unchecked")
		OFDataMapperInterface<OWLObject, File> fileMapper = (OFDataMapperInterface<OWLObject, File>) this
				.getInvoker().getObject("MappersList", "File");

		// get data set paths
		// List< String> dataPaths = getTask1DataSet();
		// List< String> dataPaths = getTask2DataSet();
		// List< String> dataPaths = getTask3DataSet();
		// List< String> dataPaths = getTask4DataSet();
		// List< String> dataPaths = getTask5DataSet();
		// List<String> dataPaths = getTask6DataSet();
		// List< String> dataPaths = getTask7DataSet();
		// List< String> dataPaths = getTask8DataSet();
		List< String> dataPaths = getTaskInterwovenDataSet();

		for (String s : dataPaths) {
			importCount = 1;
			logger.addDebugStrign("\n\n NEW DATA SET, on " + s);

			// set File indivual to importsFromFile obj property
			OWLObject entity = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual(	this.getProcedureIndividualName(), IMPORTFROMFILE_objProp);
			File oldArg = fileMapper.mapFromOntology(entity, getOWLOntologyRefeferences());
			File newArg = new File(s);
			fileMapper.replaceIntoOntology(entity, oldArg, newArg, getOWLOntologyRefeferences());
			//this.getOWLOntologyRefeferences().synchroniseReasoner();

			try{
				System.out.println( "new run " + s);
				importDataFile();
			}catch(Exception e){
				e.printStackTrace();
				logger.addDebugStrign( e.getCause().toString());
				logger.addDebugStrign( e.getLocalizedMessage());
			}

			// FOR TESTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			// clean if not recognised after save ontology
			try {
				Thread.sleep(25000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// String ontologyTaskName = "task1Ontology";
			// String ontologyTaskName = "task2Ontology";
			// String ontologyTaskName = "task3Ontology";
			// String ontologyTaskName = "task4Ontology";
			// String ontologyTaskName = "task5Ontology";
			// String ontologyTaskName = "task6Ontology";
			// String ontologyTaskName = "task7Ontology";
			// String ontologyTaskName = "task8Ontology";
			// String ontologyTaskName = "taskInterOntology";
			
			int i = 0;
			for( String ontologyTaskName : getOntologyName()){
				OWLReferences taskOntoRef = OWLReferences
						.getOWLReferences(ontologyTaskName);
				OWLLibrary.saveOntology(false,
						notDetachedFilePath +i+"/" + s.substring(s.lastIndexOf("/") + 1)
								 + ".owl", taskOntoRef);
				i++;
			}

			try {
				Thread.sleep(25000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.addDebugStrign( " total delay " + totalDelay);
			totalDelay = 0;
			

			Task1Updater.cleanOntology(getInvoker(), "task1Ontology");
			Task2Updater.cleanOntology(getInvoker(), "task2Ontology");
			Task3Updater.cleanOntology(getInvoker(), "task3Ontology");
			Task4Updater.cleanOntology(getInvoker(), "task4Ontology");
			Task5Updater.cleanOntology(getInvoker(), "task5Ontology");
			Task6Updater.cleanOntology(getInvoker(), "task6Ontology");
			Task7Updater.cleanOntology(getInvoker(), "task7Ontology");
			Task8Updater.cleanOntology(getInvoker(), "task8Ontology");
			
			
			// reset predefined and placing ontology
			OWLReferences placingOntoRef = OWLReferences.getOWLReferences("placingOntology");
			synchronized( placingOntoRef){
				placingOntoRef.getReasoner().dispose();
				new OWLReferences("placingOntology", 
						"/Data/school/unige/Master_Thesis/cooding/eclipse/OntologicalFramework/files/ontologies/SmartHome/PlacingOntology.owl", 
						"http://www.semanticweb.org/OntologyFramework/SmartHome/PlacingOntology", 
						OWLReferences.LOADFROMFILEcommand);
			}
			// clean predefined ontology
		 	long cleaningTime = System.nanoTime();
		 	synchronized( Algorithm.class){
		 		String predefinedOntoName = this.getOWLOntologyRefeferences().getOntoName();
				String predefinedOntoFile = this.getOWLOntologyRefeferences().getFilePath();
				String predefinedOntoIri = this.getOWLOntologyRefeferences().getOntologyPath();
				
				synchronized( this.getOWLOntologyRefeferences()){
					this.getOWLOntologyRefeferences().getReasoner().dispose();
					new OWLReferences( predefinedOntoName, 
							predefinedOntoFile, 
							predefinedOntoIri, 
							OWLReferences.LOADFROMFILEcommand);
				}
			}
			logger.addDebugStrign( "predefined ontology cleaning in : " + ((System.nanoTime() - cleaningTime)/1000000) + "[ms]");
		}

		OFProcedureBuilder.stopAllProcedure();
		OFSchedulingBuilder.shotDownAllScheduler();
		logger.addDebugStrign("TEST ENDED !!!!!!!!!!!!!!!!!!!!!!!!1");
	}

	private void importDataFile() {
		OFBuiltMapInvoker invoker = this.getInvoker();
		// get time to preserve state changes
		Long preservingStateChanges = Long.valueOf(this.getOWLOntologyRefeferences()
				.getOnlyDataPropertyB2Individual(
						this.getProcedureIndividualName(),
						CHANGESTATEPRESERVINGTIME_dataProp).getLiteral());

		// get Ontology name where store data (this class store with replacing)
		OWLNamedIndividual ontoInd = this.getOWLOntologyRefeferences()
				.getOnlyObjectPropertyB2Individual(
						this.getProcedureIndividualName(),
						IMPORTTOONTOLOGY_objProp);
		String ontoName = this.getOWLOntologyRefeferences().getOnlyDataPropertyB2Individual(
				ontoInd,this.getOWLOntologyRefeferences().getOWLDataProperty(ONTOLOGYNAME_dataProp)).getLiteral();
		OWLReferences placingOntoRef = OWLReferences.getOWLReferences(ontoName);

		// get procedures that listen this imported for new incoming data
		Set<OWLNamedIndividual> listeners = this.getOWLOntologyRefeferences()
				.getObjectPropertyB2Individual(
						this.getProcedureIndividualName(), LISTENEDBY_objProp);

		placingOntoRef.synchroniseReasoner();
		
		// get simulation time scale from ontology
		String value = this.getOWLOntologyRefeferences()
				.getOnlyDataPropertyB2Individual(
						this.getProcedureIndividualName(),
						SIMULATIONTIMESCALE_dataProp).getLiteral();
		Double temporalRapport = null;
		try {
			temporalRapport = Double.valueOf(value);
		} catch (Exception e) {
			temporalRapport = defaultTemporalRapport;
		}
		// set to simulate importing delays
		Long previousTime = null;

		// get file to import from ontology
		@SuppressWarnings("unchecked")
		OFDataMapperInterface<OWLObject, File> fileMapper = (OFDataMapperInterface<OWLObject, File>) invoker
				.getObject("MappersList", "File");
		OWLObject entity = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual(
				this.getProcedureIndividualName(), IMPORTFROMFILE_objProp);
		File f = fileMapper.mapFromOntology(entity,
				this.getOWLOntologyRefeferences());
		logger.addDebugStrign("Data Importing from FIle: "
				+ f.getAbsolutePath());

		// open file and read by line
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			fis = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line = reader.readLine();
			long t = System.currentTimeMillis();
			while (line != null) {
				boolean waterOn = false;
				
				// Tokens string by places
				StringTokenizer st = new StringTokenizer(line);
				List<String> tokens = new ArrayList<String>();
				while (st.hasMoreElements())
					tokens.add(st.nextToken().trim());
				// add time to data
				String time = tokens.get(1);// .substring(0, 12);
				tokens.remove(1);
				tokens.set(0, tokens.get(0) + "-" + time);
				// token = [ DataTime, SensorID, value]

				// convert DataTime string to Long Unix time stamp
				Date date = null;
				if (!tokens.get(0).contains("."))
					tokens.set(0, tokens.get(0) + ".000");
				else {
					if (tokens.get(0).substring(tokens.get(0).lastIndexOf("."))
							.length() < 4)
						tokens.set(0, tokens.get(0) + "000");
					tokens.set(
							0,
							tokens.get(0).substring(0,
									tokens.get(0).lastIndexOf(".") + 4));
				}
				// System.err.println("!!!! " + tokens.get(0) + " " +
				// importCount);

				//String timeLogging = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
					date = sdf.parse(tokens.get(0));
					//timeLogging = tokens.get(0);
					tokens.set(0, String.valueOf(date.getTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				//System.err.println( tokens.get(0) + " || " + date + " || " + timeLogging);
				// token = [ TimeStamp, SensorID, value]

				// reset all state changes which holds between an incoming data
				// and the successive
				boolean reset;
				boolean changeState = false;
				if (stateChangesTime != null)
					if (System.currentTimeMillis() - stateChangesTime > preservingStateChanges) {
						for (OWLNamedIndividual i : stateReset) {
							placingOntoRef.removeDataPropertyB2Individual(i, 
									placingOntoRef.getOWLDataProperty(STATECHANGED_dataProp),
									placingOntoRef.getOWLLiteral(!changeState), false);
						}
						reset = true;
					} else
						reset = false;
				else
					reset = true;

				// put data into the system mapping file information
				OWLDataProperty prop = null;
				OWLNamedIndividual ind = null;
				OWLLiteral newValue = null;
				OWLLiteral oldValue = null;
				String testing = tokens.get(1).replaceAll("[0-9]", "");
				if (testing.equals(motion_symb)) {
					prop = placingOntoRef.getOWLDataProperty(motionValue_dataProp);
					String individualName = tokens.get(1).replace(motion_symb,
							motion_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);
					Boolean flag = null;
					if (tokens.get(2).equals(motion_trueValue))
						flag = true;
					else if (tokens.get(2).equals(motion_falseValue))
						flag = false;
					newValue = placingOntoRef.getOWLLiteral(flag);
				} else if (testing.equals(item_symb)) {
					prop = placingOntoRef.getOWLDataProperty(itemValue_dataProp);
					String individualName = tokens.get(1).replace(item_symb,
							item_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);
					Boolean flag = null;
					if (tokens.get(2).equals(item_trueValue))
						flag = true;
					else if (tokens.get(2).equals(item_falseValue))
						flag = false;
					newValue = placingOntoRef.getOWLLiteral(flag);
					if (oldValue != null)
						if (!oldValue.equals(newValue))
							changeState = true;
						else
							changeState = false;
					else
						changeState = true;
				} else if (testing.equals(door_symb)) {
					prop = placingOntoRef.getOWLDataProperty(doorValue_dataProp);
					String individualName = tokens.get(1).replace(door_symb,
							door_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);
					Boolean flag = null;
					if (tokens.get(2).equals(door_trueValue))
						flag = true;
					else if (tokens.get(2).equals(door_falseValue))
						flag = false;
					newValue = placingOntoRef.getOWLLiteral(flag);
					if (oldValue != null)
						if (!oldValue.equals(newValue))
							changeState = true;
						else
							changeState = false;
					else
						changeState = true;
				} else if (testing.equals(temperature_symb)) {
					prop = placingOntoRef.getOWLDataProperty(temperatureValue_dataProp);
					String individualName = tokens.get(1).replace(
							temperature_symb, temperature_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);
					Float temp = Float.valueOf(tokens.get(2));
					newValue = placingOntoRef.getOWLLiteral(temp);
				} else if (testing.equals(phone_symb)) {
					prop = placingOntoRef.getOWLDataProperty(phoneValue_dataProp);
					String individualName = tokens.get(1).replace(phone_symb,
							phone_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);
					Boolean flag = null;
					if (tokens.get(2).equals(phone_trueValue))
						flag = true;
					else if (tokens.get(2).equals(phone_falseValue))
						flag = false;
					newValue = placingOntoRef.getOWLLiteral(flag);
					if (oldValue != null)
						if (!oldValue.equals(newValue))
							changeState = true;
						else
							changeState = false;
					else
						changeState = true;
				} else if (testing.equals(couldWater_symb)) {
					prop = placingOntoRef.getOWLDataProperty(waterValue_dataProp);
					String individualName = testing.replace(couldWater_symb,
							couldwater_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);

					Float flow = Float.valueOf(tokens.get(2));
					/*
					 * Boolean flow = false; if( fl > 0) flow = true; waterOn =
					 * true;
					 */

					newValue = placingOntoRef.getOWLLiteral(flow);
					if (oldValue != null)
						if (!oldValue.equals(newValue))
							changeState = true;
						else
							changeState = false;
					else
						changeState = true;
				} else if (testing.equals(hotWater_symb)) {
					prop = placingOntoRef.getOWLDataProperty(waterValue_dataProp);
					String individualName = testing.replace(hotWater_symb,
							hotwater_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);

					Float flow = Float.valueOf(tokens.get(2));
					/*
					 * Boolean flow = false; if( fl > 0) flow = true; waterOn =
					 * true;
					 */

					newValue = placingOntoRef.getOWLLiteral(flow);
					if (oldValue != null)
						if (!oldValue.equals(newValue))
							changeState = true;
						else
							changeState = false;
					else
						changeState = true;
				} else if (testing.equals(burner_symb)) {
					prop = placingOntoRef.getOWLDataProperty(burnerValue_dataProp);
					String individualName = testing.replace(burner_symb,
							burner_name);
					ind = placingOntoRef.getOWLIndividual(individualName);
					oldValue = placingOntoRef.getOnlyDataPropertyB2Individual(ind,prop);
					Float flow = Float.valueOf(tokens.get(2));
					newValue = placingOntoRef.getOWLLiteral(flow);
					if (oldValue != null)
						if (!oldValue.equals(newValue))
							changeState = true;
						else
							changeState = false;
					else
						changeState = true;
				}

				
				// simulate incoming data waiting for delays
				Long computationTime = System.currentTimeMillis()-t;
				logger.addDebugStrign("importing computational time: " + computationTime + " [ms]");
				if (previousTime != null) {
					if ((date.getTime() - computationTime - previousTime) > 0) {
						try {
							logger.addDebugStrign("importing simulation wait for " + (Math.round((date.getTime() - previousTime)/ temporalRapport) - computationTime));
							Thread.sleep(Math.round((date.getTime() - previousTime)	/ temporalRapport) - computationTime);
						} catch (InterruptedException e) {
							logger.addDebugStrign(e.getMessage(), true);
						} catch( Exception e){
							logger.addDebugStrign(e.getMessage(), true);
							logger.addDebugStrign(e.getStackTrace().toString(), true);
						}
					} else{
						logger.addDebugStrign( "importing simulation late !!! " + (date.getTime() - computationTime - previousTime) + " / " + temporalRapport + " [ms]", true);
						totalDelay += -(date.getTime() - computationTime - previousTime);
					}
				}
				previousTime = date.getTime();
				t = System.currentTimeMillis();
				
				// add state changed object property
				if (changeState == true) {
					placingOntoRef.addDataPropertyB2Individual(ind, 
							placingOntoRef
									.getOWLDataProperty(STATECHANGED_dataProp),
							placingOntoRef.getOWLLiteral(changeState),false);
					stateReset.add(ind);
					if (reset)
						stateChangesTime = System.currentTimeMillis();
				}

				// replace sensor value
				if (oldValue != null)
					placingOntoRef.replaceDataProperty(ind, prop, oldValue,
							newValue, false);
				else
					placingOntoRef.addDataPropertyB2Individual(ind, prop, newValue,
							false);

				// add time stamp value
				@SuppressWarnings({ "unchecked", "rawtypes" })
				OFDataMapperInterface<OWLNamedIndividual, Long> timeMapper = (OFDataMapperInterface) invoker
						.getObject("MappersList", "TimeInstant");
				Long oldArg = timeMapper.mapFromOntology(ind, placingOntoRef);
				if (oldArg != null)
					timeMapper.replaceIntoOntology(ind, oldArg,
							Long.valueOf(tokens.get(0)), placingOntoRef);
				else
					timeMapper.mapToOntology(ind, Long.valueOf(tokens.get(0)),
							placingOntoRef);
				// add last time stamp to Abitant1 individual
				OWLNamedIndividual abitant = placingOntoRef.getOWLIndividual("Abitant1");
				oldArg = timeMapper.mapFromOntology(abitant, placingOntoRef);
				if (oldArg != null)
					timeMapper.replaceIntoOntology(abitant, oldArg,
							Long.valueOf(tokens.get(0)), placingOntoRef);
				else
					timeMapper.mapToOntology(abitant,
							Long.valueOf(tokens.get(0)), placingOntoRef);

				// synchronise reasoner
				placingOntoRef.synchroniseReasoner();

		/*		// display location result given by the reasoner
				Set<OWLNamedIndividual> locatedIn = placingOntoRef.getObjectPropertyB2Individual("Abitant1", "isLocatedIn");
				Set<String> strLocatedIn = new HashSet<String>();
				for (OWLNamedIndividual i : locatedIn) {
					strLocatedIn.add(OWLLibrary.getOWLObjectName(i));
				}
				Set<OWLNamedIndividual> nearTo = placingOntoRef.getObjectPropertyB2Individual("Abitant1", "isNearTo");
				Set<String> strNearTo = new HashSet<String>();
				for (OWLNamedIndividual i : nearTo) {
					strNearTo.add(OWLLibrary.getOWLObjectName(i));
				}
				logger.addDebugStrign(" Importing data:" + tokens + " at line "
						+ importCount++ + "  ||  Abitant1 located in:"
						+ strLocatedIn + "  ||  and near to:" + strNearTo);
		 */		logger.addDebugStrign(" Importing data:" + tokens + " at line "	+ importCount++ );
				
				// add new data flag in the predefined ontology to advise that a
				// new data is incoming
				for (OWLNamedIndividual i : listeners) {
					// prova per integer property
					OWLDataProperty newDataProp = this.getOWLOntologyRefeferences().getOWLDataProperty(NEWDATA_dataProp);
					/*if (i.equals(OWLLibrary.getOWLIndividual("P_Task3Dethacer",
							OWLReferences
									.getOWLReferences("predefinedOntology")))) {
						OWLLiteral waiters = OWLLibrary
								.getOnlyDataPropertyB2Individual(i,
										newDataProp,
										getOWLOntologyRefeferences());
						Integer waits;
						Boolean replaceFlag = true;
						OWLLiteral oldWaitsLit;
						if (waiters != null) {
							waits = Integer.valueOf(waiters.getLiteral());
							oldWaitsLit = OWLLibrary.getOWLLiteral(waits,
									this.getOWLOntologyRefeferences());
							if (waits < 0)
								waits = 0;
							else
								waits++;
						} else {
							waits = 1;
							oldWaitsLit = OWLLibrary.getOWLLiteral(waits,
									this.getOWLOntologyRefeferences());
							replaceFlag = false;
						}
						OWLLiteral waitsLit = OWLLibrary.getOWLLiteral(waits,
								this.getOWLOntologyRefeferences());
						if (replaceFlag) {
							OWLLibrary.replaceDataProperty(i, newDataProp,
									oldWaitsLit, waitsLit, false,
									this.getOWLOntologyRefeferences());
							logger.addDebugStrign("$$$$$$$$   replace"
									+ OWLLibrary.getOWLObjectName(i) + " "
									+ OWLLibrary.getOWLObjectName(newDataProp)
									+ " old : "
									+ OWLLibrary.getOWLObjectName(oldWaitsLit)
									+ " new "
									+ OWLLibrary.getOWLObjectName(waitsLit));
						} else {
							OWLLibrary.addDataPropertyB2Individual(i,
									newDataProp, waitsLit, false,
									this.getOWLOntologyRefeferences());
							logger.addDebugStrign("$$$$$$$$   adding"
									+ OWLLibrary.getOWLObjectName(i) + " "
									+ OWLLibrary.getOWLObjectName(newDataProp)
									+ " "
									+ OWLLibrary.getOWLObjectName(newDataProp));
						}

					} else*/
					
					
					this.getOWLOntologyRefeferences().addDataPropertyB2Individual(i, newDataProp,
								this.getOWLOntologyRefeferences().getOWLLiteral(Boolean.valueOf(true)),
								false);
				}

				// reset water sensor
				/*
				 * if( ! waterOn){ prop =
				 * OWLLibrary.getOWLDataProperty(waterValue_dataProp,
				 * placingOntoRef); String individualName = testing.replace(
				 * couldWater_symb, couldwater_name); ind =
				 * OWLLibrary.getOWLIndividual(individualName, placingOntoRef);
				 * oldValue = OWLLibrary.getOnlyDataPropertyB2Individual(ind,
				 * prop, placingOntoRef); Boolean flow = false; newValue =
				 * OWLLibrary.getOWLLiteral(flow, placingOntoRef); if( oldValue
				 * != null) if( ! oldValue.equals( newValue)) changeState =
				 * true; else changeState = false; else changeState = true;
				 * 
				 * prop = OWLLibrary.getOWLDataProperty(waterValue_dataProp,
				 * placingOntoRef); individualName = testing.replace(
				 * hotWater_symb, hotwater_name); ind =
				 * OWLLibrary.getOWLIndividual(individualName, placingOntoRef);
				 * oldValue = OWLLibrary.getOnlyDataPropertyB2Individual(ind,
				 * prop, placingOntoRef); newValue =
				 * OWLLibrary.getOWLLiteral(flow, placingOntoRef); if( oldValue
				 * != null) if( ! oldValue.equals( newValue)) changeState =
				 * true; else changeState = false; else changeState = true; }
				 */

				// go to the next line of the file
				line = reader.readLine();
			}
		} catch (FileNotFoundException ex) {
			logger.addDebugStrign(ex.getMessage(), true);
		} catch (IOException ex) {
			logger.addDebugStrign(ex.getMessage(), true);
		} finally {
			try {
				reader.close();
				fis.close();

			} catch (IOException ex) {
				logger.addDebugStrign(ex.getMessage(), true);
			}
		}
	}

	private static List<String> getOntologyName(){
		final List<String> ontoNames = new ArrayList<String>();
		ontoNames.add( "task1Ontology");
		ontoNames.add( "task2Ontology");
		ontoNames.add( "task3Ontology");
		ontoNames.add( "task4Ontology");
		ontoNames.add( "task5Ontology");
		ontoNames.add( "task6Ontology");
		ontoNames.add( "task7Ontology");
		ontoNames.add( "task8Ontology");
		return( ontoNames);
	}
	
	// importing files
	private static List<String> getTask1DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t1");
		dataSetPaths.add(basePath + "/p13/p13.t1");
		dataSetPaths.add(basePath + "/p14/p14.t1");
		dataSetPaths.add(basePath + "/p15/p15.t1");
		dataSetPaths.add(basePath + "/p17/p17.t1");
		dataSetPaths.add(basePath + "/p18/p18.t1");
		dataSetPaths.add(basePath + "/p19/p19.t1");
		dataSetPaths.add(basePath + "/p20/p20.t1");
		dataSetPaths.add(basePath + "/p22/p22.t1");
		dataSetPaths.add(basePath + "/p23/p23.t1");
		dataSetPaths.add(basePath + "/p24/p24.t1");
		dataSetPaths.add(basePath + "/p25/p25.t1");
		dataSetPaths.add(basePath + "/p26/p26.t1");
		dataSetPaths.add(basePath + "/p28/p28.t1");
		dataSetPaths.add(basePath + "/p29/p29.t1");
		dataSetPaths.add(basePath + "/p30/p30.t1");
		dataSetPaths.add(basePath + "/p31/p31.t1");
		dataSetPaths.add(basePath + "/p32/p32.t1");
		dataSetPaths.add(basePath + "/p33/p33.t1");
		dataSetPaths.add(basePath + "/p34/p34.t1");
		return (dataSetPaths);
	}

	private static List<String> getTask2DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t2");
		dataSetPaths.add(basePath + "/p13/p13.t2");
		dataSetPaths.add(basePath + "/p14/p14.t2");
		dataSetPaths.add(basePath + "/p15/p15.t2");
		dataSetPaths.add(basePath + "/p17/p17.t2");
		dataSetPaths.add(basePath + "/p18/p18.t2");
		dataSetPaths.add(basePath + "/p19/p19.t2");
		dataSetPaths.add(basePath + "/p20/p20.t2");
		dataSetPaths.add(basePath + "/p22/p22.t2");
		dataSetPaths.add(basePath + "/p23/p23.t2");
		dataSetPaths.add(basePath + "/p24/p24.t2");
		dataSetPaths.add(basePath + "/p25/p25.t2");
		dataSetPaths.add(basePath + "/p26/p26.t2");
		dataSetPaths.add(basePath + "/p27/p27.t2");
		dataSetPaths.add(basePath + "/p28/p28.t2");
		dataSetPaths.add(basePath + "/p29/p29.t2");
		dataSetPaths.add(basePath + "/p30/p30.t2");
		dataSetPaths.add(basePath + "/p31/p31.t2");
		dataSetPaths.add(basePath + "/p32/p32.t2");
		dataSetPaths.add(basePath + "/p33/p33.t2");
		dataSetPaths.add(basePath + "/p34/p34.t2");
		return (dataSetPaths);
	}

	private static List<String> getTask3DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t3");
		dataSetPaths.add(basePath + "/p13/p13.t3");
		dataSetPaths.add(basePath + "/p14/p14.t3");
		dataSetPaths.add(basePath + "/p15/p15.t3");
		dataSetPaths.add(basePath + "/p17/p17.t3");
		dataSetPaths.add(basePath + "/p18/p18.t3");
		dataSetPaths.add(basePath + "/p19/p19.t3");
		dataSetPaths.add(basePath + "/p20/p20.t3");
		dataSetPaths.add(basePath + "/p22/p22.t3");
		dataSetPaths.add(basePath + "/p23/p23.t3");
		dataSetPaths.add(basePath + "/p24/p24.t3");
		dataSetPaths.add(basePath + "/p25/p25.t3");
		dataSetPaths.add(basePath + "/p26/p26.t3");
		dataSetPaths.add(basePath + "/p27/p27.t3");
		dataSetPaths.add(basePath + "/p28/p28.t3");
		dataSetPaths.add(basePath + "/p29/p29.t3");
		dataSetPaths.add(basePath + "/p30/p30.t3");
		dataSetPaths.add(basePath + "/p31/p31.t3");
		dataSetPaths.add(basePath + "/p32/p32.t3");
		dataSetPaths.add(basePath + "/p33/p33.t3");
		dataSetPaths.add(basePath + "/p34/p34.t3");
		return (dataSetPaths);
	}

	private static List<String> getTask4DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t4");
		dataSetPaths.add(basePath + "/p13/p13.t4");
		dataSetPaths.add(basePath + "/p14/p14.t4");
		dataSetPaths.add(basePath + "/p15/p15.t4");
		dataSetPaths.add(basePath + "/p17/p17.t4");
		dataSetPaths.add(basePath + "/p18/p18.t4");
		dataSetPaths.add(basePath + "/p19/p19.t4");
		dataSetPaths.add(basePath + "/p20/p20.t4");
		dataSetPaths.add(basePath + "/p22/p22.t4");
		dataSetPaths.add(basePath + "/p23/p23.t4");
		dataSetPaths.add(basePath + "/p24/p24.t4");
		dataSetPaths.add(basePath + "/p25/p25.t4");
		dataSetPaths.add(basePath + "/p26/p26.t4");
		dataSetPaths.add(basePath + "/p27/p27.t4");
		dataSetPaths.add(basePath + "/p28/p28.t4");
		dataSetPaths.add(basePath + "/p29/p29.t4");
		dataSetPaths.add(basePath + "/p30/p30.t4");
		dataSetPaths.add(basePath + "/p31/p31.t4");
		dataSetPaths.add(basePath + "/p32/p32.t4");
		dataSetPaths.add(basePath + "/p33/p33.t4");
		dataSetPaths.add(basePath + "/p34/p34.t4");
		return (dataSetPaths);
	}

	private static List<String> getTask5DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t5");
		dataSetPaths.add(basePath + "/p13/p13.t5");
		dataSetPaths.add(basePath + "/p14/p14.t5");
		dataSetPaths.add(basePath + "/p15/p15.t5");
		dataSetPaths.add(basePath + "/p17/p17.t5");
		dataSetPaths.add(basePath + "/p18/p18.t5");
		dataSetPaths.add(basePath + "/p19/p19.t5");
		dataSetPaths.add(basePath + "/p20/p20.t5");
		dataSetPaths.add(basePath + "/p22/p22.t5");
		dataSetPaths.add(basePath + "/p23/p23.t5");
		dataSetPaths.add(basePath + "/p24/p24.t5");
		dataSetPaths.add(basePath + "/p25/p25.t5");
		dataSetPaths.add(basePath + "/p26/p26.t5");
		dataSetPaths.add(basePath + "/p27/p27.t5");
		dataSetPaths.add(basePath + "/p28/p28.t5");
		dataSetPaths.add(basePath + "/p29/p29.t5");
		dataSetPaths.add(basePath + "/p30/p30.t5");
		dataSetPaths.add(basePath + "/p31/p31.t5");
		dataSetPaths.add(basePath + "/p32/p32.t5");
		dataSetPaths.add(basePath + "/p33/p33.t5");
		dataSetPaths.add(basePath + "/p34/p34.t5");
		return (dataSetPaths);
	}

	private static List<String> getTask6DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t6");
		dataSetPaths.add(basePath + "/p13/p13.t6");
		dataSetPaths.add(basePath + "/p14/p14.t6");
		dataSetPaths.add(basePath + "/p15/p15.t6");
		dataSetPaths.add(basePath + "/p17/p17.t6");
		dataSetPaths.add(basePath + "/p18/p18.t6");
		dataSetPaths.add(basePath + "/p19/p19.t6");
		dataSetPaths.add(basePath + "/p20/p20.t6");
		dataSetPaths.add(basePath + "/p22/p22.t6");
		dataSetPaths.add(basePath + "/p23/p23.t6");
		dataSetPaths.add(basePath + "/p24/p24.t6");
		dataSetPaths.add(basePath + "/p25/p25.t6");
		dataSetPaths.add(basePath + "/p26/p26.t6");
		dataSetPaths.add(basePath + "/p27/p27.t6");
		dataSetPaths.add(basePath + "/p28/p28.t6");
		dataSetPaths.add(basePath + "/p29/p29.t6");
		dataSetPaths.add(basePath + "/p30/p30.t6");
		dataSetPaths.add(basePath + "/p31/p31.t6");
		dataSetPaths.add(basePath + "/p32/p32.t6");
		dataSetPaths.add(basePath + "/p33/p33.t6");
		dataSetPaths.add(basePath + "/p34/p34.t6");
		return (dataSetPaths);
	}

	private static List<String> getTask7DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t7");
		dataSetPaths.add(basePath + "/p13/p13.t7");
		dataSetPaths.add(basePath + "/p14/p14.t7");
		dataSetPaths.add(basePath + "/p15/p15.t7");
		dataSetPaths.add(basePath + "/p17/p17.t7");
		dataSetPaths.add(basePath + "/p18/p18.t7");
		dataSetPaths.add(basePath + "/p19/p19.t7");
		dataSetPaths.add(basePath + "/p20/p20.t7");
		dataSetPaths.add(basePath + "/p22/p22.t7");
		dataSetPaths.add(basePath + "/p23/p23.t7");
		dataSetPaths.add(basePath + "/p24/p24.t7");
		dataSetPaths.add(basePath + "/p25/p25.t7");
		dataSetPaths.add(basePath + "/p26/p26.t7");
		dataSetPaths.add(basePath + "/p27/p27.t7");
		dataSetPaths.add(basePath + "/p28/p28.t7");
		dataSetPaths.add(basePath + "/p29/p29.t7");
		dataSetPaths.add(basePath + "/p30/p30.t7");
		dataSetPaths.add(basePath + "/p31/p31.t7");
		dataSetPaths.add(basePath + "/p32/p32.t7");
		dataSetPaths.add(basePath + "/p33/p33.t7");
		dataSetPaths.add(basePath + "/p34/p34.t7");
		return (dataSetPaths);
	}

	private static List<String> getTask8DataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.t8");
		dataSetPaths.add(basePath + "/p13/p13.t8");
		dataSetPaths.add(basePath + "/p14/p14.t8");
		dataSetPaths.add(basePath + "/p15/p15.t8");
		dataSetPaths.add(basePath + "/p17/p17.t8");
		dataSetPaths.add(basePath + "/p18/p18.t8");
		dataSetPaths.add(basePath + "/p19/p19.t8");
		dataSetPaths.add(basePath + "/p20/p20.t8");
		dataSetPaths.add(basePath + "/p22/p22.t8");
		dataSetPaths.add(basePath + "/p23/p23.t8");
		dataSetPaths.add(basePath + "/p24/p24.t8");
		dataSetPaths.add(basePath + "/p25/p25.t8");
		dataSetPaths.add(basePath + "/p26/p26.t8");
		dataSetPaths.add(basePath + "/p27/p27.t8");
		dataSetPaths.add(basePath + "/p28/p28.t8");
		dataSetPaths.add(basePath + "/p29/p29.t8");
		dataSetPaths.add(basePath + "/p30/p30.t8");
		dataSetPaths.add(basePath + "/p31/p31.t8");
		dataSetPaths.add(basePath + "/p32/p32.t8");
		dataSetPaths.add(basePath + "/p33/p33.t8");
		dataSetPaths.add(basePath + "/p34/p34.t8");
		return (dataSetPaths);
	}

	private static List<String> getTaskInterwovenDataSet() {
		final List<String> dataSetPaths = new ArrayList<String>();
		dataSetPaths.add(basePath + "/p04/p04.interwoven");
		dataSetPaths.add(basePath + "/p13/p13.interwoven");
		dataSetPaths.add(basePath + "/p14/p14.interwoven");
		dataSetPaths.add(basePath + "/p15/p15.interwoven");
		dataSetPaths.add(basePath + "/p17/p17.interwoven");
		dataSetPaths.add(basePath + "/p18/p18.interwoven");
		dataSetPaths.add(basePath + "/p19/p19.interwoven");
		dataSetPaths.add(basePath + "/p20/p20.interwoven");
		dataSetPaths.add(basePath + "/p23/p23.interwoven");
		dataSetPaths.add(basePath + "/p24/p24.interwoven");
		dataSetPaths.add(basePath + "/p25/p25.interwoven");
		dataSetPaths.add(basePath + "/p26/p26.interwoven");
		dataSetPaths.add(basePath + "/p27/p27.interwoven");
		dataSetPaths.add(basePath + "/p28/p28.interwoven");
		dataSetPaths.add(basePath + "/p29/p29.interwoven");
		dataSetPaths.add(basePath + "/p30/p30.interwoven");
		dataSetPaths.add(basePath + "/p31/p31.interwoven");
		dataSetPaths.add(basePath + "/p32/p32.interwoven");
		dataSetPaths.add(basePath + "/p33/p33.interwoven");
		dataSetPaths.add(basePath + "/p34/p34.interwoven");
		return (dataSetPaths);
	}
}

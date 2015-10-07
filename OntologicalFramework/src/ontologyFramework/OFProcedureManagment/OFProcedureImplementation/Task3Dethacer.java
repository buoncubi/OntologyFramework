package ontologyFramework.OFProcedureManagment.OFProcedureImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFDataMapping.reservatedDataType.TimeWindow;
import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyChange;

public class Task3Dethacer extends OFJobAbstract{

	private static OFDebugLogger logger = new OFDebugLogger(Task3Dethacer.class, true);

	// counter to create different names for different individuals
	private static Integer nameCounter = 0;
	private static Integer nameCounter2 = 0;
	private static Integer windowsCount = 0;

	
	// interesting data to retrieve from plcingOntology for this task
	private static final String DOOR11_indName = "S_Door11";
	private static final String MOTION51_indName = "S_Motion51";
	private static final String MOTION10_indName = "S_Motion10";
	private static final String MOTION13_indName = "S_Motion13";
	private static final String MOTION14_indName = "S_Motion14";
	private static final String MOTION08_indName = "S_Motion08";
	private static final String MOTION09_indName = "S_Motion09";
	private static final String MOTION_commonName = "Motion";
	private static final String WATERB_indName = "S_Ad1B";
	private static final String WATERC_indName = "S_Ad1C";
	private static final String abitantName = "Abitant1"; // individual (results)
	// to descriminate that the abitant is close to the corridor cabinet and not to the couch
	private static final String PLACINGLOCATED_objProp = "isNearTo";
	private static final String PLACINGKITCHEN_indName = "A_KitchenCloset";
	private static final String PLACINGTABLE_indName = "A_LivingTable";
	
	// task1Ontology class names 
	//public static final String DOOR_className = "Door";
	//public static final String WATHER_className = "Wather";
	public static final String LIVINGTABLE_className = "LivingTable";
	public static final String KITCHENCLOSET_className = "KitchenCloset";
	public static final String VIRTUALSENSOR_className = "VirtualSensor";
	private static final String ROOT_className = "TimeRepresentation";
	private static final String EMptyLivingRoomWindow_className = "LivingRoomEmptyWindow";
	private static final String EMptyKitchenWindow_className = "KitchenEmptyWindow";
	private static final String TIMEWINDOW_className = "TW-";
	// task1Ontology data properties names
	private static final String DOOR_dataProp = "hasDoorValue";
	private static final String WATHER_dataProp = "hasWaterValue";
	private static final String POSITION_dataProp = "hasMotionValue";
	private static final String TIME_dataProp = "hasTypeTimeStamp";
	// task1Ontology individuals name
	private static final String CLOCK_indName = "C_SystClock";
	private static final String TIMEWINDOW_indName = "W_Tw-";
	public  static final String RECOGNISED_indName = "Vs_PlantsWatered";

	// desctription of interesting data to import from placingOntology to task1Ontology
	private static List<OWLLiteral> data_state = new ArrayList<OWLLiteral>();
	private static List<String> indsName = new ArrayList<String>();
	private static List<String> property = new ArrayList<String>();
	private static List<String> classes =  new ArrayList<String>();
	static{
		indsName.add( DOOR11_indName); property.add( DOOR_dataProp); classes.add( null);
		indsName.add( MOTION51_indName); property.add( POSITION_dataProp); classes.add( KITCHENCLOSET_className);
		indsName.add( MOTION10_indName); property.add( POSITION_dataProp); classes.add( LIVINGTABLE_className);
		indsName.add( MOTION13_indName); property.add( POSITION_dataProp); classes.add( LIVINGTABLE_className);
		indsName.add( MOTION14_indName); property.add( POSITION_dataProp); classes.add( LIVINGTABLE_className);
		indsName.add( MOTION08_indName); property.add( POSITION_dataProp); classes.add( LIVINGTABLE_className);
		indsName.add( MOTION09_indName); property.add( POSITION_dataProp); classes.add( LIVINGTABLE_className);
		indsName.add( WATERB_indName); property.add( WATHER_dataProp); classes.add( null);
		indsName.add( WATERC_indName); property.add( WATHER_dataProp); classes.add( null);
		for( int i = 0; i < indsName.size(); i++)
			data_state.add( null);
	}
	
	// window specification.
	// size of a single window
	private static final Long windowSize = 300L; 
	// max delay to consider two windows to be closed between each other and merge them (only for empty windows)
	private static final Long windowCloseLimit = 100000L;
	// actual clock of the representation
	private static Long dataTime = null;
	// flag to discriminate if a time window is for an new data or not
	private static boolean wasEmptyWindow = false;
	// collection of time windows to track their position between each other
	private static final List< TimeWindow> timeLine = new ArrayList< TimeWindow>();
	
	// interesting entity to retrieve from the ontological framework
	private static final String PLACING_ontologyName = "placingOntology";
	private OWLReferences placingOntoRef, taskOntoRef;
	private OFDataMapperInterface<OWLNamedIndividual, TimeWindow> twMapper;
	

	@SuppressWarnings("unchecked")
	@Override
	synchronized void runJob(JobExecutionContext context) throws JobExecutionException {
		// remove new data property
		this.getOWLOntologyRefeferences().removeDataPropertyB2Individual( this.getProcedureIndividualName(), DataImporter.NEWDATA_dataProp, 
				Boolean.valueOf( true), false);
		
		//this.getOWLOntologyRefeferences().replaceDataProperty(
		//		this.getOWLOntologyRefeferences().getOWLIndividual( this.getProcedureIndividualName()), 
		//		this.getOWLOntologyRefeferences().getOWLDataProperty( DataImporter.NEWDATA_dataProp), 
		//		this.getOWLOntologyRefeferences().getOWLLiteral( true), 
		//		this.getOWLOntologyRefeferences().getOWLLiteral(false), false);

		logger.addDebugStrign( this.getProcedureIndividualName() + " detacher activated");

		// get time window mapper
		twMapper = (OFDataMapperInterface< OWLNamedIndividual, TimeWindow>) 
				this.getInvoker().getObject("MappersList", "TimeWindow");

		// get Ontology where store data (this class store withoud replacing)
		taskOntoRef = getImportingOntologyReferences();
		// get the spatial ontology to activate data flowing
		placingOntoRef = OWLReferences.getOWLReferences( PLACING_ontologyName);
		
		List<OWLLiteral> data = getImportingData( indsName, property);
		
		// check state and if it has changed added to activity ontology (taskOntoRef)
		Long clock = null;
		Integer importsCount = 0;
		boolean dataImported = false;
		for( int i = 0; i < indsName.size(); i++){
			if (checkState( data_state.get( i), data.get( i))) {
				importsCount++;
				Long t = importingData( data.get( i), indsName.get( i), property.get( i), classes.get( i));
				
				data_state.set( i, data.get( i));
				if( ! indsName.get( i).contains( MOTION_commonName))
					dataImported = true;
				if ( t != null)
					clock = t;
				
				logger.addDebugStrign( this.getProcedureIndividualName() + " adding data " + 
						indsName.get( i) + "-" + nameCounter + "-" + nameCounter2++ + " " + 
						property.get( i) + " " + data.get( i).getLiteral());
				
			}
		}
		
		manageTimeWindows( dataImported, clock);
		
		
		
		// remove data for synchronisation with respect to P_Importer
	/*	OWLDataProperty newDataProp = OWLLibrary.getOWLDataProperty( DataImporter.NEWDATA_dataProp, 
				this.getOWLOntologyRefeferences());
		OWLLiteral waiters = OWLLibrary.getOnlyDataPropertyB2Individual(this.getProcedureIndividualName(), 
				DataImporter.NEWDATA_dataProp, getOWLOntologyRefeferences());
		Integer waits = Integer.valueOf( waiters.getLiteral());
		if( waits > 1000)
			waits = 2;
		synchronized( this.getOWLOntologyRefeferences().getReasoner()){
			OWLLibrary.replaceDataProperty(
					OWLLibrary.getOWLIndividual( this.getProcedureIndividualName(), this.getOWLOntologyRefeferences()),
					newDataProp, 
					OWLLibrary.getOWLLiteral( waits, this.getOWLOntologyRefeferences()), 
					OWLLibrary.getOWLLiteral((waits - importsCount),this.getOWLOntologyRefeferences()), 
					false, this.getOWLOntologyRefeferences());
		}
		logger.addDebugStrign( " %%%%%%% update " + this.getProcedureIndividualName()
				+ OWLLibrary.getOWLObjectName( newDataProp) + " old " + 
				waits + " new " + (waits+1));
		*/
	}


	// return true if the new data is different from before (state). False otherwise
	private boolean checkState( OWLLiteral state, OWLLiteral data){
		if( data != null)
			if( state != null) // if state is different update it and add data
				if( ! state.getLiteral().equals( data.getLiteral()))
					//state = data; 
					return true;
				else return false;
			else 
				//state = data;
				return true;
		else return false;
	}


	// the frist time that a new data is imported it updates the clock individual.
	// in other times it create a fix time window around the data of size: windowSize
	// if there are no new data it add an empty time window. If another not new data is
	// coming it augment such time window. The maximum interval of time between two data
	// that is not true is: windowCloseLimit. Otherwise two empty windows will be created.
	private void manageTimeWindows(boolean dataImported, Long clock){
		if (dataImported) { // interesting data coming	
			nameCounter++;
			// update task1Ontology clock only once at the beginning
			if (nameCounter == 1) {
				synchronized(taskOntoRef.getReasoner()){
					OWLLiteral timeStamp = taskOntoRef.getOnlyDataPropertyB2Individual(CLOCK_indName,
									TIME_dataProp);
					OWLNamedIndividual ckInd = taskOntoRef.getOWLIndividual(CLOCK_indName);
					OWLDataProperty timeProp = taskOntoRef.getOWLDataProperty(TIME_dataProp);
					OWLLiteral newTime = taskOntoRef.getOWLLiteral(clock);
					//synchronized( taskOntoRef.getReasoner()){	
					taskOntoRef.replaceDataProperty( ckInd, timeProp, timeStamp, newTime, false);
				}
				dataTime = clock;
			}

			wasEmptyWindow = false;

		} else if (nameCounter > 0){// && nearToCloset) { // no interesting data, add empty time windows to use spatial informations
			Long placingTime = Long.valueOf(placingOntoRef.getOnlyDataPropertyB2Individual( abitantName ,	TIME_dataProp).getLiteral());

			// merge time windows
			Long margedSize = null, margedCenter = null;
			if (wasEmptyWindow) {
				Long relativePlacingTime = placingTime - dataTime;
				TimeWindow last = timeLine.get(timeLine.size() - 1);
				if (relativePlacingTime - last.getRelativeCentre() < windowCloseLimit) {
					timeLine.remove(timeLine.size() - 1);
					//synchronized( taskOntoRef.getReasoner()){
						OWLNamedIndividual tlInd = taskOntoRef.getOWLIndividual(	last.getIndividualName());
						twMapper.removeFromOntology( tlInd,	last, taskOntoRef);
					//}
					windowsCount--;

					if (relativePlacingTime >= last.getRelativeCentre()) {
						margedSize = relativePlacingTime
								+ (windowSize / 2)
								- (last.getRelativeCentre() - (last
										.getSize() / 2));
						if (margedSize % 2 != 0)
							margedSize = margedSize + 1;
						margedCenter = last.getRelativeCentre()
								- (last.getSize() / 2) + (margedSize / 2);
					} else {
						margedSize = last.getRelativeCentre()
								+ (last.getSize() / 2)
								- (relativePlacingTime - (windowSize / 2));
						if (margedSize % 2 != 0)
							margedSize = margedSize + 1;
						margedCenter = relativePlacingTime
								- (windowSize / 2) + (margedSize / 2);
					}

					margedCenter += dataTime;
				}
			} else { // get new time window
				margedSize = windowSize;
				margedCenter = placingTime;
			}

			Set<OWLNamedIndividual> places = placingOntoRef.getObjectPropertyB2Individual( abitantName, PLACINGLOCATED_objProp);
			OWLNamedIndividual kit = placingOntoRef.getOWLIndividual( PLACINGKITCHEN_indName);
			OWLNamedIndividual tabl = placingOntoRef.getOWLIndividual( PLACINGTABLE_indName);
			for( OWLNamedIndividual pl : places){
				if( pl.equals(kit)){
					// add to time line
					//synchronized( taskOntoRef.getReasoner()){	
						TimeWindow w = this.addTimeWidnow(margedCenter, margedSize,
								taskOntoRef);


						// set this time windows to be an empty window
						OWLAxiom axiom = taskOntoRef.setSubClassOf( EMptyKitchenWindow_className,w.getClassName());
						List<OWLOntologyChange> changes = taskOntoRef.getAddAxiom(axiom);
						taskOntoRef.applyChanges(changes);
					//}
					wasEmptyWindow = true;
				}else if( pl.equals(tabl)){
					// add to time line
					//synchronized( taskOntoRef.getReasoner()){	
						TimeWindow w = this.addTimeWidnow(margedCenter, margedSize,	taskOntoRef);


						// set this time windows to be an empty window
						OWLAxiom axiom = taskOntoRef.setSubClassOf( EMptyLivingRoomWindow_className,
								w.getClassName());
						List<OWLOntologyChange> changes = taskOntoRef.getAddAxiom(axiom);
						taskOntoRef.applyChanges(changes);
				}
				wasEmptyWindow = true;
			}
		}
	}
	
	// add one time window
	private TimeWindow addTimeWidnow( Long t, Long size, OWLReferences taskOntoRef){
		Long newCentre;
		if( dataTime != null)
			newCentre = t - dataTime;
		else  newCentre = 0L;

		TimeWindow tw = new TimeWindow( size, newCentre);
		tw.setClassName( TIMEWINDOW_className + windowsCount);
		tw.setIndividualName( TIMEWINDOW_indName + windowsCount);
		windowsCount = windowsCount + 1;
		tw.setRootClass( ROOT_className);
		OWLNamedIndividual tw_Ind = taskOntoRef.getOWLIndividual( tw.getIndividualName());
		twMapper.mapToOntology( tw_Ind, tw, taskOntoRef);
		timeLine.add( tw);
		return(tw);
	}

	// import the related data from the specified ontology. Lists parameter must have the
	// same length and thei represents the name and the property from which get data.
	// outputs are a list with the respective litterals
	private List< OWLLiteral> getImportingData( List<String> individualsName, List<String> properties){
		List< OWLLiteral> out = new ArrayList<OWLLiteral>();
		synchronized( placingOntoRef.getReasoner()){
		for( int i = 0 ; i < properties.size(); i++)
			out.add( placingOntoRef.getOnlyDataPropertyB2Individual( individualsName.get(i), properties.get(i)));
		}
		return( out);
	}
	
	// impots data in the task1RefOntology from the placing ontology.
	private Long importingData( OWLLiteral data, String dataName, String propName, String className){
		OWLLiteral tLiteral = placingOntoRef.getOnlyDataPropertyB2Individual( dataName, TIME_dataProp);
		Long t = null;
		if( tLiteral != null){
			t = Long.valueOf( tLiteral.getLiteral());
		}

		if( data.isFloat()){
			Float f = Float.valueOf( data.getLiteral());
			if( f > 0)
				data = taskOntoRef.getOWLLiteral( true);
			else data = taskOntoRef.getOWLLiteral( false);
		}
					
		//synchronized( taskOntoRef.getReasoner()){
			OWLNamedIndividual ind = taskOntoRef.getOWLIndividual( dataName + "-" + nameCounter + "-" + nameCounter2);
			OWLDataProperty prop = taskOntoRef.getOWLDataProperty( propName);
			OWLDataProperty timeProp = taskOntoRef.getOWLDataProperty( TIME_dataProp);
			OWLClass clazz = null;
			if( className != null)
				clazz = taskOntoRef.getOWLClass( className);
				
			taskOntoRef.addDataPropertyB2Individual( ind, prop, data, false);
			if( tLiteral != null)
				taskOntoRef.addDataPropertyB2Individual( ind, timeProp, tLiteral, false);
			if( className != null)
				taskOntoRef.addIndividualB2Class( ind, clazz, false);
		//}

		this.addTimeWidnow(t, windowSize, taskOntoRef);
		
		return(t);
	}
	
	// get reference of this procedure with respect to the ontology that it manages,
	// it is described in the predefinedOntology with the objProp "importsToOntology"
	private OWLReferences getImportingOntologyReferences(){
		String ontoName;
	//	synchronized( this.getOWLOntologyRefeferences().getReasoner()){
			OWLNamedIndividual ontoInd = this.getOWLOntologyRefeferences().getOnlyObjectPropertyB2Individual(
					this.getProcedureIndividualName(),	DataImporter.IMPORTTOONTOLOGY_objProp);
			ontoName = this.getOWLOntologyRefeferences().getOnlyDataPropertyB2Individual( ontoInd,
					this.getOWLOntologyRefeferences().getOWLDataProperty( DataImporter.ONTOLOGYNAME_dataProp)).getLiteral();
	//	}
		OWLReferences ontoReference = OWLReferences.getOWLReferences(ontoName);
		return( ontoReference);
	}

	/**
	 * @param nameCounter the nameCounter and nameCounter2 to set
	 */
	public static synchronized void setNameCounters(Integer nameCount) {
		//synchronized( nameCounter){
			nameCounter = nameCount;
			nameCounter2 = nameCount;
		//}
	}



	public static synchronized void setWindowsCount(Integer windowsCounter) {
		//synchronized( windowsCounter){
			Task3Dethacer.windowsCount = windowsCounter;
		//}
	}


	/**
	 * @return the timeline
	 */
	public static synchronized List<TimeWindow> getTimeline() {
		//synchronized( timeLine){
			return timeLine;
		//}
	}

	
	public static synchronized void clearTimeline() {
		//synchronized( timeLine){
			timeLine.clear();
		//}
	}
}

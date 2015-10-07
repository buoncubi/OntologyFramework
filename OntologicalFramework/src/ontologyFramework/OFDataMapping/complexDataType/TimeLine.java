package ontologyFramework.OFDataMapping.complexDataType;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFDataMapping.reservatedDataType.Procedure;
import ontologyFramework.OFDataMapping.reservatedDataType.TimeWindow;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class implements the mapping mechanism between an individual
 * and a TimeLine object. Namely, it implements also the definition
 * of a time line expressed as a list of {@link ontologyFramework.OFDataMapping.reservatedDataType.TimeWindow}.
 * 
 * In particular, the idea is based over three predefined SWRL rules
 * into the ontology:
 * <pre>
 * {@code TimeWindow(?c), Thing(?d), hasTypeTimeStamp(?d, ?t), 
 *	hasTypeTimeWindowsLowerBound(?c, ?l), hasTypeTimeWindowsUpperBound(?c, ?u), 
 *  greaterThan(?t, ?l), lessThan(?t, ?u) -> belongsToTimeWindows(?d, ?c)}
 * 
 * {@code Clock(?ck), TimeWindow(?c), hasTypeTimeStamp(?ck, ?tck), 
 *  hasTypeTimeWindowsCentralInstant(?c, ?tc), hasTypeTimeWindowsSize(?c, ?w), 
 *  add(?a, ?t, ?w2), add(?t, ?tc, ?tck), divide(?w2, ?w, "2"^^long) 
 *  -> hasTypeTimeWindowsUpperBound(?c, ?a)}
 * 
 * {@code Clock(?ck), TimeWindow(?c), hasTypeTimeStamp(?ck, ?tck), 
 *  hasTypeTimeWindowsCentralInstant(?c, ?tc), hasTypeTimeWindowsSize(?c, ?w), 
 * 	add(?t, ?tck, ?tc), divide(?w2, ?w, "2"^^long), subtract(?a, ?t, ?w2) 
 * 	-> hasTypeTimeWindowsLowerBound(?c, ?a)}
 * </pre>
 * Thanks to which it is possible to move a TimeWindow
 * only updating the clock value, defined as a Time Instance belong
 * to the individual: {@literal C_SystemClock}.
 * 
 * Since it is useful to automatically build ontological classes
 * to represent time windows in a way that an individual, which
 * has a instant property, will be classified in the appropriate 
 * windows; a particular mechanism has been adopted. A time windows is 
 * defined through an individual {@literal IndBaseName-index} which
 * belongs to the class {@literal TimeWindow}, 
 * since it has the following data property:
 * "{@code IndBaseName-index hasTypeTimeWindowesSize "20000"^^long}"
 * and
 * "{@code IndBaseName-index hasTypeTimeWindowsCentralInstant "-10000"^^long}".
 * When the reasoner is update it will apply the above SWRL rules to
 * add two data property as: 
 * "{@code IndBaseName-index hasTypeTimeWindowsUpperBound "1383042263107"^^long}"
 * and
 * "{@code IndBaseName-index hasTypeTimeWindowsLowerBound "1383042243107"^^long}". 
 * Moreover, if an individual {@literal I} will have a time stamp property
 * such that it would be classified into a class associated to the individual
 * {@literal IndBaseName-index} the above SWRL rules will push the reasoner
 * to add this object property:
 * "{@code I belongsToTimeWindows IndBaseName-index}".
 * To make those information working with a classification in ontological 
 * sense the following SWRL rules must be added:
 * "{@code Thing(?d), belongsToTimeWindows(?d, IndBaseName-index) 
 * -> ClassBaseName-index(?d)}".
 * Where the string "index" is preserved for human readability. Furthermore,
 * this step require to create the class named ClassBaseName-indix that will be 
 * placed as a sub class of a root class named with a base class name.
 * 
 * The proposes of this class is to make those steps automatically
 * during the mapping between an ontology and the system. Also interesting
 * is how to do it for a list of TimeWindows, and so for a time line. This
 * can be done by some static methods of this class where
 * two relative (with respect to the system clock) quantities
 * are needed.
 * 
 * Since Reaoner's updates can be also longer than a time line a cleaner
 * pool can be added into the list. This is just a time window, and 
 * so it follows all the above consideration and it is allocated in the minimum 
 * index inside the time line (0). This particular windows should be big enough
 * to guarantee that an eventually procedure, which is typically synchronized 
 * with the reasoner updating, can remove all the individuals that are gone
 * through all the time line since they are just classified by the reaosoner 
 * inside the cleaning class.  
 * 
 * This class is created and initialized using 
 * {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved through the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}. 
 * 
 * Actually, for thecnical problem the methods {@link #replaceIntoOntology(OWLNamedIndividual, TimeLine, TimeLine, OWLReferences)}
 * is not implemented.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class TimeLine implements OFDataMapperInterface< OWLNamedIndividual, TimeLine> {

	private static final OFDebugLogger logger = new OFDebugLogger( TimeLine.class, true);//DebuggingClassFlagData.getFlag( OWLDERDEBUG_individualName));
	@SuppressWarnings("unused")
	private final OFDebugLogger log = new OFDebugLogger( this, true);
	
	public static final long SINCEEVER_windowsSize = 31536000000L;//~1year 1381786380L;  // ~= 16days
	public static final String CLEANER_nameAugmenter = "-CleanerPool";
	
	private List< TimeWindow> timeLine = new ArrayList< TimeWindow>();
	private String individualBaseName, classBaseName;
	private OWLClass rootClass;
	private boolean hasCleaner = false;
	@SuppressWarnings("unused")
	private String[] keyWord;
	private OFBuiltMapInvoker listInvoker;
	private Procedure cleaner;

	/**
	 * Creates a new succession of {@link TimeWindow} to be
	 * mapped between ontology and the system. This constructor
	 * should be used to define time lines from api since it doesn not
	 * give a reference to the static class {@link OFBuiltMapInvoker}
	 * used to retrieve primitive mapper to map a time line.
	 * After this call the time line is empty
	 * 
	 * @param individualBaseName a basic string to define a time windows individual.
	 * In this documentation, during class introduction it has been called: "{@literal IndBaseName}".
	 * @param classBaseName a basic string to define the class which represent a time
	 * windows, In this documentation, during class introduction it has been called: "{@literal classBaseName}".
	 * @param rootClass ontological class to set as super class of all classes built
	 * in relation with "{@literal ClassBaseName-index}".
	 */
	public TimeLine( String individualBaseName, String classBaseName,  OWLClass rootClass){
		this.individualBaseName = individualBaseName;
		this.classBaseName = classBaseName;
		this.rootClass = rootClass;
	}
	/**
	 * Creates a new succession of {@link TimeWindow} to be
	 * mapped between ontology and the system. This constructor
	 * should be used to inport time lines from the ontology since it 
	 * gives a reference to the static class {@link OFBuiltMapInvoker}
	 * used to retrieve primitive mapper to map a time line.
	 * After this call the time line is empty
	 * 
	 * @param individualBaseName a basic string to define a time windows individual.
	 * In this documentation, during class introduction it has been called: "{@literal IndBaseName}".
	 * @param classBaseName a basic string to define the class which represent a time
	 * windows, In this documentation, during class introduction it has been called: "{@literal classBaseName}".
	 * @param rootClass ontological class to set as super class of all classes built
	 * in relation with "{@literal ClassBaseName-index}".
	 * @param listInvoker a map to all the builded class from the framework.
	 */
	public TimeLine( String individualBaseName, String classBaseName,  OWLClass rootClass, OFBuiltMapInvoker listInvoker){
		this.individualBaseName = individualBaseName;
		this.classBaseName = classBaseName;
		this.rootClass = rootClass;
		this.listInvoker = listInvoker;
	}


	/**
	 * Add a new {@link TimeWindow} to this time line.
	 * The index of this time windows will be at the actual
	 * last position.
	 * Basically it just calls:
	 * {@code new TimeWindow( granularity, relativeCentre)}.
	 * 
	 * @param granularity total size of the centred time windows
	 * in milliseconds. 
	 * @param relativeCentre a relative (where 0 means now) centre
	 * of the time windows.
	 */
	public void addToTimeLine( Long granularity, Long relativeCentre){
		timeLine.add( new TimeWindow( granularity, relativeCentre));
	}
	/**
	 * Add a new {@link TimeWindow} to this time line.
	 * The index of this time windows will be equal to the
	 * input parameter and, if this require to insert it into
	 * the list the further components will be rightly shifted of
	 * one position.
	 * Basically it just calls:
	 * {@code new TimeWindow( granularity, relativeCentre)}.
	 * 
	 * @param index the position of this time windows inside
	 * the time line list.
	 * @param granularity total size of the centred time windows
	 * in milliseconds. 
	 * @param relativeCentre a relative (where 0 means now) centre
	 * of the time windows.
	 */
	public void addToTimeLine( int index, Long granularity, Long relativeCentre){
		timeLine.add( index, new TimeWindow( granularity, relativeCentre));
	}
	/**
	 * This method is analogous to {@link #addToTimeLine(Long, Long)}
	 * where the time windows is not created inside this method
	 * but is given as an input parameter. 
	 * 
	 * @param tw a Time windows to add in the time line list
	 * at the last position.
	 */
	public void addToTimeLine( TimeWindow tw) {
		timeLine.add(tw);
	}
	/**
	 * This method is analogous to {@link #addToTimeLine(int, Long, Long)}
	 * where the time windows is not created inside this method
	 * but is given as an input parameter.
	 * 
	 * @param index of the time window inside the time line list.
	 * @param tw a Time windows to add in the time line list
	 * at the given position with eventually rightly shifting of components.
	 */
	public void addToTimeLine( int index, TimeWindow tw) {
		timeLine.add( index, tw);
	}
	
	/**
	 * Remove a time windows, from the time line list, at the
	 * index indicate by the input parameter. Eventually other
	 * elements will be left shifted.
	 * 
	 * @param index position of the windows to remove inside the 
	 * time line list
	 */
	public void removeFromTimeLine( int index){
		timeLine.remove(index);
	}
	
	
	/**
	 * Returns the complete individual name given a particular index
	 * relate to the position of time windows inside the time
	 * line list. This method will return the name that
	 * in the class introduction is: {@literal IndBaseName-index}.
	 * 
	 * @param index of the time windows inside the time line.
	 * @return the complete name of the individual which represent 
	 * a time window located in a particular position of the time line.
	 */
	public String getIndividualName( int index){
		return( individualBaseName + index);
	}
	/**
	 * Returns the complete class name given a particular index
	 * relate to the position of time windows inside the time
	 * line list. This method will return the name that
	 * in the class introduction is: {@literal ClassBaseName-index}.
	 * 
	 * @param index of the time windows inside the time line.
	 * @return the complete name of the class which represent 
	 * a time window located in a particular position of the time line.
	 */
	public String getClassName( int index){
		return( classBaseName + index);
	}
	
	
	
	/**
	 * Return the time window object which is in a given
	 * position inside this time line.
	 * 
	 * @param index of the time windows inside the time line.
	 * @return the time window located in such position insite the
	 * time line-
	 */
	public TimeWindow getTimeWindow( int index){
		return( timeLine.get(index));
	}
	
	/**
	 * @return the timeLine
	 */
	public List<TimeWindow> getTimeLine() {
		return timeLine;
	}
	/**
	 * @param timeLine the timeLine to set
	 */
	public void setTimeLine(List<TimeWindow> timeLine) {
		this.timeLine = timeLine;
	}
	

	/**
	 * @return the individualBaseName.
	 */
	public String getIndividualBaseName() {
		return individualBaseName;
	}
	
	/**
	 * @param individualBaseName the individualBaseName to set
	 */
	public void setIndividualCName(String individualBaseName) {
		this.individualBaseName = individualBaseName;
	}

	/**
	 * @return the CleanerindividualName
	 */
	public String getCleanerIndividualName() {
		return individualBaseName + CLEANER_nameAugmenter;
	}
	/**
	 * @return the CleanerClassName
	 */
	public String getCleanerClassName() {
		return classBaseName + CLEANER_nameAugmenter;
	}
	
	/**
	 * @return the classBaseName
	 */
	public String getClassBaseName() {
		return classBaseName;
	}
	/**
	 * @param classBaseName the classBaseName to set
	 */
	public void setClassBaseName(String classBaseName) {
		this.classBaseName = classBaseName;
	}

	/**
	 * @return the rootClass
	 */
	public OWLClass getRootClass() {
		return rootClass;
	}
	/**
	 * @param rootClass the rootClass to set
	 */
	public void setRootClass(OWLClass rootClass) {
		this.rootClass = rootClass;
	}

	/**
	 * @return true if the time line has a cleaning pool. False otherwise.
	 */
	public boolean hasCleaner() {
		return hasCleaner;
	}
	
	
	/**
	 * Add a cleaning time windows with the index 0. It is recommended
	 * to do this operation when no more time windows have to be added
	 * in the line to avoid that the cleaner will move from its index.
	 * The cleaning pool is a time window with a fixed size: {@value #SINCEEVER_windowsSize}
	 * and a relative center computed in a way that the upper bound of
	 * the pool is conceded with the lower bound of the time windows which
	 * has index equal to 1 in the time line.
	 * 
	 * @param cleaner procedure to remove individual from the cleaning
	 * pool.
	 */
	public void addCleaner( Procedure cleaner) {
		TimeWindow sample = timeLine.get( 0);//timeLine.size() - 1);

		Long relativeCentre = sample.getRelativeCentre() - ( sample.getSize() / 2)
			-( SINCEEVER_windowsSize / 2); 
		
		this.addToTimeLine( 0, SINCEEVER_windowsSize, relativeCentre); 
		logger.addDebugStrign( "add new cleaner to TimeLine Time Line : " + timeLine);
		
		this.cleaner = cleaner;
		hasCleaner = true;
	}
	
	
	
	/**
	 *  It just call {@link #mapNames(Procedure)} with the parameter
	 *  equal to {@code null}.
	 */
	public void mapNames(){
		mapNames( null);
	}
	/**
	 * It maps all the names with their base string and their definitive index.
	 * It is recommended to do this operation when no more 
	 * time windows have to be added in the line 
	 * to avoid inconsistent index mapping.
	 * Moreover, if the parameter is not null, this methods add
	 * a cleaner to the time line using {@link #addCleaner(Procedure)}
	 * 
	 * @param cleaner the procedure to be associate to the cleaning pull.
	 * If it is null than no cleaning pool will be added.
	 */
	public void mapNames( Procedure cleaner){
		int count = 1;
		for( int i = 0; i < timeLine.size(); i++){
			TimeWindow tw = timeLine.get( i);
			if( tw.getRelativeCentre() >= 0){
				tw.setClassName( getClassName( count));
				tw.setIndividualName( getIndividualName( count++));
			}
			
			tw.setRootClass( OWLLibrary.getOWLObjectName( getRootClass()));
		}
		
		count = -1;
		for( int i = timeLine.size() - 1; i >= 0; i--){
			TimeWindow tw = timeLine.get( i);
			if( tw.getRelativeCentre() < 0){
				tw.setClassName( getClassName( count));
				tw.setIndividualName( getIndividualName( count--));
			}
		}
		
		if( cleaner != null){
			addCleaner( cleaner);
		
			timeLine.get( 0).setClassName( getCleanerClassName());
			timeLine.get( 0).setIndividualName( getCleanerIndividualName());
			timeLine.get( 0).setRootClass( OWLLibrary.getOWLObjectName( getRootClass()));
		}
	}
	
	
	/**
	 * @return the cleaner
	 */
	public Procedure getCleaner() {
		return cleaner;
	}
	
	/**
	 * @param cleaner the cleaner to set
	 */
	public void setCleaner(Procedure cleaner) {
		this.cleaner = cleaner;
	}
	
	/**
	 * @return the listInvoker to point to the builded class. 
	 * Basically, used to refer to data type mappers.
	 */
	public OFBuiltMapInvoker getListInvoker() {
		return listInvoker;
	}

	/**
	 * @param listInvoker the listInvoker to point to the builded class. 
	 * Basically, used to refer to data type mappers.
	 */
	public void setListInvoker(OFBuiltMapInvoker listInvoker) {
		this.listInvoker = listInvoker;
	}
	
	
	// individual is not used !!!!!!!!!!!!
	// listInvoker must be initialised and mapNames() must be called
	@Override
	public TimeLine mapFromOntology(OWLNamedIndividual cleanerIndividual,
			OWLReferences ontoRef) {
		TimeLine tl = new TimeLine( this.getClassBaseName(), this.getIndividualBaseName(), this.getRootClass());
		TimeWindowsDataMapper twMapper = (TimeWindowsDataMapper) listInvoker.getObject( "MappersList", "TimeWindow");
		if( twMapper != null){ 
			for( TimeWindow i : timeLine){
				//System.out.println( i.getIndividualName());
				TimeWindow t = twMapper.mapFromOntology( ontoRef.getOWLIndividual( i.getIndividualName()), ontoRef);
				t.setRootClass( ontoRef.getOWLObjectName( getRootClass()));
				tl.addToTimeLine( t);	
			}
			ProcedureDataMapper procMapper = (ProcedureDataMapper) listInvoker.getObject( "MapperList", "Procedure");
			if( procMapper != null){
				Procedure cl = procMapper.mapFromOntology(cleanerIndividual, ontoRef);
				tl.setCleaner( cl);
			}
				
		}
		return( tl);
	}
	

	// individual is not used as the name for the cleaner
	// listInvoker must be initialised and mapNames() must be called
	@Override
	public boolean mapToOntology(OWLNamedIndividual cleanerIndividual, TimeLine value,
			OWLReferences ontoRef) {
		TimeWindowsDataMapper twMapper = (TimeWindowsDataMapper) listInvoker.getObject( "MappersList", "TimeWindow");
		if( twMapper != null){ 
			for( TimeWindow i : timeLine)
				twMapper.mapToOntology( ontoRef.getOWLIndividual( i.getIndividualName()), i, ontoRef);
			
			if( hasCleaner()){
				ProcedureDataMapper procMapper = (ProcedureDataMapper) listInvoker.getObject( "MapperList", "Procedure");
				if( procMapper != null){
					procMapper.mapToOntology( cleanerIndividual, cleaner, ontoRef);
				}
			}
			logger.addDebugStrign( " time line mapper to ontology " + this.getTimeLine());
			return true;
		}
		return false;
	}
	
	
	@Override
	public boolean removeFromOntology(OWLNamedIndividual cleanerIndividual, TimeLine value,
			OWLReferences ontoRef) {
		TimeWindowsDataMapper twMapper = (TimeWindowsDataMapper) listInvoker.getObject( "MappersList", "TimeWindow");
		if( twMapper != null){ 
			for( TimeWindow i : timeLine)
				twMapper.removeFromOntology( ontoRef.getOWLIndividual( i.getIndividualName()), i, ontoRef);
			
			if( hasCleaner()){
				ProcedureDataMapper procMapper = (ProcedureDataMapper) listInvoker.getObject( "MapperList", "Procedure");
				if( procMapper != null){
					procMapper.removeFromOntology( cleanerIndividual, cleaner, ontoRef);
				}
			}
			logger.addDebugStrign( " time line mapper remove from ontology " + this.getTimeLine());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg, TimeLine oldArg,
			TimeLine newArg, OWLReferences ontoRef) {
		return false;
	}
	

	@Override
	public void setKeyWords(String[] kw) {
		this.keyWord = kw;
	}
	
	
	/**
	 * This method return a time line with time windows already attached to it.
	 * In particolar the maximum temporal span of a time line is given through
	 * the parameters {@code lowerLimit} and {@code higthLimit}. This span will
	 * modified (in terms of milliseconds) to be divisible in an integer number 
	 * of time windows that will have the same size during all the time
	 * line. The size is defined by the parameter {@code granularity}. No cleaner
	 * are attached to this time line and not name mapping is performed
	 * by this method.
	 * 
	 * @param lowerLimit minimum relative (0 = now) temporal span in milliseconds.
	 * @param highLimit maximum relative (0 = now) temporal span in milliseconds.
	 * @param granularity size of all the time windows.
	 * @param baseClassName a basic string to define the class which represent a time
	 * windows, In this documentation, during class introduction it has been called: "{@literal classBaseName}".
	 * @param baseIndividualName a basic string to define a time windows individual.
	 * In this documentation, during class introduction it has been called: "{@literal IndBaseName}".
	 * @param rootClass ontological class to set as super class of all classes built
	 * in relation with "{@literal ClassBaseName-index}".
	 * @return a complete time line over a temporal span,
	 * where all the windows has equal size.
	 */
	public static TimeLine getConstantTimeLine( Long lowerLimit, Long highLimit, Long granularity, 
			String baseClassName, String baseIndividualName, OWLClass rootClass){
	
		TimeLine timeLine = new TimeLine( baseClassName, baseIndividualName, rootClass);
		
		// granularity must be pari
		if( granularity % 2 != 0)
			granularity++;
	
		//int count = -1;
		Long cPrevious = - granularity / 2;
		if( lowerLimit > 0){
			timeLine.addToTimeLine( 0, granularity, cPrevious);
			/*out.add( 0, new TimeWindow( granularity, cPrevious, 
					baseClassName + count, baseIndividualName + count--, rootClass));*/
			while( cPrevious - granularity / 2 >= -lowerLimit){
				Long c = cPrevious - granularity;
				timeLine.addToTimeLine( 0, granularity, c);
				/*out.add( 0, new TimeWindow( granularity, c, 
							baseClassName + count, baseIndividualName + count--, rootClass));*/
				cPrevious = c;
			}
		}
	
		//count = 1;
		cPrevious = granularity / 2;
		if( highLimit > 0){
			timeLine.addToTimeLine(granularity, cPrevious);
			/*out.add( new TimeWindow( granularity, cPrevious, 
					baseClassName + count, baseIndividualName + count++, rootClass, ontoRef));*/
			while( cPrevious + granularity / 2 <= highLimit){
				Long c = cPrevious + granularity;
				timeLine.addToTimeLine(granularity, c);
				/*out.add( new TimeWindow( granularity, c, 
							baseClassName + count, baseIndividualName + count++, rootClass, ontoRef));*/
				cPrevious = c;
			}
		}
		
		logger.addDebugStrign( "created new constant Time Line : " + timeLine.getTimeLine());
		return( timeLine);
	}
	
	
	/**
	 * This method return a time line with time windows already attached to it.
	 * In particular the maximum temporal span of a time line is given through
	 * the parameters {@code lowerLimit} and {@code higthLimit}. This span will
	 * modified (in terms of milliseconds) to be divisible in an integer number 
	 * of time windows that will have the size which increases going further
	 * from the relative 0 value. The initial size is defined by the parameter 
	 * {@code granularity} and follows the rule: {@code granularity *= modularity;}
	 * which is recursively computed for every time windows starting from the 
	 * relative 0 and going up or down in the time axes. 
	 * No cleaner are attached to this time line and not 
	 * name mapping is performed by this method.
	 * 
	 * @param lowerLimit minimum relative (0 = now) temporal span in milliseconds.
	 * @param highLimit maximum relative (0 = now) temporal span in milliseconds.
	 * @param granularity initial windows size
	 * @param modularity multiplication factor for which the size increase going
	 * further from the relative center.
	 * @param baseClassName a basic string to define the class which represent a time
	 * windows, In this documentation, during class introduction it has been called: "{@literal classBaseName}".
	 * @param baseIndividualName a basic string to define a time windows individual.
	 * In this documentation, during class introduction it has been called: "{@literal IndBaseName}".
	 * @param rootClass ontological class to set as super class of all classes built
	 * in relation with "{@literal ClassBaseName-index}".
	 * @return a complete time line over a temporal span,
	 * where all the windows has size which increases going further from now.
	 */
	public static TimeLine getSquareTimeLine( Long lowerLimit, Long highLimit, Long granularity, Integer modularity, 
			String baseClassName, String baseIndividualName, OWLClass rootClass){
		
		TimeLine timeLine = new TimeLine( baseClassName, baseIndividualName, rootClass);
		// granularity must be pari
		if( granularity % 2 != 0)
			granularity++;
	
		//int count = -1;
		Long granul = granularity;
		Long cPrevious = - granul / 2;
		if( lowerLimit > 1){
			timeLine.addToTimeLine( 0, granularity, cPrevious);
			/*out.add( 0, new TimeWindow( granul, cPrevious, 
					baseClassName + count, baseIndividualName + count--, rootClass, ontoRef));*/
			while( cPrevious - granul / 2 >= -lowerLimit){
				granul *= modularity;
				Long c = cPrevious - granul;
				timeLine.addToTimeLine( 0, granularity, c);
				/*out.add( 0, new TimeWindow( granul, c, 
							baseClassName + count, baseIndividualName + count--, rootClass, ontoRef));*/
				cPrevious = c;
			}
		}
	
		//count = 1;
		granul = granularity;
		if( highLimit > 1){
			cPrevious = granul / 2;
			timeLine.addToTimeLine(granularity, cPrevious);
			/*out.add( new TimeWindow( granul, cPrevious, 
					baseClassName + count, baseIndividualName + count++, rootClass, ontoRef));*/
			while( cPrevious + granul / 2 <= highLimit){
				granul *= modularity;
				Long c = cPrevious + granul;
				timeLine.addToTimeLine(granularity, c);
				/*out.add( new TimeWindow( granul, c, 
							baseClassName + count, baseIndividualName + count++, rootClass, ontoRef));*/
				cPrevious = c;
			}
		}
		
		logger.addDebugStrign( "created new squared Time Line : " + timeLine.getTimeLine());
		return( timeLine);
	}
	

}
package ontologyFramework.OFDataMapping.reservatedDataType;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.complexDataType.TimeLine;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * This class is the mapping representation of an ontological time windows.
 *  
 * A time windows is represented in the ontology with an individual and a bunch
 * of SWRL rules more addressed in {@link ontologyFramework.OFDataMapping.complexDataType.TimeWindowsDataMapper}. 
 * 
 * Refer also to {@link TimeLine} for more details about
 * time windows and their usage.
 * 
 * This implementation supposes that the time windows is centered.
 * So it center is equal to its size divided by 2. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class TimeWindow{

	private Long size, relativeCentre;
	private String individualName;
	private static String[] keyWord;
	private String className;
	private String rootClass;
	
	/**
	 * Create a time window whic has a size and a center value with respect to the
	 * centre ( = 0 ) of an abstract time line always time invariant.
	 * For example give time windows as: {@literal T1( 10, 0), T2( 10, -15)} 
	 * {@literal and T3( 10, +15)} the representation will be distributed uniformally
	 * in an always fixed time line; as: 
	 * {@literal T2€[ -15, -5) & T1€[ -5, 5) & T3[5, 15)}.
	 * During the running of the system this line will move during time
	 * and so the actual windows would be:
	 * {@literal T2€[ -15+t, -5+t) & T1€[ -5+t, 5+t) & T3[5+t, 15+t)}
	 * Where {@literal t} is a value close to the real time instance.   
	 * 
	 * @param size number of millisecond of the windows size
	 * @param relativeCentre relative number of millisecond in which the windows is
	 * centered with respect to now.
	 */
	public TimeWindow( Long size, Long relativeCentre){
		this.size = size;
		this.relativeCentre = relativeCentre;
	}
	/**
	 * It creates a time windows using the parameters: {@code size} and 
	 * {@code relative center} as sow in {@link #TimeWindow(Long, Long)}.
	 * Moreover, it assign to this class names for ontological
	 * entities that are needed to map the windows from this framework to 
	 * the ontology. In particolar they are the name of an Individual belong
	 * to the ontological class {@literal DataType -> TimeWindow}. And the name
	 * of the class in which other individual can be classified as belong to
	 * a give time window. {@literal DataType -> TimeRepresentation} is
	 * the ontological path by default
	 * 
	 * @param size number of millisecond of the windows size
	 * @param relativeCentre relative number of millisecond in which the windows is
	 * centered with respect to now.
	 * 
	 * @param individualName name of the individual that describe this time window in the 
	 * ontology
	 * @param className name of the class that will behave as
	 * a time windows from the reasoning point of view. This class will
	 * collect all the other individual of the ontology which has a time stamp
	 * property that fall on this windows of time.
	 */
	public TimeWindow( Long size, Long relativeCentre, String className, String individualName){
		this.size = size;
		this.relativeCentre = relativeCentre;
		this.className = className;
		this.individualName = individualName;
	}
	
	/**
	 * @param size the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}
	/**
	 * @param relativeCentre the relativeCentre to set
	 */
	public void setRelativeCentre(Long relativeCentre) {
		this.relativeCentre = relativeCentre;
	}
	
	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * @return the relativeCentre
	 */
	public Long getRelativeCentre() {
		return relativeCentre;
	}
	
	/**
	 * @return the individual name
	 */
	public String getIndividualName() {
		return individualName;
	}
	
	/**
	 * @param individualName
	 */
	public void setIndividualName(String individualName){
		this.individualName = individualName;
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the rootClass
	 */
	public String getRootClass() {
		return rootClass;
	}
	/**
	 * @param rootClass the rootClass to set
	 */
	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}
	@Override
	public String toString(){
		/*return( "TimeWindows: \"" + OWLLibrary.getOWLObjectName( individual) + 
				"\". Linked to class named :" + className + 
				". has CentraRelative instance on: " + relativeCentre + " has windows size: " + size + " [ms].");*/
		return(" In rootClass: " + rootClass +
				". ind: " + individualName + 
				". class: " + className + 
				". centre: " + relativeCentre + 
				". size: " + size + " | ");
				
	}
	
	/**
	 * @return the keyWord
	 */
	public static String[] getKeyWord() {
		return keyWord;
	}

	/**
	 * @param keyWord the keyWord to set
	 */
	public static void setKeyWord(String[] keyWord) {
		TimeWindow.keyWord = keyWord;
	}
	
	/**
	 * return the windows with its size and central instant computed
	 * with respect to an reference clock value. (long unix time stamp in milliseconds)
	 * @param actualCk time stamp of when compute the windows
	 * @return time windows compute with respect to an actual referiment.
	 */
	public AbsoluteTimeWindow getAbsoluteTimeWindows( Long actualCk){
		Long centralTime = actualCk + relativeCentre;
		Long upperBound = centralTime + ( size / 2);
		Long lowerBound = centralTime - ( size / 2);
		return( new AbsoluteTimeWindow( lowerBound, centralTime, upperBound, actualCk));
	}
	/**
	 * @return the absulute time windows computed for the time which this method is called
	 * 
	 * Return the result of: {@link #getAbsoluteTimeWindows(Long)} {@code (System.currentTimeMillis())}
	 */
	public AbsoluteTimeWindow getAbsoluteTimeWindows(){
		return( getAbsoluteTimeWindows( System.currentTimeMillis()));
	}
	
	/**
	 * return the windows with its size and central instant  as are descripted 
	 * in the ontology refered from {@code ontoRef}. It must contain the data
	 * property {@code keyWord[ 4] = "hasTypeTimeWindowsUpperBound} 
	 * and {@code keyWord[ 5] = "hasTypeTimeWindowsLowerBound}.
	 * @param ontoRef time stamp of when compute the windows
	 * @return time windows compute with respect to an actual referiment.
	 */
	public AbsoluteTimeWindow getAbsoluteTimeWindows( OWLReferences ontoRef){		
		OWLDataProperty upper = ontoRef.getOWLDataProperty( keyWord[ 4]);
		OWLDataProperty lower = ontoRef.getOWLDataProperty( keyWord[ 3]);
		OWLNamedIndividual individual = ontoRef.getOWLIndividual( getIndividualName());
		OWLLiteral up = ontoRef.getOnlyDataPropertyB2Individual( individual, upper);
		OWLLiteral lo = ontoRef.getOnlyDataPropertyB2Individual( individual, lower);
		if( ( up != null) && ( lo != null)){
			Long u = Long.valueOf( up.getLiteral());
			Long l = Long.valueOf( lo.getLiteral());
			Long c = l + ( size / 2);
			Long ck = c - relativeCentre;
			return( new AbsoluteTimeWindow( l, c, u, ck));
		}
		return( null);
	}
}

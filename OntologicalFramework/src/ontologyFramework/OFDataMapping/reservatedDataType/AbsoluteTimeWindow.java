package ontologyFramework.OFDataMapping.reservatedDataType;


/**
 * This class defines the Object which implements an AbsoluteTimeWindows.
 * It should be use to refer to a time windows which has its place
 * in a time line. Basically it is just a data structure to store
 * the state of a time windows in a particular instant.
 * Note that in this framework time instances are describe has a Long 
 * which represents a Unix time stamp.
 * 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class AbsoluteTimeWindow {
	
	private  Long upperBound, centralTime, lowerBound, actualClock;

	/**
	 * Create absolute time windows with final property set. 
	 * 
	 * @param lowerBound minimum time stamp of the windows
	 * @param centralTime central time stamp of the windows
	 * @param upperBound maximum time stamp of the windows
	 * @param ck time instant fixed in the representation. It should be the time
	 * when this windows has been frozen.
	 */
	public AbsoluteTimeWindow( Long lowerBound, Long centralTime, Long upperBound, Long ck){
		this.upperBound = upperBound;
		this.centralTime = centralTime;
		this.lowerBound = lowerBound;
		this.actualClock = ck;
	}
	
	/**
	 * @return the upperBound
	 */
	public Long getUpperBound() {
		return upperBound;
	}

	/**
	 * @return the lowerBound
	 */
	public Long getLowerBound() {
		return lowerBound;
	}

	/**
	 * @return the centralTime
	 */
	public Long getCentralTime() {
		return centralTime;
	}

	/**
	 * @return the actualClock when the framework decide to froze the windows in this class
	 */
	public Long getActualClock() {
		return actualClock;
	}
} 

package ontologyFramework.OFContextManagement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

/**
 * This class notifies into console
 * the progress of reasoning classification. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OFReasonerProgressMonitor implements ReasonerProgressMonitor{

	// get debugger
	private OFDebugLogger logger = new OFDebugLogger( this, true);//DebuggingClassFlagData.getFlag( OWLLibrary.OWLDERDEBUG_individualName));
	Long startTime;
	String startStr, reasonerName;
		
	public void setReasonerName( String reasonerName){
		this.reasonerName = reasonerName;
	}
	
	@Override
	public void reasonerTaskStarted(String taskName) {
		startTime = System.nanoTime();
		Date date = Calendar.getInstance().getTime();
		startStr = taskName + " " + reasonerName + " started at: " +  new SimpleDateFormat( OFDebugLogger.DATAFORMAT).format(date);
	}

	@Override
	public void reasonerTaskStopped() {
		logger.addDebugStrign( startStr + " and ends now. "+ reasonerName + " [Durarion:" +
				(System.nanoTime() - startTime) + "ns]");
	}

	@Override
	public void reasonerTaskProgressChanged(int value, int max) {
	}

	@Override
	public void reasonerTaskBusy() {
		logger.addDebugStrign( "WARNING : reasoner task is busy. ");
	}

}

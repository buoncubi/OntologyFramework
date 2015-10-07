package ontologyFramework.OFErrorManagement.OFException;

import java.io.Serializable;


/**
 * This class define an exception Object. 
 * In fact it can be initialized by the building mechanism
 * relate to the exception and it contains all the
 * needed information about it.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ExceptionData implements Serializable{

	private String indName;
	private String mess;
	private boolean notify;
	private boolean kill;
	private int backStep;
	private OFExceptionNotifierInterface notifier;
	
	/**
	 * Create a new object relate to an exception.
	 * 
	 * @param individualName name of the ontological individual relate to 
	 * this exception
	 * @param message that the exception is caring out.
	 * @param notifyFlag if it is true than the exception will be notified
	 * if it occurs. Otherwise it will never be notified.
	 * @param killFlag if it is true than the system will be stopped as 
	 * soon as this exception occurs. Otherwise the system will continuous its
	 * work with no guarantees. 
	 * @param backTrace define the number of back steps into the stack trace
	 * in order to give a better explanation of the exception.
	 * @param noti class which describe how should the system react when this
	 * exception occurs.
	 */
	public ExceptionData( String individualName, String message, boolean notifyFlag, boolean killFlag, int backTrace, OFExceptionNotifierInterface noti){
		indName = individualName;
		mess = message;
		notify = notifyFlag;
		kill = killFlag;
		notifier = noti;
	}
	/**
	 * Create a new object relate to an exception without specify which
	 * is the behavior of the system when the exception occurs. By 
	 * default the framework will no do nothing about. 
	 * 
	 * @param individualName name of the ontological individual relate to 
	 * this exception
	 * @param message that the exception is caring out.
	 * @param notifyFlag if it is true than the exception will be notified
	 * if it occurs. Otherwise it will never be notified.
	 * @param killFlag if it is true than the system will be stopped as 
	 * soon as this exception occurs. Otherwise the system will continuous its
	 * work with no guarantees. 
	 * @param backTrace define the number of back steps into the stack trace
	 * in order to give a better explanation of the exception.
	 */
	public ExceptionData( String individualName, String message, boolean notifyFlag, boolean killFlag, int backTrace){
		indName = individualName;
		mess = message;
		notify = notifyFlag;
		kill = killFlag;
	}

	/**
	 * @return the name of the ontological individual attached to this
	 * exception
	 */
	public String getIndName() {
		return indName;
	}

	/**
	 * @return the message that this exception is carring out.
	 */
	public String getMess() {
		return mess;
	}

	/**
	 * @return true if the exception must be notified; false if not
	 */
	public boolean isNotify() {
		return notify;
	}

	/**
	 * @return true if the system should be stoppen when this 
	 * exception occurs, false if not.
	 */
	public boolean isKill() {
		return kill;
	}

	/**
	 * @return the number of back steps into the stack trace
	 * in order to give a better explanation of the exception.
	 */
	public int getBackStep() {
		return backStep;
	}

	/**
	 * @return the notifier instance which define what must be
	 * done when an exception occurs.
	 */
	public OFExceptionNotifierInterface getNotifier() {
		return notifier;
	}
	
	/**
	 * notify the exception using the OFExceptionNotifier 
	 */
	public void notifyException() {
		getNotifier().notifyException( this);
	}
	
	/**
	 * @param notifier to set the class which defines what must be done
	 * when an exception occurs.
	 */
	public void setNotifier(OFExceptionNotifierInterface notifier) {
		this.notifier = notifier;
	}
}

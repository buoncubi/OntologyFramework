package ontologyFramework.OFErrorManagement.OFException;

import java.io.Serializable;


/**
 * This interface gives the basic entity needed to define
 * an exception notifier.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public interface OFExceptionNotifierInterface extends Serializable {

	/**
	 * This method should implements the behavior 
	 * of the system when a particular exception occurs.
	 * 
	 * @param exD information about the occurred exception 
	 */
	public void notifyException( ExceptionData exD);
}

//how to call an exception!!!! from everywhere
//ExceptionData exD = (ExceptionData) listInvoker.getClassFromList( "ExceptionList", "exc");
//		exD.getNotifier().notifyException( exD);
//or	exD.notifyException();
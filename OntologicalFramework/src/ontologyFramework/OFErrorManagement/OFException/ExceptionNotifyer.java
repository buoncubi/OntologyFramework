package ontologyFramework.OFErrorManagement.OFException;


/**
 * This is the simplest way to react to an exception. 
 * In fact, it will just notify them in console.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ExceptionNotifyer implements OFExceptionNotifierInterface{

	@Override
	public void notifyException(ExceptionData exD) {
		// TODO Auto-generated method stub
		System.err.println( " to implement " + this.getClass() + " ExceptionData of individual:" + exD.getIndName());
		
	}

}

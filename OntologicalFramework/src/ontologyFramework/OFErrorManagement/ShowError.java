package ontologyFramework.OFErrorManagement;
import javax.swing.JOptionPane;




// Then it close the program 
// arg[0] -> title of the dialog box
// arg[1]...arg[n]  -> errors divided by 'separator'
//backStep are the backward step on the steak to show method name and line number
//if arg = default => does not show nothing but you can call the other methods
/**
 * This class is used to show a dialog box with informations about 
 * the occurred error; than it closes the program. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class ShowError {

	private final String separator = ". \n";
	 
	/**
	 * Show error in a dialog box.
	 * 
	 * @param backStep number of compilation trace steps to reproduce in 
	 * the error notification.
	 * @param arg is a vector where: arg[0] is the title of the dialog
	 * box while the other components are string to be notified.
	 */
	public ShowError ( int backStep, String[] arg){
		
		if( !( arg[0].equals( "default") && arg.length == 1)){
			// build a string as a following of array 'arg' element
			String error = "";
			for( int i = 1; i < arg.length; i++)
				error += separator + arg[i];
			
			error += "\n\n      COMPILING INFO : \n";
			String[] compilingInfo = catchCaller( 3); 
			String[] compilingInfoPlus = catchCaller( 4 + backStep);
			error += " package.class  : " + compilingInfo[ 0] + separator;
			error += " method  : " + compilingInfo[ 1] + " line number  : " + compilingInfo[ 2] + separator;
			error += "  called by : " + compilingInfoPlus[0] + "." + compilingInfoPlus[ 1] + "()   line:" + compilingInfoPlus[ 2] + separator;
			
			// show a dialog box
			JOptionPane a = new JOptionPane();
			JOptionPane.showMessageDialog(a, error, arg[0], JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	 
	/**
	 * Retrieve and propose useful information for error recognition.
	 * 
	 * @param backCallStepNumber number of compilation trace steps to reproduce in 
	 * the error notification.
	 * @return return the name of package, class and method from which notifier has been called
	 * return: str[0] = package.class, str[1] = method, str[2] = line number.
	 */
	public String[] catchCaller( int backCallStepNumber){
		String[] ret = new String[ 3];
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		try{
			StackTraceElement e = stacktrace[ backCallStepNumber]; // [0] -> java, [1] -> catchCaller, [2] -> who call catchCaller, [3] -> who call who call catchCaller .....
			ret[ 0] = e.getClassName();
			ret[ 1] = e.getMethodName();
			Integer tmp = e.getLineNumber();
			ret[ 2] = tmp.toString();
			return( ret);
		}
		catch( java.lang.ArrayIndexOutOfBoundsException e){
			return( new String[] {" index out of : Thread.currentThread().getStackTrace() ",
					" index out of : Thread.currentThread().getStackTrace() ",
					" index out of : Thread.currentThread().getStackTrace() "});
		}
	}
}

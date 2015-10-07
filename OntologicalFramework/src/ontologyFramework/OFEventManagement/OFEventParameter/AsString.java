package ontologyFramework.OFEventManagement.OFEventParameter;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.OFEventParameterInterface;

/**
 * Given an input it returns it as a String. If input is null it returns null.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class AsString implements OFEventParameterInterface {

	@Override
	public String getParameter(Object input, OWLReferences ontoRef) {
		if( input == null)
			return( null);
		return( (String) input);
	}

}

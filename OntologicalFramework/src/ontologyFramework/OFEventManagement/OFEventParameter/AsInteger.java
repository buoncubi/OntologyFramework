package ontologyFramework.OFEventManagement.OFEventParameter;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.OFEventParameterInterface;

/**
 * This class get an input and return its value as integer.
 * If the input is "null" it returns "null";
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class AsInteger implements OFEventParameterInterface {

	@Override
	public Integer getParameter(Object input, OWLReferences ontoRef) {
		if( input == null)
			return( null);
		return Integer.valueOf( (String) input);
	}

}

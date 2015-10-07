package ontologyFramework.OFEventManagement.OFEventParameter;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.OFEventParameterInterface;

/**
 * Given an input as a String it returns the OWLClass associated to that name.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class AsOWLClass implements OFEventParameterInterface {
	
	@Override
	public Object getParameter(Object input, OWLReferences ontoRef) {
		return( ontoRef.getOWLClass( (String) input));
	}
}

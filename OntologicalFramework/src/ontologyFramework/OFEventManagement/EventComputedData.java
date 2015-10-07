package ontologyFramework.OFEventManagement;

import ontologyFramework.OFContextManagement.OWLReferences;

/**
 * This class simply contains two field, moved during event parameter computation
 * between the classes {@link ontologyFramework.OFEventManagement.OFEventParameterDefinition} and a
 * class,  called by name, which implements {@link ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface}.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class EventComputedData{
	 
	private Object obj;
	private String ontoName; //OWLReferences ontoRef;
	
	/**
	 * create new EventComputedData
	 * 
	 * @param parameter computed parameter for the Event 
	 * @param ontoRef ontological referents of the parameter
	 */
	public EventComputedData( Object parameter, OWLReferences ontoRef){
		this.obj = parameter;
		this.ontoName = ontoRef.getOntoName();
	}

	/**
	 * @return the parameter
	 */
	public Object getParameter() {
		return obj;
	}

	/**
	 * @return the ontological reference of the parameter;
	 */
	public OWLReferences getOntoRef() {
		return OWLReferences.getOWLReferences( ontoName);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return( this.getParameter() + " " + this.getClass().getClass() + " of " + this.getOntoRef().getOntoName());
	}
	
}
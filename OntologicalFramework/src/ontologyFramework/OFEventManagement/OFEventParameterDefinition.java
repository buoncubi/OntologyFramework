package ontologyFramework.OFEventManagement;

import java.io.Serializable;
import java.util.List;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFRunning.OFInvokingManager.ReflationInstanciater;

/**
 * This class defines the definition of a paramiter of a particular Event.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class OFEventParameterDefinition implements Serializable{

	private List<String> classPackageName;
	private Object input;
	private String ontoName; //OWLReferences ontoRef;
	
	/**
	 * create new parameter definition
	 * 
	 * @param classpackageName full java qualifyer of the parameter implementstion. Which must implement {@link OFEventParameterInterface}
	 * @param parameterInput initial input to the parameter implementation
	 * @param eventOntoRef ontological reference of this parameter
	 */
	public OFEventParameterDefinition( List< String> classpackageName, Object parameterInput, String eventOntoRef){
		classPackageName = classpackageName;
		input = parameterInput;
		ontoName = eventOntoRef;//ontoRef =  eventOntoRef;
		// System.out.println( this.getClass().getSimpleName() + " created new with : input="+input + " | className=" + classPackageName + " | ontoRef=" + eventOntoRef.getOntoName());
	}

	/**
	 * @return the java full qualifyer of the parameter implementation
	 */
	public List<String> getClassPackageName() {
		// System.out.println( this.getClass().getSimpleName() + " called get class name : " + classPackageName); 
		return classPackageName;
	}

	/**
	 * @return the input to the parameter implementation
	 */
	public Object getInput() {
		// System.out.println( this.getClass().getSimpleName() + " called get input : " + input);
		return input;
	}

	/**
	 * @param input set the input to the parameter implementation
	 */
	public void setInput(Object input) {
		this.input = input;
		// System.out.println( this.getClass().getSimpleName() + " called set input : " + input);
	}

	/**
	 * @return the ontological reference of this parameter
	 */
	public OWLReferences getOWLReferences() {
		// System.out.println( this.getClass().getSimpleName() + " called get ontoRef : " + OWLReferences.getOntoName());
		return OWLReferences.getOWLReferences( ontoName); // ontoRef);
	}
	
	//public String getOWLReferencesName(){
	//	return ontoName;
	//}
	
	/**
	 * Compute the value of the parameter. It instanciate the parameter implentation using {@link #getClassPackageName()}
	 * and call {@link OFEventParameterInterface#getParameter(Object, OWLReferences)}, Where {@link #getInput()}
	 * and {@link #getOWLReferences()} are the inputs, respectivaly. 
	 * 
	 * @return the object returned by {@link OFEventParameterInterface#getParameter(Object, OWLReferences)}
	 */
	public Object getParameter(){
		Object in = input;
		for( String s : classPackageName){ 
			OFEventParameterInterface parameterCalculator = ReflationInstanciater.instanciateOFEventParameterByName( s);
			Object paraValue = parameterCalculator.getParameter( in,  OWLReferences.getOWLReferences( ontoName)); // ontoRef);
			in = paraValue;
		}
		return( in);
	}
	
}

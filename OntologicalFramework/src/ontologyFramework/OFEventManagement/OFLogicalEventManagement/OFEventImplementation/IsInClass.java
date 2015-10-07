package ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventImplementation;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class implement the event that takes as input : {@code ...( OWLNamedIndividual ind, OWLClass cl)}.
 * Which return true if the individual belongs to the class and false otherwise. In the ontology 
 * an event must be defined which belongs to the class {@code "OFEvent"} thus has the properties:
 * <pre> 
 * 	{@code implementsOFEventName "ontologyFramework.OFEventManagement.OFEventImplementation.OFEventProcedure.IsInClass"^^string}
 *  {@code &}
 *  {@code hasEvent definition 
 *  		"in:ontologyFramework.OFEventManagement.OFEventParameter.
 *			 ?a @ontoName(S1-3) Exception.AsOWLClass
 *			 ?b @ontoName exc.AsOWLIndividual
 *			 !r IsInClass(?b ?a)"^^strign}
 * </pre>
 * So, {@link #isCorrectInput(List)} return true if {@code ts.get(0).getParameter() instanceof OWLNamedIndividual}
 * and {@code inputs.get( 1).getParameter() instanceof OWLClass}, {@code inputs.get(1).getOntoRef() != null} and {@code inputs.get(0).getOntoRef() != null}, are true.
 * The {@link #evaluateEvent(List, OFBuiltMapInvoker)} just ask to the reasoner of the ontology named
 * {@code "ontoName(S1-3)"} if in the class "Exception" exist an individual called "exc" and propagates the
 * answare. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class IsInClass implements OFEventInterface {

	/*ONTOLOGY Example
	 
	 
	in:ontologyFramework.OFEventManagement.OFEventImplementation.OFEventParameter.

 	?a @ontoName(S1-3) Exception.AsOWLClass
 	?b @ontoName exc.AsOWLIndividual

	!r IsInClass(?b ?a)
	
	or it can be written as "! (?b ?a)"*/
	
	@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get(0).getParameter() instanceof OWLNamedIndividual)
			if( inputs.get( 1).getParameter() instanceof OWLClass)
				if( inputs.get(0).getOntoRef() != null)
					if( inputs.get( 1).getOntoRef() != null)
						return true;
		return false;
	}

	@Override
	public Boolean evaluateEvent(List<EventComputedData> inputs, OFBuiltMapInvoker invoker) {
		OWLNamedIndividual ind = (OWLNamedIndividual) inputs.get(0).getParameter();
		OWLClass cl = (OWLClass) inputs.get( 1).getParameter();
		Set<OWLNamedIndividual> indB2cl = inputs.get( 1).getOntoRef().getIndividualB2Class( cl);
		for( OWLNamedIndividual i : indB2cl)
			if( i.equals( ind))
				return true;
		return false;
	}

}

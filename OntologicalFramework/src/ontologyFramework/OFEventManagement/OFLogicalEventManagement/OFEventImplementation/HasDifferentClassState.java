package ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventImplementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/*
 		?a c-7.AsOWLClass
 		
 		!r HasDifferentIndividual( ?a)

.*/
public class HasDifferentClassState implements OFEventInterface {

	static private Map< String, Set< OWLNamedIndividual>> classState = new HashMap< String, Set <OWLNamedIndividual>>();
	
	@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get(0).getOntoRef() != null)
			if( inputs.get(0).getOntoRef() instanceof OWLReferences)
				if( inputs.get(0).getParameter() != null)
					if( inputs.get(0).getParameter() instanceof OWLClass)
						return( true);
		return false;
	}

	@Override
	public Boolean evaluateEvent(List<EventComputedData> inputs,
			OFBuiltMapInvoker invoker) {

		OWLClass base = (OWLClass) inputs.get( 0).getParameter();
		OWLReferences ontoRefBase = inputs.get( 0).getOntoRef();
		Set< OWLNamedIndividual> baseInd = ontoRefBase.getIndividualB2Class( base);
		
		Set< OWLNamedIndividual> stateInd = classState.get( OWLLibrary.getOWLObjectName( base));
		Boolean result = false;
		if( ( stateInd != null) && ( baseInd != null)){
			result = stateInd.equals( baseInd);
		}
		
		classState.put( OWLLibrary.getOWLObjectName( base), baseInd);
		
		return result;
	}

}

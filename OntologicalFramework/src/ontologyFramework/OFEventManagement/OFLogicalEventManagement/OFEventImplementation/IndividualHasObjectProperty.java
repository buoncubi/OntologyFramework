package ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventImplementation;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

@SuppressWarnings("serial")
public class IndividualHasObjectProperty implements OFEventInterface {

	@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get( 0).getParameter() instanceof OWLNamedIndividual)
			if( inputs.get( 1).getParameter() instanceof OWLObjectProperty)
				if( inputs.get( 2).getParameter() instanceof OWLNamedIndividual)
					if( inputs.get( 0).getOntoRef() != null)
						return true;
		return false;
	}

	@Override
	public Boolean evaluateEvent(List<EventComputedData> inputs,
			OFBuiltMapInvoker invoker) {
		
		OWLReferences ontoRef = inputs.get( 0).getOntoRef();
		OWLNamedIndividual ind = (OWLNamedIndividual) inputs.get( 0).getParameter();
		OWLObjectProperty prop = (OWLObjectProperty) inputs.get( 1).getParameter();
		OWLNamedIndividual value = (OWLNamedIndividual) inputs.get( 2).getParameter();
		Set<OWLNamedIndividual> set = ontoRef.getObjectPropertyB2Individual( ind, prop);
		for( OWLNamedIndividual i : set)
			if( i.equals( value))
				return true;
		
		return false;
	}

}

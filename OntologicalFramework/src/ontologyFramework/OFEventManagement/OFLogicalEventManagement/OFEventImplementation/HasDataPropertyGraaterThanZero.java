package ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventImplementation;

import java.util.List;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

@SuppressWarnings("serial")
public class HasDataPropertyGraaterThanZero  implements OFEventInterface {

	@Override
	public boolean isCorrectInput(List<EventComputedData> inputs) {
		if( inputs.get(0).getParameter() instanceof OWLNamedIndividual)
			if( inputs.get(0).getOntoRef() != null)
				if( inputs.get(1).getParameter() instanceof OWLDataProperty)
					if( inputs.get(1).getOntoRef() != null)
						return true;
				
		return false;
	}

	@Override
	public Boolean evaluateEvent(List<EventComputedData> inputs,
			OFBuiltMapInvoker invoker) {
		
		OWLNamedIndividual ind = (OWLNamedIndividual) inputs.get(0).getParameter();
		OWLDataProperty prop = (OWLDataProperty) inputs.get(1).getParameter();
		OWLReferences ontoRef0 = (OWLReferences) inputs.get(0).getOntoRef();
		
		OWLLiteral lit = ontoRef0.getOnlyDataPropertyB2Individual( ind, prop);
		if( lit != null){
			if( lit.isBoolean()){
				Boolean b = Boolean.valueOf( lit.getLiteral());
				if( b)
					return true;
			}else if ( lit.isDouble()){
				Double d = Double.valueOf( lit.getLiteral());
				if( d > 0)
					return true;
			}else if ( lit.isFloat()){
				Float f = Float.valueOf( lit.getLiteral());
				if( f > 0)
					return true;
			}else if ( lit.isInteger()){
				Integer i = Integer.valueOf( lit.getLiteral());
				if( i > 0)
					return true;
			}
		}		
		return false;
	}

}

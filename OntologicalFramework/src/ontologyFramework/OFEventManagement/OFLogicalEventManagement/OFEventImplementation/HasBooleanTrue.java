package ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventImplementation;

import java.util.List;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFEventManagement.EventComputedData;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventInterface;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class implement the event that takes as input : {@code ...( OWLNamedIndividual ind, OWLDataProperty prop)}.
 * Which return true if the individual has that property and false otherwise. In the ontology 
 * an event must be defined which belongs to the class {@code "OFEvent"} thus has the properties:
 * <pre> 
 * 	{@code implementsOFEventName "ontologyFramework.OFEventManagement.OFEventImplementation.OFEventProcedure.HasBooleanValue"^^string}
 *  {@code &}
 *  {@code hasEvent definition 
 *  		"in:ontologyFramework.OFEventManagement.OFEventParameter.
 *			 ?a exc.AsOWLIndividual
 *			 ?b hasExceptionNotify.AsOWLDataProperty
 *			 !r HasBooleanTrue( ?a ?b)"^^strign}
 * </pre>
 * So, {@link #isCorrectInput(List)} return true if {@code inputs.get( 0).getParameter() instanceof OWLNamedIndividual}
 * and {@code inputs.get( 1).getParameter() instanceof OWLObjectProperty} and {@code inputs.get( 0).getOntoRef()} are true.
 * The {@link #evaluateEvent(List, OFBuiltMapInvoker)} just uses  the {@code invoker.getClassFromList( "MappersList", "Boolean")}
 * the get the boolean mapper and check if the value is true.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class HasBooleanTrue implements OFEventInterface {

	/* ONTOLOGY EXAMPLE
	in:ontologyFramework.OFEventManagement.OFEventImplementation.OFEventParameter.

 	?a exc.AsOWLIndividual
 	?b hasExceptionNotify.AsOWLDataProperty

	!r HasBooleanTrue( ?a ?b)*/
	
	
	@Override
	public boolean isCorrectInput( List<EventComputedData> inputs) {
		/*if( inputs.get( 0).getParameter() instanceof OWLNamedIndividual){
			if( inputs.get( 1).getParameter() instanceof OWLObjectProperty){
				if( inputs.get( 0).getOntoRef() != null){
					return( true);
				}
			}
		}
		return false;*/
		if( inputs.get( 0).getParameter() instanceof OWLNamedIndividual){
			if( inputs.get( 1).getParameter() instanceof OWLDataProperty){
				if( inputs.get( 0).getOntoRef() != null){
					return( true);
				}
			}
		}
		return false;
	}

	@Override
	public Boolean evaluateEvent( List<EventComputedData> inputs, OFBuiltMapInvoker invoker) {
		/*OWLNamedIndividual ind = (OWLNamedIndividual) inputs.get( 0).getParameter();
		OWLObjectProperty prop = (OWLObjectProperty) inputs.get(1).getParameter();
		OWLReferences ontoRef = (OWLReferences) inputs.get( 0).getOntoRef();
		OWLNamedIndividual value = OWLLibrary.getOnlyObjectPropertyB2Individual( ind, prop, ontoRef);
		if( value != null){
			OFDataMapperInterface mapper = (OFDataMapperInterface) invoker.getObject( "MappersList", "Boolean");
			Boolean bool = (Boolean) mapper.mapFromOntology( value, ontoRef);
			return( bool);
		}
		return( false);*/
		
		OWLNamedIndividual ind = (OWLNamedIndividual) inputs.get( 0).getParameter();
		OWLDataProperty prop = (OWLDataProperty) inputs.get(1).getParameter();
		OWLReferences ontoRef = (OWLReferences) inputs.get( 0).getOntoRef();
		OWLLiteral value = ontoRef.getOnlyDataPropertyB2Individual( ind, prop);
		if( value != null){
			if( value.getLiteral().contains( "true"))
				return true;
			else return false;
		}
		return( false);
	}

}

package ontologyFramework.OFDataMapping.complexDataType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFDataMapping.reservatedDataType.TimeWindow;

/**
 * This class implements a mapping mechanism between an individual
 * and a {@link TimeWindow}. This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved through the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 *  
 * This implementation consider that can exist only one property for
 * every types into an individual which describe a timeWindows.
 * 
 * Actually, this class does not implements the method: 
 * {@link #replaceIntoOntology(OWLNamedIndividual, TimeWindow, TimeWindow, OWLReferences)}
 * 
 * Refer also to {@link TimeLine} for more details about
 * time windows and their usage. 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class TimeWindowsDataMapper implements OFDataMapperInterface< OWLNamedIndividual, TimeWindow>  {

	private String[] keyWords;
	
	@Override
	public void setKeyWords(String[] kw) {
		this.keyWords = kw;
		TimeWindow.setKeyWord( kw);
	} 
	
	@Override
	public TimeWindow mapFromOntology(OWLNamedIndividual individual,
			OWLReferences ontoRef) {
		
		OWLDataProperty centProp = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLDataProperty windProp = ontoRef.getOWLDataProperty( keyWords[ 2]);
		OWLLiteral centrLit = ontoRef.getOnlyDataPropertyB2Individual(individual, centProp);
		OWLLiteral windLit = ontoRef.getOnlyDataPropertyB2Individual(individual, windProp);
		Set<OWLClass> classes = ontoRef.getIndividualClasses(individual);
		String className = null;

		for( OWLClass c : classes){
			Set<OWLClass> superC = ontoRef.getSuperClassOf( c);

			if( superC.contains( ontoRef.getOWLClass( keyWords[ 6]))){
				className  = ontoRef.getOWLObjectName(c);
				break;
			}
		}
		
		Long centre = Long.valueOf( centrLit.getLiteral());
		Long windows = Long.valueOf( windLit.getLiteral());
		
		TimeWindow tw = null;
		if( className != null)
			tw= new TimeWindow( windows, centre, 
				ontoRef.getOWLObjectName(individual), className);
		else System.out.println( "Exception !! " + individual + " must belong to a sub class of " + keyWords[ 6]);
		return( tw);
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual individual, TimeWindow value,
			OWLReferences ontoRef) {
		// get centre value
		Long centre = value.getRelativeCentre();
		OWLLiteral centreLit = ontoRef.getOWLLiteral( centre);
		OWLDataProperty centProp = ontoRef.getOWLDataProperty( keyWords[ 1]);
	
		// get size value
		Long size =  value.getSize();
		OWLLiteral sizeLit = ontoRef.getOWLLiteral( size);
		OWLDataProperty windProp = ontoRef.getOWLDataProperty( keyWords[ 2]);
		
		// get TimeRepresentation class
		OWLClass timeRepClass = ontoRef.getOWLClass( keyWords[ 0]);
		
		// add swrl rule
		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		String ontologyIRI = ontoRef.getIriOntologyPath().toString();
        // Thing(?d)
        OWLClass things = ontoRef.getFactory().getOWLThing();
        SWRLVariable d = ontoRef.getFactory().getSWRLVariable( IRI.create( ontologyIRI + "#d"));
 		SWRLClassAtom classAtom = ontoRef.getFactory().getSWRLClassAtom( things, d);
 		antecedent.add( classAtom);
 		//  belongsToTimeWindows(?d, individual)
 		OWLObjectProperty belongsTW = ontoRef.getOWLObjectProperty( keyWords[ 5]);
 		SWRLIndividualArgument indRule = ontoRef.getFactory().getSWRLIndividualArgument( individual);
 		SWRLObjectPropertyAtom dataRule = ontoRef.getFactory().getSWRLObjectPropertyAtom(belongsTW, d, indRule);
        antecedent.add( dataRule);
		// -> ClassName( ?d)
        OWLClass cl = ontoRef.getOWLClass( value.getClassName());
		classAtom = ontoRef.getFactory().getSWRLClassAtom( cl, d);
		
        SWRLRule rule = ontoRef.getFactory().getSWRLRule(antecedent,  
        		Collections.singleton(classAtom));
        //ontoRef.getManager().applyChange(new AddAxiom( ontoRef.getOntology(), rule));

        OWLClass rootClass = ontoRef.getOWLClass( value.getRootClass());
       
        // set individul belong to the class which is representing
        OWLClass describesClass = ontoRef.getOWLClass( value.getClassName());
        
        //synchronized( ontoRef){//.getReasoner()){
        	ontoRef.addIndividualB2Class(individual, describesClass, false);
	    	 if( rootClass != null){
	         	OWLAxiom axiom = ontoRef.setSubClassOf( rootClass, cl);
	         	ontoRef.applyChanges( ontoRef.getAddAxiom(axiom, false));
	         }
	    	 ontoRef.applyChanges( ontoRef.getAddAxiom( rule));
	    	 ontoRef.addDataPropertyB2Individual(individual, centProp, centreLit, false);
	    	 ontoRef.addDataPropertyB2Individual(individual, windProp, sizeLit, false);
	    	 ontoRef.addIndividualB2Class( individual, timeRepClass, false);
        //}
        
		return true;
	}
	
	@Override
	public boolean removeFromOntology(OWLNamedIndividual individual, TimeWindow value,
			OWLReferences ontoRef) {

		// get centre value
		Long centre = value.getRelativeCentre();
		OWLLiteral centreLit = ontoRef.getOWLLiteral( centre);
		OWLDataProperty centProp = ontoRef.getOWLDataProperty( keyWords[ 1]);
	
		// get size value
		Long size =  value.getSize();
		OWLLiteral sizeLit = ontoRef.getOWLLiteral( size);
		OWLDataProperty windProp = ontoRef.getOWLDataProperty( keyWords[ 2]);
		
		// get TimeRepresentation class
		OWLClass timeRepClass = ontoRef.getOWLClass( keyWords[ 0]);
		
		// swrl rule
		Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
		String ontologyIRI = ontoRef.getIriOntologyPath().toString();
        // Thing(?d)
        OWLClass things = ontoRef.getFactory().getOWLThing();
        SWRLVariable d = ontoRef.getFactory().getSWRLVariable( IRI.create( ontologyIRI + "#d"));
 		SWRLClassAtom classAtom = ontoRef.getFactory().getSWRLClassAtom( things, d);
 		antecedent.add( classAtom);
 		//  belongsToTimeWindows(?d, individual)
 		OWLObjectProperty belongsTW = ontoRef.getOWLObjectProperty( keyWords[ 5]);
 		SWRLIndividualArgument indRule = ontoRef.getFactory().getSWRLIndividualArgument( individual);
 		SWRLObjectPropertyAtom dataRule = ontoRef.getFactory().getSWRLObjectPropertyAtom(belongsTW, d, indRule);
        antecedent.add( dataRule);
		// -> ClassName( ?d)
        OWLClass cl = ontoRef.getOWLClass( value.getClassName());
		classAtom = ontoRef.getFactory().getSWRLClassAtom( cl, d);
		
        SWRLRule rule = ontoRef.getFactory().getSWRLRule(antecedent,  
        		Collections.singleton(classAtom));
        //ontoRef.getManager().applyChange(new AddAxiom( ontoRef.getOntology(), rule));

        OWLClass rootClass = ontoRef.getOWLClass( value.getRootClass());
       
        // set individul belong to the class which is representing
        OWLClass describesClass = ontoRef.getOWLClass( value.getClassName());
        
        //synchronized( ontoRef){//.getReasoner()){
        	ontoRef.removeIndividualB2Class(individual, describesClass, false);
	    	 if( rootClass != null){ // remove only sub class axioms not classes
	         	OWLAxiom axiom = ontoRef.setSubClassOf( rootClass, cl);
	         	ontoRef.applyChanges( ontoRef.getRemoveAxiom(axiom, false));
	         }
	    	 ontoRef.applyChanges( ontoRef.getRemoveAxiom( rule));
	    	 ontoRef.removeDataPropertyB2Individual(individual, centProp, centreLit, false);
	    	 ontoRef.removeDataPropertyB2Individual(individual, windProp, sizeLit, false);
	    	 ontoRef.removeIndividualB2Class( individual, timeRepClass, false);
        //}
		        
		return( true);
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual arg,
			TimeWindow oldArg, TimeWindow newArg, OWLReferences ontoRef) {
		// TODO Auto-generated method stub
		return false;
	}
}


 
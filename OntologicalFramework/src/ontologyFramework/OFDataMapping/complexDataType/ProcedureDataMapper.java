package ontologyFramework.OFDataMapping.complexDataType;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.OFDataMapperInterface;
import ontologyFramework.OFDataMapping.reservatedDataType.Procedure;

/**
 * This class implements a mapping mechanism between an individual
 * and a {@link ontologyFramework.OFDataMapping.reservatedDataType.Procedure}. 
 * This class is created
 * and initialized using {@link ontologyFramework.OFDataMapping.OFDataMapperBuilder}; furthermore,
 * it can be retrieved through the static map manager: 
 * {@link ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker}.
 * Please refer to {@link ontologyFramework.OFProcedureManagment.OFProcedureBuilder} for the usage of the
 * key words defined by the individual: {@literal M_ProcedureDataMapper}.
 * Also refer to {@link ontologyFramework.OFProcedureManagment.Algorithm} 
 * for more detail about the meaning of
 * such information.
 * 
 * This implementation consider that can exist only one property for
 * every types into an individual which describe a procedure.
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ProcedureDataMapper implements OFDataMapperInterface< OWLNamedIndividual, Procedure>   {

	private String[] keyWords;

	@Override
	public Procedure mapFromOntology(OWLNamedIndividual individual,
			OWLReferences ontoRef) {

		OWLDataProperty hasName = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral name = ontoRef.getOnlyDataPropertyB2Individual(individual, hasName);
		
		OWLDataProperty hasCheckerFreq = ontoRef.getOWLDataProperty( keyWords[ 2]);
		OWLLiteral checkerFreq = ontoRef.getOnlyDataPropertyB2Individual(individual, hasCheckerFreq);
		
		OWLDataProperty hasPoolSize = ontoRef.getOWLDataProperty( keyWords[ 3]);
		OWLLiteral poolSize = ontoRef.getOnlyDataPropertyB2Individual(individual, hasPoolSize);
		
		OWLObjectProperty hasEvent = ontoRef.getOWLObjectProperty( keyWords[ 4]);
		OWLNamedIndividual event = ontoRef.getOnlyObjectPropertyB2Individual(individual, hasEvent);
		
		OWLObjectProperty hasScheduler = ontoRef.getOWLObjectProperty( keyWords[ 5]);
		OWLNamedIndividual scheduler = ontoRef.getOnlyObjectPropertyB2Individual(individual, hasScheduler);
		
		OWLObjectProperty hasSynch = ontoRef.getOWLObjectProperty( keyWords[ 6]);
		OWLNamedIndividual synch = ontoRef.getOnlyObjectPropertyB2Individual(individual, hasSynch);
		
		OWLObjectProperty hasTrigger = ontoRef.getOWLObjectProperty( keyWords[ 7]);
		OWLNamedIndividual trigger = ontoRef.getOnlyObjectPropertyB2Individual(individual, hasTrigger);
		
		return( new Procedure( scheduler, event, trigger, synch, name, poolSize, checkerFreq));
	}

	@Override
	public boolean mapToOntology(OWLNamedIndividual individual, Procedure proc,
			OWLReferences ontoRef) {
			
		OWLDataProperty hasName = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral name = proc.getProcedureNameLitteral();
		
		OWLDataProperty hasCheckerFreq = ontoRef.getOWLDataProperty( keyWords[ 2]);
		OWLLiteral checkerFreq = proc.getCheckerFreqInMillisecLitteral();
		
		OWLDataProperty hasPoolSize = ontoRef.getOWLDataProperty( keyWords[ 3]);
		OWLLiteral poolSize = proc.getConcurrrentPoolSizeLittteral();
		
		OWLObjectProperty hasEvent = ontoRef.getOWLObjectProperty( keyWords[ 4]);
		OWLNamedIndividual event = proc.getEvent();
		
		OWLObjectProperty hasScheduler = ontoRef.getOWLObjectProperty( keyWords[ 5]);
		OWLNamedIndividual scheduler = proc.getScheduler();
		
		OWLObjectProperty hasSynch = ontoRef.getOWLObjectProperty( keyWords[ 6]);
		OWLNamedIndividual synch = proc.getSynchronization();
		
		OWLObjectProperty hasTrigger = ontoRef.getOWLObjectProperty( keyWords[ 7]);
		OWLNamedIndividual trigger = proc.getTimeTrigger();

		//synchronized( ontoRef.getReasoner()){
			if( name != null)
				ontoRef.addDataPropertyB2Individual(individual, hasName, name, false);
			if( checkerFreq != null)
				ontoRef.addDataPropertyB2Individual(individual, hasCheckerFreq, checkerFreq, false);
			if( poolSize != null)
				ontoRef.addDataPropertyB2Individual(individual, hasPoolSize, poolSize, false);
			if( event != null)
				ontoRef.addObjectPropertyB2Individual(individual, hasEvent, event, false);
			if( scheduler != null)
				ontoRef.addObjectPropertyB2Individual(individual, hasScheduler, scheduler, false);
			if( synch != null)
				ontoRef.addObjectPropertyB2Individual(individual, hasSynch, synch, false);
			if( trigger != null)
				ontoRef.addObjectPropertyB2Individual(individual, hasTrigger, trigger, false);
		//}
		return( true);
	}

	@Override
	public void setKeyWords(String[] kw) {
		this.keyWords = kw;
	}

	@Override
	public boolean removeFromOntology(OWLNamedIndividual individual, Procedure proc,
			OWLReferences ontoRef) {
		OWLDataProperty hasName = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral name = proc.getProcedureNameLitteral();
		
		OWLDataProperty hasCheckerFreq = ontoRef.getOWLDataProperty( keyWords[ 2]);
		OWLLiteral checkerFreq = proc.getCheckerFreqInMillisecLitteral();
		
		OWLDataProperty hasPoolSize = ontoRef.getOWLDataProperty( keyWords[ 3]);
		OWLLiteral poolSize = proc.getConcurrrentPoolSizeLittteral();
		
		OWLObjectProperty hasEvent = ontoRef.getOWLObjectProperty( keyWords[ 4]);
		OWLNamedIndividual event = proc.getEvent();
		
		OWLObjectProperty hasScheduler = ontoRef.getOWLObjectProperty( keyWords[ 5]);
		OWLNamedIndividual scheduler = proc.getScheduler();
		
		OWLObjectProperty hasSynch = ontoRef.getOWLObjectProperty( keyWords[ 6]);
		OWLNamedIndividual synch = proc.getSynchronization();
		
		OWLObjectProperty hasTrigger = ontoRef.getOWLObjectProperty( keyWords[ 7]);
		OWLNamedIndividual trigger = proc.getTimeTrigger();

		//synchronized( ontoRef.getReasoner()){
			if( name != null)
				ontoRef.removeDataPropertyB2Individual(individual, hasName, name, false);
			if( checkerFreq != null)
				ontoRef.removeDataPropertyB2Individual(individual, hasCheckerFreq, checkerFreq, false);
			if( poolSize != null)
				ontoRef.removeDataPropertyB2Individual(individual, hasPoolSize, poolSize, false);
			if( event != null)
				ontoRef.removeObjectPropertyB2Individual(individual, hasEvent, event, false);
			if( scheduler != null)
				ontoRef.removeObjectPropertyB2Individual(individual, hasScheduler, scheduler, false);
			if( synch != null)
				ontoRef.removeObjectPropertyB2Individual(individual, hasSynch, synch, false);
			if( trigger != null)
				ontoRef.removeObjectPropertyB2Individual(individual, hasTrigger, trigger, false);
		//}
		return( true);
	}

	@Override
	public boolean replaceIntoOntology(OWLNamedIndividual individual,
			Procedure proc, Procedure newproc, OWLReferences ontoRef) {
		
		OWLDataProperty hasName = ontoRef.getOWLDataProperty( keyWords[ 1]);
		OWLLiteral name = proc.getProcedureNameLitteral();
		OWLLiteral newName = newproc.getProcedureNameLitteral();
		
		OWLDataProperty hasCheckerFreq = ontoRef.getOWLDataProperty( keyWords[ 2]);
		OWLLiteral checkerFreq = proc.getCheckerFreqInMillisecLitteral();
		OWLLiteral newCheckerFreq = newproc.getCheckerFreqInMillisecLitteral();
		
		OWLDataProperty hasPoolSize = ontoRef.getOWLDataProperty( keyWords[ 3]);
		OWLLiteral poolSize = proc.getConcurrrentPoolSizeLittteral();
		OWLLiteral newPoolSize = newproc.getConcurrrentPoolSizeLittteral();
		
		OWLObjectProperty hasEvent = ontoRef.getOWLObjectProperty( keyWords[ 4]);
		OWLNamedIndividual event = proc.getEvent();
		OWLNamedIndividual newEvent = newproc.getEvent();
		
		OWLObjectProperty hasScheduler = ontoRef.getOWLObjectProperty( keyWords[ 5]);
		OWLNamedIndividual scheduler = proc.getScheduler();
		OWLNamedIndividual newScheduler = newproc.getScheduler();
		
		OWLObjectProperty hasSynch = ontoRef.getOWLObjectProperty( keyWords[ 6]);
		OWLNamedIndividual synch = proc.getSynchronization();
		OWLNamedIndividual newSynch = newproc.getSynchronization();
		
		OWLObjectProperty hasTrigger = ontoRef.getOWLObjectProperty( keyWords[ 7]);
		OWLNamedIndividual trigger = proc.getTimeTrigger();
		OWLNamedIndividual newTrigger = newproc.getTimeTrigger();
		
		//synchronized( ontoRef.getReasoner()){
			if( name != null){
				ontoRef.removeDataPropertyB2Individual(individual, hasName, name, false);
			} if( newName != null){
				ontoRef.addDataPropertyB2Individual(individual, hasName, newName, false);
			} if( checkerFreq != null){
				ontoRef.removeDataPropertyB2Individual(individual, hasCheckerFreq, checkerFreq, false);
			} if( newCheckerFreq != null){	
				ontoRef.addDataPropertyB2Individual(individual, hasCheckerFreq, newCheckerFreq, false);
			} if( poolSize != null){
				ontoRef.removeDataPropertyB2Individual(individual, hasPoolSize, poolSize, false);
			} if( newPoolSize != null){
				ontoRef.addDataPropertyB2Individual(individual, hasPoolSize, newPoolSize, false);
			} if( event != null){
				ontoRef.removeObjectPropertyB2Individual(individual, hasEvent, event, false);
			} if( newEvent != null){
				ontoRef.addObjectPropertyB2Individual(individual, hasEvent, newEvent, false);
			} if( scheduler != null){
				ontoRef.removeObjectPropertyB2Individual(individual, hasScheduler, scheduler, false);
			} if( newScheduler != null){
				ontoRef.addObjectPropertyB2Individual(individual, hasScheduler, newScheduler, false);
			} if( synch != null){
				ontoRef.removeObjectPropertyB2Individual(individual, hasSynch, synch, false);
			} if( newSynch != null){
				ontoRef.addObjectPropertyB2Individual(individual, hasSynch, newSynch, false);
			} if( trigger != null){
				ontoRef.removeObjectPropertyB2Individual(individual, hasTrigger, trigger, false);
			} if( newTrigger != null){
				ontoRef.addObjectPropertyB2Individual(individual, hasTrigger, newTrigger, false);
			}
		//}
		return( true);
	}

}

package ontologyFramework.OFEventManagement.OFTimeTriggerManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.reservatedDataType.NameMapper;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFEventManagement.OFEventParameterDefinition;
import ontologyFramework.OFEventManagement.OFLogicalEventManagement.OFEventBuilder;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class, as all the class that implements {@link OFBuilderInterface} has the proposes to initialize
 * classes to be used during system evolution. In this case its initializes Trigger relate to Time,
 * in particular the classes: {@link OFTimeTriggerDefinition}. 
 * <p>
 * A call to {@link #buildInfo(String[], OWLReferences, OFBuiltMapInvoker)} causes the reset of the
 * initialized classes Map, then all the individual inside the ontological class, named {@code keyWord[ 0]}
 * (by default: "OFTimeTrigger") are processed. Where, The definition of this class must be:
 * 	{@code (hasTimeTriggerDefinition exactly 1 TimeTriggerDefinition) and (implementsOFTimeTriggerName exactly 1 Name)}
 * <p>
 * For all of them it gets the name of the trigger implementation as the fully qualifyer if the class that
 * implement it. This retrieved thanks to the object property named {@code keyWord[ 1]}, (by default: "implementsOFTimeTriggerName").
 * Than, the method retrieve the definiton of the trigger as the string value of the 
 * object property named: {@code keyWord[ 3]} (by default, "hasTypeTimeTriggerDefinition")
 * attached to an individual that is linked to this one trhougth the object property named
 * {@code keyWord[ 2]} (by default: "hasTimeTriggerDefinition"). Where, the parsing procedure 
 * of the text are hinnerated from {@link OFEventBuilder}. As well as the managament of its parameter
 * are managed by {@link OFEventParameterDefinition}
 * <p>
 * The call to the method {@link #getInitialisedObject()} after called {@link #initializeDefinition(OWLNamedIndividual, OFTimeTriggerDefinition, OWLReferences)}}
 * returns a {@code HashMap<String,} {@link OFTimeTriggerDefinition}{@code >} where, keys are the names of the individuals
 * belong to the class named {@code keyWord[ 0]}. While the values are the classes which represent and
 * allow to compute all the temporal triggers available during the calling of {@link #initializeDefinition(OWLNamedIndividual, OFTimeTriggerDefinition, OWLReferences)}.
 * 
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OFTimeTriggerBuilder implements OFBuilderInterface<OFTimeTriggerDefinition>{

	private String[] keyWords;
	private Map< String, OFTimeTriggerDefinition> toInitialise = new HashMap< String, OFTimeTriggerDefinition>();
	
	// get debugger
	private OFDebugLogger logger = new OFDebugLogger( this, DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));
		
	
	@Override
	public void buildInfo(String[] keywords, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker) {
		keyWords = keywords;
		toInitialise.clear();
		logger.addDebugStrign( "OFTimeTriggerBuilder starts to build info ...");
	
		// for all the individual belong to class : OFTimeTrigger
		Set<OWLNamedIndividual> eventsInd = ontoRef.getIndividualB2Class( keyWords[ 0]);
		for( OWLNamedIndividual e : eventsInd){
		
			// get the name of the method
			/*OWLObjectProperty prop = OWLLibrary.getOWLObjectProperty( keyWords[ 1], ontoRef);
			OWLNamedIndividual nameInd = OWLLibrary.getOnlyObjectPropertyB2Individual( e, prop, ontoRef);
			String className = NameMapper.getNameFromOntology( nameInd, ontoRef);*/
			String className = NameMapper.getNameFromOntology( e, ontoRef);
			
			// create new TriggerData
			OFTimeTriggerDefinition ttd = new OFTimeTriggerDefinition( className);
			
			// get the trigger definition
			/*prop = OWLLibrary.getOWLObjectProperty( keyWords[ 2], ontoRef);
			Set<OWLNamedIndividual> nameInds = OWLLibrary.getObjectPropertyB2Individual( e, prop, ontoRef);
			for( OWLNamedIndividual i : nameInds)
				initializeDefinition( i, ttd, ontoRef);*/
			initializeDefinition( e, ttd, ontoRef);
			
			// add all in a list (than added to static list)
			toInitialise.put( OWLLibrary.getOWLObjectName( e), ttd);
		}
		
	}

	@Override
	public Map<String, OFTimeTriggerDefinition> getInitialisedObject() {
		return toInitialise;
	}

	// coming from OFEventBuilder
	private String importing;
	
	private void initializeDefinition( OWLNamedIndividual individualName,
			OFTimeTriggerDefinition eventDef,	OWLReferences ontoRef) {

		// get text of the definition of the event
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1]); //3
		Set<OWLLiteral> literals = (Set<OWLLiteral>) ontoRef
				.getDataPropertyB2Individual(individualName, prop);
		String txt = OFEventBuilder.getText(literals);

		// parse text, for every line
		StringTokenizer tokenised = new StringTokenizer(txt.trim(), OFEventBuilder.ENDLine_symb);
		while (tokenised.hasMoreTokens()) {
			String line = tokenised.nextToken().trim();
			if( line.startsWith( OFEventBuilder.IMPORT_symb)){
				// if starts with "in:" => the string after defines package class Name
				// it must be unique
				importing = line.replace( OFEventBuilder.IMPORT_symb, "").trim();
			} else if (line.startsWith(OFEventBuilder.VARIABLE_symb)) {
				// if starts with "?" => the string after defines a parameter
				eventDef.addToParameterMap( getParameterDefinition(line, ontoRef));
			} else if (line.startsWith(OFEventBuilder.RETURN_symb)) {
				// if start with "!" => the string after defines an Event
				getParameterOrder( line, eventDef);
			} else {
				logger.addDebugStrign("Exception");
				break;
			}
		}
		logger.addDebugStrign( "+++++++++++++++++++++++++");
		
		//return ( eventDef);
	}

	// return a map between ParameterName and ParameterData
	// line : "?par input.AsMethod1.AsMethod2..."
	private Map< String, OFEventParameterDefinition> getParameterDefinition(String line, OWLReferences ontoRef) {
		// create object to initialise in this method
		OFEventParameterDefinition parData;
		Map< String, OFEventParameterDefinition> paraMap = new HashMap<String, OFEventParameterDefinition>();
		
		// get tokens inside the row, it must have at least one " "
		StringTokenizer token = new StringTokenizer(line.trim(), OFEventBuilder.ASSEGNATIONPARAMETER_symb);
		if (token.countTokens() >= 2) {
			
			// get variable Name
			String varName = token.nextElement().toString()
					.replace(OFEventBuilder.VARIABLE_symb, "");

			// get reference to OWL Ontology
			String nextToken = token.nextElement().toString().trim();
			OWLReferences eventRef = ontoRef;
			if (nextToken.startsWith(OFEventBuilder.ATONTOLOGY_symb)) {
				// get ontology references
				nextToken = nextToken.replace(OFEventBuilder.ATONTOLOGY_symb, "");
				if (!ontoRef.getOntoName().equals(nextToken))
					eventRef = OWLReferences.getOWLReferences(nextToken);
				nextToken = token.nextElement().toString().trim();
			}

			// get commands: 10.asString.asOWLClass = 
			// asOWLClass( asString( Integer 10)) --> return Object
			StringTokenizer tok = new StringTokenizer(nextToken, OFEventBuilder.COMMAND_symb);
			// get initial variable 10
			String entityName = tok.nextElement().toString().trim();
			if (entityName.equals(OFEventBuilder.NULL_symb))
				entityName = null;
			// get series of procedure to compute the parameter 
			// list[1] pac.kage.asString
			// list[2] pac.kage.asOWLClass
			List< String> classesName = new ArrayList< String>();
			while( tok.hasMoreTokens()){
				// get java.package.Class, merging: import + command
				StringTokenizer tok1 = new StringTokenizer( importing, OFEventBuilder.COMMAND_symb);
				String packageClassName = "";
				while( tok1.hasMoreTokens()){
					packageClassName += tok1.nextToken().trim() + OFEventBuilder.COMMAND_symb;
				}
				packageClassName += tok.nextElement().toString().trim();
				classesName.add( packageClassName);
			}
			  
			// create new parameter and add to map
			parData = new OFEventParameterDefinition( classesName, entityName, eventRef.getOntoName());
			paraMap.put( varName, parData);
			
			logger.addDebugStrign(" ? : varName = " + varName +
					" | triggerRef = "	+ parData.getOWLReferences().getOntoName() + 
					" | command = " + parData.getClassPackageName() + 
					" | input = " + parData.getInput());

		} else
			logger.addDebugStrign("Exception");
		
		return( paraMap);
	}

	// line : "!r eventMethod( ?a ?b)"[A] or "! (a? b?)"[C]
	// static !?!?!?!?!?!?!?
	private void getParameterOrder( String line, OFTimeTriggerDefinition e) {
		// object to initialise in this method
		List<String> parOrder = new ArrayList<String>();
		
		// get token inside the line (it must to have at least one " ")
		StringTokenizer token = new StringTokenizer(line.trim(), OFEventBuilder.ASSEGNATIONPARAMETER_symb);
		if (token.countTokens() >= 2) {
			String nameTok = token.nextElement().toString();
			// get variable Name, not used !!!
			String varName = nameTok.replace(OFEventBuilder.RETURN_symb, "").trim(); // "!r"

			// get parameters order: eventMethod( Object a, Object b)
			// "eventMethod" is not used !!!
			String command = line.replace( nameTok, "").trim();
			int start = command.indexOf(OFEventBuilder.STARTPARAMETR_symb);
			int end = command.indexOf(OFEventBuilder.ENDPARAMETER_symb);
			command = command.substring(start + 1, end);
			StringTokenizer parameters = new StringTokenizer(command.trim(),
					OFEventBuilder.SPLIT_symb);
			while (parameters.hasMoreTokens())
				parOrder.add(parameters.nextToken().trim()
						.replace(OFEventBuilder.VARIABLE_symb, ""));

			// set mapped relation in EventDefinition
			e.setOrder( parOrder);
			//e.setVarName( varName);			
			logger.addDebugStrign(" ! : varName = " + varName
					+ " | parameterList = " + parOrder);
		} else
			logger.addDebugStrign("Exception");
	}
}

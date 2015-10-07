package ontologyFramework.OFEventManagement.OFLogicalEventManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.reservatedDataType.EventComputeLowMapper;
import ontologyFramework.OFDataMapping.reservatedDataType.NameMapper;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFEventManagement.OFEventParameterDefinition;
import ontologyFramework.OFRunning.OFInitialising.OFBuilderInterface;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * This class, as all the class that implements {@link OFBuilderInterface} has the proposes to initialize
 * classes to be used during system evolution. In this case its initializes Events,
 * in particular the classes: {@link OFEventParameterDefinition}, {@link OFEventDefinition} and {@link OFEventAggregation}. 
 * <p>
 * A call to {@link #buildInfo(String[], OWLReferences, OFBuiltMapInvoker)} causes the reset of the
 * initialized classes Map, then all the individual inside the ontological class, named {@code keyWord[ 0]}
 * (by default: "Event") are processed. Where, The definition of this class must be:
 * 	{@code (hasTypeEventParameter min 1 string) and (hasTypeEventComputeLow exactly 1 string)} 
 * <p>
 * For all of them it retrieves the computational low (ex: "r1 && r2") as a string and creates a 
 * new {@link OFEventAggregation}. Than, it gets the value of the data property 
 * named {@code keyWord[ 1]}} (by default: "hasTypeEventParameter") and 
 * it parse the incoming value (for example: "r1 = OFEventProcedure_IndName") using the symbol 
 * {@link #ASSEGNATION_symb}; by default {@value #ASSEGNATION_symb}. 
 * Note that the parameter is discarded if the parse has no two token. 
 * Then for each parameter belong to the individual a new 
 * {@link  OFEventDefinition} is created with the full identify java class which belongs to the individual 
 * "OFEventProcedure_IndName", Name retrieved from the value of the {@code keyWord[ 2]}(by default: implements OFEventName).  
 * Finally, it gets parameters trough ontological individual linked by the object property named: {@code keyWord[ 3]} 
 * (by Default: "hasEventDefinition"). Parameters are  added to the Event Definition thanks a parsing 
 * Mechanism of the data type value belong to the data property: {@code keyWord[ 4]} 
 * (by default "hasTypeEventDefinition").   
 * <p>
 * The call to the method {@link #getInitialisedObject()} after called {@link #initializeDefinition(OWLNamedIndividual, OFEventDefinition, OWLReferences)}
 * returns a {@code HashMap<String,} {@link OFEventAggregation}{@code >} where, keys are the names of the individuals
 * belong to the class named {@code keyWord[ 0]}. While the values are the classes which represent and
 * allow to compute all the Events available during the calling of {@link #initializeDefinition(OWLNamedIndividual, OFEventDefinition, OWLReferences)}
 *  
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OFEventBuilder implements OFBuilderInterface {

	private OFDebugLogger logger = new OFDebugLogger( this, DebuggingClassFlagData.getFlag( OFInitialiser.BUILDERDEBUG_individualName));
	
	// schered list that is initialized
	private Map< String, OFEventAggregation> toInitialise = new HashMap< String, OFEventAggregation>();
	private String[] keyWords;
	
	@Override
	public void buildInfo(String[] keywords, OWLReferences ontoRef,
			OFBuiltMapInvoker listInvoker) {
		
		keyWords = keywords;
		toInitialise.clear();
		logger.addDebugStrign( "OFEventBuilder starts to build info ...");
		
		
		// for all the individual belong to class : Event
		Set<OWLNamedIndividual> eventsInd = ontoRef.getIndividualB2Class( keyWords[ 0]);
		for( OWLNamedIndividual e : eventsInd){

			// get relation to satisfy
			String low = EventComputeLowMapper.getNameFromOntology( e, ontoRef);
			// create class to store initialised data about this event
			OFEventAggregation aggregator = new OFEventAggregation( low);				
			
			// get parameters for the aggregation low : I hasTypeEventParameter "?a = §*isInClass"^^string
			/*OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 1], ontoRef);		
			Set<OWLLiteral> literals = (Set<OWLLiteral>) ontoRef.getDataPropertyB2Individual( e, prop, ontoRef);
			Set<String> varAssegn = ontoRef.getOWLLiteralAsString( literals);*/
			
			// get object property: ' e hasEventAggregation_varName individual ' 
			// and bulds string: "?varName = individualName" 
			Set<String> variablesName = EventComputeLowMapper.getVariablesName( low);
			Map< String, OWLNamedIndividual> varAssegn = new HashMap< String, OWLNamedIndividual>();
			for( String v : variablesName){
				OWLObjectProperty prop = ontoRef.getOWLObjectProperty( keyWords[ 1] + v);
				varAssegn.put( v, ontoRef.getOnlyObjectPropertyB2Individual(e, prop));
			}
			
			logger.addDebugStrign( "  processing individual: " + ontoRef.getOWLObjectName( e) + ". With aggregation low: " + low);
			// for all the definition of variable linked to this individual
			for( String s : varAssegn.keySet()){
				logger.addDebugStrign( " initializing event: " + s);
				
				/*// Tokens by ASSEGNATION_symb = "="  (ex "?a = §*isInClass")
				StringTokenizer tokenised = new StringTokenizer( s.trim(), ASSEGNATION_symb);
				// s must contain only one ASSEGNATION_symb
				if( tokenised.countTokens() == 2){
					// get the left hand side of the expressions ( ?a)
					String variableName = s;//tokenised.nextElement().toString().trim();
					
					// get the name of the java method (java.package.Class) 
					// linked to the individual (§*isInClass) with objProp: implementsOFEventName
					String classPackageNameInd = tokenised.nextElement().toString().trim();
					OWLNamedIndividual eventInterfaceInd = ontoRef.getOnlyObjectPropertyB2Individual(
							classPackageNameInd, keyWords[2], ontoRef);
					System.err.println(keyWords[2]);
					String classPackageName = NameMapper.getNameFromOntology( eventInterfaceInd, ontoRef);
										
					// get the individual describe has the text which define the event, trough: hasEventDefinition
					OWLNamedIndividual eventDefInd = ontoRef.getOnlyObjectPropertyB2Individual(
							classPackageNameInd, keyWords[3], ontoRef); */
			
				
				String classPackageName = NameMapper.getNameFromOntology( varAssegn.get( s), ontoRef);
				// create new event
				OFEventDefinition eventDef = new OFEventDefinition( classPackageName);
				initializeDefinition( varAssegn.get( s), eventDef, ontoRef);
					
				// add to list
				aggregator.addParameter( s, eventDef); //variableName
				//} else logger.addDebugStrign("Exception!!");// must be one "=" symb
			}
			
			// add all in a list (than added to static list)
			toInitialise.put( ontoRef.getOWLObjectName( e), aggregator);
		}
	}

	@Override
	public Map<?, ?> getInitialisedObject() {
		return toInitialise;
	}

	
	/**
	 * Symbol for divide parameters and accept tokens, used only in Event aggregation
	 */
	public static final String ASSEGNATION_symb = "=";
	/**
	 * System symbol to end a line, it represents the end of a command
	 */
	public static final String ENDLine_symb = System.getProperty("line.separator");
	/**
	 * Symbol to assign parameters to a variable. It represents an assegnation during Parameter definition
	 */
	public static final String ASSEGNATIONPARAMETER_symb = " ";
	/**
	 * Symbol which identify that the word used before than the next {@link #SPLIT_symb} 
	 * is a local variable. 
	 */
	public static final String VARIABLE_symb = "?";
	/**
	 * Symbol which identify that the word used before than the next {@link #SPLIT_symb} 
	 * is a the actual event instruction and no more a parameter.
	 */
	public static final String RETURN_symb = "!";
	/**
	 * It can be used after the declaration of a variable and identify the {@link OWLReferences}
	 * name i in whihc the parameter must be retrieved. If it is not specified than the
	 * corrent ontology is considered. 
	 */
	public static final String ATONTOLOGY_symb = "@";
	/**
	 * Symbol used the decide chains of computation to retrieve parameter, where 
	 * they must to contains {@link #SPLIT_symb}. An example is:
	 * {@code name.AsInteger.AsIntegerOWLDataProperty}, equivalent to write
	 * {@code AsIntegerOWLDataProperty( AsInteger( name.toString()))}; in other languages.
	 */
	public static final String COMMAND_symb = ".";
	/**
	 * Intercepts whenever null value should be given as input to computer parameter. It can
	 * be used only as a first element of parameter computation.
	 */
	public static final String NULL_symb = "^";
	/**
	 * It defines the starting point in which parameter are used inside the event definition.
	 * It must be used in the returning line defined by {@link #RETURN_symb}. Between this two symbol 
	 * no check of the name is provided.
	 */
	public static final String STARTPARAMETR_symb = "("; 
	// the name before is not used !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	/**
	 * It defines the starting point in which parameter are used inside the event definition.
	 */
	public static final String ENDPARAMETER_symb = ")";
	/**
	 * Symbol used compute tokens of every lines.
	 */
	public static final String SPLIT_symb = " ";
	/**
	 * Symbol used to define the full identify package in which all the computational
	 * method to compute parameters are located. This string is added to the name of the name of
	 * the procedure ( ex: "in: java.package." + "AsIntegerOWLDataProperty).
	 */
	public static final String IMPORT_symb = "in:";
	
	//private static final String importTockenSymb = ".";
	//private static final String javaImportingSymb = ".";

	private String importing;
	
	private void initializeDefinition( OWLNamedIndividual individualName,
			OFEventDefinition eventDef,	OWLReferences ontoRef) {
		
		// get text of the definition of the event
		OWLDataProperty prop = ontoRef.getOWLDataProperty( keyWords[ 2]); //4
		Set<OWLLiteral> literals = (Set<OWLLiteral>) ontoRef
				.getDataPropertyB2Individual(individualName, prop);
		String txt = getText( literals);
		

		// parse text, for every line
		StringTokenizer tokenised = new StringTokenizer(txt.trim(), ENDLine_symb);
		while (tokenised.hasMoreTokens()) {
			String line = tokenised.nextToken().trim();
			if( line.startsWith( IMPORT_symb)){
				// if starts with "in:" => the string after defines package class Name
				// it must be unique
				importing = line.replace( IMPORT_symb, "").trim();
			} else if (line.startsWith(VARIABLE_symb)) {
				// if starts with "?" => the string after defines a parameter
				eventDef.addToParameterMap( getParameterDefinition(line, ontoRef));
			} else if (line.startsWith(RETURN_symb)) {
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
		StringTokenizer token = new StringTokenizer(line.trim(), ASSEGNATIONPARAMETER_symb);
		if (token.countTokens() >= 2) {
			
			// get variable Name
			String varName = token.nextElement().toString()
					.replace(VARIABLE_symb, "");

			// get reference to OWL Ontology
			String nextToken = token.nextElement().toString().trim();
			OWLReferences eventRef = ontoRef;
			if (nextToken.startsWith(ATONTOLOGY_symb)) {
				// get ontology references
				nextToken = nextToken.replace(ATONTOLOGY_symb, "");
				if (!ontoRef.getOntoName().equals(nextToken))
					eventRef = OWLReferences.getOWLReferences(nextToken);
				nextToken = token.nextElement().toString().trim();
			}

			// get commands: 10.asString.asOWLClass = 
			// asOWLClass( asString( Integer 10)) --> return Object
			StringTokenizer tok = new StringTokenizer(nextToken, COMMAND_symb);
			// get initial variable 10
			String entityName = tok.nextElement().toString().trim();
			if (entityName.equals(NULL_symb))
				entityName = null;
			// get series of procedure to compute the parameter 
			// list[1] pac.kage.asString
			// list[2] pac.kage.asOWLClass
			List< String> classesName = new ArrayList< String>();
			while( tok.hasMoreTokens()){
				// get java.package.Class, merging: import + command
				StringTokenizer tok1 = new StringTokenizer( importing, COMMAND_symb);//importTockenSymb);
				String packageClassName = "";
				while( tok1.hasMoreTokens()){
					packageClassName += tok1.nextToken().trim() + COMMAND_symb; //javaImportingSymb;
				}
				packageClassName += tok.nextElement().toString().trim();
				classesName.add( packageClassName);
			}
			  
			// create new parameter and add to map
			parData = new OFEventParameterDefinition( classesName, entityName, eventRef.getOntoName());
			paraMap.put( varName, parData);
			
			logger.addDebugStrign(" ? : varName = " + varName +
					" | eventRef = "	+ parData.getOWLReferences().getOntoName() + 
					" | command = " + parData.getClassPackageName() + 
					" | input = " + parData.getInput());

		} else
			logger.addDebugStrign("Exception");
		
		return( paraMap);
	}

	// line : "!r eventMethod( ?a ?b)"[A] or "! (a? b?)"[C]
	// static ???
	private void getParameterOrder( String line, OFEventDefinition e) {
		// object to initialise in this method
		List<String> parOrder = new ArrayList<String>();
		
		// get token inside the line (it must to have at least one " ")
		StringTokenizer token = new StringTokenizer(line.trim(), ASSEGNATIONPARAMETER_symb);
		if (token.countTokens() >= 2) {
			String nameTok = token.nextElement().toString();
			// get variable Name, not used !!!
			String varName = nameTok.replace(RETURN_symb, "").trim(); // "!r"

			// get parameters order: eventMethod( Object a, Object b)
			// "eventMethod" is not used !!!
			String command = line.replace( nameTok, "").trim();
			int start = command.indexOf(STARTPARAMETR_symb);
			int end = command.indexOf(ENDPARAMETER_symb);
			command = command.substring(start + 1, end);
			StringTokenizer parameters = new StringTokenizer(command.trim(),
					SPLIT_symb);
			while (parameters.hasMoreTokens())
				parOrder.add(parameters.nextToken().trim()
						.replace(VARIABLE_symb, ""));

			// set mapped relation in EventDefinition
			e.setOrder( parOrder);
			//e.setVarName( varName);			
			logger.addDebugStrign(" ! : varName = " + varName
					+ " | parameterList = " + parOrder);
		} else
			logger.addDebugStrign("Exception");
	}
	
	public static String getText( Set<OWLLiteral> literals){
		String txt = "";
		String retTxt = null;
		// if the definition is split in more data property rebuild the text
		if( literals.size() > 1){
			for( OWLLiteral l : literals){
				String s = l.getLiteral();
				if( s.startsWith( IMPORT_symb))
					txt = s + ENDLine_symb + txt;
				if( s.startsWith( VARIABLE_symb))
					txt = txt + s + ENDLine_symb;
				if( s.startsWith( RETURN_symb))
					retTxt = s;
			}
			txt += retTxt;
		} else txt = OWLLibrary.getOnlyString( literals);
		
		return( txt);
	}
}

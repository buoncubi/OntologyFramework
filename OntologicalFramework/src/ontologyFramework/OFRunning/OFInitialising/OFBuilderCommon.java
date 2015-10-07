package ontologyFramework.OFRunning.OFInitialising;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFDataMapping.reservatedDataType.KeyWordsMapper;
import ontologyFramework.OFDataMapping.reservatedDataType.NameMapper;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInvokingManager.OFBuiltMapInvoker;

/**
 * 
 * <P>
 * This is a static class (set as non instanciable) which collects
 * static methods useful during the system initialisation through 
 * builder mechanism. Especially in {@link OFInitialiser#buildIndividual(OWLNamedIndividual, OWLReferences)} 
 * 
 * <P>
 * @author Buoncomapgni Luca
 * @version 1.0
 * @see OFInitialiser
 * @see OFBuilderInterface
 * @see KeyWordsMapper
 * @see NameMapper
 * @see OFDebugLogger
 */
public class OFBuilderCommon{

	/**
	 *  represents the name of the Object Property which link a builder
	 *  individual to its Java class. It links to an individual which
	 *  belongs to the "Name" ontological class. For this it represents
	 *  a string which has the complete.java.class.package path to a class
	 *  that implements {@link OFBuilderInterface}.
	 *  By default it is equal to {@value #CLASSPACKAGE_objProp}
	 */
	public static final String CLASSPACKAGE_objProp = "hasTypeName";//"implementsOFBuilderName";
	/**
	 *  represents the name of the Object Property which link a builder
	 *  individual to its initialised class inside the Map. It links to an individual which
	 *  belongs to the "Name" ontological class. For this, it represents
	 *  a string which is the key to refer to the output of 
	 *  {@link OFBuilderInterface#getInitialisedObject()}
	 *  from the class {@link OFBuiltMapInvoker}.
	 *  By default it is equal to {@value #BUILDLISTNAME_objProp}
	 */
	public static final String BUILDLISTNAME_objProp = "buildsList";//"buildsListName";
	/**
	 *  represents the name of the Object Property which link a builder
	 *  individual to the key words that we want inject inside the method
	 *  {@link OFBuilderInterface#buildInfo(String[], OWLReferences, OFBuiltMapInvoker)}. 
	 *  It links to an individual which
	 *  belongs to the "KeyWord" ontological class. For this, it represents
	 *  an array of strings which.
	 *  By default it is equal to {@value #HASKEYWORDS_objProp}
	 */
	public static final String HASKEYWORDS_objProp = "hasKeyWords";
	
	
	/**
	 * It is the name of the data propriety which indicates that 
	 * the system should print on console. If it is attached to an individual
	 * which belongs to the ontological class "Debugger". 
	 * By default it is set to: {@value #CONSOLEFLAG_dataProp}
	 */
	public static final String CONSOLEFLAG_dataProp = "hasDebuggingPrintOnConsole";
	/**
	 * It is the name of the data propriety which indicates that 
	 * the system should print on file. If it is attached to an individual
	 * which belongs to the ontological class "Debugger".
	 * By default it is set to: {@value #FILEFLAG_dataProp} 
	 */
	public static final String FILEFLAG_dataProp = "hasDebuggingPrintOnFile";
	/**
	 * It is the name of the data propriety which indicates that 
	 * the system should show the GUI on start up. If it is attached to an individual
	 * which belongs to the ontological class "Debugger".
	 * By default it is set to: {@value #GUIFLAG_dataProp} 
	 */
	public static final String GUIFLAG_dataProp = "hasDebuggingRunGui";
	/**
	 * It is the name of the data propriety which indicates that 
	 * the system should update the file and show log when
	 * their count reach this determinate threshold. If it is attached to an individual
	 * which belongs to the ontological class "Debugger".
	 * By default it is set to: {@value #PRINTRATE_dataProp}.
	 * Note that if the system is brutally shotted down, some logging
	 * text could be still buffered; and they would not be notified. 
	 */
	public static final String PRINTRATE_dataProp = "hasDebuggingOrderPrintRate";
		
	/**
	 *  Static class with all static method. Constructor non instatiable 
	 */
	private OFBuilderCommon() {
        throw new AssertionError();
    }
	
	/**
	 * It retrieve the OWLNamedIndividual and calls {@link #getKeyWords(OWLNamedIndividual, OWLReferences)} 
	 * 
	 * @param individual name of the ontological individual which belongs to {@link OFInitialiser#BUILDER_className} class
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return keyWords mapped keyWords from Literal to String[]
	 * 
	 * It retrieves the OWLNamedIndividual using {@link ontoRef#getOWLIndividual},
	 * and it calls {@link #getKeyWords(OWLNamedIndividual, OWLReferences)};
	 * transferring the returning value. 
	 */
	static public String[] getKeyWords( String individual, OWLReferences ontoRef) {
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individual);
		return( getKeyWords( ind, ontoRef));
	}
	/**
	 * It maps the literal, described as a keyWord in the ontology,
	 * linked to the builder individual. 
	 * 
	 * @param individual ontological individual which belongs to {@link OFInitialiser#BUILDER_className} class
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return keyWords mapped keyWords from Literal to String[]
	 * 
	 * Given an individual which represents the builder it gets the individual 
	 * which describe the keyWord, through the Object Property defined by the name
	 * {@link #HASKEYWORDS_objProp}. This, by default has the value {@value #HASKEYWORDS_objProp}.
	 * Than, this methods uses {@link KeyWordsMapper#getKeyWordFromOntology(OWLNamedIndividual, OWLReferences)}
	 * to map the actual literal into an array of String.  
	 * <pre>
	 * 	{@code BuilderIndividual € OFBuilder }         [ please refer to ... {@link OFInitialiser}]
	 * 		{@code hasKeyWords exactly 1 KeyWordInd}
	 * 			where:	{@code KeyWordInd € KeyWord}
	 * 						{@code KeyWordInd hasTypeKeyWord "key1 key2 key3"^^string} 
	 * </pre>
	 */
	static public String[] getKeyWords( OWLNamedIndividual individual, OWLReferences ontoRef) {
		// get individual which describe keyWords
		/*OWLObjectProperty prop = ontoRef.getOWLObjectProperty( HASKEYWORDS_objProp);
		Set<OWLNamedIndividual> values = ontoRef.getObjectPropertyB2Individual(individual, prop);
		OWLNamedIndividual value = (OWLNamedIndividual) ontoRef.getOnlyElement( values);
		// get keyWord as string[]
		return( KeyWordsMapper.getKeyWordFromOntology( value));*/
		return( KeyWordsMapper.getKeyWordFromOntology( individual, ontoRef));
	}
	
	
	
	/**
	 * It retrieve the OWLNamedIndividual and calls {@link #getImplementsName(OWLNamedIndividual, OWLReferences)}.
	 * 
	 * @param individualName name of the ontological individual which belongs to {@link OFInitialiser#BUILDER_className} class
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return classDirectory the Java package.class path to the class which this individual represents
	 *
	 * It computes the individual from its name; using {@link ontoRef#getOWLIndividual(String, OWLReferences)}.
	 * Then calls {@link #getImplementsName(OWLNamedIndividual, OWLReferences)}.
	 * And propagates its returning value.
	 */
	static public String getImplementsName( String individualName, OWLReferences ontoRef) {
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( getImplementsName( ind, ontoRef));
	}
	/**
	 * Retrieve a reference to the class that, the builder individual addresses to
	 * 
	 * @param individual ontological individual which belongs to {@link OFInitialiser#BUILDER_className} class
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return classDirectory the Java package.class path to the class which this individual represents
	 * 
	 * It retrieves the individual which is linked trough the Object Property named: {@value #CLASSPACKAGE_objProp}
	 * (defined in the field {@link #CLASSPACKAGE_objProp}). Finally, it uses it with
	 * {@link NameMapper#getNameFromOntology(OWLNamedIndividual, OWLReferences)}
	 * the return the name as a String.
	 * <pre>
	 * {@code BuilderIndividual € OFBuilder }         [ please refer to ... {@link OFInitialiser}]
	 * 		{@code implementsOFBuilderName exactly 1 PathName}
	 * 			where: 	{@code PathName € Name}
	 * 						{@code PathName hasTypeName "complete.path.package.class"^^string}
	 *  </pre>
	 */
	static public String getImplementsName( OWLNamedIndividual individual, OWLReferences ontoRef){
		OWLObjectProperty prop = ontoRef.getOWLObjectProperty( CLASSPACKAGE_objProp);
		return( getNameFromIndividual( individual, prop, ontoRef));
	}

	
	
	/**
	 * It retrieve the OWLNamedIndividual and calls {@link #getBuildedListName(OWLNamedIndividual, OWLReferences)}.
	 * 
	 * @param individualName name of the ontological individual which belongs to {@link OFInitialiser#BUILDER_className} class
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return listName the name that will be used as a key in the map {@link OFBuiltMapInvoker} to address this data
	 * 
	 * It computes the individual from its name; using {@link ontoRef#getOWLIndividual(String, OWLReferences)}.
	 * Then calls {@link #getImplementsName(OWLNamedIndividual, OWLReferences)}.
	 * And propagates its returning value.
	 * 
	 */
	static public String getBuildedListName( String individualName, OWLReferences ontoRef) {
		OWLNamedIndividual ind = ontoRef.getOWLIndividual( individualName);
		return( getBuildedListName( ind, ontoRef));
	}
	/**
	 * It compute the name that will be used to stored the builded classes 
	 * 
	 * @param individual ontological individual which belongs to {@link OFInitialiser#BUILDER_className} class
	 * @param ontoRef reference to the ontology in which the individual is belong to
	 * @return listName the name that will be used as a key in the map {@link OFBuiltMapInvoker} to address this data
	 * 
	 *  It retrieves the individual which is linked trough the Object Property named: {@value #BUILDLISTNAME_objProp}
	 * (defined in the field {@link #BUILDLISTNAME_objProp}). Finally, it uses it with
	 * {@link NameMapper#getNameFromOntology(OWLNamedIndividual, OWLReferences)}
	 * the return the name as a String.
	 * <pre>
	 * {@code BuilderIndividual € OFBuilder }         [ please refer to ... {@link OFInitialiser}]
	 * 		{@code hasListName exactly 1 ListName}
	 * 			where: 	{@code ListName € Name}
	 * 						{@code ListName hasTypeName "StaticBuildedList"^^string}
	 *  </pre>
 	 */
	static public String getBuildedListName( OWLNamedIndividual individual, OWLReferences ontoRef) {
		/*OWLObjectProperty prop = ontoRef.getOWLObjectProperty( BUILDLISTNAME_objProp);
		return( getNameFromIndividual( individual, prop));*/
		OWLDataProperty prop = ontoRef.getOWLDataProperty( "buildsList");
		OWLLiteral value = ontoRef.getOnlyDataPropertyB2Individual( individual, prop);
		return( value.getLiteral());
	}
	
	
	/**
	 * It initialises the main debugging property
	 * 
	 * @param individual which defines the debugging Property
	 * @param listInvoker to retrieve already builded classes
	 * @param ontoRef the reference to the ontology in which the individual belongs to.
	 *
	 * It initialises the class {@link OFDebugLogger} with the information stored
	 * in the only (if more are available one will be picked up) individual belong to  the ontological class {@value ontologyFramework.OFErrorManagement.DebuggingClassFlagData#DEBUGGER_classFlags}
	 * (where its name is described by: {@link ontologyFramework.OFErrorManagement.DebuggingClassFlagData#DEBUGGER_classFlags}).
	 * In particular, given an individual X, it makes four steps:
	 * <pre>
	 * 	{@code 1 -> get the only individual attached to X trough} 
	 * 				{@link #CONSOLEFLAG_dataProp}, {@code which has value} {@value #CONSOLEFLAG_dataProp}. 
	 * 				{@code Compute the boolean value and uses it to call}
	 * 				{@link OFDebugLogger#setPrintOnConsole(Boolean)}
	 * 	
	 * 	{@code 2 -> get the only individual attached to X trough}
 	 * 				{@link #FILEFLAG_dataProp}, {@code which has value} {@value #FILEFLAG_dataProp}.
 	 * 				{@code Compute the string value and uses it to call}
	 * 				{@link OFDebugLogger#setPrintOnFile(String)}
	 * 
	 *  {@code 3 -> get the only individual attached to X trough}
 	 * 				{@link #PRINTRATE_dataProp}, {@code which has value} {@value #PRINTRATE_dataProp}.
 	 * 				{@code Compute the integer value and uses it to call}
	 * 				{@link OFDebugLogger#setOrderPrintingRate(Integer)}
	 * 
	 *  {@code 4 -> get the only individual attached to X trough}
 	 * 				{@link #GUIFLAG_dataProp}, {@code which has value} {@value #GUIFLAG_dataProp}.
 	 * 				{@code Compute the boolean value and uses it to start the GUI}
	 * 			
	 * </pre>
	 */
	static public synchronized void buildDebugger( OWLNamedIndividual individual, OFBuiltMapInvoker listInvoker, OWLReferences ontoRef){
		// get print on console flag
		OWLDataProperty prop = ontoRef.getOWLDataProperty( CONSOLEFLAG_dataProp);
		OWLLiteral value = ontoRef.getOnlyDataPropertyB2Individual(individual, prop);
		Boolean consoleFlag = Boolean.valueOf(value.getLiteral());
		OFDebugLogger.setPrintOnConsole(consoleFlag);
		
		// get print on file path
		prop = ontoRef.getOWLDataProperty(FILEFLAG_dataProp );
		value = ontoRef.getOnlyDataPropertyB2Individual(individual, prop);
		String fileFlag = String.valueOf( value.getLiteral());
		OFDebugLogger.setPrintOnFile( fileFlag);
		
		// get ordering and printing rate
		prop = ontoRef.getOWLDataProperty( PRINTRATE_dataProp);
		value = ontoRef.getOnlyDataPropertyB2Individual(individual, prop);
		Integer orderRate = Integer.valueOf( value.getLiteral());
		OFDebugLogger.setOrderPrintingRate(orderRate);
		
		// get show GUI flag
		prop = ontoRef.getOWLDataProperty( GUIFLAG_dataProp);
		value = ontoRef.getOnlyDataPropertyB2Individual(individual, prop);
		Boolean guiFlag = Boolean.valueOf(value.getLiteral());
		OFDebugLogger.setStartGui(guiFlag);
	}
	
	// calls NameMapper for the only individual in the set
	static private String getNameFromIndividual( OWLNamedIndividual individual, OWLObjectProperty propName, OWLReferences ontoRef){
		/*Set<OWLNamedIndividual> values = ontoRef.getObjectPropertyB2Individual( individualName, propName);
		String name = null;
		for( OWLNamedIndividual v : values){
			name = NameMapper.getNameFromOntology( v);
			break;
		}
		return( name);*/
		return(NameMapper.getNameFromOntology( individual, ontoRef));
	}
	
}
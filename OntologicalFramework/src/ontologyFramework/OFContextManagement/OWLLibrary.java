package ontologyFramework.OFContextManagement;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ontologyFramework.OFErrorManagement.OFDebugLogger;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;


/**
 * This  class implement several common procedure
 * for manipulating entity inside an ontology, using
 * OWL api 3.0
 * 
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 */
public class OWLLibrary {

	/**
	 * Full qualifier of the Pellet reasoner Factory. String to be called by
	 * Java reflection to instantiate a Reasoner.
	 */
	public static final String PELLET_reasonerFactoryQualifier = "com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory";
	/**
	 * Full qualifier of the Snorocket reasoner Factory. String to be called by
	 * Java reflection to instantiate a Reasoner.
	 */
	public static final String SNOROCKET_reasonerFactoryQualifier = "au.csiro.snorocket.owlapi3.SnorocketReasonerFactory";
	/**
	 * Full qualifier of the Hermit reasoner Factory. String to be called by
	 * Java reflection to instantiate a Reasoner.
	 */
	public static final String HERMIT_reasonerFactoryQualifier = "org.semanticweb.HermiT.Reasoner$ReasonerFactory";
	/**
	 * Full qualifier of the Fact++ reasoner Factory. String to be called by
	 * Java reflection to instantiate a Reasoner.
	 */
	public static final String FACTPLUSPLUS_reasonerFactoryQualifier = "uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory";
	
	// get debugger
	@SuppressWarnings("unused")
	private  final String OWLDERDEBUG_individualName = "C_OWLDebug";
	private OFDebugLogger logger = new OFDebugLogger( this, false);//DebuggingClassFlagData.getFlag( OWLDERDEBUG_individualName));
	private static  OFDebugLogger loggerReasoner = new OFDebugLogger( OWLLibrary.class, true);
	private  final static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	private  final List<OWLOntologyChange> changeList = new ArrayList<OWLOntologyChange>(); 

	// create ontology manager
	/**
	 * creates and returns a new OWLOntologyManager. 
	 * If the parameter has not null values of:
	 * {@link OWLReferences#getIriFilePath()} and
	 * {@link OWLReferences#getOntologyPath()} 
	 * that this method set the manager using: 
	 * {@code manager.addIRIMapper( new SimpleIRIMapper( ontoPath, filePath))}  
	 * 
	 * @param OWLReferences reference to the ontology.
	 * @return the manager of the ontology refereed by the parameter. 
	 */
	 public OWLOntologyManager createOntologyManager( OWLReferences OWLReferences){
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI iriOnto = OWLReferences.getIriOntologyPath();
		IRI iriFile = OWLReferences.getIriFilePath();
		if( iriOnto != null)
			if( iriFile != null){
				SimpleIRIMapper mapper = new SimpleIRIMapper( iriOnto, iriFile);
				manager.addIRIMapper( mapper); // load from file
			}
			//else  empty manager ... load from web
		else System.out.println( "EXCEPTION");
		return( manager);
	}
	
	// create new ontology
	/**
	 * Creates an new empty ontology in accord to the 
	 * {@link OWLReferences#getIriOntologyPath()}. It will return null 
	 * if the ontology path associate to the parameter is null. 
	 * 
	 * @param OWLReferences reference to the ontology.
	 * @return a new empty ontology in accord with the parameter.
	 * @throws OWLOntologyCreationException
	 */
	 public OWLOntology createOntology( OWLReferences OWLReferences) throws OWLOntologyCreationException{
		 long initialTime = System.nanoTime();
		 IRI iri = OWLReferences.getIriOntologyPath();
		 if( iri != null){
			 OWLOntology out = OWLReferences.getManager().createOntology( iri);
			 logger.addDebugStrign( "ontology created in: " + (System.nanoTime() - initialTime) + " [ns]");
			 return( out);
		 }else{
		     System.out.println( "EXCEPTION");
			 return( null);
		 }
	}
	// load ontology
	/**
	 * It loads an ontology from file in accord with the function parameter; 
	 * to do so the method uses the ontology manager 
	 * from: {@link OWLReferences#getManager()}.
	 * It will return null if {@link OWLReferences#getIriOntologyPath()} 
	 * is null. 
	 * 
	 * @param OWLReferences reference to the ontology.
	 * @return a pointer to the ontology refered by the parameter,
	 * @throws OWLOntologyCreationException
	 */
	 public OWLOntology loadOntologyFromFile( OWLReferences OWLReferences) throws OWLOntologyCreationException{
		 long initialTime = System.nanoTime();
		 IRI iri = OWLReferences.getIriOntologyPath();
		 if( iri != null){
			 OWLOntology out = OWLReferences.getManager().loadOntology( iri);
			 logger.addDebugStrign( "ontology loaded from file in: " + (System.nanoTime() - initialTime) + " [ns]");
			 return( out); 
		 }else{
			System.out.println( "EXCEPTION");
			return( null);
		 }
	}
	/**
	 * It loads an ontology where its {@link OWLReferences#getIriOntologyPath()} 
	 * defines a path to be browsed into the web. It returns null if the IRI
	 * ontology Path is null.
	 * 
	 * @param ontoRef reference to the ontology.
	 * @return a pointer to the ontology refered by the parameter,
	 * @throws OWLOntologyCreationException
	 */
	 public OWLOntology loadOntologyFromWeb( OWLReferences ontoRef) throws OWLOntologyCreationException{
		 long initialTime = System.nanoTime();
		 IRI iri = ontoRef.getIriOntologyPath();
		 if( iri != null){
			 OWLOntology out = ontoRef.getManager().loadOntologyFromOntologyDocument( iri);
			 logger.addDebugStrign( "ontology loaded from web in: " + (System.nanoTime() - initialTime) + " [ns]");
			 return( out);
		 }else{
		     System.out.println( "EXCEPTION");
			 return( null);
		 }
	}
	
	// get prefix
	/**
	 * Returns a prefix manager to be attached into an ontolofy manager
	 * to simplify IRI definitions and usage 
	 * 
	 * @param OWLReferences a reference to an OWL ontology.
	 * @return a prefix manager format.
	 */
	 public PrefixOWLOntologyFormat getPrefixFormat( OWLReferences OWLReferences) {
		 long initialTime = System.nanoTime();
		 PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) OWLReferences.getManager().getOntologyFormat( OWLReferences.getOntology());
		 pm.setDefaultPrefix( OWLReferences.getIriOntologyPath() + "#");
		 logger.addDebugStrign( "prefix manager given in: " + (System.nanoTime() - initialTime) + " [ns]");
		 return (pm);
	}
	
	// get data factory
	/**
	 * Returns the OWLDataFactory associate to the OWLManager 
	 * associate to the parameter.
	 * 
	 * @param OWLReferences
	 * @return an OWL data factory
	 */
	 public OWLDataFactory getOWLDataFactory( OWLReferences OWLReferences){
		 long initialTime = System.nanoTime();
		 OWLDataFactory out = OWLReferences.getManager().getOWLDataFactory();
		 logger.addDebugStrign( "OWLDataFactory given n: " + (System.nanoTime() - initialTime) + " [ns]");
		 return( out);
	 }
	
	/**
	 * It creates and returns a Reasoner instance. The type of 
	 * reasoner is defined by the reasoner name factory, which could be:
	 * {@link #PELLET_reasonerFactoryQualifier}, {@link #SNOROCKET_reasonerFactoryQualifier},
	 * {@link #HERMIT_reasonerFactoryQualifier} or {@link #FACTPLUSPLUS_reasonerFactoryQualifier}.
	 * The created reasoner, will be attached to the ontology references given
	 * as parameter. If buffering flag is true than the reasoner will update its 
	 * state only if {@code reasoner.flush()} is called. Otherwise this reasoner
	 * will synchronizes itself at any applied ontological changes. The system 
	 * will return null if a Reflaction error occurs in instancing the
	 * class defined by the parameter reasonerFactoryName. 
	 * 
	 * @param reasonerFactoryName full qualifier to the reasoner factory.
	 * @param ontoRef references to the OWL ontology.
	 * @param buffering flag.
	 * @return a new instance to the specified reasoner.
	 */
	 public OWLReasoner getReasoner( String reasonerFactoryName, OWLReferences ontoRef, boolean buffering){
		 long initialTime = System.nanoTime();
		try {
			OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class.forName(reasonerFactoryName).newInstance();
			OWLReasoner reasoner;
			OFReasonerProgressMonitor progressMonitor = new OFReasonerProgressMonitor();
			progressMonitor.setReasonerName( ontoRef.getOntoName());//reasonerFactoryName.substring(
					//reasonerFactoryName.lastIndexOf(".") + 1 ).replace( "ReasonerFactory", ""));
	        OWLReasonerConfiguration config = new SimpleConfiguration( progressMonitor);
			if( buffering){
				reasoner = reasonerFactory.createReasoner( ontoRef.getOntology(), config);
			}else{
				reasoner = reasonerFactory.createNonBufferingReasoner( ontoRef.getOntology(), config);
			}
			ontoRef.getManager().addOntologyChangeListener( (OWLOntologyChangeListener) reasoner );
			logger.addDebugStrign( "Reasoner created in: " + (System.nanoTime() - initialTime) + " [ns]");
			return reasoner;
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return( null);
	}
	/**
	 * Returns an instance of the Pellet reasoner. If buffering is true
	 * than the reasoner is update only when {@code reasoner.flush()} is called.
	 * Otherwise returns a reasoner which synchronizes itself at any ontological
	 * changes.
	 * 
	 * @param ontoRef references to the OWL ontology
	 * @param buffering flag
	 * @return a new Pellet reasoner instance
	 */
	 public OWLReasoner getPelletReasoner( OWLReferences ontoRef, boolean buffering){
		OWLReasoner pellet = getReasoner( PELLET_reasonerFactoryQualifier, ontoRef, buffering);
		return( pellet);
	}
	/**
	 * Returns an instance of the Snorocket reasoner. If buffering is true
	 * than the reasoner is update only when {@code reasoner.flush()} is called.
	 * Otherwise returns a reasoner which synchronizes itself at any ontological
	 * changes.
	 * 
	 * @param ontoRef references to the OWL ontology
	 * @param buffering flag
	 * @return a new Snorocket reasoner instance
	 */
	 public OWLReasoner getSnorocketReasoner( OWLReferences ontoRef, boolean buffering){
		return( getReasoner( SNOROCKET_reasonerFactoryQualifier, ontoRef, buffering));
	}
	/**
	 * Returns an instance of the Hermit reasoner. If buffering is true
	 * than the reasoner is update only when {@code reasoner.flush()} is called.
	 * Otherwise returns a reasoner which synchronizes itself at any ontological
	 * changes.
	 * 
	 * @param ontoRef references to the OWL ontology
	 * @param buffering flag
	 * @return a new Hermit reasoner instance
	 */
	 public OWLReasoner getHermitReasoner( OWLReferences ontoRef, boolean buffering){
		return( getReasoner( HERMIT_reasonerFactoryQualifier, ontoRef, buffering));
	}
	/**
	 * Returns an instance of the Fact++ reasoner. If buffering is true
	 * than the reasoner is update only when {@code reasoner.flush()} is called.
	 * Otherwise returns a reasoner which synchronizes itself at any ontological
	 * changes.
	 * 
	 * @param ontoRef references to the OWL ontology
	 * @param buffering flag
	 * @return a new Fact++ reasoner instance
	 */
	 public OWLReasoner getFactReasoner( OWLReferences ontoRef, boolean buffering){
		return( getReasoner( FACTPLUSPLUS_reasonerFactoryQualifier, ontoRef, buffering));
	}
	
	// print ontology on console
	/**
	 * It prints the ontology over console using Manchester formatting. 
	 * 
	 * @param ontoRef reference to an OWL ontology
	 * @throws OWLOntologyStorageException
	 */
	 public void printOntonolyOnConsole( OWLReferences ontoRef) throws OWLOntologyStorageException {
		try{
			long initialTime = System.nanoTime();
			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
			OWLOntologyManager man = ontoRef.getManager();
			OWLOntology ont = ontoRef.getOntology();
			OWLOntologyFormat format = man.getOntologyFormat(ont);
			if (format.isPrefixOWLOntologyFormat())
				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
			man.saveOntology( ont, manSyntaxFormat, new StreamDocumentTarget(System.out));
			logger.addDebugStrign( "ontology printed in console in: " + (System.nanoTime() - initialTime) + " [ns]");
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}

	
	/**
	 * If the Ontology is consistent it will synchronize a buffering reasoner
	 * calling {@code reasoner.flush()}; if the reasoner has a false buffering 
	 * flag, than this method has no effects. If an inconsistency error 
	 * occurs than this method will print over console an explanation of the 
	 * error. Note that if the ontology is inconsistent than all the methods
	 * in this class may return a null value.
	 * 
	 * @param ontoRef references to an OWL ontology.
	 */
	  public void synchroniseReasoner( OWLReferences ontoRef){
		if( ontoRef.isConsistent()){
			try{
				Long initialTime = System.nanoTime();//System.currentTimeMillis();
				ontoRef.getReasoner().flush();
				Long finalTime = System.nanoTime(); //System.currentTimeMillis()
				loggerReasoner.addDebugStrign( " synchronising... reasoner.flush() for ontology named: " +  
						". Reasoning Time: " + ( finalTime - initialTime) + " [ns]" + " over ontology: " + ontoRef.getOntology() +
						" reasoned axioms " + "0");//getInfferedAxiomCount( ontoRef));
			} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
				notifyPelletException( ontoRef);
			}
		} else {
			ontoRef.checkConsistent();
		}
	}
	  
	public Integer getInfferedAxiomCount( OWLReferences ontoRef){
		Integer count = 0;
		Set< OWLNamedIndividual> allIndividuals = ontoRef.getIndividualB2Class( ontoRef.getFactory().getOWLThing());
		Set<OWLObjectProperty> allObjProp = ontoRef.getOntology().getObjectPropertiesInSignature( true);
		Set<OWLDataProperty> allDataProp = ontoRef.getOntology().getDataPropertiesInSignature( true);
		OWLReasoner reasoner = ontoRef.getReasoner();
		for( OWLNamedIndividual i : allIndividuals){
			// export object Property
			for( OWLObjectProperty p : allObjProp){ 
				// for all the object property in the ontology
				Set< OWLNamedIndividual> indWithThisProp = reasoner.getObjectPropertyValues(i, p).getFlattened();
				count = count + indWithThisProp.size();
			}
			// export data prop
			for( OWLDataProperty p : allDataProp){ 
				// for all the data property in the ontology
				Set< OWLLiteral> indWithThisProp = reasoner.getDataPropertyValues(i, p);
				count = count + indWithThisProp.size();
			}
			// export class 
			Set<OWLClass> allClass = reasoner.getTypes( i, false).getFlattened();
			count = count + allClass.size();
		}
		return( count);
	}
	
	// get from ontology
	/**
	 * Returns an Object which represents an ontological class
	 * with a given name and specifics IRI paths. If the entity
	 * already exists in the ontology than the object will refer to it, 
	 * otherwise the method will create a new ontological entity.
	 * 
	 * @param className string to define the name of the ontological class
	 * @param ontoRef reference to an OWL ontology.
	 * @return the OWL class with the given name and IRI paths in accord to the OWLReference   
	 */
	 public OWLClass getOWLClass( String className, OWLReferences ontoRef) {
		 long initialTime = System.nanoTime();
		 OWLClass classObj = ontoRef.getFactory().getOWLClass(className, ontoRef.getPm());
		 logger.addDebugStrign( "OWLClass [" + className + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
		 return (classObj);
	}
	/**
	 * Returns an Object which represents an onological individual
	 * with a given name and specific IRI paths. If the entity
	 * already exists in the entology than the object will refer to it, 
	 * otherwise the method will create a new ontological entity.
	 * 
	 * @param individualName string to define the name of the ontological individual
	 * @param ontoRef reference to an OWL ontology.
	 * @return the OWL individual with the given name and IRI paths in accord to the OWLReference
	 */
	 public OWLNamedIndividual getOWLIndividual( String individualName, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		 OWLNamedIndividual individualObj = ontoRef.getFactory().getOWLNamedIndividual(
				":" + individualName, ontoRef.getPm());
		 logger.addDebugStrign( "OWLNamedIndividual [" + individualName + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
		 return (individualObj);
	}
	/**
	 * Returns an Object which represents an onological data property
	 * with a given name and specific IRI paths. If the entity
	 * already exists in the entology than the object will refer to it, 
	 * otherwise the method will create a new ontological entity.
	 * 
	 * @param dataPropertyName string to define the name of the ontological data property
	 * @param ontoRef reference to an OWL ontology.
	 * @return the OWL data property with the given name and IRI paths in accord to the OWLReference
	 */
	 public OWLDataProperty getOWLDataProperty(String dataPropertyName, OWLReferences ontoRef) {
		 long initialTime = System.nanoTime();
		 OWLDataProperty property = ontoRef.getFactory().getOWLDataProperty(":"
				+ dataPropertyName, ontoRef.getPm());
		 logger.addDebugStrign( "OWLDataProperty [" + dataPropertyName +"] given in: " + (System.nanoTime() - initialTime) + " [ns]");
		 return (property);
	}
	/**
	 * Returns an Object which represents an onological object property
	 * with a given name and specific IRI paths. If the entity
	 * already exists in the entology than the object will refer to it, 
	 * otherwise the method will create a new ontological entity.
	 * 
	 * @param objPropertyName string to define the name of the ontological object property
	 * @param ontoRef reference to an OWL ontology.
	 * @return the OWL object property with the given name and IRI paths in accord to the OWLReference
	 */
	 public OWLObjectProperty getOWLObjectProperty( String objPropertyName, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		 OWLObjectProperty property = ontoRef.getFactory().getOWLObjectProperty(
				":"	+ objPropertyName, ontoRef.getPm());
		 logger.addDebugStrign( "OWLObjectProperty [" + objPropertyName + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
		 return (property);
	}
	/**
	 * Returns an Object which represents an onological literal
	 * with a given value and specific IRI paths. Indeed it calls:
	 * {@code this.getOWLLiteral( value, null, ontoRef)}.
	 * 
	 * @param value object to define the value of the ontological literal
	 * @param ontoRef reference to an OWL ontology.
	 * @return the OWL literal with the given value, type and IRI paths in accord to the OWLReference
	 */
	 public OWLLiteral getOWLLiteral( Object value, OWLReferences ontoRef){
		return( getOWLLiteral( value, null, ontoRef));
	}
	/**
	 * Given an Object value this method returns the OWLLiteral in accord with the
	 * actual type of value. The parameter Type can be null if value is of type:
	 * String, Integer, Boolean, Float, Long; otherwise this method will returns null.
	 * For more specific data type this methods require to give in input the 
	 * right OWLDataType parameter. Generally it will return null if the 
	 * data type of the parameter value is unknown.  
	 * 
	 * @param value object to define the value of the ontological literal
	 * @param type the OWL data type to define the literal
	 * @param ontoRef reference to an OWL ontology.
	 * @return the OWL literal with the given value, type and IRI paths in accord to the OWLReference
	 */
	 public OWLLiteral getOWLLiteral( Object value, OWLDatatype type, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		OWLLiteral liter = null;
		if( value instanceof String)
			liter = ontoRef.getFactory().getOWLLiteral( (String) value);
		else if( value instanceof Integer)
			liter = ontoRef.getFactory().getOWLLiteral( (Integer) value);
		else if( value instanceof Boolean)
			liter = ontoRef.getFactory().getOWLLiteral( (Boolean) value);
		else if( value instanceof Float)
			liter = ontoRef.getFactory().getOWLLiteral( (Float) value);
		else if( value instanceof Double){
			Float tmp = ((Double) value).floatValue();
			liter = ontoRef.getFactory().getOWLLiteral( (Float) tmp);
		}else if( value instanceof Long)
			liter = ontoRef.getFactory().getOWLLiteral( String.valueOf( value),
					ontoRef.getFactory().getOWLDatatype(OWL2Datatype.XSD_LONG.getIRI()));
		else if( value instanceof OWLLiteral)
			liter = (OWLLiteral) value;
		else if( type != null)
			liter = ontoRef.getFactory().getOWLLiteral( String.valueOf( value), type);
		else System.out.println("EXCEPTION: type for literal not known");
		logger.addDebugStrign( "OWLLitteral [" + liter + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
		return (liter);
	}

	
	/**
	 * It returns all the ontological individual which are defined in the 
	 * refereed ontology and which are belonging to the calss with name 
	 * defined by the parameter. Indeed this method will call 
	 * {@link #getOWLClass(String, OWLReferences)},
	 * to get the actual OWL class Object and than it use it to call
	 * {@link #getIndividualB2Class(OWLClass, OWLReferences)}. Than the 
	 * returning value is propagated, so it returns null if no individual are
	 * classified in that class or if such class does not exist in 
	 * the refereed ontology.
	 * 
	 * @param className name of the ontological calss
	 * @param ontoRef reference to an OWL ontology.
	 * @return an not ordered set of individual belong to such class.
	 */
	 public Set<OWLNamedIndividual> getIndividualB2Class( String className, OWLReferences ontoRef){
		return( getIndividualB2Class( getOWLClass( className, ontoRef), ontoRef));
	 }
	/**
	 * It returns all the ontological individual which are defined in the 
	 * refereed ontology and which are belonging to the calss 
	 * defined by the parameter. It returns null if no individual are
	 * classified in that class or if such class does not exist in 
	 * the refereed ontology.
	 * 
	 * @param ontoClass OWL class for which the individual are asked.
	 * @param ontoRef reference to an OWL ontology.
	 * @return an not ordered set of individual belong to such class.
	 */
	 public Set<OWLNamedIndividual> getIndividualB2Class( OWLClass ontoClass, OWLReferences ontoRef){
		long initialTime = System.nanoTime();
		try{
			Set< OWLNamedIndividual> out = new HashSet< OWLNamedIndividual>();
			Set<OWLIndividual> set = ontoClass.getIndividuals( ontoRef.getOntology());
			if( set != null){
				for( OWLIndividual s : set)
					out.add( s.asOWLNamedIndividual());
				out.addAll( ontoRef.getReasoner().getInstances( ontoClass, false).getFlattened());
				logger.addDebugStrign( "Individual belong to class [" + getOWLObjectName(ontoClass) + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
				return( out);
			}else return( null);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		} catch( java.lang.NullPointerException e){
			notifyNullExceptions( ontoClass.toString() );
			return( null);
		}
	}
	/**
	 * It returns one ontological individual which are defined in the 
	 * refereed ontology and which are belonging to the calss with name 
	 * defined by the parameter. Indeed this method will call {@link #getOWLClass(String, OWLReferences)},
	 * to get the actual OWL class Object and than it use it to call
	 * {@link #getIndividualB2Class(OWLClass, OWLReferences)}. Than,
	 * using {@link #getOnlyElement(Set)} it will return one
	 * individual that are belongign to the class. It returns null if no individual are
	 * classified in that class, if such class does not exist in 
	 * the refereed ontology or if the Set returned by 
	 * {@code this.getIndividualB2Class( .. )} has {@code size > 1}.
	 * 
	 * @param className name of the ontological calss
	 * @param ontoRef reference to an OWL ontology.
	 * @return an individual belong to such class.
	 */
	 public OWLNamedIndividual getOnlyIndividualB2Class( String className, OWLReferences ontoRef){
		Set<OWLNamedIndividual> set = getIndividualB2Class( getOWLClass( className, ontoRef), ontoRef);
		return( (OWLNamedIndividual) getOnlyElement(set));
	 }
	/**
	 * It returns an ontological individual which are defined in the 
	 * refereed ontology and which are belonging to the calss 
	 * defined by the parameter. It returns null if no individual are
	 * classified in it, if such class does not 
	 * exist  or if there are more than one
	 * individual classified in that class 
	 * (since it uses {@link #getOnlyElement(Set)}). 
	 * 
	 * @param ontoClass OWL class for which the individual are asked.
	 * @param ontoRef reference to an OWL ontology.
	 * @return an individual belong to such class.
	 */
	 public OWLNamedIndividual getOnlyIndividualB2Class( OWLClass ontoClass, OWLReferences ontoRef){
		long initialTime = System.nanoTime();
		try{
			Set< OWLNamedIndividual> out = new HashSet< OWLNamedIndividual>();
			Set<OWLIndividual> set = ontoClass.getIndividuals( ontoRef.getOntology());
			if( set != null){
				for( OWLIndividual s : set)
					out.add( s.asOWLNamedIndividual());
				out.addAll( ontoRef.getReasoner().getInstances( ontoClass, false).getFlattened());
				logger.addDebugStrign( "Only individual belong to class given in: " + (System.nanoTime() - initialTime) + " [ns]");
				return( (OWLNamedIndividual) getOnlyElement( out));
			}else return(null);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		} catch( java.lang.NullPointerException e){
			notifyNullExceptions( ontoClass.toString() );
			return( null);
		}
	}
	
	/**
	 * It returns the set of classes in which an individual has been
	 * classified.
	 * 
	 * @param individual ontological individual object
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not ordered set of all the classes where the 
	 * individual is belonging to.
	 */
	 public Set< OWLClass> getIndividualClasses( OWLNamedIndividual individual, OWLReferences ontoRef){
		return( ontoRef.getReasoner().getTypes( individual, false).getFlattened());
	 }
	
	/**
	 * Returns the set of literal value relate to an OWL Data Property 
	 * which has a specific name and which is assign to a given individual. 
	 * Indeed it retrieves OWL object from strings and calls: 
	 * {@link #getDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLReferences)}.
	 * Than its returning value is propagated.
	 * 
	 * @param individualName name to the ontological individual belonging to the refering ontology
	 * @param propertyName data property name applied to the ontological individual belonging to the refering ontology
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not ordered set of literal value of such property applied to a given individual
	 */
	 public Set<OWLLiteral> getDataPropertyB2Individual( String individualName, String propertyName, OWLReferences ontoRef){
		OWLNamedIndividual ind = getOWLIndividual( individualName, ontoRef);
		OWLDataProperty prop = getOWLDataProperty( propertyName, ontoRef); 
		return( getDataPropertyB2Individual( ind, prop, ontoRef));
	 }
	/**
	 * Returns the set of literal value relate to an OWL Data Property 
	 * and assigned to a given individual. It returns null if such data property or
	 * individual doesn not exist. Also if the individual has not such
	 * proprerty.
	 * 
	 * @param individual the OWL individual belonging to the refering ontology
	 * @param property the OWL data property applied to the ontological individual belonging to the refering ontology
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not ordered set of literal value of such property applied to a given individual
	 */
	public Set<OWLLiteral> getDataPropertyB2Individual( OWLNamedIndividual individual, OWLDataProperty property, OWLReferences ontoRef){
		try{
			long initialTime = System.nanoTime();
			Set<OWLLiteral>  value = individual.getDataPropertyValues(property, ontoRef.getOntology());
			Set<OWLLiteral> valueInf = ontoRef.getReasoner().getDataPropertyValues( individual, property);
			valueInf.addAll( value);
				
			//notifyDebugger( getOWLObjectName( individual), getOWLObjectName( property), 
					//	getOWLLiteralAsString( valueInf).toString(), ontoRef.getOntoName());
			logger.addDebugStrign( "get data property [" + getOWLObjectName( property) + "] belong to individual [" + getOWLObjectName( individual) + ") given in: " + (System.nanoTime() - initialTime) + " [ns]");		
			return( valueInf);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
				notifyPelletException( ontoRef);
				return( null);
		} catch( java.lang.NullPointerException e){
				notifyNullExceptions( individual + " " + property);
				return( null);
		}
	}
	/**
	 * Returns one literal value attached to a given individual
	 * througth a specific data property. Here both, individual and property, are given
	 * by name, than the system calls {@link #getOnlyDataPropertyB2Individual(String, String, OWLReferences)}
	 * and its returning value is used with {@link #getOnlyElement(Set)}.
	 * 
	 * @param individualName name to the ontological individual belonging to the refering ontology
	 * @param propertyName data property name applied to the ontological individual belonging to the refering ontology
	 * @param ontoRef reference to an OWL ontology.
	 * @return a literal value of such property applied to a given individual
	 */
	 public OWLLiteral getOnlyDataPropertyB2Individual( String individualName, String propertyName, OWLReferences ontoRef){
		Set<OWLLiteral> set = getDataPropertyB2Individual( individualName, propertyName, ontoRef);
		return( (OWLLiteral) getOnlyElement( set));
	 }
	/**
	 * Returns one litteral value attached to a given OWL individual 
	 * througth an OWL data property. This returns null if {@link #getDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLReferences)}
	 * or {@link #getOnlyElement(Set)} return null.
	 * 
	 * @param individual the OWL individual belonging to the refering ontology
	 * @param property the OWL data property applied to the ontological individual belonging to the refering ontology
	 * @param ontoRef reference to an OWL ontology.
	 * @return a literal value of such property applied to a given individual
	 */
	 public OWLLiteral getOnlyDataPropertyB2Individual( OWLNamedIndividual individual, OWLDataProperty property, OWLReferences ontoRef){
		try{
			Set<OWLLiteral> set = getDataPropertyB2Individual( individual, property, ontoRef);
			return( (OWLLiteral) getOnlyElement( set));
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		} catch( java.lang.NullPointerException e){
			notifyNullExceptions( individual + " " + property);
			return( null);
		}
	 }

	
	/**
	 * Returns all the values (individuals) to an Object property, given by name,
	 * linked to an individual, given by name as well. Indeed it retrueve the OWL
	 * Objects by name using {@link #getOWLObjectProperty(String, OWLReferences)}
	 * and {@link #getOWLIndividual(String, OWLReferences)}. 
	 * Than it calls {@link #getObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLReferences)}
	 * propagating its returning value.
	 * 
	 * @param individualName the name of an ontological individual 
	 * @param propertyName the name of an ontological object property
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not ordered set of all the values (OWLNamedIndividual) that
	 * the individual has w.r.t. such object property. 
	 */
	 public Set<OWLNamedIndividual> getObjectPropertyB2Individual( String individualName, String propertyName, OWLReferences ontoRef){
		OWLNamedIndividual ind = getOWLIndividual( individualName, ontoRef);
		OWLObjectProperty prop = getOWLObjectProperty( propertyName, ontoRef);
		return( getObjectPropertyB2Individual( ind, prop, ontoRef));
	 }
	/**
	 * Returns all the values (individuals) to an Object property, given by name,
	 * linked to an individual, given by name as well. It will return null
	 * if such object property or individual does not exist.
	 * 
	 * @param individual an OWL individual
	 * @param property an OWL object property
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not ordered set of all the values (OWLNamedIndividual) that
	 * the individual has w.r.t. such object property.
	 */
	 public Set<OWLNamedIndividual> getObjectPropertyB2Individual( OWLNamedIndividual individual, OWLObjectProperty property, OWLReferences ontoRef){
		try{
			long initialTime = System.nanoTime();
			Set< OWLNamedIndividual> out = new HashSet< OWLNamedIndividual>();
			Set< OWLIndividual> set = individual.getObjectPropertyValues(property, ontoRef.getOntology());
			if( set != null){
				for( OWLIndividual i : set)
					out.add( i.asOWLNamedIndividual());
				Set<OWLNamedIndividual> reasoned = ontoRef.getReasoner().getObjectPropertyValues( individual, property).getFlattened();
				out.addAll( reasoned);
				logger.addDebugStrign( "get object property [" + getOWLObjectName( property) + "] belong to individual [" + getOWLObjectName( individual) + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
				return( out);
			}else return( null);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	/**
	 * Returns a value (individual) to an Object property, given by name,
	 * linked to an individual, given by name as well. Indeed it retrueve the OWL
	 * Objects by name using {@link #getOWLObjectProperty(String, OWLReferences)}
	 * and {@link #getOWLIndividual(String, OWLReferences)}. 
	 * Than it calls {@link #getObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLReferences)}
	 * and its returning value is used to call 
	 * {@link #getOnlyElement(Set)} which define the actual returning
	 * value of this method.
	 * 
	 * @param individualName the name of an ontological individual 
	 * @param propertyName the name of an ontological object property
	 * @param ontoRef reference to an OWL ontology.
	 * @return a value (OWLNamedIndividual) that
	 * the individual has w.r.t. such object property. 
	 */
	 public OWLNamedIndividual getOnlyObjectPropertyB2Individual( String individualName, String propertyName, OWLReferences ontoRef){
		OWLNamedIndividual ind = getOWLIndividual( individualName, ontoRef);
		OWLObjectProperty prop = getOWLObjectProperty( propertyName, ontoRef);
		Set<OWLNamedIndividual> set = getObjectPropertyB2Individual( ind, prop, ontoRef);
		return( (OWLNamedIndividual) getOnlyElement( set));
	 }
	/**
	 * Returns a value (individual) to an Object property, given by name,
	 * linked to an individual, given by name as well. It will return null
	 * if such object property or individual does not exist. 
	 * Finally it can return null if {@link #getOnlyElement(Set)} returns
	 * null.
	 * 
	 * @param individual an OWL individual
	 * @param property an OWL object property
	 * @param ontoRef reference to an OWL ontology.
	 * @return a value (OWLNamedIndividual) that
	 * the individual has w.r.t. such object property.
	 */
	public OWLNamedIndividual getOnlyObjectPropertyB2Individual( OWLNamedIndividual individual, OWLObjectProperty property, OWLReferences ontoRef){
		try{
			Set< OWLNamedIndividual> out = new HashSet< OWLNamedIndividual>();
			Set<OWLIndividual> set = individual.getObjectPropertyValues(property, ontoRef.getOntology());
			if( set != null){
				for( OWLIndividual i : set)
					out.add( i.asOWLNamedIndividual());
				Set<OWLNamedIndividual> reasoned = ontoRef.getReasoner().getObjectPropertyValues( individual, property).getFlattened();
				out.addAll( reasoned);
				return( (OWLNamedIndividual) getOnlyElement( out));
			}else return( null);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	
	
	/**
	 * Returns all the classes that are sub classes of the given parameter.
	 * Here class is defined by name, so this method uses: 
	 * {@link #getOWLClass(String, OWLReferences)} to get an OWLClass and than
	 * it calls {@link #getSubClassOf(OWLClass, OWLReferences)}
	 * propagating its returning value. 
	 * 
	 * @param className name of the ontological class to find sub classes
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not order set of all the sub classes of cl parameter.
	 */
	 public Set<OWLClass> getSubClassOf( String className, OWLReferences ontoRef){
		OWLClass cl = getOWLClass( className, ontoRef);
		return( getSubClassOf( cl, ontoRef));
	}
	/**
	 * Returns all the classes that are sub classes of the given class parameter.
	 * It returns null if no sub classes are defined in the ontology.
	 * 
	 * @param cl OWL class to find sub classes
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not order set of all the sub-classes of cl parameter.
	 */
	 public Set<OWLClass> getSubClassOf( OWLClass cl, OWLReferences ontoRef){
		try{
			long initialTime = System.nanoTime();
			Set<OWLClassExpression> set = cl.getSubClasses( ontoRef.getOntology());
			Set<OWLClass> out = new HashSet< OWLClass>();
			if( set != null){
				for( OWLClassExpression s : set)
					out.add( s.asOWLClass());
				out.addAll( ontoRef.getReasoner().getSubClasses( cl, true).getFlattened());
				logger.addDebugStrign( "get sub classes of [" + getOWLObjectName( cl) + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
				return( out);
			} else return( null);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	
	/**
	 * Returns all the classes that are super classes of the given parameter.
	 * Here class is defined by name, so this method uses: 
	 * {@link #getOWLClass(String, OWLReferences)} to get an OWLClass and than
	 * it calls {@link #getSuperClassOf(OWLClass, OWLReferences)}
	 * propagating its returning value. 
	 * 
	 * @param className name of the ontological class to find super classes
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not order set of all the super classes of cl parameter.
	 */
	 public Set<OWLClass> getSuperClassOf( String className, OWLReferences ontoRef){
		OWLClass cl = getOWLClass( className, ontoRef);
		return( getSuperClassOf( cl, ontoRef));
	}
	/**
	 * Returns all the classes that are super classes of the given class parameter.
	 * It returns null if no super classes are defined in the ontology.
	 * 
	 * @param cl OWL class to find super classes
	 * @param ontoRef reference to an OWL ontology.
	 * @return a not order set of all the super classes of cl parameter.
	 */
	 public Set<OWLClass> getSuperClassOf( OWLClass cl, OWLReferences ontoRef){
		try{
			long initialTime = System.nanoTime();
			Set<OWLClass> classes = new HashSet< OWLClass>();
			for( OWLClassExpression j : cl.getSuperClasses( ontoRef.getOntology()))
				classes.add( j.asOWLClass());
			classes.addAll( ontoRef.getReasoner().getSuperClasses( cl, true).getFlattened());
			logger.addDebugStrign( "get super classes of [" + getOWLObjectName( cl) + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
			return( classes);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	
	
	/**
	 * Set the parameter subClass to be a sub class of the
	 * parameter superClass. Here classes are given by name, the method
	 * uses {@link #getOWLClass(String, OWLReferences)} to cal
	 * {@link #setSubClassOf(OWLClass, OWLClass, OWLReferences)} and 
	 * propagate its returning value.
	 * 
	 * @param superClassName the name of the ontological super class
	 * @param subClassName the name of the ontological sub class
	 * @param ontoRef reference to an OWL ontology.
	 * @return an ontologial axiom to describe this hyerarchly dependece between classes.
	 */
	 public OWLAxiom setSubClassOf( String superClassName, String subClassName, OWLReferences ontoRef){
		OWLClass sup = getOWLClass( superClassName, ontoRef);
		OWLClass sub = getOWLClass( subClassName, ontoRef);
		return( setSubClassOf( sup, sub, ontoRef));
	}
	/**
	 * Set the parameter subClass to be a sub class of the
	 * parameter superClass. Here classes are given by name, the method
	 * uses {@link #getOWLClass(String, OWLReferences)} to cal
	 * {@link #setSubClassOf(OWLClass, OWLClass, boolean, OWLReferences)} and 
	 * propagate its returning value. If the boolean value addAxiom is true,
	 * than the axioms to add to describe those dependencies are stored in an
	 * internal buffer. it will be not added to the buffer if it is false.
	 * 
	 * @param superClassName the name of the ontological super class
	 * @param subClassName the name of the ontological sub class
	 * @param addAxiom flag to store the adding axioms into a buffer managed in this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return an ontologial axiom to describe this hyerarchly dependece between classes.
	 */
	 public OWLAxiom setSubClassOf( String superClassName, String subClassName, boolean addAxiom, OWLReferences ontoRef){
		OWLClass sup = getOWLClass( superClassName, ontoRef);
		OWLClass sub = getOWLClass( subClassName, ontoRef);
		return( setSubClassOf( sup, sub, addAxiom, ontoRef));
	}
	/**
	 * Set the parameter subClass to be a sub class of the
	 * parameter superClass. Here classes are given by name, the method
	 * uses {@link #getOWLClass(String, OWLReferences)} to cal
	 * {@link #setSubClassOf(OWLClass, OWLClass, boolean, boolean, OWLReferences)} and 
	 * propagate its returning value. If the boolean value addAxiom is true,
	 * than the axioms to add to describe those dependencies are stored in an
	 * internal buffer. it will be not added to the buffer if it is false.
	 * On the other hand if the parameter applyChanges is true than those changes are also
	 * immidiately apllied, otherwise a call to apply them
	 * is required.
	 * 
	 * @param superClassName the name of the ontological super class
	 * @param subClassName the name of the ontological sub class
	 * @param addAxiom flag to store the adding axioms into a buffer managed in this class.
	 * @param applyChanges flag to decide if applyng those change immediatly or not.
	 * @param ontoRef reference to an OWL ontology.
	 * @return an ontologial axiom to describe this hyerarchly dependece between classes.
	 */
	 public OWLAxiom setSubClassOf( String superClassName, String subClassName, boolean addAxiom, boolean applyChanges, OWLReferences ontoRef){
		OWLClass sup = getOWLClass( superClassName, ontoRef);
		OWLClass sub = getOWLClass( subClassName, ontoRef);
		return( setSubClassOf( sup, sub, addAxiom, applyChanges, ontoRef));
	}
	/**
	 * Set the parameter subClass to be a sub class of the
	 * parameter superClass.
	 * 
	 * @param superClass the OWL super class
	 * @param subClass the OWL sub class
	 * @param ontoRef reference to an OWL ontology.
	 * @return an ontologial axiom to describe this hyerarchly dependece between classes.
	*/
	 public OWLAxiom setSubClassOf( OWLClass superClass, OWLClass subClass, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		 OWLSubClassOfAxiom out = ontoRef.getFactory().getOWLSubClassOfAxiom( subClass, superClass);
		 logger.addDebugStrign( "set sub class of [" + getOWLObjectName( superClass) + " --> " + getOWLObjectName( subClass) + "] given in: " + (System.nanoTime() - initialTime) + " [ns]");
		 return(  out);
	 }
	/**
	 * Set the parameter subClass to be a sub class of the
	 * parameter superClass. If addAxiom flag is true than, this axioms
	 * will be stored inside an internal buffer, otherwise no. This is
	 * done by calling {@link #getAddAxiom(OWLAxiom, boolean, OWLReferences)}.
	 * 
	 * @param superClass the OWL super class
	 * @param subClass the OWL sub class
	 * @param addAxiom flag to store the adding axioms into a buffer managed in this class.	 * @param ontoRef
	 * @param ontoRef reference to an OWL ontology.
	 * @return an ontologial axiom to describe this hyerarchly dependece between classes.
	 */
	 public OWLAxiom setSubClassOf( OWLClass superClass, OWLClass subClass, boolean addAxiom, OWLReferences ontoRef){
		OWLAxiom subClAxiom = setSubClassOf(subClass, superClass, ontoRef);
		if( addAxiom)
			getAddAxiom( subClAxiom, addAxiom, ontoRef);
		return( subClAxiom);
	}
	/**
	 * Set the parameter subClass to be a sub class of the
	 * parameter superClass. If addAxiom flag is true than, this axioms
	 * will be stored inside an internal buffer, otherwise no. This is
	 * done by calling {@link #getAddAxiom(OWLAxiom, boolean, OWLReferences)}.
	 * On the other hand if applyChanges id true than the changes are immediately 
	 * moved into the otology, otherwise no. This is done
	 * by calling {@link #applyChanges(OWLReferences)}.
	 * 
	 * @param superClass the OWL super class
	 * @param subClass the OWL sub class
	 * @param addAxiom flag to store the adding axioms into a buffer managed in this class.	
	 * @param applyChanges flag to decide if applyng those change immediatly or not.
	 * @param ontoRef reference to an OWL ontology.
	 * @return an ontologial axiom to describe this hyerarchly dependece between classes.
	 */
	 public OWLAxiom setSubClassOf( OWLClass superClass, OWLClass subClass, boolean addAxiom, boolean applyChanges, OWLReferences ontoRef){
		try{
			OWLAxiom subClAxiom = setSubClassOf(subClass, superClass, ontoRef);
			//if( ( ! addAxiom) && ( ! applyChanges)) false, false => do not do nothing
			if( ( ! addAxiom) && ( applyChanges)) { 
				//false, true => not add to list & apply only this change 
				List<OWLOntologyChange> adding = getAddAxiom( subClAxiom, false, ontoRef);
				applyChanges( adding, ontoRef);
			} else	if( ( addAxiom) && ( ! applyChanges)) { 
				//true, false => add to list & not apply any changes 
				getAddAxiom( subClAxiom, true, ontoRef);
			} else	if( ( addAxiom) && ( applyChanges)) { 
				//true, false => add to list & apply all changes  
				getAddAxiom( subClAxiom, true, ontoRef);
				applyChanges( ontoRef);
			}
			return( subClAxiom);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	
	
	/**
	 * It returns a list of ontology changes to be done to build a 
	 * given axiom into the ontology. Indeed it calls:
	 * {@link #getAddAxiom(OWLAxiom, boolean, OWLReferences)} with the 
	 * flag value always set to {@code true}.
	 * 
	 * @param axiom to describe relationsheps between ontological entities. 
	 * @param ontoRef reference to an OWL ontology.
	 * @return the order set of changes to build a given axiom.
	 */
	 public List<OWLOntologyChange> getAddAxiom( OWLAxiom axiom, OWLReferences ontoRef){
		return( getAddAxiom( axiom, true, ontoRef));
	}
	/**
	 * It returns a list of ontology changes to be done to build a 
	 * given axiom into the ontology. If the flag {@code addToChangeList} is true
	 * than those changes will be stored inside an internul buffer, otherwise no.
	 * 
	 * @param axiom o describe relationsheps between ontological entities.
	 * @param addToChangeList flag to decide if add them into the internal buffer of changes
	 * @param ontoRef reference to an OWL ontology.
	 * @return the order set of changes to build a given axiom.
	 */
	 public List<OWLOntologyChange> getAddAxiom( OWLAxiom axiom, boolean addToChangeList, OWLReferences ontoRef){
		List<OWLOntologyChange> addAxiom = null;
		try{
		long initialTime = System.nanoTime();
			addAxiom = ontoRef.getManager().addAxiom( ontoRef.getOntology(), axiom);
			if( addToChangeList)
				changeList.addAll( addAxiom);
		logger.addDebugStrign( "get add axiom [" + getOWLObjectName( addAxiom) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
		return( addAxiom);
	}
	
	
	
	/**
	 * It returns a list of ontology changes to be done to remove a 
	 * given axiom from the ontology. Indeed it calls:
	 * {@link #getRemoveAxiom(OWLAxiom, boolean, OWLReferences)} with the 
	 * flag value always set to {@code true}.
	 * 
	 * @param axiom to describe relationsheps between ontological entities. 
	 * @param ontoRef reference to an OWL ontology.
	 * @return the order set of changes to remove a given axiom.
	 */
	 public List<OWLOntologyChange> getRemoveAxiom( OWLAxiom axiom, OWLReferences ontoRef){
		return( getRemoveAxiom( axiom, true, ontoRef));
	}
	/**
	 * It returns a list of ontology changes to be done to remove a 
	 * given axiom from the ontology. If the flag {@code addToChangeList} is true
	 * than those changes will be stored inside an internul buffer, otherwise no.
	 * 
	 * @param axiom o describe relationsheps between ontological entities.
	 * @param addToChangeList flag to decide if add them into the internal buffer of changes
	 * @param ontoRef reference to an OWL ontology.
	 * @return the order set of changes to remove a given axiom.
	 */
	 public List<OWLOntologyChange> getRemoveAxiom( OWLAxiom axiom, boolean addToChangeList, OWLReferences ontoRef){
		//OWLOntologyChange addAxiom = new AddAxiom( ontoRef.getOntology(), axiom);
		 long initialTime = System.nanoTime();
		List<OWLOntologyChange> removeAxiom = null;
		try{
			removeAxiom = ontoRef.getManager().removeAxiom( ontoRef.getOntology(), axiom);
			if( addToChangeList)
				changeList.addAll( removeAxiom);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
		logger.addDebugStrign( "get remove axiom ["+ getOWLObjectName( removeAxiom) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
		return( removeAxiom);
	}
	 
	
	/**
	 * It applies all the changes and axioms stored in the internal buffer 
	 * into the ontology. After its work, it will clean up this buffer.
	 * 
	 * @param ontoRef reference to an OWL ontology.
	 */
	 public void applyChanges( OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		try{
			ontoRef.getManager().applyChanges( changeList);
			changeList.clear();
		logger.addDebugStrign( "apply changes [" + getOWLObjectName( changeList) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}
	/**
	 * It applies, into the ontology, only the change given as parameter.
	 * 
	 * @param addAxiom change to apply in the ontology
	 * @param ontoRef param ontoRef reference to an OWL ontology.
	 */
	 public void applyChanges( OWLOntologyChange addAxiom, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		try{
			ontoRef.getManager().applyChange( addAxiom);
		logger.addDebugStrign( "apply changes [" +  getOWLObjectName( addAxiom.getAxiom()) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}
	/**
	 * It applies, into the ontology, all the changes given as parameter.
	 * 
	 * @param addAxiom list of ontological changes.
	 * @param ontoRef reference to an OWL ontology.
	 */
	 public void applyChanges( List<OWLOntologyChange> addAxiom, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		try{
			ontoRef.getManager().applyChanges( addAxiom);
		logger.addDebugStrign( "apply changes [" + getOWLObjectName( addAxiom) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}
		
	
 	/**
 	 * Return all the sub object property relate do a property
 	 * given as an input parameter. Indeed, it retrieve the object from their
 	 * names using {@link #getOWLObjectProperty(String, OWLReferences)}. Than
 	 * it calls {@link #getSubObjectProperty(OWLObjectProperty, OWLReferences)}
 	 * and propagate its returning value.
 	 * 
 	 * @param objectPropName the name of the ontological object property to check for its sub property 
 	 * @param ontoRef reference to an OWL ontology.
 	 * @return an unordered set of Expression to define this hyererchly relations.
 	 */
 	 public Set<OWLObjectPropertyExpression> getSubObjectProperty( String objectPropName, OWLReferences ontoRef){
		OWLObjectProperty prop = getOWLObjectProperty( objectPropName, ontoRef);
		return( getSubObjectProperty( prop, ontoRef));
	}
	/**
	 * eturn all the sub object property relate do a property
 	 * given as an input parameter.It can return null if no sub object property
 	 * are defined into the ontology for the input parameter.
	 * 
	 * @param objectProp the OWL object property to check for its sub property
	 * @param ontoRef reference to an OWL ontology.
 	 * @return an unordered set of Expression to define this hyererchly relations.
	 */
	 public Set< OWLObjectPropertyExpression> getSubObjectProperty( OWLObjectProperty objectProp, OWLReferences ontoRef){
		try{
			return( ontoRef.getReasoner().getSubObjectProperties(objectProp, true).getFlattened());
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}

		
	/**
	 * Its pourpuses is to be used when an entity of the ontology can
	 * have only one element by construction. In particolar this method
	 * returns true {@code if( set.size() > 1)}. Otherwise it will iterate over 
	 * the set and return just the first value. Note that a set does not
	 * guarantee that its order is always the same.
	 * 
	 * @param set a generic set of object
	 * @return an element of the set
	 */
	 public Object getOnlyElement( Set< ?> set){
		if( set != null){
			for( Object i : set){
				//notifyDebugger( "returning ", getOWLObjectName( (OWLObject) i), "fom ", set.toString());
				return( i);
			}
		} else System.out.println("EXCEPTION !!" + set);
		return( null); 
	}
	
	/**
	 * Its pourpuses is to be used when an entity of the ontology can
	 * have only one literal by construction. In particolar this method
	 * returns true {@code if( set.size() > 1)}. Otherwise it will iterate over 
	 * the set and return just the first value. Note that a set does not
	 * guarantee that its order is always the same.
	 * 
	 * @param set set of literals
	 * @return an element of the set as a string
	 */
	 public static String getOnlyString( Set< OWLLiteral> set){
		if( set != null){
			for( OWLLiteral i : set){
				return( i.getLiteral());
			}
		} else System.out.println("EXCEPTION !! " + set);
		return( null);
	}
	 
	/**
	 * It returns the changes that must be done into the ontology to rename 
	 * an entity, they should be applied by calling 
	 * {@code applyChanges(renameChanges, ontoRef)}.
	 * 
	 * @param entity ontological object to rename
	 * @param newIRI new name as ontological IRI path
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changesa to be apllyed into the ontology to rename an entity with a new IRI.
	 */
	 public List<OWLOntologyChange> renameEntity( OWLEntity entity, IRI newIRI, OWLReferences ontoRef){
		OWLEntityRenamer renamer = new OWLEntityRenamer( ontoRef.getManager(), ontoRef.getManager().getOntologies());
		return( renamer.changeIRI( entity, newIRI));
	 }
	/**
	 * It returns the changes that must be done into the ontology to rename 
	 * an entity. If the flag appyChanges is true than the entity 
	 * will be immediately renamed into the ontology.
	 * 
	 * @param entity ontological object to rename
	 * @param newIRI new name as ontological IRI path
	 * @param applyChanges flag to apply immediatly those changes,
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changesa to be apllyed into the ontology to rename an entity with a new IRI.
	 */
	 public List<OWLOntologyChange> renameEntity( OWLEntity entity, IRI newIRI, boolean applyChanges, OWLReferences ontoRef){
		OWLEntityRenamer renamer = new OWLEntityRenamer( ontoRef.getManager(), ontoRef.getManager().getOntologies());
		List<OWLOntologyChange> changes = renamer.changeIRI( entity, newIRI);
		if( applyChanges)
			applyChanges( changes, ontoRef);
		return( changes);
	}

	
	/**
	 * It uses a render defined as {@code OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();}
	 * to get the name of an ontological object from its IRI path. 
	 * It returns null if the input parametere is null.
	 * 
	 * @param o the objet for which get the ontological name 
	 * @return the name of the ontological object given as input parameter.
	 */
	 static synchronized public String getOWLObjectName( OWLObject o){
		if( o != null)
			return( renderer.render( o));
		return( null);
	}
	static synchronized private String getOWLObjectName( List< OWLOntologyChange> os){
		String out = "[ ";
		for( OWLOntologyChange o : os){
			if( o != null){
				String ret = getOWLObjectName( ( OWLObject) o.getAxiom());
				if( ret == null)
					ret = " NULL ";
				out += ret +"; "; 
			}
		}
		return( out + "]");
	}
	/**
	 * Returns a set of names given a set of ontological objects. It uses a 
	 * renderer: {@code OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();}
	 * to do this work. The inoput set cannot have null value. It will riturn
	 * null if the input set is empty.
	 * 
	 * @param set of ontological object from which retrieve names.
	 * @return set of names of the object contained in the input parameter.
	 */
	 public Set< String> getOWLSetAsString( Set< OWLObject> set){
		Set< String> output = new HashSet<String>(); 
		for( OWLObject o : set)
			output.add( renderer.render( o));
		return( output);
	 }	 
	/**
	 * It converts all the literal inside a set into a set of string using
	 * {@code literal.getLiteral()}. The input set cannot contain null values.
	 * This method returns null if the input set is empty.
	 * 
	 * @param set set of ontological individual.
	 * @return the input set converterd to string.
	 */
	 public Set< String> getOWLLiteralAsString( Set< OWLLiteral> set){
		Set< String> output = new HashSet<String>(); 
		for( OWLLiteral l : set)
			output.add( l.getLiteral());
		return( output);
	}
	
	/**
	 * It will save an ontology into a file. The files path is 
	 * retrieved from the OWLReferences class using: 
	 * {@code ontoRef.getIriFilePath();}. Note that this procedure
	 * may replace an already existing file. The exporting of the 
	 * asserted relation is done by: {@link InferedAxiomExporter#exportOntology(OWLReferences)}
	 * and my be an expencive procedure.
	 * 
	 * @param exportInf flag to export inferences as fixed relations.
 	 * @param ontoRef reference to an OWL ontology.
	 */
	 static synchronized public void saveOntology( boolean exportInf, OWLReferences ontoRef) {
		 long initialTime = System.nanoTime();
		File file = new File( ontoRef.getIriFilePath().toString());
		try {
			if( exportInf)
				ontoRef = InferedAxiomExporter.exportOntology( ontoRef);
			ontoRef.getManager().saveOntology( ontoRef.getOntology(), IRI.create( file.toURI()));
			loggerReasoner.addDebugStrign( "save ontology in: " + (System.nanoTime() - initialTime) + " [ns]");
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}
	/**
	 * It will save an ontology into a file. The files path is 
	 * given as input parameter, and this method does not update: 
	 * {@code ontoRef.getIriFilePath();}. Note that this procedure
	 * may replace an already existing file. The exporting of the 
	 * asserted relation is done by: {@link InferedAxiomExporter#exportOntology(OWLReferences)}
	 * and my be an expencive procedure.
	 * 
	 * @param exportInf flag to export inferences as fixed relations.
	 * @param filePath directiory in which save the ontology.
 	 * @param ontoRef reference to an OWL ontology.
	 */
	 static synchronized public void saveOntology( boolean exportInf, String filePath, OWLReferences ontoRef) {
		File file = new File( filePath);
		try {
			if( exportInf)
				ontoRef = InferedAxiomExporter.exportOntology( ontoRef);
			ontoRef.getManager().saveOntology( ontoRef.getOntology(), IRI.create( file.toURI()));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}
	
	// build into Ontology
	// if bufferize = true it save the axiom in the internal states and
	// apply the changes when is called {@link #applyChanges( OWLReferences)}.
	// if false it apply the changes immediately
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * add a new object property (with its value) into an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology. 
	 * 
	 * @param ind individual that have to have a new object property.
	 * @param prop object property to be added.
	 * @param value individual which is the value of the given object property.
	 * @param bufferize flag to bufferize changes inside an internal buffer.
 	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to add this specific object property.
	 */
	 public List<OWLOntologyChange> addObjectPropertyB2Individual( OWLNamedIndividual ind, OWLObjectProperty prop,  
			OWLNamedIndividual value, boolean bufferize, OWLReferences ontoRef){
		 long initialTime = System.nanoTime();
		try{
			OWLAxiom propertyAssertion = ontoRef.getFactory()
					.getOWLObjectPropertyAssertionAxiom( prop, ind, value);
			
			List<OWLOntologyChange> add = getAddAxiom( propertyAssertion, bufferize, ontoRef);
			if( ! bufferize)
				applyChanges(add, ontoRef);
		logger.addDebugStrign( "add object property [" + getOWLObjectName( prop) + "] belong to individual [" + getOWLObjectName( ind) + " = " +  getOWLObjectName( value) + "]: " + (System.nanoTime() - initialTime) + " [ns]");
			return( add);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * add a new object property (with its value) into an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * Indeed it retrieve the ontological object from name and than it calls: 
	 * {@link #addObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLNamedIndividual, boolean, OWLReferences)}
	 * 
	 * @param individualName tha name of an ontological individual that havo to have a new object property
	 * @param propName name of the object property inside the ontology refered by ontoRef.
	 * @param valueName individual name inside te refereed ontology to be the value of the given object property
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to add this specific object property.
	 */
	 public List<OWLOntologyChange> addObjectPropertyB2Individual( String individualName, String propName,  
			String valueName, boolean bufferize, OWLReferences ontoRef){
		OWLNamedIndividual indiv = getOWLIndividual( individualName, ontoRef);
		OWLObjectProperty prop = getOWLObjectProperty( propName, ontoRef);
		OWLNamedIndividual val = getOWLIndividual( valueName, ontoRef);
		return( addObjectPropertyB2Individual( indiv, prop, val, bufferize, ontoRef));
	}

	/**
	 * Returns a list of changes to be applied into the ontology to
	 * add a new datat property (with its value) into an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology. 
	 * 
	 * @param ind individual that have to have a new data property.
	 * @param prop data property to be added.
	 * @param value literal which is the value of the given data property.
	 * @param bufferize flag to bufferize changes inside an internal buffer.
 	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to add this specific data property.
	 */
	 public List<OWLOntologyChange> addDataPropertyB2Individual(OWLNamedIndividual ind,
			OWLDataProperty prop, OWLLiteral value, boolean bufferize, OWLReferences ontoRef) {
		try{
		 long initialTime = System.nanoTime();
			OWLAxiom newAxiom = ontoRef.getFactory()
					.getOWLDataPropertyAssertionAxiom( prop, ind, value);
			List<OWLOntologyChange> add = getAddAxiom( newAxiom, bufferize, ontoRef);
			if( ! bufferize)
				applyChanges(add, ontoRef);
		    logger.addDebugStrign( "add data property [" + getOWLObjectName( prop) + "] belong to individual [" + getOWLObjectName( ind) + " = " +  getOWLObjectName( value) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
			return( add);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * add a new data property (with its value) into an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * Indeed it retrieve the ontological object from name and than it calls: 
	 * {@link #addDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLLiteral, boolean, OWLReferences)}
	 * 
	 * @param individualName tha name of an ontological individual that havo to have a new data property
	 * @param propertyName name of the data property inside the ontology refered by ontoRef.
	 * @param value literal to be added as the value of a data property.
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to add the given data property.
	 */
	 public List<OWLOntologyChange> addDataPropertyB2Individual( String individualName,
			String propertyName, Object value, boolean bufferize, OWLReferences ontoRef) {
		OWLNamedIndividual indiv = getOWLIndividual( individualName, ontoRef);
		OWLDataProperty prop = getOWLDataProperty( propertyName, ontoRef);
		OWLLiteral lit = getOWLLiteral( value, null, ontoRef);
		return( addDataPropertyB2Individual( indiv, prop, lit, bufferize, ontoRef));
	}

	/**
	 * Returns the ontological changes to be applyed to put an 
	 * individual inside an ontological class.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology. 
	 * 
	 * @param ind individual to add into an ontological class
	 * @param c ontological class that than will conteind this individual
	 * @param bufferize flag to bufferize changes inside an internal buffer.
	 * @param ontoRef reference to an OWL ontology.
	 * @return changes to be done into the refereed ontology to set an individual to be belonging to a specific class. 
	 */
	 public List<OWLOntologyChange> addIndividualB2Class(OWLNamedIndividual ind, OWLClass c, boolean bufferize, OWLReferences ontoRef) {
		 long initialTime = System.nanoTime();
		try{
			OWLAxiom newAxiom = ontoRef.getFactory().getOWLClassAssertionAxiom( c, ind);
			List<OWLOntologyChange> add = getAddAxiom( newAxiom, false, ontoRef);
			if( ! bufferize)
				applyChanges(add, ontoRef);
		logger.addDebugStrign( "add individual [" + getOWLObjectName( ind) + "] belong to class [" + getOWLObjectName( c) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
			return( add);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}

	}
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * set an individual to belonging to a class.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * Indeed it retrieve the ontological object from name inside the refering ontology
	 * and than it calls: 
	 * {@link #addIndividualB2Class(OWLNamedIndividual, OWLClass, boolean, OWLReferences)}
	 * 
	 * @param individualName tha name of an ontological individual that have to be beloging to a given class.
	 * @param className the name of an ontological class that will contains the input individual parameter.
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to set an individual belong to a class.
	 */
	 public List<OWLOntologyChange> addIndividualB2Class(String individualName, String className,
			boolean bufferize, OWLReferences ontoRef) {
		OWLNamedIndividual indiv = getOWLIndividual( individualName, ontoRef);
		OWLClass cl = getOWLClass( className, ontoRef);
		return( addIndividualB2Class( indiv, cl, bufferize, ontoRef));
	}
	
	
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * remove an object property (with its value) from an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology. 
	 * 
	 * @param ind individual from which remove a given object property.
	 * @param prop object property to be removed.
	 * @param value individual which is the value of the given object property.
	 * @param bufferize flag to bufferize changes inside an internal buffer.
 	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to remove this specific object property.
	 */
	 public List<OWLOntologyChange> removeObjectPropertyB2Individual( OWLNamedIndividual ind, OWLObjectProperty prop,  
			OWLNamedIndividual value, boolean bufferize, OWLReferences ontoRef){
		try{
		long initialTime = System.nanoTime();
			OWLAxiom propertyAssertion = ontoRef.getFactory()
					.getOWLObjectPropertyAssertionAxiom( prop, ind, value);
			
			List<OWLOntologyChange> add = getRemoveAxiom( propertyAssertion, bufferize, ontoRef);
			if( ! bufferize)
				applyChanges(add, ontoRef);
		logger.addDebugStrign( "remove object property [" + getOWLObjectName( prop) + "] belong to individual [" + getOWLObjectName( ind) + " = " +  getOWLObjectName( value) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
			return( add);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}

	}
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * remove a given object property (with its value) from an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * Indeed it retrieve the ontological object from name and than it calls: 
	 * {@link #removeObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLNamedIndividual, boolean, OWLReferences)}
	 * 
	 * @param individualName tha name of an ontological individual from which remove the object property
	 * @param propName name of the object property inside the ontology refered by ontoRef.
	 * @param valueName individual name inside te refereed ontology to be the value of the given object property
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to remove this specific object property.
	 */
	 public List<OWLOntologyChange> removeObjectPropertyB2Individual( String individualName, String propName,  
			String valueName, boolean bufferize, OWLReferences ontoRef){
		OWLNamedIndividual indiv = getOWLIndividual( individualName, ontoRef);
		OWLObjectProperty prop = getOWLObjectProperty( propName, ontoRef);
		OWLNamedIndividual val = getOWLIndividual( valueName, ontoRef);
		return( removeObjectPropertyB2Individual( indiv, prop, val, bufferize, ontoRef));
	}

	
//	/**
//	 * Returns a list of changes to be applied into the ontology to
//	 * remove a class from the ontology.
//	 * If the bufferize flag is true than those changes will be saved inside at the
//	 * internal buffer of this class which can be applied by calling:
//	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
//	 * changes will be immediately applied to the refering ontology. 
//	 * 
//	 * @param cl class to remove from the ontology
//	 * @param bufferize flag to bufferize changes inside an internal buffer.
//	 * @param ontoRef reference to an OWL ontology.
//	 * @return the changes to be done into the refereed ontology to remove this specific object property.
//	 */
//	 public List<OWLOntologyChange> removeClass( OWLClass cl,  
//			boolean bufferize, OWLReferences ontoRef){
//		try{
//			OWLAxiom propertyAssertion = ontoRef.getFactory()
//					.getOWLObjectPropertyAssertionAxiom( prop, ind, value);
//			
//			List<OWLOntologyChange> add = getRemoveAxiom( propertyAssertion, bufferize, ontoRef);
//			if( ! bufferize)
//				applyChanges(add, ontoRef);
//			return( add);
//		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
//			notifyPelletException( ontoRef);
//			return( null);
//		}
//
//	}
//	/**
//	 * Returns a list of changes to be applied into the ontology to
//	 * remove a class from the ontology.
//	 * If the bufferize flag is true than those changes will be saved inside at the
//	 * internal buffer of this class which can be applied by calling:
//	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
//	 * changes will be immediately applied to the refering ontology.
//	 * Indeed it retrieve the ontological object from name and than it calls: 
//	 * {@link #removeObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLNamedIndividual, boolean, OWLReferences)}
//	 * 
//	 * @param className the name of the class to remove from the ontology
//	 * @param bufferize flag to buffering changes internally to this class.
//	 * @param ontoRef reference to an OWL ontology.
//	 * @return the changes to be done into the refereed ontology to remove this specific object property.
//	 */
//	 public List<OWLOntologyChange> removeClass( String className,  
//			boolean bufferize, OWLReferences ontoRef){
//		OWLClass cl = getOWLClass( className, ontoRef);
//		return( removeClass( cl, bufferize, ontoRef));
//	}

	/**
	 * Returns a list of changes to be applied into the ontology to
	 * remove  datat property (with its value) from an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology. 
	 * 
	 * @param ind individual from which remove the given data property.
	 * @param prop data property to be removed.
	 * @param value literal which is the value of the given data property.
	 * @param bufferize flag to bufferize changes inside an internal buffer.
 	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to remove this specific data property.
	 */
	 public List<OWLOntologyChange> removeDataPropertyB2Individual(OWLNamedIndividual ind,
			OWLDataProperty prop, OWLLiteral value, boolean bufferize, OWLReferences ontoRef) {
		long initialTime = System.nanoTime();
		try{
			OWLAxiom newAxiom = ontoRef.getFactory()
					.getOWLDataPropertyAssertionAxiom( prop, ind, value);
			List<OWLOntologyChange> add = getRemoveAxiom( newAxiom, bufferize, ontoRef);
			if( ! bufferize)
				applyChanges(add, ontoRef);
			logger.addDebugStrign( "remove data property [" + getOWLObjectName( prop) + "] belong to individual [" + getOWLObjectName( ind) + " = " +  getOWLObjectName( value) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
			return( add);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * remove a data property (with its value) from an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * Indeed it retrieve the ontological object from name and than it calls: 
	 * {@link #removeDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLLiteral, boolean, OWLReferences)}
	 * 
	 * @param individualName tha name of an ontological individual from which remove the data property
	 * @param propertyName name of the data property inside the ontology refered by ontoRef.
	 * @param value literal to be removed as the value of a data property.
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to remove this specific data property.
	 */
	 public List<OWLOntologyChange> removeDataPropertyB2Individual( String individualName,
			String propertyName, Object value, boolean bufferize, OWLReferences ontoRef) {
		OWLNamedIndividual indiv = getOWLIndividual( individualName, ontoRef);
		OWLDataProperty prop = getOWLDataProperty( propertyName, ontoRef);
		OWLLiteral lit = getOWLLiteral( value, null, ontoRef);
		return( removeDataPropertyB2Individual( indiv, prop, lit, bufferize, ontoRef));
	}

	/**
	 * Returns the ontological changes to be applyed to remove an 
	 * individual from an ontological class.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology. 
	 * 
	 * @param ind individual to remove from an ontological class
	 * @param c ontological class that than was conteind this individual
	 * @param bufferize flag to bufferize changes inside an internal buffer.
	 * @param ontoRef reference to an OWL ontology.
	 * @return changes to be done into the refereed ontology to set an individual to not be anymore belonging to a specific class. 
	 */
	 public List<OWLOntologyChange> removeIndividualB2Class(OWLNamedIndividual ind, OWLClass c,
			boolean bufferize, OWLReferences ontoRef) {
		 long initialTime = System.nanoTime();
		try{
			OWLAxiom newAxiom = ontoRef.getFactory().getOWLClassAssertionAxiom( c, ind);
			List<OWLOntologyChange> add = getRemoveAxiom( newAxiom, false, ontoRef);
			if( ! bufferize)
				applyChanges(add, ontoRef);
			logger.addDebugStrign( "remove individual [" + getOWLObjectName( ind) + "] belong to class [" + getOWLObjectName( c) + "] in: " + (System.nanoTime() - initialTime) + " [ns]");
			return( add);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
			return( null);
		}
	}
	/**
	 * Returns a list of changes to be applied into the ontology to
	 * remove an individual to belonging to a class.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * Indeed it retrieve the ontological object from name inside the refering ontology
	 * and than it calls: 
	 * {@link #removeIndividualB2Class(OWLNamedIndividual, OWLClass, boolean, OWLReferences)}
	 * 
	 * @param individualName tha name of an ontological individual that have not to be beloging to a given class.
	 * @param className the name of an ontological class that will no more contains the input individual parameter.
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return the changes to be done into the refereed ontology to set an individual to do not belong to a class anymore.
	 */
	 public List<OWLOntologyChange> removeIndividualB2Class(String individualName, String className,
			boolean bufferize, OWLReferences ontoRef) {
		OWLNamedIndividual indiv = getOWLIndividual( individualName, ontoRef);
		OWLClass cl = getOWLClass( className, ontoRef);
		return( removeIndividualB2Class( indiv, cl, bufferize, ontoRef));
	}
	
	/**
	 * Returns the changes to be apllied into the refering ontology for 
	 * removing an individual.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * 
	 * @param individual to be removed from the ontology.
	 * @param bufferize flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return tha changes to be done into the refereed ontology to remove a given individual.
	 */
	 public List<OWLOntologyChange> removeIndividual( OWLNamedIndividual individual, Boolean bufferize, OWLReferences ontoRef){
		OWLEntityRemover remover = new OWLEntityRemover( ontoRef.getManager(), Collections.singleton( ontoRef.getOntology()));
		individual.accept(remover);
		long initialTime = System.nanoTime();
	    List<OWLOntologyChange> out = remover.getChanges();
	    if( ! bufferize)
			applyChanges(out, ontoRef);
	    //remover.reset();
	    logger.addDebugStrign( "remove individual [" + getOWLObjectName( individual) + "] : " + (System.nanoTime() - initialTime) + " [ns]");
	    return( out);
	}
	/**
	 * Returns the changes to be applied into the refering ontology for removing
	 * a set of individuals.
	 * If the bufferize flag is true than those changes will be saved inside at the
	 * internal buffer of this class which can be applied by calling:
	 * {@link #applyChanges(OWLReferences)}. If this flag is false than only this
	 * changes will be immediately applied to the refering ontology.
	 * 
	 * @param individuals set of individuals to be removed.
	 * @param bufferised flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * @return tha changes to be done into the refereed ontology to remove a given set individuals.
	 */
	 public List<OWLOntologyChange> removeIndividual( Set< OWLNamedIndividual> individuals, Boolean bufferised, OWLReferences ontoRef){
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for( OWLNamedIndividual i : individuals)
			changes.addAll( removeIndividual( i, bufferised, ontoRef));
		return( changes);
	}
	
	/**
	 * Atomically (with respect to reasoner update) replacing of a data property.
	 * Indeed, it will remove all the possible data property with a given values
	 * using {@link #removeDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLLiteral, boolean, OWLReferences)}.
	 * Than, it add the new value calling {@link #addDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLLiteral, boolean, OWLReferences)}.
	 * Refer to this last two for how the flag bufferized is used.
	 * 
	 * @param ind individual for which a data property will be replaced.
	 * @param prop property to replace
	 * @param oldValue set of old values to remove
	 * @param newValue new value to add
	 * @param buffered flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 */
	 public void replaceDataProperty( OWLNamedIndividual ind, 
			OWLDataProperty prop, Set< OWLLiteral> oldValue, OWLLiteral newValue, Boolean buffered, OWLReferences ontoRef){
		try{
			for( OWLLiteral l : oldValue)
				this.removeDataPropertyB2Individual( ind, prop, l, buffered, ontoRef);
			this.addDataPropertyB2Individual( ind, prop, newValue, buffered, ontoRef);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}
	}
	/**
	 * Atimically (with respect to reasoner update) replacing of a data property.
	 * Indeed, it will remove the possible data property with a given value
	 * using {@link #removeDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLLiteral, boolean, OWLReferences)}.
	 * Than, it add the new value calling {@link #addDataPropertyB2Individual(OWLNamedIndividual, OWLDataProperty, OWLLiteral, boolean, OWLReferences)}.
	 * Refer to this last two for how the flag bufferized is used.
	 * 
	 * 
	 * @param ind individual for which a data property will be replaced.
	 * @param prop property to replace
	 * @param oldValue value to remove
	 * @param newValue new value to add
	 * @param buffered flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 */
	 public void replaceDataProperty( OWLNamedIndividual ind, 
			OWLDataProperty prop, OWLLiteral oldValue, OWLLiteral newValue, Boolean buffered, OWLReferences ontoRef){
		try{		
			this.removeDataPropertyB2Individual( ind, prop, oldValue, buffered, ontoRef);
			this.addDataPropertyB2Individual( ind, prop, newValue, buffered, ontoRef);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}	
	}
	
	/**
	 * Atomically (with respect to reasoner update) replacing of a object property.
	 * Indeed, it will remove the possible object property with a given values
	 * using {@link #removeObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLNamedIndividual, boolean, OWLReferences)}.
	 * Than, it add the new value calling {@link #addObjectPropertyB2Individual(OWLNamedIndividual, OWLObjectProperty, OWLNamedIndividual, boolean, OWLReferences)}.
	 * Refer to this last two for how the flag bufferized is used.
	 * 
	 * @param ind individual for which a object property will be replaced.
	 * @param prop property to replace
	 * @param oldValue set of old values to remove
	 * @param newValue new value to add
	 * @param buffered flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 * 
	 */
	 public void replaceObjectProperty( OWLNamedIndividual ind, 
			OWLObjectProperty prop, OWLNamedIndividual oldValue, OWLNamedIndividual newValue, Boolean buffered, OWLReferences ontoRef){
		try{
			this.removeObjectPropertyB2Individual( ind, prop, oldValue, buffered, ontoRef);
			this.addObjectPropertyB2Individual( ind, prop, newValue, buffered, ontoRef);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}	
	}
	
	 /**
	 * Atomically (with respect to reasoner update) replacing of individual
	 * type. Which means to remove an individual from a class and add it to
	 * belong to another calss.
	 * Indeed, it will remove the possible type with a given values
	 * using {@link #removeIndividualB2Class(OWLNamedIndividual, OWLClass, boolean, OWLReferences)}.
	 * Than, it add the new value calling {@link #addIndividualB2Class(OWLNamedIndividual, OWLClass, boolean, OWLReferences)}.
	 * Refer to this last two for how the flag bufferized is used.
	 * 
	 * @param ind individual to change its classification.
	 * @param oldValue old class in which the individual is belonging to
	 * @param newValue new class in which the individual will belonging to
	 * @param buffered flag to buffering changes internally to this class.
	 * @param ontoRef reference to an OWL ontology.
	 */
	 public void replaceIndividualClass( OWLNamedIndividual ind, 
			OWLClass oldValue, OWLClass newValue, Boolean buffered, OWLReferences ontoRef){
		try{
			this.removeIndividualB2Class( ind, oldValue, buffered, ontoRef);
			this.addIndividualB2Class( ind, newValue, buffered, ontoRef);
		} catch( org.semanticweb.owlapi.reasoner.InconsistentOntologyException e){
			notifyPelletException( ontoRef);
		}	
	}
	
	/**
	 * It uses Manchester syntax to explain possible inconsistencies.
	 * 
	 * @param ontoRef reference to an OWL ontology.
	 * @return an inconcistency explanation as a string of text.
	 */
	public static String getPelletExplanation( OWLReferences ontoRef){
		// should throw org.semanticweb.owlapi.reasoner.InconsistentOntologyException
		PelletExplanation.setup();
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%  INCONSISTENCY  " + ontoRef.getOntoName() + " %%%%%%%%%%%%%%%%%%%%%%%%%%");
		try {
			// The renderer is used to pretty print explanation
			ManchesterSyntaxExplanationRenderer renderers = new ManchesterSyntaxExplanationRenderer();
			// The writer used for the explanation rendered
			StringWriter out = new StringWriter();
			renderers.startRendering( out );
			// Create an explanation generator
			PelletExplanation expGen = new PelletExplanation( ontoRef.getOntology(), false);//pelletReasoners );
			Set<Set<org.semanticweb.owlapi.model.OWLAxiom>> explanation = expGen.getInconsistencyExplanations();
		
			renderers.render( explanation );
			renderers.endRendering();
			//logger.addDebugStrign( out.toString(), true);
			return("Is " + ontoRef.getOntoName() +" consis+ ontoRef.getOntoName() +tent: " + ontoRef.isConsistent() + out.toString());
		}catch( Exception e){
			return(  ontoRef.getOntoName() +"is not consistent. No message was give from the reasoner, an error occurs during esplanation retrieves. " + e.getCause());
		}
	}
	
	private static void notifyPelletException( OWLReferences ontoRef){
		String explanation = getPelletExplanation( ontoRef);
		loggerReasoner.addDebugStrign( explanation);
	//	JOptionPane a = new JOptionPane();
	//	JOptionPane.showMessageDialog(a, explanation, "Ontology is INCONSISTENT", JOptionPane.ERROR_MESSAGE);
	}
	
	/*private static void notifyDebugger( String arg1, String arg2, String arg3, String arg4){
		//System.out.println( getCallerMathodName() + ".. " + arg1 + " " + arg2 + " " + arg3 + " " + arg4);
	}*/
	
	private static void notifyNullExceptions(String arg) {
		//System.out.println( getCallerMathodName() + " cannot have null inputs. [InList: " + arg + "]");
	}
	
	@SuppressWarnings("unused")
	private static String getCallerMathodName(){
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement e = stacktrace[ 3];
		return( e.getMethodName());
	}
	
	public static void setReasoneVerbose( Boolean flag){
		loggerReasoner.setFlagToFollow( flag);
	}
	
	public void setOWLVerbose( Boolean flag){
		logger.setFlagToFollow( flag);
	}
}

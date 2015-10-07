package globFitRecognition.sceneLearning;

import globFitRecognition.primitiveShapeData.PrimitiveShapeData;
import globFitRecognition.sceneRecognition.PrimitiveRelation;
import globFitRecognition.sceneRecognition.SceneIndividualCreator;

import java.util.List;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLVariable;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;

public class SceneLearner extends SWRLmanager{
	
	// TODO how to give always different and intelligent name while learning ?????????????????
	public static final String CLASS_NAME_LEARNED_BASE = "L_"; 
	public static final String INDIVIDUAL_NAME_RECOGNITION_PARAMETER = "Parameter_Recognition";
	public static final String INDIVIDUAL_NAME_LEARNING_PARAMETER = "Parameter_Learning";
	public static final String PROPERTY_LEARNED_SCENE_ID = "hasParameter_LearningScene_cnt";
	public static final String PROPERTY_BASE_NAME_CARDINALITY = "hasParameter_SceneCardinality_";
	
	private String newSceneName;
	
	private static Long sceneId = 0l;
	
	private String logSWRL = " Add rule: ", logOWL = "";
	
	public SceneLearner( List< PrimitiveRelation> sceneList, OWLReferences ontology){
		// initialize swrl manager
		super( ontology);

		newSceneName = createNewSceneName( ontology);
		
		// add new class for this learned scene as a sub class of "Scene"
		Integer classCardinality = 0;
		ontology.applyChanges( ontology.getAddAxiom( ontology.setSubClassOf( SceneIndividualCreator.CLASS_NAME_SCENE, newSceneName)));
		logOWL += "learning new class: " +  newSceneName + " (as a subclass of " + SceneIndividualCreator.CLASS_NAME_SCENE + ")\n Add class Expressions: ";
		// create SWRL recognition rule
		SWRLIndividualArgument sceneIndividual = this.getConst( ontology.getOWLIndividual( SceneIndividualCreator.INDIVIDUAL_NAME_SCENE));
		for( PrimitiveRelation prRel : sceneList){
			// create the precondition part of the rule for this primitive relation in the scene
			createPrimitveRoleCondition( sceneIndividual, prRel, ontology);
			// add cardinality restriction in the class expression for scene hierachical classification
			addCardinalityRestriction( prRel, ontology);
			classCardinality += 2;
		}
		createPrimitiveRoleInferation( sceneIndividual, ontology);
		this.addRoole();
		addClassCardinalityReferemce( classCardinality, ontology);
		System.out.println( logOWL);
		System.out.println( logSWRL);
	}

	// get the value of hasLearningScene_idx to generate names for learnend entities
	// it updates also the state of the counter
	private String createNewSceneName( OWLReferences ontology){
		// get actual count value
		OWLNamedIndividual param = ontology.getOWLIndividual( INDIVIDUAL_NAME_LEARNING_PARAMETER);
		OWLDataProperty prop = ontology.getOWLDataProperty( PROPERTY_LEARNED_SCENE_ID);
		OWLLiteral value = ontology.getOnlyDataPropertyB2Individual(param, prop);
		if( value == null){
			// create count
			OWLLiteral initValue = ontology.getOWLLiteral( Long.valueOf( 0));
			ontology.addDataPropertyB2Individual( param, prop, initValue, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
			sceneId = 0l;
		} else {
			// update count
			sceneId = sceneId + 1l;
		    OWLLiteral newValue = ontology.getOWLLiteral( Long.valueOf( sceneId));
			ontology.replaceDataProperty(param, prop, value, newValue, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
		}
		// generate name
		return( CLASS_NAME_LEARNED_BASE + sceneId);
	}
	
	// create preconditions in the role for every primitive relations
	private void createPrimitveRoleCondition( SWRLIndividualArgument sceneIndividual, PrimitiveRelation prRel, OWLReferences ontology) {
		// add Ci+(?pi+)
		OWLClass preClass = prRel.getIndividualClasses( ontology).getPreClass();
		SWRLVariable preVar = this.getVariable( ontology.getOWLObjectName( prRel.getPreInd()));
		this.addClassCondition( preClass, preVar);
		logSWRL += OWLLog( preClass) + "( " + OWLLog( preVar) + "), ";
		// add Cj-(?pj-)
		OWLClass postClass = prRel.getIndividualClasses( ontology).getPostClass();
		SWRLVariable postVar = this.getVariable( ontology.getOWLObjectName( prRel.getPostInd()));
		this.addClassCondition( postClass, postVar);
		logSWRL += OWLLog( postClass) + "( " + OWLLog( postVar) + "), ";
		// add psi+(?si, ?pi+)
		OWLObjectProperty sceneProp = prRel.getRelation().getSceneProperty( ontology);
		this.addObjectPropertyCondition( sceneIndividual, sceneProp, preVar);
		logSWRL += OWLLog( sceneProp) + "( " + OWLLog( sceneIndividual) + ", " + OWLLog( preVar) + "), ";
		// add psi-(?si, ?pi-)
		this.addObjectPropertyCondition( sceneIndividual, sceneProp, postVar);
		logSWRL += OWLLog( sceneProp) + "( " + OWLLog( sceneIndividual) + ", " + OWLLog( postVar) + "), ";
	}
	
	// create assertion part of the rule
	private void createPrimitiveRoleInferation( SWRLIndividualArgument sceneIndividual, OWLReferences ontology){
		this.addClassInferation( ontology.getOWLClass( newSceneName), sceneIndividual);
		logSWRL += " -> " + newSceneName + "( " + OWLLog( sceneIndividual) + ") ";
	}
	
	// add LearnedScene = hasSceneComponent_* exactly * Primitive ...
	private void addCardinalityRestriction( PrimitiveRelation prRel, OWLReferences ontology) {		
		OWLClass cl = ontology.getOWLClass( newSceneName);
		OWLObjectProperty prop = prRel.getRelation().getSceneProperty( ontology);
		OWLClass clValuePre = prRel.getIndividualClasses(ontology).getPreClass();
		OWLClass clValuePost = prRel.getIndividualClasses(ontology).getPostClass();
		if( clValuePre.equals( clValuePost)) { 
			addExactClassExpression( cl, prop, 2, clValuePre, ontology);
		} else {
			addExactClassExpression( cl, prop, 1, clValuePre, ontology);
			addExactClassExpression( cl, prop, 1, clValuePost, ontology);
		}
	}
	private void addExactClassExpression( OWLClass cl, OWLObjectProperty prop, int cardinality, OWLClass value, OWLReferences ontology){
		OWLClassExpression exp = ontology.getFactory().getOWLObjectExactCardinality( cardinality, prop, value);
		OWLEquivalentClassesAxiom clExp = ontology.getFactory().getOWLEquivalentClassesAxiom( cl, exp);
		ontology.applyChanges( new AddAxiom( ontology.getOntology(), clExp));
		logOWL +=  OWLLog( cl) + " " + OWLLog( prop) + " exactly " + cardinality + " " + OWLLog( value) + " ^ "; 
	}

	// add the cardinality to the learning parameter as a data property of type"hasScenCardinality_SceneName"
	private void addClassCardinalityReferemce(Integer classCardinality, OWLReferences ontology) {		
		OWLNamedIndividual paramInd = ontology.getOWLIndividual( INDIVIDUAL_NAME_LEARNING_PARAMETER);
		OWLDataProperty prop = ontology.getOWLDataProperty( PROPERTY_BASE_NAME_CARDINALITY + newSceneName);
		OWLLiteral value = ontology.getOWLLiteral( classCardinality);
		ontology.addDataPropertyB2Individual(paramInd, prop, value, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
		// set property to be a sub property of hasParameter
		//OWLSubDataPropertyOfAxiom subProp = ontology.getFactory().getOWLSubDataPropertyOfAxiom( prop, ontology.getOWLDataProperty( "hasParameter"));
		//ontology.applyChanges( ontology.getAddAxiom( subProp));
	}
	
	private static String OWLLog( OWLObject o){
		return OWLLibrary.getOWLObjectName( o);
	}
}


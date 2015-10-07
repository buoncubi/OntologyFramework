package globFitRecognition.sceneLearning;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import globFitRecognition.sceneRecognition.SceneIndividualCreator;
import ontologyFramework.OFContextManagement.OWLReferences;

public class SceneConfidenceEvaluator {

	public static final String PROPERTY_NAME_CONFIDENCE_TRESHOULD = "hasParameter_Confidence_th";
	
	private Integer classCardinality, individualCardinality;
	private Float sceneConfidence;
	private Float confidenceThresould;
	
	public SceneConfidenceEvaluator( SceneIndividualCreator scene, OWLReferences ontology){
		// get threshould value from ontology
		OWLNamedIndividual param = ontology.getOWLIndividual( SceneLearner.INDIVIDUAL_NAME_LEARNING_PARAMETER);
		OWLDataProperty prop = ontology.getOWLDataProperty( PROPERTY_NAME_CONFIDENCE_TRESHOULD);
		confidenceThresould = Float.valueOf( ontology.getOnlyDataPropertyB2Individual(param, prop).getLiteral());
		// get scene cardinality
		individualCardinality = scene.getCardinality();
		// get max cardinality of all the class where scene individual belongs to
		OWLNamedIndividual sceneIndividual = ontology.getOWLIndividual( SceneIndividualCreator.INDIVIDUAL_NAME_SCENE);
		Set< OWLClass> sceneClasses = getAllSceneClasses( sceneIndividual, ontology);
		classCardinality = getMaxClassCardinality( sceneIndividual, sceneClasses, ontology);
		// compute confidence
		sceneConfidence = computeSceneConfidence();
		System.out.println( " compute scene confidence= " + sceneConfidence + " ( ==  " + classCardinality + " / " + individualCardinality + ")");
	}

	private Set<OWLClass> getAllSceneClasses(OWLNamedIndividual sceneIndividual, OWLReferences ontology) {
		Set< OWLClass> allClasses = ontology.getIndividualClasses( sceneIndividual);
		allClasses.remove( ontology.getOWLClass( SceneIndividualCreator.CLASS_NAME_SCENE));
		return allClasses;
	}
	
	private Integer getMaxClassCardinality(OWLNamedIndividual sceneIndividual, Set<OWLClass> sceneClasses, OWLReferences ontology) {
		Integer maxCardinality = 0;
		for( OWLClass cl : sceneClasses){
			OWLNamedIndividual paramInd = ontology.getOWLIndividual( SceneLearner.INDIVIDUAL_NAME_LEARNING_PARAMETER);
			OWLDataProperty prop = ontology.getOWLDataProperty( SceneLearner.PROPERTY_BASE_NAME_CARDINALITY + ontology.getOWLObjectName( cl));
			OWLLiteral cardinalityLiteral = ontology.getOnlyDataPropertyB2Individual(paramInd, prop);
			if( cardinalityLiteral != null){
				Integer cardinality = Integer.valueOf( cardinalityLiteral.getLiteral());
				if( cardinality > maxCardinality)
					maxCardinality = cardinality;
			}
		}
		return maxCardinality;
	}
	
	private Float computeSceneConfidence(){
		if( individualCardinality != 0)
			return Float.valueOf( classCardinality) / Float.valueOf( individualCardinality);
		else return 0.0f;
	}

	public Boolean isAboveTreshould(){
		if( sceneConfidence > confidenceThresould)
			return true;
		return false;
	}
	
	public Float getConfidenceThresould(){
		return confidenceThresould;
	}
	
	public Integer getClassCardinality() {
		return classCardinality;
	}

	public Integer getIndividualCardinality() {
		return individualCardinality;
	}

	public Float getSceneConfidence() {
		return sceneConfidence;
	}
	
}

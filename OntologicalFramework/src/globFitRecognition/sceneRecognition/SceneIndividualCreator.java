package globFitRecognition.sceneRecognition;

import globFitRecognition.primitiveShapeData.PrimitiveShapeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import ontologyFramework.OFContextManagement.OWLReferences;

public class SceneIndividualCreator {

	public static final String INDIVIDUAL_NAME_SCENE = "S";
	public static final String CLASS_NAME_PRIMITIVE = "Primitive";
	public static final String[] NO_PRIMITIVE_CLASS_NAME = {"Directionable"};
	public static final String CLASS_NAME_SCENE = "Scene";
	public static final String DATA_PROPERTY_NAME_CARDINALITY = "hasPrimitiveCardinality";
	
	private List< PrimitiveRelation> sceneList;
	private OWLReferences ontology;
	private Integer cardinality = 0; // used to compute classification confidence
	
	// use the populate scene list to add the individual scene to the ontology
	public void addSceneIndividual() {
		// add scene primitive relations
		OWLNamedIndividual ind = ontology.getOWLIndividual( INDIVIDUAL_NAME_SCENE);
		for( PrimitiveRelation rel : sceneList){
			OWLObjectProperty prop = rel.getRelation().getSceneProperty(ontology);
			ontology.addObjectPropertyB2Individual(ind, prop, rel.getPreInd(), PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
			ontology.addObjectPropertyB2Individual(ind, prop, rel.getPostInd(), PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
		}
		// add cardinality as a property to the scene individual
		OWLDataProperty prop = ontology.getOWLDataProperty( DATA_PROPERTY_NAME_CARDINALITY);
		OWLLiteral value = ontology.getOWLLiteral( cardinality);
		ontology.addDataPropertyB2Individual(ind, prop, value, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
	}
	
	// create and populate primitive scene list
	public SceneIndividualCreator( OWLReferences ontology){
		sceneList = new ArrayList< PrimitiveRelation>();
		this.ontology = ontology;
		
		Set< OWLNamedIndividual> primitiveIndividuals = ontology.getIndividualB2Class( CLASS_NAME_PRIMITIVE);
		List< PropertiesRelation> allRelations = PropertiesRelation.initializeAllRelation();
		// for all the primitive shape
		for( OWLNamedIndividual prInd : primitiveIndividuals){
			// for all the primitive shape relations
			for( PropertiesRelation prRel : allRelations){
				Set< OWLNamedIndividual> primitiveValues = prRel.getPropertyValue( ontology, prInd);
				// for all the values of the property applied to the individual
				for( OWLNamedIndividual prValue : primitiveValues){
					this.populateList( prInd, prRel, prValue);
				}
			}
		}
		cardinality = sceneList.size() * 2; // consider also inverse
		System.out.println( " sceneList: " + sceneList);
	}
	
	public List<PrimitiveRelation> getSceneList() {
		return sceneList;
	}

	public Integer getCardinality() {
		return cardinality;
	}

	// create primitive relation and add it to the scene list
	private void populateList(OWLNamedIndividual prInd, PropertiesRelation prRel, OWLNamedIndividual prValue) {
		PrimitiveRelation candidateRelation = new PrimitiveRelation( prInd, prRel, prValue);
		if( ! isInverseListed( candidateRelation))
			sceneList.add( new PrimitiveRelation( prInd, prRel, prValue));
	}

	// check if a primitive relation is equal to another. (equals means the same relation or it inverse)
	private boolean isInverseListed( PrimitiveRelation candidateRelation) {
		//System.out.println("----------");
		for( PrimitiveRelation alreadyRelation : sceneList)
			if( alreadyRelation.equals( candidateRelation))
				return true;
		return false;
	}
		
}

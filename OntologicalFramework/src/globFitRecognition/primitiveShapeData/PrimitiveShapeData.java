
package globFitRecognition.primitiveShapeData;

import globFitRecognition.sceneLearning.SceneLearner;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import ontologyFramework.OFContextManagement.OWLReferences;

public abstract class PrimitiveShapeData {

	public static final Boolean BUFFERIZE_ONTOLOGY_CHANGES = true;
	public static final Boolean LOG_ONTOLOGY_CHANGES = true;
	
	public static final String CENTROID_X_DATA_PROPERTY_NAME = "hasPrimitiveGeometricCenter_x";
	public static final String CENTROID_Y_DATA_PROPERTY_NAME = "hasPrimitiveGeometricCenter_y";
	public static final String CENTROID_Z_DATA_PROPERTY_NAME = "hasPrimitiveGeometricCenter_z";
	
	public static final String PROPERTY_PARAMETER_SHAPE_ID = "hasParameter_Primitive_cnt";
	
	public static final String INDIVIDUAL_NAME_MIDDLE_STR = "_";
	
	protected Dimentional3Data centroid;
	protected String individualName;
	
	protected static Long individualId = 0l;
	
	abstract String getIndividualNamePrefix();
	
	public PrimitiveShapeData( Dimentional3Data centroid){
		this.centroid = centroid;
		this.individualName = null;
	}
	
	public PrimitiveShapeData( Dimentional3Data centroid, String individualName){
		this.centroid = centroid;
		this.individualName = individualName;
	}
	
	public Dimentional3Data getCentroid(){
		return this.centroid;
	}
	
	public String getIndividualName(){
		return this.individualName;
	}
	
	protected Long getIndividualId() {
		return individualId;
	}

	public void setIndividualId(Long individualId) {
		PrimitiveShapeData.individualId = individualId;
	}

	@Override
	public String toString(){
		return "( indName: " + this.individualName + ") ; ( centroid: " + this.centroid + ") ; ";  
	}
	
	public void addShapeIndividualToOntology( OWLReferences ontoRef){
		if( individualName == null){
			individualId = getIndivdualId( ontoRef);
			individualName = this.getIndividualNamePrefix() + INDIVIDUAL_NAME_MIDDLE_STR + individualId;
		}
		OWLNamedIndividual individual = ontoRef.getOWLIndividual( this.getIndividualName());
		addDataPropertyToIndividual( ontoRef, individual);
//		OWLClass primitiveClass = ontoRef.getOWLClass( SceneIndividualCreator.PRIMITIVE_CLASS_NAME);
//		ontoRef.addIndividualB2Class(individual, primitiveClass, BUFFERIZE_ONTOLOGY_CHANGES);
		if( LOG_ONTOLOGY_CHANGES)
			System.out.println( " added to ontology " + ontoRef.toString() + "\n  -->\t" + this.toString());
	}

	private Long getIndivdualId( OWLReferences ontoRef) {
		OWLNamedIndividual param = ontoRef.getOWLIndividual( SceneLearner.INDIVIDUAL_NAME_LEARNING_PARAMETER);
		OWLDataProperty prop = ontoRef.getOWLDataProperty( PROPERTY_PARAMETER_SHAPE_ID);
		OWLLiteral value = ontoRef.getOnlyDataPropertyB2Individual(param, prop);
		if( value == null){
			// create count
			OWLLiteral initValue = ontoRef.getOWLLiteral( Long.valueOf( 0));
			ontoRef.addDataPropertyB2Individual( param, prop, initValue, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
			return 0l;
		} else {
			// update count
		    individualId = individualId + 1l;
		    OWLLiteral newValue = ontoRef.getOWLLiteral( Long.valueOf( individualId));
			ontoRef.replaceDataProperty(param, prop, value, newValue, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
			return individualId;//Long.valueOf( value.getLiteral());
		}
	}

	void addDataPropertyToIndividual(OWLReferences ontoRef, OWLNamedIndividual individual){
		OWLDataProperty centroidX_prop = ontoRef.getOWLDataProperty(CENTROID_X_DATA_PROPERTY_NAME);
		OWLDataProperty centroidY_prop = ontoRef.getOWLDataProperty(CENTROID_Y_DATA_PROPERTY_NAME);
		OWLDataProperty centroidZ_prop = ontoRef.getOWLDataProperty(CENTROID_Z_DATA_PROPERTY_NAME);
		centroid.addToOntology( ontoRef, individual, centroidX_prop, centroidY_prop, centroidZ_prop);
	}
}

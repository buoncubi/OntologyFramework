package globFitRecognition.primitiveShapeData;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class SphereShapeData extends PrimitiveShapeData {

	public static final String RADIUS_DATA_PROPERTY_NAME = "hasSphereRadius";
	
	private Float radius;
	
	public SphereShapeData(Float radius, Dimentional3Data centroid) {
		super(centroid);
		this.radius = radius; 
	}
	
	public SphereShapeData(Float radius, Dimentional3Data centroid, String individualName) {
		super(centroid, individualName);
		this.radius = radius; 
	}
	
	public Float getRadius(){
		return this.radius;
	}

	@Override
	public String toString(){
		return "SPHERE= " + super.toString() + "( radius: " + this.getRadius() + ")"; 
	}
	
	@Override
	void addDataPropertyToIndividual(OWLReferences ontoRef, OWLNamedIndividual individual) {
		super.addDataPropertyToIndividual( ontoRef, individual);
		OWLDataProperty d_prop = ontoRef.getOWLDataProperty(RADIUS_DATA_PROPERTY_NAME);
		ontoRef.addDataPropertyB2Individual(individual, d_prop, ontoRef.getOWLLiteral( this.radius), BUFFERIZE_ONTOLOGY_CHANGES);
	}
	
	public static SphereShapeData addSphereIndividual( OWLReferences ontoRef, Float radius, Dimentional3Data centroid, String individualName){
		SphereShapeData s = new SphereShapeData( radius, centroid, individualName); 
		s.addShapeIndividualToOntology(ontoRef);
		return s;
	}
	
	public static SphereShapeData addSphereIndividual( OWLReferences ontoRef, Float radius, Dimentional3Data centroid){
		SphereShapeData s = new SphereShapeData( radius, centroid); 
		s.addShapeIndividualToOntology(ontoRef);
		return s;
	}
	
	@Override
	String getIndividualNamePrefix() {
		return "S";
	}
}

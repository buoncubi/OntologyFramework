package globFitRecognition.primitiveShapeData;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class PlaneShapeData extends DirectionableShapeData {

	public static final String D_COEFFICIENT_PROPERTY_NAME = "hasPlaneCoefficent_d";
	
	private float dCoefficient;
	
	public PlaneShapeData(Dimentional3Data axisDirection, Dimentional3Data centroid, float dCoefficient) {
		super(axisDirection, centroid);
		this.dCoefficient = dCoefficient;
	}
	
	public PlaneShapeData(Dimentional3Data axisDirection, Dimentional3Data centroid, String individualName, float dCoefficient) {
		super(axisDirection, centroid, individualName);
		this.dCoefficient = dCoefficient;
	}
	
	public float getDCoefficient(){
		return dCoefficient;
	}
	
	@Override
	public String toString(){
		return "PLANE = " + super.toString() + "( D_coeff: " + this.dCoefficient + ")"; 
	}
	
	@Override
	void addDataPropertyToIndividual(OWLReferences ontoRef, OWLNamedIndividual individual) {
		super.addDataPropertyToIndividual( ontoRef, individual);
		OWLDataProperty d_prop = ontoRef.getOWLDataProperty(D_COEFFICIENT_PROPERTY_NAME);
		ontoRef.addDataPropertyB2Individual(individual, d_prop, ontoRef.getOWLLiteral( this.dCoefficient), BUFFERIZE_ONTOLOGY_CHANGES);
	}
	
	public static PlaneShapeData addPlaneIndividual( OWLReferences ontoRef, String name, Float d, Dimentional3Data axis, Dimentional3Data centroid){
		PlaneShapeData p = new PlaneShapeData( axis, centroid, name, d);
		p.addShapeIndividualToOntology(ontoRef);
		return p;
	}
	public static PlaneShapeData addPlaneIndividual( OWLReferences ontoRef, Float d, Dimentional3Data axis, Dimentional3Data centroid){
		PlaneShapeData p = new PlaneShapeData( axis, centroid, d);
		p.addShapeIndividualToOntology(ontoRef);
		return p;
	}
	
	@Override
	String getIndividualNamePrefix() {
		return "P";
	}
}

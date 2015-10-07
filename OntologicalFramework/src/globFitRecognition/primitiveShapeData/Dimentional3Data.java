package globFitRecognition.primitiveShapeData;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Dimentional3Data {

	public static final String DEFAULT_DESCRIPTION = "";
	private float x, y, z;
	private String description;
	
	public Dimentional3Data( float x, float y, float z){
		initialize( x, y, z, DEFAULT_DESCRIPTION);
	}	
	public Dimentional3Data( float x, float y, float z, String description){
		initialize( x, y, z, description);
	}
	private void initialize( float x, float y, float z, String description){
		this.x = x;
		this.y = y;
		this.z = z;
		this.description = description;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getZ(){
		return z;
	}
	
	@Override
	public String toString(){
		if( DEFAULT_DESCRIPTION.isEmpty())
			return "{ " + x + ", " + y + ", " + z + "}";
		return description + " :{ " + x + ", " + y + ", " + z + "}";
	}
	
	public void addToOntology(OWLReferences ontoRef, OWLNamedIndividual individual, OWLDataProperty xProp, OWLDataProperty yProp, OWLDataProperty zProp) {
		ontoRef.addDataPropertyB2Individual(individual, xProp, ontoRef.getOWLLiteral( this.x), PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
		ontoRef.addDataPropertyB2Individual(individual, yProp, ontoRef.getOWLLiteral( this.y), PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
		ontoRef.addDataPropertyB2Individual(individual, zProp, ontoRef.getOWLLiteral( this.z), PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
	}
	
}

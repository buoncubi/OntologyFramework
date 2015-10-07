package globFitRecognition.primitiveShapeData;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class ConeShapeData extends DirectionableShapeData {

	public static final String HEIGHT_DATA_PROPERTY_NAME = "hasConeHeight";
	public static final String RADIUS_DATA_PROPERTY_NAME = "hasConeRadius";
	private static final String APEX_COORDINATE_X_DATA_PROPERTY_NAME = "hasConeApix_x";
	private static final String APEX_COORDINATE_Y_DATA_PROPERTY_NAME = "hasConeApix_y";
	private static final String APEX_COORDINATE_Z_DATA_PROPERTY_NAME = "hasConeApix_z";
	
	private Float height, radius;
	private Dimentional3Data apex;
	
	public ConeShapeData(Dimentional3Data apex, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid) {
		super(axisDirection, centroid);
		this.height = height;
		this.radius = radius;
		this.apex = apex;
	}
	
	public ConeShapeData(Dimentional3Data apex, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid, String individualName) {
		super(axisDirection, centroid, individualName);
		this.height = height;
		this.radius = radius;
		this.apex = apex;
	}

	public Float getHeight(){
		return this.height;
	}
	
	public Float getRadius(){
		return this.radius;
	}
	
	public Dimentional3Data getApex(){
		return this.apex;
	}
	
	@Override
	public String toString(){
		return "CONE = " + super.toString() + "( height: " + this.height + ") ; ( radius: " + this.radius + ") ; ( apex: " + this.apex + ")";
	}

	@Override
	void addDataPropertyToIndividual(OWLReferences ontoRef, OWLNamedIndividual individual) {
		super.addDataPropertyToIndividual( ontoRef, individual);
		OWLDataProperty apexX_prop = ontoRef.getOWLDataProperty( APEX_COORDINATE_X_DATA_PROPERTY_NAME);
		OWLDataProperty apexY_prop = ontoRef.getOWLDataProperty( APEX_COORDINATE_Y_DATA_PROPERTY_NAME);
		OWLDataProperty apexZ_prop = ontoRef.getOWLDataProperty( APEX_COORDINATE_Z_DATA_PROPERTY_NAME);
		apex.addToOntology(ontoRef, individual, apexX_prop, apexY_prop, apexZ_prop);
		OWLDataProperty height_prop = ontoRef.getOWLDataProperty( HEIGHT_DATA_PROPERTY_NAME);
		ontoRef.addDataPropertyB2Individual(individual, height_prop, ontoRef.getOWLLiteral( this.height), BUFFERIZE_ONTOLOGY_CHANGES);
		OWLDataProperty radius_prop = ontoRef.getOWLDataProperty(RADIUS_DATA_PROPERTY_NAME);
		ontoRef.addDataPropertyB2Individual(individual, radius_prop, ontoRef.getOWLLiteral( this.radius), BUFFERIZE_ONTOLOGY_CHANGES);
	}
	
	public static ConeShapeData addConeIndividual( OWLReferences ontoRef, Dimentional3Data apex, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid, String individualName){
		ConeShapeData c = new ConeShapeData( apex, height, radius, axisDirection, centroid, individualName); 
		c.addShapeIndividualToOntology(ontoRef);
		return c;
	}
	
	public static ConeShapeData addConeIndividual( OWLReferences ontoRef, Dimentional3Data apex, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid){
		ConeShapeData c = new ConeShapeData( apex, height, radius, axisDirection, centroid); 
		c.addShapeIndividualToOntology(ontoRef);
		return c;
	}

	@Override
	String getIndividualNamePrefix() {
		return "C";
	}
}

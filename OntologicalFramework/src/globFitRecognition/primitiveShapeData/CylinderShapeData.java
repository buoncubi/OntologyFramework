package globFitRecognition.primitiveShapeData;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class CylinderShapeData extends DirectionableShapeData {

	public static final String HEIGHT_DATA_PROPERTY_NAME = "hasCylinderHeight";
	public static final String RADIUS_DATA_PROPERTY_NAME = "hasCylinderRadious";
	private static final String POINT_AXIS_COORDINATE_X_DATA_PROPERTY_NAME = "hasCylinderPointOnAxis_x";
	private static final String POINT_AXIS_COORDINATE_Y_DATA_PROPERTY_NAME = "hasCylinderPointOnAxis_y";
	private static final String POINT_AXIS_COORDINATE_Z_DATA_PROPERTY_NAME = "hasCylinderPointOnAxis_z";
	
	private Float height, radius;
	private Dimentional3Data pointOnAxis;
	
	public CylinderShapeData(Dimentional3Data pointOnAxis, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid) {
		super(axisDirection, centroid);
		this.height = height;
		this.radius = radius;
		this.pointOnAxis = pointOnAxis;
	}
	
	public CylinderShapeData(Dimentional3Data pointOnAxis, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid, String individualName) {
		super(axisDirection, centroid, individualName);
		this.height = height;
		this.radius = radius;
		this.pointOnAxis = pointOnAxis;
	}

	public Float getHeight(){
		return this.height;
	}
	
	public Float getRadius(){
		return this.radius;
	}
	
	public Dimentional3Data getPointOnAxis(){
		return this.pointOnAxis;
	}
	
	@Override
	public String toString(){
		return "CYLINDER = " + super.toString() + "( height: " + this.height + ") ; ( radius: " + this.radius + ") ; ( pointOnAxis: " + this.pointOnAxis + ")";
	}
	
	@Override
	void addDataPropertyToIndividual(OWLReferences ontoRef, OWLNamedIndividual individual) {
		super.addDataPropertyToIndividual( ontoRef, individual);
		OWLDataProperty pointX_prop = ontoRef.getOWLDataProperty( POINT_AXIS_COORDINATE_X_DATA_PROPERTY_NAME);
		OWLDataProperty pointY_prop = ontoRef.getOWLDataProperty( POINT_AXIS_COORDINATE_Y_DATA_PROPERTY_NAME);
		OWLDataProperty pointZ_prop = ontoRef.getOWLDataProperty( POINT_AXIS_COORDINATE_Z_DATA_PROPERTY_NAME);
		pointOnAxis.addToOntology(ontoRef, individual, pointX_prop, pointY_prop, pointZ_prop);
		OWLDataProperty height_prop = ontoRef.getOWLDataProperty( HEIGHT_DATA_PROPERTY_NAME);
		ontoRef.addDataPropertyB2Individual(individual, height_prop, ontoRef.getOWLLiteral( this.height), BUFFERIZE_ONTOLOGY_CHANGES);
		OWLDataProperty radius_prop = ontoRef.getOWLDataProperty(RADIUS_DATA_PROPERTY_NAME);
		ontoRef.addDataPropertyB2Individual(individual, radius_prop, ontoRef.getOWLLiteral( this.radius), BUFFERIZE_ONTOLOGY_CHANGES);
	}
	
	public static CylinderShapeData addCylinderIndividual( OWLReferences ontoRef, Dimentional3Data pointOnAxis, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid){
		CylinderShapeData r = new CylinderShapeData( pointOnAxis, height, radius, axisDirection, centroid); 
		r.addShapeIndividualToOntology(ontoRef);
		return r;
	}
	
	public static CylinderShapeData addCylinderIndividual( OWLReferences ontoRef, Dimentional3Data pointOnAxis, Float height, Float radius, Dimentional3Data axisDirection, Dimentional3Data centroid, String individualName){
		CylinderShapeData r = new CylinderShapeData( pointOnAxis, height, radius, axisDirection, centroid, individualName); 
		r.addShapeIndividualToOntology(ontoRef);
		return r;
	}
	
	@Override
	String getIndividualNamePrefix() {
		return "R";
	}
}

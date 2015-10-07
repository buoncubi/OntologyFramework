package globFitRecognition.primitiveShapeData;

import ontologyFramework.OFContextManagement.OWLReferences;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public abstract class DirectionableShapeData extends PrimitiveShapeData{

	private static final String AXIS_DIRECTION_X_DATA_PROPERTY_NAME = "hasPrimitiveAxisDirection_x";
	private static final String AXIS_DIRECTION_Y_DATA_PROPERTY_NAME = "hasPrimitiveAxisDirection_y";
	private static final String AXIS_DIRECTION_Z_DATA_PROPERTY_NAME = "hasPrimitiveAxisDirection_z";
	
	private Dimentional3Data axisDirection;
	
	public DirectionableShapeData(Dimentional3Data axisDirection, Dimentional3Data centroid) {
		super(centroid);
		this.axisDirection = axisDirection;
	}
	
	public DirectionableShapeData(Dimentional3Data axisDirection, Dimentional3Data centroid, String individualName) {
		super(centroid, individualName);
		this.axisDirection = axisDirection;
	}

	public Dimentional3Data getAxisDirection(){
		return this.axisDirection;
	}
	
	@Override
	public String toString(){
		return super.toString() + "( axisDirection: " + this.axisDirection + ") ; ";
	}
	
	@Override
	void addDataPropertyToIndividual(OWLReferences ontoRef, OWLNamedIndividual individual) {
		super.addDataPropertyToIndividual( ontoRef, individual);
		OWLDataProperty axisX_dataProp = ontoRef.getOWLDataProperty( AXIS_DIRECTION_X_DATA_PROPERTY_NAME);
		OWLDataProperty axisY_dataProp = ontoRef.getOWLDataProperty( AXIS_DIRECTION_Y_DATA_PROPERTY_NAME);
		OWLDataProperty axisZ_dataProp = ontoRef.getOWLDataProperty( AXIS_DIRECTION_Z_DATA_PROPERTY_NAME);
		axisDirection.addToOntology( ontoRef,  individual, axisX_dataProp, axisY_dataProp, axisZ_dataProp);
	}
}

package globFitRecognition;

import globFitRecognition.primitiveShapeData.Dimentional3Data;
import globFitRecognition.primitiveShapeData.PlaneShapeData;
import globFitRecognition.sceneLearning.SceneConfidenceEvaluator;
import globFitRecognition.sceneLearning.SceneLearner;
import globFitRecognition.sceneRecognition.SceneIndividualCreator;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFContextManagement.OWLLibrary;
import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFErrorManagement.OFGUI.GuiRunner;


// this class is a work around to some protege bug.
// it loads an ontology, reason on it and save another ontology exporting all the inferations
public class OntologyDeveloping {

	public static final String BASE_PATH = "files/globFitOntologies/";//"/home/luca-phd/Documents/school luca/PhD/coding/0_shapeRecognition/Ontologies/globalFit/";
	public static final String BASE_SERIAL_PATH = BASE_PATH + "Serial/";
	public static final Boolean START_GUI = false; // with true the script does not save to file
	
	private static OFDebugLogger log;
	public static final String LOG_SAVING_FILE_NAME = "/home/luca-phd/catkin_ws/src/object_perception/semantic_geometric_traking/files/experiment_results/test_04-09-2015_11_51//experiment1.txt";
	
	// parameter to open the ontology
	public static final String ontoName = "onto1";
	public static final String iriPath	= "http://www.semanticweb.org/upitalia/ontologies/2015/7/untitled-ontology-9";
	public static final String filePath = BASE_PATH + "globFit-recognition-base.owl"; 
	public static final String savePath = BASE_PATH + "globFit-javad.owl";
	public static final String saveSerialPath = BASE_SERIAL_PATH + "globFit-javad.owl";
	public static final int command = OWLReferences.LOADFROMFILEcommand;
	
	public static final String[] BASE_INDIVIDUALS = {SceneLearner.INDIVIDUAL_NAME_LEARNING_PARAMETER, SceneLearner.INDIVIDUAL_NAME_RECOGNITION_PARAMETER}; // at every scan remove all individual except those
	
	static Long scanId = 0l;
	
	private static List< PlaneShapeData> addShapesIndividual(OWLReferences ontology) {
		List< PlaneShapeData> out = new ArrayList< PlaneShapeData>();
		Dimentional3Data p1_centroid = new Dimentional3Data( -0.9f, 0.2f, 0.3f);
		Dimentional3Data p1_axis = new Dimentional3Data( 0.1f, -0.1f, 1.1f);
		PlaneShapeData p1 = PlaneShapeData.addPlaneIndividual( ontology, 0.2f,  p1_axis, p1_centroid);
		out.add( p1);
		Dimentional3Data p2_centroid = new Dimentional3Data( 0.35f, 0.43f, 1.05f);
		Dimentional3Data p2_axis = new Dimentional3Data( 0.2f, 0.05f, 1.1f);
		PlaneShapeData p2 = PlaneShapeData.addPlaneIndividual( ontology, 0.2f,  p2_axis, p2_centroid);
		out.add( p2);
		
		Dimentional3Data p3_centroid = new Dimentional3Data( -0.9f, 0.2f, 0.3f);
		Dimentional3Data p3_axis = new Dimentional3Data( 0.1f, -0.1f, 1.1f);
		PlaneShapeData p3 = PlaneShapeData.addPlaneIndividual( ontology, 0.2f,  p3_axis, p3_centroid);
		out.add( p3);
		Dimentional3Data p4_centroid = new Dimentional3Data( 0.35f, 0.43f, 1.05f);
		Dimentional3Data p4_axis = new Dimentional3Data( 0.2f, 0.05f, 1.1f);
		PlaneShapeData p4 = PlaneShapeData.addPlaneIndividual( ontology, 0.2f,  p4_axis, p4_centroid);
		out.add( p4);
		
		
		return out;
	}
	
	/*private static void cleanOntology( OWLReferences ontoRef){
		Set< OWLNamedIndividual> allInd = ontoRef.getIndividualB2Class( ontoRef.getFactory().getOWLThing());
		for( OWLNamedIndividual i : allInd){
			for( int j = 0; j < BASE_INDIVIDUALS.length; j++)
				if( ! ontoRef.getOWLObjectName( i).equals( BASE_INDIVIDUALS[ j]))
					ontoRef.removeIndividual( i, PrimitiveShapeData.BUFFERIZE_ONTOLOGY_CHANGES);
		}
	}*/
	
	public static void main(String[] args) {
		log = new OFDebugLogger( OntologyDeveloping.class, true);
		
		// load ontology 
		OWLReferences ontology = new OWLReferences( ontoName, filePath, iriPath, command);
		ontology.setOWLVerbose(true);
		// add individual for simulation
		addShapesIndividual( ontology); 
		// call reasoning
		ontology.synchroniseReasoner();
				
		// run GUI if it is the case
		if( START_GUI)
			new Thread( new GuiRunner( ontoName)).start();
		
		// compute the scene individual
		SceneIndividualCreator createdScene = new SceneIndividualCreator( ontology);
		createdScene.addSceneIndividual();
		// call reasoning
		ontology.synchroniseReasoner();
			
		// evaluate the confidence of the new scene
		SceneConfidenceEvaluator sceneConf = new SceneConfidenceEvaluator( createdScene, ontology);
		// if it is the case learn new scene
		if( ! sceneConf.isAboveTreshould())
			new SceneLearner( createdScene.getSceneList(), ontology);
		
		// wait forever for GUI inspection
		if( START_GUI) {
			while( true){
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			// delate indiviual and save !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//OWLLibrary.saveOntology( false, saveSerialPath + "_" + scanId, ontology);
			//cleanOntology( ontology);
			OWLLibrary.saveOntology( false, savePath, ontology);
		}
		
		 
		/*OFDebugLogger.setPrintOnFile( LOG_SAVING_FILE_NAME);
		log.addDebugStrign( " TEST !!!!");
		OFDebugLogger.flush();*/
	}
}

package ontologyFramework.OFContextManagement;

import java.io.Serializable;

/**
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 * get the essential data to load again a specific OWLOnotology using the class { @link OWLReferences}.
 * Actual serialization of ontology is done saving it into owl file and then reload into OWLReferences
 * thanks to the informations stored in this class.
 */
@SuppressWarnings("serial")
public class OWLReferencesSerializable implements Serializable{

	String ontoName;
	String filePath;
	String ontologyPath;
	int usedCommand;
	
	
	/**
	 * initializes all the field of the class
	 * 
	 * @param ontoName the name associated to this OWLReferences
	 * @param filePath the directory path to the file 
	 * @param ontologyPath the ontologyPath (IRI path)
	 * @param usedCommand the usedCommand to load (from file or web) or create
	 */
	public OWLReferencesSerializable(String ontoName, String filePath,
			String ontologyPath, int usedCommand) {
		this.ontoName = ontoName;
		this.filePath = filePath;
		this.ontologyPath = ontologyPath;
		this.usedCommand = usedCommand;
	}

	/**
	 * @return the name associated to this OWLReferences
	 */
	public String getOntoName() {
		return ontoName;
	}

	/**
	 * @return the directory path to the file 
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @return the ontologyPath (IRI path)
	 */
	public String getOntologyPath() {
		return ontologyPath;
	}

	/**
	 * @return the usedCommand to load (from file or web) or create
	 */
	public int getUsedCommand() {
		return usedCommand;
	}

}

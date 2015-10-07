package ontologyFramework.OFContextManagement.synchronisingManager;

import java.io.Serializable;
import java.util.List;

/**
 * @author Buoncomapgni Luca
 * @version 1.0
 *
 * get the essential data to load again a synchroniser using the class { @link OFSynchroniserData}.
 * Actual serialization of synchroniser is done saving it into owl file and then reload into OFSynchroniserData
 * thanks to the informations stored in this class.
 */
@SuppressWarnings("serial")
public class OFSerializeSynchroniserData implements Serializable{

	private List< String> ontoNames; 
	private int size;
	private OFSynchroniserManagmentInterface manager;
	private String syName;
	
	/**
	 * Initialize all the field of this class
	 * 
	 * @param syName the synchronizer Name
	 * @param size the order of the synchronizer
	 * @param manager the manager attached to this synchronizer
	 * @param ontoNames the names associated to those OWLReferences
	 */
	public OFSerializeSynchroniserData(String syName, int size,
			OFSynchroniserManagmentInterface manager, List<String> ontoNames) {
		this.ontoNames = ontoNames;
		this.size = size;
		this.syName = syName;
		this.manager = manager;
	}

	/**
	 * @return the names associated to those OWLReferences
	 */
	public List<String> getOntoNames() {
		return ontoNames;
	}

	/**
	 * @return the order of the synchronizer
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return the synchronizer Name
	 */
	public String getSyName() {
		return syName;
	}

	/**
	 * @return the manager attached to this synchronizer
	 */
	public OFSynchroniserManagmentInterface getManager() {
		return manager;
	}
	

}

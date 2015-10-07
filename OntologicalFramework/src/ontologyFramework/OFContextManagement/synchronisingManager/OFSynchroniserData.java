package ontologyFramework.OFContextManagement.synchronisingManager;

import java.util.ArrayList;
import java.util.List;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.DebuggingClassFlagData;
import ontologyFramework.OFErrorManagement.OFDebugLogger;
import ontologyFramework.OFRunning.OFInitialising.OFInitialiser;


public class OFSynchroniserData{

	private List<OWLReferences> ontoList; 
	private int size;
	private String syName;
	private OFSynchroniserManagmentInterface manager;
	private OWLReferences incomingRef;
	
	private OFDebugLogger serializeLog = new OFDebugLogger( this, true);//DebuggingClassFlagData.getFlag( OFInitialiser.SERIALIZATORDEBUG_individualName));
	
	public OFSynchroniserData( OFSerializeSynchroniserData data){
		size = data.getSize();
		ontoList = new ArrayList< OWLReferences>(size);
		for( String s : data.getOntoNames()){
			ontoList.add( OWLReferences.getOWLReferences( s));
		}
		syName = data.getSyName();
		manager = data.getManager();
	}
	public OFSynchroniserData( Integer order, String synchName, OWLReferences ontoRef, OFSynchroniserManagmentInterface man){
		size = order;
		ontoList = new ArrayList< OWLReferences>( size);
		syName = synchName;
		manager = man;
		incomingRef = ontoRef;
		initializeData( incomingRef);
	}
	private void initializeData( OWLReferences ontoRef){
		// get base of the addresses
		String ontoNameBase = ontoRef.getOntoName();
		String filePathBase = ontoRef.getIriFilePath().toString();
		String ontoPathBase = ontoRef.getIriOntologyPath().toString();

		for( int i = 0; i < size; i++){
			// modify base strings
			String ontoName = ontoNameBase + "(" + syName + "-" + (i+1) + ")";
			String ontoPath = ontoPathBase + "/" + syName + "-" + (i+1);
			int last = filePathBase.lastIndexOf("/");
			int format = filePathBase.lastIndexOf(".");
			String filePath = filePathBase.substring(0, last) +
					syName + filePathBase.substring( last, format) +
					"(" + syName + "-" + (i+1) + ")" + 
					filePathBase.substring( format, filePathBase.length());
			
			// create new ontology
			OWLReferences newOntoRef = null;
			
			newOntoRef = new OWLReferences( ontoName, filePath, ontoPath, OWLReferences.CREATEcommand);
			
			// add ontology to the list
			ontoList.add( newOntoRef);
		}
	}
	
	// add at the beginning, shift to the right and remove the last 
	public void addToList( OWLReferences ontoRef){
		System.err.println( this.getClass() + " not implemented jet");
	}
	
	public OWLReferences getFromList( Integer order){
		if( order < size)
			return( ontoList.get( order));
		return( null);
	}
	
	public List<OWLReferences> getList(){
		return( ontoList);
	}
	
	protected OFSynchroniserManagmentInterface getManager() {
		return manager;
	}
	public void synchronise(){
		getManager().synchronise( this);
	}
	
	public OFSerializeSynchroniserData getSerialisableData(){
		List<String> ontoNames = new ArrayList< String>();
		// convert OWLReference to instanceName
		for( OWLReferences r : ontoList)
			ontoNames.add( r.getOntoName());
	    // get only serializable values
		OFSerializeSynchroniserData ssd = new OFSerializeSynchroniserData( syName, size, manager, ontoNames);
		serializeLog.addDebugStrign( " return serializable Synchroniser : " + " synchName = " + ssd.getSyName() +
				" | synchOrder" + ssd.getSize() +
				" | ontoNames = " + ssd.getOntoNames() +
				" | Manager = " + ssd.getManager());
		return( ssd);
	}
}
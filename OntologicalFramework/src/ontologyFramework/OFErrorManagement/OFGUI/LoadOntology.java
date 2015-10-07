package ontologyFramework.OFErrorManagement.OFGUI;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import ontologyFramework.OFContextManagement.OWLReferences;
import ontologyFramework.OFErrorManagement.FileManager;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class LoadOntology {

	static{
		updateOntology( false);
	}
	
	public static synchronized void updateOntology( boolean initialized){
		if( initialized)
			ClassExcange.changeVisibilityProgressBar(true);
		
		// read value and update ClassExcange
		String ontoName = ClassExcange.getOntoNameObj().getText();
		
		// import Ontology
		OWLReferences ontoRef = OWLReferences.getOWLReferences( ontoName);
		if( ontoRef != null){
			//ClassExcange.setOntoName( ontoName);
	    	/*ClassExcange.setReasoner( ontoRef.getReasoner());
	    	ClassExcange.setFactory( ontoRef.getFactory());
	    	ClassExcange.setPm( ontoRef.getPm());
	    	ClassExcange.setOntology( ontoRef.getOntology());
	    	ClassExcange.setManager( ontoRef.getManager());*/
			ClassExcange.setOntoRef( ontoRef);
		} else {
			// show a dialog box
			JOptionPane a = new JOptionPane();
			String message = "Does not exist any instance of OntologyManager loaded with the name : " + ontoName + ".\n no changes will take place.";
			String title = "Ontology name not known"; 
			JOptionPane.showMessageDialog( a, message, title, JOptionPane.ERROR_MESSAGE);
		}
		
		if(initialized)
			ClassExcange.changeVisibilityProgressBar(false);
	}

	// if true print on file the ontology
	// if false save the ontology 
	public static synchronized void saveOntology( boolean toOWL){
		OWLReferences ontoRef = ClassExcange.getOntoRef();
		
		String message;
		String format;
		ArrayList<String> strs; 
		if( ! toOWL){
			format = "txt";
			strs = new ArrayList<String>();
			BufferedReader tmp = getOntologyTokens();
			boolean loop =true;
			String line;
			try {
				line = tmp.readLine();
				while( loop){
					if( line == null)
						loop = false;
					else{
						loop = true;
						line = tmp.readLine();
						strs.add( line);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message = "IO Exception " + e.getCause() + System.getProperty("line.separator") +
						e.getMessage();
			}
		}
		else{
			format = "owl";
			strs = new ArrayList<String>( 1);
			strs.add( ontoRef.getOntoName());
		}
		
		String name = ClassExcange.getSavingName();
		boolean nameOk = false;
		if( (name != null) && ( ! name.trim().isEmpty()) &&( ! name.equals( "default")))
				nameOk = true;
			
		if( nameOk){
			String path = ClassExcange.getSavingPath();
			FileManager fm;
			try{
				if( (path == null) || ( path.trim().isEmpty()) || ( path.equals( ClassExcange.getSavingPath())))
					fm = new FileManager( "default", name, format, ClassExcange.defaultSavingPath);
				else
					fm = new FileManager( path, name, format);
				
				
				fm.loadFile();
				fm.printOnFile( strs, true);
				
				message = " saving this ontology configuration as : " + fm.getFileFormat() + " format.\n"
						+ " on the path : " + fm.getFilePath(); 
				
				fm.closeFile();
				
			} catch( java.lang.NullPointerException e){
				message = " NULL Exception" + e.getCause() + System.getProperty("line.separator") +
						e.getMessage();
				e.printStackTrace();
			}
		}else message = " given name not valid.";
		
		// show message
		JOptionPane a = new JOptionPane();
		JOptionPane.showMessageDialog(a, message, "saving file", JOptionPane.INFORMATION_MESSAGE);
	}
	
	// print ontology and return the string
	public static synchronized BufferedReader getOntologyTokens(){
		OWLReferences ontoRef = ClassExcange.getOntoRef();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
		
		StreamDocumentTarget print = new StreamDocumentTarget( ps);// new SystemOutDocumentTarget();
		
		try {
			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
			OWLOntologyFormat format =  ontoRef.getManager().getOntologyFormat(ontoRef.getOntology());
			if (format.isPrefixOWLOntologyFormat())
				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
			ontoRef.getManager().saveOntology( ontoRef.getOntology(), manSyntaxFormat, print); 
		} catch (OWLOntologyStorageException e) {	
			e.printStackTrace();
		}
		
		StringBuilder buffer = new StringBuilder();
		buffer.append( baos.toString());
		BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
		
		return( br);
	}	
}

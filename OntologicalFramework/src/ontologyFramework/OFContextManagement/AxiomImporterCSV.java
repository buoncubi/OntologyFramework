package ontologyFramework.OFContextManagement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyFactory;

import fileManagerAPI.FileReader;

public class AxiomImporterCSV extends FileReader{
	public static String COMMA = ",";

	private List< List< Double>> dataMatrix = new ArrayList< List< Double>>();
	private  OWLReferences ontoRef;
	
	public AxiomImporterCSV(String path, Boolean isRelative, OWLReferences ontoRef) {
		super(path, isRelative);
		this.ontoRef = ontoRef;
	}
	public AxiomImporterCSV(String path, Boolean isRelative, String ontoName, String filePath, String ontoPath) {
		super(path, isRelative);
		this.ontoRef = new OWLReferences( ontoName, filePath, ontoPath, OWLReferences.CREATEcommand);
	}
	public AxiomImporterCSV(String path, Boolean isRelative){
		super(path, isRelative);
	}
	
	
	// dataPropertyName dimensione uguale alle colonne
	// individualNames dimesnione uguale alle righe
	public Integer importToOntology( List< String> dataPropertyName, String individualNames, String className, Integer initialOrder){
		this.manipulateFile();
		
		int columnCount = 0;
		for( List< Double> c : dataMatrix){
			int rowCount = 0;
			String name = individualNames + "-" + (columnCount + initialOrder + 1);  
			for( Double r : c){
				String dataProp = dataPropertyName.get( rowCount); 
				ontoRef.addDataPropertyB2Individual(name, dataProp, r, false);
				ontoRef.addIndividualB2Class( name, className, false);
				rowCount++;
			}
			ontoRef.addDataPropertyB2Individual(name, "hasOrder", -(initialOrder + columnCount + 1), false);
			columnCount++;
		}
		return( columnCount + 1);
	}
	
	
	// read the file in csv
	@Override
	public List< List< Double>> manipulateFile() {
		try {
			this.openFile();
			BufferedReader manip = this.getFileMatipolator();
			String line = manip.readLine();
			while( line != null){
								
				List< Double> dataRow = new ArrayList< Double>();
				while( true){
					if( ! line.isEmpty()){
						int commaIdx = line.indexOf( COMMA);
						if( commaIdx != -1){
							if( commaIdx != 0){
								String tmp = line.substring( 0, commaIdx);
								line = line.replaceFirst( tmp + COMMA, "");
								dataRow.add( Double.valueOf( tmp.replace(",", "")));
							} else break;
						} else {
							String tmp = line.substring( 0);
							dataRow.add( Double.valueOf( tmp.replace(",", "")));
							break;
						}
					} else break;
				}
				dataMatrix.add( dataRow);	
				
				
				
				line = manip.readLine();
				
			}
			this.closeFile();
		} catch (IOException e) {
			this.showError(e);
		}
		return dataMatrix;
	}
}

package myFileManager;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import fileManagerAPI.FileWriter;

// dove non è diveramente indicato questa classe
// si comporta analogamente come Reader.
public class Writer extends FileWriter< Boolean>{
					 //extends LazyWriter< Boolean>{	
	
	private Integer count = 0;
	private Integer countLimit = 100;
	
	public Writer(String path, Boolean isRelative, Boolean appendFile) {
		super(path, isRelative, appendFile);
	}

	@Override
	public Boolean manipulateFile() {
		try {
			this.openFile();
			BufferedWriter writer = this.getFileMatipolator();
			// ottieni le stringa da scrivere dalla superclasse
			List< String> toAppend = this.getToAppend();
			// se non ci sono stati errori e se c'è qualcosa da scrivere
			if(( writer != null) && ( toAppend != null) && ( ! toAppend.isEmpty())){
				// per ogni elemento della lista
				for( String line : toAppend){
					// scrivi la linea
					writer.write( line.toString());
					// scrivi il carattere "vai a capo" (\n)
					writer.newLine();
				}
				// pulisci perchè tutto è stato scritto
				this.getToAppend().clear();
			} else {
				return( false);
			}
		} catch (FileNotFoundException e) {
			this.showError( e);
			return( false);
		} catch (IOException e) {
			this.showError( e);
		} finally {
			try {
				this.closeFile();
			} catch (IOException e) {
				this.showError( e);
				return( false);
			}
		}
		return true;
	}
	
	
}
